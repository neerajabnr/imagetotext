package it.sella.f24.bean.auth;

public class AuthParam {
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
		return "AuthParam [key=" + key + ", value=" + value + "]";
	}
	
	

}
