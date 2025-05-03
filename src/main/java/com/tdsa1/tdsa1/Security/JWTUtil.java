package com.tdsa1.tdsa1.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {


    private final long Expration_time = 1000 * 60 * 60;//one hour


    private final String SECRET = "my_secret_key_which_should_be_at_least_32_characters!";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());//create key type from  our secret

    public String createToken(String username) {


        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Expration_time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact(); // compact all the these and return a token string fo us

    }


    // Get username from JWT token

    public String extractUsername(String token) {

        Claims body = Jwts.parserBuilder()//préparer un objet capable de lire/décoder un token JWT.
                .setSigningKey(key)//quelle clé secrète utiliser pour vérifier la signature du token.
                .build()
                .parseClaimsJws(token)//extract everything from the token
                .getBody();


        return body.getSubject();


    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

}
