package com.jwt.app.web.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.SignatureException;

public class TokenManagerTest {
    public static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private TokenManager tokenManager;

    @BeforeEach
    public void setUp() {
        tokenManager = new TokenManager("2f1ceec6d77ab5e8faa776221da5e40655986062c0647db2504909cf543a135e",  120);
    }

    @Test
    public void testGenerateToken() {
        String token = tokenManager.generateToken("user");
        Assertions.assertNotNull(token);
    }

    @Test
    public void testValidateToken() {
        String token = tokenManager.generateToken("user");
        boolean isValid = tokenManager.validateToken(token, "1");
        Assertions.assertTrue(isValid);
    }

    @Test
    public void testValidateInvalidToken() {
        Assertions.assertThrows(SignatureException.class,() -> tokenManager.validateToken(INVALID_TOKEN, "1"));
    }

    @Test
    public void testValidateTokenWithInvalidId() {
        String token = tokenManager.generateToken("user");
        boolean isValid = tokenManager.validateToken(token, "2");
        Assertions.assertFalse(isValid);
    }
}