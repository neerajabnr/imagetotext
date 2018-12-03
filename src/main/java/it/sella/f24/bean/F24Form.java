package it.sella.f24.bean;

public class F24Form {
	
	private String encodedImage;
	private String transactionId;
	public String getEncodedImage() {
		return encodedImage;
	}
	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	@Override
	public String toString() {
		return "F24Form [encodedImage=" + encodedImage + ", transactionId=" + transactionId + "]";
	}
}
