package it.sella.f24.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;

import it.sella.f24.service.F24Format;
import it.sella.f24.service.F24OCRService;
import it.sella.f24.service.GoogleService;
import it.sella.f24.service.opennlp.Data;
import it.sella.f24.service.opennlp.F24JSON;
import it.sella.f24.service.opennlp.ResBody;
import it.sella.f24.testclasses.FileReaderfromFolder;
import it.sella.f24.view.F24Form;

@RestController
@RequestMapping("/f24")
public class F24Controller {
	private static Logger logger = null;
	static {
		logger = Logger.getLogger(F24Controller.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}

	@Autowired
	private F24OCRService ocrService;

	@Autowired
	private GoogleService googleService;

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate restTemplate() {
		// Do any additional configuration here

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		requestFactory.setProxy(Proxy.NO_PROXY);
		// return builder.build();
		return new RestTemplate(requestFactory);

	}

	@RequestMapping(value = "/api/simplificato/form/ocr", method = RequestMethod.POST)
	public String f24ImageToText(@RequestBody F24Form f24Form) {
		// System.out.println(f24Form);

		// https://f24imageskew.herokuapp.com/f24/api/imageskew

		/*
		 * Request format to Skew Service
		 * 
		 * { "encodedImage":"{{encoded_image}}"
		 * 
		 * }
		 */

		// Calling Authentication Service

//		String accessToken = authCheck();
		
		String accessToken = "123";
		if (accessToken.isEmpty()) {
			return "{\"status\":\"Access token is empty, please provide the correct details\"}";
		} else {
			// for local testing
			ObjectMapper mapper = new ObjectMapper();
			F24Format format = null;
			String reqJSON = "{\"encodedImage\":\"" + f24Form.getEncodedImage() + "\"}";

			// testing in cloud

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("encodedImage", f24Form.getEncodedImage());

			System.out.println("Input JSON:" + jsonObject);
			F24JSON f24json = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), headers);

//			HttpEntity<String> entity = new HttpEntity<>(reqJSON, headers);
			String f24Result = "{}";
			byte[] decodeBase64 = null;
			Data data = null;

			try {
				System.out.println("Calling Skew Service");
				ResponseEntity<String> response = restTemplate.exchange(
						"https://f24imageskew.herokuapp.com/f24/api/imageskew", HttpMethod.POST, entity, String.class);

				f24json = mapper.readValue(response.getBody(), F24JSON.class);
				System.out.println("Response from Skew Service:" + f24json.getEncodedImage());
				decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());

				// decodeBase64 = Base64.decodeBase64(f24Form.getEncodedImage());
				
				System.out.println("Decoded" + decodeBase64);
				for (int i = 0; i < decodeBase64.length/4; i++) {
					System.out.print(decodeBase64[i]);
				}
				
				System.out.println("nextline");

				for (int i = (decodeBase64.length/4)+1; i < decodeBase64.length/2; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("nextline");

				for (int i = (decodeBase64.length/2)+1; i < decodeBase64.length; i++) {
					System.out.print(decodeBase64[i]);
				}
				
				System.out.println("Calling Google Service");
				data = googleService.readText(decodeBase64, "");
				System.out.println("Calling OCR Service");
				// f24Result = ocrService.processJson(data);

				format = ocrService.processJson(data);
				System.out.println("Printing F24 Result");
			} catch (IOException e) {
				return "{\"status\":\"KO\"}";
			} catch (Exception e) {
				return "{\"status\":\"KO\"}";
			}

			System.out.println("F24 JSON from the Service:\n" + format.getF24format1() + "\n" + format.getF24format2());
			String input = format.getF24format2();
//			callF24(input);
			// return f24Result;
			return format.getF24format1();
		}

		// String sampleResult =
		// "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";

	}

	


	@RequestMapping(value = "/api/image/encode", method = RequestMethod.PUT)
	public String f24Encode(@RequestParam("file") MultipartFile file) {
		String encodeBase64String = "";
		try {

			encodeBase64String = Base64.encodeBase64String(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		F24Form f24Form = new F24Form();
		f24Form.setEncodedImage(encodeBase64String);
		f24Form.setTransactionId("123");
		String f24ImageToText = f24ImageToText(f24Form);
		// return "{\"encodedImage\":\"" + encodeBase64String + "\"}";
		return f24ImageToText;
	}

	@RequestMapping(value = "/api/imageencode", method = RequestMethod.PUT)
	public String f24Encode(@RequestParam("file") File file) {
		String encodeBase64String = "";
		try {

			byte[] bytesArray = new byte[(int) file.length()];

			FileInputStream fis = new FileInputStream(file);
			fis.read(bytesArray); // read file into bytes[]
			fis.close();
			encodeBase64String = Base64.encodeBase64String(bytesArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodeBase64String;
	}


	@RequestMapping(value = "/api/simplificato/localtest", method = RequestMethod.POST)
	public String f24localtest() {
		
		  
		  // https://f24-img-skew.herokuapp.com/f24/api/imageskew
		  
		  FileReaderfromFolder readerfromFolder =new FileReaderfromFolder();
		  
		  List<String> filesfromFolder = readerfromFolder.getFilesfromFolder();
		  
		  String encodedImage="",resencodedImage=""; 
		  HttpHeaders headers = new
		  HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
		  HttpEntity<String> entity=null; 
		  ResponseEntity<String> response = null; 
		  File sourceimage = null; 
		  ObjectMapper mapper = new ObjectMapper(); 
		  F24JSON f24json=null;
		  byte[] decodeBase64 =null; 
		  Data data = null; 
		  String f24Result ="{}";
		  F24Format f24Format=null;
		  
		  for (String filename : filesfromFolder) {
		  
		  sourceimage=new File(filename); 
//		  logger.info("Image path:  "+filename);
		  
		  encodedImage = f24Encode(sourceimage);
		  String reqJSON = "{\"encodedImage\":\"" + encodedImage + "\"}";
		  entity = new HttpEntity<String>(reqJSON, headers);
		  
		  try {
		  
			  response = restTemplate.exchange("https://f24imageskew.herokuapp.com/f24/api/imageskew",HttpMethod.POST, entity, String.class);
			  f24json=mapper.readValue(response.getBody(), F24JSON.class);
			  resencodedImage=f24json.getEncodedImage(); 
			  decodeBase64=Base64.decodeBase64(resencodedImage);
			  System.out.println("Response Encoded Image:"+resencodedImage);
			  System.out.println("Calling Google Service"); 
			  data =googleService.readText(decodeBase64,"");
			  System.out.println("Calling OCR Service"); 
			  f24Format =ocrService.processJson(data);
			  
			  
		  Thread.sleep(3000);
		  }catch (IOException e) {
			  e.printStackTrace(); 
			  return "{\"status\":\"KO\"}"; 
			  }
		  catch (Exception e) {
			  e.printStackTrace(); 
			  return "{\"status\":\"KO\"}"; } }
		  
		  // String sampleResult = 
		  // "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";
		  
		  return f24Result;
		 }

	

	

}
