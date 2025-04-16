package com.ludus.services;

import com.ludus.dtos.requests.AuthDto;
import com.ludus.dtos.requests.UserDtoRequest;
import com.ludus.models.UserModel;
import com.ludus.exceptions.InvalidCredentialsException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.UserInactiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenService tokenService;

    AuthService(AuthenticationManager authenticationManager, UserService userService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Transactional
    public String registerUser(UserDtoRequest userDto, BindingResult result) {
        this.userService.createUser(userDto, result);
        return login(new AuthDto(userDto.email(), userDto.password()));
    }

    public String login(AuthDto loginDTO) {
        validateLogin(loginDTO);
        return this.tokenService.createToken(loginDTO);
    }

    private void validateLogin(AuthDto loginDTO) {
        try {
            if (loginDTO.email() == null || loginDTO.password() == null) {
                throw new InvalidCredentialsException("Email e senha são obrigatórios");
            } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
                throw new InvalidCredentialsException("Email no formato incorreto");
            }

            Optional<UserModel> user = this.userService.findByEmail(loginDTO.email());

            if (user.isEmpty())
                throw new NotFoundException("User Not found");

            if (!user.get().isEnabled())
                throw new UserInactiveException("Usuário inativo");

            if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
                throw new InvalidCredentialsException("Email ou senha não conferem");
            }

        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException();
        } catch (UserInactiveException e) {
            throw new UserInactiveException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro durante o login");
        }

    }
}