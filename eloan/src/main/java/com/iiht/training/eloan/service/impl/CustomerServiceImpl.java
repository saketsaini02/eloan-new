package com.iiht.training.eloan.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private ProcessingInfoRepository processingInfoRepository;

	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;

	@Override
	public UserDto register(UserDto userDto) {
		Users user = this.convertInputUserDtoToEntity(userDto);
		user.setRole("Customer");
		Users addUser = this.usersRepository.save(user);
		UserDto newUserDto = this.convertEntityToUserDto(addUser);
		return newUserDto;

	}

	@Override
	public LoanOutputDto applyLoan(Long customerId, LoanDto loanDto) {
		Users user = this.usersRepository.findById(customerId).orElse(null);
		if(user ==null)
			throw new CustomerNotFoundException("No Customer found with the specified id!!");
		String role = user.getRole();
		if (this.usersRepository.existsById(customerId) && role.equals("Customer")) {
			Loan loan = this.convertInputLoanDtoToEntity(loanDto, customerId);
			loan.setCustomerId(customerId);
			loan.setStatus(0);
			Loan newLoan = this.loanRepository.save(loan);
			loanDto.setLoanApplicationDate(newLoan.getLoanApplicationDate());
			Long loanAppId = newLoan.getId();
			UserDto newUserDto = this.convertEntityToUserDto(user);
			LoanOutputDto loanOutputDto = this.convertEntityToLoanOutputDto(newUserDto, loanDto);
			loanOutputDto.setLoanAppId(loanAppId);
			return loanOutputDto;
		}
		return null;
	}

	@Override
	public LoanOutputDto getStatus(Long loanAppId) {
		Loan Loan = this.loanRepository.findById(loanAppId).orElse(null);
		if(Loan ==null)
			throw new LoanNotFoundException("No Loan found with the specified id!!");
		LoanOutputDto loanOutputDto = this.ConvertEntitytoLoanStatusOutputDto(Loan);
		return loanOutputDto;
	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) {
		List<Loan> listloans = this.loanRepository.findByCustomerId(customerId);
		if(listloans.toString().equals("[]") ) {
			throw new CustomerNotFoundException("No Customer found with the specified id!!");
		}
		List<LoanOutputDto> loanOutputDto = listloans.stream().map(this::ConvertEntitytoLoanStatusOutputDto)
				.collect(Collectors.toList());

		return loanOutputDto;
	}

	// Utility methods
	private UserDto convertEntityToUserDto(Users addUser) {
		UserDto userDto = new UserDto();
		userDto.setId(addUser.getId());
		userDto.setFirstName(addUser.getFirstName());
		userDto.setLastName(addUser.getLastName());
		userDto.setEmail(addUser.getEmail());
		userDto.setMobile(addUser.getMobile());
		return userDto;
	}

	private Users convertInputUserDtoToEntity(UserDto userDto) {
		Users user = new Users();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setMobile(userDto.getMobile());
		return user;
	}

	private LoanOutputDto convertEntityToLoanOutputDto(UserDto newUserDto, LoanDto loanDto) {
		LoanOutputDto loanOutputDto = new LoanOutputDto();
		loanOutputDto.setCustomerId(newUserDto.getId());
		loanOutputDto.setLoanDto(loanDto);
		loanOutputDto.setUserDto(newUserDto);
		loanOutputDto.setStatus("Applied");
		return loanOutputDto;
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

	private Loan convertInputLoanDtoToEntity(LoanDto loanDto, Long customerId) {
		Loan loan = new Loan();
		loan.setLoanName(loanDto.getLoanName());
		loan.setBillingIndicator(loanDto.getBillingIndicator());
		loan.setLoanAmount(loanDto.getLoanAmount());
		String Date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		loan.setLoanApplicationDate(Date);
		loan.setBusinessStructure(loanDto.getBusinessStructure());
		loan.setBillingIndicator(loanDto.getBillingIndicator());
		loan.setTaxIndicator(loanDto.getTaxIndicator());
		return loan;
	}

	private LoanOutputDto ConvertEntitytoLoanStatusOutputDto(Loan loan) {
		Long CustomerId = loan.getCustomerId();
		Long loanAppId = loan.getId();
		Users user = this.usersRepository.findById(CustomerId).orElse(null);
		String Remarks=loan.getRemark();
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
		ProcessingDto ProcessingDto = this.convertEntityToProcessingDto(ProcessingInfo);
		loanOutputDto.setProcessingDto(ProcessingDto);
		loanOutputDto.setRemark(Remarks);
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
