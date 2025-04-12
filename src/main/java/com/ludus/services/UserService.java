package com.ludus.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.dtos.requests.UserPatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.dtos.responses.UserDtoResponse;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.exceptions.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.ludus.models.UserModel;
import com.ludus.repositories.UserRepository;
import com.ludus.utils.UtilHelper;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UtilHelper utilHelper;

    public ApiDtoResponse<UserDtoResponse> getAllUsers(int page, String name) {
        if (page < 1) {
            throw new InvalidPageException("Page number must be greater than 0");
        }

        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, 10);
        Page<UserModel> userPage;

        if (name != null) {
            userPage = userRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
        } else {
            userPage = userRepository.findByActiveTrue(pageable);
        }

        List<UserDtoResponse> userDTOs = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());  
                    
        InfoDtoResponse info = utilHelper.buildPageableInfoDto(userPage, "/users");
        return new ApiDtoResponse<>(info, userDTOs);
    }

    public UserDtoResponse getUserById(Long id) {
        if (id == null || id < 1) {
            throw new InvalidIdException();
        }
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{id}, Locale.getDefault())));

        if (userModel.isActive() == false) {
            throw new NotFoundException(
                    messageSource.getMessage("user.not.found", new Object[]{id}, Locale.getDefault()));
        }
        return convertToDTO(userModel);
    }

    public void createUser(UserDtoRequest userDTO, BindingResult bindingResult) {
        validateFields(userDTO, bindingResult, null);

        try {
            UserModel userModel = new UserModel();
            userModel.setEmail(userDTO.email());
            userModel.setName(userDTO.name());
            userModel.setPassword(userDTO.password());
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("user.creation.error", null, Locale.getDefault()));
        }
    }

    public void updateUser(Long id, UserPatchDtoRequest userDTO, BindingResult bindingResult) {
        if (id == null || id < 1)
            throw new InvalidIdException();

        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSource.getMessage("user.not.found", 
                        new Object[]{id}, Locale.getDefault())));

        validatePatchFields(userDTO, bindingResult, id);

        try {
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
            userModel.setActive(false);
            userRepository.save(userModel);
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("user.deletion.error", null, Locale.getDefault()));
        }
    }

    private UserDtoResponse convertToDTO(UserModel userModel) {
        return new UserDtoResponse(userModel.getId(), userModel.getEmail(), userModel.getName());
    }

    public void validateFields(UserDtoRequest userDTO, BindingResult bindingResult, Long userId) {

        List<String> errors = new ArrayList<>();

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

    public void validatePatchFields(UserPatchDtoRequest userDTO, BindingResult bindingResult, Long userId) {

        List<String> errors = new ArrayList<>();

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

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email);
        if (user == null || !user.isActive()) {
            throw new NotFoundException("User not found");
        }
        return user;
    }
}
