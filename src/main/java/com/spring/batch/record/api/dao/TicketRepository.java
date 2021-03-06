package com.spring.batch.record.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.batch.record.api.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

}
