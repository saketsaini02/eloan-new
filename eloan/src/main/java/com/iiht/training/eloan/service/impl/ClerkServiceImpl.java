package com.iiht.training.eloan.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.AlreadyProcessedException;
import com.iiht.training.eloan.exception.ClerkNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.exception.ManagerNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ClerkService;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private ProcessingInfoRepository processingInfoRepository;

	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;

	@Override
	public List<LoanOutputDto> allAppliedLoans() {
		List<Loan> loans = this.loanRepository.findByStatus(0);
		List<LoanOutputDto> loanOutputDto = loans.stream().map(this::ConvertEntitytoLoanStatusOutputDto)
				.collect(Collectors.toList());
		return loanOutputDto;
	}

	@Override
	public ProcessingDto processLoan(Long clerkId, Long loanAppId, ProcessingDto processingDto) {
		Loan loan = this.loanRepository.findById(loanAppId).orElse(null);
		if (loan == null) {
			throw new LoanNotFoundException("Loan Not Found");
		}
		Users user = this.usersRepository.findById(clerkId).orElse(null);
		String Role = user.getRole();
		if (user == null || !Role.equals("Clerk"))
			throw new ClerkNotFoundException("Clerk not found!!");
		if (Role.equals("Clerk")) {
			int loanstatus = loan.getStatus();
			if (loanstatus == 0) {
				ProcessingInfo processingInfo = this.convertProcessingDtoToEntity(processingDto);
				processingInfo.setLoanAppId(loanAppId);
				processingInfo.setLoanClerkId(clerkId);
				String Date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
				processingInfo.setValuationDate(Date);
				ProcessingInfo newprocessingInfo = this.processingInfoRepository.save(processingInfo);
				ProcessingDto newProcessingDto = this.convertEntityToProcessingDto(newprocessingInfo);
				loan.setStatus(1);
				this.loanRepository.save(loan);
				return newProcessingDto;
			}
		}
		throw new AlreadyProcessedException("Already Processed Loan!");
	}

	private ProcessingInfo convertProcessingDtoToEntity(ProcessingDto processingDto) {
		ProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.setAcresOfLand(processingDto.getAcresOfLand());
		processingInfo.setAddressOfProperty(processingDto.getAddressOfProperty());
		processingInfo.setAppraisedBy(processingDto.getAppraisedBy());
		processingInfo.setLandValue(processingDto.getLandValue());
		processingInfo.setAddressOfProperty(processingDto.getAddressOfProperty());
		processingInfo.setSuggestedAmountOfLoan(processingDto.getSuggestedAmountOfLoan());
		// processingInfo.setValuationDate(processingDto.getValuationDate());
		return processingInfo;
	}

	private UserDto convertEntityToUserDto(Users addUser) {
		UserDto userDto = new UserDto();
		userDto.setId(addUser.getId());
		userDto.setFirstName(addUser.getFirstName());
		userDto.setLastName(addUser.getLastName());
		userDto.setEmail(addUser.getEmail());
		userDto.setMobile(addUser.getMobile());
		return userDto;
	}

	private ProcessingDto convertEntityToProcessingDto(ProcessingInfo processingInfo) {
		ProcessingDto ProcessingDto = new ProcessingDto();
		ProcessingDto.setAcresOfLand(processingInfo.getAcresOfLand());
		ProcessingDto.setAddressOfProperty(processingInfo.getAddressOfProperty());
		ProcessingDto.setAppraisedBy(processingInfo.getAppraisedBy());
		ProcessingDto.setLandValue(processingInfo.getLandValue());
		ProcessingDto.setSuggestedAmountOfLoan(processingInfo.getSuggestedAmountOfLoan());
		ProcessingDto.setValuationDate(processingInfo.getValuationDate());
		return ProcessingDto;
	}

	private SanctionOutputDto convertEntityToSanctionOutputDto(SanctionInfo sanctionInfo) {
		SanctionOutputDto SanctionOutputDto = new SanctionOutputDto();
		SanctionOutputDto.setLoanAmountSanctioned(sanctionInfo.getLoanAmountSanctioned());
		SanctionOutputDto.setLoanClosureDate(sanctionInfo.getLoanClosureDate());
		SanctionOutputDto.setMonthlyPayment(sanctionInfo.getMonthlyPayment());
		SanctionOutputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
		SanctionOutputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
		return SanctionOutputDto;
	}

	private LoanOutputDto ConvertEntitytoLoanStatusOutputDto(Loan loan) {
		Long CustomerId = loan.getCustomerId();
		Long loanAppId = loan.getId();
		Users user = this.usersRepository.findById(CustomerId).orElse(null);
		SanctionInfo SanctionInfo = this.sanctionInfoRepository.findByloanAppId(loanAppId);
		ProcessingInfo ProcessingInfo = this.processingInfoRepository.findByloanAppId(loanAppId);
		LoanOutputDto loanOutputDto = new LoanOutputDto();
		loanOutputDto.setCustomerId(loan.getCustomerId());
		loanOutputDto.setLoanAppId(loan.getId());
		LoanDto loanDto = this.convertEntityToLoanDto(loan);
		UserDto userDto = this.convertEntityToUserDto(user);
		loanOutputDto.setLoanDto(loanDto);
		loanOutputDto.setUserDto(userDto);
		if (loan.getStatus() == 0) {
			loanOutputDto.setStatus("Applied");
			return loanOutputDto;
		} else if (loan.getStatus() == 1) {
			loanOutputDto.setStatus("Processed");
			ProcessingDto ProcessingDto = this.convertEntityToProcessingDto(ProcessingInfo);
			loanOutputDto.setProcessingDto(ProcessingDto);
			return loanOutputDto;
		} else if (loan.getStatus() == 2) {
			loanOutputDto.setStatus("Sanctioned");
			ProcessingDto ProcessingDto = this.convertEntityToProcessingDto(ProcessingInfo);
			loanOutputDto.setProcessingDto(ProcessingDto);
			SanctionOutputDto SanctionOutputDto = this.convertEntityToSanctionOutputDto(SanctionInfo);
			loanOutputDto.setSanctionOutputDto(SanctionOutputDto);
			return loanOutputDto;
		} else if (loan.getStatus() == -1)
			loanOutputDto.setStatus("Rejected");
		RejectDto RejectDto = new RejectDto();
		loanOutputDto.setRemark(RejectDto.getRemark());
		return loanOutputDto;
	}

	private LoanDto convertEntityToLoanDto(Loan loan) {
		LoanDto loanDto = new LoanDto();
		loanDto.setBillingIndicator(loan.getBillingIndicator());
		loanDto.setBusinessStructure(loan.getBusinessStructure());
		loanDto.setLoanAmount(loan.getLoanAmount());
		loanDto.setLoanApplicationDate(loan.getLoanApplicationDate());
		loanDto.setLoanName(loan.getLoanName());
		loanDto.setTaxIndicator(loan.getTaxIndicator());
		return loanDto;
	}

}
