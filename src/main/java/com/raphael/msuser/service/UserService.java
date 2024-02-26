package com.raphael.msuser.service;

import com.raphael.msuser.entity.UserEntity;
import com.raphael.msuser.exception.EntityNotFoundException;
import com.raphael.msuser.exception.PasswordInvalidException;
import com.raphael.msuser.exception.UniqueCredentialsViolationException;
import com.raphael.msuser.repository.UserRepository;
import com.raphael.msuser.web.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(UserEntity user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueCredentialsViolationException("E-mail ou CPF já cadastrado no sistema.");
        }
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário com id %s não encontrado.", id))
        );
    }

    @Transactional
    public void editPassword(Long id, String password) {
        UserEntity user = findById(id);
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordInvalidException("A nova senha tem que ser diferente da senha atual.");
        }
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public UserEntity editUser(Long id, UserUpdateDto updateDto) {
        UserEntity user = findById(id);

        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setBirthdate(updateDto.getBirthdate());
        user.setCep(updateDto.getCep());
        user.setActive(updateDto.getActive());

        return userRepository.save(user);
    }
}
