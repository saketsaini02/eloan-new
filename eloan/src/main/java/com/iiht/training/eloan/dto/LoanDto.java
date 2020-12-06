package com.iiht.training.eloan.dto;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;

public class LoanDto {
	@NotEmpty(message = "loanName is required!")
	@Length(max = 100, min = 3)
	private String loanName;
//	@NotEmpty(message = "loanAmount is required!")
//	@Length(max = 100, min = 3)
	@DecimalMin(value="1.0")
//	@Pattern(regexp = "[0-9]+")
	private Double loanAmount;
	private String loanApplicationDate;
	@NotEmpty(message = "businessStructure is required!")
	private String businessStructure;	
	@NotEmpty(message = "billingIndicator is required!")
	private String billingIndicator;
	@NotEmpty(message = "taxIndicator is required!")
	private String taxIndicator;
	public String getLoanName() {
		return loanName;
	}
	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}
	public Double getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	public String getLoanApplicationDate() {
		return loanApplicationDate;
	}
	public void setLoanApplicationDate(String loanApplicationDate) {
		this.loanApplicationDate = loanApplicationDate;
	}
	public String getBusinessStructure() {
		return businessStructure;
	}
	public void setBusinessStructure(String businessStructure) {
		this.businessStructure = businessStructure;
	}
	public String getBillingIndicator() {
		return billingIndicator;
	}
	public void setBillingIndicator(String billingIndicator) {
		this.billingIndicator = billingIndicator;
	}
	public String getTaxIndicator() {
		return taxIndicator;
	}
	public void setTaxIndicator(String taxIndicator) {
		this.taxIndicator = taxIndicator;
	}
	
	
}
