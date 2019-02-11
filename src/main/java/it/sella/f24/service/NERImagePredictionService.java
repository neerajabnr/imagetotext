package it.sella.f24.service;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.F24JSON;
import it.sella.f24.controller.F24Controller;
import it.sella.f24.util.LoadPropertiesUtil;

@Service
public class NERImagePredictionService {
	
	
	
	@Autowired
	private GoogleService googleOCRService;
	
	@Autowired
	private SkewService skewService;
	
	@Autowired
	private NERService nerService;
	
	
	
	public void predict(MultipartFile image,String instanceName) throws Exception {
		
		System.out.println("Calling Skew Service for image skewing");
		byte[] decodeBase64 = skewService.skew(image);

		System.out.println("Calling Google Service for processing of the Image data");
		Data data = googleOCRService.readText(decodeBase64, "");
		
		System.out.println("Calling Open NLP NER Service");
		nerService.trainandTest(data, instanceName);
	}

}
