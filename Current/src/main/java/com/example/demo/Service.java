package com.example.demo;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Service
public class Service {
	
	
	Logger log = LoggerFactory.getLogger(Service.class);

	@Autowired
	 List<WhatsAppDetails> allUploadedDetails;
	
	 boolean login;
	 int contactsSize;
	 int contactsCount;
	 int count;

	
	HashMap<String,String> unknownMobileNumbers = new HashMap<String, String>();
	
	
	ChromeOptions options = new ChromeOptions();
    WebDriver driver = new ChromeDriver(options.addArguments("C:\\Users\\Vignesh S\\AppData\\Local\\Google\\Chrome\\User Data\\Default"));
	
	public HashMap<String,String> excelDataExtraction(MultipartFile file) throws EncryptedDocumentException, IOException {
		
		List<WhatsAppDetails> whatsAppDetails = new ArrayList<>();
		Workbook workBook = WorkbookFactory.create(file.getInputStream());
	    Sheet sheet =  workBook.getSheetAt(0);
	for(Row row :sheet) {
		String mobileNumber = row.getCell(0).getStringCellValue();		
	    String message = row.getCell(1).getStringCellValue();		    
	    whatsAppDetails.add(new WhatsAppDetails(mobileNumber,message));
	    log.info(mobileNumber);
	}
	allUploadedDetails = whatsAppDetails;
		whatsAppAutomationCycle(allUploadedDetails);
		return unknownMobileNumbers;
	}

	public void whatsAppAutomationCycle(List<WhatsAppDetails> whatsAppDetails) {
	    log.info("Details Submitted to whatsAppAutomation");
//	    contactsSize = whatsAppDetails.size();
		for(WhatsAppDetails chatNumberAndDetail : whatsAppDetails) {
		String mobileNumber =	chatNumberAndDetail.getMobileNumber();
		String message =	chatNumberAndDetail.getMessage();	
		whatsAppWebRedirect("+91"+mobileNumber, message);
	    log.info("completed redirection");
		}
		System.out.println("Automation Completed");
    	System.out.println(unknownMobileNumbers);
    	
    	driver.quit();

	}

	
	public void whatsAppWebRedirect(String mobileNumber, String message) {
		log.info("Inside Redirection");
		count++;
System.out.println(count);
	    driver.get("https://api.whatsapp.com/send/?phone=" + mobileNumber + "&text=" + message + "&type=phone_number&app_absent=0");

	    // Wait for the WhatsApp Web interface to load with retries and timeout
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
	    boolean isLoaded = false;
	    int maxRetries = 3;
	    int retryCount = 0;

	    while (!isLoaded && retryCount < maxRetries) {
	        try {
	            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main_block\"]/div[1]")));
	            isLoaded = true;
	        } catch (Exception e) {
	            log.warn("WhatsApp Web interface loading timed out. Retrying...");
	            log.warn("Exception Occured", e);
	            driver.navigate().refresh(); // Refresh the page and retry
	            retryCount++;
	        }
	    }

	    if (!isLoaded) {
	        log.error("WhatsApp Web interface failed to load after retries. Exiting.");
	        driver.quit();
	        return;
	    }
	    
	    //Clicks the Continue to chat button
	    WebElement clickButton = driver.findElement(By.xpath("//*[@id=\"action-button\"]/span"));
	    clickButton.click(); 
		//Clicks the use whatsApp button
	    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"fallback_block\"]/div/div/h4[2]/a/span")));
	    element.click();

	    
//	    WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(20));
	  
	    Boolean QRCodeORChatWindowAvailability = loginWait(driver, mobileNumber, message);

	
	try{
	    if(QRCodeORChatWindowAvailability) {
	    	
	    	WebElement QRCodeCheck;
	    	try {
	    	QRCodeCheck = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div/div[3]/div[1]/div/div/div[2]/div[2]/canvas"));
	    	}catch(NoSuchElementException e) {
	    		QRCodeCheck = null;
	    	}
	    	
	    	if(QRCodeCheck!=null) {
	    		System.out.println("QR Exists");

	    		WebDriverWait QRDisappearance = new WebDriverWait(driver, Duration.ofSeconds(60));
	    		
	    		try {
	    		QRDisappearance.until(ExpectedConditions.invisibilityOf(QRCodeCheck));
//	    		QRDisappearance.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span"))));
	    		}
	    		catch(org.openqa.selenium.TimeoutException e) {
	    			driver.navigate().refresh();
	    		}
	    		try {
	            WebElement invalidNumberPopup = driver.findElement(By.xpath("//*[@id=\"app\"]/div/span[2]/div/span/div/div/div/div/div/div[2]/div/button"));
	            if (invalidNumberPopup.isDisplayed()) {
	            	
	            	unknownMobileNumbers.put(mobileNumber, message);
	            	System.out.println("Invalid number");
	            	return;	
	            }

	    		
	    		}catch(Exception e) {
System.out.println(unknownMobileNumbers);	    			
	    		}
	    	//*[@id="app"]/div/span[2]/div/span/div/div/div/div/div/div[1]
	    		try {
	    		QRDisappearance.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span")) );
	    		WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span"));
		 	    wait.until(ExpectedConditions.visibilityOf(sendButton));
		 	    sendButton.click(); 
	    		}
		 	   catch(NoSuchElementException noElement) {
   				
   			}
		 	    
		 	    try {
		 	    Thread.sleep(3000);
		 	    }catch(Exception e) {
		 	    	e.printStackTrace();
		 	    }
//		 	    driver.navigate().refresh();
//		 	    driver.wait(3000);
		 	     
		 	    List<WebElement> messageElements =  driver.findElements(By.cssSelector("div.message-in, div.message-out"));
		 	    
		 	    for(WebElement messageElement : messageElements) {
		 	    	
		 	    	String text = messageElement.getText();
		 	    	
		 	    	if(text.contains(message)) {
		 	    		
		 	    		System.out.println("Message Sent");
		 	    		break;
		 	    		
		 	    	}
		 	    	
		 	    }
		 	   try {
			 	    Thread.sleep(3000);
			 	    }catch(Exception e) {
			 	    	e.printStackTrace();
			 	    }
		 	     
		//     	 wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("message-in"), message));
	    	}
	    	else{
	    		
	    		System.out.println("QR Check Null");
	    		
	    		try {
	    		WebElement invalidNumber = driver.findElement(By.xpath("//*[@id=\"app\"]/div/span[2]/div/span/div/div/div/div/div/div[2]/div/button"));
	    		System.out.println("Invalid Number");
            	unknownMobileNumbers.put(mobileNumber, message);
	    		return;
	    		}
	    		catch(Exception e) {
	    			
	    			try {
	    	   	    WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span"));
		    	    
		    	    wait.until(ExpectedConditions.visibilityOf(sendButton));
		 	    	sendButton.click(); 
	    			}
	    			catch(NoSuchElementException noElement) {
	    				
	    			}
		 	    	
		 	    	  try {
					 	    Thread.sleep(3000);
					 	    }catch(Exception ex) {
					 	    	ex.printStackTrace();
					 	    }
		 	    	
//			 	    driver.navigate().refresh();
		 	    	  
				 	    List<WebElement> messageElements =  driver.findElements(By.cssSelector("div.message-in, div.message-out"));
				 	    
				 	    for(WebElement messageElement : messageElements) {
				 	    	
				 	    	String text = messageElement.getText();
				 	    	
				 	    	if(text.contains(message)) {
				 	    		
				 	    		System.out.println("Message Sent");
				 	    		break;
				 	    		
				 	    	}
				 	    	
				 	    }
	    		}
	    		
	 

	//     	 System.out.println(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("message-in"), message)));
	    	}
	    }
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	}
	
	
	public Boolean loginWait(WebDriver driver, String mobileNumber, String message){
		
		try {
			 WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(20));
			loginWait.until(
					ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"app\"]/div/div/div[3]/div[1]/div/div/div[2]/div[2]/canvas")), //QR Code Test
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[2]/button/span")), //QR Chat Window
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"app\"]/div/span[2]/div/span/div/div/div/div/div/div[2]/div/button"))) // Invalid Number
					);
			login = true;
		}catch(Exception e) {
			login =false;
			System.out.println("Refreshing..");
			whatsAppWebRedirect(mobileNumber, message);
		}
		return login;
	}

	}
