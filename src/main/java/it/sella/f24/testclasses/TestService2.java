package it.sella.f24.testclasses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import opennlp.tools.namefind.NameFinderMETest;

public class TestService2 {
	public static void main(String[] args) throws Exception {
		TestService2 service = new TestService2();
		service.processTokens();
	}

	private void processTokens() throws Exception {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("D:\\Neeraja\\ocr\\json\\2.json"));
			JSONObject jsonObject = (JSONObject) obj;
			// System.out.println(jsonObject);
			String jsonString = jsonObject.toString();
			// jsonString = StringUtils.normalizeSpace(jsonString);

			String secone = null, sectwo = null;
			StringBuffer seconedata = new StringBuffer();
			StringBuffer sectwodata = new StringBuffer();

			List<String> seconelist = new ArrayList<>();
			List<String> sectwolist = new ArrayList<>();
			int spacecount = 0;

			for (int i = jsonString.indexOf("UNIFICATO"); i < jsonString.indexOf("IDENTIFICATIVO"); i++) {
				if (secone == null) {
					secone = "" + jsonString.charAt(i);
					if (jsonString.charAt(i) == ' ') {
						spacecount++;
					}
				} else {
					secone = secone + jsonString.charAt(i);
					if (jsonString.charAt(i) == ' ') {
						spacecount++;
					}
				}

			}
			secone = secone.replaceAll("\\\\", "#");
			secone = secone.replaceAll("#n", ":");
			// System.out.println(secone);
			StringTokenizer tokenizer = new StringTokenizer(secone, ":");
			while (tokenizer.hasMoreTokens()) {
				String temp = tokenizer.nextToken();
				temp = temp.replaceAll("\\s", "#");
				seconelist.add(temp);
				seconedata.append(" " + temp);
			}
			System.out.println("Section1:-----");
			System.out.println(seconedata);

			for (int i = jsonString.indexOf("OPERAZIONE"); i < jsonString.indexOf("EURO"); i++) {
				if (sectwo == null) {
					sectwo = "" + jsonString.charAt(i);
					if (jsonString.charAt(i) == ' ') {
						spacecount++;
					}
				} else {
					sectwo = sectwo + jsonString.charAt(i);
					if (jsonString.charAt(i) == ' ') {
						spacecount++;
					}
				}

			}
			System.out.println("Space count: " + (spacecount));
			sectwo = sectwo.replaceAll("\\\\", "#");
			sectwo = sectwo.replaceAll("#n", ":");
			StringTokenizer tokenizer2 = new StringTokenizer(sectwo, ":");
			while (tokenizer2.hasMoreTokens()) {
				String temp = tokenizer2.nextToken();
				temp = temp.replaceAll("\\s", "#");

				sectwolist.add(temp);
				sectwodata.append(" " + temp);

			}

			System.out.println(sectwolist);
			System.out.println("Section2:-----");
			System.out.println(sectwodata);
			/*Map<String, String> seconemap = new HashMap<>();
			Map<String, String> sectwomap = new HashMap<>();
			NameFinderMETest test = new NameFinderMETest();
			seconemap = test.f24_section1(seconedata.toString().trim(), spacecount);
			sectwomap = test.f24_section2(sectwodata.toString().trim(), spacecount);
			// System.out.println("Section1: " + seconemap);
			// System.out.println("Section2: " + sectwomap);

			seconemap.putAll(sectwomap);
			System.out.println(seconemap);

			Set mapset = seconemap.entrySet();
			Iterator iterator = mapset.iterator();
			for (; iterator.hasNext();) {
				Entry entry = (Entry) iterator.next();
				// System.out.println(entry.getKey()+" "+entry.getValue());
				String temp = (String) entry.getValue();

				seconemap.replace((String) entry.getKey(), temp.replace("#", ""));
			}*/

//			prepareJSON(seconemap);

//			System.out.println(seconemap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

	private void prepareJSON(Map<String, String> secmap) throws FileNotFoundException, IOException {

		// PrintWriter writer = new
		// PrintWriter("D:\\Neeraja\\ocr\\json\\myjson.json", "UTF-8");
		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/f24.json"))) {
			while ((line = br.readLine()) != null) {
				mydata = line;
				String value = secmap.get("Fiscale");
				if (value.contains("FISCALE")) {
					String temp = "";
					for (int i = value.indexOf("FISCALE") + 7; i < value.length(); i++) {
						temp = temp + value.charAt(i);
					}
					mydata = line.replace("v1", temp);
				} else {
					mydata = line.replace("v1", secmap.get("Fiscale"));
				}

				value = secmap.get("Anagrafici");
				if (value.contains("ANAGRAFICI")) {
					String temp = "";
					for (int i = value.indexOf("ANAGRAFICI") + 10; i < value.length(); i++) {
						temp = temp + value.charAt(i);
					}
					mydata = mydata.replace("v2", temp);
				} else {
					mydata = mydata.replace("v2", secmap.get("Anagrafici"));
				}

				if (secmap.containsKey("Name"))
					mydata = mydata.replace("v3", secmap.get("Name"));

				if (secmap.containsKey("DOB_and_Sex")) {
					value = secmap.get("DOB_and_Sex");
					System.out.println(value);
					if(StringUtils.isNumeric(value)){
						mydata = mydata.replace("v4", value);
					}else if(StringUtils.isAlphanumeric(value)){
						int p = 0;
						if (value.contains("M")) {
							p = value.indexOf("M");
						} else if (value.contains("F")){
							p = value.indexOf("F");
						}
						String dob = value.substring(0, p - 1);
						Character sex = value.charAt(p);
						
						mydata = mydata.replace("v4", dob);
						mydata = mydata.replace("v5", sex.toString());
						
					}else if(StringUtils.isAlpha(value)){
						mydata = mydata.replace("v5", value);
					}else{
						mydata = mydata.replace("v4", value);
					}
				}
				if (secmap.containsKey("City"))
					mydata = mydata.replace("v6", secmap.get("City"));
				if (secmap.containsKey("Prov"))
					mydata = mydata.replace("v7", secmap.get("Prov"));

				buffer.append(mydata+"\n");
				System.out.println(mydata+"\n");
			}
		} finally {

			// writer.close();
		}
		System.out.println(buffer.toString());
		// return buffer.toString();

	}
}
