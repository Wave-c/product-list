package com.wave.auth_service.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LogoutDto 
{
    private String username;
    private String instance;
}
