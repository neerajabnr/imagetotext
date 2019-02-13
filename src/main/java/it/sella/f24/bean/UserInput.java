package it.sella.f24.bean;

public class UserInput {
	private String fieldName;//Read only -Bold
	private int minLengthofField;//Input box, should accept only numbers
	private int maxLengthofField;//Input box, should accept only numbers
	private boolean isMandatory;//radiobutton, true and false
	private TypeofInput typeofInput;//Dropdown
	private DateFormat dateFormat;//Dropdown, if the type of Input is Date
	private int minWordCount;//Input box, should accept only numbers
	private int maxWordCount;//Input box, should accept only numbers
	
	
	public UserInput() {
		super();
	}
	public UserInput(String fieldName, int minLength, int maxLength, boolean isMandatory, TypeofInput typeofInput,
			DateFormat dateFormat, int minWordCount, int maxWordCount) {
		super();
		this.fieldName = fieldName;
		this.minLengthofField = minLength;
		this.maxLengthofField = maxLength;
		this.isMandatory = isMandatory;
		this.typeofInput = typeofInput;
		this.dateFormat = dateFormat;
		this.minWordCount = minWordCount;
		this.maxWordCount = maxWordCount;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public int getMinLength() {
		return minLengthofField;
	}
	public void setMinLength(int minLength) {
		this.minLengthofField = minLength;
	}
	public int getMaxLength() {
		return maxLengthofField;
	}
	public void setMaxLength(int maxLength) {
		this.maxLengthofField = maxLength;
	}
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public TypeofInput getTypeofInput() {
		return typeofInput;
	}
	public void setTypeofInput(TypeofInput typeofInput) {
		this.typeofInput = typeofInput;
	}
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	public int getMinWordCount() {
		return minWordCount;
	}
	public void setMinWordCount(int minWordCount) {
		this.minWordCount = minWordCount;
	}
	public int getMaxWordCount() {
		return maxWordCount;
	}
	public void setMaxWordCount(int maxWordCount) {
		this.maxWordCount = maxWordCount;
	}
	@Override
	public String toString() {
		return "UserInput [fieldName=" + fieldName + ", minLength=" + minLengthofField + ", maxLength=" + maxLengthofField
				+ ", isMandatory=" + isMandatory + ", typeofInput=" + typeofInput + ", dateFormat=" + dateFormat
				+ ", minWordCount=" + minWordCount + ", maxWordCount=" + maxWordCount + "]";
	}
	
}
