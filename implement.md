# SwiftRide — Trip, Matching & Payment Flow

## 1. System Overview

SwiftRide uses a microservice architecture with three main services:

| Service | Responsibility |
|---|---|
| Trip Service | Trip creation, confirmation, rejection, pickup, and completion |
| Matching Service | Driver availability, driver discovery, matching, and trip offers |
| Payment Service | Payment processing after trip completion |

Infrastructure:

- **MongoDB:** Persistent storage for each service.
- **RabbitMQ:** Asynchronous communication between services.
- **Eureka:** Service discovery for synchronous HTTP communication.

### Communication rules

- Use Eureka + HTTP when a service needs an immediate response.
- Use RabbitMQ when a service publishes an event that another service processes asynchronously.
- Each service owns its own database collections.
- One service must not directly modify another service's database.

---

# 2. Complete Trip Lifecycle

```text
REQUESTED
    |
    +---- Rider rejects
    |         |
    |         v
    |      REJECTED
    |
    +---- Rider confirms
              |
              v
           MATCHING
              |
              +---- Matching unsuccessful
              |           |
              |           v
              |         FAILED
              |
              +---- Driver accepts offer
                          |
                          v
                    DRIVER_ASSIGNED
                          |
                          +---- Driver picks up rider
                          |           |
                          |           v
                          |       IN_PROGRESS
                          |           |
                          |           +---- Driver completes
                          |                       |
                          |                       v
                          |                   COMPLETED
                          |
                          +---- Cancellation
                                      |
                                      v
                                  CANCELLED
```

The `driverId` remains null when the trip is created and while it is in `MATCHING`.

It is assigned after a driver successfully accepts an offer.

---

# 3. Phase 1 — Trip Creation

## 3.1. Rider creates a trip

Endpoint:

```http
POST /api/trips
```

Request:

```json
{
  "from": {
    "latitude": 10.762622,
    "longitude": 106.660172
  },
  "to": {
    "latitude": 10.7769,
    "longitude": 106.7009
  }
}
```

The `riderId` comes from the authenticated JWT, not from the request body.

### Processing flow

1. Rider sends the starting and destination coordinates.
2. Trip Service obtains the authenticated rider ID.
3. Trip Service calls Matching Service through Eureka.
4. Matching Service calculates:
   - Route geometry.
   - Distance.
   - Estimated duration.
   - Estimated fare.
5. Trip Service creates a trip with status `REQUESTED`.
6. Trip Service stores the route and fare as a snapshot.
7. Trip Service returns the created trip to the rider.

Example trip:

```json
{
  "_id": "trip-123",
  "riderId": "rider-123",
  "driverId": null,
  "status": "REQUESTED",
  "from": {},
  "to": {},
  "distanceMeters": 5000,
  "durationSeconds": 900,
  "totalFare": 45000,
  "createdAt": "2026-11-22T10:00:00Z"
}
```

---

# 4. Phase 2 — Rider Decision

After receiving the trip details, the rider can reject or confirm the trip.

## 4.1. Scenario A — Rider rejects

Endpoint:

```http
POST /api/trips/{tripId}/reject
```

Processing:

1. Validate the authenticated rider owns the trip.
2. Verify the trip status is `REQUESTED`.
3. Delete the trip.

No RabbitMQ event is needed.

## 4.2. Scenario B — Rider confirms

Endpoint:

```http
POST /api/trips/{tripId}/confirm
```

Processing:

1. Validate rider ownership.
2. Verify the trip status is `REQUESTED`.
3. Change status to `MATCHING`.
4. Save the updated trip.
5. Publish a `TripConfirmed` event directly to RabbitMQ.
6. Return the updated trip.

Example event:

```java
public record TripConfirmedEvent(
    String eventId,
    String tripId,
    String riderId,
    Position from,
    Position to,
    Instant confirmedAt
) {}
```

RabbitMQ configuration:

```text
Exchange: trip.exchange
Routing key: trip.confirmed
Queue: matching.trip.confirmed
```

---

# 5. Phase 3 — Driver Availability

Matching Service manages driver availability.

Endpoint:

```http
POST /api/drivers/matching
```

Example:

```json
{
  "activeStage": "ONLINE",
  "position": {
    "latitude": 10.762622,
    "longitude": 106.660172
  }
}
```

The driver can be discovered by `findNearestAvailableDriver()` only when:

```text
activeStage == MATCHING
```

When an offer is sent, the driver is temporarily reserved(activeStage == MATCH_FOUND).

When the offer is rejected or expires(based on MatchingConstant), the driver can become available again.

When the offer is accepted, the driver becomes busy.

---

# 6. Phase 4 — Matching Service Consumes TripConfirmed

Matching Service consumes:

```text
Exchange: trip.exchange
Routing key: trip.confirmed
Queue: matching.trip.confirmed
```

### Processing flow

1. Receive the RabbitMQ message.
2. Create a MatchingSession.
3. Save the MatchingSession.
4. Start the matching worker.

Example matching job:

```json
{
  "_id": "job-123",
  "tripId": "trip-123",
  "attemptCount": 0,
  "maxAttempts": 5,
  "startedAt": "2026-11-22T10:00:00Z",
  "expiresAt": "2026-11-22T10:02:00Z",
  "status": "SEARCHING"
}
```

---

# 7. Phase 5 — Matching Rider With Driver

Matching Service runs:

```text
MatchingRiderWithDriver
```

### Processing flow

1. Load the MatchingSession.
2. Check matching duration.
3. Check maximum attempts.
4. Find the nearest available driver.
5. Reserve the driver.
6. Create a TripOffer.
7. Wait for driver response or offer expiration(based on MatchingConstant).

### Scenario A — Matching failed

Matching stops when:

```java
boolean maxAttemptsReached =
    session.getAttemptCount() >= MatchingConstant.MAX_MATCHING_ATTEMPT;

boolean durationExceeded =
    Instant.now().isAfter(job.getExpiresAt());
```

If either condition is true:

1. Change matching job status to `FAILED`.
2. Publish a `MatchingFailed` event directly to RabbitMQ.

Example event:

```java
public record MatchingFailedEvent(
    String eventId,
    String tripId,
    String reason,
    Instant failedAt
) {}
```

RabbitMQ configuration:

```text
Exchange: matching.exchange
Routing key: matching.failed
Queue: trip.matching.failed
```

Trip Service consumes `MatchingFailed`:

1. Receive the event.
2. Verify the trip is still `MATCHING`.
3. Change trip status to `FAILED`.
4. Save the update.

### Scenario B — Driver found

1. Reserve the available driver.
2. Create a TripOffer.
3. Set the offer status to `PENDING`.
4. Set `expiresAt`.
5. Make the offer available to the driver.

Example:

```json
{
  "_id": "offer-123",
  "tripId": "trip-123",
  "driverId": "driver-123",
  "status": "PENDING",
  "createdAt": "2026-11-22T10:00:00Z",
  "expiresAt": "2026-11-22T10:00:15Z"
}
```

---

# 8. Phase 6 — Driver Receives TripOffer

The driver frontend requests:

```http
GET /api/trips/me/offers //Get pending offer from db
```

The driver ID comes from the authenticated JWT.

For the initial implementation, polling is sufficient. SSE or WebSocket can be added later for real-time delivery.

The frontend must never access MongoDB directly.

## 8.1. Scenario A — Driver rejects

Endpoint:

```http
POST /api/trips/{tripId}/driver-reject
```

This endpoint belongs to Matching Service because Matching Service owns TripOffer(move tripOffer to trip-service).

Processing:

1. Validate driver identity.
2. Find the offer belonging to this driver and trip.
3. Verify the offer is `PENDING`.
4. Verify the offer has not expired.
5. Change offer status to `REJECTED`.
6. Release the driver's reservation.
7. Start the next matching attempt.

## 8.2. Scenario B — Offer expires

`OFFER_LIVE_DURATION` defines how long a driver can accept an offer. (define in MatchingConstant)

Example:

```text
OFFER_LIVE_DURATION = 15 seconds
```

The acceptance API must check expiration itself. Do not rely only on a scheduled expiration worker.

Expiration condition:

```java
offer.getExpiresAt().isBefore(Instant.now())
```

The expiration worker changes:

```text
PENDING -> EXPIRED
```

It also releases the driver reservation and starts the next matching attempt.

## 8.3. Scenario C — Driver accepts

Endpoint:

```http
POST /api/trips/{tripId}/driver-accept
```

This endpoint belongs to Matching Service.

Processing:

1. Validate the authenticated driver.
2. Find the offer.
3. Verify that it belongs to the authenticated driver.
4. Verify status is `PENDING`.
5. Verify the offer has not expired.
6. Verify the driver reservation is valid.
7. Accept the offer.
8. Change the driver to `IN_MATCH`.
9. Publish a `DriverAccepted` event directly to RabbitMQ.

Example event:

```java
public record DriverAcceptedEvent(
    String eventId,
    String tripId,
    String riderId,
    String driverId,
    String offerId,
    Instant assignedAt
) {}
```

RabbitMQ configuration:

```text
Exchange: matching.exchange
Routing key: driver.accepted
Queue: trip.driver.accepted
```

### Trip Service consumes DriverAssigned

Trip Service consumes the event from RabbitMQ.

Processing:

1. Receive `DriverAccepted`.
2. Verify trip status is `MATCHING`.
3. Set `driverId`.
4. Change trip status to `PICKUP`.
5. Save the updated trip.

---

# 9. Phase 7 — Driver Picks Up Rider

Endpoint:

```http
POST /api/trips/{tripId}/pickedup
```

This endpoint belongs to Trip Service.

Processing:

1. Validate authenticated driver.
2. Verify driver is assigned to the trip.
3. Verify trip status is `PICKUP`.
4. Change status to `IN_PROGRESS`.
5. Save the update.

State transition:

```text
DRIVER_ASSIGNED -> IN_PROGRESS
```

---

# 10. Phase 8 — Driver Completes Trip

Endpoint:

```http
POST /api/trips/{tripId}/complete
```

This endpoint belongs to Trip Service.

Processing:

1. Validate authenticated driver.
2. Verify driver is assigned to the trip.
3. Verify trip status is `IN_PROGRESS`.
4. Change trip status to `COMPLETED`.
5. Save the updated trip.
6. Publish a `TripCompleted` event directly to RabbitMQ.
7. Return completed trip.

Example event:

```java
public record TripCompletedEvent(
    String eventId,
    String tripId,
    String riderId,
    String driverId,
    BigDecimal totalFare,
    Instant completedAt
) {}
```

RabbitMQ configuration:

```text
Exchange: trip.exchange
Routing key: trip.completed
Queue: payment.trip.completed
```

---

# 11. Phase 9 — Payment Service Consumes TripCompleted

Payment Service consumes:

```text
Exchange: trip.exchange
Routing key: trip.completed
Queue: payment.trip.completed
```

Processing:

1. Receive `TripCompleted`.
2. Create a payment record for the trip.
3. Start payment processing.
4. Update payment status based on the payment provider's response.

Example payment:

```json
{
  "_id": "payment-123",
  "tripId": "trip-123",
  "riderId": "rider-123",
  "driverId": "driver-123",
  "amount": 45000,
  "status": "PENDING"
}
```

---

# 12. RabbitMQ Event Flow Summary

| Event | Publisher | Consumer | Result |
|---|---|---|---|
| `TripConfirmed` | Trip Service | Matching Service | Start matching |
| `MatchingFailed` | Matching Service | Trip Service | Trip becomes FAILED |
| `DriverAccepted` | Matching Service | Trip Service | Assign driver |
| `TripCompleted` | Trip Service | Payment Service | Start payment |

Each event should contain:

- `eventId`
- `eventType`
- `aggregateId`
- `occurredAt`
- `payload`

---

# 13. Database Collections

## Trip Service

```text
trips
```

| Collection | Purpose |
|---|---|
| `trips` | Trip lifecycle and details |

## Matching Service

```text
active_drivers
matching_sessions
trip_offers
driver_reservations
```

| Collection | Purpose |
|---|---|
| `active_drivers` | Driver location and availability |
| `matching_sessions` | Matching process and retry information |
| `trip_offers` | Offers sent to drivers |
| `driver_reservations` | Driver reservation information |

## Payment Service

```text
payments
```

| Collection | Purpose |
|---|---|
| `payments` | Payment records and payment status |

---

# 14. Important Technical Rules

1. Publish RabbitMQ events after saving the corresponding business state.
2. Configure RabbitMQ durable exchanges, durable queues, and persistent messages.
3. Configure publisher confirms and consumer acknowledgements.
4. Configure retry handling and dead-letter queues for failed messages.
5. Use conditional database updates for trip and offer state transitions.
6. Ensure that only the assigned driver can pick up or complete a trip.
7. Check offer expiration when the driver attempts to accept an offer.
8. Release driver reservations when offers are rejected or expire.
9. Keep trip status and driver availability as separate concepts.
10. Use MongoDB transactions for operations that update multiple documents within the same service database.
11. Keep transactions inside their owning microservice. Do not attempt a distributed MongoDB