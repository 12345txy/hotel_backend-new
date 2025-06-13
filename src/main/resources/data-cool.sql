USE hotel_ac_db;

-- 此处插入"系统测试用例 冷"中的配置
INSERT INTO room_config VALUES
                            (1, 32, 100),
                            (2, 28, 125),
                            (3, 30, 150),
                            (4, 29, 200),
                            (5, 35, 100);

INSERT INTO ac_config VALUES
    (1, '制冷', 18, 28, 25, 1,
     0.33, 0.5, 1, 'M');