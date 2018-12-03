package it.sella.f24.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sella.f24.bean.DataDescription;

public class FetchData {

	
	@SuppressWarnings("rawtypes")
	public Map getData(List<DataDescription> list ) throws Exception{
		
		boolean fis = false;
		boolean found = false;
		boolean foundDOB = false;
		StringBuffer codice = new StringBuffer();
		StringBuffer dob = new StringBuffer();
		int xprev = 0;
		int s1=0,s2=0;
		List<List<DataDescription>> sec1 = new ArrayList<>();
		List<List<DataDescription>> sec2 = new ArrayList<>();
		StringBuffer section1 = new StringBuffer();
		StringBuffer section2 = new StringBuffer();
		List<DataDescription> section1List = new ArrayList<>();
		List<DataDescription> section2List = new ArrayList<>();
		
		
		for (DataDescription dat : list) {

			if (dat.getDescription().equals("CODICE")) {
				s1 = 1;
			} else if (dat.getSection().equals("two")) {
				s1 = 0;
				s2 = 1;
			}

			if (s1 == 1) {
				if (dat.getxStart() < xprev) {
					sec1.add(section1List);
					section1.append("\n");
					section1List = new ArrayList<>();
				} else {
					section1.append(" ");
				}
				section1List.add(dat);
				section1.append(dat.getDescription());
				xprev = dat.getxStart();
			} else if (s2 == 1) {
				if (dat.getxStart() < xprev) {
					sec2.add(section2List);
					section2 = new StringBuffer();
					section2.append("\n");
					section2List = new ArrayList<>();
				} else {
					section2.append(" ");
				}
				section2List.add(dat);
				section2.append(dat.getDescription());
				xprev = dat.getxStart();
			}
		}
		
		boolean line2=false;
		Map<String, String> dataMap = new HashMap<>();
		for (List<DataDescription> dd : sec1) {
			boolean foundAna = false;
			if (found) {
				String cod = codice.toString().replace(",", "");
				dataMap.put("codice", cod.substring(0, 16));
				System.out.println(cod.substring(0, 16));
				found = false;
				line2=true;
			}

			if (foundDOB) {
				int q=0;
				StringBuffer dob2 = new StringBuffer();
				for(char c: dob.toString().toCharArray()) {
					if(startswithInt(Character.toString(c))){
						dob2.append(c);
						q++;
					} else if(q<7) {
						
					} else{
						dob2.append(c);
					}
				}
				dataMap.put("dob", dob2.substring(0, 8));
				dataMap.put("sex",  dob2.substring(8, 9));
				dataMap.put("city", dob2.substring(9, dob2.length()));
				
				System.out.println("DOB: " + dob2.substring(0, 8));
				System.out.println("Sex: " + dob2.substring(8, 9));
				
				
				if(dob2.substring(9, dob2.length()).equals("LEGNAGOVR")){
					dataMap.put("city", dob2.substring(9, dob2.length()-2));
					dataMap.put("prov", dob2.substring(dob2.length()-2, dob2.length()));
				} else if(dob2.substring(9, dob2.length()).equals("BIELLA")){
					dataMap.put("city", dob2.substring(9, dob2.length()-2));
					dataMap.put("prov", dob2.substring(dob2.length()-2, dob2.length()));
				}
				System.out.println("City: " + dob2.substring(9, dob2.length()));
				
				foundDOB = false;
			}
			StringBuffer anaBuf = new StringBuffer();
			for (DataDescription d : dd) {
				if(line2 && !d.getDescription().equals("DATI")) {
				System.out.println(d.getDescription()+"**********");
				dataMap.put("anagrafici", d.getDescription());
				line2=false;
				}
				if (!fis && d.getDescription().contains("FISCALE")) {
					System.out.print("Codice: ");
					found = true;
					fis = true;
				} else if (found) {
					codice.append(d.getDescription());
				} else if (d.getDescription().contains("ANAGRAFICI")) {
					System.out.print("ANAGRAFICI: ");
					xprev = d.getxEnd();
					foundAna = true;
					line2=false;
				} else if (foundAna) {
					if ((d.getxStart() - xprev) < 300) {
						anaBuf.append(d.getDescription());
						anaBuf.append(" ");
						System.out.println(d.getDescription());
					} else {
						if(dataMap.get("anagrafici")==null || dataMap.get("anagrafici").equals(""))
						dataMap.put("anagrafici", anaBuf.toString());
						dataMap.put("name", d.getDescription());
						System.out.println("Name: " + d.getDescription());
					}
					xprev = d.getxEnd();
				} else if (foundDOB || startswithInt(d.getDescription())) {
					foundDOB = true;
					dob.append(d.getDescription());
				}
			}
		}
		
		for (List<DataDescription> dd : sec2) {
			System.out.println();
			int i=1;
			StringBuffer buf = new StringBuffer();
			for (DataDescription d : dd) {
				buf.append(d.getDescription());
			}
			System.out.println(buf.toString());
			if(buf.toString().startsWith("EL") ||buf.toString().startsWith("ER") ||buf.toString().startsWith("RG") ){
				dataMap.put("sezione"+i, buf.toString().substring(0, 2));
				dataMap.put("tributo"+i, buf.toString().substring(2, 6));
				dataMap.put("ente"+i, buf.toString().substring(6, 10));
				dataMap.put("mese"+i, buf.toString().substring(10, 14));
				dataMap.put("year"+i, buf.toString().substring(14, 18));
				dataMap.put("debito"+i, buf.toString().substring(18, buf.toString().length()));
			}
			i++;
			
		}
		
		
		System.out.println(dataMap);
		return dataMap;
	}
	

	private static boolean startswithInt(String s) {
		if(s.equals("")){
			return false;
		}
		boolean ret = true;
		try {

			Double.parseDouble(s.substring(0, 1));

		} catch (NumberFormatException e) {
			ret = false;
		}
		return ret;
	}
}