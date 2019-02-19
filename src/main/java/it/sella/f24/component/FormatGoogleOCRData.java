package it.sella.f24.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.DataDescription;
import it.sella.f24.bean.DescComparator;
import it.sella.f24.bean.TextAnnotation;

@Component
public class FormatGoogleOCRData {

	public String getImageText(Data data) {

		// logger.info("before processing" + data);

		String ocrData = "";

		List<DataDescription> descriptions = new ArrayList<>();
		for (TextAnnotation txtAnn : data.getTextAnnotation()) {
			if (txtAnn.getLocale() == null || txtAnn.getLocale().isEmpty()) {
				DataDescription d = new DataDescription(txtAnn.getDescription(),
						txtAnn.getBoundingPoly().getVertices().get(0).getX(),
						txtAnn.getBoundingPoly().getVertices().get(0).getY(),
						txtAnn.getBoundingPoly().getVertices().get(1).getX(),
						txtAnn.getBoundingPoly().getVertices().get(3).getY());
				d.setDifference(0);
				descriptions.add(d);
			}
		}
		try {
			//Comparator<DataDescription> descComp = (DataDescription d1, DataDescription d2)-> {if(d1.getyStart().equals(d2.getyStart())) else d1.getyStart() - d2.getyStart()}
				
			
			descriptions.sort((d1,d2)->{
				double res = d1.getyEnd()-d2.getyEnd();
				if(res>-10&&res<10) {
					return d1.getxStart() - d2.getxStart();
				}
				else
				return d1.getyEnd() - d2.getyEnd();
			});
		} catch (Exception e) {
//			e.printStackTrace();

			System.out.println("Comparision method violates it's general contract");
		}

		System.out.println("Data Description");
		int xstart = descriptions.get(0).getxStart();
		int yend= descriptions.get(0).getyEnd();
		int curyend;
		int curxend;
		int xprevend = 0;
		//int xprevstart = descriptions.get(0).getxStart();
		boolean appendstar=false;

		for (DataDescription dataDescription : descriptions) {
			if(appendstar) {
				//yend = dataDescription.getyEnd();
				//xstart = dataDescription.getxStart();
				appendstar = false;
			}
			curyend = dataDescription.getyEnd();
			curxend = dataDescription.getxEnd();
			xstart = dataDescription.getxStart();
			String desc = dataDescription.getDescription();
			if(xprevend-xstart>0) {
				//double res = curyend-yend;
				///if(res>-5&&res<5) {
					appendstar = true;
										
				//}
				
			}
			
//			if(StringUtils.isAlpha(desc)) {
//				desc="";
//			}
//			
			if(appendstar) {
				ocrData = ocrData +" ** "+ desc + " ";
			}
			
			else
			ocrData = ocrData + desc + " ";
			
			xprevend = dataDescription.getxEnd();

		}
		ocrData = removeData(ocrData);
		return ocrData;

	}
	
	private static String removeData(String data) {
		//data = data.replaceAll("[A-Z]{1,}|[a-z]{1,}", "");
		data = data.replaceAll("[A-Za-z]", "");
		data = data.replaceAll("10 %", "");
		data = data.replaceAll("21 %", "");
		return data;
	}
	
	public String getImageText_new(Data data) {

		// logger.info("before processing" + data);

		String ocrData = "";

		List<DataDescription> descriptions = new ArrayList<>();
		for (TextAnnotation txtAnn : data.getTextAnnotation()) {
			if (txtAnn.getLocale() == null || txtAnn.getLocale().isEmpty()) {
				DataDescription d = new DataDescription(txtAnn.getDescription(),
						txtAnn.getBoundingPoly().getVertices().get(0).getX(),
						txtAnn.getBoundingPoly().getVertices().get(0).getY(),
						txtAnn.getBoundingPoly().getVertices().get(1).getX(),
						txtAnn.getBoundingPoly().getVertices().get(3).getY());
				d.setDifference(0);
				descriptions.add(d);
			}
		}
		try {
			//Comparator<DataDescription> descComp = (DataDescription d1, DataDescription d2)-> {if(d1.getyStart().equals(d2.getyStart())) else d1.getyStart() - d2.getyStart()}
				
			
			descriptions.sort((d1,d2)->{
				double res = d1.getyEnd()-d2.getyEnd();
				if(res>-10&&res<10) {
					return d1.getxStart() - d2.getxStart();
				}
				else
				return d1.getyEnd() - d2.getyEnd();
			});
		} catch (Exception e) {
//			e.printStackTrace();

			System.out.println("Comparision method violates it's general contract");
		}

		int count=0;
		
		Map<Integer, List<DataDescription>> dataMap = new HashMap<Integer, List<DataDescription>>();
		
		
		int count1=1;
		int count2=1;
		int count3 =1;
		int count4=1;
		int count5=1;
		for (DataDescription dataDescription : descriptions) {
			
			if(dataDescription.getDescription().equals("Rendita")||dataDescription.getDescription().equals("endita"))
				count++;
			if(count==1) {
				
				if(dataDescription.getDescription().equals("emergenza")||dataDescription.getDescription().equals("emergenza"))
					count1++;
				
				if(count1==1) {
					
					this.addToMap(1, dataDescription, dataMap);
				}
				else if(count1==2) {
					this.addToMap(2, dataDescription, dataMap);				}
				
				//System.out.println("");
				//blockOne.add(dataDescription);
			}
			else if(count==2) {
				
				
				if(dataDescription.getDescription().equals("emergenza")||dataDescription.getDescription().equals("emergenza"))
					count2++;
				if(count2==1) {
					this.addToMap(3, dataDescription, dataMap);				}
				else if(count2==2) {
					this.addToMap(4, dataDescription, dataMap);				}
				
				//blockTwo.add(dataDescription);
			}
			else if(count==3) {
				
				
				if(dataDescription.getDescription().equals("emergenza")||dataDescription.getDescription().equals("emergenza"))
					count3++;
				if(count3==1) {
					this.addToMap(5, dataDescription, dataMap);				}
				else if(count3==2) {
					this.addToMap(6, dataDescription, dataMap);				}
				
				//blockThree.add(dataDescription);
			}
			else if(count==4) {
				
				if(dataDescription.getDescription().equals("emergenza")||dataDescription.getDescription().equals("emergenza"))
					count4++;
				if(count4==1) {
					this.addToMap(7, dataDescription, dataMap);				}
				else if(count4==2) {
					this.addToMap(8, dataDescription, dataMap);				}
				
				//blockFour.add(dataDescription);
			}
			else if(count==5) {
				
				if(dataDescription.getDescription().equals("emergenza")||dataDescription.getDescription().equals("emergenza"))
					count5++;
				if(count5==1) {
					this.addToMap(9, dataDescription, dataMap);				}
				else if(count5==2) {
					this.addToMap(10, dataDescription, dataMap);				}
				
				//blockFive.add(dataDescription);
			
			}
			ocrData =	ocrData+" "+ dataDescription.getDescription();
		}
	
		System.out.println(this.processMap(dataMap));
		return ocrData;

	}
	
	/*private void print(List<DataDescription> block) {
		for(DataDescription desc  : block) {
			System.out.println(desc.getDescription()+" ");
		}
	}
	
	private void split(List<DataDescription> block) {
		
		for(DataDescription desc  : block) {
			System.out.println(desc.getDescription()+" ");
		}
	}*/
	
	private void addToMap(Integer key, DataDescription value,Map<Integer, List<DataDescription>> dataMap)
	{
		if(Objects.nonNull(dataMap.get(key))) {
			List<DataDescription> dataList = dataMap.get(key);
			dataList.add(value);
		}
		else
		{
			List<DataDescription> dataList = new ArrayList<>();
			dataList.add(value);
			dataMap.put(key, dataList);
		}
	}
	
	private String[] processMap(Map<Integer, List<DataDescription>> dataMap) {
		String[] lines = new String[10];
		
		for(int i=1;i<=10;i++) {
			StringBuilder temp = new StringBuilder();
			List<DataDescription> descriptions = dataMap.get(i);
			for(DataDescription desc : descriptions) {
				if(StringUtils.isNumeric(desc.getDescription())||(StringUtils.isAlphanumeric(desc.getDescription())&&!desc.getDescription().contains("RB"))) {
					temp.append(desc.getDescription());
					temp.append("**");
				}
			}
			lines[i-1]=temp.toString();
		}
		
		return lines;
	}
	
}
