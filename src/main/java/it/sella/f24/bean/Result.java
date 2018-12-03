package it.sella.f24.bean;

public class Result {
	
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Result [key=" + key + ", value=" + value + "]";
	}
	public Result(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

}
