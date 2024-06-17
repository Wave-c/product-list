package com.wave.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wave.auth_service.entity.Refresh;

public interface RefreshRepository extends JpaRepository<Refresh, String>
{
    
}
