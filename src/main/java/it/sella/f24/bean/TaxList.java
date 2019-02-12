
package it.sella.f24.bean;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "section",
    "taxCode",
    "receiverCode",
    "isAmendment",
    "isRealEstateVariation",
    "isDeposit",
    "isFinalPayment",
    "realEstateCount",
    "referenceMonth",
    "referenceYear",
    "deductionAmount",
    "taxAmount",
    "compensationAmount"
})
public class TaxList {

    @JsonProperty("section")
    private String section;
    @JsonProperty("taxCode")
    private String taxCode;
    @JsonProperty("receiverCode")
    private String receiverCode;
    @JsonProperty("isAmendment")
    private String isAmendment;
    @JsonProperty("isRealEstateVariation")
    private String isRealEstateVariation;
    @JsonProperty("isDeposit")
    private String isDeposit;
    @JsonProperty("isFinalPayment")
    private String isFinalPayment;
    @JsonProperty("realEstateCount")
    private Integer realEstateCount;
    @JsonProperty("referenceMonth")
    private String referenceMonth;
    @JsonProperty("referenceYear")
    private String referenceYear;
    @JsonProperty("deductionAmount")
    private String deductionAmount;
    @JsonProperty("taxAmount")
    private Double taxAmount;
    @JsonProperty("compensationAmount")
    private String compensationAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("section")
    public String getSection() {
        return section;
    }

    @JsonProperty("section")
    public void setSection(String section) {
        this.section = section;
    }

    @JsonProperty("taxCode")
    public String getTaxCode() {
        return taxCode;
    }

    @JsonProperty("taxCode")
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @JsonProperty("receiverCode")
    public String getReceiverCode() {
        return receiverCode;
    }

    @JsonProperty("receiverCode")
    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    @JsonProperty("isAmendment")
    public String getIsAmendment() {
        return isAmendment;
    }

    @JsonProperty("isAmendment")
    public void setIsAmendment(String isAmendment) {
        this.isAmendment = isAmendment;
    }

    @JsonProperty("isRealEstateVariation")
    public String getIsRealEstateVariation() {
        return isRealEstateVariation;
    }

    @JsonProperty("isRealEstateVariation")
    public void setIsRealEstateVariation(String isRealEstateVariation) {
        this.isRealEstateVariation = isRealEstateVariation;
    }

    @JsonProperty("isDeposit")
    public String getIsDeposit() {
        return isDeposit;
    }

    @JsonProperty("isDeposit")
    public void setIsDeposit(String isDeposit) {
        this.isDeposit = isDeposit;
    }

    @JsonProperty("isFinalPayment")
    public String getIsFinalPayment() {
        return isFinalPayment;
    }

    @JsonProperty("isFinalPayment")
    public void setIsFinalPayment(String isFinalPayment) {
        this.isFinalPayment = isFinalPayment;
    }

    @JsonProperty("realEstateCount")
    public Integer getRealEstateCount() {
        return realEstateCount;
    }

    @JsonProperty("realEstateCount")
    public void setRealEstateCount(Integer realEstateCount) {
        this.realEstateCount = realEstateCount;
    }

    @JsonProperty("referenceMonth")
    public String getReferenceMonth() {
        return referenceMonth;
    }

    @JsonProperty("referenceMonth")
    public void setReferenceMonth(String referenceMonth) {
        this.referenceMonth = referenceMonth;
    }

    @JsonProperty("referenceYear")
    public String getReferenceYear() {
        return referenceYear;
    }

    @JsonProperty("referenceYear")
    public void setReferenceYear(String referenceYear) {
        this.referenceYear = referenceYear;
    }

    @JsonProperty("deductionAmount")
    public String getDeductionAmount() {
        return deductionAmount;
    }

    @JsonProperty("deductionAmount")
    public void setDeductionAmount(String deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    @JsonProperty("taxAmount")
    public Double getTaxAmount() {
        return taxAmount;
    }

    @JsonProperty("taxAmount")
    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    @JsonProperty("compensationAmount")
    public String getCompensationAmount() {
        return compensationAmount;
    }

    @JsonProperty("compensationAmount")
    public void setCompensationAmount(String compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
