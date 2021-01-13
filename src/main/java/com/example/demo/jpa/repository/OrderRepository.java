package com.example.demo.jpa.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findOrderByCityAndStateOrPlacedAt(String city, String state, Date placedAt);

    @Query("select o from Order o where o.name ='syc'")
    @Modifying//增强@Query效果，执行@Query中的操作
    void deleteAllByName(String name);

    List<Order> findByCityOrderByPlacedAt(String city);

}
