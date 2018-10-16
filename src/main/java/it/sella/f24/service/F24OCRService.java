package it.sella.f24.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sella.f24.service.opennlp.Data;
import it.sella.f24.service.opennlp.DataDescription;
import it.sella.f24.service.opennlp.TextAnnotation;
import it.sella.f24.testclasses.Result;
import opennlp.tools.namefind.NameFinderMETest3;
import opennlp.tools.namefind.NameFinderMETest4;

@Service
public class F24OCRService {

	public String processJson(Data data) throws Exception {
		
		System.out.println("Data in Service:"+data);
		// read json file data to String
		try {
			// JSONParser parser = new JSONParser();
			// Object obj = parser.parse(new FileReader("D:\\Neeraja\\ocr\\json\\1.json"));
			// JSONObject jsonObject = (JSONObject) obj;

			// jsonData =
			// Files.readAllBytes(Paths.get("D:\\Neeraja\\ocr\\json\\testimagejson2.json"));
			List<DataDescription> list = new ArrayList<>();
			ObjectMapper objectMapper = new ObjectMapper();

			// Data data = objectMapper.readValue(jsondata, Data.class);
			// String jsonString = new String(data);
			int keycount=0;
			for (TextAnnotation txtAnn : data.getTextAnnotation()) {

				if (txtAnn.getDescription().equalsIgnoreCase("FISCALE")
						|| txtAnn.getDescription().equalsIgnoreCase("ANAGRAFICI")
						|| txtAnn.getDescription().equalsIgnoreCase("IDENTIFICATIVO")
						|| txtAnn.getDescription().equalsIgnoreCase("EURO")) {
					keycount++;
				}
			}
			if(keycount<=2) {
				return "{\"status\":\"This is not a F24 Image, please provide a valid F24 image\"}";
			}
			int spacecount = countSpace(data);
			System.out.println(spacecount + "space");
			list = process(data);
			Collections.sort(list);
			System.out.println("After sorting");
			boolean first = true;
			int prev = 0;
			StringBuffer section1 = new StringBuffer();
			StringBuffer section2 = new StringBuffer();
			int s1 = 0, s2 = 0;
			int xprev = 0, yprev = 0, ydiff = 0, xprevEnd = 0;
			boolean pr = false;
			for (DataDescription dat : list) {

				if (dat.getDescription().equals("CODICE")) {
					s1 = 1;
				} else if (dat.getSection().equals("two")) {
					s1 = 0;
					s2 = 1;
				}

				if (s1 == 1) {
					if (dat.getxStart() < xprev) {
						section1.append(" \\n ");
					} else {
						section1.append(" ");
					}
					section1.append(dat.getDescription());
					xprev = dat.getxStart();
				} else if (s2 == 1) {
					if (dat.getxStart() < xprev) {
						section2.append(" \\n ");
					} else {
						section2.append(" ");
					}
					section2.append(dat.getDescription());
					xprev = dat.getxStart();
				}
			}

			String sec1 = section1.toString();
			String sec2 = section2.toString();
			sec2 = sec2.replace(" , ", "*");
			sec2 = sec2.replace(" . ", " ");
			sec2 = sec2.replace(" 00", "*00");

			System.out.println("Section1:----\n" + sec1.trim());
			System.out.println("Section2:----\n" + sec2.trim());

			List<Result> seconelist = new ArrayList<>();
			List<Result> sectwolist = new ArrayList<>();

			if (spacecount > 49 & spacecount < 70) {
				System.out.println("Without Space");
				NameFinderMETest3 test = new NameFinderMETest3();
				seconelist = test.f24_section1(sec1.trim());
				sectwolist = test.f24_section2(sec2.trim());

			} else if (spacecount > 70 && spacecount < 85) {
				System.out.println("With Space");
				NameFinderMETest4 test = new NameFinderMETest4();
				seconelist = test.f24_section1(sec1.trim());
				sectwolist = test.f24_section2(sec2.trim());
			}

			System.out.println("Section1:  " + seconelist);
			System.out.println("Section2:  " + sectwolist);

			seconelist.addAll(sectwolist);
			System.out.println(seconelist);
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

	private String prepareJSON(List<Result> results) throws FileNotFoundException, IOException {

		// PrintWriter writer = new
		// PrintWriter("D:\\Neeraja\\ocr\\json\\myjson.json", "UTF-8");
		StringBuffer buffer = new StringBuffer();
		String line, mydata = null;
		int rowcount = 0;
		String v1 = "", v2 = "", v3 = "", v4 = "", v5 = "", v6 = "", v7 = "", sz = "", t = "", c = "", m = "", a = "",
				d = "", db = "", cr = "";
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
					v2 = v2 + result.getValue();
			}
			if (result.getKey().contains("Name")) {
				v3 = v3 + result.getValue();
			}
			if (result.getKey().contains("DOB")) {
				 if (StringUtils.isNumeric(result.getValue()) ||StringUtils.isAlpha(result.getValue()))
				v4 = v4 + result.getValue();
			}
			if (result.getKey().contains("Sex")) {
				if (StringUtils.isAlpha(result.getValue()))
					v5 = v5 + result.getValue();
			}
			if (result.getKey().contains("City")) {
				// if (StringUtils.isAlpha(result.getValue()))
				v6 = v6 + result.getValue();
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
					rowcount++;
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

		}
		// Replacing * with , in the debit values
		db = db.replace("*", ",");
		// replacing the values in Json

		buildf24(rowcount);
		StringTokenizer sztokenizer = new StringTokenizer(sz, ";");
		StringTokenizer ttokenizer = new StringTokenizer(t, ";");
		StringTokenizer ctokenizer = new StringTokenizer(c, ";");
		StringTokenizer mtokenizer = new StringTokenizer(m, ";");
		StringTokenizer atokenizer = new StringTokenizer(a, ";");
		StringTokenizer dtokenizer = new StringTokenizer(d, ";");
		StringTokenizer dbtokenizer = new StringTokenizer(db, ";");
		StringTokenizer crtokenizer = new StringTokenizer(cr, ";");
		try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/f24.json"))) {
			while ((line = br.readLine()) != null) {
				mydata = line;
				mydata = line.replace("v1", v1);
				mydata = mydata.replace("v2", v2);
				mydata = mydata.replace("v3", v3);
				mydata = mydata.replace("v4", v4);
				mydata = mydata.replace("v5", v5);
				mydata = mydata.replace("v6", v6);
				mydata = mydata.replace("v7", v7);

				if (sztokenizer.countTokens() == 0) {
					mydata = mydata.replaceAll("x1", "");
				} else if (mydata.contains("x1") && sztokenizer.hasMoreTokens()) {
					mydata = mydata.replaceFirst("x1", sztokenizer.nextToken());
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

				buffer.append(mydata + "\n");

			}
		} finally {

			// writer.close();
		}
		System.out.println(buffer.toString());
		return buffer.toString();

	}

	private void buildf24(int rowcount) {
		String data = "", section2row = "", section2rows = "";
		StringBuffer buffer = new StringBuffer();
		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/java/it/sella/f24/service/section2row.json"))) {
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
				section2rows = section2rows + section2row + "," + "\n";
			}
		}

		try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/testf24.txt"))) {
			while ((data = br.readLine()) != null) {
				data = data.replace("section2rows", section2rows);
				buffer.append(data + "\n");
			}
			FileWriter writer = new FileWriter("src/main/java/it/sella/f24/service/f24.json");
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

//				if (txtAnn.getBoundingPoly().getVertices().get(0).getX() > start
//						&& txtAnn.getBoundingPoly().getVertices().get(1).getX() < end) {
					String des = txtAnn.getDescription();
					if (txtAnn.getDescription().equals("*")) {
						des = "X";
					}
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
