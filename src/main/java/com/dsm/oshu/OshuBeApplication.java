package com.dsm.oshu;

import com.dsm.oshu.store.infrastructure.PublicDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(PublicDataProperties.class)
public class OshuBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OshuBeApplication.class, args);
	}

}
