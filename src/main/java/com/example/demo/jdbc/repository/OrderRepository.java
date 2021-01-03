package com.example.demo.jdbc.repository;

import com.example.demo.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}
