package it.sella.f24.bean.auth;

import java.util.List;

public class AuthInputPayload {

	private String flowToken;
	private List<AuthParam> data;
	public String getFlowToken() {
		return flowToken;
	}
	public void setFlowToken(String flowToken) {
		this.flowToken = flowToken;
	}
	public List<AuthParam> getData() {
		return data;
	}
	public void setData(List<AuthParam> data) {
		this.data = data;
	}
	
	
}
