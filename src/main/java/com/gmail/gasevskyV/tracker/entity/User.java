package com.gmail.gasevskyV.tracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "usr")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "User name can't be empty")
    private String username;

    @NotBlank(message = "Surname can't be empty")
    private String surname;

    @NotBlank(message = "Password can't be empty")
    private String password;
    @Transient
    @NotBlank(message = "Password confirmation can't be empty")
    private String password2;

    private Boolean active;

    @Email(message="Email is not correct")
    @NotBlank(message = "Email name can't be empty")
    private String email;

    private String activationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime lastVisit = LocalDateTime.now();

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    //    oauth
    @Column(name = "google_id")
    private String googleId;
    @Column(name = "google_name")
    private String googleName;
    private String userpic;
    private String gender;
    private String locale;

    @Override
    public String toString() {
        return username + " " + surname;
    }

    //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return getActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getActive();
    }

    @Override
    public boolean isEnabled() {

        return getActive();
    }
}