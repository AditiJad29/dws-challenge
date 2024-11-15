package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class ChallengeApplication {
	@Autowired
	private AccountsService accountsService;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

	@PostConstruct
	public void setUpData(){
		accountsService.createAccount(new Account("1", BigDecimal.valueOf(1000L)));
		accountsService.createAccount(new Account("2", BigDecimal.valueOf(500L)));
		accountsService.createAccount(new Account("3", BigDecimal.valueOf(2000L)));
	}

}
