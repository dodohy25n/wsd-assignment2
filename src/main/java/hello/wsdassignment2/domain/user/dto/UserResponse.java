package hello.wsdassignment2.domain.user.dto;

import hello.wsdassignment2.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 정보 응답")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponse {

    private Long id;
    private String email;
    private String name;

    private UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }
}
