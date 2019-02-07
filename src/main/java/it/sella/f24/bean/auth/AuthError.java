package it.sella.f24.bean.auth;

public class AuthError {
	
	private String code;
	private String description;
	private String params;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	@Override
	public String toString() {
		return "AuthError [code=" + code + ", description=" + description + ", params=" + params + "]";
	}
	
	

}
