# 프로젝트 선정

`e-commerce` 를 선정 하였습니다.

## Milestone

- 진행 가능 한 날 : 월,화,수,목,일 ( 주 5일 )
- 1MD : 1일 ( 3시간 )

### 설계 문서 작성 (3MD)

- 요구 사항 분석
- UML
    - 시퀀스 다이어그램
    - 플로우 차트
    - ERD
- 도메인 객체 설계

### API 명세 작성 (2MD)

- API 명세 작성
- Mock 개발
- 패키지 주고 설계

### 구현 (5MD)

- 개발 환경 구성
- Test 코드 작성
- 요구사항 API 개발

## [요구사항 분석](https://docs.google.com/spreadsheets/d/1t_x48OX8v5qGE9aPk9-a_7EixDlwSdpn_QKgB-MDL4c/edit?usp=sharing)

## 플로우 차트

### [포인트 충전/조회](docs/01-플로우차트-포인트충전_조회.png)

### [선착순 쿠폰 발급](docs/01-플로우차트-선착순_쿠폰_발급.png)

### [주문/결재](docs/01-플로우차트-주문_결재.png)

## 시퀀스 다이어그램

### [포인트 충전/조회](docs/02-시퀀스-포인트충전_조회.png)

### [상품 조회](docs/02-시퀀스-상품_목록_조회.png)

### [선착순 쿠폰 발급](docs/02-시퀀스-선착순_쿠폰_발급.png)

### [주문/결재](docs/02-시퀀스-주문_결재.png)

## [ERD 설계](docs/03-ERD.png)

- 쿠폰, 제품 테이블에 수량을 추가 할 경우 조회 시 Lock이 걸림으로 조회하는 사람들 전체에 영향일 미처 별도의 테이블로 분리
- 쿠폰, 제품 테이블에 현재 상태를 저장하는 enum 컬럼을 추가 하여 재고 소진 여부를 상태값으로 표기 ( 수량 표기 하지 않음 )
- 판매 상위 3개 제품의 조회의 경우 스케쥴을 이용해 최근 3일간의 데이터를 취합, 통계를 view에 저장하여 ToP3 제품을 조회
- 주문이 생성 될 때 재고 차감, 결재가 취소시 주문 취소 및 재고 복구

## [API 명세](https://docs.google.com/spreadsheets/d/1t_x48OX8v5qGE9aPk9-a_7EixDlwSdpn_QKgB-MDL4c/edit?gid=0#gid=0)

## 패키지 구조

```
.
├── ECommerceApplication.java
├── GlobalExceptionHandler.java
├── api 
│     ├── coupon
│     │     ├── CouponController.java
│     │     └── dto
│     │         ├── request
│     │         │     └── RequestIssueCouponDTO.java
│     │         └── response
│     │             └── ResponseIssueCouponDTO.java
├── business
│     ├── coupon
│     │     ├── CouponService.java
│     │     └── ICouponRepository.java
│     ├── order
├── common
│     ├── exception
│     │     ├── ErrorResponse.java
│     │     ├── custom
│     │     │     ├── AlreadyExistsException.java
│     │     │     ├── BadRequestException.java
│     │     │     ├── ConflictException.java
│     │     │     ├── LimitExceededException.java
│     │     │     └── ResourceNotFoundException.java
│     │     └── message
│     │         └── ExceptionMessage.java
│     └── utils
├── domain
│     ├── coupon
│     │     └── Coupon.java
│     └── enums
│         ├── OrderState.java
│         └── ProductState.java
└── infrastructure
    └── coupon
        ├── CouponRepository.java
        └── ICouponJPARepository.java
```

### api

- API 레이어
- API 명세에 따라 Controller, DTO를 구성
- Controller에서 Service를 호출
- 관심사 : API 레이어는 Request를 전달 받아 값을 검증하고 Service에 요청 후 응답 받은 값을 Response로 정제하여 반환

### business

- 비즈니스 로직 레이어
- Service, Loader, Repository 인터페이스를 구성
- Service에서 Loader 의존하고 Loader에서 생성되어 있는 기능을 가져다 사용함
- Loader에서 Repository 인터페이스를 의존하고 Repository 구현은 어떻게 되는지 관심사가 아님
- 관심사 : Repository에게 데이터를 요청하여 응답받고 비즈니스 로직 ( 코드로 이루워진 CodeBase 핵심 로직 )을 구현

### infrastructure

- 데이터 베이스 및 외부 서비스와 연동 레이어
- 외부 서비스와 연동하기 위한 구현체를 작성, 직접적으로 외부서비스를 바로 호출하지 않는 이유는 외부 서비스 변경시 영향을 최소화
- 관심사 : 외부 서비스를 추상화 하고 외부 서비스에 의존적이지 않도록 구현 ( 외부 서비스 변경시 영향 최소화 )

### domain

- 도메인 객체 레이어
- JPA Entity를 비지니스의 핵심 도메인으로 정의, 코드 전역으로 사용될 enum을 정의
- 관심사 : 도메인 객체의 상태를 관리하고 도메인 객체의 행위를 구현

### common

- 공통 레이어
- 공통적으로 사용되는 Exception, Message, Util 등을 정의

## 스웨거

![05-API-명세서(Swagger).png](docs/05-API-%EB%AA%85%EC%84%B8%EC%84%9C%28Swagger%29.png)