-- V2__seed_data.sql
-- 초기 테스트 데이터 시딩

-- --------------------------------------------------------
-- 1. Users 데이터 (10명)
-- --------------------------------------------------------
INSERT INTO `user` (username, email, password, name, role, created_at, updated_at) VALUES
('admin@bookstore.com', 'admin@bookstore.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '관리자', 'ROLE_ADMIN', NOW(), NOW()),
('user1@test.com', 'user1@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '김철수', 'ROLE_USER', NOW(), NOW()),
('user2@test.com', 'user2@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '이영희', 'ROLE_USER', NOW(), NOW()),
('user3@test.com', 'user3@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '박민수', 'ROLE_USER', NOW(), NOW()),
('user4@test.com', 'user4@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '최자바', 'ROLE_USER', NOW(), NOW()),
('user5@test.com', 'user5@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '정스프링', 'ROLE_USER', NOW(), NOW()),
('user6@test.com', 'user6@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '강파이썬', 'ROLE_USER', NOW(), NOW()),
('user7@test.com', 'user7@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '조리액트', 'ROLE_USER', NOW(), NOW()),
('user8@test.com', 'user8@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '윤데이터', 'ROLE_USER', NOW(), NOW()),
('user9@test.com', 'user9@test.com', '$2a$10$61RGStMjBUg.R2OKYe5gAO6b0ngrAhtkMnTfnK/mV/Hmrcx0p.e7u', '한클라우드', 'ROLE_USER', NOW(), NOW());

-- --------------------------------------------------------
-- 2. Book 데이터 (20권)
-- --------------------------------------------------------
INSERT INTO `book` (title, summary, isbn, price, stock_quantity, created_at, updated_at) VALUES
('헤드 퍼스트 디자인 패턴', '재미있게 배우는 객체지향', 'ISBN-001', 32000, 100, NOW(), NOW()),
('클린 코드', '애자일 소프트웨어 장인 정신', 'ISBN-002', 25000, 50, NOW(), NOW()),
('스프링 부트 완벽 가이드', '스프링의 정석', 'ISBN-003', 40000, 30, NOW(), NOW()),
('JPA 프로그래밍', '자바 ORM 표준', 'ISBN-004', 38000, 40, NOW(), NOW()),
('리액트를 다루는 기술', '프론트엔드 개발의 시작', 'ISBN-005', 28000, 60, NOW(), NOW()),
('Do it! 점프 투 파이썬', '가장 쉬운 파이썬 입문서', 'ISBN-006', 18000, 100, NOW(), NOW()),
('혼자 공부하는 SQL', '데이터베이스 기초', 'ISBN-007', 22000, 80, NOW(), NOW()),
('모던 자바스크립트 Deep Dive', '자바스크립트의 모든 것', 'ISBN-008', 45000, 20, NOW(), NOW()),
('토비의 스프링 3.1', '스프링의 바이블', 'ISBN-009', 65000, 10, NOW(), NOW()),
('이펙티브 자바', '자바 전문가로 가는 길', 'ISBN-010', 36000, 45, NOW(), NOW()),
('해리포터와 마법사의 돌', '판타지 소설의 고전', 'ISBN-011', 15000, 200, NOW(), NOW()),
('반지의 제왕 1', '톨킨의 명작', 'ISBN-012', 18000, 150, NOW(), NOW()),
('어린 왕자', '어른들을 위한 동화', 'ISBN-013', 9000, 300, NOW(), NOW()),
('나미야 잡화점의 기적', '히가시노 게이고 장편소설', 'ISBN-014', 14000, 120, NOW(), NOW()),
('코스모스', '칼 세이건의 우주 이야기', 'ISBN-015', 25000, 50, NOW(), NOW()),
('사피엔스', '유발 하라리의 인류사', 'ISBN-016', 22000, 60, NOW(), NOW()),
('총 균 쇠', '인류 문명의 기원', 'ISBN-017', 28000, 40, NOW(), NOW()),
('부의 추월차선', '경제적 자유를 위하여', 'ISBN-018', 16000, 90, NOW(), NOW()),
('역행자', '돈과 시간의 자유', 'ISBN-019', 19000, 110, NOW(), NOW()),
('돈의 속성', '최상위 부자가 말하는 돈', 'ISBN-020', 17000, 130, NOW(), NOW());

-- --------------------------------------------------------
-- 3. Orders 데이터
-- --------------------------------------------------------
INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 50000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 30000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 75000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 15000, 'CANCELLED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 42000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 88000, 'SHIPPED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 25000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 36000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 19000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

INSERT INTO `orders` (user_id, total_price, status, created_at, updated_at)
SELECT id, 62000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), NOW()
FROM `user` WHERE role = 'ROLE_USER';

-- --------------------------------------------------------
-- 4. OrderItem 데이터
-- --------------------------------------------------------

-- 모든 주문에 첫 번째 책(아이템) 추가
INSERT IGNORE INTO `order_item` (order_id, book_id, quantity, price_at_order)
SELECT o.id,
       (SELECT id FROM book ORDER BY RAND() LIMIT 1), -- 랜덤 책
       1,
       25000
FROM `orders` o;

-- 일부 주문(짝수 ID)에 두 번째 책 추가
INSERT IGNORE INTO `order_item` (order_id, book_id, quantity, price_at_order)
SELECT o.id,
       (SELECT id FROM book ORDER BY RAND() LIMIT 1),
       2,
       15000
FROM `orders` o
WHERE o.id % 2 = 0;

-- --------------------------------------------------------
-- 5. Review 데이터 (약 50건)
-- --------------------------------------------------------
INSERT INTO `review` (user_id, book_id, rating, content, created_at, updated_at)
SELECT
    (SELECT id FROM user WHERE role='ROLE_USER' ORDER BY RAND() LIMIT 1),
    (SELECT id FROM book ORDER BY RAND() LIMIT 1),
    5, '인생 책입니다. 강력 추천!', NOW(), NOW()
FROM `user` LIMIT 5;

INSERT INTO `review` (user_id, book_id, rating, content, created_at, updated_at)
SELECT
    (SELECT id FROM user WHERE role='ROLE_USER' ORDER BY RAND() LIMIT 1),
    (SELECT id FROM book ORDER BY RAND() LIMIT 1),
    4, '내용이 알차고 좋습니다.', NOW(), NOW()
FROM `user` LIMIT 5;

INSERT INTO `review` (user_id, book_id, rating, content, created_at, updated_at)
SELECT
    (SELECT id FROM user WHERE role='ROLE_USER' ORDER BY RAND() LIMIT 1),
    (SELECT id FROM book ORDER BY RAND() LIMIT 1),
    3, '그저 그렇네요. 평범합니다.', NOW(), NOW()
FROM `user` LIMIT 5;

INSERT INTO `review` (user_id, book_id, rating, content, created_at, updated_at)
SELECT
    (SELECT id FROM user WHERE role='ROLE_USER' ORDER BY RAND() LIMIT 1),
    (SELECT id FROM book ORDER BY RAND() LIMIT 1),
    5, '배송도 빠르고 책 상태도 좋아요.', NOW(), NOW()
FROM `user` LIMIT 5;

-- 위의 20건을 기반으로 반복
INSERT INTO `review` (user_id, book_id, rating, content, created_at, updated_at)
SELECT
    (SELECT id FROM user WHERE role='ROLE_USER' ORDER BY RAND() LIMIT 1),
    (SELECT id FROM book ORDER BY RAND() LIMIT 1),
    FLOOR(1 + (RAND() * 5)), -- 1~5 랜덤 평점
    '랜덤 리뷰 내용입니다.',
    NOW(), NOW()
FROM `orders` LIMIT 30;
