package com.zjh.flashsale.db.dao;

import com.zjh.flashsale.db.po.Order;

public interface OrderDao {
    public void insertOrder(Order order);

    public Order queryOrder(String orderNo);

    public void updateOrder(Order order);
}
