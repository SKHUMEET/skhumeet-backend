package skhumeet.backend.domain.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

public class MemberDTO {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinRequest {
        private String memberNumber;
        private String email;
        private String password;
        private String checkedPassword;
        private String name;
        private String authority;

        public Member toEntity() {
            return Member.builder()
                    .memberNumber(this.memberNumber)
                    .email(this.email)
                    .password(this.password)
                    .name(this.name)
                    .authority(Authority.valueOf(authority))
                    .build();
        }
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private String memberNumber;
        private String email;
        private String name;

        public Response(Member member) {
            this.memberNumber = member.getMemberNumber();
            this.email = member.getEmail();
            this.name = member.getName();
        }
    }

}
