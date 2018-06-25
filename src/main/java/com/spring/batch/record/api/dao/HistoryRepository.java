package com.spring.batch.record.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.batch.record.api.model.History;

public interface HistoryRepository extends JpaRepository<History, Integer> {

}
