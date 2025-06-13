package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 账单Mapper接口
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    
    /**
     * 根据客户ID查询账单列表
     */
    @Select("SELECT * FROM bills WHERE customer_id = #{customerId} ORDER BY create_time DESC")
    List<Bill> findByCustomerId(Long customerId);
    
    /**
     * 根据房间号查询账单列表
     */
    @Select("SELECT * FROM bills WHERE room_number = #{roomNumber} ORDER BY create_time DESC")
    List<Bill> findByRoomNumber(Integer roomNumber);
    
    /**
     * 根据房间ID查询账单列表
     */
    @Select("SELECT * FROM bills WHERE room_id = #{roomId} ORDER BY create_time DESC")
    List<Bill> findByRoomId(Long roomId);
    
    /**
     * 根据账单状态查询账单列表
     */
    @Select("SELECT * FROM bills WHERE status = #{status} ORDER BY create_time DESC")
    List<Bill> findByStatus(String status);
    
    /**
     * 查询未支付的账单
     */
    @Select("SELECT * FROM bills WHERE status = 'UNPAID' ORDER BY create_time DESC")
    List<Bill> findUnpaidBills();
} 