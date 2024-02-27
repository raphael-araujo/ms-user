package com.raphael.msuser.jwt;

import com.raphael.msuser.entity.UserEntity;
import com.raphael.msuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String email) {
        UserEntity user = userRepository.findByEmail(email);
        return JwtUtils.createToken(user.getEmail(), user.getCpf());
    }
}
