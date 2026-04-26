package com.project.taskhub.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.taskhub.dto.mapper.UserMapper;
import com.project.taskhub.dto.request.LoginRequestDTO;
import com.project.taskhub.dto.request.RegisterUserRequestDTO;
import com.project.taskhub.dto.response.LoginResponseDTO;
import com.project.taskhub.dto.response.RegisterUserResponseDTO;
import com.project.taskhub.entity.User;
import com.project.taskhub.repository.UserRepository;
import com.project.taskhub.security.TokenConfiguration;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticatorController {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfiguration tokenConfiguration;

    public AuthenticatorController(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager,
	    TokenConfiguration tokenConfiguration) {
	this.userRepository = userRepository;
	this.userMapper = userMapper;
	this.passwordEncoder = passwordEncoder;
	this.authenticationManager = authenticationManager;
	this.tokenConfiguration = tokenConfiguration;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginrequestdto) {
	UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(loginrequestdto.email(), loginrequestdto.password());
	Authentication auth = authenticationManager.authenticate(userAndPassword);
	User user = (User) auth.getPrincipal();
	String token = tokenConfiguration.generatorToken(user);

	return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO registeruserrequestdto) {
	User user = userMapper.toEntity(registeruserrequestdto);
	user.setPassword(passwordEncoder.encode(registeruserrequestdto.password()));
	userRepository.save(user);

	return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toRegisterDTO(user));
    }

}
