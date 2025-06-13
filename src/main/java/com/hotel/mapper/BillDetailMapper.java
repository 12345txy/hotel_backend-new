package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.BillDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账单详单Mapper接口
 */
@Mapper
public interface BillDetailMapper extends BaseMapper<BillDetail> {
    /**
     * 根据账单ID查询账单详情
     * @param billId 账单ID
     * @return 账单详情列表
     */
    List<BillDetail> findByBillId(@Param("billId") Long billId);
}