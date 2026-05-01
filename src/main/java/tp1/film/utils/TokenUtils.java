package tp1.film.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
// pour le token utils fonction

@Component
public class TokenUtils {

    @Value("${security.token.secret}")
    private String secret;

    @Value("${security.token.expiration-ms-authentication}")
    private long authExpiration;

    @Value("${security.token.expiration-ms-confirmation}")
    private long confirmationExpiration;

    private static final String TYPE_CLAIM = "type";

    public String generateToken(String username, String ttype) {
        long expiration = ttype.equalsIgnoreCase("auth") ? authExpiration : confirmationExpiration;
        return Jwts.builder()
                .claim(TYPE_CLAIM, ttype)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractType(String token) {
        return extractClaim(token, claims -> claims.get(TYPE_CLAIM, String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, String username, String expectedType) {
        final String extractedUsername = extractUsername(token);
        final String extractedType = extractType(token);

        return (extractedUsername.equals(username) &&
                extractedType.equalsIgnoreCase(expectedType) &&
                !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}