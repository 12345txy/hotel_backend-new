# 酒店空调管理系统

## 项目介绍

这是一个基于Spring Boot的酒店空调管理系统，实现了酒店房间管理、空调调度、计费等核心功能。

### 主要功能

1. **房间管理**
   - 办理入住
   - 办理退房
   - 查看可用房间

2. **空调服务**
   - 开启/关闭空调
   - 调节空调参数（模式、风速、温度）
   - 智能调度系统

3. **调度策略**
   - 风速优先调度
   - 时间片轮转（120秒）
   - 优先队列管理

4. **计费系统**
   - 房费计算
   - 空调费用计算
   - 详单生成

## 技术栈

- **后端**: Spring Boot 2.7.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.2
- **构建工具**: Maven
- **Java版本**: 11

## 项目结构

```
src/
├── main/
│   ├── java/com/hotel/
│   │   ├── controller/          # 控制器层
│   │   ├── service/             # 业务逻辑层
│   │   ├── mapper/              # 数据访问层
│   │   ├── entity/              # 实体类
│   │   ├── dto/                 # 数据传输对象
│   │   ├── enums/               # 枚举类
│   │   ├── config/              # 配置类
│   │   └── HotelAcSystemApplication.java
│   └── resources/
│       ├── application.yml      # 应用配置
│       └── schema.sql          # 数据库脚本
```

## 快速开始

### 1. 环境准备

- JDK 11+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE hotel_ac_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_ac_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
```

### 3. 运行项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd hotel-ac-system

# 编译并运行
mvn spring-boot:run
```

项目启动后访问：http://localhost:8080

## API 接口

### 酒店管理接口

#### 获取可用房间
```
GET /api/hotel/rooms/available
```

#### 办理入住
```
POST /api/hotel/checkin
Content-Type: application/json

{
    "name": "张三",
    "idCard": "123456789012345678",
    "phoneNumber": "13800138000",
    "roomNumber": 1
}
```

#### 办理退房
```
POST /api/hotel/checkout/{roomId}
```

### 空调管理接口

#### 开启空调
```
POST /api/ac/room/{roomId}/start?currentTemp=30.0
```

**参数说明:**
- `roomId`: 房间ID (1-5)
- `currentTemp`: 当前温度（可选参数）
  - 如果提供：设置房间当前温度为指定值
  - 如果不提供：使用房间默认温度

#### 关闭空调
```
POST /api/ac/room/{roomId}/stop
```

#### 调节空调参数
```
PUT /api/ac/room/{roomId}/adjust?fanSpeed=HIGH&targetTemp=22&mode=COOLING
```

#### 获取调度状态
```
GET /api/ac/schedule/status
```

#### 获取房间空调状态
```
GET /api/ac/room/{roomId}/status
```

### 账单管理接口

#### 查询所有账单
```
GET /api/bills
```

#### 根据ID查询账单详情
```
GET /api/bills/{billId}
```

#### 根据客户ID查询账单
```
GET /api/bills/customer/{customerId}
```

#### 根据房间号查询账单（兼容接口）
```
GET /api/bills/room/{roomNumber}
```

#### 根据房间ID查询账单
```
GET /api/bills/room-id/{roomId}
```

#### 查询未支付账单
```
GET /api/bills/unpaid
```

#### 支付账单
```
POST /api/bills/{billId}/pay
```

#### 取消账单
```
POST /api/bills/{billId}/cancel
```

#### 查询账单详单
```
GET /api/bills/{billId}/details
```

### 测试接口 (/api/test)

#### 手动触发时间片检查
```
POST /api/test/time-slice-check
```

#### 手动触发温度更新
```
POST /api/test/temperature-update
```

#### 获取所有房间状态
```
GET /api/test/rooms/status
```

#### 获取指定房间状态
```
GET /api/test/rooms/{roomId}/status
```

#### 设置房间温度（测试用）
```
POST /api/test/rooms/{roomId}/temperature?temperature=25.0
```

#### 重置系统状态
```
POST /api/test/reset
```

## 调度策略说明

### 优先级规则
1. **风速优先**: HIGH(3) > MEDIUM(2) > LOW(1)
2. **时间片轮转**: 相同风速时，等待时间≥120秒触发轮转
3. **房间号优先**: 风速和时间相同时，房间号小的优先

### 调度触发条件
1. 调风操作
2. 关机操作或达到目标温度
3. 新的空调请求
4. 时间片轮转（120秒）

### 费率标准
- **低风速**: 0.5元/分钟
- **中风速**: 1.0元/分钟
- **高风速**: 1.5元/分钟
- **房费**: 100元/天

## 系统配置

在 `application.yml` 中可以配置以下参数：

```yaml
hotel:
  ac:
    total-count: 3      # 总空调数量
    room-count: 5       # 总房间数量
    default-temp: 25    # 默认温度
    time-slice: 120     # 时间片轮转时间(秒)
  billing:
    room-rate: 100.0    # 房费(元/天)
    ac-rate:
      low: 0.5          # 低风速费率
      medium: 1.0       # 中风速费率
      high: 1.5         # 高风速费率
```

## 测试用例

项目包含了制冷和制热两种模式的测试用例，可以参考 `csv_output1` 目录下的测试数据。

## 开发说明

### 添加新功能

1. 在相应的包下创建新的类文件
2. 遵循现有的代码结构和命名规范
3. 添加必要的注释和文档

### 数据库变更

1. 修改 `schema.sql` 文件
2. 更新对应的实体类
3. 如需要，更新Mapper接口

## 许可证

MIT License