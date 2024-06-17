package com.wave.auth_service.controller.grpcController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import com.wave.token.Token.TokenRequest;
import com.wave.token.Token.TokenResponse;
import com.wave.token.TokenServiceGrpc.TokenServiceImplBase;
import com.wave.utility.StreamObserverUtility;
import com.wave.auth_service.service.JwtService;
import com.wave.auth_service.service.UserService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class TokenGrpcController extends TokenServiceImplBase
{
    private static final Logger log = LoggerFactory.getLogger(TokenGrpcController.class);
    private final JwtService jwtService;
    private final UserService userService;

    private TokenResponse validateToken(TokenRequest request)
    {
        log.info("Received request: {}", request);
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(jwtService.extractUsername(request.getToken(), true));
        return TokenResponse
            .newBuilder()
            .setIsTokenValid(jwtService.isTokenValid(request.getToken(), userDetails, true))
            .build();
    }

    @Override
    public StreamObserver<TokenRequest> validateToken(StreamObserver<TokenResponse> responseObserver)
    {
        return StreamObserverUtility.proxyStream(responseObserver, this::validateToken);
    }
}
