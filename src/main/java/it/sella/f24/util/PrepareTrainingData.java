package it.sella.f24.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class PrepareTrainingData {

	private void splitSectionTraining() throws Exception {
		String data = "", data1 = "";
		int opRowCount = 1;

		PrintWriter writer = new PrintWriter("D:\\Neeraja\\ocr\\training data\\sectionsplit_result.txt", "UTF-8");
		BufferedReader section1br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section1trainingnewspace_result.txt"));
		BufferedReader constantsbr = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section2trainingnewspace_result_part1.txt"));
		BufferedReader variablesbr = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section2trainingnewspace_result_part2.txt"));

		try (BufferedReader br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\sectionsplitformat.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				data = line;

				for (int i = 0; i < 330; i++) {
					// section1 data replace
					String section1data = "";

					if ((section1data=section1br.readLine() )!= null) {
						System.out.println(section1data);
					}
					data1 = data.replace("s1", section1data);

					// Operazione replace

					String operazione = getOperazione(opRowCount);
					data1 = data1.replace("o1", operazione);

					// Constants data replace
					String constantdata = "";

					if ((constantdata=constantsbr.readLine() )!= null) {
						data1 = data1.replace("c1", constantdata);
					}else{
						data1 = data1.replace("c1", "EL 1234 A999");
					}
					
					if ((constantdata=constantsbr.readLine()) != null && data1.contains("c2")){
						data1 = data1.replace("c2", constantdata);
					}else{
						data1 = data1.replace("c2", "EL 1234 A999");
					}
					
					
					if ((constantdata=constantsbr.readLine()) != null && data1.contains("c3")) {
						constantdata = constantsbr.readLine();
						data1 = data1.replace("c3", constantdata);
					}else{
						data1 = data1.replace("c3", "EL 1234 A999");
					}
					
					
					if ((constantdata=constantsbr.readLine()) != null && data1.contains("c4")) {
						constantdata = constantsbr.readLine();
						data1 = data1.replace("c4", constantdata);
					}else{
						data1 = data1.replace("c4", "EL 1234 A999");
					}
					
					
					if ((constantdata=constantsbr.readLine()) != null && data1.contains("c5")) {
						constantdata = constantsbr.readLine();
						data1 = data1.replace("c5", constantdata);
					}else{
						data1 = data1.replace("c5", "EL 1234 A999");
					}
					

					

					// Variables Data replace
					String variabledata = "";
					if ((variabledata=constantsbr.readLine()) != null) {
						data1 = data1.replace("v1", variabledata);
					}
					
					if (variablesbr.readLine() != null && data1.contains("v2")) {
						data1 = data1.replace("v2", variabledata);
					}
					if (variablesbr.readLine() != null&& data1.contains("v3")) {
						data1 = data1.replace("v3", variabledata);
					}
					if (variablesbr.readLine() != null&& data1.contains("v4")) {
						data1 = data1.replace("v4", variabledata);
					}
					if (variablesbr.readLine() != null&& data1.contains("v5")) {
						data1 = data1.replace("v5", variabledata);
					}
					

					// Euro Replace
					data1 = data1.replace("e1", getCurrency(opRowCount));

					if (!data1.isEmpty()) {
						writer.println(data1);
						opRowCount++;
					}

				}
			}
		}
		writer.close();
	}

	private String getOperazione(int opRowCount) {
		String[] abArr = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		List<String> abList = Arrays.asList(abArr);
		Collections.shuffle(abList);

		Random random = new Random();
		Character character = (char) (random.nextInt(26) + 'A');
		int numofcases = 6;
		int key = opRowCount % numofcases;
		String o1 = "";
		switch (key) {
		case 0:
			o1 = (String) cString(abList.subList(0, 18));
			break;
		case 1:
			o1 = character + (String) cString(abList.subList(0, 18));
			break;
		case 2:
			for (int i1 = 0; i1 < 26; i1++) {
				Collections.shuffle(abList);
				if (i1 % 2 == 0) {
					o1 = o1 + cString(abList.subList(0, 1));
				} else {
					o1 = o1 + " ";
				}

			}

			break;
		case 3:
			for (int i1 = 0; i1 < 25; i1++) {
				Collections.shuffle(abList);
				if (i1 % 2 == 0) {
					o1 = o1 + cString(abList.subList(0, 1));
				} else {
					o1 = o1 + " ";
				}

			}

			o1 = character + " " + o1;
			break;
		case 4:
			StringBuffer buffer = new StringBuffer();
			buffer.append(cString(abList.subList(0, 18)));
			for (int i = 0; i < 10; i++) {
				buffer.insert(random.nextInt(9), " ");
			}
			o1=buffer.toString();
			break;
		case 5:
			StringBuffer buffer1 = new StringBuffer();
			buffer1.append(character);
			buffer1.append(cString(abList.subList(0, 17)));
			for (int i = 0; i < 10; i++) {
				buffer1.insert(random.nextInt(9), " ");
			}
			o1=buffer1.toString();
			break;
		default:
			break;
		}

		return o1;
	}

	public static void main(String[] args) throws Exception {
		PrepareTrainingData data = new PrepareTrainingData();
		 data.prepareforSection1();
//		 data.prepareforSection2Part2();
//		data.splitSectionTraining();
	}

	public void prepareforSection2Part2() throws Exception {
		int opRowCount = 1;
		String data = "", data1 = "";

		PrintWriter writer = new PrintWriter(
				"D:\\Neeraja\\ocr\\training data\\section2trainingnewspace_result_part1.txt", "UTF-8");

		Random rand1 = new Random();
		// "EL", "ER", "RG",
		String[] sArr = { "L", "R", "E", "G", "EL", "ER", "RG" };
		List<String> sList = Arrays.asList(sArr);

		try (BufferedReader br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section2trainingnewspace_part1.txt"))) {
			String line;

			while ((line = br.readLine()) != null) {
				data = line;
				for (int i = 0; i < 6000; i++) {

					Collections.shuffle(sList);
					data1 = data.replace("v1", sList.get(0));

					data1 = data1.replace("v2", String.valueOf(rand1.ints(1111, 9999).findFirst().getAsInt()));

					Character c = (char) (rand1.nextInt(26));

					String v3 = getCodice(opRowCount);

					data1 = data1.replace("v3", v3);

					String rd = "", rm = "";

					Integer ird = rand1.ints(1, 31).findFirst().getAsInt();

					Integer irm = rand1.ints(1, 12).findFirst().getAsInt();

					if (ird.toString().length() == 1) {
						rd = "0" + ird.toString();
					} else {
						rd = ird.toString();
					}
					if (irm.toString().length() == 1) {
						rm = "0" + irm.toString();
					} else {
						rm = irm.toString();
					}

					data1 = data1.replace("v4", rd + rm);

					data1 = data1.replace("v5", String.valueOf(rand1.nextInt(20) + 2000));

					// mese and anno

					data1 = data1.replace("v10",
							String.valueOf(rand1.nextInt(4000)) + String.valueOf(rand1.nextInt(20) + 2000));
					data1 = data1.replace("v6", "TESTDESC");
					data1 = data1.replace("x1", String.valueOf(rand1.nextInt(9)));

					String db = getCurrency(opRowCount);
					data1 = data1.replace("v7", db);

					data1 = data1.replace("v9", db);

					data1 = data1.trim();
					if (!data1.isEmpty()) {
						writer.println(data1);
						opRowCount++;
					}

				}
			}
		}

		writer.close();

	}

	private String getCodice(int opRowCount) {
		Random rand = new Random();
		Character c = (char) (rand.nextInt(26) + 'A');
		int num_of_cases = 8;
		int key = opRowCount % num_of_cases;
		StringBuffer buffer = new StringBuffer();
		switch (key) {
		case 0:
			buffer.append(c.toString());
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			break;
		case 1:
			buffer.append(c.toString());
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			break;
		case 2:
			buffer.append(c.toString());
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			break;
		case 3:
			buffer.append(c.toString());
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			break;
		case 4:
			buffer.append(c.toString());
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			break;
		case 5:
			buffer.append(c.toString());
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			break;
		case 6:
			buffer.append(c.toString());
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			break;
		case 7:
			buffer.append(c.toString());
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(" ");
			buffer.append(rand.nextInt(9));
			buffer.append(rand.nextInt(9));
			break;

		default:
			break;
		}

		return buffer.toString();
	}

	private String getCurrency(int opRowCount) {
		String[] dobitoArr = { "0", "2", "1", "3", "4", "9", "8", "5", "6", "7" };
		List<String> dobitoList = Arrays.asList(dobitoArr);
		Random rand = new Random();
		int num_of_cases = 7;
		int key = opRowCount % num_of_cases;
		String db1, db2, db = "";
		switch (key) {
		case 0:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(3) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			// db = db1 + "*" + db2;
			db = db1 + " , " + db2;
			break;
		case 1:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(3) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			db = db1 + " " + db2;
			break;
		case 2:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			db = db1 + " " + db2;
			break;
		case 3:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			// db = rand.nextInt(9) + " " + db1 + " " + db2;
			db = rand.nextInt(9) + " . " + db1 + " . " + db2;
			break;
		case 4:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			// db = rand.nextInt(9) + "*" + db1 + " " + db2;
			db = rand.nextInt(9) + "." + db1 + " " + db2;
			break;
		case 5:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			// db = rand.nextInt(9) + "*" + db1 + "*" + db2;
			db = rand.nextInt(9) + "." + db1 + " , " + db2;
			break;
		case 6:
			Collections.shuffle(dobitoList);
			db1 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 2));
			Collections.shuffle(dobitoList);
			db2 = (String) cString(dobitoList.subList(0, rand.nextInt(2) + 1));
			// db = db1 + "*" + db2;
			db = db1 + " . " + db2;
			break;
		default:
			break;
		}

		return db;
	}

	public void prepareforSection1() throws Exception {
		FileInputStream fis = new FileInputStream("src/main/resources/Dataset_Italian.xls");
		HSSFWorkbook workbook = new HSSFWorkbook(fis);
		HSSFSheet sheet = workbook.getSheet("Section1");
		HSSFRow row = sheet.getRow(0);
		PrepareTrainingData readExcelData = new PrepareTrainingData();
		Random rand = new Random();
		int opRowCount = 1;

		String data = "", data1 = "";

		PrintWriter writer = new PrintWriter("D:\\Neeraja\\ocr\\training data\\section1trainingnewspace_result.txt",
				"UTF-8");

		String[] abArr = { "A", "B", "1", "C", "D", "E", "5", "F", " ", "G", "H", "I", "8", "J", "K", "L", " ", "M",
				"N", "O", "9", " ", "P", "Q", "R", "S", "6", " ", "T", "U", " ", "V", "W", " ", "X", "Y", "Z" };
		List<String> abList = Arrays.asList(abArr);

		String[] mfArr = { "M", "F" };
		List<String> mfList = Arrays.asList(mfArr);

		// Getting coloumn numbers
		int cognome_col = -1, name_col = -1, dob_col = -1, commune_col = -1, prov_col = -1;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			if (row.getCell(i).getStringCellValue().trim().equals("Cognome")) {
				cognome_col = i;
				continue;
			} else if (row.getCell(i).getStringCellValue().trim().equals("Name")) {
				name_col = i;
				continue;
			} else if (row.getCell(i).getStringCellValue().trim().equals("DOB")) {
				dob_col = i;
				continue;
			} else if (row.getCell(i).getStringCellValue().trim().equals("Comune")) {
				commune_col = i;
				continue;
			} else if (row.getCell(i).getStringCellValue().trim().equals("Prov")) {
				prov_col = i;
				continue;
			} else {
				break;
			}
		}

		HSSFCell cell = null;
		HSSFRow curr_row = null;

		int rowcount = 1;
		int rowmax = 13015;
		int numoflines = 1305;

		try (BufferedReader br = new BufferedReader(
				new FileReader("D:\\Neeraja\\ocr\\training data\\section1trainingnewspace.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				data = line;

				int end = rowcount + numoflines;
				if (end > rowmax) {
					end = rowmax;
				}
				for (int i = rowcount; i < end; i++) {
					Collections.shuffle(abList);

					// getFiscale(opRowCount);
					// data1 = data.replace("v1", cString(abList.subList(0,
					// 16)));
					data1 = data.replace("v1", getFiscale(opRowCount));

					curr_row = sheet.getRow(i);
					cell = curr_row.getCell(cognome_col);
					String v2 = String.valueOf(cell).toUpperCase();
					if (v2.equals("NULL")) {
						v2 = "";
					}
					// data1 = data1.replace("v2",
					// cell.toString().toUpperCase());

					// data1 = data1.replace("v3",
					// readExcelData.getName(opRowCount));
					// GHETTO ** FRANCO
					cell = curr_row.getCell(name_col);
					String v3 = String.valueOf(cell).toUpperCase();
					if (v3.equals("NULL")) {
						v3 = "";
					}
					data1 = data1.replace("v3", v2 + " ** " + v3);

					// cell = curr_row.getCell(dob_col);
					// data1 = data1.replace("v4",
					// cell.toString().toUpperCase());

					data1 = data1.replace("v4", readExcelData.getDOB(opRowCount));

					Collections.shuffle(mfList);
					data1 = data1.replace("v5", cString(mfList.subList(0, 1)));

					cell = curr_row.getCell(commune_col);
					data1 = data1.replace("v6", cell.toString().toUpperCase());

					cell = curr_row.getCell(prov_col);
					data1 = data1.replace("v7", cell.toString().toUpperCase());

					if (!data1.isEmpty()) {
						writer.println(data1);
						opRowCount++;
					}
				}
				rowcount = rowcount + numoflines;
				if (rowcount >= rowmax) {
					rowcount = 1;
				}
			}
		}

	}

	private CharSequence getFiscale(int opRowCount) {

		String[] abArr1 = {  "1",  "2", "3", "4", "5", "6", "7", "8", "9", "0","1",  "2", "3", "4", "5", "6", "7", "8", "9", "0"};
		
		List<String> abList1 = Arrays.asList(abArr1);

		String[] abArr = { "A", "B", "1", "C", "2", "P", "Q", "R","3","J", "K", "L", "4", "5","M", "N", "O", "6", "7", "8","U", "V", "W", "9", "0", "D", "6" ,"E", "5", "F", "G",
				"H", "I",    "S", "T", "1", "X", "3","Y",
				"Z" };
		List<String> abList = Arrays.asList(abArr);

		Collections.shuffle(abList);
		Collections.shuffle(abList1);
		int numofcases = 6;
		int key = opRowCount % numofcases;
		Collections.shuffle(abList);
		String v1 = "";
		Random random=new Random();
		
		
		switch (key) {
		case 0:
			v1 = (String) cString(abList.subList(0, 16));
			StringBuffer buffer=new StringBuffer(v1);
			for(int i=0;i<5;i++){
				buffer.insert(random.nextInt(15), " ");
			}
			v1=buffer.toString();
			break;
		case 1:
			for (int i = 0; i < 32; i++) {
				Collections.shuffle(abList);
				if (i % 2 == 0) {
					v1 = v1 + (String) cString(abList.subList(0, 1));
				} else {
					v1 = v1 + " ";
				}
			}
			break;
			
		case 2:
			v1=(String) cString(abList.subList(0, 16));
		//for Legal people
		case 3:
			v1=(String) cString(abList1.subList(0, 11));
		case 4:
			v1 = (String) cString(abList1.subList(0, 11));
			StringBuffer buffer1=new StringBuffer(v1);
			for(int i=0;i<5;i++){
				buffer1.insert(random.nextInt(10), " ");
			}
			v1=buffer1.toString();
			break;
		case 5:
			for (int i = 0; i < 22; i++) {
				if (i % 2 == 0) {
					Collections.shuffle(abList1);
					v1 = v1 + (String) cString(abList1.subList(0, 1));
				} else {
					v1 = v1 + " ";
				}
			}
			break;
			
		default:
			break;
		}

		return v1;
	}

	private static CharSequence cString(List<String> subList) {
		StringBuffer str = new StringBuffer();
		for (String s : subList) {
			str = str.append(s);
		}
		// System.out.println(str);
		return str.toString();
	}

	private String getDOB(int rownum) {

		int num_of_cases = 8;

		String[] dobArr1 = { "2", "3", "O", "5", "O", "6", "8", "1", "7", "4", "9", "0","0" };
		List<String> dobList1 = Arrays.asList(dobArr1);

		Random rand = new Random();
		String v4 = "", rd = "", rm = "";

		Integer ird = rand.ints(1, 31).findFirst().getAsInt();

		Integer irm = rand.ints(1, 12).findFirst().getAsInt();

		Integer iry = rand.ints(1900, 2000).findFirst().getAsInt();

		if (ird.toString().length() == 1) {
			rd = "0" + ird.toString();
		} else {
			rd = ird.toString();
		}
		if (irm.toString().length() == 1) {
			rm = "0" + irm.toString();
		} else {
			rm = irm.toString();
		}

		int key = rownum % num_of_cases;

		switch (key) {
		case 0:
			v4 = rd + rm + iry.toString();
			break;
		case 1:
			v4 = rd + " " + rm + " " + iry.toString();
			break;
		case 2:
			v4 = rd + rm + " " + iry.toString();
			break;
		case 3:
			for (int i1 = 0; i1 < 8; i1++) {
				Collections.shuffle(dobList1);
				if (i1 % 2 == 0) {
					v4 = v4 + cString(dobList1.subList(0, 1));
				} else {
					v4 = v4 + " ";
				}

			}
			v4 = v4 + String.valueOf(rand.nextInt(20) + 2000);
			break;
		case 4:

			for (int i1 = 0; i1 < 16; i1++) {
				Collections.shuffle(dobList1);
				if (i1 % 2 == 0) {
					v4 = v4 + cString(dobList1.subList(0, 1));
				} else {
					v4 = v4 + " ";
				}

			}
			break;
		case 5:
			for (int i1 = 0; i1 < 8; i1++) {
				Collections.shuffle(dobList1);
				if (i1 % 2 == 0) {
					v4 = v4 + cString(dobList1.subList(0, 1));
				} else {
					v4 = v4 + "";
				}

			}
			v4 = v4 + String.valueOf(rand.nextInt(20) + 1900);
			break;
		case 6:
			for (int i1 = 0; i1 < 8; i1++) {
				Collections.shuffle(dobList1);
				if (i1 % 2 == 0) {
					v4 = v4 + cString(dobList1.subList(0, 1));
				} else {
					v4 = v4 + "";
				}

			}
			v4 = v4 + " " + String.valueOf(rand.nextInt(20) + 1900);
			break;
		case 7:
			Collections.shuffle(dobList1);
			v4 = (String) cString(dobList1.subList(0, 8));
			StringBuffer buffer=new StringBuffer(v4);
			for(int i=0;i<4;i++){
				buffer.insert(rand.nextInt(7), " ");
			}
			v4=buffer.toString();
			break;
		case 8:
			v4 = rd + rm + iry.toString();
			break;

		default:
			break;
		}

		return v4;

	}

	private String getName(int rowcount) {

		Random rand = new Random();

		String[] AnaArr = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		List<String> AnaList = Arrays.asList(AnaArr);
		String a1 = "", a2 = "";

		if (rowcount % 2 == 0) {
			Collections.shuffle(AnaList);

			a1 = (String) cString(AnaList.subList(0, rand.nextInt(3) + 6));
		} else {
			Collections.shuffle(AnaList);

			a1 = (String) cString(AnaList.subList(0, rand.nextInt(3) + 6));

			Collections.shuffle(AnaList);

			a2 = (String) cString(AnaList.subList(0, rand.nextInt(3) + 5));
			a1 = a1 + " " + a2;

		}

		return a1;

	}
}
