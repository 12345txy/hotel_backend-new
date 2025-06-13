package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Bill;
import com.hotel.entity.BillDetail;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;

import java.util.List;

/**
 * 账单服务接口
 */
public interface BillService extends IService<Bill> {

    /**
     * 创建并结算账单
     * @param billDetails 账单详单列表
     * @param customer 顾客对象
     * @param room 房间对象
     * @return 结算后的账单
     */
    Bill createAndSettleBill(List<BillDetail> billDetails, Customer customer, Room room);

    /**
     * 根据房间ID查询账单
     * @param roomId 房间ID
     * @return 账单列表
     */
    List<Bill> getBillsByRoomId(Long roomId);

    /**
     * 根据顾客ID查询账单
     * @param customerId 顾客ID
     * @return 账单列表
     */
    List<Bill> getBillsByCustomerId(Long customerId);

    /**
     * 根据账单ID获取完整账单信息（包括详单）
     * @param billId 账单ID
     * @return 包含详单的账单
     */
    Bill getFullBillById(Long billId);
}
