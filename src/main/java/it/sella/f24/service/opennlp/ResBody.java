package it.sella.f24.service.opennlp;

import java.util.List;

public class ResBody {
	private String status;
	private List<String> errors;
	private Payload payload;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	@Override
	public String toString() {
		return "ResBody [status=" + status + ", errors=" + errors + ", payload=" + payload + "]";
	}

}


