package com.example.demo.jpa.repository;

import com.example.demo.entity.Taco;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

public interface TacoRepository extends CrudRepository<Taco,Long> {
    Iterable<Taco> findAll(PageRequest page);
}
