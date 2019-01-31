package it.sella.f24.bean;

import java.util.Comparator;

public class DescComparator implements Comparator<DataDescription> {

	@Override
	public int compare(DataDescription des1, DataDescription des2) {

		int result = des1.getyStart() - des2.getyStart();
		try {
			
			if (result < 10 && result > -10) {
				result = des1.getxStart() - des2.getxStart();
			} else {
				result = des1.getyStart() - des2.getyStart();
			}
		}catch (Exception e) {
			System.out.println("des1.getyStart()"+des1.getyStart());
			System.out.println("des2.getyStart()"+des2.getyStart());
			System.out.println("des1.getxStart()"+des1.getxStart());
			System.out.println("des2.getxStart()"+des2.getxStart());
			
			System.out.println("des1.getDescription()"+des1.getDescription()+" "+"des2.getDescription()"+des2.getDescription());
		}

		return result;

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
