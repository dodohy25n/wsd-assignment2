package hello.wsdassignment2.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "리프레시 토큰 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    private Long userId;
    private String refreshToken;
    private Long timeToLive; // 남은 시간 (초)
}
