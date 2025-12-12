package hello.wsdassignment2.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 검색 요청")
public class UserSearchRequest {

    @Schema(description = "검색어 (이름, 이메일 포함)", example = "test")
    private String keyword;

    @Schema(description = "사용자 역할 (USER, ADMIN)", example = "USER")
    private String role;
}
