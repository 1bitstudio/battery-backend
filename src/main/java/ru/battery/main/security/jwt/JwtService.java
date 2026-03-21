package ru.battery.main.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.battery.main.security.dto.JwtAuthenticationDto;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationDto generateAuthToken(String email, Long userId) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setAccessToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        jwtDto.setUserId(userId);
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setAccessToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSingInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSingInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Expired JwtException", expEx);
        } catch (UnsupportedJwtException expEx) {
            log.error("Unsupported JwtException", expEx);
        } catch (MalformedJwtException expEx) {
            log.error("Malformed JwtException", expEx);
        } catch (SecurityException expEx) {
            log.error("SecurityException", expEx);
        } catch (Exception expEx) {
            log.error("Invalid Token", expEx);
        }
        return false;
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
