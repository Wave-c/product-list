package com.wave.auth_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.wave.auth_service.entity.User;
import com.wave.auth_service.entity.dto.LogoutDto;
import com.wave.auth_service.entity.dto.UserDto;

import com.wave.token.Token.JwtDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService 
{
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtDto signUp(UserDto userDto, String instance)
    {
        User user = userService.save(userDto);
        return jwtService.generateJwtPair(user, instance);
    }

    public JwtDto signIn(UserDto userDto, String instance)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userDto.getUsername());
        return jwtService.generateJwtPair(userDetails, instance);
    }

    public void logout(LogoutDto logoutDto)
    {
        jwtService.removeRefreshToken(logoutDto.getInstance());
    }
}
