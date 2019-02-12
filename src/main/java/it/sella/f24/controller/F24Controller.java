package it.sella.f24.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.F24Form;
import it.sella.f24.bean.F24JSON;
import it.sella.f24.bean.ResBody;
import it.sella.f24.service.F24OCRService;
import it.sella.f24.service.GoogleService;
import it.sella.f24.util.FileReaderfromFolder;
import it.sella.f24.util.LoadPropertiesUtil;

@RestController
@RequestMapping("/f24")
public class F24Controller {
	private static Logger logger = null;
	private static Properties props = null;
	static {
		logger = Logger.getLogger(F24Controller.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		props = LoadPropertiesUtil.loadPropertiesFile();
	}

	@Autowired
	private F24OCRService ocrService;

	@Autowired
	private GoogleService googleService;

	@Autowired
	private RestTemplate restTemplate;

//	@Bean
//	public RestTemplate restTemplate() {
//		// Do any additional configuration here
//
//		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//
//		requestFactory.setProxy(Proxy.NO_PROXY);
//		// // return builder.build();
//		return new RestTemplate(requestFactory);
//
//	}

	/*
	 * main { imagetoJSO() { //Step1: templateandskew(); // ocr //preprocessing
	 * preprocessing - (Generic and spicif )
	 * 
	 * nlp
	 * 
	 * datamappintojson
	 * 
	 * 
	 * } }
	 */

	@RequestMapping(value = "/api/simplificato/form/ocr", method = RequestMethod.POST)
	public String f24ImageToJSON(@RequestBody F24Form f24Form) {

		System.out.println("Calling Authentication Service to get the valid Auth Token");
		// String accessToken = authCheck();

		String accessToken = "123";
		if (accessToken.isEmpty()) {
			return "{\"status\":\"Access token is empty, please provide the correct details\"}";
		} else {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("encodedImage", f24Form.getEncodedImage());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), headers);

			try {
				System.out.println("Calling Service for Template Matching and Skewing");
				ResponseEntity<String> response = restTemplate.exchange(props.getProperty("ServiceURL"),
						HttpMethod.POST, entity, String.class);

				ObjectMapper mapper = new ObjectMapper();
				F24JSON f24json = mapper.readValue(response.getBody(), F24JSON.class);
				// System.out.println("Response from Skew Service:" +
				// f24json.getEncodedImage());
				byte[] decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());

				System.out.println("Calling Google Service for processing of the Image data");
				Data data = googleService.readText(decodeBase64, "");

				System.out.println("Calling OCR Service to preprocess and prepare the JSON");
				String f24Result = ocrService.processJson(data);

				System.out.println("Printing F24 JSON:\n" + f24Result);
				System.out.println("Calling F24 Payment Service to make the Payment");
				// callF24(f24Result);
				return f24Result;

			} catch (IOException e) {
				return "{\"status\":\"KO\"}";
			} catch (Exception e) {
				return "{\"status\":\"KO\"}";
			}

		}

	}
	
	
	@RequestMapping(value = "/api/authcheck", method = RequestMethod.POST)
	public String authCheck(@RequestHeader("apiKey") String apiKey) {

		System.setProperty("java.net.useSystemProxies", "false");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiKey", apiKey);
		headers.set("Auth-Schema", "S2S");
		ObjectMapper mapper = new ObjectMapper();

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = null;
		// https://sandbox.platfr.io/api/public/auth/v2/s2s/producers/gbs/session
		// https://sandbox.platfr.io/api/gbs/banking/v2/accounts/1234/balance

		try {
			System.out.println("Calling service");
			response = restTemplate.exchange("https://sandbox.platfr.io/api/public/auth/v2/s2s/producers/gbs/session",
					HttpMethod.POST, entity, String.class);

			System.out.println("Response Body:" + response.getBody());

			ResBody resBody = mapper.readValue(response.getBody(), ResBody.class);

			System.out.println(resBody.getPayload().getAccessToken());

			// accessCheck(resBody.getPayload().getAccessToken());

			System.out.println("Response codes:" + response.getStatusCodeValue() + " " + response.getStatusCode());

			return resBody.getPayload().getAccessToken();
		} catch (Exception exception) {
			System.out.println("hello");
			exception.printStackTrace();
			return "{\"status\":\"KO\"}";
		}
	}
	
	@RequestMapping(value = "/api/simplificato/form/callf24", method = RequestMethod.POST)
	public String callF24(@RequestHeader("apiKey") String apiKey,@RequestBody String f24JSON) {
		System.out.println("API Key ---"+apiKey);
		// https://sandbox.platfor.io/api/gbs/banking/v4.0/accounts/14537780/payments/f24-simple/orders
		String authToken = authCheck(apiKey);
		String accountID="14537780";
		System.setProperty("java.net.useSystemProxies", "false");
		String URL="https://sandbox.platfr.io/api/gbs/banking/v4.0/accounts/"+accountID+"/payments/f24-simple/orders";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.set("Content-Type", "application/json");

		headers.set("apiKey", apiKey);

		headers.set("Auth-Schema", "S2S-AUTH");
		headers.set("Auth-Token", authToken);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("Input JSON:\n" + f24JSON);
		HttpEntity<String> entity = new HttpEntity<>(f24JSON, headers);
		ResponseEntity<String> response = null;

		try {
			System.out.println("Calling service");
			response = restTemplate.exchange(
					URL,
					HttpMethod.POST, entity, String.class);

			System.out.println("Response Body:" + response.getBody()+response.getStatusCode()+response.getStatusCodeValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"status\":\"KO\"}";
		}
		return response.getBody();
	}

	@RequestMapping(value = "/api/simplificato/form/ocrtest", method = RequestMethod.POST)
	public String f24test(@RequestBody F24Form f24Form) {
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

		// String accessToken = authCheck();

		String accessToken = "123";
		if (accessToken.isEmpty()) {
			return "{\"status\":\"Access token is empty, please provide the correct details\"}";
		} else {
			// for local testing
			ObjectMapper mapper = new ObjectMapper();
			String reqJSON = "{\"encodedImage\":\"" + f24Form.getEncodedImage() + "\"}";

			// testing in cloud

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("encodedImage", f24Form.getEncodedImage());

			System.out.println("Input JSON:" + jsonObject);
			F24JSON f24json = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			// HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(),
			// headers);

			HttpEntity<String> entity = new HttpEntity<>(reqJSON, headers);
			String f24Result = "{}";
			byte[] decodeBase64 = null;
			Data data = null;

			try {
				System.out.println("Calling Skew Service");
				ResponseEntity<String> response = restTemplate.exchange("http://localhost:4000/f24/api/imageskew",
						HttpMethod.POST, entity, String.class);
				// http://localhost:5000/f24/api/imageskew

				f24json = mapper.readValue(response.getBody(), F24JSON.class);
				System.out.println("Response from Skew Service:" + f24json.getEncodedImage());
				decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());

				// decodeBase64 = Base64.decodeBase64(f24Form.getEncodedImage());

				System.out.println("Decoded" + decodeBase64);
				for (int i = 0; i < decodeBase64.length / 4; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("nextline");

				for (int i = (decodeBase64.length / 4) + 1; i < decodeBase64.length / 2; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("nextline");

				for (int i = (decodeBase64.length / 2) + 1; i < decodeBase64.length; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("Calling Google Service");
				data = googleService.readText(decodeBase64, "");
				System.out.println("Calling OCR Service");
				f24Result = ocrService.processJson(data);

				System.out.println("Printing F24 Result");
			} catch (IOException e) {
				System.out.println("HI");
				return "{\"status\":\"KO\"}";
			} catch (Exception e) {
				System.out.println("HI");
				return "{\"status\":\"KO\"}";
			}

			return f24Result;
		}

	}

	@RequestMapping(value = "/api/image/encode", method = RequestMethod.PUT)
	public String f24Encode(@RequestParam("file") MultipartFile file) {
		System.out.println("Hello");
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
		// String f24ImageToText = f24ImageToJSON(f24Form);
		// return "{\"encodedImage\":\"" + encodeBase64String + "\"}";

		String f24ImageToText = f24test(f24Form);
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

		FileReaderfromFolder readerfromFolder = new FileReaderfromFolder();

		List<String> filesfromFolder = readerfromFolder.getFilesfromFolder();

		String encodedImage = "", resencodedImage = "";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = null;
		ResponseEntity<String> response = null;
		File sourceimage = null;
		ObjectMapper mapper = new ObjectMapper();
		F24JSON f24json = null;
		byte[] decodeBase64 = null;
		Data data = null;
		String f24Result = "{}";

		for (String filename : filesfromFolder) {

			sourceimage = new File(filename);
			logger.info("Image path: " + filename);

			encodedImage = f24Encode(sourceimage);
			String reqJSON = "{\"encodedImage\":\"" + encodedImage + "\"}";
			entity = new HttpEntity<String>(reqJSON, headers);

			try {

				response = restTemplate.exchange("http://localhost:4000/f24/api/imageskew", HttpMethod.POST, entity,
						String.class);
				f24json = mapper.readValue(response.getBody(), F24JSON.class);
				resencodedImage = f24json.getEncodedImage();
				decodeBase64 = Base64.decodeBase64(resencodedImage);
				System.out.println("Response Encoded Image:" + resencodedImage);
				System.out.println("Calling Google Service");
				data = googleService.readText(decodeBase64, "");
				System.out.println("Calling OCR Service");
				f24Result = ocrService.processJson(data);

				Thread.sleep(3000);
			} catch (IOException e) {
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

	@RequestMapping(value = "/api/imagetotext", method = RequestMethod.POST)
	public String f24ImagetoText(@RequestParam("file") MultipartFile file) {

		JSONObject jsonObject = new JSONObject();
		try {
		jsonObject.put("encodedImage", Base64.encodeBase64String(file.getBytes()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), headers);
		String imageText = "";

			System.out.println("Calling Service for Template Matching and Skewing");
			ResponseEntity<String> response = restTemplate.exchange(props.getProperty("ServiceURL"), HttpMethod.POST,
					entity, String.class);

			ObjectMapper mapper = new ObjectMapper();
			F24JSON f24json = mapper.readValue(response.getBody(), F24JSON.class);
			// System.out.println("Response from Skew Service:" +
			// f24json.getEncodedImage());
			byte[] decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());

			System.out.println("Calling Google Service for processing of the Image data");
			Data data = googleService.readText(decodeBase64, "");
			imageText = ocrService.getImageText(data);
			return imageText;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"status\":\"KO\"}";
		}

		// String encodeBase64String = "";
		// byte[] decodeBase64 = null;
		// Data data = null;
		// String imageText = "";
		// try {
		//
		// encodeBase64String = Base64.encodeBase64String(file.getBytes());
		// decodeBase64 = Base64.decodeBase64(encodeBase64String);
		// System.out.println("Calling Google Service");
		// data = googleService.readText(decodeBase64, "");
		// imageText = ocrService.getImageText(data);
		// return imageText;
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return "{\"status\":\"KO\"}";
		// }

		// String f24ImageToText = f24ImageToJSON(f24Form);
		// return "{\"encodedImage\":\"" + encodeBase64String + "\"}";

	}

	@RequestMapping(value = "/api/translate", method = RequestMethod.POST)
	public String f24Translate(@RequestBody String rowVal) {

		// https://f24-img-skew.herokuapp.com/f24/api/imageskew

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = null;
		ResponseEntity<String> response = null;

		//
		// String datatoService = "Namespace(alpha=0.0, attn_debug=False,
		// avg_raw_probs=False, batch_size=30, beam_size=5, beta=-0.0,
		// block_ngram_repeat=0, config=None,"
		// + "coverage_penalty='none', data_type='text', dump_beam='',
		// dynamic_dict=False, fast=False, gpu=-1, "
		// + "ignore_when_blocking=[], image_channel_size=3, length_penalty='none',
		// log_file='', log_file_level='0', max_length=100, max_sent_length=None,"
		// + "min_length=0, models=['demo-model_step_100000.pt'], n_best=1,
		// output='pred.txt', replace_unk=True, report_bleu=False, report_rouge=False,"
		// + "sample_rate=16000, save_config=None, share_vocab=False,
		// src='data/src-test.txt', src_dir='', stepwise_penalty=False, tgt=None,"
		// + "verbose=True, window='hamming', window_size=0.02, window_stride=0.01)";

		// Map serviceMap = new HashMap<>();
		// serviceMap.put("opt", datatoService);

		// String row="EL 3918 H 3 3 3 ";
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("row", rowVal);

		entity = new HttpEntity<>(jsonObject, headers);
		System.out.println(entity.getBody());
		System.out.println(entity.getHeaders());
		System.out.println("HI Calling");
		try {

			response = restTemplate.exchange("https://fabrick.sg.gbs.tst/api/fabrick/f24/translate", HttpMethod.POST,
					entity, String.class);
			System.out.println(response.getBody());
			return response.getBody();

		} catch (Exception e) {
			e.printStackTrace();
			return rowVal;
		}

		// String sampleResult =
		// "{\"F24Semplificato\":{\"Contribuente\":{\"CodiceFiscale\":\"VTINDR85S13D938T\",\"DatiAnagrafici\":{\"Cognome\":\"VITI\",\"Nome\":\"ANDREA\",\"RagioneSociale\":\"\",\"DataDiNascita\":\"13/11/1985\",\"Sesso\":\"M\",\"Comune\":\"GATTINARA\",\"Prov\":\"VC\"},\"DomicilioFiscale\":{\"Comune\":\"\",\"Prov\":\"\",\"ViaeNumeroCivico\":\"\"},\"SecondoCodiceFiscale\":\"\",\"CodiceIdentificativo\":\"\",\"IdentificativoOperazione\":\"\"},\"Taxes\":{\"CodiceUfficio\":\"\",\"CodiceAtto\":\"\",\"Tax\":[{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"1.11\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"ER\",\"CodiceTributo\":\"6099\",\"CodiceEnte\":\"\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"0\",\"MeseRif\":\"0101\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"2.22\",\"CrebitoImporto\":\"\"},{\"Sezione\":\"EL\",\"CodiceTributo\":\"3944\",\"CodiceEnte\":\"D933\",\"Ravv\":\"\",\"ImmVar\":\"\",\"Acc\":\"\",\"Saldo\":\"\",\"NumImm\":\"1\",\"MeseRif\":\"0104\",\"AnnoRif\":\"2018\",\"Detrazione\":\"\",\"DebitoImporto\":\"3.33\",\"CrebitoImporto\":\"\"}]},\"Payment\":{\"DataIncasso\":\"23/07/2018\",\"ContoOrdinante\":\"11O1641490340\",\"SaldoFinale\":\"6.66\",\"Product\":\"0\"}}}";

	}

	@RequestMapping(value = "/api/sample", method = RequestMethod.POST)
	public String f24Sample(@RequestParam("file") MultipartFile file) {

		String encodeBase64String = "";
		byte[] decodeBase64 = null;
		Data data = null;
		String imageText = "";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		try {
			map.add("file", new ByteArrayResource(file.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// JSONObject jsonObject=new JSONObject();
		// jsonObject.put("file", file);

		HttpEntity<?> entity = new HttpEntity(map, headers);
		try {

			restTemplate.exchange("https://f24imagetotext.herokuapp.com/f24/api/imagetotext", HttpMethod.POST, entity,
					String.class);
		} catch (Exception exception) {
			System.out.println("Hello");
			exception.printStackTrace();
		}
		return imageText;

		// String f24ImageToText = f24ImageToJSON(f24Form);
		// return "{\"encodedImage\":\"" + encodeBase64String + "\"}";

	}

	@RequestMapping(value = "/api/image/googletest", method = RequestMethod.PUT)
	public String f24GoogleTest(@RequestParam("file") MultipartFile file) {
		System.out.println("Hello");
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
		// String f24ImageToText = f24ImageToJSON(f24Form);
		// return "{\"encodedImage\":\"" + encodeBase64String + "\"}";

		String accessToken = "123";
		if (accessToken.isEmpty()) {
			return "{\"status\":\"Access token is empty, please provide the correct details\"}";
		} else {
			// for local testing
			ObjectMapper mapper = new ObjectMapper();
			String reqJSON = "{\"encodedImage\":\"" + f24Form.getEncodedImage() + "\"}";

			// testing in cloud

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("encodedImage", f24Form.getEncodedImage());

			System.out.println("Input JSON:" + jsonObject);
			F24JSON f24json = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			// HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(),
			// headers);

			HttpEntity<String> entity = new HttpEntity<>(reqJSON, headers);
			String f24Result = "{}";
			byte[] decodeBase64 = null;
			Data data = null;

			try {
				System.out.println("Calling Skew Service");
				ResponseEntity<String> response = restTemplate.exchange("http://localhost:4000/f24/api/imageskew",
						HttpMethod.POST, entity, String.class);
				// http://localhost:5000/f24/api/imageskew

				f24json = mapper.readValue(response.getBody(), F24JSON.class);
				System.out.println("Response from Skew Service:" + f24json.getEncodedImage());
				decodeBase64 = Base64.decodeBase64(f24json.getEncodedImage());

				// decodeBase64 = Base64.decodeBase64(f24Form.getEncodedImage());

				System.out.println("Decoded" + decodeBase64);
				for (int i = 0; i < decodeBase64.length / 4; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("nextline");

				for (int i = (decodeBase64.length / 4) + 1; i < decodeBase64.length / 2; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("nextline");

				for (int i = (decodeBase64.length / 2) + 1; i < decodeBase64.length; i++) {
					System.out.print(decodeBase64[i]);
				}

				System.out.println("Calling Google Service");
				data = googleService.readText(decodeBase64, "");
				System.out.println("Calling OCR Service");
				f24Result = ocrService.getGoogleText(data);

			} catch (Exception e) {
				e.printStackTrace();
				return "Hi";
			}

			return "Hello";
		}

	}

}
