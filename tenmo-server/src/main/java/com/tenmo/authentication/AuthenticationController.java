package com.tenmo.authentication;

import com.tenmo.authentication.dto.AuthenticationResponseDto;
import com.tenmo.authentication.dto.LoginDto;
import com.tenmo.authentication.dto.RegisterDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponseDto> login(@Valid @RequestBody LoginDto loginDto){
        return ResponseEntity.ok(authenticationService.login(loginDto));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register")
    public void register(@Valid @RequestBody RegisterDto registerDto){
        authenticationService.register(registerDto);
    }
}
