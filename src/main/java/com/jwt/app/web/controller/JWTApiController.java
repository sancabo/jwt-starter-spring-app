package com.jwt.app.web.controller;

import com.jwt.app.web.dto.AuthenticationRequestDTO;
import com.jwt.app.web.dto.AuthenticationResponseDTO;
import com.jwt.app.web.dto.UserProfileDTO;
import com.jwt.app.web.security.JWTUserDetailsService;
import com.jwt.app.web.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;

@RestController
@CrossOrigin
public class JWTApiController {
    private final static Logger logger = LoggerFactory.getLogger(JWTApiController.class);

    @Autowired
    private JWTUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(name = "id") Integer id
    ) {
        return getUserById(id);
    }

    private ResponseEntity<UserProfileDTO> getUserById(Integer id) {
        if(id.equals(1)){
            return ResponseEntity.ok(buildExampleUser(1));
        } else if(id.equals(2)){
            return ResponseEntity.ok(buildExampleUser(2));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private UserProfileDTO buildExampleUser(Integer id) {
        return UserProfileDTO.builder()
                .bio("An average person with id=" + id)
                .birthDay(Date.from(Instant.now()))
                .name("Santiago-" + id)
                .lastName("Cabo")
                .verified(true)
                .build();
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticateDummyToken(
            @RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUser(), request.getPassword())
            );
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthenticationResponseDTO("Error Authenticating"));
        }
        String jwtToken = tokenManager.generateToken(request.getUser());
        return ResponseEntity.ok(new AuthenticationResponseDTO(jwtToken));
    }

}
