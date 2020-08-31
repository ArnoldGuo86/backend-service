package com.mguo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*Comment out for using mock db*/
//@MapperScan(value = "com.mguo.mapper")
public class BackndServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackndServiceApplication.class, args);
	}

}
