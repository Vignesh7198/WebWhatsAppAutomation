package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ExcelAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExcelAutomationApplication.class, args);
	}
	
	   @Bean
	    public List<WhatsAppDetails> allUploadedDetails() {
	        return new ArrayList<WhatsAppDetails>();
	    }
	   
	   
//	   @Bean
//	   public WebDriver webDriver() {
//	       return WebDriverManager.chromedriver().create();
//	   }
	   
	   @Bean
	   public java.lang.String String(){
		return new String();
	   }

}
