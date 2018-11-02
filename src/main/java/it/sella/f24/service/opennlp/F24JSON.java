package it.sella.f24.service.opennlp;

public class F24JSON {
	private String encodedImage;

	public String getEncodedImage() {
		return encodedImage;
	}

	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}

	@Override
	public String toString() {
		return "F24JSON [encodedImage=" + encodedImage + "]";
	}

}
