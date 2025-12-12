package hello.wsdassignment2.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "로그인 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResponse {
    private String accessToken;
    private Long expiresIn;

    public static LoginResponse of(String accessToken, long expiresIn) {
        return new LoginResponse(accessToken, expiresIn);
    }
}
