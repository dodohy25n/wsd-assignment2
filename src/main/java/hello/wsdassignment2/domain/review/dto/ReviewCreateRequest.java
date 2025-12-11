package hello.wsdassignment2.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {
    private Long userId;
    private Long bookId;
    private Integer rating;
    private String content;
}
