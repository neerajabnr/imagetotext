package it.sella.f24.controller;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Base64;

import it.sella.f24.service.F24OCRService;
import it.sella.f24.service.GoogleService;
import it.sella.f24.service.opennlp.Data;
import it.sella.f24.view.F24Form;

@RestController
@RequestMapping("/f24")
public class F24Controller {
	
	@Autowired
	private F24OCRService ocrService;
	
	@Autowired
	private GoogleService googleService;
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@RequestMapping(value="/api/simplificato/form/ocr",method=RequestMethod.POST)
	public String f24ImageToText(@RequestBody F24Form f24Form) {
		System.out.println(f24Form);
		
		
		
//		https://f24-img-skew.herokuapp.com/f24/api/imageskew
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("encoded_img",f24Form.getEncodedImage());

		ResponseEntity<String> response = restTemplate.exchange(
				"https://f24-img-skew.herokuapp.com/f24/api/imageskew", HttpMethod.POST, new HttpEntity<>(headers), String.class);
				
		System.out.println("hello"+response);	
		byte[] decodeBase64 = Base64.decodeBase64(f24Form.getEncodedImage()); // TODO CHANGES REQUIRED
		Data data = null;
		String f24Result ="{}";
		try {
			// TODO call skew image service
			data = googleService.readText(decodeBase64,"");
		    f24Result = ocrService.processJson(data);
		} catch (IOException e) {
			return "{\"status\":\"KO\"}";
		} catch (Exception e) {
			return "{\"status\":\"KO\"}";
		}
		
		//String sampleResult = "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";
		
		return f24Result;
	}
	/*
	@RequestMapping(value="/api/fileupload",method=RequestMethod.POST)
	public String fileUpload(@RequestParam("file") MultipartFile file) {
		String processJson ="{}";
		try {
			processJson = ocrService.processJson(file.getBytes());
			System.out.println(processJson);
			System.out.println("Response ----------------------");
			System.out.println(processJson.trim().replaceAll("\n", ""));
		} catch (Exception e) {
			return "{\"status\":\"Error\"}";
		}
		return processJson;
	}*/
	
	@RequestMapping(value="/api/googlevision/imagetotext",method=RequestMethod.POST)
	public Data imageToText(@RequestParam("file") MultipartFile file) {
		Data processJson = googleService.readText(file, "");	
		return processJson;
	}
	
	@RequestMapping(value="/api/googlevision/print",method=RequestMethod.POST)
	public String printString(@RequestBody String jsonString) {
		System.out.println(jsonString);
		
		if(jsonString.isEmpty()) {
			return  "{\"status\":\"Empty form\"}";
		}
		return "Hello"+jsonString;
	}
	
	
	@RequestMapping(value="/api/simplificato/ocr",method=RequestMethod.POST)
	public String f24imageToText(@RequestParam("file") MultipartFile file) {
		Data data = googleService.readText(file, "");	
//		System.out.println("processed ocr data : "+data);
		String f24Result ="{}";

		try {
			f24Result = ocrService.processJson(data);
		} catch (Exception e) {
			return "{\"status\":\"Error\"}";
		}
		
		return f24Result;
	}	
	
	
	@RequestMapping(value="/api/simplificato/ocr/hello",method=RequestMethod.POST)
	public String f24imageToText(@RequestParam("file") File file) throws IOException {
		Data data = googleService.readText(file, "");	
//		System.out.println("processed ocr data : "+data);
		String f24Result ="{}";

		try {
			f24Result = ocrService.processJson(data);
		} catch (Exception e) {
			return "{\"status\":\"Error\"}";
		}
		
		return f24Result;
	}	
	
	@RequestMapping(value="/api/image/encode",method=RequestMethod.PUT)
	public String f24Encode(@RequestParam("file") MultipartFile file) {
		String encodeBase64String = "";
		try {
			encodeBase64String = Base64.encodeBase64String(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return "{\"encoded_string\":\""+	encodeBase64String+"\"}";
		return "{\"encoded_string\":\""+	encodeBase64String+"\"}";
	}
	
	
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/hello")
	public ResponseEntity<String> callService() {
		
//		https://f24-img-skew.herokuapp.com/f24/api/imageskew
			
			
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
//		HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://f24imagetotext.herokuapp.com/f24/api/googlevision/print", HttpMethod.POST, new HttpEntity<>(headers), String.class);
		
		System.out.println(response);
		return response;
	}
	
	
	
	//new service
	
	
	
	@RequestMapping(value="/api/simplificato/hello",method=RequestMethod.POST)
	public String f24ImageToTextExample(@RequestParam("file") MultipartFile file) {
		
		
		
//		https://f24-img-skew.herokuapp.com/f24/api/imageskew
		
		String encodedImage=f24Encode(file);
		
		System.out.println("encodedImage:\n"+encodedImage);
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(encodedImage,headers);
		//headers.set("encoded_img",encodedImage);
		ResponseEntity<String> response=null;
		
		
		
		try
		{

		restTemplate.exchange(
				"https://f24-img-skew.herokuapp.com/f24/api/imageskew", HttpMethod.POST, entity, String.class);
		}
		catch(Exception e)
		{
			
			System.out.println("hello problem");	
			e.printStackTrace();
		}
		
		byte[] decodeBase64 = Base64.decodeBase64(response.toString()); // TODO CHANGES REQUIRED
		Data data = null;
		String f24Result ="{}";
		try {
			// TODO call skew image service
			data = googleService.readText(decodeBase64,"");
		    f24Result = ocrService.processJson(data);
		} catch (IOException e) {
			return "{\"status\":\"KO\"}";
		} catch (Exception e) {
			return "{\"status\":\"KO\"}";
		}
		
		//String sampleResult = "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";
		
		return f24Result;
	}
	
	

	
}
