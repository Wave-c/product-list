package com.wave.auth_service.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wave.auth_service.entity.dto.UserDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Table(name = "user_t")
@Accessors(chain = true)
@NoArgsConstructor
public class User implements UserDetails
{
    @Id
    private String id;

    private String username;
    private String password;
    private Role role;

    private LocalDateTime createdAt;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(UserDto userDto)
    {
        this.id = UUID.randomUUID().toString();
        this.username = userDto.getUsername();
        this.password =  passwordEncoder.encode(userDto.getPassword());
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() 
    {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() 
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() 
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() 
    {
        return true;
    }

    @Override
    public boolean isEnabled() 
    {
        return true;
    }
}
