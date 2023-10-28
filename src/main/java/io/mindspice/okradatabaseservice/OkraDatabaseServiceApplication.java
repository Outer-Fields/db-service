package io.mindspice.okradatabaseservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@SpringBootApplication
public class OkraDatabaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OkraDatabaseServiceApplication.class, args);

		// Print endpoints

	}

}
