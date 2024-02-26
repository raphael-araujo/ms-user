package com.raphael.msuser.jwt;

import com.raphael.msuser.entity.UserEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private final UserEntity user;

    public JwtUserDetails(UserEntity user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }
}
