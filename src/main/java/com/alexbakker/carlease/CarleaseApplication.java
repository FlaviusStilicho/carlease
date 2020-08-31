package com.alexbakker.carlease;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class CarleaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarleaseApplication.class, args);
		log.info("Hello there!");
	}

}
