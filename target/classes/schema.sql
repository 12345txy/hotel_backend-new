-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotel_ac_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotel_ac_db;

-- 房间表
DROP TABLE IF EXISTS room_config;
CREATE TABLE room_config (
    id INT PRIMARY KEY COMMENT '房间ID(1-5)',
    default_temp DOUBLE NOT NULL COMMENT '默认温度',
    daily_rate DOUBLE NOT NULL COMMENT '日租价格'
);
-- 创建账单表
CREATE TABLE `bills` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账单ID',
                         `room_id` bigint DEFAULT NULL COMMENT '房间ID',
                         `check_in_time` datetime DEFAULT NULL COMMENT '入住时间',
                         `check_out_time` datetime DEFAULT NULL COMMENT '退房时间',
                         `stay_days` int DEFAULT NULL COMMENT '住宿天数',
                         `room_fee` decimal(10,2) DEFAULT NULL COMMENT '房费(元)',
                         `ac_total_fee` decimal(10,2) DEFAULT NULL COMMENT '空调总费用(元)',
                         `total_amount` decimal(10,2) DEFAULT NULL COMMENT '账单总金额(元)',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         PRIMARY KEY (`id`),
                         KEY `idx_room_id` (`room_id`),
                         KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';
-- 创建客户表
DROP TABLE IF EXISTS customers;
CREATE TABLE `customers` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(50) DEFAULT NULL COMMENT '姓名',
    `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
    `phone_number` varchar(20) DEFAULT NULL COMMENT '电话号码',
    `current_room_id` bigint DEFAULT NULL COMMENT '当前入住房间ID',
    `check_in_time` datetime DEFAULT NULL COMMENT '入住时间',
    `check_out_time` datetime DEFAULT NULL COMMENT '退房时间',
    `check_in_days` int DEFAULT NULL COMMENT '入住天数',
    `status` varchar(20) DEFAULT NULL COMMENT '客户状态：CHECKED_IN(已入住), CHECKED_OUT(已退房)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`current_room_id`),
    KEY `idx_status` (`status`),
    KEY `idx_check_in_time` (`check_in_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '客户表';

# CREATE TABLE IF NOT EXISTS rooms (
#     id BIGINT PRIMARY KEY COMMENT '房间ID(1-5)',
#     status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '房间状态',
#     current_temp DOUBLE DEFAULT 32.0 COMMENT '当前温度',
#     target_temp DOUBLE COMMENT '目标温度',
#     ac_on BOOLEAN DEFAULT FALSE COMMENT '空调是否开启',
#     ac_mode VARCHAR(20) COMMENT '空调模式',
#     fan_speed VARCHAR(20) COMMENT '风速',
#     current_ac_id BIGINT COMMENT '当前服务的空调ID',
#     customer_id BIGINT COMMENT '入住客户ID',
#     check_in_time DATETIME COMMENT '入住时间',
#     ac_request_time DATETIME COMMENT '空调请求时间',
#     service_start_time DATETIME COMMENT '服务开始时间',
#     waiting_start_time DATETIME COMMENT '等待开始时间',
#     service_duration BIGINT DEFAULT 0 COMMENT '服务时长(秒)',
#     waiting_duration BIGINT DEFAULT 0 COMMENT '等待时长(秒)',
#     initial_temp DOUBLE DEFAULT 32.0 COMMENT '房间初始温度',
#     is_warming_back BOOLEAN DEFAULT FALSE COMMENT '是否正在回温',
#     warming_start_time DATETIME COMMENT '回温开始时间',
#     paused_mode VARCHAR(20) COMMENT '暂停时的空调模式',
#     paused_fan_speed VARCHAR(20) COMMENT '暂停时的风速',
#     paused_target_temp DOUBLE COMMENT '暂停时的目标温度',
#     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     INDEX idx_status (status),
#     INDEX idx_customer_id (customer_id)
# ) COMMENT '房间表';

-- 客户表
# CREATE TABLE IF NOT EXISTS customers (
#     id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(100) NOT NULL COMMENT '姓名',
#     id_card VARCHAR(18) NOT NULL COMMENT '身份证号',
#     phone_number VARCHAR(20) NOT NULL COMMENT '电话号码',
#     current_room_id BIGINT COMMENT '当前入住房间ID',
#     check_in_time DATETIME COMMENT '入住时间',
#     check_out_time DATETIME COMMENT '退房时间',
#     status VARCHAR(20) NOT NULL DEFAULT 'CHECKED_IN' COMMENT '客户状态',
#     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     INDEX idx_id_card (id_card),
#     INDEX idx_phone (phone_number),
#     INDEX idx_room_id (current_room_id),
#     FOREIGN KEY (current_room_id) REFERENCES rooms (id) ON DELETE SET NULL
# ) COMMENT '客户表';

-- 空调表
DROP TABLE IF EXISTS ac_config;
CREATE TABLE ac_config (
    id INT PRIMARY KEY COMMENT '配置编号',
    mode VARCHAR(20) NOT NULL COMMENT '工作模式',
    min_temp DOUBLE NOT NULL COMMENT '最小温度',
    max_temp DOUBLE NOT NULL COMMENT '最大温度',
    default_temp DOUBLE NOT NULL COMMENT '默认温度',
    rate DOUBLE NOT NULL COMMENT '费率(x元/摄氏度)',
    low_speed_rate DOUBLE NOT NULL COMMENT '低风降温速率(x摄氏度/分钟)',
    mid_speed_rate DOUBLE NOT NULL COMMENT '中风降温速率(x摄氏度/分钟)',
    high_speed_rate DOUBLE NOT NULL COMMENT '高风降温速率(x摄氏度/分钟)',
    default_speed enum('L', 'M', 'H') NOT NULL COMMENT '默认风速'
);
# CREATE TABLE IF NOT EXISTS air_conditioners (
#     id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     ac_number INT NOT NULL UNIQUE COMMENT '空调编号',
#     serving_room_id BIGINT COMMENT '当前服务的房间ID',
#     on_status BOOLEAN DEFAULT FALSE COMMENT '是否开启',
#     mode VARCHAR(20) COMMENT '工作模式',
#     fan_speed VARCHAR(20) COMMENT '风速',
#     target_temp DOUBLE DEFAULT 0.0 COMMENT '目标温度',
#     current_temp DOUBLE DEFAULT 0.0 COMMENT '当前温度',
#     request_time DATETIME COMMENT '请求时间',
#     service_start_time DATETIME COMMENT '服务开始时间',
#     service_end_time DATETIME COMMENT '服务结束时间',
#     service_duration BIGINT DEFAULT 0 COMMENT '服务时长(分钟)',
#     cost DOUBLE DEFAULT 0.0 COMMENT '当前费用',
#     priority INT DEFAULT 0 COMMENT '优先级',
#     service_time BIGINT DEFAULT 0 COMMENT '服务时间(分钟)',
#     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     INDEX idx_ac_number (ac_number),
#     INDEX idx_serving_room (serving_room_id)
# ) COMMENT '空调表';

-- 账单主表
# CREATE TABLE IF NOT EXISTS bills (
#     id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     bill_number VARCHAR(50) NOT NULL UNIQUE COMMENT '账单号',
#     room_id BIGINT NOT NULL COMMENT '房间ID',
#     customer_id BIGINT NOT NULL COMMENT '客户ID',
#     customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
#     check_in_time DATETIME NOT NULL COMMENT '入住时间',
#     check_out_time DATETIME NOT NULL COMMENT '退房时间',
#     stay_days INT NOT NULL COMMENT '住宿天数',
#     room_fee DOUBLE NOT NULL COMMENT '房费(元)',
#     ac_total_fee DOUBLE NOT NULL DEFAULT 0.0 COMMENT '空调总费用(元)',
#     total_amount DOUBLE NOT NULL COMMENT '账单总金额(元)',
#     status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT '账单状态',
#     remark TEXT COMMENT '备注',
#     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     INDEX idx_bill_number (bill_number),
#     INDEX idx_room_id (room_id),
#     INDEX idx_customer_id (customer_id),
#     INDEX idx_status (status),
#     FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE RESTRICT,
#     FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE RESTRICT
# ) COMMENT '账单主表';

# 账单详单表
DROP TABLE IF EXISTS bill_details;
CREATE TABLE bill_details(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL COMMENT '房间ID',
    ac_mode VARCHAR(20) COMMENT '空调模式',
    fan_speed VARCHAR(20) COMMENT '风速',
    request_time DATETIME NOT NULL COMMENT '请求时间',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration BIGINT NOT NULL COMMENT '持续时间(分钟)',
    cost DOUBLE NOT NULL COMMENT '费用',
    rate DOUBLE NOT NULL COMMENT '费率(元/分钟)',
    detail_type VARCHAR(50) NOT NULL COMMENT '详单类型',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '账单详单表';

-- 初始化房间数据 (roomId: 1-5)
# INSERT INTO
#     rooms (
#         id,
#         status,
#         current_temp,
#         initial_temp
#     )
# VALUES (1, 'AVAILABLE', 32.0, 32.0),
#     (2, 'AVAILABLE', 32.0, 32.0),
#     (3, 'AVAILABLE', 32.0, 32.0),
#     (4, 'AVAILABLE', 32.0, 32.0),
#     (5, 'AVAILABLE', 32.0, 32.0)
# ON DUPLICATE KEY UPDATE
#     status = VALUES(status);

-- 初始化空调数据
# INSERT INTO
#     air_conditioners (ac_number)
# VALUES (1),
#     (2),
#     (3)
# ON DUPLICATE KEY UPDATE
#     ac_number = VALUES(ac_number);

-- 添加房间表字段：分配的空调号（手动执行以下SQL，如果字段已存在会报错可忽略）
-- ALTER TABLE rooms ADD COLUMN assigned_ac_number INT COMMENT '分配的空调编号';
-- ALTER TABLE rooms ADD INDEX idx_assigned_ac (assigned_ac_number);