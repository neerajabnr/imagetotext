package it.sella.testclasses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Sample2 {
	
	public static void main(String[] args) {
		    //Example: EL13918H553
//		    Pattern pattern1 = Pattern.compile("[A-Z][0-9]{5}");//3
//		    //Example: EL3918H553,ELI3918H553,EIL3918H553
//	        Pattern pattern2 = Pattern.compile("[A-Z][A-Z][0-9]{4}");//1
//	        //Example: E1L3918H553
//	        Pattern pattern3 = Pattern.compile("[0-9][A-Z][0-9]{4}");//2
//	        Pattern pattern4 = Pattern.compile("[\\s]{0,1}[A-Z][0-9]{4}");
		    
			String s = "**,:EL3918 H5534";
			s = s.replaceAll("[^a-zA-Z0-9\\s]", "");

			StringBuffer buffer = new StringBuffer(s);
		    
		    Pattern pattern1 = Pattern.compile("[0-9]{5} ");//3
	        Pattern pattern2 = Pattern.compile("[A-Z][0-9]{4} ");//1
	        
	        Matcher matcher1 = pattern1.matcher(s);
	        Matcher matcher2 = pattern2.matcher(s);
	        
	        
	        if (matcher1.find()) {
        		System.out.println("matcher1");
        		
        		System.out.println("Pattern found from " + matcher1.start() + 
        				" to " + (matcher1.end()-1)); 
        		
        		buffer.insert(matcher1.start()+1, " ");
        	} else if (matcher2.find()) {
        		System.out.println("matcher2");
        		
        		System.out.println("Pattern found from " + matcher2.start() + 
        				" to " + (matcher2.end()-1)); 
        		
        		buffer.insert(matcher2.start()+1, " ");
        	}else {
        		System.out.println("No match");
        	}
	        
	        System.out.println(buffer);
	        
	        
	        int count=0;
			for(int i=0;i<s.length();i++) {
				Character character=s.charAt(i);
				if(StringUtils.isAlphanumeric(character.toString())) {
					count++;
				}else {
					continue;
				}
			}
			System.out.println(count);

	}

}
