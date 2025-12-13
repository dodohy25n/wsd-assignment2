# API 설계

## 개요

이 프로젝트는 다음 주요 기능을 포함하는 온라인 서점 API를 구현합니다:

- **인증 (Authentication)**: JWT 기반 로그인, 토큰 갱신, 로그아웃
- **사용자 (Users)**: 회원가입, 내 정보 관리, (관리자) 회원 관리
- **도서 (Books)**: CRUD, 검색(키워드/가격), 페이지네이션
- **리뷰 (Reviews)**: CRUD, 도서별 리뷰 통계
- **주문 (Orders)**: CRUD

## 엔드포인트 요약

### 인증 (Auth)
- `POST /api/auth/login` : 로그인
- `POST /api/auth/refresh` : 토큰 갱신
- `POST /api/auth/logout` : 로그아웃

### 사용자 (User)
- `POST /api/users` : 회원가입
- `GET /api/users/me` : 내 정보 조회
- `PUT /api/users/me` : 내 정보 수정
- `DELETE /api/users/me` : 회원 탈퇴

### 관리자 (Admin)
- `GET /api/admin/users` : 전체 회원 조회
- `GET /api/admin/users/{userId}` : 특정 회원 상세 조회
- `DELETE /api/admin/tokens/{userId}` : 특정 유저 토큰 강제 만료
- `GET /api/admin/tokens` : 전체 리프레시 토큰 조회
- `GET /api/admin/tokens/{userId}` : 특정 유저 리프레시 토큰 조회
- `DELETE /api/admin/tokens/{userId}` : 특정 유저 리프레시 토큰 삭제


### 도서 (Books)
- `GET /api/books` : 도서 목록 조회 (검색: keyword, minPrice, maxPrice / 정렬, 페이징)
- `GET /api/books/{bookId}` : 도서 상세 조회
- `POST /api/books` : 도서 등록 
- `PUT /api/books/{bookId}` : 도서 수정
- `DELETE /api/books/{bookId}` : 도서 소프트 삭제 
- `DELETE /api/books/{bookId}/hard` : 도서 영구 삭제
- `GET /api/books/{bookId}/reviews` : 해당 도서의 리뷰 목록
- `GET /api/books/{bookId}/stats` : 해당 도서의 평점 통계

### 리뷰 (Reviews)
- `POST /api/reviews` : 리뷰 작성
- `GET /api/reviews` : 리뷰 목록 조회
- `GET /api/reviews/{reviewId}` : 리뷰 단건 조회
- `PUT /api/reviews/{reviewId}` : 리뷰 수정
- `DELETE /api/reviews/{reviewId}` : 리뷰 삭제

### 주문 (Orders)
- `POST /api/orders` : 주문 생성
- `GET /api/orders` : 주문 목록 조회
- `GET /api/orders/{orderId}` : 주문 상세 조회
- `PATCH /api/orders/{orderId}` : 주문 수정
- `DELETE /api/orders/{orderId}` : 주문 취소
