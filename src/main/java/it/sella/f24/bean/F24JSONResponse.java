
package it.sella.f24.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SuppressWarning",
    "accountNumber",
    "executionDate",
    "documentAmount",
    "currency",
    "officeCode",
    "deedCode",
    "operationCode",
    "payer",
    "payerAgent",
    "taxList"
})
public class F24JSONResponse {

    @JsonProperty("SuppressWarning")
    private String suppressWarning;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("executionDate")
    private String executionDate;
    @JsonProperty("documentAmount")
    private Double documentAmount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("officeCode")
    private String officeCode;
    @JsonProperty("deedCode")
    private String deedCode;
    @JsonProperty("operationCode")
    private String operationCode;
    @JsonProperty("payer")
    private Payer payer;
    @JsonProperty("payerAgent")
    private PayerAgent payerAgent;
    @JsonProperty("taxList")
    private List<TaxList> taxList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SuppressWarning")
    public String getSuppressWarning() {
        return suppressWarning;
    }

    @JsonProperty("SuppressWarning")
    public void setSuppressWarning(String suppressWarning) {
        this.suppressWarning = suppressWarning;
    }

    @JsonProperty("accountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("accountNumber")
    public void setAccountNumber(String accountID) {
        this.accountNumber = accountID;
    }

    @JsonProperty("executionDate")
    public String getExecutionDate() {
        return executionDate;
    }

    @JsonProperty("executionDate")
    public void setExecutionDate(String executionDate) {
        this.executionDate = executionDate;
    }

    @JsonProperty("documentAmount")
    public Double getDocumentAmount() {
        return documentAmount;
    }

    @JsonProperty("documentAmount")
    public void setDocumentAmount(Double documentAmount) {
        this.documentAmount = documentAmount;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("officeCode")
    public String getOfficeCode() {
        return officeCode;
    }

    @JsonProperty("officeCode")
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    @JsonProperty("deedCode")
    public String getDeedCode() {
        return deedCode;
    }

    @JsonProperty("deedCode")
    public void setDeedCode(String deedCode) {
        this.deedCode = deedCode;
    }

    @JsonProperty("operationCode")
    public String getOperationCode() {
        return operationCode;
    }

    @JsonProperty("operationCode")
    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    @JsonProperty("payer")
    public Payer getPayer() {
        return payer;
    }

    @JsonProperty("payer")
    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    @JsonProperty("payerAgent")
    public PayerAgent getPayerAgent() {
        return payerAgent;
    }

    @JsonProperty("payerAgent")
    public void setPayerAgent(PayerAgent payerAgent) {
        this.payerAgent = payerAgent;
    }

    @JsonProperty("taxList")
    public List<TaxList> getTaxList() {
        return taxList;
    }

    @JsonProperty("taxList")
    public void setTaxList(List<TaxList> taxList) {
        this.taxList = taxList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	@Override
	public String toString() {
		return "F24JSONResponse [suppressWarning=" + suppressWarning + ", accountNumber=" + accountNumber
				+ ", executionDate=" + executionDate + ", documentAmount=" + documentAmount + ", currency=" + currency
				+ ", officeCode=" + officeCode + ", deedCode=" + deedCode + ", operationCode=" + operationCode
				+ ", payer=" + payer + ", payerAgent=" + payerAgent + ", taxList=" + taxList + ", additionalProperties="
				+ additionalProperties + "]";
	}
    
    

}
