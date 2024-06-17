package com.wave.auth_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.wave.auth_service.entity.Refresh;
import com.wave.auth_service.entity.User;
import com.wave.auth_service.repository.RefreshRepository;
import com.wave.token.Token.JwtDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService 
{
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final RefreshRepository refreshRepository;

    public JwtDto generateJwtPair(UserDetails userDetails, String instance)
    {
        return JwtDto
            .newBuilder()
            .setAccess(generateToken(userDetails, true, instance))
            .setRefresh(generateToken(userDetails, false, instance))
            .build();
    }

    public String generateToken(UserDetails userDetails, boolean isAccess, String instance)
    {
        Map<String, Object> claims = new HashMap<>();

        if(userDetails instanceof User customUserDetails)
        {
            claims.put("role", customUserDetails.getRole());
        }

        String token = generateToken(claims, userDetails, isAccess);
        if(!isAccess)
        {
            refreshRepository.save(new Refresh(instance, token));
        }
        return token;
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean isAccess)
    {
        Date refreshExpiration;
        if(isAccess)
        {
            refreshExpiration = Date.from(LocalDateTime.now().plusDays(2).atZone(ZoneId.systemDefault()).toInstant());
        }
        else
        {
            refreshExpiration = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        }

        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(refreshExpiration)
            .signWith(SignatureAlgorithm.HS256, getJwtSecretKey()).compact();
    }

    private SecretKey getJwtSecretKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void removeRefreshToken(String instance)
    {
        refreshRepository.deleteById(instance);
    }

    public String extractId(String token, boolean isAccess)
    {
        return this.extractClaim(token, Claims::getId, isAccess);
    }

    public String extractUsername(String token, boolean isAccess)
    {
        return this.extractClaim(token, Claims::getSubject, isAccess);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers, boolean isAccess)
    {
        final Claims CLAIMS = extractAllClaims(token, isAccess);
        return claimsResolvers.apply(CLAIMS);
    }

    private Claims extractAllClaims(String token, boolean isAccess)
    {
        return Jwts.parser()
            .setSigningKey(getJwtSecretKey())
            .build().parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, boolean isAccess)
    {
        final String USERNAME = extractUsername(token, isAccess);
        return (USERNAME.equals(userDetails.getUsername()) && !isTokenExpired(token, isAccess));
    }

    private boolean isTokenExpired(String token, boolean isAccess)
    {
        return extractExpiration(token, isAccess).before(new Date());
    }

    private Date extractExpiration(String token, boolean isAccess)
    {
        return extractClaim(token, Claims::getExpiration, isAccess);
    }
}