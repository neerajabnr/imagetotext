package it.sella.f24.util;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.Result;
import it.sella.f24.bean.TextAnnotation;
import it.sella.f24.controller.F24Controller;
import it.sella.f24.service.F24OCRService;
import opennlp.tools.namefind.NameFinderMETokenFinder;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;

public class Sample {
	public static void main(String[] args) throws IOException, ParseException {
//		String data="",section2row="",section2rows="";
//		StringBuffer buffer=new StringBuffer();
//		try(BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/section2row.json"))){
//			while((data=br.readLine())!=null){
//				section2row=section2row+data+"\n";
//			}
//		}
//		
//		int rowcount=4;
//		
//		for(int i=0;i<rowcount;i++){
//			if(i==rowcount-1){
//				section2rows=section2rows+section2row+"\n";
//			}else{ 
//				section2rows=section2rows+section2row+" , "+"\n";
//			}
//		}
//		
//		
//		try(BufferedReader br = new BufferedReader(new FileReader("src/main/java/it/sella/f24/service/testf24.txt"))){
//			while((data=br.readLine())!=null){
//				data=data.replace("section2rows", section2rows);
//				buffer.append(data+"\n");
//			}
//		}
//		
//		
//		FileWriter writer=new FileWriter("src/main/java/it/sella/f24/service/f24.json");
//		writer.append(buffer);
//		writer.close();
//		System.out.println(buffer);
		
/*		F24OCRService f24ocrService=new F24OCRService();
		
		 byte[] jsonData = Files.readAllBytes(Paths.get("/home/bsindia/Documents/f24_txt//testimagejson3.txt"));
		 try {
			f24ocrService.processJson(jsonData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
*/	
		
		/*String v4="CODICE12345678LORIS";
		
		if(v4.length()>8) {
			if(StringUtils.isNumeric(v4.substring(0, 8))) {
				v4=v4.substring(0,7);
			}else if(StringUtils.isNumeric(v4.substring(v4.length()-8, v4.length()))) {
				v4=v4.substring(v4.length()-8, v4.length());
			}else if(StringUtils.contains("[A-Z]",v4 )){
				v4=v4.replaceAll("[A-Z]", "");
			}
		}
	
	System.out.println(v4);*/
		
		
//		Pattern testPattern = Pattern.compile("test");
//
//		Pattern[] patterns = new Pattern[]{testPattern};
//		Map<String, Pattern[]> regexMap = new HashMap<>();
//		String type = "testtype";
//
//		regexMap.put(type, patterns);
//
//		RegexNameFinder finder =
//		new RegexNameFinder(regexMap);

//		Span[] result = finder.find(sentence);
		
//		System.out.println(result[0].getType());
		
//		F24OCRService service=new F24OCRService();
//		service.buildf24(4);
//		LocalDate date=java.time.LocalDate.now();
//		
//		System.out.println(date);  y
		
//		String data="30101947";
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
//		java.util.Date date;
//		try {
//			date = new SimpleDateFormat("ddMMyyyy").parse(data);
//			System.out.println(formatter.format(date));
//		} catch (java.text.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	   /* Date date =  new Date(Long.parseLong(data));
	    formatter.setLenient(false);
	    
	    
	    System.out.println(Long.parseLong(data));
	   System.out.println(formatter.format(date)); 
	   
	   try {
		System.out.println(formatter.parse(data));
	} catch (java.text.ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} */
		
		
//		F24Controller controller=new F24Controller();
//		controller.callF24(null);
//		
		/*String s="### MOTIVO  PAGAMENTO ####               EL 3914 H 360 2016 45*00 #### EL  3918 H 3 6 0 2016 172*00 #### EL 3961 MO 9 8 2017 3*00 #### ER 3918 D600 2018 243*00  ####      #### IIIIIII  8 ####      L  #### LLLLLL EURO - 1 463*00 #### FINALE ####    (  COMPARE A    /  /    #### 1 10 ####  /   ####  AB\r\n" ;
		StringTokenizer tokens=new StringTokenizer(s, "####");
		
		while (tokens.hasMoreElements()) {
			String token=tokens.nextToken();
			if(token.contains("EURO")) {
				System.out.println(token);
				
				token =token.replaceAll("[^A-Z0-9]","");
				token =token.replaceAll("[A-Z]","");
				token =token.replaceAll("00",".00");

				System.out.println(token);
			}
			
		}*/
		//testing
		/*F24OCRService f24ocrService=new F24OCRService();
		
	Data data=new Data();
	
	
	TextAnnotation annotation=new TextAnnotation();
	
	annotation.setDescription("CODICEFISCALEANAGRAFICI");
	List<TextAnnotation> annotations =new ArrayList<>();
	data.setTextAnnotation(annotations);
		
		try {
			f24ocrService.processJson(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
//		String s="#### HOTIVO DEL PAGAMENTO ####                EL 3918 D 6 1 2 2013 1 967*00 #### EL 3918 I 6 8 4 2013 264*00  ####       #### COPIA PER IL SOGGETTO CHE EFT ETTUA IL VERSAMENTO #### LL SALDO #### FINALE EURO 2 231*00 #### ESTREMI DEL VERSAMENTO  DA COMPILARE A CURA DI BANCA  POSTE  AGENTE DELLA RISCOSSIONE ####        ####     ####  AB CAB";
//		s=s.substring(0, s.indexOf("ESTREMI"));
		
//		for(int i=s.indexOf("ESTREMI");i<s.length();i++) {
//			
//			System.out.print(s.charAt(i));
//			s=s.replace(s.charAt(i), ' ');
//		}
		
//		System.out.println(s);
		
//		NameFinderMETokenFinder tokenFinder = new NameFinderMETokenFinder();
//		String sentence = " **   F 24   DELEGA IRREVOCABILE A MODELLO DI PAGAMENTO UNIFICATO AGENZIA ** PROV PER L ' ACCREDITO ALLA TESORERIA COMPETENTE CONTRIBUENTE **     CODICE FISCALE BRBLRS 4 7 R 30 E 5 1 2 B  ,     DATI ANAGRAFICI BARBIERI ** LORIS      )       3 0 1 0 1 9 4 7 F LEGNAGO ** VR CODICE FISCALE    ,  ,  ,     **   MOTIVO DEL PAGAMENTO  /     .   **         EL 3944 H 5 3 3 0303  2018 43 , 00  ER 3918 D 600  0202  2018 43 , 00 PION     7777777777777777 ** EURO -  86 . 00  \r\n" ; 
//			
//		try {
//			List<Result> f24_SectionDemo = tokenFinder.f24_SectionDemo(sentence);
//			System.out.println(f24_SectionDemo);
//			
//			
//			
//			for (Result result : f24_SectionDemo) {
//				if(result.getKey().equals("Section1")) {
//					System.out.println("Section1:"+result.getValue());
//					
//				}
//				if(result.getKey().equals("Section2")) {
//					System.out.println("Section2:"+result.getValue());
//					
//				}
//			}
//				
//				
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String row="EL 3 9 1 8  H6 0 0 ";
		String pattern = "2:4";
		StringBuffer buffer = new StringBuffer();
		StringTokenizer stringTokenizer = new StringTokenizer(pattern, ":");

		row = row.replaceAll("\\s", "");
		

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

		System.out.println("row:"+buffer.toString());
		
	}
}
