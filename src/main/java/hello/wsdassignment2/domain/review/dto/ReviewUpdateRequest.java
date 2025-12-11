package hello.wsdassignment2.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "리뷰 수정 요청")
@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {

    @Schema(description = "별점 (1-5)", example = "4")
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "수정된 내용입니다.")
    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    private String content;
}
