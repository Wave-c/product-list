package com.wave.auth_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.auth_service.entity.dto.AuthDto;
import com.wave.auth_service.entity.dto.LogoutDto;
import com.wave.auth_service.entity.dto.UserDto;
import com.wave.auth_service.service.AuthService;
import com.wave.token.Token.JwtDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController 
{
    private final AuthService authService;
    
    @Operation(summary = "registration of user")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserDto request, HttpServletResponse response)
    {
        String instance = UUID.randomUUID().toString();
        JwtDto jwt = authService.signUp(request, instance);

        AuthDto authDto = new AuthDto()
            .setAccessToken(jwt.getAccess())
            .setInstance(instance);
        
        Cookie refreshCookie = new Cookie("RefreshToken", jwt.getRefresh());
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30*24*60*60*1000);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        response.setContentType("text/plain");

        return ResponseEntity.ok(authDto);
    }

    @Operation(summary = "login user")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody UserDto request, HttpServletResponse response)
    {
        String instance = UUID.randomUUID().toString();
        JwtDto jwt = authService.signIn(request, instance);

        AuthDto authDto = new AuthDto()
            .setAccessToken(jwt.getAccess())
            .setInstance(instance);
        
        Cookie cookieRefresh = new Cookie("RefreshToken", jwt.getRefresh());
        cookieRefresh.setPath("/");
        cookieRefresh.setMaxAge(30*24*60*60*1000);
        cookieRefresh.setHttpOnly(true);
        response.addCookie(cookieRefresh);
        response.setContentType("text/plain");

        return ResponseEntity.ok(authDto);
    }

    @Operation(summary = "logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutDto request)
    {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}