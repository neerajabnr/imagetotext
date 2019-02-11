package it.sella.f24.component;

import java.util.ArrayList;
import java.util.List;

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
			descriptions.sort(new DescComparator());
		} catch (Exception e) {
//			e.printStackTrace();

			System.out.println("Comparision method violates it's general contract");
		}

		System.out.println("Data Description");

		for (DataDescription dataDescription : descriptions) {

			String desc = dataDescription.getDescription();
			ocrData = ocrData + desc + " ";

		}

		return ocrData;

	}

}