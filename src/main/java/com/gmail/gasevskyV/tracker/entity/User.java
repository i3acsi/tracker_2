package com.gmail.gasevskyV.tracker.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "usr")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String surname;
    private String password;
    private Boolean active;
    private String email;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

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

    private void dosmth(Runnable r){
        r.run();
    }

}

class S{
    static int i = 1;
    static {
        System.out.println("static S, i =" +i +" b= " + "not init");
        i=4;
        b=9;
    }
    static final int b;
    int a = 4;
    {
        System.out.println("S non static, a="+a);
        a=6;
    }
    public S(){
        System.out.println("constructor S, i= "+ i + " a= "+a+" b="+b);
    }
}

class C extends S{
    static int ci = 99;
    static {
        System.out.println("static C, ci =" +ci +" cb= " + "not init");
        ci=44;
        cb=99;
    }
    static final int cb;
    int ca = 4;
    {
        System.out.println("C non static, ca="+ca);
        ca=6;
    }
    public C(){
        System.out.println("constructor C, ci= "+ ci + " ca= "+ca+" cb="+cb);
    }
}

class M{
    public static void main(String[] args) {
        C c = new C();
    }
}