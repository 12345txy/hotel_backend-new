USE hotel_ac_db;

-- 此处插入"系统测试用例 冷"中的配置
INSERT INTO room_config VALUES
                            (1, 10, 100),
                            (2, 15, 125),
                            (3, 18, 150),
                            (4, 12, 200),
                            (5, 14, 100);

INSERT INTO ac_config VALUES
    (1, '制热', 18, 25, 22, 1,
     0.33, 0.5, 1, 'M');