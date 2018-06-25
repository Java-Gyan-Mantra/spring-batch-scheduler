package com.spring.batch.record.api.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.batch.record.api.dao.TicketRepository;
import com.spring.batch.record.api.model.History;
import com.spring.batch.record.api.model.Ticket;

@Configuration
@EnableBatchProcessing
public class BatchProcessConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private TicketRepository ticketRepository;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Bean
	public ItemReader<Ticket> reader() throws Exception {
		String jpqlQuery = "select t from Ticket  t";
		JpaPagingItemReader<Ticket> reader = new JpaPagingItemReader<>();
		reader.setQueryString(jpqlQuery);
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setPageSize(100);
		reader.afterPropertiesSet();
		reader.setSaveState(true);
		return reader;
	}

	@Bean
	public ItemProcessor<Ticket, History> processor() {
		/* using lambda here */
		ItemProcessor<Ticket, History> itemProcessor = ticket -> {
			/*
			 * here am checking duration for 1 month , if record already have more than 1
			 * month in DB then delete it
			 */
			if (getDuration(sdf.format(ticket.getJourneyDate()), sdf.format(new Date())) > 30) {
				/* if one month old record then deleting */
				ticketRepository.delete(ticket);
				return new History(ticket.getId(), ticket.getName(), ticket.getSource(), ticket.getDestination(),
						ticket.getJourneyDate());
			} else {
				return null;
			}

		};
		return itemProcessor;
	}

	private int getDuration(String fromDate, String toDate) {
		int days = 0;
		int avgDate = 0;
		try {
			Date startDate = sdf.parse(fromDate);
			Date endDate = sdf.parse(toDate);
			long diff = endDate.getTime() - startDate.getTime();
			days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			days = days + avgDate;
		} catch (ParseException e) {
			// handle exception (if any)
		}

		return days;

	}

	@Bean
	public ItemWriter<History> writer() {
		JpaItemWriter<History> writer = new JpaItemWriter<History>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1").<Ticket, History>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	@Bean
	public Job exportPerosnJob() throws Exception {
		return jobBuilderFactory.get("exportPeronJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}
}
