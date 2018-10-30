package it.sella.f24.testclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/f24image")
public class RestClientDemo {
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/hello")
	public ResponseEntity<String> callService() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://f24imagetotext.herokuapp.com/f24/api/simplificato/ocr", HttpMethod.POST, null, String.class);
		
		System.out.println(response);
		return response;
	}

}
