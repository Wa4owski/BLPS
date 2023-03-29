package com.example.lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class MoneymanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymanApplication.class, args);
	}

}
