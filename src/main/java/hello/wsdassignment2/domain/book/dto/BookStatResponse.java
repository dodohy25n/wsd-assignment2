package hello.wsdassignment2.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "리뷰 통계 응답")
public class BookStatResponse {

    @Schema(description = "평균 평점 (소수점 첫째 자리 반올림)", example = "4.5")
    private double averageRating;

    @Schema(description = "전체 리뷰 개수", example = "120")
    private long reviewCount;

    // JPQL에서 호출할 생성자
    public BookStatResponse(Double averageRating, Long reviewCount) {
        // null 방지 및 소수점 첫째 자리 반올림 처리
        double rating = (averageRating != null) ? averageRating : 0.0;
        this.averageRating = Math.round(rating * 10) / 10.0;

        this.reviewCount = (reviewCount != null) ? reviewCount : 0;
    }
}