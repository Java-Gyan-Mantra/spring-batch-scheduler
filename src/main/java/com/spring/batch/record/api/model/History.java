package com.spring.batch.record.api.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
	@Id
	private int id;
	private String name;
	private String source;
	private String destination;
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	private Date journeyDate;
}
