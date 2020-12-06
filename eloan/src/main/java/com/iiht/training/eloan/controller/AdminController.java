package com.iiht.training.eloan.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.exception.AlreadyFinalizedException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@PostMapping("/register-clerk")
	public ResponseEntity<UserDto> registerClerk(@Valid @RequestBody UserDto userDto,BindingResult result){
		if(result.hasErrors()) {
			throw new InvalidDataException("Invalid Data Format!");
		}
		UserDto useroutputDto = this.adminService.registerClerk(userDto);
		ResponseEntity<UserDto> response =
				new ResponseEntity<UserDto>(useroutputDto, HttpStatus.OK);
		return response;
	}
	
	@PostMapping("/register-manager")
	public ResponseEntity<UserDto> registerManager(@Valid @RequestBody UserDto userDto, BindingResult result){
		if(result.hasErrors())
		 throw new InvalidDataException("Invalid data Format!");
		UserDto useroutputDto=this.adminService.registerManager(userDto);
		ResponseEntity<UserDto> response= new ResponseEntity<UserDto>(useroutputDto,HttpStatus.OK);
		return response;
	}
	
	@GetMapping("/all-clerks")
	public ResponseEntity<List<UserDto>> getAllClerks(){
		List<UserDto> users=this.adminService.getAllClerks();
		 ResponseEntity<List<UserDto>> response= new ResponseEntity<List<UserDto>>(users, HttpStatus.OK);
		return response;

	}
	
	@GetMapping("/all-managers")
	public ResponseEntity<List<UserDto>> getAllManagers(){
		List<UserDto> users=this.adminService.getAllManagers();
		 ResponseEntity<List<UserDto>> response= new ResponseEntity<List<UserDto>>(users, HttpStatus.OK);
		return response;
	}
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ExceptionResponse> handler(InvalidDataException ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.BAD_REQUEST.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.BAD_REQUEST);
		return response;
	}
	
}
