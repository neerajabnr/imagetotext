package it.sella.f24.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sella.f24.bean.auth.AuthInputPayload;
import it.sella.f24.bean.auth.AuthParam;
import it.sella.f24.bean.auth.AuthResponse;
import it.sella.f24.bean.auth.AuthServiceInput;
import it.sella.f24.bean.auth.Payload;
import it.sella.f24.util.LoadPropertiesUtil;

@Service
public class Authservice {

	@Autowired
	private RestTemplate restTemplate;

	private static Properties props = null;
	
	public static String APIKey = null;

	static {

		PropertyConfigurator.configure("src/main/resources/f24template1.properties");
		props = LoadPropertiesUtil.loadPropertiesFile();
	}

	private ResponseEntity<String> callAuthService(AuthInputPayload payload) {
		System.setProperty("java.net.useSystemProxies", "false");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiKey", APIKey);
		headers.set("Auth-Schema", "S2S");
		System.out.println(headers);
		String authURL = props.getProperty("authURL");
		HttpEntity<AuthInputPayload> entity = new HttpEntity<AuthInputPayload>(payload, headers);
		ResponseEntity<String> response = null;
		response = restTemplate.exchange(authURL, HttpMethod.POST, entity, String.class);
		return response;
		// return resBody.getPayload().getAccessToken();

	}

	private AuthResponse authRequestStepOne(String userName, String password) {

		ResponseEntity<String> response = this.callAuthService(new AuthInputPayload());
		
		ObjectMapper mapper = new ObjectMapper();
		AuthResponse resBody = null;
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			
			try {
				resBody = mapper.readValue(response.getBody(), AuthResponse.class);

				if (Objects.nonNull(resBody.getPayload())) {
					String flowToken = resBody.getPayload().getFlowToken();
					System.out.println(flowToken);
					resBody = this.authRequestStepTwo(userName, password, flowToken);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resBody;
	}

	private AuthResponse authRequestStepTwo(String userName, String password, String flowToken) {
		AuthInputPayload payload = new AuthInputPayload();
		payload.setFlowToken(flowToken);
		AuthParam user = new AuthParam();
		user.setKey("USERNAME");
		user.setValue(userName);
		AuthParam pwd = new AuthParam();
		pwd.setKey("PASSWORD");
		pwd.setValue(password);
		AuthParam channelId = new AuthParam();
		channelId.setKey("CHANNEL_ID");
		channelId.setValue("platfr");
		List<AuthParam> data = new ArrayList<>();
		data.add(user);
		data.add(pwd);
		data.add(channelId);
		payload.setData(data);
		System.out.println(payload);
		AuthResponse resBody = null;
		ResponseEntity<String> response = this.callAuthService(payload);
		ObjectMapper mapper = new ObjectMapper();
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			
			try {
				resBody = mapper.readValue(response.getBody(), AuthResponse.class);
				System.out.println(resBody);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resBody;

	}

	private void authRequeststepThree() {

	}

	public AuthResponse authorise(AuthServiceInput authServiceInput) {
		APIKey = authServiceInput.getApiKey();
		return this.authRequestStepOne(authServiceInput.getUserName(), authServiceInput.getPassword());

	}
}
