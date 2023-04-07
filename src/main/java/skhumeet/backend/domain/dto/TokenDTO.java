package skhumeet.backend.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
}