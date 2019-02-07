package it.sella.f24.bean.auth;

public class AuthServiceInput {
	
	private String userName;
	private String password;
	private String apiKey;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@Override
	public String toString() {
		return "AuthServiceInput [userName=" + userName + ", password=" + password + "]";
	}
	

}
