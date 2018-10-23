package it.sella.f24.testclasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;

import it.sella.f24.service.F24OCRService;

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
		
		String v4="CODICE12345678";
		
		if(v4.length()>7) {
	if(StringUtils.isNumeric(v4.substring(0, 7))) {
		v4=v4.substring(0,7);
	}else if(StringUtils.isNumeric(v4.substring(v4.length()-8, v4.length()-1))) {
		v4=v4.substring(v4.length()-8, v4.length());
	}
	
	System.out.println(v4);
		
	}
	}
}
