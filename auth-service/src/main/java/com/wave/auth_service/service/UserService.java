package com.wave.auth_service.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wave.auth_service.entity.User;
import com.wave.auth_service.entity.dto.UserDto;
import com.wave.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService 
{
    private final UserRepository userRepository;

    public User save(UserDto userDto)
    {
        if(userRepository.existsByUsername(userDto.getUsername()))
        {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        User user = new User(userDto);
        return userRepository.save(user);
    }

    public UserDetailsService userDetailsService()
    {
        return this::getByUsername;
    }

    public User getByUsername(String username)
    {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким Username не найден"));
    }
}
