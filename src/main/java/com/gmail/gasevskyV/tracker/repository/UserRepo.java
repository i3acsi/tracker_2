package com.gmail.gasevskyV.tracker.repository;

import com.gmail.gasevskyV.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Optional<User> findById(Long id);

    User findUserByActivationCode(String code);
}
