package com.n3lx.minidrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniDrive {

	public static void main(String[] args) {
		SpringApplication.run(MiniDrive.class, args);
	}

}
