package skhumeet.backend.oauth2;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import skhumeet.backend.domain.member.Authority;
import skhumeet.backend.domain.member.Member;

import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String loginId;
    private final String name;
    private final String nickname;
    private final String profileImage;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> ofNaver(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("Not supported OAuth 2.0 API");
        };
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .loginId((String) response.get("id"))
                .name((String) response.get("name"))
                .nickname((String) response.get("nickname"))
                .profileImage((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey("id")
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .loginId((String) response.get("id"))
                .name((String) response.get("name"))
                .nickname((String) response.get("nickname"))
                .profileImage((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey("id")
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .loginId(loginId)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .authority(Authority.USER)
                .build();
    }

}
