package it.sella.f24.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.DataDescription;
import it.sella.f24.bean.Result;
import it.sella.f24.bean.TextAnnotation;
import it.sella.f24.controller.F24Controller;
import it.sella.f24.util.LoadPropertiesUtil;
import opennlp.tools.namefind.NameFinderMESectionTest;
import opennlp.tools.namefind.NameFinderMETokenFinder;

@Service
public class F24OCRService {

	private static Logger logger = null;
	private static Properties props = null;
	private static Map<String, String> propslist = new HashMap<>();
	static {
		logger = Logger.getLogger(F24OCRService.class);
		props = LoadPropertiesUtil.loadPropertiesFile();

		String[] section1data = props.getProperty("section1").split(";");
		String[] section1start = section1data[0].split(":");
		propslist.put("section1start", section1start[1]);
		String[] section1end = section1data[1].split(":");
		propslist.put("section1end", section1end[1]);

		String[] section2data = props.getProperty("section2").split(";");
		String[] section2start = section2data[0].split(":");
		propslist.put("section2start", section2start[1]);
		String[] section2end = section1data[1].split(":");
		propslist.put("section2end", section2end[1]);

		propslist.put("section1Remove", props.getProperty("section1Remove"));

		propslist.put("section2Remove", props.getProperty("section2Remove"));

		propslist.put("section2Replace", props.getProperty("section2Replace"));

		propslist.put("section1Label", props.getProperty("section1Label"));

		propslist.put("imageRecognitionDoubleCheck", props.getProperty("imageRecognitionDoubleCheck"));

		propslist.put("section2Pattern", props.getProperty("section2Pattern"));

		propslist.put("euroRemove", props.getProperty("euroRemove"));

	}

	public String processJson(Data data) throws Exception {

		List<Result> seconelist = new ArrayList<>();
		String f24Result = "", ocrData = "";
		Map<String, String> valuesMap = null;

		try {
			ocrData = getImageText(data);

			valuesMap = preprocessData(ocrData);

			System.out.println("Sending to NLP");

			seconelist = sendToNLP(valuesMap);

			System.out.println("Sending the data to prepare Json");

			f24Result = prepareJSON(seconelist);

			return f24Result;

		} catch (Exception e1) {
			e1.printStackTrace();
			return "{\"status\":\"KO\"}";
		}

	}

	public String getImageText(Data data) {

		int keycount = 0, xprevEnd = 0, xstart = 0;
		String ocrData = "";

		String imageRecognitionDoubleCheck = propslist.get("imageRecognitionDoubleCheck");

		for (TextAnnotation txtAnn : data.getTextAnnotation()) {

			if (txtAnn.getLocale() == null || txtAnn.getLocale().isEmpty()) {
				xstart = txtAnn.getBoundingPoly().getVertices().get(0).getX();
				if ((xstart - xprevEnd) > 800) {
					ocrData = ocrData + "**" + " ";
				}

				String desc = txtAnn.getDescription().replaceAll(".*[a-z].*", "");
				ocrData = ocrData + desc + " ";

				StringTokenizer checkTokenizer = new StringTokenizer(imageRecognitionDoubleCheck, ";");
				while (checkTokenizer.hasMoreTokens()) {
					String token = checkTokenizer.nextToken();
					if (txtAnn.getDescription().contains(token)) {
						keycount++;
					}
				}
				xprevEnd = txtAnn.getBoundingPoly().getVertices().get(1).getX();
			}

		}

		if (keycount <= 2) {
			logger.info("{\"status\":\"This is not a F24 Image, please provide a valid F24 image\"}");
			return "{\"status\":\"This is not a F24 Image, please provide a valid F24 image\"}";
		}

		System.out.println("Data from Google Service :" + ocrData);

		logger.info("Data from Google Service : :" + ocrData);

		return ocrData;
	}

	private Map<String, String> preprocessData(String data) {

		System.out.println("Sending the Data to NLP to divide it into Sections");

		Map<String, String> valuesList = splitSections(data);
		return valuesList;

	}

	private Map<String, String> splitSections(String data) {
		NameFinderMETokenFinder tokenFinder = new NameFinderMETokenFinder();
		List<Result> f24_section1 = null;
		String section1 = "";
		String operazione="";
		String section2Constants = "";
		String section2Variables = "";
		HttpClient httpClient = HttpClientBuilder.create().build();

		String euro = "";

		//String url = "http://localhost:2000/f24/api/translate";//
		String url = "https://fabrick.sg.gbs.tst/api/fabrick/f24/translate";

		// add header
		// post.setHeader("User-Agent", USER_AGENT);

		// Sending to NLP to divide the data
		try {
			f24_section1 = tokenFinder.f24SectionSplit(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Section List:" + f24_section1);
		logger.info("Section Result");

		for (Result result : f24_section1) {

			logger.info(result.getKey() + " " + result.getValue());
			if (result.getKey().equals("Section1")) {
				section1 = section1 + result.getValue();
			}
			
			if (result.getKey().equals("Operazione")) {
				operazione = operazione + result.getValue();
			}

			if (result.getKey().equals("Constants")) {

				try {
					
					String row=checkPattern(result.getValue());
					logger.info("Row:"+row);
					
					int countChar=checkCount(row);
//					if(countChar<=12) {
						
						//Calling OpenNMT service
//						StringBuffer output = new StringBuffer();
//						HttpPost httpPost = new HttpPost(url);
//						httpPost.setHeader("Content-type", "application/json");
//						JSONObject json = new JSONObject();
//						json.put("row", row);  
//						
//						StringEntity stringEntity = new StringEntity(json.toJSONString());
//						httpPost.getRequestLine();
//						httpPost.setEntity(stringEntity);
//						
//						CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);
//						
//						BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//						
//						String line = "";
//						while ((line = rd.readLine()) != null) {
//							output.append(line);
//						}
//						String value = output.toString();
//						logger.info("Row from NMT:" + value);
//						section2Constants = section2Constants + value + "##";
						
//					}else {
//						section2Constants = section2Constants + result.getValue() + "##";
//					}
					 section2Constants = section2Constants + result.getValue() + "##";
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
			if (result.getKey().equals("Variables")) {
				section2Variables = section2Variables + result.getValue() + "##";
			}

			if (result.getKey().equals("euro")) {
				euro = euro + result.getValue();
			}

		}

		String section1Remove = propslist.get("section1Remove");
		String section2Remove = propslist.get("section2Remove");
		String section2Replace = propslist.get("section2Replace");
		String euroRemove = propslist.get("euroRemove");

		section1 =removeNoise(section1, section1Remove);
		section2Constants = removeNoise(section2Constants, section2Replace);
		section2Variables = removeNoise(section2Variables, section2Remove);
//		euro = removeNoise(euro, euroRemove);
		euro = euro.replaceAll("\\s+", "");
		
		

		System.out.println("Section1:\t" + section1);
		System.out.println("Section2Constants:\t" + section2Constants);
		System.out.println("Section2Variables:\t" + section2Variables);
		System.out.println("Euro:\t" + euro);
		System.out.println("Operazione:\t" + operazione);

		logger.info("Section1:\t" + section1);
		logger.info("Section2Constants:\t" + section2Constants);
		logger.info("Section2Variables:\t" + section2Variables);
		logger.info("Euro:\t" + euro);
		logger.info("Operazione:\t" + operazione);


		Map<String, String> valuesList = new HashMap<>();

		valuesList.put("section1", section1);
		valuesList.put("section2Constants", section2Constants);
		valuesList.put("section2Variables", section2Variables);
		valuesList.put("euro", euro);
		valuesList.put("operazione", operazione);

		return valuesList;
	}

	private int checkCount(String row) {
		int count=0;
		for(int i=0;i<row.length();i++) {
			Character character=row.charAt(i);
			if(StringUtils.isAlphanumeric(character.toString())) {
				count++;
			}else {
				continue;
			}
		}
		return count;
	}

	private String checkPattern(String value) {
		value=value.replaceAll("[^a-zA-Z0-9\\s]", "");
		StringBuffer buffer = new StringBuffer(value);
	    
	    Pattern pattern1 = Pattern.compile("[0-9]{5} ");//EL13918 H553,EIL13918 H553
        Pattern pattern2 = Pattern.compile("[A-Z][0-9]{4} ");//EL3918 H553,ELI3918
        
        Matcher matcher1 = pattern1.matcher(value);
        Matcher matcher2 = pattern2.matcher(value);
        
        
        if (matcher1.find()) {
    		System.out.println("matcher1"+pattern1);
    		logger.info("matcher1:"+pattern1);
    		System.out.println("Pattern found from " + matcher1.start() + 
    				" to " + (matcher1.end()-1)); 
    		
    		buffer.insert(matcher1.start()+1, " ");
    	} else if (matcher2.find()) {
    		System.out.println("matcher2"+pattern2);
    		logger.info("matcher2:"+pattern2);
    		System.out.println("Pattern found from " + matcher2.start() + 
    				" to " + (matcher2.end()-1)); 
    		
    		buffer.insert(matcher2.start()+1, " ");
    	}else {//EL 3918 H533
    		logger.info("No match");
    	}
        
        System.out.println(buffer.toString());
		return buffer.toString();
	}

	private String removeNoise(String value, String replacements) {

		StringTokenizer tokenizer = new StringTokenizer(replacements, ";");
		try {
			while (tokenizer.hasMoreElements()) {
				String replaceValue = (String) tokenizer.nextElement();
				String[] keys = replaceValue.split("#");
				if (keys[1] == " ") {
					keys[1] = "";
				}
				if (value.contains(keys[0]))
					value = value.replace(keys[0], keys[1]);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	private List<Result> sendToNLP(Map<String, String> valuesMap) {

		List<Result> secOneList = new ArrayList<>();
		List<Result> secTwoConstantsList = new ArrayList<>();
		List<Result> secTwoVariablesList = new ArrayList<>();

		String section1 = valuesMap.get("section1");
		String section2Constants = valuesMap.get("section2Constants");
		String section2Variables = valuesMap.get("section2Variables");
		String euro = valuesMap.get("euro");
		String operazione=valuesMap.get("operazione");

		StringTokenizer constantData = new StringTokenizer(section2Constants, "##");
		StringTokenizer variableData = new StringTokenizer(section2Variables, "##");

		NameFinderMETokenFinder tokenFinder = new NameFinderMETokenFinder();

		try {
			secOneList = tokenFinder.f24_Section1(section1);

			while (constantData.hasMoreElements()) {
				String row = constantData.nextToken();
				logger.info("After process row");
				if(checkCount(row)>=9) {
					row = processRow(row);
				}
				System.out.println("row"+row);
				secTwoConstantsList = tokenFinder.f24_Section2_Constants(row.trim());

				logger.info("Row to NLP:" + row.trim());

				if (secTwoConstantsList.size() >= 1) {
					secOneList.addAll(secTwoConstantsList);
				} else {
					logger.info("Error data:" + secTwoConstantsList);
				}

			}

			while (variableData.hasMoreElements()) {
				String row = variableData.nextToken();

				secTwoVariablesList = tokenFinder.f24_Section2_Variables(row.trim());

				logger.info("Row to NLP:" + row.trim());

				if (secTwoVariablesList.size() >= 1) {
					secOneList.addAll(secTwoVariablesList);
				} else {
					logger.info("Error data:" + secTwoVariablesList);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Adding Euro Value to the List

		Result euroVal = new Result("euro", euro);
		Result operazioneVal=new Result("operazione", operazione);
		secOneList.add(euroVal);
		secOneList.add(operazioneVal);
		System.out.println("Values List:  " + secOneList);

		return secOneList;
	}

	private String processRow(String row) {

		// EL 3944 H 5 3 3 0303 2018 43 , 00
		String pattern = propslist.get("section2Pattern");
		StringBuffer buffer = new StringBuffer();
		StringTokenizer stringTokenizer = new StringTokenizer(pattern, ":");

		row = row.replaceAll("\\W+", "");
		

		buffer.append(row);

		int count = 0, i = 0;
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			System.out.println(Integer.parseInt(token) + count + i);
			buffer.insert(Integer.parseInt(token) + count + i, " ");
			System.out.println(buffer);
			count = count + Integer.parseInt(token);
			i++;
		}

		return buffer.toString();
	}

	private String prepareJSON(List<Result> results) throws FileNotFoundException, IOException {

		if (results.isEmpty()) {
			return "{\"status\":\"KO\"}";
		}

		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		int rowcount = 0;
		String codiceFiscale = "", cognome = "", nome = "", dob = "", sex = "", city = "", prov = "", operazione = "",seizone = "",
				tributo = "", codice = "", mese = "", anno = "", detrazoine = "", dobito = "", credito = "", euro = "";
		// fecthing the data from the list
		ListIterator<Result> iterator = (ListIterator<Result>) results.listIterator();
		for (; iterator.hasNext();) {
			Result result = iterator.next();
			if (result.getKey().contains("Fiscale")) {
				if (StringUtils.isAlphanumeric(result.getValue()))
					codiceFiscale = codiceFiscale + result.getValue();
			}
			if (result.getKey().contains("Anagrafici")) {
				if (StringUtils.isAlpha(result.getValue()))
					cognome = cognome + result.getValue() + " ";
			}

			if (result.getKey().contains("Name")) {
				nome = nome + result.getValue() + " ";
			}
			if (result.getKey().contains("DOB")) {
				if (StringUtils.isNumeric(result.getValue()) || StringUtils.isAlpha(result.getValue()))
					dob = dob + result.getValue();
			}
			dob = dob.replaceAll("O", "0");
			if (dob.length() > 8) {
				if (StringUtils.isNumeric(dob.substring(0, 8))) {
					dob = dob.substring(0, 8);
				} else if (StringUtils.isNumeric(dob.substring(dob.length() - 8, dob.length()))) {
					dob = dob.substring(dob.length() - 8, dob.length());
				}
			}

			if (result.getKey().contains("Sex")) {
				if (StringUtils.isAlpha(result.getValue()))
					sex = sex + result.getValue();
			}
			if (result.getKey().contains("City")) {
				if (StringUtils.isAlpha(result.getValue()))
					city = city + result.getValue() + " ";
			}
			if (result.getKey().contains("Prov")) {
				if (StringUtils.isAlpha(result.getValue()))
					prov = prov + result.getValue();
			}
			if (result.getKey().contains("operazione")) {
					operazione = operazione + result.getValue() + ";";
			}
			
			if (result.getKey().contains("Sezione")) {
				System.out.println("Sezione"+result.getValue());
				if (StringUtils.isAlpha(result.getValue()) && (result.getValue().length() == 2))
					seizone = seizone + result.getValue() + ";";
				System.out.println("");
			}
			if (result.getKey().contains("tributo")) {
				System.out.println("tributo"+result.getValue());
				if (StringUtils.isNumeric(result.getValue()) && result.getValue().length() == 4) {
					tributo = tributo + result.getValue() + ";";
					// rowcount++;
				}
			}
			if (result.getKey().contains("codice")) {
				System.out.println("codice"+result.getValue());
				if (StringUtils.isAlphanumeric(result.getValue())) {
					if (result.getValue().length() > 4) {
						codice = codice + result.getValue().substring(0, 4) + ";";
					} else {
						codice = codice + result.getValue() + ";";
					}
				}

			}
			
			if (result.getKey().contains("mese")) {
				System.out.println("mese"+result.getValue());
				if (StringUtils.isNumeric(result.getValue()))
					mese = mese + result.getValue() + ";";
			}
			if (result.getKey().contains("anno")) {
				System.out.println("anno"+result.getValue());
				if (StringUtils.isNumeric(result.getValue()))
					anno = anno + result.getValue() + ";";
			}
			if (result.getKey().contains("detrazione")) {
				if (StringUtils.isAlpha(result.getValue()))
					detrazoine = detrazoine + result.getValue() + ";";
			}
			if (result.getKey().contains("dobito")) {
				System.out.println("dobito"+result.getValue());
				dobito = dobito + result.getValue() + ";";
			}
			if (result.getKey().contains("credito")) {
				credito = credito + result.getValue() + ";";
			}

			if (result.getKey().contains("euro")) {
				euro = euro + result.getValue();
			}
		}

		cognome = cognome.trim();
		nome = nome.trim();
		dob = dob.replaceAll("[A-Z]", "");
		city = city.trim();
		// Replacing the keywords with empty space in both the section1

		codiceFiscale = searchKeyword(codiceFiscale);
		cognome = searchKeyword(cognome);
		nome = searchKeyword(nome);
		dob = searchKeyword(dob);
		sex = searchKeyword(sex);
		city = searchKeyword(city);
		prov = searchKeyword(prov);

		if (codiceFiscale.length() > 16) {
			String temp = "";
			for (int i = 0; i < 16; i++) {
				temp = temp + codiceFiscale.charAt(i);
			}
			codiceFiscale = temp;
		}
//		dob = convertDOB(dob);

		System.out.println("Date" + dob);

		logger.info("Section1 Result data:\n");
		logger.info("CodiceFiscale: " + codiceFiscale + "\n");
		logger.info("Cognome: " + cognome + "\n");
		logger.info("Nome: " + nome + "\n");
		logger.info("DataDiNascita: " + dob + "\n");
		logger.info("Sesso: " + sex + "\n");
		logger.info("Comune: " + city + "\n");
		logger.info("Prov: " + prov + "\n");
		logger.info("Operazione:"+ operazione + "\n");

		// Replacing * with , in the debit values
		dobito = dobito.replace("*", ".");
		euro = euro.replace(",", ".");

		StringTokenizer sztokenizer = new StringTokenizer(seizone, ";");
		StringTokenizer ttokenizer = new StringTokenizer(tributo, ";");
		StringTokenizer ctokenizer = new StringTokenizer(codice, ";");
		rowcount = ttokenizer.countTokens();
		buildf24(rowcount);
		StringTokenizer mtokenizer = new StringTokenizer(mese, ";");
		StringTokenizer atokenizer = new StringTokenizer(anno, ";");
		StringTokenizer dtokenizer = new StringTokenizer(detrazoine, ";");
		StringTokenizer dbtokenizer = new StringTokenizer(dobito, ";");
		StringTokenizer crtokenizer = new StringTokenizer(credito, ";");

		logger.info("Section2 Result data:\n");
		logger.info("Seizone:\t" + seizone + "\n");
		logger.info("tributo:\t" + tributo + "\n");
		logger.info("Codice:\t" + codice + "\n");
		logger.info("Mese:\t" + mese + "\n");
		logger.info("Anno:\t" + anno + "\n");
		logger.info("Dobito:\t" + dobito + "\n");
		logger.info("Euro:\t" + euro + "\n");

		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/f24.txt"))) {
			while ((line = br.readLine()) != null) {
				mydata = line;
				mydata = line.replace("v1", codiceFiscale);
				mydata = mydata.replace("v2", cognome);
				mydata = mydata.replace("v3", nome);
				mydata = mydata.replace("v4", dob);
				mydata = mydata.replace("v5", sex);
				mydata = mydata.replace("v6", city);
				mydata = mydata.replace("v7", prov);

				// Calculating Sysdate
				LocalDate currdate = java.time.LocalDate.now();

				mydata = mydata.replace("sysdate", String.valueOf(currdate));

				if (sztokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x1", "");
				} else if (mydata.contains("x1") && sztokenizer.hasMoreTokens()) {
					String stemp = sztokenizer.nextToken();
					if (stemp.length() > 2) {
						stemp = stemp.substring(0, 1);
					}
					mydata = mydata.replaceFirst("x1", stemp);
				}
				if (ttokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x2", "");
				} else if (mydata.contains("x2") && ttokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x2", ttokenizer.nextToken());
				}

				if (ctokenizer.countTokens() == 0) {

					mydata = mydata.replaceAll("x3", "");
				} else if (mydata.contains("x3") && ctokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x3", ctokenizer.nextToken());
				}

				if (mtokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x4", "");
				} else if (mydata.contains("x4") && mtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x4", mtokenizer.nextToken());
				}
				if (atokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x5", "");
				} else if (mydata.contains("x5") && atokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x5", atokenizer.nextToken());
				}
				if (dtokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x6", "");
				} else if (mydata.contains("x6") && dtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x6", dtokenizer.nextToken());
				}
				if (dbtokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x7", "0");
				} else if (mydata.contains("x7") && dbtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x7", dbtokenizer.nextToken());
				}

				if (crtokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x8", "");
				} else if (mydata.contains("x8") && crtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x8", crtokenizer.nextToken());
				}

				if (euro.isEmpty()) {
					mydata = mydata.replace("e1", "0");
				} else {
					mydata = mydata.replace("e1", euro);
				}

				buffer.append(mydata + "\n");

			}
		} finally {

			// writer.close();
		}

		return buffer.toString();

	}

	private String searchKeyword(String value) {

		String section1Label = propslist.get("section1Label");

		StringTokenizer tokenizer = new StringTokenizer(section1Label, ";");

		while (tokenizer.hasMoreTokens()) {
			String keyword = tokenizer.nextToken();
			String[] replaceKeywords = keyword.split("#");

			if (replaceKeywords[1] == " ") {
				replaceKeywords[1] = "";
			}
			if (value.contains(replaceKeywords[0])) {
				value = value.replace(replaceKeywords[0], replaceKeywords[1]);
			}

		}
		return value;
	}

	private String convertDOB(String dob) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date date = null;
		try {
			date = new SimpleDateFormat("ddMMyyyy").parse(dob);
			dob = format.format(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return "\"{\"status\":\"Date is formatted incorrectly\"}\"";
		}
		return dob;
	}

	private void buildf24(int rowcount) {
		String data = "", section2row = "", section2rows = "";
		StringBuffer buffer = new StringBuffer();
		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/section2row.txt"))) {
			while ((data = br.readLine()) != null) {
				section2row = section2row + data + "\n";
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < rowcount; i++) {
			if (i == rowcount - 1) {
				section2rows = section2rows + section2row + "\n";
			} else {
				section2rows = section2rows + section2row + "," + "\n\t";
			}
		}

		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/testf24.txt"))) {
			while ((data = br.readLine()) != null) {
				data = data.replace("section2rows", section2rows);
				buffer.append(data + "\n");
			}
			FileWriter writer = new FileWriter("src/main/java/it/sella/f24/service/f24.txt");
			writer.append(buffer);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}