package it.sella.f24.bean;

public class Payload {
	private String accessToken;
	private String sessionId;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	@Override
	public String toString() {
		return "Payload [accessToken=" + accessToken + ", sessionId=" + sessionId + "]";
	}
}

