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

## 서버 Configuration