package skhumeet.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhumeet.backend.domain.member.Authority;
import skhumeet.backend.domain.member.Member;

public class MemberDTO {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String memberNumber;
        private String loginId;
        private String name;
        private String nickname;
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

    @Schema(name = "MemberDTO.Response")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private String memberNumber;
        private String id;
        private String name;
        private String nickname;
        private String profileImage;

        public Response(Member member) {
            this.memberNumber = member.getMemberNumber();
            this.id = member.getLoginId();
            this.name = member.getName();
            this.nickname = member.getNickname();
            this.profileImage = member.getProfileImage();
        }
    }

}
