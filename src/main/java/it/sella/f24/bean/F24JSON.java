package it.sella.f24.bean;

import java.util.List;

public class F24JSON {
	private String encodedImage;
	private List<Integer> bounds;

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

	public List<Integer> getBounds() {
		return bounds;
	}

	public void setBounds(List<Integer> bounds) {
		this.bounds = bounds;
	}

}
