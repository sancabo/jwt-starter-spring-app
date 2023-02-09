package com.jwt.app.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTfilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTfilter.class);

    @Autowired
    private JWTUserDetailsService userDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        String idFromReq = request.getRequestURI().replace("/user/","");

        if(token == null) {
            logger.info("Request without token");
        }
        else if(tokenManager.validateToken(token, idFromReq)) {
            String username = tokenManager.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new
                    WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);

    }
}
