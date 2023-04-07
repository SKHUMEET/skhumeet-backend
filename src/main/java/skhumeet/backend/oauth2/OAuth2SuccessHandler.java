package skhumeet.backend.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import skhumeet.backend.domain.dto.TokenDTO;
import skhumeet.backend.domain.dto.MemberDTO;
import skhumeet.backend.repository.MemberRepository;
import skhumeet.backend.token.TokenProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        MemberDTO.Response memberResponse = MemberDTO.Response.builder()
                .id(attributes.get("id").toString())
                .name(attributes.get("name").toString())
                .nickname(attributes.get("nickname").toString())
                .profileImage(attributes.get("profile_image").toString())
                .build();

        TokenDTO tokens = tokenProvider.createTokens(memberResponse.getId());

        String targetUrl = UriComponentsBuilder.fromUriString("/")
                .queryParam("accessToken", tokens.getAccessToken())
                .queryParam("refreshToken", tokens.getRefreshToken())
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


}
