package com.jwt.app.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {
    public static final String USER_ID = "userId";
    private final Logger logger  = LoggerFactory.getLogger(TokenManager.class);
    private final String jwtSecret;
    private final Integer tokenValidity;

    public TokenManager(@Value("${token.secret}") String jwtSecret,  @Value("${token.validity}") Integer tokenValidity){
        this.jwtSecret = jwtSecret;
        this.tokenValidity = tokenValidity;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, "1");
        return Jwts.builder().setClaims(claims).setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean validateToken(String tokenFromHeader, String idFromReq){
        String token = tokenFromHeader.replace("Bearer ","");
        String header = token.split( "\\." )[0];
        logger.info("header: {}", header);
        String payload = token.split( "\\." )[1];
        logger.info("payload: {}", payload);
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
        String algorithm = claimsJws.getHeader().getAlgorithm();
        logger.info("algo: {}", algorithm);
        Claims body = claimsJws.getBody();
        String generatedSignature = generateSignature(header, payload);
        logger.info("generatedSignature: {}", generatedSignature);
        logger.info("retrievedSignature: {}", claimsJws.getSignature());
        logger.info("id={}, id={}", idFromReq, body.get(USER_ID,String.class));

        boolean isSigned = !StringUtils.isEmpty(claimsJws.getSignature());
        boolean algorithmMatches = SignatureAlgorithm.HS512.getValue().matches(algorithm);
        boolean signatureMatches = claimsJws.getSignature().matches(generatedSignature);
        boolean isTokenExpired = body.getExpiration().before(new Date());
        boolean idMatches = idFromReq.equals(body.get(USER_ID,String.class));

        return isSigned
                && algorithmMatches
                && signatureMatches
                && !isTokenExpired
                && idMatches;
    }

    private String generateSignature(String header, String payload) {
        try {
            SecretKeySpec secret = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS512.getJcaName());
            Mac mac = Mac.getInstance(SignatureAlgorithm.HS512.getJcaName());
            mac.init(secret);
            String body = header + "." + payload;
            logger.info("Generating test signature for {}", body);
            byte[] hmacDataBytes = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacDataBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getUsernameFromToken(String tokenFromHeader) {
        String token = tokenFromHeader.replace("Bearer ","");
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
