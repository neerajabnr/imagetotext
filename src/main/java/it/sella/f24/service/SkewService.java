package it.sella.f24.service;

import java.io.File;
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

import it.sella.f24.bean.F24JSON;
import it.sella.f24.controller.F24Controller;
import it.sella.f24.util.LoadPropertiesUtil;

@Service
public class SkewService {
	
	private static Logger logger = null;
	private static Properties props = null;
	static {
		logger = Logger.getLogger(F24Controller.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		props = LoadPropertiesUtil.loadPropertiesFile();
	}
	
	@Autowired
	private RestTemplate restTemplate;
	
	public byte[] skew(MultipartFile image) throws IOException {
		
		String encodeBase64String = Base64.encodeBase64String(image.getBytes());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("encodedImage", encodeBase64String);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(props.getProperty("ServiceURL"),
				HttpMethod.POST, entity, String.class);

		ObjectMapper mapper = new ObjectMapper();
		F24JSON f24json = mapper.readValue(response.getBody(), F24JSON.class);
		
		byte[] decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());
		return decodeBase64;
	}

}
