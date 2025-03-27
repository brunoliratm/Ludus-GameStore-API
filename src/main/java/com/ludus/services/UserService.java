package com.ludus.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<UserDTO> getAllUsers() {
        try {
            List<UserModel> userModels = userRepository.findAll();
            return userModels.stream().filter(user -> user.getActive() == 1).map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RetrievalException("Error retrieving users");
        }
    }

    public UserDTO getUserById(Long id) {
        try {
            UserModel userModel = userRepository.findById(id)
                    .orElseThrow(() -> new RetrievalException("User not found with id: " + id));

            if (userModel.getActive() == 0) {
                throw new NotFoundException("User not found with id: " + id);
            }
            return convertToDTO(userModel);
        } catch (Exception e) {
            throw new RetrievalException("Error retrieving user by id: " + id);
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
            throw new RetrievalException("Error creating user");
        }
    }

    public void updateUser(Long id, UserDTO userDTO, BindingResult bindingResult) {
        if (id == null || id < 1)
            throw new InvalidIdException();

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

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
            throw new RetrievalException("Error updating user");
        }
    }

    public void deleteUser(Long id) {
        if (id == null || id < 1)
            throw new InvalidIdException();

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        try {
            userModel.setActive(0);
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException("Error deleting user");
        }
    }

    private UserDTO convertToDTO(UserModel userModel) {
        return new UserDTO(userModel.getCpf(), userModel.getEmail(), userModel.getName(),
                userModel.getPassword());
    }

    public void validateFields(UserDTO userDTO, BindingResult bindingResult, Long userId) {

        List<String> errors = new ArrayList<>();

        UserModel findByCpf = userRepository.findByCpf(userDTO.cpf());
        if (findByCpf != null && (userId == null || !findByCpf.getId().equals(userId))) {
            errors.add("CPF already registered");
        }

        UserModel findByEmail = userRepository.findByEmail(userDTO.email());
        if (findByEmail != null && (userId == null || !findByEmail.getId().equals(userId))) {
            errors.add("Email already registered");
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
