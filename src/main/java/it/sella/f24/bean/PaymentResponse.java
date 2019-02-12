package it.sella.f24.bean;

import java.util.List;

public class PaymentResponse {
	
	private String executionDate;
	private String documentAmount;
	private String currency;
	private String officeCode;
	private String deedCode;
	private String operationCode;
	private Payer payer;
	private PayerAgent payerAgent;
	private List<TaxList> taxList;
	
	

}
