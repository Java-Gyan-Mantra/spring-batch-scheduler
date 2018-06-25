package com.spring.batch.record.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.batch.record.api.dao.TicketRepository;
import com.spring.batch.record.api.model.Ticket;

@RestController
public class TicketController {

	@Autowired
	private TicketRepository ticketRepository;

	@PostMapping("/bookTickets")
	public String bookTickets(@RequestBody List<Ticket> tickets) {
		ticketRepository.saveAll(tickets);
		return tickets.size() + " ticket booked successfully...";
	}

	@GetMapping("/getAlltickets")
	public List<Ticket> getAllTickets() {
		return ticketRepository.findAll();
	}
}
