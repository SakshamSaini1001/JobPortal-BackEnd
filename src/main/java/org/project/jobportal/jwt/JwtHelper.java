package org.project.jobportal.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {

    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret"; // At least 32 characters
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 hour

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token,Claims:: getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token,Claims :: getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims,T> claimResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        CustomUserDetails customUser = (CustomUserDetails)userDetails;
        claims.put("id",customUser.getId());
        claims.put("name",customUser.getName());
        claims.put("accountType",customUser.getAccountType());
        claims.put("profileId",customUser.getProfileId());
        return doGenerateToken(claims,userDetails.getUsername());
    }

    public String doGenerateToken(Map<String,Object> claims,String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException e) {
//            System.out.println("Token expired: " + e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            System.out.println("Unsupported token: " + e.getMessage());
//        } catch (MalformedJwtException e) {
//            System.out.println("Malformed token: " + e.getMessage());
//        } catch (SecurityException | IllegalArgumentException e) {
//            System.out.println("Invalid token: " + e.getMessage());
//        }
//        return false;
//    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
