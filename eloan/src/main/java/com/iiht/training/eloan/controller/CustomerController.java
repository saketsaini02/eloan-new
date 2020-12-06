package com.iiht.training.eloan.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto, BindingResult result) {

		if (result.hasErrors()) {
			throw new InvalidDataException("Invalid Data Format!");
		}
		UserDto useroutputDto = this.customerService.register(userDto);
		ResponseEntity<UserDto> response = new ResponseEntity<UserDto>(useroutputDto, HttpStatus.OK);
		return response;
	}

	@PostMapping("/apply-loan/{customerId}")
	public ResponseEntity<LoanOutputDto> applyLoan(@PathVariable Long customerId, @Valid @RequestBody LoanDto loanDto,
			BindingResult result) {
		if (result.hasErrors()) {
			throw new InvalidDataException("Invalid Data Format!");
		}
		LoanOutputDto loanOutputDto = this.customerService.applyLoan(customerId, loanDto);
		ResponseEntity<LoanOutputDto> response = new ResponseEntity<LoanOutputDto>(loanOutputDto, HttpStatus.OK);
		return response;
	}

	@GetMapping("/loan-status/{loanAppId}")
	public ResponseEntity<LoanOutputDto> getStatus(@PathVariable Long loanAppId) {
		LoanOutputDto loanOutputDto = this.customerService.getStatus(loanAppId);
		ResponseEntity<LoanOutputDto> response = new ResponseEntity<LoanOutputDto>(loanOutputDto, HttpStatus.OK);
		return response;
	}

	@GetMapping("/loan-status-all/{customerId}")
	public ResponseEntity<List<LoanOutputDto>> getStatusAll(@PathVariable Long customerId) {

		List<LoanOutputDto> loanOutputDto = this.customerService.getStatusAll(customerId);
		ResponseEntity<List<LoanOutputDto>> response = new ResponseEntity<List<LoanOutputDto>>(loanOutputDto,
				HttpStatus.OK);
		return response;
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handler(CustomerNotFoundException ex) {
		ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), System.currentTimeMillis(),
				HttpStatus.NOT_FOUND.value());
		ResponseEntity<ExceptionResponse> response = new ResponseEntity<ExceptionResponse>(exception,
				HttpStatus.NOT_FOUND);
		return response;
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ExceptionResponse> handler(InvalidDataException ex) {
		ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), System.currentTimeMillis(),
				HttpStatus.BAD_REQUEST.value());
		ResponseEntity<ExceptionResponse> response = new ResponseEntity<ExceptionResponse>(exception,
				HttpStatus.BAD_REQUEST);
		return response;
	}

	@ExceptionHandler(LoanNotFoundException.class)

	public ResponseEntity<ExceptionResponse> handler(LoanNotFoundException ex) {
		ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), System.currentTimeMillis(),
				HttpStatus.NOT_FOUND.value());
		ResponseEntity<ExceptionResponse> response = new ResponseEntity<ExceptionResponse>(exception,
				HttpStatus.NOT_FOUND);
		return response;
	}
}
