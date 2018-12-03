package it.sella.f24.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
   "textAnnotations"
})
public class Data {
	
	@JsonProperty("textAnnotations")
	private List<TextAnnotation> textAnnotation;
	
	/*@JsonProperty("textAnnotations")
	private List<LabelAnnotation> labelAnnotations;
	
	@JsonProperty("textAnnotations")
	private List<SafeSearchAnnotation> safeSearchAnnotations;
	
	@JsonProperty("textAnnotations")
	private List<ImagePropertiesAnnotation> imagePropertiesAnnotations;
	
	@JsonProperty("textAnnotations")
	private List<CropHintsAnnotation> cropHintsAnnotations;
	
	@JsonProperty("textAnnotations")
	private List<FullTextAnnotation> fullTextAnnotations;
	
	@JsonProperty("textAnnotations")
	private List<WebDetection> webDetections;*/

	@JsonProperty("textAnnotations")
	public List<TextAnnotation> getTextAnnotation() {
		return textAnnotation;
	}

	@JsonProperty("textAnnotations")
	public void setTextAnnotation(List<TextAnnotation> textAnnotation) {
		this.textAnnotation = textAnnotation;
	}

	@Override
	public String toString() {
		return "Data [textAnnotation=" + textAnnotation + "]";
	}
	

	
	
	

}
