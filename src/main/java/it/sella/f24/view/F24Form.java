package it.sella.f24.view;

public class F24Form {
	
	private String encoded_string;
	private String transactionId;
	public String getEncoded_string() {
		return encoded_string;
	}
	public void setEncoded_string(String encoded_string) {
		this.encoded_string = encoded_string;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	@Override
	public String toString() {
		return "F24Form [encoded_string=" + encoded_string + ", transactionId=" + transactionId + "]";
	}
	
}
