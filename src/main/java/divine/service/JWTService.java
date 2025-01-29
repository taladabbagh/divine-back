package divine.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService {
    private static final String SECRET = "a_secure_secret_key_of_your_choice";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 hours

    public String generateToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token, String userId) {
        Claims claims = parseToken(token);
        return claims.getSubject().equals(userId) && claims.getExpiration().after(new Date());
    }

    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getEmailFromToken(String token) {
        return (String) parseToken(token).get("email");
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
