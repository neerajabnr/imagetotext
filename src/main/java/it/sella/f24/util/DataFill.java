package it.sella.f24.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFill {

	// public static void main(String[] args) {
	// String template = "codice tributo codice identificado anno di ferimento
	// Importi a debito versad rateariome/reglonel pro mese d "
	// + " 0v1 0v2 Importi a credito compensad v3,v4 v5 v6 "
	// + "IMPOS TE DIRETTE - IVA RITENUTE ALLA FONTE ALTRI TRIBUTI "
	// + "ED INTERESSI codice ufficio codice alto TOTALE v7- v8,v9";
	// String withValues = "codice tributo codice identificado anno di ferimento
	// Importi a debito versad rateariome/reglonel pro mese d "
	// + " 01 01 Importi a credito compensad 261,39 6099 2015 "
	// + "IMPOS TE DIRETTE - IVA RITENUTE ALLA FONTE ALTRI TRIBUTI "
	// + "ED INTERESSI codice ufficio codice alto TOTALE 6139- 261,39";
	//
	// String patternFromTemplate = template.replaceAll("\\.", "\\\\."); //
	// escape "."
	// patternFromTemplate = patternFromTemplate.replace("v1", "(.*)"); //
	// capturing group 1
	// patternFromTemplate = patternFromTemplate.replace("v2", "(.*)"); //
	// capturing group 2
	// patternFromTemplate = patternFromTemplate.replace("v3", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v4", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v5", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v6", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v7", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v8", "(.*)");
	// patternFromTemplate = patternFromTemplate.replace("v9", "(.*)");
	// System.out.println(patternFromTemplate);
	// Pattern p = Pattern.compile(patternFromTemplate);
	// Matcher m = p.matcher(withValues);
	// if (m.matches()) {
	// System.out.println("v1 = "+m.group(1));
	// System.out.println("v2 = "+m.group(2));
	//
	// System.out.println("v3 = "+m.group(3));
	// System.out.println("v4 = "+m.group(4));
	// System.out.println("v5 = "+m.group(5));
	// }
	// }

	// public static void main(String[] args) throws FileNotFoundException,
	// UnsupportedEncodingException {
	// String data = "SEZIONE ERARIO.codice tributo.codice identificado.anno
	// di."
	// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
	// + " <START:val1> 0v1 <END> <START:val1a> 0v2 <END> .Importi a credito
	// compensad. <START:val2> v3,v4 <END> ."
	// + " <START:val3> v5 <END> . <START:val4> v6 <END> .IMPOS TE DIRETTE -
	// IVA.RITENUTE ALLA FONTE."
	// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.TOTALE.
	// <START:val5> v7 <END> -. <START:val6> v8,v9 <END> .";
	//
	// String data1 = "SEZIONE ERARIO.codice tributo.codice identificado.anno
	// di."
	// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
	// + "<START:val1> 01 01 <END> .Importi a credito compensad. <START:val2>
	// 261,39 <END> ."
	// + " <START:val3> 6099 <END> . <START:val4> 2015 <END> .IMPOS TE DIRETTE -
	// IVA.RITENUTE ALLA FONTE."
	// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO
	// (A-B).TOTALE.6139-.261,39.";
	// PrintWriter writer = new PrintWriter("D:\\OCR\\section2.txt", "UTF-8");
	//
	// Random rand1 = new Random();
	//
	// Random rand2 = new Random();
	//
	// Random rand3 = new Random();
	//
	// Random rand4 = new Random();
	//
	// Random rand5 = new Random();
	//
	// Random rand6 = new Random();
	//
	// Random rand7 = new Random();
	//
	// Random rand8 = new Random();
	//
	// Random rand9 = new Random();
	//
	//
	//
	// for(int i=0;i<20000;i++){
	// data1 = data.replace("v1", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v2", String.valueOf(rand2.nextInt(9)));
	// data1 = data1.replace("v3", String.valueOf(rand3.nextInt(999)));
	// data1 = data1.replace("v4", String.valueOf(rand4.nextInt(99)));
	// data1 = data1.replace("v5", String.valueOf(rand5.nextInt(9999)));
	// data1 = data1.replace("v6", String.valueOf(rand6.nextInt(2100)));
	// data1 = data1.replace("v7", String.valueOf(rand7.nextInt(9999)));
	// data1 = data1.replace("v8", String.valueOf(rand8.nextInt(999)));
	// data1 = data1.replace("v9", String.valueOf(rand9.nextInt(99)));
	// writer.println(data1);
	// }
	//
	//
	// writer.close();
	// }

	// public static void main(String[] args) throws FileNotFoundException,
	// UnsupportedEncodingException {
	// String data = "CODICE FISCALE <START:codice1> v1 v2 v3 v4 v5 v6 v7 v8 v9
	// va1 va2 <END> .1.cognome,"
	// + " denominazione o raglone sociale. <START:name1> DATI ANAGRAFICI va3
	// va4 va5 va6 va7 <END> .data di nascka.sesso (MF)."
	// + "comune fo Stato estero di nascita.Barrare in caso di andampos."
	// + "non coincidente con anno solare.comune.prov.. <START:prov1> va8 <END>
	// .Via e numero civico. <START:civic1> va9 vb1 vb2 vb3 vb4. <END> "
	// + "DOMICILIO FISCALE <START:civic1> vb5 <END> .CODICE FISCALE del
	// coobbligato, erede,.genitore, tutore o curatore fallimentare.";
	//
	//
	//// String data = "SEZIONE ERARIO.codice tributo.codice identificado.anno
	// di."
	//// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
	//// + "<START:val1> 0v1 0v2 <END> .Importi a credito compensad.
	// <START:val2> 261,39 <END> ."
	//// + " <START:val3> 6099 <END> . <START:val4> 2015 <END> .IMPOS TE DIRETTE
	// - IVA.RITENUTE ALLA FONTE."
	//// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO
	// (A-B).TOTALE.6139-.261,39.";
	//
	// String data1 = "SEZIONE ERARIO.codice tributo.codice identificado.anno
	// di."
	// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
	// + "<START:val1> 01 01 <END> .Importi a credito compensad. <START:val2>
	// 261,39 <END> ."
	// + " <START:val3> 6099 <END> . <START:val4> 2015 <END> .IMPOS TE DIRETTE -
	// IVA.RITENUTE ALLA FONTE."
	// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO
	// (A-B).TOTALE.6139-.261,39.";
	// PrintWriter writer = new PrintWriter("D:\\OCR\\section1.txt", "UTF-8");
	//
	// Random rand1 = new Random();
	//
	//
	// String[] abArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
	// "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
	// "Z"};
	// List<String> abList = Arrays.asList(abArr);
	// Collections.shuffle(abList);
	// System.out.println(abList.subList(0, 7));
	//
	// for(int i=0;i<10000;i++){
	// data1 = data.replace("v1", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v2", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v3", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v4", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v5", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v6", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v7", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v8", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("v9", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("va1", String.valueOf(rand1.nextInt(9)));
	// data1 = data1.replace("va2", String.valueOf(rand1.nextInt(9)));
	//
	// Collections.shuffle(abList);
	// data1 = data1.replace("va3",cString(abList.subList(0,
	// rand1.nextInt(7))));
	// Collections.shuffle(abList);
	// data1 = data1.replace("va4", cString(abList.subList(0,
	// rand1.nextInt(8))));Collections.shuffle(abList);
	// data1 = data1.replace("va5", cString(abList.subList(0,
	// rand1.nextInt(9))));Collections.shuffle(abList);
	// data1 = data1.replace("va6", cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("va7", cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("va8",cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("va9", cString(abList.subList(0,
	// rand1.nextInt(5))));Collections.shuffle(abList);
	// data1 = data1.replace("vb1",cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("vb2",cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("vb3",cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("vb4",cString(abList.subList(0,
	// rand1.nextInt(7))));Collections.shuffle(abList);
	// data1 = data1.replace("vb5",cString(abList.subList(0,
	// rand1.nextInt(7))));
	//
	// writer.println(data1);
	// }
	//
	//
	// writer.close();
	// }

	public static void main(String[] args) throws IOException {
		f24_section22();
	}

	private static void f24_section22() throws IOException {
		String data = "Sezione cod , write to codice entew ww anno di <START:Seizone> v1 <END> <START:Tributo> v2 <END> <START:Erito> Dv3 <END> riferimento detrazione importi a dobito versati importi a credito compensati <START:Year> v4 <END> TESTDESC <START:Amount> v5*v6 <END> <START:Tributo> v2 <END> <START:Erito> Dv3 <END> <START:Year> v4 <END> TESTDESC <START:Amount> v5*v6 <END> Dund Autodeto COS343";

		String data1 = "Sezione cod , . vituo codice ano di leazkenel ene a t 31NOWLSOLIVONVIVI 11 mesend . importi a debito versai importi p a credito compens"
				+ " ali <START:Sezione> EL <END> <START:Cod> 3918 <END> . <START:Codice> 0600 <END> <START:Val> X <END> S <START:Year> 2018 <END> - <START:Import1> 243 <END> <START:Import2> , <END> <START:Import3> 00 <END>";

		// String data = "SEZIONE ERARIO.codice tributo.codice identificado.anno
		// di."
		// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
		// + "<START:val1> 0v1 0v2 <END> .Importi a credito compensad.
		// <START:val2> 261,39 <END> ."
		// + " <START:val3> 6099 <END> . <START:val4> 2015 <END> .IMPOS TE
		// DIRETTE - IVA.RITENUTE ALLA FONTE."
		// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO
		// (A-B).TOTALE.6139-.261,39.";

		// String data1 = "SEZIONE ERARIO.codice tributo.codice
		// identificado.anno di."
		// + "ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d."
		// + "<START:val1> 01 01 <END> .Importi a credito compensad.
		// <START:val2> 261,39 <END> ."
		// + " <START:val3> 6099 <END> . <START:val4> 2015 <END> .IMPOS TE
		// DIRETTE - IVA.RITENUTE ALLA FONTE."
		// + "ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO
		// (A-B).TOTALE.6139-.261,39.";
		PrintWriter writer = new PrintWriter("D:\\Neeraja\\ocr\\training data\\section2trainingnew_result.txt",
				"UTF-8");

		Random rand1 = new Random();

		String[] sArr = { "EL", "ER", "RG" };
		List<String> sList = Arrays.asList(sArr);
		String[] abArr = { "0", "2", "C", "D", "5", "4", "G", "H", "9", "8" };
		List<String> abList = Arrays.asList(abArr);
		Collections.shuffle(abList);
		String[] yearArr = { "0", "2", "1", "3", "D", "4", "C", "9", "8" };
		List<String> yearList = Arrays.asList(yearArr);

		try (BufferedReader br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section2trainingnew.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				data = line;
				for (int i = 0; i < 2500; i++) {
					//Combining v2 and v3
					Collections.shuffle(abList);
					data1 = data.replace("v2", String.valueOf(rand1.nextInt(9999)));
//					 data1 = data1.replace("v3",
//					 String.valueOf(rand1.nextInt(5000)));
					// data1 = data1.replace("v4",
					// String.valueOf(rand1.nextInt(9999)));
					data1 = data1.replace("v5", String.valueOf(rand1.nextInt(20) + 2000));
					// data1 = data1.replace("v5",
					// String.valueOf(rand1.nextInt(999)));
					 data1 = data1.replace("v6", "TESTDESC");
					// data1 = data1.replace("v4",
					// String.valueOf(rand1.nextInt(999)));
					data1 = data1.replace("v7", String.valueOf(rand1.nextInt(999))+"*00");
					data1 = data1.replace("a1", String.valueOf(rand1.nextInt(9)));
					data1 = data1.replace("a2", String.valueOf(rand1.nextInt(9)));
					Collections.shuffle(abList);
					data1 = data1.replace("x1", abList.get(0));
					Collections.shuffle(abList);
					data1 = data1.replace("y1", abList.get(0));
					Collections.shuffle(abList);
					data1 = data1.replace("y2", abList.get(0));
					data1 = data1.replace("x2", String.valueOf(rand1.nextInt(9)));
					data1 = data1.replace("x3", String.valueOf(rand1.nextInt(9)));
					data1 = data1.replace("x4", String.valueOf(rand1.nextInt(9)));
					data1 = data1.replace("x5", String.valueOf(rand1.nextInt(999)));

					Collections.shuffle(sList);
					data1 = data1.replace("v1", sList.get(0));

					Collections.shuffle(abList);
					data1 = data1.replace("v3", cString(abList.subList(0, rand1.nextInt(3) + 3)));
					Collections.shuffle(yearList);
					data1 = data1.replace("v4",
							String.valueOf(rand1.nextInt(9999)));
					data1 = data1.trim();
					if (!data1.isEmpty()) {
						writer.println(data1);
					}

				}
			}
		}

		writer.close();
	}

	private static void f24_section21() throws FileNotFoundException, UnsupportedEncodingException {
		String data = "COMPETENTE CODICE FISCALE <START:Fiscale> v1 <END> cognome , denominazione o ragione sociale DATI ANAGRAFICI <START:Anagrafici> v2 <END> <START:Name> v3 <END> data <START:DayMonth> v9 <END> di nascita <START:Year> v6 <END> Sesso Mo F ) comune lo Stato estero ) di nascita <START:Gender> v8 <END> <START:City> v7 <END> CODICE FISCALE del coobbligato genitore , tutore o curatore fallimentare , erede , MOTIVO M DEL PAGAMENTO codice dontificative IDENTIFICATIVO OPERAZIONE";

		String data1 = "CODICE FISCALE <START:codice> BRSDRN24C42A859R <END> \ncognomie,"
				+ " denambazlonie o ragione sociale\nnome\nDATI ANAGRAFICI <START:dati> BORASIO <END> "
				+ "\n <START:name> ADRIANA <END> \ndala di nascita\nsessa(MF) comune lo Stato estero)"
				+ " di nascila\n <START:val1> 02 <END> <START:val2> 03 <END> <START:val3> 1924 <END> "
				+ "<START:val4> F <END> <START:val5> BIELLA <END> \n�\nCODICE FISCALE del coobbligato,"
				+ " erede,\ngenitore, tutore o curatore fallimentare\ncodice identificalvo";

		PrintWriter writer = new PrintWriter("D:\\Neeraja\\ocr\\training data\\section1trainingnew_result.txt",
				"UTF-8");

		Random rand1 = new Random();

		String[] abArr = { "A", "B", "1", "C", "D", "E", "5", "F", "G", "H", "I", "8", "J", "K", "L", "M",
				"N", "O", "9", "P", "Q", "R", "S", "6", "T", "U", "V", "W", "X", "Y", "Z" };
		List<String> abList = Arrays.asList(abArr);
		String[] dobArr = { "2", "3", "O", "5", "O", "'" ,"6", "8", "1", "7","'" };
		List<String> dobList = Arrays.asList(dobArr);
		String[] mfArr = { "M", "F" };
		List<String> mfList = Arrays.asList(mfArr);
		String[] cityArr = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		List<String> cityList = Arrays.asList(cityArr);
		Collections.shuffle(abList);
		String dob = "12345OABC";
		System.out.println(abList.subList(0, 7));

		try (BufferedReader br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section1trainingnew.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				data = line;
				for (int i = 0; i < 2500; i++) {
					// data1 = data.replace("v4",
					// String.valueOf(rand1.nextInt(999999)));
					// data1 = data1.replace("v5",
					// String.valueOf(rand1.nextInt(9)));
					// data1 = data1.replace("v6",
					// String.valueOf(rand1.nextInt(9999)));
					// data1 = data1.replace("v4",
					// String.valueOf(rand1.nextInt(200)+1900));

					Collections.shuffle(abList);
					data1 = data.replace("v1", cString(abList.subList(0, 16)));
					Collections.shuffle(cityList);
					data1 = data1.replace("v2", cString(cityList.subList(0, rand1.nextInt(3) + 6)));
					Collections.shuffle(cityList);
					data1 = data1.replace("v3", cString(cityList.subList(0, rand1.nextInt(3) + 6)));
					// data1 = data1.replace("v4",cString(dobList.subList(0,
					// rand1.nextInt(3)+9)));
					Collections.shuffle(mfList);
					String v4 = "";
					for (int i1 = 0; i1 < 5; i1++) {
						Collections.shuffle(dobList);
						if (i1 % 2 == 0) {
							v4 = v4 + cString(dobList.subList(0, 1));
						} else {
							v4 = v4 + " ";
						}

					}
					v4 = v4 + String.valueOf(rand1.nextInt(20) + 9999);
					System.out.println(v4);
					data1 = data1.replace("v4", v4);
					 data1 = data1.replace("v5",cString(mfList.subList(0,
					 1)));
					Collections.shuffle(cityList);
					data1 = data1.replace("v6", cString(cityList.subList(0, rand1.nextInt(3) + 8)));
					Collections.shuffle(mfList);
					data1 = data1.replace("v7", cString(mfList.subList(0, 2)));
					// data1 = data1.replace("v8",cString(mfList.subList(0,
					// 1)));
					if (!data1.isEmpty()) {
						writer.println(data1);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	private static CharSequence cString(List<String> subList) {
		StringBuffer str = new StringBuffer();
		for (String s : subList) {
			str = str.append(s);
		}
		// System.out.println(str);
		return str.toString();
	}

}
