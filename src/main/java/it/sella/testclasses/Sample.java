package it.sella.testclasses;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sample {

	public static void main(String[] args) {
		
		String s="**,:E L3918 H553";
		s=s.replaceAll("[^a-zA-Z0-9\\s]", "");
		    
		    StringBuffer buffer=new StringBuffer(s);
		    //Example: EL13918H553
		    Pattern pattern1 = Pattern.compile("[A-Z]{2}[0-9]{5}");
		    //Example: EL3918H553,ELI3918H553,EIL3918H553
	        Pattern pattern2 = Pattern.compile("[A-Z][A-Z][0-9]{4}");
	        //Example: E1L3918H553
	        Pattern pattern3 = Pattern.compile("[0-9][A-Z][0-9]{4}");
	        Pattern pattern4 = Pattern.compile("[0-9][A-Z][0-9]{5}");
	        Pattern pattern5 = Pattern.compile("[A-Z][0-9]{5}");
	        
	        Matcher matcher1 = pattern1.matcher(s);
	        Matcher matcher4 = pattern4.matcher(s);
	        Matcher matcher2 = pattern2.matcher(s);
	        Matcher matcher3 = pattern3.matcher(s);
	        Matcher matcher5 = pattern5.matcher(s);
	        
	        	if (matcher1.find()) {
	        		System.out.println("matcher1");
	        		
	        		System.out.println("Pattern found from " + matcher1.start() + 
	        				" to " + (matcher1.end()-1)); 
	        		
	        		buffer.insert(matcher1.start()+3, " ");
	        	} else if (matcher4.find()) {
	        		System.out.println("matcher4");
	        		
	        		System.out.println("Pattern found from " + matcher4.start() + 
	        				" to " + (matcher4.end()-1)); 
	        		
	        		buffer.insert(matcher4.start()+3, " ");
	        	} else if (matcher2.find()) {
	        		System.out.println("matcher2");
	        		
	        		System.out.println("Pattern found from " + matcher2.start() + 
	        				" to " + (matcher2.end()-1)); 
	        		
	        		buffer.insert(matcher2.start()+2, " ");
	        	}else if (matcher3.find()) {
	        		System.out.println("matcher3");
	        		
	        		System.out.println("Pattern found from " + matcher3.start() + 
	        				" to " + (matcher3.end()-1)); 
	        		
	        		buffer.insert(matcher3.start()+2, " ");
	        	}else if (matcher5.find()) {
	        		System.out.println("matcher5");
	        		
	        		System.out.println("Pattern found from " + matcher5.start() + 
	        				" to " + (matcher5.end()-1)); 
	        		
	        		buffer.insert(matcher5.start()+2, " ");
	        	}else {
	        		System.out.println("No Match");
	        	}
	        	
	        	System.out.println(buffer);
	        
//	        System.out.println(matcher2.find());
//
//		
//
//		// Pattern testPattern = Pattern.compile("test");
//		//
//		// String sentence[] = new String[] { "a", "test", "b", "c" };
//		//
//		// Pattern[] patterns = new Pattern[] { testPattern };
//		// Map<String, Pattern[]> regexMap = new HashMap<>();
//		// String type = "Neeraja";
//		//
//		// regexMap.put(type, patterns);
//		//
//		// RegexNameFinder finder = new RegexNameFinder(regexMap);
//		//
//		// Span[] result = finder.find(sentence);
//		//
//		// System.out.println(result[0].getType());
//
//		String s = "######ELI3944H #####5 3 30303 2018 43 , 00";
//
//		String scopy=s.replaceAll("\\W+", "");
//		
//		int len=0;
//		
////		String Seizone="";
////		System.out.println(scopy);
////		// EL 3944 H 5 3 3 0303 2018 43 , 00
////		int countofAlpha=0;
////		for(int i=0;i<3;i++) {
////			Character character = scopy.charAt(i);
////			if(StringUtils.isAlphanumeric(character.toString())) {
////				countofAlpha++;
////				Seizone=Seizone+scopy.charAt(i);
////			}
////		}
//		
//		
//		Sample sample=new Sample();
//		
////		Seizone=sample.removeChar(Seizone);
//		
//		
////		if(countofAlpha==2) {
////			len=10;
////		}else if(countofAlpha==3) {
////			len=11;
////		}
//		
//
//		String anotherString = "";
//		int index = 0;
//		for (int j = 0; j < s.length(); j++) {
//
//			Character character = s.charAt(j);
//			if (StringUtils.isAlphanumeric(character.toString()) && anotherString.length() < 11) {
//				anotherString = anotherString + character;
//				index = j;
//			}
//		}
//
//		
//		String replace=anotherString.substring(0,3);
//		
//		replace=sample.removeChar(replace);
//		anotherString=anotherString.replace(anotherString.substring(0, 3),replace+" " );
//
//		String pattern = "2:4:4";
//
//		anotherString = anotherString.replaceAll("\\s", "");
//
//		System.out.println(anotherString);
//
//		StringTokenizer stringTokenizer2 = new StringTokenizer(pattern, ":");
//
//		// 1,2; prevToken2+4
//
//
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(anotherString);
//
//		int count = 0, i = 0;
//		while (stringTokenizer2.hasMoreTokens()) {
//			String token = stringTokenizer2.nextToken();
//
//			buffer.insert(Integer.parseInt(token) + count + i, " ");
//
//			count = count + Integer.parseInt(token);
//			i++;
//		}
//
//		System.out.println(buffer);
//
//		if (s.charAt(index + 1) == ' ') {
//			s = buffer.toString() + s.substring(index + 2, s.length());
//		} else {
//			s = buffer.toString() + s.substring(index + 1, s.length());
//		}
//
//		System.out.println(s);
//
//	
	}
	
	public String removeChar(String test){
		String constants = "EL:ER:RG:RL";


		String extraChar = "";

		StringTokenizer stringTokenizer = new StringTokenizer(constants, ":");

		while (stringTokenizer.hasMoreTokens()) {
			String constant = stringTokenizer.nextToken();
			int count = 0;
			for (int i = 0; i < test.length(); i++) {
				if (constant.indexOf(test.charAt(i)) == -1) {
					// System.out.println("Char:"+test.charAt(i));
					extraChar = String.valueOf(test.charAt(i));
				} else {
					count++;
				}

				if (count == 2) {
					test = test.replace(extraChar, "");
				}
			}
		}

		System.out.println("test:----" + test);
		
		return test;
	}

}
