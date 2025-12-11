package hello.wsdassignment2.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {
    private Integer rating;
    private String content;
}
