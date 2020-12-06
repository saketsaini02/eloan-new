package com.iiht.training.eloan.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.AlreadyFinalizedException;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.exception.ManagerNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private ProcessingInfoRepository processingInfoRepository;

	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;

	@Override
	public List<LoanOutputDto> allProcessedLoans() {
		List<Loan> loans = this.loanRepository.findByStatus(1);
		List<LoanOutputDto> loanOutputDto = loans.stream().map(this::ConvertEntitytoLoanStatusOutputDto)
				.collect(Collectors.toList());
		return loanOutputDto;
	}

	@Override
	public RejectDto rejectLoan(Long managerId, Long loanAppId, RejectDto rejectDto) {
		Loan loan = this.loanRepository.findById(loanAppId).orElse(null);
		if (loan == null) {
			throw new LoanNotFoundException("Loan Not Found");
		}
		Users user = this.usersRepository.findById(managerId).orElse(null);
		String Role = user.getRole();
		if (user == null || !Role.equals("Manager"))
			throw new ManagerNotFoundException("Manager not found!!");
		if (Role.equals("Manager")) {
			int loanstatus = loan.getStatus();
			if (loanstatus == 1) {
				loan.setRemark(rejectDto.getRemark());
				loan.setStatus(-1);
				this.loanRepository.save(loan);
				return rejectDto;
			}

		}
		throw new AlreadyFinalizedException("Already Finalized Loan!!");
	}

	@Override
	public SanctionOutputDto sanctionLoan(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		Loan loan = this.loanRepository.findById(loanAppId).orElse(null);
		if (loan == null) {
			throw new LoanNotFoundException("Loan Not Found");
		}
		Users user = this.usersRepository.findById(managerId).orElse(null);
		String Role = user.getRole();
		if (user == null || !Role.equals("Manager"))
			throw new ManagerNotFoundException("Manager not found!!");
		if (Role.equals("Manager")) {
			int loanstatus = loan.getStatus();
			if (loanstatus == 1) {

				SanctionInfo sanctionInfo = this.convertInputSanctionDtoToEntity(sanctionDto);
				sanctionInfo.setLoanAppId(loanAppId);
				sanctionInfo.setManagerId(managerId);
				SanctionInfo newsanctionInfo = this.sanctionInfoRepository.save(sanctionInfo);
				SanctionOutputDto sanctionOutputDto = this.convertEntityToSanctionOutputDto(newsanctionInfo);
				loan.setStatus(2);
				this.loanRepository.save(loan);
				return sanctionOutputDto;
			}
		}
		throw new AlreadyFinalizedException("Already Finalized Loan!!");

	}

	private SanctionInfo convertInputSanctionDtoToEntity(SanctionDto sanctionDto) {
		SanctionInfo sanctionInfo = new SanctionInfo();
		sanctionInfo.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		Double LoanAmountSanctioned = sanctionDto.getLoanAmountSanctioned();
		sanctionInfo.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		String PaymentStartDate = sanctionDto.getPaymentStartDate();
		sanctionInfo.setTermOfLoan(sanctionDto.getTermOfLoan());
		double TermOfLoan = sanctionDto.getTermOfLoan();
		int termYear = (int) Math.ceil(TermOfLoan);
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(PaymentStartDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.YEAR, termYear);
			String loanClosureDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
			sanctionInfo.setLoanClosureDate(loanClosureDate);
			double Termpaymentamount = (LoanAmountSanctioned) * Math.pow((1 + 4.5 / 100), TermOfLoan);
			Double Monthlypayment = Termpaymentamount / TermOfLoan;
			sanctionInfo.setMonthlyPayment(Monthlypayment);
			return sanctionInfo;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sanctionInfo;

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
