package com.gmail.gasevskyV.tracker.repository;

import com.gmail.gasevskyV.tracker.entity.User;
import com.sun.mail.imap.protocol.UIDSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByGoogleName(String googleName);

    Optional<User> findById(Long id);

    User findUserByActivationCode(String code);
}
