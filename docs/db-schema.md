# 데이터베이스 스키마 (Database Schema)

이 애플리케이션은 관계형 데이터베이스(MySQL)를 사용합니다.

## 테이블 구성 (Tables)

### `users` (사용자)
- `id` (PK, BigInt, Auto Inc): 사용자 고유 ID
- `email` (Varchar, Unique): 이메일 (로그인 ID)
- `password` (Varchar): 암호화된 비밀번호
- `name` (Varchar): 사용자 이름
- `role` (Enum): 역할 (ROLE_USER, ROLE_ADMIN)
- `created_at`, `updated_at`: 생성일시, 수정일시

### `books` (도서)
- `id` (PK, BigInt, Auto Inc): 도서 고유 ID
- `title` (Varchar): 도서 제목
- `summary` (Text): 줄거리/설명
- `isbn` (Varchar, Unique): ISBN 번호
- `price` (Decimal): 가격
- `stock_quantity` (Int): 재고 수량
- `deleted_at` (Timestamp): Soft Delete를 위한 삭제 일시 (Nullable)
- `created_at`, `updated_at`: 생성일시, 수정일시

### `reviews` (리뷰)
- `id` (PK, BigInt, Auto Inc): 리뷰 고유 ID
- `user_id` (FK -> users.id): 작성자
- `book_id` (FK -> books.id): 대상 도서
- `content` (Text): 리뷰 내용
- `rating` (Int): 평점 (1~5)
- `created_at`, `updated_at`: 생성일시, 수정일시

### `orders` (주문)
- `id` (PK, BigInt, Auto Inc): 주문 고유 ID
- `user_id` (FK -> users.id): 주문자
- `status` (Enum): 주문 상태 (PENDING, PAID, CANCELLED 등)
- `total_price` (Decimal): 총 주문 금액
- `created_at`, `updated_at`: 생성일시, 수정일시

### `order_items` (주문 상세)
- `id` (PK, BigInt, Auto Inc): 주문 항목 ID
- `order_id` (FK -> orders.id): 상위 주문
- `book_id` (FK -> books.id): 주문 도서
- `order_price` (Decimal): 주문 당시 도서 가격 (가격 변동 대응)
- `count` (Int): 주문 수량

## 인덱스 (Indexes)
- `users`: `email` (Unique Index)
- `books`: `isbn` (Unique Index)

## 연관관계 (Relationships)
- **User - Order**: 1:N (한 사용자는 여러 주문을 할 수 있음)
- **Order - OrderItem**: 1:N (한 주문에는 여러 도서가 포함될 수 있음)
- **Book - Review**: 1:N (한 도서에는 여러 리뷰가 달릴 수 있음)
- **User - Review**: 1:N (한 사용자는 여러 리뷰를 작성할 수 있음)
