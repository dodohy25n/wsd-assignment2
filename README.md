# WSD 과제 2 - 온라인 서점 API

## 1. 프로젝트 개요

이 프로젝트는 온라인 서점 API를 구현합니다.

### 주요 기능

- **사용자 인증**: JWT 기반 로그인/로그아웃/토큰갱신 및 RBAC (일반유저/관리자) 지원.
- **도서 관리**: 전체 CRUD 지원, Soft/Hard 삭제 지원.
- **검색 및 필터**: 키워드 검색, 가격 범위 필터, 페이지네이션, 정렬 기능.
- **주문 시스템**: 재고 관리를 포함한 주문 생성 및 조회.
- **리뷰**: 도서에 대한 평점 및 코멘트 작성.

---

## 2. 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.x
- **빌드 도구**: Gradle
- **데이터베이스**: MySQL 8.0, Redis (Refresh Token 저장)
- **ORM**: Spring Data JPA + QueryDSL
- **문서화**: Swagger / Open API 3.0
- **테스트**: JUnit 5, Mockito

---

## 3. 설치 및 실행 방법

### 전제 조건

- JDK 17 이상
- MySQL Server
- Redis Server 

### 로컬 실행

1. **레포지토리 클론**
   ```bash
   git clone https://github.com/dodohy25n/wsd-assignment2.git
   cd wsd-assignment2
   ```

2. **환경 변수 설정**
 
   ```bash
   # .env 설정 예시
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=your_password
   REDIS_HOST=localhost
   REDIS_PORT=6379
   JWT_SECRET=your_secure_secret_key
   ```

3. **빌드 및 실행**
   ```bash
   # Linux/Mac
   ./gradlew clean build
   java -jar build/libs/wsd-assignment2-0.0.1-SNAPSHOT.jar

   # Windows
   ./gradlew.bat clean build
   java -jar build/libs/wsd-assignment2-0.0.1-SNAPSHOT.jar
   ```

4. **API 접속**
   - Base URL: `http://localhost:8080`
   - Health Check: `http://localhost:8080/health`

---

## 4. 환경 변수 설명

| 변수명 | 설명 | 기본값 |
|--------|------|-------|
| `DB_HOST` | MySQL 데이터베이스 호스트 | `localhost` |
| `DB_PORT` | MySQL 데이터베이스 포트 | `3306` |
| `DB_NAME` | MySQL 데이터베이스 이름 | `wsd_assignment2` |
| `DB_USER` | MySQL 사용자 이름 | `root` |
| `DB_PASSWORD` | MySQL 사용자 비밀번호 | `your_password` |
| `REDIS_HOST` | Redis 호스트 | `localhost` |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `JWT_SECRET` | JWT 서명에 사용할 비밀키 | `your_jwt_secret...` |


---

## 5. 배포 정보

- **배포 URL**: `http://113.198.66.68:10233`
- **Swagger URL**: `http://113.198.66.68:10233/docs`
- **Health URL**: `http://113.198.66.68:10233/health`

---

## 6. 인증 플로우

본 프로젝트는 JWT 기반의 인증 방식을 채택하고 있으며, 보안 강화를 위해 Refresh Token Rotation 전략을 사용합니다.

### 1단계: 로그인

1. 클라이언트가 `username`/`password`로 `/api/auth/login` 요청을 보냅니다.
2. 서버는 DB 검증 후 Access Token (1시간)과 Refresh Token (14일)을 발급합니다.
3. Refresh Token은 Redis에 `{userId: refreshToken}` 형태로 저장됩니다 (만료 시간 설정)
4. 클라이언트는 두 토큰을 응답으로 받습니다.

### 2단계: API 요청 
1. 클라이언트는 API 요청 시 Authorization Header에 `Bearer <Access Token>`을 담아 전송합니다.
2. `JwtFilter`가 요청을 가로채 토큰의 서명(Signature)과 만료 여부를 검증합니다.
3. 검증 성공 시 `SecurityContextHolder`에 인증 객체(`Authentication`)를 저장하여 요청을 허용합니다.

### 3단계: 토큰 갱신 (Refresh Token Rotation)
1. Access Token이 만료되면 클라이언트는 `/api/auth/refresh` 엔드포인트로 Refresh Token을 보냅니다.
2. 서버는 다음 단계를 거쳐 검증합니다:
   - 토큰 자체의 유효성 검사 (서명, 만료)
   - Redis에 저장된 토큰과 일치하는지 확인 (탈취된 토큰 사용 방지)
3. 검증 성공 시, 새로운 Access Token과 새로운 Refresh Token을 발급합니다.
4. Redis의 기존 토큰을 삭제하고 새로운 Refresh Token으로 교체합니다.
   - RTR 효과: Refresh Token이 탈취되더라도 이미 사용된 토큰으로 갱신을 시도하면 Redis 불일치로 인해 차단되므로 보안성이 높습니다.

---

## 7. 인증 및 권한

### 역할

| Role | 설명 |
|------|-------------|
| `ROLE_USER` | 도서 검색, 주문, 리뷰 작성, 본인 프로필 관리 가능 등|
| `ROLE_ADMIN` | 사용자 관리, 사용자 인증 관리 기능|


### 테스트 계정

- **관리자**: `admin@bookstore.com` / `1234`
- **사용자**: `user1@test.com` / `1234`

---

## 8. DB 연결 정보 (테스트용)

개발 및 테스트 환경에서의 기본 DB 연결 정보입니다. 

- **Database**: MySQL 8.0+
- **Host**: `localhost`
- **Port**: `3306`
- **Username**: `root`
- **Password**: `0000` 
- **Database Name**: `wsd_assignment2` 

## 9. 표준 응답

### 표준 성공 응답
```json
{
  "success": true,
  "code": 200,
  "message": "Success",
  "data": {
    "books": [
      {
        "id": 2,
        "title": "클린 코드",
        "summary": "애자일 소프트웨어 장인 정신",
        "isbn": "ISBN-002",
        "price": 25000.00,
        "stockQuantity": 50,
        "createdAt": "2025-12-12T22:36:45",
        "updatedAt": "2025-12-12T22:36:45"
      }
    ]
  }
}
```

### 표준 페이지네이션 응답
```json
{
  "success": true,
  "code": 200,
  "message": "Success",
  "data": {
    "books": [
        {
            "id": 1,
            "title": "JPA 프로그래밍",
            "summary": "자바 ORM 표준",
            "isbn": "ISBN-004",
            "price": 38000.00,
            "stockQuantity": 40,
            "createdAt": "2025-12-12T22:36:45",
            "updatedAt": "2025-12-12T22:36:45"
        },
        {
            "id": 2,
            "title": "모던 자바스크립트 Deep Dive",
            "summary": "자바스크립트의 모든 것",
            "isbn": "ISBN-008",
            "price": 45000.00,
            "stockQuantity": 20,
            "createdAt": "2025-12-12T22:36:45",
            "updatedAt": "2025-12-12T22:36:45"
        }
    ],
    "page": {
        "page": 1,
        "size": 10,
        "totalElements": 5,
        "totalPages": 1
    }
  }
}
```

### 표준 에러 응답
```json
{
    "success": false,
    "code": "VALIDATION_FAILED",
    "message": "입력값 유효성 검사에 실패했습니다.",
    "errors": {
        "title": "제목을 입력해주세요."
    },
    "path": "/api/books",
    "timestamp": "2025-12-13T12:23:28.0134812"
}
```

---
## 10. 엔드포인트 요약표 (URL, 메서드, 설명)

### System & Auth
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/health` | 서버 상태 및 버전 정보 확인 | Public |
| `POST` | `/api/auth/login` | 사용자 로그인 (Access/Refresh Token 발급) | Public |
| `POST` | `/api/auth/refresh` | Access Token 갱신 (RTR 적용) | Public (Refresh Token Cookie) |
| `POST` | `/api/auth/logout` | 로그아웃 (Refresh Token 삭제) | Public (Refresh Token Cookie) |

### User
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users` | 회원 가입 | Public |
| `GET` | `/api/users/me` | 내 정보 조회 | User |
| `PUT` | `/api/users/me` | 내 정보 수정 | User |
| `DELETE` | `/api/users/me` | 회원 탈퇴 | User |

### Books
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/books` | 도서 등록 | User |
| `GET` | `/api/books` | 도서 목록 조회 (검색/필터/페이징) | User |
| `GET` | `/api/books/{bookId}` | 도서 상세 조회 | User |
| `GET` | `/api/books/{bookId}/reviews` | 특정 도서 리뷰 목록 | User |
| `GET` | `/api/books/{bookId}/stats` | 특정 도서 리뷰 통계 | User |
| `PUT` | `/api/books/{bookId}` | 도서 정보 수정 | User |
| `DELETE` | `/api/books/{bookId}` | 도서 논리적 삭제 (Soft Delete) | User |
| `DELETE` | `/api/books/{bookId}/hard` | 도서 물리적 삭제 (Hard Delete) | User |


### Orders
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders` | 주문 생성 | User |
| `GET` | `/api/orders` | 내 주문 목록 조회 | User |
| `GET` | `/api/orders/{orderId}` | 주문 상세 조회 | User |
| `PATCH` | `/api/orders/{orderId}` | 주문 수정 (대기 상태일 경우) | User |
| `DELETE` | `/api/orders/{orderId}` | 주문 취소 (배송 전일 경우) | User |

### Reviews
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/reviews` | 리뷰 작성 | User |
| `GET` | `/api/reviews` | 전체 리뷰 목록 조회 | User |
| `GET` | `/api/reviews/{reviewId}` | 리뷰 상세 조회 | User |
| `PUT` | `/api/reviews/{reviewId}` | 리뷰 수정 | User (Owner) |
| `DELETE` | `/api/reviews/{reviewId}` | 리뷰 삭제 (Soft Delete) | User (Owner) |
| `DELETE` | `/api/reviews/{reviewId}/hard` | 리뷰 완전 삭제 (Hard Delete) | User (Owner) |

### Admin
| Method | URI | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/users` | 전체 사용자 목록 조회 | Admin |
| `GET` | `/api/admin/users/{userId}` | 특정 사용자 상세 조회 | Admin |
| `DELETE` | `/api/admin/users/{userId}` | 사용자 강제 탈퇴 | Admin |
| `GET` | `/api/admin/tokens` | 전체 Refresh Token 목록 | Admin |
| `GET` | `/api/admin/tokens/{userId}` | 특정 사용자 Refresh Token 조회 | Admin |
| `DELETE` | `/api/admin/tokens/{userId}` | 특정 사용자 Refresh Token 삭제 | Admin |


## 11. 성능 및 보안 고려사항

### 성능 최적화

1.  **데이터베이스 인덱싱**
    -   `@Column(unique = true)` 어노테이션을 사용하여 `users(email)`, `users(username)`, `books(isbn)` 컬럼에 유니크 인덱스를 생성했습니다. 이를 통해 중복 데이터 방지 및 `Where` 절 조회 성능을 O(1) 혹은 O(log N) 수준으로 보장합니다.

2.  **페이지네이션**
    -   `BookRepositoryImpl` (QueryDSL)에서 `Pageable` 인터페이스를 활용하여 **Offset-based Pagination**을 구현했습니다.
    -   `PageableExecutionUtils`를 사용하여 검색 결과가 페이지 사이즈보다 작을 경우, 불필요한 `Count Query` 실행을 방지하는 최적화를 적용했습니다.

3.  **동적 쿼리 및 검색 최적화**
    -   `QueryDSL`의 `BooleanExpression`을 활용하여 **검색 조건(키워드, 가격 범위)** 유무에 따라 쿼리가 동적으로 생성되도록 구현했습니다. 불필요한 `Join`이나 조건절을 제거하여 쿼리 효율을 높였습니다.

4. **Redis 기반 토큰 관리**
   -  `RefreshTokenService`에서 `RedisTemplate`을 사용하여 리프레시 토큰을 저장(`save`) 및 조회(`getByUserId`)합니다.
   -  In-Memory 저장소인 Redis의 짧은 응답 속도를 활용하여 로그인 검증 성능을 극대화했습니다.

5. **트랜잭션 최적화 (`@Transactional(readOnly = true)`)**
   - 모든 Service 클래스의 조회 메서드에 `readOnly = true` 옵션을 적용했습니다.
   - 영속성 컨텍스트의 **Dirty Checking(변경 감지)을 생략**하여 메모리 사용량을 절약하고, 조회 성능을 향상시켰습니다.

6. **지연 로딩 전략**
   - JPA 연관관계 매핑 시 `FetchType.LAZY`를 기본 전략으로 사용하여, 불필요한 연관 엔티티의 조회를 방지하고 N+1 문제를 예방하기 위한 기반을 마련했습니다.

### 보안 구현

1.  **JWT 인증 및 Refresh Token Rotation (RTR)**
    -   `SecurityConfig`에서 `SessionCreationPolicy.STATELESS`를 설정하여 완전한 무상태 인증을 구현했습니다.
    -   **Token Rotation**: `AuthService.refresh` 메서드 호출 시 Access Token뿐만 아니라 **Refresh Token도 함께 재발급**하여 Redis에 덮어씁니다. 이는 탈취된 리프레시 토큰의 재사용을 방지하고 보안성을 대폭 강화합니다.

2.  **비밀번호 암호화**
    -   `SecurityConfig`의 `passwordEncoder()` 빈 등록을 통해 `BCryptPasswordEncoder`를 사용합니다. 회원가입 시 비밀번호를 안전한 해시값으로 변환하여 DB에 저장합니다.

3.  **입력값 검증 (Validation)**
    -   `BookCreateRequest` 등 DTO에서 `@NotBlank`, `@PositiveOrZero` 등의 `Bean Validation` 어노테이션을 사용하여 입력값을 1차로 검증합니다.
    -   검증 실패 시 `GlobalExceptionHandler`가 `MethodArgumentNotValidException`을 포착하여 상세한 필드별 에러 리스트를 반환합니다.

4.  **IP 기반 레이트 리밋 (Rate Limiting)**
    -   `RateLimitFilter` 클래스에서 **Bucket4j** 라이브러리를 사용하여 구현했습니다.
    -   클라이언트 IP 별로 버킷을 생성하여 **분당 50회**로 요청을 제한하며, 초과 시 `429 Too Many Requests` 상태 코드를 반환하여 공격적인 트래픽을 차단합니다.

---

## 12. 인프라 및 CI/CD 구축
 
GitHub Actions와 Docker를 활용하여 자동화된 배포 파이프라인(CI/CD)을 구축했습니다.
 
### 인프라 아키텍처
 
1. **Container Request**: GitHub Actions가 코드를 빌드하고 Docker Image를 생성하여 Docker Hub에 Push합니다.
2. **Deploy Trigger**: 배포 서버(JCloud)에 SSH로 접속하여 최신 이미지를 Pull 받고 컨테이너를 재시작합니다.
3. **Service Orchestration**: `docker-compose`를 사용하여 Spring Boot 앱, MySQL, Redis를 하나의 네트워크로 관리합니다.
 
### CI/CD 파이프라인 (GitHub Actions)
 
| 단계 | 설명 |
|---|---|
| **CI (Continuous Integration)** | `main` 브랜치 Push 및 PR 시 트리거됩니다. <br> - **Test**: Gradle 기반 유닛/통합 테스트 수행 (MySQL/Redis 서비스 컨테이너 활용) <br> - **Build**: 애플리케이션 빌드 검증 |
| **CD (Continuous Deployment)** | `main` 브랜치 Push 시에만 트리거됩니다. <br> - **Login**: Docker Hub 로그인 <br> - **Push**: Docker Image 빌드 및 태킹(`latest`) 후 레지스트리 전송 <br> - **Deploy**: JCloud 서버에 SSH 접속 -> `docker-compose pull` -> `up -d` 실행 |

### 배포 환경 (Docker)

- **Base Image**: `eclipse-temurin:17-jre-jammy`
- **Multi-stage Build**: 빌드(Gradle)와 실행(JRE) 단계를 분리하여 이미지 크기 최적화
- **Auto Healing**: `restart: always` 정책을 적용하여 서버 재부팅이나 장애 시 자동 복구

---

## 13. 한계점 및 개선 계획

### 한계점

-   **단순한 권한 체계**: 현재 `USER`와 `ADMIN` 두 가지 역할만 존재하여, 모든 일반 사용자가 도서를 등록하거나 수정할 수 있는 구조입니다. 실제 서비스에서는 작가와 독자의 권한 분리가 필요합니다.
-   **Rate Limit 분산 처리 미비**: 현재 Rate Limit 정보가 각 인스턴스의 메모리(`ConcurrentHashMap`)에 저장됩니다. 다중 서버 배포 시 IP 제한이 서버별로 각각 적용되는 한계가 있습니다.
-   **단일 DB 의존성**: 읽기/쓰기 트래픽이 `MySQL` 단일 인스턴스에 집중되어 있어 대규모 트래픽 발생 시 병목이 생길 수 있습니다.
-   **배포 시 다운타임 발생**: 현재의 `docker-compose down -> up` 배포 방식은 컨테이너 재시작 시간 동안 서비스 중단이 발생합니다.

### 개선 계획

-   **사용자 권한 세분화**:
    -   현재의 `USER` 권한을 `AUTHOR`(작가)와 `READER`(독자)로 분리하여 비즈니스 요구사항에 맞는 권한 체계를 구축할 예정입니다.
    -   **`AUTHOR`**: 도서 등록, 수정, 삭제 및 본인 도서 관리 기능 접근 가능.
    -   **`READER`**: 도서 조회, 구매, 리뷰 작성 기능만 접근 가능.
-   **Redis 활용 범위 확장**:
    -   현재 Refresh Token 관리에만 사용 중인 Redis를 Rate Limiting (Bucket4j Redis Extension) 저장소로 확장하여 분산 환경에서도 정확한 요청 제한을 구현할 예정입니다.
    -   `@Cacheable`을 활용해 자주 조회되는 '베스트셀러'나 '카테고리 목록'을 캐싱하여 DB 부하를 줄일 계획입니다.
-   **인프라 고도화 (CI/CD)**:
    -   **무중단 배포 도입**: Nginx를 리버스 프록시로 두고 Blue/Green 배포 전략을 적용하여 배포 중 서비스 중단을 제거할 예정입니다.

---
