package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.otp = :otp WHERE u.email = :email")
    void updateOtpByEmail(@Param("email") String email, @Param("otp") String otp);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.otp = :otp WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
