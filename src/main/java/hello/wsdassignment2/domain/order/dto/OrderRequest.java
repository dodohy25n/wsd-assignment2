package hello.wsdassignment2.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderRequest {
    private Long userId;
    private Long bookId;
    private int count;
}
