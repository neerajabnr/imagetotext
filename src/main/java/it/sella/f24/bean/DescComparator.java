package it.sella.f24.bean;

import java.util.Comparator;

public class DescComparator implements Comparator<DataDescription> {

	@Override
	public int compare(DataDescription des1, DataDescription des2) {

		double result = des1.getyStart() - des2.getyStart();
		try {
			if (result < 10 && result > -10) {
				return des1.getxStart() - des2.getxStart();
				
			} else {
				return des1.getyStart() - des2.getyStart();	
			}
		}catch (Exception e) {
			
		}

		return 0;

		// int result=des1.getxStart();
		//
		// return des1.getxStart()-des2.getxStart();
		//
		// /*if (des1.getyStart() > des2.getyStart()) {
		// System.out.println(true);
		// return 1;
		// } else {
		// return 0;
		// }*/
	}

}
