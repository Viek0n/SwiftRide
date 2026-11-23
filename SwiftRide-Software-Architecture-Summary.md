# SwiftRide – Software Architecture (Version 1.0)

> **Nguồn:** Tài liệu bài tập lớn môn Kiến trúc Phần mềm, Trường Đại học Sài Gòn, Khoa Công nghệ Thông tin, học kỳ I năm học 2026–2027.  
> **Mục đích:** Bản Markdown rút gọn các nội dung cốt lõi để tham khảo và tích hợp vào workspace VS Code.  
> **Lưu ý:** Tên dự án trong nội dung PDF là **SwiftRide** (dù tên tệp PDF được tải lên là `RideLite-KienTrucPhanMem.pdf`).

---

## 1. Tổng quan dự án

SwiftRide là hệ thống đặt xe kết nối hai nhóm người dùng:

- **Rider:** người đặt xe.
- **Driver:** tài xế nhận và thực hiện chuyến đi.

Đây là mô hình **two-sided marketplace**. Giá trị cốt lõi của hệ thống là độ tin cậy và khả năng cân bằng cung – cầu theo thời gian, không gian.

Tài liệu đề xuất kiến trúc **Microservices**, giới hạn trong 3 dịch vụ cốt lõi và 3 cơ sở dữ liệu riêng. Trong đó **Payment Service là dịch vụ bắt buộc**.

Ba dịch vụ:

1. **Trip Service:** quản lý vòng đời và trạng thái chuyến đi.
2. **Matching and Pricing Service:** tìm tài xế phù hợp và tính giá chuyến đi.
3. **Payment Service:** xử lý thanh toán và sổ cái giao dịch.

Các thành phần như Driver Discovery, Route, Time Estimation, Surge Pricing, Tax, Toll… có thể là những dịch vụ riêng trong hệ thống lớn, nhưng nằm ngoài phạm vi 3 service cốt lõi của bài tập. Message broker, Outbox và observability có thể được triển khai theo lộ trình mở rộng.

## 2. Kịch bản nghiệp vụ end-to-end

Luồng chính từ lúc đặt xe đến khi thanh toán:

1. **Đặt xe:** Rider mở ứng dụng, chọn điểm đón và điểm đến, sau đó nhấn “Đặt xe”. Request đi qua API Gateway đến Trip Service.
2. **Tạo chuyến:** Trip Service tạo chuyến mới với trạng thái `Requested` và lưu vào SQL Server.
3. **Tìm tài xế và báo giá:** Trip Service gọi Matching and Pricing Service. Service này tìm tài xế gần nhất dựa trên dữ liệu vị trí trong MongoDB và tính giá.
4. **Rider xác nhận giá:**
   - Rider đồng ý: tiếp tục tìm tài xế.
   - Rider từ chối: chuyến đi bị hủy.
5. **Đề xuất chuyến cho tài xế:**
   - Tài xế chấp nhận: chuyển sang thực hiện chuyến.
   - Tài xế từ chối: nếu số lần thử chưa vượt quá `k`, hệ thống tìm tài xế khác; nếu vượt quá `k`, chuyến không thành công.
6. **Thực hiện chuyến:** tài xế lần lượt cập nhật các trạng thái:
   - `EnRoute`: tài xế đang đến.
   - `PickedUp`: đã đón khách.
   - `DroppedOff`: đã trả khách.
7. **Thanh toán:** sau khi trả khách, Trip Service phát sự kiện để Payment Service thu tiền rider và ghi sổ cái vào PostgreSQL.
8. **Kết thúc:** khi thanh toán hoàn tất, chuyến đi chuyển sang trạng thái cuối thành công.

### 2.1. Công thức tính giá

Công thức ước lượng giá trong tài liệu:

\[
p = (c_d \cdot d + c_t \cdot t)\cdot s + c_m \cdot \eta \cdot (1+T)
\]

Trong đó:

| Ký hiệu | Ý nghĩa |
|---|---|
| `p` | Giá chuyến đi |
| `d` | Quãng đường |
| `t` | Thời gian ước lượng |
| `c_d` | Hệ số chi phí theo quãng đường |
| `c_t` | Hệ số chi phí theo thời gian |
| `s` | Hệ số giá động (surge) |
| `c_m` | Phí phụ, ví dụ phí cầu đường |
| `η` | Hệ số khuyến mãi |
| `T` | Thuế |

> Tài liệu không quy định giá trị cụ thể của các hệ số; chúng cần được xác định theo chính sách tính giá của hệ thống.

## 3. Kiến trúc tổng quan

### 3.1. Nguyên tắc giao tiếp

Client không gọi trực tiếp từng microservice. Mọi request đi qua **API Gateway**.

- **Đồng bộ:** REST/gRPC khi cần phản hồi ngay.
- **Bất đồng bộ:** RabbitMQ để truyền domain event giữa các service.
- **Database per Service:** mỗi service sở hữu dữ liệu và cơ sở dữ liệu riêng, không chia sẻ database trực tiếp.

Một số sự kiện minh họa:

- `TripAccepted`
- `RideCompleted`
- `PaymentSettled`

### 3.2. Client Architecture và các tầng

| Tầng | Thành phần | Trách nhiệm |
|---|---|---|
| Presentation | Rider App / Driver App | UI/UX, kiểm tra dữ liệu đầu vào, gọi API. Quy tắc nghiệp vụ quan trọng vẫn phải được kiểm tra ở server. |
| Edge / Gateway | API Gateway (YARP) | Điểm vào chung, định tuyến, xác thực JWT, rate limiting và aggregation dữ liệu cho màn hình. |
| Application | Các microservice | Xử lý nghiệp vụ; mỗi service sở hữu database riêng. |
| Integration | RabbitMQ | Truyền sự kiện giữa các service. |

### 3.3. API Gateway và BFF

Tài liệu đề xuất **YARP (Yet Another Reverse Proxy)** vì được Microsoft phát triển, tích hợp với ASP.NET Core và phù hợp hệ sinh thái .NET. Ocelot cũng là lựa chọn có thể tiếp cận trong .NET.

Phân biệt:

- **API Gateway:** điểm vào chung và chịu trách nhiệm định tuyến request.
- **BFF (Backend for Frontend):** cung cấp API riêng phù hợp cho từng loại client.

SwiftRide ban đầu dùng một API Gateway. Có thể tách Rider BFF và Driver BFF nếu nhu cầu của hai giao diện khác biệt đáng kể.

### 3.4. Phân chia cơ sở dữ liệu

| Microservice | Cơ sở dữ liệu | Lý do |
|---|---|---|
| Trip Service | SQL Server (quan hệ) | Trạng thái chuyến đi cần ACID, ràng buộc toàn vẹn và chuyển trạng thái nhất quán. |
| Matching and Pricing Service | MongoDB (document) | Dữ liệu vị trí và đặc trưng route/rider/driver linh hoạt; hỗ trợ geo-spatial index. |
| Payment Service | PostgreSQL (quan hệ) | Sổ cái giao dịch kiểu double-entry cần transaction mạnh, kiểm toán và tính nhất quán tài chính. |

Đây là cách tiếp cận **Polyglot Persistence**: mỗi service chọn loại database phù hợp nhất với nghiệp vụ.

Đánh đổi: sử dụng nhiều công nghệ database làm tăng chi phí vận hành, sao lưu và giám sát. Tài liệu cũng chấp nhận phương án dùng cùng một loại database nhưng tách schema/database theo từng service cho bài tập lớn.

## 4. Vòng đời chuyến đi (Trip Lifecycle)

Trip Service là nơi quản lý trạng thái chuyến đi. Các service khác phản ứng thông qua sự kiện.

Luồng trạng thái tổng quát:

```text
Start
  |
Rider yêu cầu xe
  |
Trip Service tạo chuyến (Requested)
  |
Matching and Pricing: tìm tài xế + tính giá
  |
Hiển thị giá cho Rider
  |---------------- Rider từ chối ----------------> Cancelled
  |
Rider đồng ý
  |
Đề xuất cho tài xế
  |---------------- Tài xế từ chối quá k lần -----> Failed
  |                     |
  |                     +-- Chưa quá k lần: tìm tài xế khác
  |
Tài xế chấp nhận (DriverAccepted)
  |
EnRoute
  |
PickedUp
  |
DroppedOff
  |
PaymentPending
  |---------------- Thanh toán lỗi ---------------> Chờ xử lý /
  |                                                   retry có giới hạn
  |
Paid
  |
Thành công
```

Các trạng thái được nêu trong tài liệu gồm:

- `Requested`
- `DriverAccepted`
- `EnRoute`
- `PickedUp`
- `DroppedOff`
- `PaymentPending`
- `Paid`
- Nhánh kết thúc: `Cancelled` / `Failed`

### 4.1. Xử lý lỗi thanh toán

- Khi thanh toán gặp lỗi tạm thời, hệ thống retry có giới hạn.
- Các lần retry phải dùng cùng một **idempotency key**.
- Nếu vẫn thất bại sau số lần retry cho phép, chuyến đi chuyển sang trạng thái chờ xử lý.
- Không được trừ tiền lặp hoặc thông báo thanh toán thành công sai.

## 5. Chi tiết ba microservice

### 5.1. Trip Service

**Trách nhiệm:**

- Khởi tạo chuyến đi.
- Quản lý và chuyển trạng thái chuyến đi.
- Áp dụng State pattern cho vòng đời chuyến đi.
- Phát domain event khi có thay đổi nghiệp vụ.

**API tiêu biểu:**

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/trips` | Tạo chuyến đi |
| POST | `/trips/{id}/driver-accept` | Ghi nhận tài xế chấp nhận |
| POST | `/trips/{id}/pickup` | Ghi nhận đã đón khách |
| POST | `/trips/{id}/dropoff` | Ghi nhận đã trả khách |

**Database:** SQL Server.

### 5.2. Matching and Pricing Service

**Trách nhiệm:**

- Tìm tài xế gần nhất bằng truy vấn địa lý (geo-query) trên MongoDB.
- Tính giá bằng Strategy pattern.
- Hỗ trợ các chiến lược giá như Standard, Surge và Promo.

**API tiêu biểu:**

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/match` | Tìm tài xế phù hợp |
| POST | `/quote` | Tính báo giá chuyến đi |

**Database:** MongoDB.

### 5.3. Payment Service

**Trách nhiệm:**

- Tạo và xử lý giao dịch thanh toán.
- Ghi sổ cái giao dịch.
- Xử lý hoàn tiền.
- Xử lý bồi thường khi một Saga thất bại.
- Hỗ trợ idempotency để một yêu cầu không khiến rider bị trừ tiền hai lần.

**API tiêu biểu:**

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/payments` | Tạo giao dịch thanh toán |
| POST | `/payments/{id}/refund` | Hoàn tiền |

**Database:** PostgreSQL.

**Lưu ý bảo mật:** trong hệ thống thực tế, Payment Service chỉ lưu token do cổng thanh toán cấp, không lưu số thẻ hoặc CVV.

## 6. Áp dụng nguyên tắc SOLID

### 6.1. S – Single Responsibility Principle

Mỗi lớp chỉ nên có một lý do để thay đổi. Ví dụ, tách việc tính giá khỏi việc lưu dữ liệu:

```csharp
public sealed class FareCalculator
{
    public decimal Calculate(Trip trip, IPricingStrategy strategy)
        => strategy.GetPrice(trip);
}

public sealed class TripRepository
{
    private readonly TripDbContext _db;

    public TripRepository(TripDbContext db) => _db = db;

    public async Task SaveAsync(Trip trip)
    {
        _db.Trips.Update(trip);
        await _db.SaveChangesAsync();
    }
}
```

- `FareCalculator`: chỉ chịu trách nhiệm tính giá.
- `TripRepository`: chỉ chịu trách nhiệm truy xuất/lưu dữ liệu chuyến đi.

### 6.2. O – Open/Closed Principle

Mở để mở rộng, đóng để sửa đổi. Có thể thêm chiến lược tính giá mới mà không sửa chiến lược cũ:

```csharp
public interface IPricingStrategy
{
    decimal GetPrice(Trip trip);
}

public sealed class StandardPricing : IPricingStrategy
{
    public decimal GetPrice(Trip trip)
        => trip.Distance * 1.0m + trip.EstimatedTime * 0.5m;
}

public sealed class SurgePricing : IPricingStrategy
{
    private readonly IPricingStrategy _basePricing;
    private readonly decimal _multiplier;

    public SurgePricing(IPricingStrategy basePricing, decimal multiplier)
        => (_basePricing, _multiplier) = (basePricing, multiplier);

    public decimal GetPrice(Trip trip)
        => _basePricing.GetPrice(trip) * _multiplier;
}
```

### 6.3. L – Liskov Substitution Principle

Các lớp triển khai phải thay thế được abstraction mà không phá vỡ hành vi mong đợi.

Ví dụ, mọi `IPricingStrategy` phải tuân thủ hợp đồng rằng giá trả về không âm:

```csharp
public decimal Quote(Trip trip, IPricingStrategy strategy)
{
    decimal price = strategy.GetPrice(trip);

    if (price < 0)
        throw new InvalidOperationException("Gia khong hop le");

    return price;
}
```

Điều kiện giá không âm cần được xác định như một hậu điều kiện và kiểm thử. Kiểm tra runtime chỉ là hàng rào bảo vệ, không tự nó chứng minh LSP.

### 6.4. I – Interface Segregation Principle

Tách interface nhỏ theo nhu cầu sử dụng, tránh một interface quá lớn:

```csharp
public interface ITripReader
{
    Task<Trip?> GetByIdAsync(Guid id);
}

public interface ITripWriter
{
    Task SaveAsync(Trip trip);
}
```

Service chỉ đọc dữ liệu nên chỉ phụ thuộc `ITripReader`, giúp giảm coupling.

### 6.5. D – Dependency Inversion Principle

Module cấp cao phụ thuộc vào abstraction, không phụ thuộc trực tiếp vào chi tiết triển khai. Dùng Dependency Injection của .NET:

```csharp
public sealed class TripService
{
    private readonly ITripWriter _writer;
    private readonly IEventBus _bus;

    public TripService(ITripWriter writer, IEventBus bus)
        => (_writer, _bus) = (writer, bus);

    public async Task AcceptAsync(Trip trip)
    {
        trip.Accept();
        await _writer.SaveAsync(trip);
        await _bus.PublishAsync(new TripAccepted(trip.Id));
    }
}
```

Đăng ký DI trong `Program.cs`:

```csharp
builder.Services.AddScoped<ITripWriter, TripRepository>();
builder.Services.AddSingleton<IEventBus, RabbitMqEventBus>();
```

## 7. Design patterns và kiến trúc được sử dụng

| Pattern | Phân loại | Ứng dụng |
|---|---|---|
| State | Behavioral | Quản lý vòng đời chuyến đi và các chuyển trạng thái hợp lệ. |
| Strategy | Behavioral | Các chiến lược tính giá Standard / Surge / Promo. |
| Repository | DDD | Trừu tượng hóa truy cập database cho từng service. |
| Factory | Creational | Tạo đối tượng PaymentMethod như Card / Wallet. |
| Observer / Pub-Sub | Behavioral | Phát domain event qua RabbitMQ. |
| API Gateway | Architectural | Cung cấp một điểm vào cho client bằng YARP. |
| Saga | Architectural | Điều phối quy trình qua nhiều service và định nghĩa hành động bồi thường. |
| Outbox | Architectural | Ghi dữ liệu và event tin cậy trong cùng transaction. |

### 7.1. Ví dụ State pattern

Các trạng thái nghiệp vụ đầy đủ được tài liệu mô tả:

```text
Requested
  -> DriverAccepted
  -> EnRoute
  -> PickedUp
  -> DroppedOff
  -> PaymentPending
  -> Paid

Nhánh kết thúc: Cancelled / Failed
```

Ví dụ rút gọn minh họa State pattern:

```csharp
public abstract class TripState
{
    public abstract TripState Accept();
    public abstract TripState Complete();
}

public sealed class RequestedState : TripState
{
    public override TripState Accept() => new AcceptedState();

    public override TripState Complete()
        => throw new InvalidOperationException("Chua duoc chap nhan");
}

public sealed class AcceptedState : TripState
{
    public override TripState Accept()
        => throw new InvalidOperationException("Da chap nhan roi");

    public override TripState Complete() => new CompletedState();
}

public sealed class CompletedState : TripState
{
    public override TripState Accept() => this;
    public override TripState Complete() => this;
}
```

Đây là ví dụ rút gọn để minh họa pattern; state machine thực tế cần thể hiện đầy đủ các trạng thái và transition nghiệp vụ.

## 8. Ngăn xếp công nghệ .NET

| Thành phần | Công nghệ đề xuất |
|---|---|
| Runtime / Framework | .NET 10 (LTS), ASP.NET Core Minimal API / Controllers |
| ORM / Data | EF Core 10 (SQL Server, PostgreSQL), MongoDB.Driver |
| API Gateway | YARP |
| Message Broker | RabbitMQ (qua MassTransit) |
| Authentication | OAuth 2.0 / OpenID Connect, JWT Bearer |
| Container | Docker + docker-compose |
| Testing | xUnit + Moq |
| Observability | OpenTelemetry (log, metric, distributed trace) |

### 8.1. Cấu trúc thư mục đề xuất

```text
SwiftRide/
├── src/
│   ├── ApiGateway/                 # YARP, routing + JWT
│   ├── TripService/
│   │   ├── Domain/                 # Trip, TripState (State pattern)
│   │   ├── Application/             # TripService, DTOs
│   │   ├── Infrastructure/          # EF Core + SQL Server
│   │   └── Api/                     # Controllers / Minimal API
│   ├── MatchingService/
│   │   ├── Domain/
│   │   ├── Application/
│   │   ├── Infrastructure/          # MongoDB
│   │   └── Api/
│   └── PaymentService/
│       ├── Domain/
│       ├── Application/
│       ├── Infrastructure/          # PostgreSQL
│       └── Api/
├── tests/
│   └── TripService.Tests/            # xUnit + Moq
├── docker-compose.yml               # 3 services + 3 databases + RabbitMQ
└── SwiftRide.sln
```

## 9. Trade-off và yêu cầu chất lượng

### 9.1. Kiến trúc mã nguồn

Microservices không mặc định tốt hơn Monolith.

Với nhóm nhỏ, nghiệp vụ chưa ổn định và tải thấp, **Modular Monolith** thường dễ phát triển, kiểm thử và triển khai hơn.

Microservices phù hợp khi:

- Cần triển khai và mở rộng từng service độc lập.
- Ranh giới nghiệp vụ đã đủ rõ.
- Nhóm chấp nhận chi phí vận hành của hệ thống phân tán.

Lộ trình được đề xuất cho SwiftRide:

1. Xác định module và bounded context.
2. Chạy được luồng nghiệp vụ cơ bản.
3. Tách service và database.
4. Bổ sung messaging, Outbox và observability.

Trong tài liệu, Microservices là lựa chọn phục vụ mục tiêu học tập của bài tập lớn.

### 9.2. Nhất quán và độ tin cậy

- **Strong consistency** bên trong một service.
- **Eventual consistency** giữa các service.
- **Transactional Outbox:** ghi thay đổi dữ liệu và event vào Outbox trong cùng transaction; worker publish event sau đó.
- **Idempotent consumer:** consumer phải xử lý idempotent vì message broker thường giao nhận theo cơ chế at-least-once, nên một message có thể đến nhiều lần.
- **Timeout:** mọi lời gọi mạng cần có timeout.
- **Retry:** chỉ retry lỗi tạm thời; dùng exponential backoff + jitter.
- **Dead-letter queue:** message lỗi nhiều lần được chuyển vào hàng đợi lỗi.
- **Saga:** điều phối/choreography quy trình nhiều bước và định nghĩa hành động bồi thường. Saga không thay thế transaction nội bộ hoặc Outbox.

### 9.3. Bảo mật và khả năng quan sát

**Bảo mật:**

- Dùng OAuth 2.0 / OpenID Connect.
- Gateway xác thực, nhưng mỗi service vẫn phải kiểm tra quyền truy cập tài nguyên.
- Không ghi log token, số thẻ hoặc dữ liệu cá nhân nhạy cảm.
- Không đặt secret trực tiếp trong source code.

**Observability:**

- Mỗi request/message mang `CorrelationId` hoặc trace context.
- Dùng OpenTelemetry để liên kết log, metric và distributed trace.
- Theo dõi các chỉ số:
  - Tỷ lệ đặt xe thành công.
  - Thời gian matching.
  - Độ trễ API.
  - Tỷ lệ thanh toán lỗi.
  - Độ dài hàng đợi message.

---

## 10. Các nguyên tắc cần giữ khi triển khai

1. Client chỉ giao tiếp với hệ thống thông qua API Gateway, không gọi trực tiếp từng microservice.
2. Trip Service là nguồn quản lý trạng thái chuyến đi.
3. Mỗi service sở hữu dữ liệu riêng; không truy cập trực tiếp database của service khác.
4. Matching and Pricing chịu trách nhiệm tìm tài xế và tính giá.
5. Payment Service chịu trách nhiệm giao dịch, sổ cái và hoàn tiền.
6. Các thao tác thanh toán phải idempotent để tránh thu tiền trùng.
7. Dùng giao tiếp đồng bộ khi cần phản hồi ngay; dùng event/message cho xử lý bất đồng bộ.
8. Không retry vô hạn; phải có timeout, retry có giới hạn và dead-letter queue.
9. Bảo mật phải được kiểm tra ở cả Gateway và service.
10. Ưu tiên hoàn thành luồng nghiệp vụ cốt lõi trước khi mở rộng Outbox, Saga và observability đầy đủ.

## 11. Phạm vi và điểm cần xác nhận khi hiện thực

Các nội dung sau được tài liệu nêu ở mức định hướng, chưa cung cấp đầy đủ chi tiết triển khai:

- Giá trị cụ thể của các hệ số trong công thức tính giá.
- Quy tắc chọn tài xế, bán kính tìm kiếm và cách cập nhật vị trí.
- Giá trị giới hạn `k` cho số lần tài xế từ chối.
- Cơ chế xác thực người dùng, phân quyền chi tiết và quản lý tài khoản.
- Cấu trúc bảng/collection và schema dữ liệu chi tiết.
- Định dạng payload của domain event và message.
- Chính sách retry cụ thể, timeout và thời gian lưu dead-letter message.
- Quy trình Saga chi tiết và các hành động bồi thường tương ứng.

Không nên tự xem các nội dung trên là yêu cầu đã được chốt nếu chưa có đặc tả bổ sung.

---

**Kết thúc tài liệu tóm tắt SwiftRide – Software Architecture v1.0.**
