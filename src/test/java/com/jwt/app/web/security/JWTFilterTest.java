package com.jwt.app.web.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.times;
@ExtendWith(MockitoExtension.class)
public class JWTFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JWTUserDetailsService userDetailsService;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private JWTfilter jwtFilter;

    @Test
    public void testDoFilterInternalWhenTokenIsPresentAndValid() throws Exception {
        // arrange
        String token = "validToken";
        String username = "testuser";
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        Mockito.when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.when(tokenManager.validateToken(token, "1")).thenReturn(true);
        Mockito.when(tokenManager.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(userDetails.getAuthorities()).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/user/1");

        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // assert
        Mockito.verify(tokenManager).validateToken(token, "1");
        Mockito.verify(tokenManager).getUsernameFromToken(token);
        Mockito.verify(userDetailsService).loadUserByUsername(username);
        Mockito.verify(userDetails, times(2)).getAuthorities();
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWhenTokenIsNotPresent() throws Exception {
        // arrange
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/user/2");
        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWhenTokenIsInvalid() throws Exception {
        // arrange
        String token = "invalidToken";
        Mockito.when(request.getHeader("Authorization")).thenReturn(token);
        Mockito.when(request.getRequestURI()).thenReturn("/user/2");
        Mockito.when(tokenManager.validateToken(token, "2")).thenReturn(false);

        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // assert
        Mockito.verify(tokenManager).validateToken(token, "2");
        Mockito.verify(filterChain).doFilter(request, response);
    }

}