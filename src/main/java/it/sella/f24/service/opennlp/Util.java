package it.sella.f24.service.opennlp;

public class Util {
	
	public static void main(String[] args) {
		int xd,xc;
		xc=611;
		
		for(xd=509;xd<=620||xc<=726;xd++){
			System.out.println("OPERAZIONE  x: 439  y: 346 Sezione  x: 40  y: 359 cod  x: 87  y: 359 ,  "
					+ "x: 101  y: 359 tributo  x: 105  y: 359 codice  x: 139  y: 360 entew  x: 165  y: 360 ww  "
					+ "x: 208  y: 361 riferimento  x: 361  y: 367 anno  x: 367  y: 358 di  x: 387  y: 358 "
					+ "<START:Sezione> v1 <END>  x: 47  y: 375 <START:tributo> v2 <END>  x: 95  y: 373 "
					+ "<START:codice> v3 <END>  x: 145  y: 374 detrazione  x: 441  y: 366 importi  "
					+ "x: 521  y: 365 a  x: 549  y: 365 dobito  x: 557  y: 365 versati  x: 582  y: 366 "
					+ "importi  x: 615  y: 366 a  x: 644  y: 366 credito  x: 651  y: 367 compensati "
					+ " x: 678  y: 367 2018  x: 366  y: 377 <START:dobito> v6 <END> + x: "+ xd +" y: 378 ,  "
					+ "x: 536  y: 378 00  x: 539  y: 378 <START:tributo> v2 <END>  x: 95  y: 390"
					+ " <START:codice> v3 <END>  x: 144  y: 390 2018  x: 366  y: 393 "
					+ "<START:credito> v6 <END>  x: "+xc+"  y: 393 ,  x: 640  y: 393 00  x: 646  "
					+ "y: 394 Dund  x: 183  y: 528 Autodeto  x: 436  y: 537 COS343  x: 505  y: 538 \n");
			xc++;
		}
	}

}
