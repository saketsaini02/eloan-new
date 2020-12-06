package com.iiht.training.eloan.dto;

import javax.validation.constraints.NotEmpty;

public class RejectDto {
	@NotEmpty
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
