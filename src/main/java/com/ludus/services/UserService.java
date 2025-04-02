package com.ludus.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.ludus.dto.UserDTO;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.exceptions.ValidationException;
import com.ludus.models.UserModel;
import com.ludus.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageSource messageSource;

    public List<UserDTO> getAllUsers() {
        try {
            List<UserModel> userModels = userRepository.findAll();
            return userModels.stream().filter(user -> user.getActive() == 1).map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("retrieval.error", null, Locale.getDefault()));
        }
    }

    public UserDTO getUserById(Long id) {
        try {
            UserModel userModel = userRepository.findById(id)
                    .orElseThrow(() -> new RetrievalException(messageSource.getMessage("user.not.found", 
                            new Object[]{id}, Locale.getDefault())));

            if (userModel.getActive() == 0) {
                throw new NotFoundException(messageSource.getMessage("user.not.found", 
                        new Object[]{id}, Locale.getDefault()));
            }
            return convertToDTO(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("retrieval.error", null, Locale.getDefault()));
        }
    }

    public void createUser(UserDTO userDTO, BindingResult bindingResult) {
        validateFields(userDTO, bindingResult, null);

        try {
            UserModel userModel = new UserModel();
            userModel.setCpf(userDTO.cpf());
            userModel.setEmail(userDTO.email());
            userModel.setName(userDTO.name());
            userModel.setPassword(userDTO.password());
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("user.creation.error", null, Locale.getDefault()));
        }
    }

    public void updateUser(Long id, UserDTO userDTO, BindingResult bindingResult) {
        if (id == null || id < 1)
            throw new InvalidIdException();

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSource.getMessage("user.not.found", 
                        new Object[]{id}, Locale.getDefault())));

        validateFields(userDTO, bindingResult, id);

        try {
            if (userDTO.cpf() != null) {
                userModel.setCpf(userDTO.cpf());
            }
            if (userDTO.email() != null) {
                userModel.setEmail(userDTO.email());
            }
            if (userDTO.name() != null) {
                userModel.setName(userDTO.name());
            }
            if (userDTO.password() != null) {
                userModel.setPassword(userDTO.password());
            }
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("user.update.error", null, Locale.getDefault()));
        }
    }

    public void deleteUser(Long id) {
        if (id == null || id < 1)
            throw new InvalidIdException();

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSource.getMessage("user.not.found", 
                        new Object[]{id}, Locale.getDefault())));

        try {
            userModel.setActive(0);
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("user.deletion.error", null, Locale.getDefault()));
        }
    }

    private UserDTO convertToDTO(UserModel userModel) {
        return new UserDTO(userModel.getCpf(), userModel.getEmail(), userModel.getName(), null);
    }

    public void validateFields(UserDTO userDTO, BindingResult bindingResult, Long userId) {

        List<String> errors = new ArrayList<>();

        UserModel findByCpf = userRepository.findByCpf(userDTO.cpf());
        if (findByCpf != null && (userId == null || !findByCpf.getId().equals(userId))) {
            errors.add(messageSource.getMessage("cpf.already.exists", null, Locale.getDefault()));
        }

        UserModel findByEmail = userRepository.findByEmail(userDTO.email());
        if (findByEmail != null && (userId == null || !findByEmail.getId().equals(userId))) {
            errors.add(messageSource.getMessage("email.already.exists", null, Locale.getDefault()));
        }

        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
