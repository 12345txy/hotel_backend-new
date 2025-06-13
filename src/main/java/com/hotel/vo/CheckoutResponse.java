package com.hotel.vo;

import lombok.Data;

import java.util.List;

/**
 * 退房响应VO
 */
@Data
public class CheckoutResponse {

    /**
     * 详单列表
     */
    private List<DetailBill> detailBill;

    /**
     * 账单信息
     */
    private BillResponse bill;

    /**
     * 详单明细VO
     */
    @Data
    public static class DetailBill {
        private Long roomId;
        private String startTime;
        private String endTime;
        private Long duration;
        private String fanSpeed;
        private Double currentFee;
        private Double fee;
    }

    /**
     * 账单信息VO
     */
    @Data
    public static class BillResponse {
        private Long roomId;
        private String checkinTime;
        private String checkoutTime;
        private String duration;
        private Double roomFee;
        private Double acFee;
    }
}
