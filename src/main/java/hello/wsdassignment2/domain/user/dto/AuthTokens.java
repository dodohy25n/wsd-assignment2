package hello.wsdassignment2.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}