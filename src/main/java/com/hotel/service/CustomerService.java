package com.hotel.service;

import com.hotel.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * 顾客服务接口
 */
public interface CustomerService {

    /**
     * 保存顾客信息
     * @param customer 顾客对象
     * @return 保存后的顾客对象
     */
    Customer saveCustomer(Customer customer);

    /**
     * 根据ID查询顾客
     * @param id 顾客ID
     * @return 顾客对象（若存在）
     */
    Optional<Customer> getCustomerById(Long id);

    /**
     * 根据房间ID查询当前入住的顾客
     * @param roomId 房间ID
     * @return 顾客对象（若存在）
     */
    Optional<Customer> getCustomerByRoomId(Long roomId);

    /**
     * 更新顾客信息
     * @param customer 顾客对象
     * @return 是否更新成功
     */
    boolean updateCustomer(Customer customer);

    /**
     * 办理退房
     * @param roomId 房间ID
     * @return 是否退房成功
     */
    boolean checkOut(Long roomId);

    /**
     * 查询所有顾客
     * @return 顾客列表
     */
    List<Customer> getAllCustomers();
    /**
     * 根据房间ID查找当前入住的顾客
     * @param roomId 房间ID
     * @return 当前入住的顾客，如果没有则返回null
     */
    Customer findCurrentCustomerByRoomId(Long roomId);
}