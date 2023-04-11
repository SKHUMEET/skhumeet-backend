package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhumeet.backend.domain.member.Authority;
import skhumeet.backend.domain.member.Member;

public class MemberDTO {
    @Schema(name = "MemberDTO.Join (회원가입 요청 DTO)")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Join {
        @Schema(description = "Member ID (학번, 교직원 번호)", defaultValue = "201900000")
        private String memberNumber;
        @Schema(description = "Provided OAuth 2.0 ID (OAuth 2.0 로그인 시 제공받은 ID)", defaultValue = "")
        private String loginId;
        @Schema(description = "Name (이름)", defaultValue = "김회대")
        private String name;
        @Schema(description = "Nickname (별명)", defaultValue = "스쿠밋회원")
        private String nickname;
        @Schema(description = "Profile Image (프로필 사진)", defaultValue = "")
        private String profileImage;

        public Member toEntity() {
            return Member.builder()
                    .memberNumber(this.memberNumber)
                    .loginId(this.loginId)
                    .name(this.name)
                    .nickname(this.nickname)
                    .authority(Authority.USER)
                    .profileImage(this.profileImage)
                    .build();
        }
    }

    @Schema(name = "MemberDTO.Login (로그인 요청 DTO)")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Login {
        @Schema(description = "Provided OAuth 2.0 ID (OAuth 2.0 로그인 시 제공받은 ID)", defaultValue = "")
        private String loginId;
    }

    @Schema(name = "MemberDTO.Update (회원 정보 수정 DTO)")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        @Schema(description = "Profile Image (프로필 사진)", defaultValue = "")
        private String profileImage;
    }

    @Schema(name = "MemberDTO.Response (회원 정보 반환 DTO")
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @Schema(description = "Member ID (학번, 교직원 번호)")
        private String memberNumber;
        @Schema(description = "Provided ID from OAuth 2.0 API (OAuth 2.0 로그인 시 제공받은 ID)")
        private String loginId;
        @Schema(description = "Name (이름)")
        private String name;
        @Schema(description = "Nickname (별명)")
        private String nickname;
        @Schema(description = "Profile Image (프로필 사진)", defaultValue = "")
        private String profileImage;

        public Response(Member member) {
            this.memberNumber = member.getMemberNumber();
            this.loginId = member.getLoginId();
            this.name = member.getName();
            this.nickname = member.getNickname();
            this.profileImage = member.getProfileImage();
        }
    }

}
