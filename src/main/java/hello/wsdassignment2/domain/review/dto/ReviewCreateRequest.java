package hello.wsdassignment2.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "리뷰 생성 요청")
@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    @Schema(description = "책 ID", example = "1")
    @NotNull(message = "책 ID를 입력해주세요.")
    private Long bookId;

    @Schema(description = "별점 (1-5)", example = "5")
    @NotNull(message = "별점을 입력해주세요.")
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 재미있어요!")
    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    private String content;
}
