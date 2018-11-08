package it.sella.f24.testclasses;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

import it.sella.f24.controller.F24Controller;
import it.sella.f24.service.F24OCRService;
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
		
		
		F24Controller controller=new F24Controller();
		controller.callF24(null);
		
		
		
	}
}
