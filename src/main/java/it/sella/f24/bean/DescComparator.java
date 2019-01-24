package it.sella.f24.bean;

import java.util.Comparator;

public class DescComparator implements Comparator<DataDescription> {

	@Override
	public int compare(DataDescription des1, DataDescription des2) {
		
		int result=des1.getyStart()-des2.getyStart();
		
		if(result<10&&result>-10) {
			result=des1.getxStart()-des2.getxStart();
		}else {
			result=des1.getyStart()-des2.getyStart();
		}
		
		
		
		return result;
		
//		int result=des1.getxStart();
//		
//		return des1.getxStart()-des2.getxStart();
//
//		/*if (des1.getyStart() > des2.getyStart()) {
//			System.out.println(true);
//			return 1;
//		} else {
//			return 0;
//		}*/
	}

}
