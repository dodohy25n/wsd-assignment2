# 시스템 아키텍처 (System Architecture)

## 개요

이 프로젝트는 **Spring Boot 3.x**를 기반으로 하며, 표준 계층형 아키텍처(Layered Architecture)를 따릅니다.

## 기술 스택

- **Framework**: Spring Boot 3
- **Language**: Java 17+
- **Build Tool**: Gradle
- **Database**: 
  - MySQL (메인 저장소)
  - Redis (토큰 블랙리스트/캐시 - 선택 사항)
- **ORM**: JPA (Hibernate), QueryDSL (동적 쿼리 및 복잡한 검색)
- **Documentation**: Swagger UI (SpringDoc)
- **Security**: Spring Security + JWT

## 계층 구조

1.  **Presentation Layer (`controller`)**
    - HTTP 요청 처리, 입력값 검증 (`@Valid`), DTO 매핑 담당
    - 전역 예외 처리 (`GlobalExceptionHandler`)를 통해 일관된 에러 응답 반환
2.  **Business Layer (`service`)**
    - `@Transactional` 기반의 핵심 비즈니스 로직 수행
    - 엔티티 상태 변경 및 도메인 규칙 적용
3.  **Data Access Layer (`repository`)**
    - Spring Data JPA 인터페이스를 통한 기본 CRUD
    - QueryDSL을 활용한 복잡한 검색 및 동적 쿼리 구현 (`CustomRepository`)
4.  **Domain Layer (`entity`)**
    - DB 테이블과 매핑되는 JPA 엔티티

## 폴더 구조

- `common/`: 전역 설정, 공통 예외(Exception), 필터(Filter), 응답(Response) 객체, 유틸리티
- `domain/`: 도메인별 패키지 구성 (user, book, order, review, admin 등)
    - `controller`: API 엔드포인트
    - `service`: 비즈니스 로직
    - `repository`: DB 접근 계층
    - `entity`: 도메인 모델
    - `dto`: 데이터 전송 객체
- `security/`: Spring Security 설정, JWT 프로바이더/필터, 인증 객체 구현
