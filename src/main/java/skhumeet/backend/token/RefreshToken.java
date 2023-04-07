package skhumeet.backend.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor
@RedisHash("refreshToken")
public class RefreshToken {
    @Id
    private String id;

    private String refreshToken;

    //설정한 시간 만큼 데이터를 저장함. 설정한 시간이 지나면 자동으로 해당 데이터가 사라지는 휘발 역할
    @TimeToLive
    private Long expiration;

    public static RefreshToken createRefreshToken(String id, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .id(id)
                .refreshToken(refreshToken)
                .expiration(remainingMilliSeconds / 1000)
                .build();
    }
}
