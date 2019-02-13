package it.sella.f24.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import io.netty.handler.codec.http.HttpContentEncoder.Result;
import it.sella.f24.bean.DateFormat;
import it.sella.f24.bean.TypeofInput;
import it.sella.f24.bean.UserInput;
//import opennlp.maxent.Main;
import it.sella.f24.util.WritetoExcel;
import opennlp.tools.namefind.NameFinderMETokenFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
@Service
public class PrepareTrainingDataService {

	private static final Pattern TAG_REGEX = Pattern.compile("<START:(.+?)> (.+?) <END>");

//	public static void main(String[] args) {
//		String ts = "  . F24   DELEGA IRREVOCABILE A : MODELLO DI PAGAMENTO UNIFICATO AGENZIA  PROV . PER L ' ACCREDITO ALLA TESORERIA COMPETENTE CONTRIBUENTE CODICE FISCALE   <START:Fiscale> PPL SFN 6 9     A 4 6 D 6 4 3 J <END>  ,       DATI ANAGRAFICI <START:Anagrafici> PIPOLI <END> <START:Name> STEFANIA <END>             POK <START:DOB> 0 60119 6 9 <END> <START:Sex> F  <END> <START:City> FOGGIA <END>  <START:Prov> F G  <END> CODICE FISCALE  ,  ,  ,        MOTIVO DEL PAGAMENTO  VERSAMENTO   .  .      W .   .   .       <START:Sezione> EL <END> <START:tributo> 3918 <END> <START:codice> D 6 1 2 <END>  <START:anno> 2013 <END> <START:dobito> 1 . 967 , 00 <END> 7777777777777777  TICHETTUAL SALDO  EURO : 1 <START:euro> 2 . 231 . 00 <END> FINAL ESTREMI DEL VERSAMENTO ( DA CON LARE A CURA DI BANCA / POSTE AGENTE DELLA RISCOSSIONE CODICE BANCA / POSTE / AGENTE DELLA RISCOSSIONE DATA       /  AZIENDA CAD / SPORTELO  . 10   /       /    . ABI CAB";
//
//		ts = ts.replaceAll("\\*", "");
//		UserInput input = new UserInput("Fiscale", 11, 16, true, TypeofInput.ALPHANUMERIC, null, 1, 1);
//		UserInput input2 = new UserInput("Anagrafici", 2, 8, true, TypeofInput.ALPHA, null, 1, 4);
//		UserInput input3 = new UserInput("Name", 2, 8, true, TypeofInput.ALPHA, null, 1, 4);
//		UserInput input4 = new UserInput("DOB", 8, 8, true, TypeofInput.DATE, DateFormat.DDMMYYYY, 1, 1);
//		UserInput input5 = new UserInput("Sex", 1, 1, true, TypeofInput.ALPHA, null, 1, 1);
//		UserInput input6 = new UserInput("City", 2, 10, true, TypeofInput.ALPHA, null, 1, 4);
//		UserInput input7 = new UserInput("Prov", 2, 2, true, TypeofInput.ALPHA, null, 1, 1);
//
//		List<UserInput> inputs = new ArrayList<>();
//
//		inputs.add(input);
//		inputs.add(input2);
//		inputs.add(input3);
//		inputs.add(input4);
//		inputs.add(input5);
//		inputs.add(input6);
//		inputs.add(input7);
//
//		WritetoExcel writetoExcel = new WritetoExcel();
//
//		String taggedFilePath = writetoExcel.getColumnData("TaggedFilepath");
//		String inputExcel = writetoExcel.getColumnData("Trainingdatapath");
//
//		PrepareTrainingDataService dataService = new PrepareTrainingDataService();
//		// String
//		// trainingfilepath1=dataService.prepareTrainingwithUserinput(inputs,taggedFilePath);
//
//		String trainingfilepath2 = dataService.prepareTrainingDatawithExcel(inputExcel, taggedFilePath);
//
//	}

	public String prepareTrainingDatawithExcel(String inputExcel, String taggedFilePath) {
		FileInputStream fis = null;
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		;
		HSSFRow row = null;
		PrintWriter writer = null;

		Random rand = new Random();
		int opRowCount = 1;

		String data = "", data1 = "";

		String line = getFileContent(taggedFilePath);
		line = line.replaceAll("\\*", "");
		String taggedString = getTaggedString(line);
		System.out.println(taggedString);

		try {
			fis = new FileInputStream(inputExcel);
			workbook = new HSSFWorkbook(fis);
			sheet = workbook.getSheet("Section1");
			row = sheet.getRow(0);
			writer = new PrintWriter("src/main/resources/result.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HSSFCell cell = null;
		HSSFRow curr_row = null;

		int rowcount = 1;
		int rowmax = 13015;
		int numoflines = 1305;


		int col_num = -1;

		for (int r = 1; r < rowmax; r++) {
			String ns = taggedString;
			final Matcher matcher = TAG_REGEX.matcher(taggedString);
			while (matcher.find()) {
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim().equals(matcher.group(1))) {
						col_num = i;
					} else {
						continue;
					}
				}
				if (col_num == -1) {
					System.out.println("There is no column with the specified name" + matcher.group(1));
				}

				curr_row = sheet.getRow(r);
				cell = curr_row.getCell(col_num);
				String value = String.valueOf(cell).toUpperCase();
				ns = ns.replaceAll("<START:" + matcher.group(1) + "> " + matcher.group(2) + " <END>",
						"<START:" + matcher.group(1) + "> " + value + " <END>");
				// System.out.println(ns);
			}
			System.out.println(ns);
			
			writer.println(ns);
		}
		
		writer.close();
		
		NameFinderMETokenFinder finder=new NameFinderMETokenFinder();
		String modelfile=null;
		try {
			modelfile = finder.f24PrepareTrainingFile("result.txt");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return modelfile;
	}

	private String prepareTrainingwithUserinput(List<UserInput> inputs, String taggedFilePath) {

		return null;
	}

	private String getFileContent(String taggedFilePath) {

		FileReader fileReader;
		String line = "";
		try {
			fileReader = new FileReader(new File(taggedFilePath));
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			line = bufferedReader.readLine();

			if (line != null) {
				return line;
			} else {
				return "Tagged File is empty";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return "File is empty";
	}

	private String getTaggedString(String taggedString) {

		final Matcher matcher = TAG_REGEX.matcher(taggedString);

		String[] chars = { "a", "b", "c", "d", "e", "f" };
		int i = 1, j = 0;
		String variable = chars[j];

		while (matcher.find()) {
			taggedString = taggedString.replaceAll("<START:" + matcher.group(1) + "> " + matcher.group(2) + " <END>",
					"<START:" + matcher.group(1) + "> " + variable + i + " <END>");
			if (i == 9) {
				i = 0;
				j++;
				variable = chars[j];
			}
			i++;
		}
		return taggedString;
	}

}
