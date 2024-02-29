package com.raphael.msuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raphael.msuser.entity.UserEntity;
import com.raphael.msuser.exception.*;
import com.raphael.msuser.infra.mqueue.MsNotificationPublisher;
import com.raphael.msuser.repository.UserRepository;
import com.raphael.msuser.web.client.AddressClient;
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
    private final MsNotificationPublisher notificationPublisher;
    private final AddressClient addressClient;

    @Transactional
    public void save(UserEntity user) {
        try {
            var address = addressClient.findByCep(user.getCep());
            if (address.getUf() == null)
                throw new CepNotFoundException("O CEP informado não existe.");

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            addressClient.saveAddressWithUser(user.getCep(), user.getEmail());
            notificationPublisher.sendNotification(user.getEmail(), "CREATE");
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueCredentialsViolationException("E-mail ou CPF já cadastrado no sistema.");
        } catch (JsonProcessingException e) {
            throw new NotificationProcessingException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário com id %s não encontrado.", id))
        );
    }

    @Transactional
    public UserEntity editUser(Long id, UserUpdateDto updateDto) {
        try {
            var address = addressClient.findByCep(updateDto.getCep());
            if (address.getUf() == null)
                throw new CepNotFoundException("O CEP informado não existe.");

            UserEntity user = findById(id);

            user.setFirstName(updateDto.getFirstName());
            user.setLastName(updateDto.getLastName());
            user.setBirthdate(updateDto.getBirthdate());
            user.setCep(updateDto.getCep());
            user.setActive(updateDto.getActive());

            addressClient.saveAddressWithUser(user.getCep(), user.getEmail());
            notificationPublisher.sendNotification(user.getEmail(), "UPDATE");
            return userRepository.save(user);
        } catch (JsonProcessingException e) {
            throw new NotificationProcessingException(e.getMessage());
        }
    }

    @Transactional
    public void editPassword(Long id, String password) {
        try {
            UserEntity user = findById(id);
            if (passwordEncoder.matches(password, user.getPassword()))
                throw new PasswordInvalidException("A nova senha tem que ser diferente da senha atual.");
            user.setPassword(passwordEncoder.encode(password));
            notificationPublisher.sendNotification(user.getEmail(), "UPDATE_PASSWORD");
        } catch (JsonProcessingException e) {
            throw new NotificationProcessingException(e.getMessage());
        }
    }
}
