package it.sella.f24.bean.auth;

import java.util.List;

public class Payload {
	
	private String status;
	private String refreshToken;
	private List<AuthParam> authParams;
	private String accessToken;
	private String flowToken;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public List<AuthParam> getAuthParams() {
		return authParams;
	}
	public void setAuthParams(List<AuthParam> authParams) {
		this.authParams = authParams;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getFlowToken() {
		return flowToken;
	}
	public void setFlowToken(String flowToken) {
		this.flowToken = flowToken;
	}
	@Override
	public String toString() {
		return "Payload [status=" + status + ", refreshToken=" + refreshToken + ", authParams=" + authParams
				+ ", accessToken=" + accessToken + ", flowToken=" + flowToken + "]";
	}

}
