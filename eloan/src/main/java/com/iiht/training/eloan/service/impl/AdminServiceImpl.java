package com.iiht.training.eloan.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.AdminService;



@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDto registerClerk(UserDto userDto) {
		Users user = this.convertInputDtoToEntity(userDto);
		user.setRole("Clerk");
		Users addUser=this.usersRepository.save(user);
		UserDto newUserDto=this.convertEntityToDto(addUser);
		return newUserDto;
	}

	private UserDto convertEntityToDto(Users addUser) {
		UserDto userDto= new UserDto();
		userDto.setId(addUser.getId());
		userDto.setFirstName(addUser.getFirstName());
		userDto.setLastName(addUser.getLastName());
		userDto.setEmail(addUser.getEmail());
		userDto.setMobile(addUser.getMobile());
		return userDto;
	}

	private Users convertInputDtoToEntity(UserDto userDto) {
		Users user = new Users();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setMobile(userDto.getMobile());				
		return user;
	}

	@Override
	public UserDto registerManager(UserDto userDto) {
		Users user = this.convertInputDtoToEntity(userDto);
		user.setRole("Manager");
		Users addUser=this.usersRepository.save(user);
		UserDto newUserDto=this.convertEntityToDto(addUser);
		return newUserDto;
		
	}

	@Override
	public List<UserDto> getAllClerks() {
		
		List<Users> listUsers=this.usersRepository.FindByRole("Clerk");
		List<UserDto> UserDto = 
				listUsers.stream()
						 .map(this :: convertEntityToDto)
						 .collect(Collectors.toList());
		return UserDto;
	}

	@Override
	public List<UserDto> getAllManagers() {
		List<Users> listUsers=this.usersRepository.FindByRole("Manager");
		List<UserDto> UserDto = 
				listUsers.stream()
						 .map(this :: convertEntityToDto)
						 .collect(Collectors.toList());
		return UserDto;
	}

}
