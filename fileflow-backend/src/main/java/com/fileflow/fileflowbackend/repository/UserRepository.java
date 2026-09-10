package com.fileflow.fileflowbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fileflow.fileflowbackend.entity.User;

@Repository
public interface UserRepository
    extends JpaRepository<User, Long>
{
    User findByEmail(String email);
}