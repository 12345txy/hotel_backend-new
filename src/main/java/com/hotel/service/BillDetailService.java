package com.hotel.service;
import com.hotel.entity.BillDetail;
import com.hotel.entity.RoomRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单详单服务接口
 */
public interface BillDetailService {

    /**
     * 保存账单详单
     * @param billDetail 账单详单实体
     * @return 保存是否成功
     */
    boolean saveBillDetail(BillDetail billDetail);

    /**
     * 根据房间ID和时间范围查询账单详单
     * @param roomId 房间ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 账单详单列表
     */
    List<BillDetail> getBillDetailsByRoomIdAndTimeRange(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据房间ID查询所有账单详单
     * @param roomId 房间ID
     * @return 账单详单列表
     */
    List<BillDetail> getBillDetailsByRoomId(Long roomId);

    /**
     * 根据房间ID和客户ID查询账单详单
     * @param roomId 房间ID
     * @param customerId 客户ID
     * @return 账单详单列表
     */
    List<BillDetail> getBillDetailsByRoomIdAndCustomerId(Long roomId, Long customerId);

    /**
     * 根据房间请求创建账单详单
     * @param request 房间请求
     * @param detailType 详单类型
     * @param mode 空调模式 (1:制冷, -1:制热)
     * @return 创建的账单详单
     */
    BillDetail createBillDetailByRequest(RoomRequest request, String detailType, int mode ,int tick);
}

