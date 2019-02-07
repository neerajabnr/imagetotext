package it.sella.f24.bean.auth;

import java.util.List;

public class AuthResponse {
	
	private String status;
	private List<AuthError> errors;
	private Payload payload;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	public List<AuthError> getErrors() {
		return errors;
	}
	public void setErrors(List<AuthError> error) {
		this.errors = error;
	}
	@Override
	public String toString() {
		return "AuthResponse [status=" + status + ", errors=" + errors + ", payload=" + payload + "]";
	}
	
	

}
