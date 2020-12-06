package com.iiht.training.eloan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iiht.training.eloan.entity.Users;


@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
	
	
	@Query("select u from Users u where u.role=:role")
	List<Users> FindByRole(@Param("role") String role);

}
