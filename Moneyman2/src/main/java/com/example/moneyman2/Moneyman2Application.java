package com.example.moneyman2;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRabbit
@EnableJms
@EnableScheduling
public class Moneyman2Application {

	public static void main(String[] args) {
		SpringApplication.run(Moneyman2Application.class, args);
	}

}
