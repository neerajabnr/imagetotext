package it.sella.f24.testclasses;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.security.auth.message.callback.PrivateKeyCallback.IssuerSerialNumRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import opennlp.tools.namefind.NameFinderMETest;

public class TestService {
	public static void main(String[] args) throws Exception {
		TestService service = new TestService();
		service.processTokens();
	}

	private void processTokens() throws Exception {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("D:\\Neeraja\\ocr\\json\\testimagejson2.json"));
			JSONObject jsonObject = (JSONObject) obj;
//			System.out.println(jsonObject);
			String jsonString = jsonObject.toString();
//			jsonString = StringUtils.normalizeSpace(jsonString);
			
			String secone = null,sectwo = null;
			StringBuffer seconedata=new StringBuffer();
			StringBuffer sectwodata=new StringBuffer();

			for (int i = jsonString.indexOf("UNIFICATO"); i < jsonString.indexOf("IDENTIFICATIVO"); i++) {
				if (secone == null) {
					secone = "" + jsonString.charAt(i);
				} else {
					secone = secone + jsonString.charAt(i);
//					System.out.println(secone);
				}

			}
			secone = secone.replaceAll("\\\\", "#");
			secone = secone.replaceAll("#n", ":");
			List<String> seconelist = new ArrayList<>();
			StringTokenizer tokenizer = new StringTokenizer(secone, ":");
			System.out.println("Section1:-----");
			while (tokenizer.hasMoreTokens()) {
				String temp = tokenizer.nextToken();
				
				if(StringUtils.isAlpha(temp)||StringUtils.isAlphaSpace(temp)){
					seconelist.add(temp);
					seconedata.append(" "+temp);
				}else if (StringUtils.isAlphanumeric(temp)||StringUtils.isAlphanumericSpace(temp)) {
					temp=temp.replaceAll("\\s", "");
					seconelist.add(temp);
					seconedata.append(" "+temp);
				}else{
					seconelist.add(temp);
					seconedata.append(" "+temp);
				}
			}
			System.out.println(seconedata);
			
			for (int i = jsonString.indexOf("OPERAZIONE"); i < jsonString.indexOf("EURO"); i++) {
				if (sectwo == null) {
					sectwo = "" + jsonString.charAt(i);
				} else {
					sectwo = sectwo + jsonString.charAt(i);
				}

			}
			
			sectwo = sectwo.replaceAll("\\\\", "#");
			sectwo = sectwo.replaceAll("#n", ":");
			List<String> sectwolist = new ArrayList<>();
			StringTokenizer tokenizer2 = new StringTokenizer(sectwo, ":");
			System.out.println("Section2:-----");
			while (tokenizer2.hasMoreTokens()) {
				String temp = tokenizer2.nextToken();
//				temp=temp.replaceAll("\\s", "");
				if(StringUtils.isAlpha(temp)||StringUtils.isAlphaSpace(temp)){
					sectwolist.add(temp);
					sectwodata.append(" "+temp);
				}else if(StringUtils.isNumeric(temp)||StringUtils.isNumericSpace(temp)){
					temp=temp.replaceAll("\\s", "");
					if(temp.length()>4){
						String temp1=temp.substring(0, 4);
						String temp2=temp.substring(4, temp.length());
						sectwolist.add(temp1);
						sectwodata.append(" "+temp1);
						sectwolist.add(temp2);
						sectwodata.append(" "+temp2);
					}else {
						sectwolist.add(temp);
						sectwodata.append(" "+temp);
					}
				}else if(StringUtils.isAlphanumeric(temp)||StringUtils.isAlphanumericSpace(temp)){
					temp=temp.replaceAll("\\s", "");
					if(temp.length()>4){
						String temp1=temp.substring(0, 4);
						String temp2=temp.substring(4, temp.length());
						sectwolist.add(temp1);
						sectwodata.append(" "+temp1);
						sectwolist.add(temp2);
						sectwodata.append(" "+temp2);
					}
					else {
						sectwolist.add(temp);
						sectwodata.append(" "+temp);
					}
				}else{
					sectwolist.add(temp);
					sectwodata.append(" "+temp);
				}
			}
			
//		System.out.println(sectwolist);
		System.out.println(sectwodata);
		Map<String, String> seconemap=new HashMap<>();
		Map<String, String> sectwomap=new HashMap<>();
		NameFinderMETest test=new NameFinderMETest();
//		seconemap=test.f24_section1(seconedata.toString());
//		sectwomap=test.f24_section2(sectwodata.toString());
//		System.out.println("Section1:  "+seconemap);
//		System.out.println("Section2:  "+sectwomap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}
}
