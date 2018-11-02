package it.sella.f24.testclasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import it.sella.f24.controller.F24Controller;
import it.sella.f24.service.F24OCRService;
import it.sella.f24.service.GoogleService;
import it.sella.f24.service.opennlp.Data;
import it.sella.f24.service.opennlp.F24JSON;



public class FileReaderfromFolder {
	
	private static Logger logger = null;
	static {
		logger = Logger.getLogger(F24OCRService.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
	
	private F24OCRService f24ocrService=new F24OCRService();
	private GoogleService googleService = new GoogleService();
	private F24Controller controller=new F24Controller();
	
	
	/*public static void main(String[] args) {
	      File folder = new File("//home//bsindia//Documents//images");
	      FileReaderfromFolder listFiles = new FileReaderfromFolder();
	      System.out.println("reading files before Java8 - Using listFiles() method");
	      listFiles.listAllFiles(folder);
	      System.out.println("-------------------------------------------------");
	      System.out.println("reading files Java8 - Using Files.walk() method");
//	      listFiles.listAllFiles("//home//bsindia//Documents//images");

	     }*/
	     // Uses listFiles method  
	
	
	public static void main(String[] args) {
		FileReaderfromFolder fileReaderfromFolder=new FileReaderfromFolder();
		fileReaderfromFolder.getFilesfromFolder();
	}
	
		public List<String> getFilesfromFolder(){
			File folder = new File("//home//bsindia//Documents//images");
		      FileReaderfromFolder listFiles = new FileReaderfromFolder();
		      System.out.println("reading files before Java8 - Using listFiles() method");
		      return listFiles.listAllFiles(folder);
			
		}
	     public List<String> listAllFiles(File folder){
	    	 
	         System.out.println("In listAllfiles(File) method");
	         File[] fileNames = folder.listFiles();
	         List<String> list =new ArrayList<String>();
	         String filepath="";
	         for(File file : fileNames){
	             // if directory call the same method again
	             if(file.isDirectory()){
	                 listAllFiles(file);
	             }else{
	                 try {
	                     filepath=readContent(file);
	                     list.add(filepath);
	                 } catch (IOException e) {
	                     // TODO Auto-generated catch block
	                     e.printStackTrace();
	                 }
	        
	             }
	         }
	         for (String string : list) {
				System.out.println(string);
			}
	         return list;
	     }
	     // Uses Files.walk method   
	     public void listAllFiles(String path){
	         System.out.println("In listAllfiles(String path) method");
	         try(Stream<Path> paths = Files.walk(Paths.get(path))) {
	             paths.forEach(filePath -> {
	                 if (Files.isRegularFile(filePath)) {
	                     try {
	                         System.out.println(filePath);
	                         
	                     } catch (Exception e) {
	                         // TODO Auto-generated catch block
	                         e.printStackTrace();
	                     }
	                 }
	             });
	         } catch (IOException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         } 
	     }
	     
	     public String readContent(File file) throws IOException{
//	         System.out.println("read file " + file.getCanonicalPath() );
	         
	         String temp=file.getCanonicalPath();
	         
	        return temp;
	         
//	         System.out.println(temp);
//	         logger.info("Image:" +file.getName());
//	         Data data = googleService.readText(new File(temp), "");	
	         
	         /*try(BufferedReader br  = new BufferedReader(new FileReader(file))){
	               String strLine;
	               // Read lines from the file, returns null when end of stream 
	               // is reached
	               while((strLine = br.readLine()) != null){
	                System.out.println("Line is - " + strLine);
	               }
	         }*/
	     }
	     
	     public void readContent(Path filePath) throws IOException{
	         System.out.println("read file " + filePath);
	         List<String> fileList = Files.readAllLines(filePath);
	         System.out.println("" + fileList);
	     }
}
