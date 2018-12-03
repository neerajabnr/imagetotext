package it.sella.f24.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
		String f24Result="",ocrData = "";
		Map<String, String> valuesMap=null;

		try {
			ocrData=getImageText(data);

			valuesMap = preprocessData(ocrData);

			System.out.println("Sending to NLP");

			seconelist = sendToNLP(valuesMap);

			System.out.println("Sending the data to prepare Json");
			
			f24Result = prepareJSON(seconelist);
			
			return f24Result;

		} catch (IOException e1) {
			e1.printStackTrace();
			return "{\"status\":\"KO\"}";
		}

	}

	public String getImageText(Data data) {
		
		int keycount = 0, xprevEnd = 0, xstart = 0;
		String ocrData="";

		String imageRecognitionDoubleCheck = propslist.get("imageRecognitionDoubleCheck");
		
		
		for (TextAnnotation txtAnn : data.getTextAnnotation()) {

			if (txtAnn.getLocale() == null || txtAnn.getLocale().isEmpty()) {
				xstart = txtAnn.getBoundingPoly().getVertices().get(0).getX();
				if ((xstart - xprevEnd) > 250) {
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
		F24Controller controller=new F24Controller();
		List<Result> f24_section1 = null;
		String section1 = "";
		String section2Constants = "";
		String section2Variables= "";
		
		String euro = "";
		
		//Sending to NLP to divide the data
		try {
			f24_section1 = tokenFinder.f24SectionSplit(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Section List:" + f24_section1);
		logger.info("Section Result");

		for (Result result : f24_section1) {

			if (result.getKey().equals("Section1")) {
				section1 = section1 + result.getValue();
			}

			if (result.getKey().equals("Constants")) {
//				String value=controller.f24Translate(result.getValue());
				String value=processRow(result.getValue());
				logger.info("Row from NMT:"+value);
				section2Constants = section2Constants + value+"####";
			}
			
			if (result.getKey().equals("Variables")) {
				section2Variables = section2Variables + result.getValue()+"####";
			}
			
			if (result.getKey().equals("euro")) {
				euro = euro + result.getValue();
			}

			logger.info(result.getKey() + " " + result.getValue());
		}

		
		
		String section1Remove = propslist.get("section1Remove");
		String section2Remove = propslist.get("section2Remove");
//		String section2Replace = propslist.get("section2Replace");
		
		section1 = removeNoise(section1,section1Remove);
		section2Constants = removeNoise(section2Constants,section2Remove);
		section2Variables = removeNoise(section2Variables,section2Remove);
		euro = euro.replaceAll("//s", "");

		System.out.println("Section1:\t" + section1);
		System.out.println("Section2Constants:\t" + section2Constants);
		System.out.println("Section2Variables:\t" + section2Variables);
		System.out.println("Euro:\t" + euro);
		
		logger.info("Section1:\t" + section1);
		logger.info("Section2Constants:\t" + section2Constants);
		logger.info("Section2Variables:\t" + section2Variables);
		logger.info("Euro:\t" + euro);

		Map<String, String> valuesList = new HashMap<>();

		valuesList.put("section1", section1);
		valuesList.put("section2Constants", section2Constants);
		valuesList.put("section2Variables", section2Variables);
		valuesList.put("euro", euro);

		return valuesList;
	}

	private String removeNoise(String value,String replacements) {


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

		StringTokenizer constantData = new StringTokenizer(section2Constants, "####");
		StringTokenizer variableData = new StringTokenizer(section2Variables, "####");

		NameFinderMETokenFinder tokenFinder = new NameFinderMETokenFinder();

		try {
			secOneList = tokenFinder.f24_Section1(section1);
			
			while (constantData.hasMoreElements()) {
				String row = constantData.nextToken();

				row = processRow(row);

				secTwoConstantsList = tokenFinder.f24_Section2_Constants(row.trim());

				logger.info("Row to NLP:" + row.trim());

				if (secTwoConstantsList.size() >= 2) {
					secOneList.addAll(secTwoConstantsList);
				} else {
					logger.info("Error data:" + secTwoConstantsList);
				}

			}

			while (variableData.hasMoreElements()) {
				String row = variableData.nextToken();

				secTwoVariablesList = tokenFinder.f24_Section2_Variables(row.trim());

				logger.info("Row to NLP:" + row.trim());

				if (secTwoVariablesList.size() >= 2) {
					secOneList.addAll(secTwoVariablesList);
				} else {
					logger.info("Error data:" + secTwoVariablesList);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Adding Euro Value to the List
		
		Result euroVal=new Result("euro", euro);
		secOneList.add(euroVal);
		System.out.println("Values List:  " + secOneList);
		
		return secOneList;
	}

	private String processRow(String row) {

		// EL 3944 H 5 3 3 0303 2018 43 , 00
		String pattern = propslist.get("section2Pattern");
		StringBuffer buffer = new StringBuffer();
		StringTokenizer stringTokenizer = new StringTokenizer(pattern, ":");

		String anotherString = "";
		int index = 0;
		for (int i = 0; i < row.length(); i++) {

			Character character = row.charAt(i);
			if (StringUtils.isAlphanumeric(character.toString()) && anotherString.length() < 10) {
				anotherString = anotherString + character;
				index = i;
			}
		}

		anotherString = anotherString.replaceAll("\\s", "");

		buffer.append(anotherString);

		int count = 0, i = 0;
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			buffer.insert(Integer.parseInt(token) + count + i, " ");
			count = count + Integer.parseInt(token);
			i++;
		}

		if (row.charAt(index + 1) == ' ') {
			row = buffer.toString() + row.substring(index + 2, row.length());
		} else {
			row = buffer.toString() + row.substring(index + 1, row.length());
		}

		return row;
	}

	private String prepareJSON(List<Result> results) throws FileNotFoundException, IOException {

		if (results.isEmpty()) {
			return "{\"status\":\"KO\"}";
		}

		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		int rowcount = 0;
		String codiceFiscale = "", cognome = "", nome = "", dob = "", sex = "", city = "", prov = "", seizone = "", tributo = "", codice = "", mese = "", anno = "",
				detrazoine = "", dobito = "", credito = "", euro = "";
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
			if (result.getKey().contains("Sezione")) {
				if (StringUtils.isAlpha(result.getValue()) && (result.getValue().length() == 2))
					seizone = seizone + result.getValue() + ";";
			}
			if (result.getKey().contains("tributo")) {
				if (StringUtils.isNumeric(result.getValue()) && result.getValue().length() == 4) {
					tributo = tributo + result.getValue() + ";";
					// rowcount++;
				}
			}
			if (result.getKey().contains("codice")) {
				if (StringUtils.isAlphanumeric(result.getValue())) {
					if (result.getValue().length() > 4) {
						codice = codice + result.getValue().substring(0, 4) + ";";
					} else {
						codice = codice + result.getValue() + ";";
					}
				}

			}
			if (result.getKey().contains("mese")) {
				if (StringUtils.isNumeric(result.getValue()))
					mese = mese + result.getValue() + ";";
			}
			if (result.getKey().contains("anno")) {
				if (StringUtils.isNumeric(result.getValue()))
					anno = anno + result.getValue() + ";";
			}
			if (result.getKey().contains("detrazione")) {
				if (StringUtils.isAlpha(result.getValue()))
					detrazoine = detrazoine + result.getValue() + ";";
			}
			if (result.getKey().contains("dobito")) {
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
		dob=convertDOB(dob);

		System.out.println("Date" + dob);

		 logger.info("Section1 Result data:\n");
		 logger.info("CodiceFiscale: " + codiceFiscale + "\n");
		 logger.info("Cognome: " + cognome + "\n");
		 logger.info("Nome: " + nome + "\n");
		 logger.info("DataDiNascita: " + dob + "\n");
		 logger.info("Sesso: " + sex + "\n");
		 logger.info("Comune: " + city + "\n");
		 logger.info("Prov: " + prov + "\n");

		// Replacing * with , in the debit values
		dobito = dobito.replace("*", ".");
		euro = euro.replace("*", ".");

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
		 logger.info("Seizone:\t"+seizone+"\n");
		 logger.info("tributo:\t"+tributo+"\n");
		 logger.info("Codice:\t"+codice+"\n");
		 logger.info("Mese:\t"+mese+"\n");
		 logger.info("Anno:\t"+anno+"\n");
		 logger.info("Dobito:\t"+dobito+"\n");
		 logger.info("Euro:\t"+euro+"\n");

		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/f24testfile.txt"))) {
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
				new FileReader("src/main/java/it/sella/f24/service/section2rownew.txt"))) {
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
				new FileReader("src/main/java/it/sella/f24/service/testf24new.txt"))) {
			while ((data = br.readLine()) != null) {
				data = data.replace("section2rows", section2rows);
				buffer.append(data + "\n");
			}
			FileWriter writer = new FileWriter("src/main/java/it/sella/f24/service/f24testfile.txt");
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