package com.example.mediatracker.repository;

import com.example.mediatracker.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserDetails findByUserName(String userName);

}
