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
│         ├── coupon
│         │         ├── CouponController.java
│         │         └── dto
│         │             ├── request
│         │             │         └── RequestIssueCouponDTO.java
│         │             └── response
│         │                 └── ResponseIssueCouponDTO.java
│         ├── order
│         │         ├── OrderController.java
│         │         └── dto
│         │             ├── request
│         │             │         └── RequestCreateOrderDTO.java
│         │             └── response
│         │                 └── ResponseCreateOrderDTO.java
│         ├── payment
│         │         ├── PaymentController.java
│         │         └── dto
│         │             ├── request
│         │             │         └── RequestCreatePaymentDTO.java
│         │             └── response
│         │                 └── ResponseCreatePayment.java
│         ├── products
│         │         ├── ProductController.java
│         │         └── dto
│         │             └── response
│         │                 ├── ResponseProductListDTO.java
│         │                 └── ResponseProductTopDTO.java
│         └── user
│             ├── UserController.java
│             └── dto
│                 ├── request
│                 │         └── RequestChargeUserPointDTO.java
│                 └── response
│                     ├── ResponseChargeUserPointDTO.java
│                     ├── ResponseGetUserCoupon.java
│                     └── ResponseGetUserDTO.java
├── business
│         ├── coupon
│         │         └── CouponUseCase.java
│         ├── order
│         │         └── OrderUseCase.java
│         ├── payment
│         │         └── PaymentUseCase.java
│         ├── product
│         │         └── ProductUseCase.java
│         └── user
│             └── UserUseCase.java
├── common
│         ├── exception
│         │         ├── ErrorResponse.java
│         │         ├── custom
│         │         │         ├── AlreadyExistsException.java
│         │         │         ├── BadRequestException.java
│         │         │         ├── InvalidPaymentCancellationException.java
│         │         │         ├── LimitExceededException.java
│         │         │         └── ResourceNotFoundException.java
│         │         └── message
│         │             └── ExceptionMessage.java
│         ├── filter
│         │         └── CustomLoggingFilter.java
│         └── utils
│             └── DateTimeUtils.java
├── domain
│         ├── IDataCenter.java
│         ├── coupon
│         │         ├── Coupon.java
│         │         ├── CouponQuantity.java
│         │         ├── CouponService.java
│         │         ├── CouponState.java
│         │         ├── ICouponRepository.java
│         │         └── UserCoupon.java
│         ├── order
│         │         ├── IOrderRepository.java
│         │         ├── Order.java
│         │         ├── OrderProduct.java
│         │         ├── OrderSerivce.java
│         │         └── OrderState.java
│         ├── payment
│         │         ├── IPaymentRepository.java
│         │         ├── Payment.java
│         │         └── PaymentService.java
│         ├── product
│         │         ├── IProductRepository.java
│         │         ├── Product.java
│         │         ├── ProductQuantity.java
│         │         ├── ProductService.java
│         │         ├── ProductState.java
│         │         └── ProductTop.java
│         └── user
│             ├── IUserRepository.java
│             ├── User.java
│             └── UserService.java
└── infrastructure
    ├── coupon
    │         ├── CouponRepository.java
    │         ├── ICouponJPARepository.java
    │         ├── ICouponQuantityJPARepository.java
    │         └── IUserCouponJPARepository.java
    ├── dataCenter
    │         └── dataCenter.java
    ├── order
    │         ├── IOrderJPARepository.java
    │         ├── IOrderProductJPARepository.java
    │         └── OrderRepository.java
    ├── payment
    │         ├── IPaymentJPARepository.java
    │         └── PaymentRepository.java
    ├── product
    │         ├── IProductJPARepository.java
    │         ├── IProductQuantityJPARepository.java
    │         ├── IProductTopJPARepository.java
    │         └── ProductRepository.java
    └── user
        ├── IUserJPARepository.java
        └── UserRepository.java

```

### api

- API 레이어
- API 명세에 따라 Controller, DTO를 구성
- Controller에서 Service를 호출
- 관심사 : API 레이어는 Request를 전달 받아 값을 검증하고 UseCase에 요청 후 응답 받은 값을 Response로 정제하여 반환

### business

- 비즈니스 로직 레이어
- UseCase 를 구성 Service를 의존하여 호출
- Service Repository 인터페이스를 의존하고 Repository 구현은 어떻게 되는지 관심사가 아님
- 관심사 : API에서 어떤 행위를 하고싶은지 정의 하고 각 도메인영역의 Service를 호출하여 비즈니스 로직을 수행

### infrastructure

- 데이터 베이스 및 외부 서비스와 연동 레이어
- 외부 서비스와 연동하기 위한 구현체를 작성, 직접적으로 외부서비스를 바로 호출하지 않는 이유는 외부 서비스 변경시 영향을 최소화
- 관심사 : 외부 서비스를 추상화 하고 외부 서비스에 의존적이지 않도록 구현 ( 외부 서비스 변경시 영향 최소화 )

### domain

- 도메인 객체 레이어
- JPA Entity, 비지니스의 핵심 로직과, 각 DataBase영역의 인터페이스를 정의
- 관심사 : 도메인 객체의 상태를 관리하고 도메인 객체의 행위를 구현

### common

- 공통 레이어
- 공통적으로 사용되는 Exception, Message, Util 등을 정의

## 스웨거

- http://localhost:8080/swagger-ui/index.html
  ![05-API-명세서(Swagger).png](docs/05-API-%EB%AA%85%EC%84%B8%EC%84%9C%28Swagger%29.png)

## Chapter 2 회고록

맨 처음 코드를 작성할때 구조와 현재 작성된 코드 구조를 보았을 때 많이 발전했음을 스스로 느낍니다.
물론 아직 많이 부족하지만 이번 프로젝트를 통해 더 많은 것을 배울 수 있었습니다.
특히, 도메인 객체 설계와 패키지 구조를 어떻게 구성해야 하는지에 대해 많은 고민을 하였고, 코드의 가독성과 유지보수를 생각하며 리펙토링을 진행 한 것 같습니다.

그럼에도 아쉬운점 몇가지는
첫째로 모든 테스트 케이스를 작성하지 않더라도 NotNull에 대한 처리 및 예외처리 하지 않은 부분이 많이 아쉽습니다. ( 너무 늦게 발견하여... )
둘째로 이번에주 할 수 있었다는건 지난주에도 조금더 집중했으면 할 수 있지 않았을까? ㅎㅎ 주변에 많이 물어보고 고민할껄 이란 아쉬움이 많이 남습니다.

## 동시성 제어 방식

- [동시성 제어](docs/06-동시성%20제어방식-문서.md) 