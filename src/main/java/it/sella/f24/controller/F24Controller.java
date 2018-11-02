package it.sella.f24.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import com.google.protobuf.ByteString;

import it.sella.f24.service.F24OCRService;
import it.sella.f24.service.GoogleService;
import it.sella.f24.service.opennlp.Data;
import it.sella.f24.service.opennlp.F24JSON;
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
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	   // Do any additional configuration here
	   return builder.build();
	}
	


	@RequestMapping(value = "/api/simplificato/form/ocr", method = RequestMethod.POST)
	public String f24ImageToText(@RequestBody F24Form f24Form) {
		System.out.println(f24Form);

		// https://f24imageskew.herokuapp.com/f24/api/imageskew

		
		/*Request format to Skew Service
		 * 
		 {
			"encoded_img":"{{encoded_image}}"
			
		}*/
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectMapper mapper = new ObjectMapper();
		F24JSON f24json=null;
		HttpEntity<String> entity = new HttpEntity<>(f24Form.getEncodedImage(), headers);
		String f24Result = "{}";
		byte[] decodeBase64=null;
		Data data = null;

		try {
		ResponseEntity<String> response = restTemplate.exchange("https://f24imageskew.herokuapp.com/f24/api/imageskew",
				HttpMethod.POST, entity, String.class);
		
		f24json=mapper.readValue(response.getBody(), F24JSON.class);
		decodeBase64= Base64.decodeBase64(f24json.getEncodedImage());

		
			data = googleService.readText(decodeBase64, "");
			f24Result = ocrService.processJson(data);
		} catch (IOException e) {
			return "{\"status\":\"KO\"}";
		} catch (Exception e) {
			return "{\"status\":\"KO\"}";
		}

		// String sampleResult =
		// "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";

		return f24Result;
	}
	/*
	 * @RequestMapping(value="/api/fileupload",method=RequestMethod.POST) public
	 * String fileUpload(@RequestParam("file") MultipartFile file) { String
	 * processJson ="{}"; try { processJson =
	 * ocrService.processJson(file.getBytes()); System.out.println(processJson);
	 * System.out.println("Response ----------------------");
	 * System.out.println(processJson.trim().replaceAll("\n", "")); } catch
	 * (Exception e) { return "{\"status\":\"Error\"}"; } return processJson; }
	 */

	@RequestMapping(value = "/api/googlevision/imagetotext", method = RequestMethod.POST)
	public Data imageToText(@RequestParam("file") MultipartFile file) {
		Data processJson = googleService.readText(file, "");
		return processJson;
	}

	@RequestMapping(value = "/api/googlevision/print", method = RequestMethod.POST)
	public String printString(@RequestBody String jsonString) {
		System.out.println(jsonString);

		if (jsonString.isEmpty()) {
			return "{\"status\":\"Empty form\"}";
		}
		return "Hello" + jsonString;
	}

	@RequestMapping(value = "/api/simplificato/ocr", method = RequestMethod.POST)
	public String f24imageToText(@RequestParam("file") MultipartFile file) {
		Data data = googleService.readText(file, "");
		// System.out.println("processed ocr data : "+data);
		String f24Result = "{}";

		try {
			f24Result = ocrService.processJson(data);
		} catch (Exception e) {
			return "{\"status\":\"Error\"}";
		}

		return f24Result;
	}

	@RequestMapping(value = "/api/simplificato/ocr/hello", method = RequestMethod.POST)
	public String f24imageToText(@RequestParam("file") File file) throws IOException {
		Data data = googleService.readText(file, "");
		// System.out.println("processed ocr data : "+data);
		String f24Result = "{}";

		try {
			f24Result = ocrService.processJson(data);
		} catch (Exception e) {
			return "{\"status\":\"Error\"}";
		}

		return f24Result;
	}

	@RequestMapping(value = "/api/image/encode", method = RequestMethod.PUT)
	public String f24Encode(@RequestParam("file") File file) {
		String encodeBase64String = "";
		try {
			
			byte[] bytesArray = new byte[(int) file.length()]; 

			  FileInputStream fis = new FileInputStream(file);
			  fis.read(bytesArray); //read file into bytes[]
			encodeBase64String = Base64.encodeBase64String(bytesArray);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{\"encoded_img\":\"" + encodeBase64String + "\"}";
	}


	@RequestMapping("/hello")
	public ResponseEntity<String> callService() {

		// https://f24-img-skew.herokuapp.com/f24/api/imageskew

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://f24imagetotext.herokuapp.com/f24/api/googlevision/print", HttpMethod.POST,
				new HttpEntity<>(headers), String.class);

		System.out.println(response);
		return response;
	}

	// new service

	/*@RequestMapping(value = "/api/simplificato/hello", method = RequestMethod.POST)
	public String f24ImageToTextExample(@RequestParam("file") MultipartFile file) {

		// https://f24-img-skew.herokuapp.com/f24/api/imageskew
		String encodedImage = f24Encode(file);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(encodedImage, headers);

		ResponseEntity<String> response = null;
		ObjectMapper mapper = new ObjectMapper();
		String encodedImg = "";
		F24JSON f24json=null;
		
		

		try {

			response = restTemplate.exchange("http://localhost:5000/f24/api/imageskew", HttpMethod.POST, entity,
					String.class);
			f24json=mapper.readValue(response.getBody(), F24JSON.class);
		} catch (Exception e) {
			System.out.println("hello problem");
			e.printStackTrace();
		}
		
		encodedImg=f24json.getEncodedImage();
		System.out.println("response from service:" + response);


		

		byte[] decodeBase64 = Base64.decodeBase64(encodedImg); // TODO CHANGES REQUIRED

		Data data = null;
		String f24Result = "{}";
		try {
			// TODO call skew image service
			ByteArrayInputStream input_stream = new ByteArrayInputStream(decodeBase64);
			BufferedImage final_buffered_image = ImageIO.read(input_stream);

//			System.out.println("input_stream" + final_buffered_image);
//			System.out.println("final_buffered_image" + final_buffered_image);
//			System.out.println("Working Directory = " + System.getProperty("user.dir"));
//			ImageIO.write(final_buffered_image, "jpg", new File("/home/bsindia/Documents/F24_V2/result.jpg"));

			 data = googleService.readText(decodeBase64,"");
			 f24Result = ocrService.processJson(data);
		} catch (IOException e) {
			e.printStackTrace();
			return "{\"status\":\"KO\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"status\":\"KO\"}";
		}

		// String sampleResult =
		// "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";

		return f24Result;
	}*/

	@RequestMapping(value = "/api/check", method = RequestMethod.PUT)
	public String cehck(@RequestParam("file") MultipartFile file) {

		File sourceimage = new File("/home/bsindia/Documents/F24_V2/check.jpg");
		try {
			BufferedImage image = ImageIO.read(sourceimage);
			ImageIO.write(image, "jpg", new File("/home/bsindia/Documents/F24_V2/result.jpg"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	
	
	@RequestMapping(value = "/api/simplificato/localtest", method = RequestMethod.POST)
	public String f24ImageToTextTesting() {

		// https://f24-img-skew.herokuapp.com/f24/api/imageskew
		
		FileReaderfromFolder readerfromFolder =new FileReaderfromFolder();
		
		List<String> filesfromFolder = readerfromFolder.getFilesfromFolder();
		
		String encodedImage="",resencodedImage="";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity=null;
		ResponseEntity<String> response = null;
		File sourceimage = null;
		ObjectMapper mapper = new ObjectMapper();
		F24JSON f24json=null;
		byte[] decodeBase64 =null;
		Data data = null;
		String f24Result = "{}";
		
		for (String filename : filesfromFolder) {
			
			sourceimage=new File(filename);
			logger.info("Image path:  "+filename);
			encodedImage = f24Encode(sourceimage);
			entity = new HttpEntity<String>(encodedImage, headers);
			
			try {

				response = restTemplate.exchange("https://f24imageskew.herokuapp.com/f24/api/imageskew", HttpMethod.POST, entity,
						String.class);
				f24json=mapper.readValue(response.getBody(), F24JSON.class);
				resencodedImage=f24json.getEncodedImage();
				decodeBase64= Base64.decodeBase64(resencodedImage);
				System.out.println("Response Encoded Image:"+resencodedImage);
				System.out.println("Calling Google Service");
				data = googleService.readText(decodeBase64,"");
				System.out.println("Calling OCR Service");
				 f24Result = ocrService.processJson(data);
			
			}catch (IOException e) {
				e.printStackTrace();
				return "{\"status\":\"KO\"}";
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"status\":\"KO\"}";
			}
		}

		// String sampleResult =
		// "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";

		return f24Result;
	}
	
	
	@RequestMapping(value = "/api/authcheck", method = RequestMethod.POST)
	public String authCheck() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiKey", "GYJ22DBXIII0171G9VA1Y9BN3KUOTOSL0");
		headers.set("Auth-Schema", "S2S-AUTH");
		
		HttpEntity<String> entity=null;
		ResponseEntity<String> response = null;

		try {
			System.out.println("Calling service");
//			entity=new HttpEntity<>("GYJ22DBXIII0171G9VA1Y9BN3KUOTOSL0",headers);
			response = restTemplate.exchange("https://sandbox.platfr.io/api/public/auth/v2/s2s/producers/gbs/session", HttpMethod.POST, entity,
					String.class);
			System.out.println(response);
		}catch(Exception exception){
			System.out.println("hello");
			exception.printStackTrace();
		}
		return null;
	}

}
