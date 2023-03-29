package com.shamine.teamsmessagingbackend.repositories;

import com.shamine.teamsmessagingbackend.entities.OTP;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTPRepository extends CrudRepository<OTP, Integer> {

    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode ORDER BY o.createdAt DESC")
    List<OTP> findByEmailAndOtpCode(String email, int otpCode, Pageable pageable);
}
