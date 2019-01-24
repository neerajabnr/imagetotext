package it.sella.f24.bean;

import java.util.Comparator;

public class DescXComparator implements Comparator<DataDescription>{

	@Override
	public int compare(DataDescription description1, DataDescription description2) {
		
//		description1.getxStart() = txtAnn.getBoundingPoly().getVertices().get(0).getX();
//		if ((description1.getxStart() - description2.getxStart()) > 250) {
//			
//		}
		return description1.getxStart() - description2.getxStart();
	}

}
