package com.ludus.services;

import com.ludus.dtos.requests.AuthDto;
import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.models.UserModel;
import com.ludus.exceptions.InvalidCredentialsException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.UserInactiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;
    private UserService userService;
    private TokenService tokenService;

    AuthService(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userService.loadUserByEmail(email);
    }

    @Transactional
    public String registerUser(UserDtoRequest userDto, BindingResult result,
            AuthenticationManager authenticationManager) {
        this.userService.createUser(userDto, result);
        return login(new AuthDto(userDto.email(), userDto.password()), authenticationManager);
    }

    public String login(AuthDto loginDTO, AuthenticationManager authenticationManager) {
        validateLogin(loginDTO);
        return this.tokenService.createToken(loginDTO, authenticationManager);
    }

    private void validateLogin(AuthDto loginDTO) {
        if (loginDTO.email() == null || loginDTO.password() == null) {
            throw new InvalidCredentialsException("Email and password are required");
        } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Invalid email format");
        }

        Optional<UserModel> user = this.userService.findByEmail(loginDTO.email());

        if (user.isEmpty())
            throw new NotFoundException("User not found");

        if (!user.get().isEnabled())
            throw new UserInactiveException("User inactive");

        if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
            throw new InvalidCredentialsException("Email or password do not match");
        }
    }
}
