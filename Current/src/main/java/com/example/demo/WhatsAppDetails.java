package com.example.demo;

import org.springframework.stereotype.Component;





@Component
public class WhatsAppDetails {

	public WhatsAppDetails(String mobileNumber, String message) {
		super();
		this.mobileNumber = mobileNumber;
		this.message = message;
	}
	
	private String mobileNumber;
	
	private String message;
	

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
