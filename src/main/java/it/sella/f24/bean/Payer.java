
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
    "name",
    "surname",
    "businessName",
    "fiscalCode",
    "birthDate",
    "birthPlace",
    "birthProvince",
    "sex",
    "address"
})
public class Payer {

    @JsonProperty("name")
    private String name;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("businessName")
    private String businessName;
    @JsonProperty("fiscalCode")
    private String fiscalCode;
    @JsonProperty("birthDate")
    private String birthDate;
    @JsonProperty("birthPlace")
    private String birthPlace;
    @JsonProperty("birthProvince")
    private String birthProvince;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("address")
    private Address address;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("surname")
    public String getSurname() {
        return surname;
    }

    @JsonProperty("surname")
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty("businessName")
    public String getBusinessName() {
        return businessName;
    }

    @JsonProperty("businessName")
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @JsonProperty("fiscalCode")
    public String getFiscalCode() {
        return fiscalCode;
    }

    @JsonProperty("fiscalCode")
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    @JsonProperty("birthDate")
    public String getBirthDate() {
        return birthDate;
    }

    @JsonProperty("birthDate")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @JsonProperty("birthPlace")
    public String getBirthPlace() {
        return birthPlace;
    }

    @JsonProperty("birthPlace")
    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    @JsonProperty("birthProvince")
    public String getBirthProvince() {
        return birthProvince;
    }

    @JsonProperty("birthProvince")
    public void setBirthProvince(String birthProvince) {
        this.birthProvince = birthProvince;
    }

    @JsonProperty("sex")
    public String getSex() {
        return sex;
    }

    @JsonProperty("sex")
    public void setSex(String sex) {
        this.sex = sex;
    }

    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
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
		return "Payer [name=" + name + ", surname=" + surname + ", businessName=" + businessName + ", fiscalCode="
				+ fiscalCode + ", birthDate=" + birthDate + ", birthPlace=" + birthPlace + ", birthProvince="
				+ birthProvince + ", sex=" + sex + ", address=" + address + ", additionalProperties="
				+ additionalProperties + "]";
	}
    
    

}
