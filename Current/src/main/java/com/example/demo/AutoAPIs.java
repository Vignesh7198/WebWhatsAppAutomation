package com.example.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AutoAPIs {

	
	@Autowired
	Service service;
	
	
	Logger log = LoggerFactory.getLogger(AutoAPIs.class);
	
	@Autowired
	List<WhatsAppDetails> allUploadedDetails; 
	
	@PostMapping(path = "/uploadExcel")
	public ModelAndView excelUpload(@RequestParam("file") MultipartFile file) throws EncryptedDocumentException, IOException{
		
		log.info("Sending Excel file to Extraction");
		HashMap<String,String> invalidNumbers = service.excelDataExtraction(file);
		ModelAndView modelView = new ModelAndView();
		 modelView.addObject("invalidNumbers",invalidNumbers);
		 modelView.setViewName("index");
		 return modelView;
		

		
	}
	
	

}
