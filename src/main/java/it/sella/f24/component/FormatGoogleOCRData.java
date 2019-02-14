package it.sella.f24.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
			
			//if(StringUtils.isAlpha(desc)) {
				//desc="";
			//}
			//if(xstart-xprevend<3) {
				//ocrData=ocrData+" # "+desc+" ";
			//}
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
		data = data.replaceAll("10 %", "");
		data = data.replaceAll("21 %", "");
		return data;
	}
}
