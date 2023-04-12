package skhumeet.backend.token;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import skhumeet.backend.domain.dto.TokenDTO;
import skhumeet.backend.repository.MemberRepository;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static skhumeet.backend.token.JwtExpirationEnums.ACCESS_TOKEN_EXPIRATION_TIME;
import static skhumeet.backend.token.JwtExpirationEnums.REFRESH_TOKEN_EXPIRATION_TIME;

@Slf4j
@Component
public class TokenProvider {
    private final Key key;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final MemberRepository memberRepository;

    public TokenProvider(
            @Value("${jwt.secret}") String secretKey,
            RefreshTokenRedisRepository refreshTokenRedisRepository,
            MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.memberRepository = memberRepository;
    }

    public TokenDTO createTokens(String id) {
        String accessToken = generateAccessToken(id);
        String refreshToken = saveRefreshToken(id).getRefreshToken();

        return new TokenDTO(accessToken, refreshToken);
    }

    // Token util methods
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveToken(String token) {
        return token.substring(7);
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.get("id").toString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private String getCurrentUserAuthorities(String id) {
        return memberRepository.findByLoginId(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found")).getAuthority().toString();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new JwtException("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new JwtException("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new JwtException("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("Claims not found JWT Token", e);
            throw new JwtException("Claims not found JWT Token");
        }
    }

    // Access Token
    public String generateAccessToken(String loginId) {
        Claims claims = Jwts.claims();
        claims.put("id", loginId);
        claims.put("auth", getCurrentUserAuthorities(loginId));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME.getValue()))
                .signWith(key)
                .compact();
    }

    // Refresh Token
    private RefreshToken saveRefreshToken(String id) {
        return refreshTokenRedisRepository.save(
                RefreshToken.createRefreshToken(
                        id,
                        generateRefreshToken(id),
                        REFRESH_TOKEN_EXPIRATION_TIME.getValue()
                )
        );
    }

    public String generateRefreshToken(String id) {
        Claims claims = Jwts.claims();
        claims.put("id", id);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME.getValue()))
                .signWith(key)
                .compact();
    }

    // 토큰 재발급
    public TokenDTO reissue(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        String username = claims.get("id").toString();
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username)
                .orElseThrow(NoSuchElementException::new);

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            return reissueToken(refreshToken);
        }
        throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }

    private TokenDTO reissueToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        String username = claims.get("id").toString();
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            String accessToken = generateAccessToken(username);
            return TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(saveRefreshToken(username).getRefreshToken())
                    .build();
        }
        return TokenDTO.builder()
                .accessToken(generateAccessToken(username))
                .refreshToken(refreshToken)
                .build();
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return getRemainMilliSeconds(refreshToken) < JwtExpirationEnums.REISSUE_EXPIRATION_TIME.getValue();
    }

    public long getRemainMilliSeconds(String token) {
        Date expiration = parseClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }
}