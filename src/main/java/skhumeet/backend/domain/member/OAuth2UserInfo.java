package skhumeet.backend.domain.member;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getNickname();
    String getProfileImage();
}
