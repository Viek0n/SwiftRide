# SwiftRide Payment Service

The service owns one MongoDB `Bill` per trip. A bill embeds its payment attempts, balanced ledger journals, and outbox events so each state change can be saved atomically as one MongoDB document. The unique `tripId` index prevents duplicate bills; optimistic locking protects concurrent updates.

## Flow

1. Consume `RideCompleted` from RabbitMQ and create a pending bill using the amount fixed by Matching Service and persisted by Trip Service.
2. The rider starts a VNPay checkout. Repeating a request with the same idempotency key returns the same attempt and URL. A different attempt is allowed only after the previous attempt has a definitive failure.
3. Verify the signed VNPay IPN, merchant code, amount, and currency. A successful capture appends a balanced ledger journal and a `PaymentSettled` outbox event in the bill document.
4. Publish the outbox event to RabbitMQ. Delivery is at-least-once; Trip Service consumers must deduplicate by `eventId`.

The browser return endpoint only redirects to the configured web app. It does not settle a payment; the rider should poll the payment status endpoint while the IPN is being processed.

## HTTP endpoints

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/payments/{tripId}/checkout` | Create or resume a VNPay checkout attempt |
| `GET` | `/payments/{tripId}` | Read payment status for the trip |
| `GET` | `/payments/vnpay/ipn` | Receive VNPay server notification |
| `GET` | `/payments/vnpay/return` | Redirect the browser to the configured web return URL |

Rider endpoints require `X-Rider-Id` and checkout also requires `Idempotency-Key` (8–120 characters). Until JWT authentication is wired, the API Gateway must remove any client-supplied `X-Rider-Id`, set it from the authenticated token, and prevent direct public access to this service.

## RabbitMQ contract

When `RABBITMQ_ENABLED=true`, the service declares the durable `swiftride.events` topic exchange and consumes:

- Routing key: `trip.ride-completed`
- Queue: `RIDE_COMPLETED_QUEUE` (default `swiftride.trip.ride-completed`)
- Payload:

```json
{
  "tripId": "trip-id",
  "riderId": "rider-id",
  "amount": 42000,
  "currency": "VND",
  "quoteId": "matching-quote-id",
  "occurredAt": "2026-11-13T03:00:00Z"
}
```

After capture, `PaymentSettled` is published with an `eventId`, trip/rider IDs, amount, currency, gateway transaction ID, and occurrence time. Failed messages are sent to the queue's `.dlq`; this version does not automatically retry messages.

The current Trip Service does not yet publish `RideCompleted` (its completion use case is still a stub), and no Trip Service consumer for `PaymentSettled` is wired yet. The payment-side event contracts are in place, but the end-to-end trip transition requires those two integrations.

## Configuration

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | Payment database URI (default `mongodb://localhost:27017/swiftride_payment`) |
| `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET` | VNPay merchant credentials |
| `VNPAY_PAYMENT_URL` | VNPay checkout endpoint (defaults to sandbox) |
| `VNPAY_RETURN_URL` | Public backend browser-return URL |
| `WEB_RETURN_URL` | Rider web-app return URL |
| `RABBITMQ_ENABLED` | Enable event consumption and outbox publishing (default `false`) |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | RabbitMQ connection |

This version has no COD, no automatic charge retry, no rider-attempt cap or payment expiry policy, no driver/platform payout split, and no refund execution. A second successful capture is journaled as payable-to-customer and marked `REQUIRES_REVIEW`; it is not automatically refunded.
