package com.example.HM.repository;

import com.example.HM.entity.User;
import com.example.HM.entity.type.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByProviderIdAndAuthProviderType(String providerId, AuthProviderType providerType);
}