package it.sella.f24.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Service;

import it.sella.f24.service.opennlp.Data;
import it.sella.f24.service.opennlp.DataDescription;
import it.sella.f24.service.opennlp.TextAnnotation;
import it.sella.f24.testclasses.Result;
import opennlp.tools.namefind.NameFinderMETest4;

@Service
public class F24OCRService {

	private static Logger logger = null;
	static {
		logger = Logger.getLogger(F24OCRService.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}

	public F24Format processJson(Data data) throws Exception {

		try {
			// JSONParser parser = new JSONParser();
			// Object obj = parser.parse(new FileReader("D:\\Neeraja\\ocr\\json\\1.json"));
			// JSONObject jsonObject = (JSONObject) obj;

			List<DataDescription> list = new ArrayList<>();
			int keycount = 0;
			for (TextAnnotation txtAnn : data.getTextAnnotation()) {

				if (txtAnn.getDescription().equalsIgnoreCase("FISCALE")
						|| txtAnn.getDescription().equalsIgnoreCase("ANAGRAFICI")
						|| txtAnn.getDescription().equalsIgnoreCase("IDENTIFICATIVO")
						|| txtAnn.getDescription().equalsIgnoreCase("EURO")) {
					keycount++;
				}
			}
			if (keycount <= 2) {
				// logger.info("{\"status\":\"This is not a F24 Image, please provide a valid
				// F24 image\"}");
				// return "{\"status\":\"This is not a F24 Image, please provide a valid F24
				// image\"}";
			}
			int spacecount = countSpace(data);
			System.out.println(spacecount + "space");
			// logger.info("Space count" + spacecount);
			list = process(data);

			/*
			 * try { Collections.sort(list); } catch (IllegalArgumentException e) {
			 * e.printStackTrace(); System.out.
			 * println("{\"status\":\"Invalid input data, please try to capture one more time!!!\"}"
			 * ); //logger.
			 * info("{\"status\":\"Invalid input data, please try to capture one more time!!!\"}"
			 * ); return
			 * "{\"status\":\"Invalid input data, please try to capture one more time!!!\"}"
			 * ; }
			 */

			// FetchData f = new FetchData();
			// f.getData(list);
			boolean first = true;
			int prev = 0;
			StringBuffer section1 = new StringBuffer();
			StringBuffer section2 = new StringBuffer();

			List<DataDescription> sec1list = new ArrayList<>();
			List<DataDescription> sec2list = new ArrayList<>();

			int s1 = 0, s2 = 0;
			int xprev = 0, yprev = 0, ydiff = 0, xprevEnd = 0,yprevEnd=0;
			boolean pr = false;
			for (DataDescription dat : list) {
				if (dat.getDescription().startsWith("777")
						|| dat.getDescription().startsWith("177") && dat.getDescription().endsWith("7")) {
					continue;
				}
				if (dat.getDescription().equals("CODICE")) {
					s1 = 1;
				} else if (dat.getSection().equals("two")) {
					s1 = 0;
					s2 = 1;
				}

				if (s1 == 1) {
					if (dat.getxStart() < xprev) {
						section1.append(" \\n ");
					} else if ((dat.getxStart() - xprevEnd) > 200) {
						section1.append(" ** ");
					} else {
						section1.append(" ");
					}
					section1.append(dat.getDescription());
					sec1list.add(dat);
					xprev = dat.getxStart();
				} else if (s2 == 1) {
					if (dat.getxStart() < xprev) {
						section2.append(" \\n ");
					} else {
						section2.append(" ");
					}
					section2.append(dat.getDescription());
					sec2list.add(dat);
					xprev = dat.getxStart();
				}
				xprevEnd = dat.getxEnd();
			}
			/*
			 * String sec1 = section1.toString(); String sec2 = section2.toString();
			 */

			String sec1 = "";
			String sec2 = "";

			Collections.sort(sec1list);
//			System.out.println(sec1list);

			xprevEnd = 0;
			xprev = 0;
			
			System.out.println("preparing Sec1 list");

			for (int i = 0; i < sec1list.size(); i++) {
				if ((sec1list.get(i).getxStart() - xprevEnd) > 200) {
					sec1 = sec1 + "**" + " ";
				}
				sec1 = sec1 + sec1list.get(i).getDescription() + " ";
				xprevEnd = sec1list.get(i).getxEnd();
				xprev = sec1list.get(i).getxStart();
			}

			yprev=0;
			yprevEnd=0;
			System.out.println("Sorting sec2");
//			Collections.sort(sec2list);
			
			System.out.println("preparing Sec2 list");
			
			for (int i = 0; i < sec2list.size(); i++) {
				if ((sec2list.get(i).getyStart() - yprev) > 15) {
					sec2 = sec2 + "####" + " ";
				}
				sec2 = sec2 + sec2list.get(i).getDescription() + " ";
				yprev=sec2list.get(i).getyStart();
				yprevEnd=sec2list.get(i).getyEnd();
				
			}
			
			
			sec2=searchKeyword(sec2);
			System.out.println("sec2 list done");

			sec2 = sec2.replace(" , ", "*");
			sec2 = sec2.replace(" . ", " ");
			sec2 = sec2.replace(" . ", " ");
			sec1 = sec1.replace(".", "");
			sec1 = sec1.replace("-", "");
			sec1 = sec1.replace("(", "");
			sec1 = sec1.replace(")", "");
			sec1 = sec1.replace(" MF ", "");
			sec1 = sec1.replace(" MOF ", "");
			sec1 = sec1.replace(" FO", " F ");

			sec1 = sec1.replace(":", "");
			sec2 = sec2.replace(":", "");
			sec1 = sec1.replace("|", "");
			sec2 = sec2.replace("|", "");
			sec2 = sec2.replace(" 00", "*00");

			sec2 = sec2.replace("E L", "EL");
			sec2 = sec2.replace("E R", "ER");
			sec2 = sec2.replace("ELR", "ER");
			sec2 = sec2.replace("EL1", "EL ");
			sec2 = sec2.replace("ELI", "EL ");
			sec2 = sec2.replace("ER1", "ER ");
			sec2 = sec2.replace("ERI", "ER ");
			sec2 = sec2.replace("EIL", "EL ");
			sec2 = sec2.replace("E1L", "EL ");
			sec2 = sec2.replace("EIR", "ER ");
			sec2 = sec2.replace("E1R", "ER ");
			// MOF Replace
			// FO Replace

			System.out.println("Section1:----\n" + sec1.trim());
			System.out.println("Section2:----\n" + sec2.trim());

			// logger.info("After the data conversion");
//			logger.info(/* "Section1:----\n" + */ sec1.trim());
			logger.info(sec2.trim());

			List<Result> seconelist = new ArrayList<>();
			List<Result> sectwolist = new ArrayList<>();

			
			String testsec2="EL  3944 H 5 3 3 0303 2018 43*00 ******* ER 3918 D 6 0 0 2 0202 2018 43*00       IIIIII SALDO FINAL EURO ) 86*00";
			NameFinderMETest4 test = new NameFinderMETest4();
			seconelist = test.f24_section1(sec1.trim());
			sectwolist = test.f24_section2(testsec2);
			sectwolist = test.f24_section2(sec2.trim());

			System.out.println("Section1:  " + seconelist);
			System.out.println("Section2:  " + sectwolist);

			// logger.info("Result data");
			// logger.info("Section1: \n" + seconelist);
			// logger.info("Section2: \n" + sectwolist);

			seconelist.addAll(sectwolist);
			// System.out.println(seconelist);
			// logger.info("Sending the data to prepare Json");
			return prepareJSON(seconelist);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}

	private int countSpace(Data data) {
		int spacecount = 0;

		for (TextAnnotation txtAnn : data.getTextAnnotation()) {

			if (txtAnn.getLocale() != null && txtAnn.getLocale() != "") {
				for (int i = txtAnn.getDescription().indexOf("UNIFICATO"); i < txtAnn.getDescription()
						.indexOf("EURO"); i++) {
					if (txtAnn.getDescription().charAt(i) == ' ') {
						spacecount++;
					}
				}
				break;
			}
		}
		return spacecount;
	}

	private String prepareJSON2(List<Result> results) throws FileNotFoundException, IOException {

		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		int rowcount = 0;
		String v1 = "", v2 = "", v3 = "", v4 = "", v5 = "", v6 = "", v7 = "", sz = "", t = "", c = "", m = "", a = "",
				d = "", db = "", cr = "", e = "";
		// fecthing the data from the list
		ListIterator<Result> iterator = (ListIterator<Result>) results.listIterator();
		for (; iterator.hasNext();) {
			Result result = iterator.next();
			if (result.getKey().contains("Fiscale")) {
				if (StringUtils.isAlphanumeric(result.getValue()))
					v1 = v1 + result.getValue();
			}
			if (result.getKey().contains("Anagrafici")) {
				if (StringUtils.isAlpha(result.getValue()))
					v2 = v2 + result.getValue() + " ";
			}

			if (result.getKey().contains("Name")) {
				v3 = v3 + result.getValue() + " ";
			}
			if (result.getKey().contains("DOB")) {
				if (StringUtils.isNumeric(result.getValue()) || StringUtils.isAlpha(result.getValue()))
					v4 = v4 + result.getValue();
			}
			v4 = v4.replaceAll("O", "0");
			if (v4.length() > 8) {
				if (StringUtils.isNumeric(v4.substring(0, 8))) {
					v4 = v4.substring(0, 8);
				} else if (StringUtils.isNumeric(v4.substring(v4.length() - 8, v4.length()))) {
					v4 = v4.substring(v4.length() - 8, v4.length());
				}
			}

			if (result.getKey().contains("Sex")) {
				if (StringUtils.isAlpha(result.getValue()))
					v5 = v5 + result.getValue();
			}
			if (result.getKey().contains("City")) {
				if (StringUtils.isAlpha(result.getValue()))
					v6 = v6 + result.getValue() + " ";
			}
			if (result.getKey().contains("Prov")) {
				if (StringUtils.isAlpha(result.getValue()))
					v7 = v7 + result.getValue();
			}
			if (result.getKey().contains("Sezione")) {
				if (StringUtils.isAlpha(result.getValue()) && (result.getValue().length() == 2))
					sz = sz + result.getValue() + ";";
			}
			if (result.getKey().contains("tributo")) {
				if (StringUtils.isNumeric(result.getValue()) && result.getValue().length() == 4) {
					t = t + result.getValue() + ";";
					// rowcount++;
				}
			}
			if (result.getKey().contains("codice")) {
				if (StringUtils.isAlphanumeric(result.getValue()) && result.getValue().length() == 4)
					c = c + result.getValue() + ";";
			}
			if (result.getKey().contains("mese")) {
				if (StringUtils.isNumeric(result.getValue()))
					m = m + result.getValue() + ";";
			}
			if (result.getKey().contains("anno")) {
				if (StringUtils.isNumeric(result.getValue()))
					a = a + result.getValue() + ";";
			}
			if (result.getKey().contains("detrazione")) {
				if (StringUtils.isAlpha(result.getValue()))
					d = d + result.getValue() + ";";
			}
			if (result.getKey().contains("dobito")) {
				db = db + result.getValue() + ";";
			}
			if (result.getKey().contains("credito")) {
				cr = cr + result.getValue() + ";";
			}

			if (result.getKey().contains("euro")) {
				e = e + result.getValue();
			}
		}

		v2 = v2.trim();
		v3 = v3.trim();
		v4 = v4.replaceAll("[A-Z]", "");
		v6 = v6.trim();
		// Replacing the keywords with empty space in both the sections
		v1 = searchKeyword(v1);
		v2 = searchKeyword(v2);
		v3 = searchKeyword(v3);
		v4 = searchKeyword(v4);
		v5 = searchKeyword(v5);
		v6 = searchKeyword(v6);
		v7 = searchKeyword(v7);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date date;
		try {
			date = new SimpleDateFormat("ddMMyyyy").parse(v4);
			v4 = format.format(date);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "\"{\"status\":\"Date is formatted incorrectly\"}\"";
		}

		System.out.println("Date" + v4);

		sz = searchKeyword(sz);
		t = searchKeyword(t);
		c = searchKeyword(c);
		m = searchKeyword(m);
		a = searchKeyword(a);
		d = searchKeyword(d);
		db = searchKeyword(db);
		cr = searchKeyword(cr);
		e = searchKeyword(e);

		// logger.info("Section1 Result data:\n");
		// logger.info("CodiceFiscale: "+v1+"\n");
		// logger.info("Cognome: "+v2+"\n");
		// logger.info("Nome: "+v3+"\n");
		// logger.info("DataDiNascita: "+v4+"\n");
		// logger.info("Sesso: "+v5+"\n");
		// logger.info("Comune: "+v6+"\n");
		// logger.info("Prov: "+v7+"\n");

		// Replacing * with , in the debit values
		db = db.replace("*", ".");
		e = e.replace("*", ".");
		// replacing the values in Json

		if (v1.length() > 16) {
			String temp = "";
			for (int i = 0; i < 16; i++) {
				temp = temp + v1.charAt(i);
			}
			v1 = temp;
		}

		StringTokenizer sztokenizer = new StringTokenizer(sz, ";");
		StringTokenizer ttokenizer = new StringTokenizer(t, ";");
		StringTokenizer ctokenizer = new StringTokenizer(c, ";");
		rowcount = ttokenizer.countTokens();
		buildf242(rowcount);
		StringTokenizer mtokenizer = new StringTokenizer(m, ";");
		StringTokenizer atokenizer = new StringTokenizer(a, ";");
		StringTokenizer dtokenizer = new StringTokenizer(d, ";");
		StringTokenizer dbtokenizer = new StringTokenizer(db, ";");
		StringTokenizer crtokenizer = new StringTokenizer(cr, ";");

//		 logger.info("Section2 Result data:\n");
//		 logger.info("Seizone:\t"+sz+"\n");
//		 logger.info("tributo:\t"+t+"\n");
//		 logger.info("Codice:\t"+c+"\n");
//		 logger.info("Mese:\t"+m+"\n");
//		 logger.info("Anno:\t"+a+"\n");
//		 logger.info("Dobito:\t"+db+"\n");
//		 logger.info("Euro:\t"+e+"\n");
		

		try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/f24.txt"))) {
			while ((line = br.readLine()) != null) {
				mydata = line;

				mydata = line.replace("v1", v1);
				mydata = mydata.replace("v2", v2);
				mydata = mydata.replace("v3", v3);
				mydata = mydata.replace("v4", v4);
				mydata = mydata.replace("v5", v5);
				mydata = mydata.replace("v6", v6);
				mydata = mydata.replace("v7", v7);

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
					mydata = mydata.replaceAll("x7", "");
				} else if (mydata.contains("x7") && dbtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x7", dbtokenizer.nextToken());
				}

				if (crtokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x8", "");
				} else if (mydata.contains("x8") && crtokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x8", crtokenizer.nextToken());
				}

				if (e.isEmpty()) {
					mydata = mydata.replace("e1", "");
				} else {
					if (e.startsWith("777")) {
						e = e.replaceAll("7", "");
					}
					mydata = mydata.replace("e1", e);
				}

				buffer.append(mydata + "\n");

			}
		} finally {

			// writer.close();
		}
		// System.out.println(buffer.toString());
		// logger.info("F24 JSON:\n" + buffer.toString());

		return buffer.toString();

	}

	private F24Format prepareJSON(List<Result> results) throws FileNotFoundException, IOException {
		
		if(results.isEmpty()) {
			return null;
		}
		String f24format1 = prepareJSON2(results);

		// PrintWriter writer = new
		// PrintWriter("D:\\Neeraja\\ocr\\json\\myjson.json", "UTF-8");
		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		int rowcount = 0;
		String v1 = "", v2 = "", v3 = "", v4 = "", v5 = "", v6 = "", v7 = "", sz = "", t = "", c = "", m = "", a = "",
				d = "", db = "", cr = "", e = "";
		// fecthing the data from the list
		ListIterator<Result> iterator = (ListIterator<Result>) results.listIterator();
		for (; iterator.hasNext();) {
			Result result = iterator.next();
			if (result.getKey().contains("Fiscale")) {
				if (StringUtils.isAlphanumeric(result.getValue()))
					v1 = v1 + result.getValue();
			}
			if (result.getKey().contains("Anagrafici")) {
				if (StringUtils.isAlpha(result.getValue()))
					v2 = v2 + result.getValue() + " ";
			}

			if (result.getKey().contains("Name")) {
				v3 = v3 + result.getValue() + " ";
			}
			if (result.getKey().contains("DOB")) {
				if (StringUtils.isNumeric(result.getValue()) || StringUtils.isAlpha(result.getValue()))
					v4 = v4 + result.getValue();
			}
			v4 = v4.replaceAll("O", "0");
			if (v4.length() > 8) {
				if (StringUtils.isNumeric(v4.substring(0, 8))) {
					v4 = v4.substring(0, 8);
				} else if (StringUtils.isNumeric(v4.substring(v4.length() - 8, v4.length()))) {
					v4 = v4.substring(v4.length() - 8, v4.length());
				}
			}

			if (result.getKey().contains("Sex")) {
				if (StringUtils.isAlpha(result.getValue()))
					v5 = v5 + result.getValue();
			}
			if (result.getKey().contains("City")) {
				if (StringUtils.isAlpha(result.getValue()))
					v6 = v6 + result.getValue() + " ";
			}
			if (result.getKey().contains("Prov")) {
				if (StringUtils.isAlpha(result.getValue()))
					v7 = v7 + result.getValue();
			}
			if (result.getKey().contains("Sezione")) {
				if (StringUtils.isAlpha(result.getValue()) && (result.getValue().length() == 2))
					sz = sz + result.getValue() + ";";
			}
			if (result.getKey().contains("tributo")) {
				if (StringUtils.isNumeric(result.getValue()) && result.getValue().length() == 4) {
					t = t + result.getValue() + ";";
					// rowcount++;
				}
			}
			if (result.getKey().contains("codice")) {
				if (StringUtils.isAlphanumeric(result.getValue()) && result.getValue().length() == 4)
					c = c + result.getValue() + ";";
			}
			if (result.getKey().contains("mese")) {
				if (StringUtils.isNumeric(result.getValue()))
					m = m + result.getValue() + ";";
			}
			if (result.getKey().contains("anno")) {
				if (StringUtils.isNumeric(result.getValue()))
					a = a + result.getValue() + ";";
			}
			if (result.getKey().contains("detrazione")) {
				if (StringUtils.isAlpha(result.getValue()))
					d = d + result.getValue() + ";";
			}
			if (result.getKey().contains("dobito")) {
				db = db + result.getValue() + ";";
			}
			if (result.getKey().contains("credito")) {
				cr = cr + result.getValue() + ";";
			}

			if (result.getKey().contains("euro")) {
				e = e + result.getValue();
			}
		}

		v2 = v2.trim();
		v3 = v3.trim();
		v4 = v4.replaceAll("[A-Z]", "");
		v6 = v6.trim();
		// Replacing the keywords with empty space in both the sections
		v1 = searchKeyword(v1);
		v2 = searchKeyword(v2);
		v3 = searchKeyword(v3);
		v4 = searchKeyword(v4);
		v5 = searchKeyword(v5);
		v6 = searchKeyword(v6);
		v7 = searchKeyword(v7);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date date=null;
		try {
			date = new SimpleDateFormat("ddMMyyyy").parse(v4);
			v4 = format.format(date);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			// return "\"{\"status\":\"Date is formatted incorrectly\"}\"";
		}

		System.out.println("Date" + v4);

		sz = searchKeyword(sz);
		t = searchKeyword(t);
		c = searchKeyword(c);
		m = searchKeyword(m);
		a = searchKeyword(a);
		d = searchKeyword(d);
		db = searchKeyword(db);
		cr = searchKeyword(cr);
		e = searchKeyword(e);

//		logger.info("Section1 Result data:\n");
//		logger.info("CodiceFiscale:	" + v1 + "\n");
//		logger.info("Cognome:	" + v2 + "\n");
//		logger.info("Nome:	" + v3 + "\n");
//		logger.info("DataDiNascita:	" + v4 + "\n");
//		logger.info("Sesso:	" + v5 + "\n");
//		logger.info("Comune:	" + v6 + "\n");
//		logger.info("Prov:	" + v7 + "\n");

		// Replacing * with , in the debit values
		db = db.replace("*", ".");
		e = e.replace("*", ".");
		// replacing the values in Json

		if (v1.length() > 16) {
			String temp = "";
			for (int i = 0; i < 16; i++) {
				temp = temp + v1.charAt(i);
			}
			v1 = temp;
		}

		StringTokenizer sztokenizer = new StringTokenizer(sz, ";");
		StringTokenizer ttokenizer = new StringTokenizer(t, ";");
		StringTokenizer ctokenizer = new StringTokenizer(c, ";");
		rowcount = ttokenizer.countTokens();
		buildf24(rowcount);
		StringTokenizer mtokenizer = new StringTokenizer(m, ";");
		StringTokenizer atokenizer = new StringTokenizer(a, ";");
		StringTokenizer dtokenizer = new StringTokenizer(d, ";");
		StringTokenizer dbtokenizer = new StringTokenizer(db, ";");
		StringTokenizer crtokenizer = new StringTokenizer(cr, ";");

		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/f24testfile.txt"))) {
			while ((line = br.readLine()) != null) {
				mydata = line;
				mydata = line.replace("v1", v1);
				mydata = mydata.replace("v2", v2);
				mydata = mydata.replace("v3", v3);
				mydata = mydata.replace("v4", v4);
				mydata = mydata.replace("v5", v5);
				mydata = mydata.replace("v6", v6);
				mydata = mydata.replace("v7", v7);

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

				if (e.isEmpty()) {
					mydata = mydata.replace("e1", "0");
				} else {
					if (e.startsWith("777")) {
						e = e.replaceAll("7", "");
					}
					mydata = mydata.replace("e1", e);
				}

				buffer.append(mydata + "\n");

			}
		} finally {

			// writer.close();
		}
		// System.out.println(buffer.toString());
		// logger.info("F24 JSON:\n" + buffer.toString());

		F24Format f24Format = new F24Format();
		f24Format.setF24format1(f24format1);
		f24Format.setF24format2(buffer.toString());
		return f24Format;

	}

	private String searchKeyword(String value) {
		String[] keywords = { "CODICE", "FISCALE", "DATI", "ANAGRAFICI", "COPIA", "PER", "IL", "SOGGETTO", "CHE",
				"EFFETTUA", "IL", "VERSAMENTO", "BANCA", "POSTE", "AGENTE", "DELLA", "RISCOSSIONE", "DATA", "ESTREMI",
				"DEL", "DA", "COMPILARE", "CURA", "DI", "SPORTELLO", "CAB", "AZENDA", "AZIEN", "MOMOA", "SPORO" ,"COMPILARE","CURA","DI","W TASTE"};
		for (int i = 0; i < keywords.length; i++) {
			value = value.replace(keywords[i], "");
		}
		return value;
	}
	

	public void buildf242(int rowcount) {
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

		try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/testf24.txt"))) {
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

	public void buildf24(int rowcount) {
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

	private List<DataDescription> process(Data data) {
		boolean diff = true, found = false;
		int diffxStart = 0, diffxEnd = 0, diffxEnd2 = 0, diffyStart = 0, diffyStart2 = 0, diffyEnd = 0, diffyEnd2 = 0,
				difference = 0;
		for (TextAnnotation txtAnn : data.getTextAnnotation()) {
			if (diff) {
				if (txtAnn.getDescription().equalsIgnoreCase("CODICE")) {
					diffxStart = txtAnn.getBoundingPoly().getVertices().get(0).getX();
					diffyStart = txtAnn.getBoundingPoly().getVertices().get(0).getY();
					diffyStart2 = txtAnn.getBoundingPoly().getVertices().get(3).getY();
					found = true;
				} else if (found && txtAnn.getDescription().equalsIgnoreCase("FISCALE")) {
					diffxEnd = txtAnn.getBoundingPoly().getVertices().get(0).getX();
					diffxEnd2 = txtAnn.getBoundingPoly().getVertices().get(1).getX();
					diffyEnd = txtAnn.getBoundingPoly().getVertices().get(0).getY();
					diffyEnd2 = txtAnn.getBoundingPoly().getVertices().get(3).getY();
					if (diffyEnd - diffyStart != 0)
						difference = (diffxEnd - diffxStart) / (diffyEnd - diffyStart);
					else if (diffyEnd2 - diffyStart2 != 0) {
						difference = (diffxEnd2 - diffxStart) / (diffyEnd2 - diffyStart2);
					}

					break;
				}
			}
		}
		List<DataDescription> list = new ArrayList<>();
		boolean first = false;
		int start = 0, end = 0;
		int secTwo = 10000;
		for (TextAnnotation txtAnn : data.getTextAnnotation()) {
			if (txtAnn.getLocale() == null || (txtAnn.getLocale().isEmpty())) {

				if (txtAnn.getDescription().equalsIgnoreCase("Semplificato")
						|| txtAnn.getDescription().equalsIgnoreCase("Surplificato")) {
					end = txtAnn.getBoundingPoly().getVertices().get(1).getX();
				} else if (txtAnn.getDescription().equalsIgnoreCase("MODELLO")) {
					start = txtAnn.getBoundingPoly().getVertices().get(0).getX();
				}

				if (!first && txtAnn.getDescription().equals("CODICE")) {
					first = true;
				} else if (first && txtAnn.getDescription().equals("CODICE")) {
					secTwo = txtAnn.getBoundingPoly().getVertices().get(3).getY() + 10;
				}

				// if (txtAnn.getBoundingPoly().getVertices().get(0).getX() > start
				// && txtAnn.getBoundingPoly().getVertices().get(1).getX() < end) {
				String des = txtAnn.getDescription();
				if (txtAnn.getDescription().equals("*")) {
					des = "X";
				}

				// if (txtAnn.getBoundingPoly().getVertices().get(0).getY() < secTwo) {
				des = des.replaceAll(".*[a-z].*", "");
				// }

				DataDescription d = new DataDescription(des, txtAnn.getBoundingPoly().getVertices().get(0).getX(),
						txtAnn.getBoundingPoly().getVertices().get(0).getY(),
						txtAnn.getBoundingPoly().getVertices().get(1).getX(),
						txtAnn.getBoundingPoly().getVertices().get(3).getY());
				d.setDifference(difference);
				if (txtAnn.getBoundingPoly().getVertices().get(0).getY() > secTwo) {
					// System.out.println(txtAnn.getBoundingPoly().getVertices().get(0).getY()+"---"+secTwo);
					d.setSection("two");
				}

				list.add(d);
			}
		}
		return list;
	}
}