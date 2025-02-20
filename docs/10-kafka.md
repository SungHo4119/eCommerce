# 나 홀로 카프카 정리

## 개요 (Introduction)

### 1.1. Kafka 정의와 배경

#### 메시지 큐 vs 분산형 스트리밍 플랫폼 개념 비교

- 메시지 큐:
    - 큐(Queue) 형태로 데이터를 저장하고, 이를 Consumer가 처리
    - 보통 메시지를 한번 소비하면 큐에서 사라짐
- 분산형 스트리밍 플랫폼
    - 데이터가 지속적으로 생성되고, 이를 실시간으로 처리 또는 전달하고자 할 떄 사용
    - 데이터를 장기간 보관하면서 Consumer가 원하는 시점에 접근하여 처리
    - 확장 가능한 Partitioning 및 Replicate 기능으로 대용량 데이터 처리 가능

#### Kafka가 등장하게 된 이유 및 역사

- 대용량 로그 수집시 방대한 양의 이벤트 로그를 효율적으로 처리하고 다양한 시스템의 모니터링을 처리해야 요구 사항이 증가
- 일반적인 메시지 큐는 로그 같은 고속, 대량의 데이터를 다루기에 비효율적
- 수평적 확장을 지원하는 시스템이 필요

### 1.2. 사용 사례(Use Case)

#### Kafka

- 로그 수집
    - 각 서버에서 발생하는 로그를 kafka 토픽에 모으고 실시간으로 Consumer(ELK Stack 같은..)가 로그를 가져감
- 이벤트 스트리밍
    - 이벤트를 Kafka 발행하고, 이벤트의 동작주체인 Consumer가 이벤트를 구독하여 처리
    - 다수의 서비스를 연결하여 이벤트기반 아키텍처를 구성

## 핵심 개념 (Core Concepts)

### 2.1. Topic & Partition

#### 토픽 분할(Partition) 개념과 오프셋(Offset)

- Topic
    - 메시지를 주고 받는 논리적 구분 단위 (카테고리)
- Partition
    - 하나의 토픽이 여러 파티션 단위로 구성
    - 파티션은 메시지를 순서대로 쌓아두며 Offset을 통해 식별
- Offset
    - 파티션 내에서 메시지가 저장된 위치를 나타내는 값
    - 파티션 별로 오프셋은 별도로 관리됨

#### 스케일 아웃과 데이터 분산 전략

- 스케일 아웃
    - 더 많은 브로커, 파티션을 추가해 처리량을 높이는 방법 (수평 확장)
- 데이터 분산 전략
    - Partition Key
        - Partition Key 를 통해 Producer가 메시지를 전송할 때 Hash 함수를 이용해 특정 파티션에 할당
    - Round-robin
        - 파티션 키가 없거나 Null인 경우 라운드 로빈 방식으로 파티션에 메시지를 분산
        - 모든 파티션에 골고루 메시지가 들어가도록 분산 전략 사용

### 2.2. 메시지 보존 정책 (Retention Policy)

#### 메시지 저장 기간

- Time-base Retention
    - 메시지가 저장된 시간을 기준으로 일정 기간 이후 삭제
- Size-base Retention
    - 토픽의 데이터 크기를 기준으로 일정 크기 이상이 되면 삭제
- Log Compaction
    - 특정 키를 기준으로 중복된 메시지를 삭제하고 가장 최신 메시지만 보존
    - 키를 기준으로 메시지를 업데이트하는 경우 사용 ( 유저 프로필 같은 동기화 필요한 경우 사용)

#### 로그 세그먼트

- 로그 세그먼트
    - 토픽의 파티션은 여러개의 로그 세그먼트 파일로 구성
    - 로그 세그먼트는 로그 파일이 일정 크기에 도달하거나 시간이 지나면 닫히고 새로운 로그 세그먼트가 생성

### 2.3. 동기화

#### 리플리케이션 팩터

- Replication Factor
    - kafka의 partition은 여러개의 Broker에 복제본을 가지고 있음
    - Replication Factor는 각 파티션을 몇개의 복제본으로 유지할지 결정
    - 장점
        - 데이터 내구성 보장, Broker 장애시 데이터 손실 방지

#### ISR(In-Sync Replica)

- ISR은 리더와 동기화 상태를 유지하고 있는 복제본의 목록
    - Leader: 파티션에 대해 읽기 쓰기를 관리하는 주체
    - follower: 리더를 복제하여 데이터를 동일하게 유지
- follower는 Leader가 받은 데이터를 Pull 방식으로 동기화
    - follower는 일정 시간 내에 리더의 Offset에 도달하지 못하면 ISR에서 제외(Outo-of-sync)

#### 브로커 장애 시 데이터 복구

- Leader Election
    - 기존 Leader Broker가 다운되면 ISR 중 (follower) 중 하나가 Leader로 선출
    - 장애 복구 시 다운 된 이전의 Leader가 복구되면 follower로 전환

## 구성 요소 (Components)

### 3.1. Broker

#### 브로커 역할, 클러스터 구성

- Broker
    - 메시지 저장소..?
        - Producer가 전송한 메시지를 디스크(로그 세그먼트)에 저장
        - Partition과 Offset을 관리
    - Client 통신
        - Producer: 메시지를 브로커에 전송 할 때 브로커는 Leader에 메시지를 기록
        - Consumer: Leader로 부터 데이터를 읽어 갈 때 Offset을 기반으로 특정 시점의 메시지를 전달
- 클러스터 메타 데이터 관리
    - Broker ID, Host, Port, Partition 정보 등
    - Zookeeper, KRaft를 통해 클러스터 메타데이터 관리
    - KRaft 모드에서는 Controller 역할을 하는 노드가 존재
        - Controller는 클러스터 내의 브로커들을 관리하고, 파티션 리더 선출, 파티션 리밸런싱 등을 수행

#### KRaft vs Zookeeper 비교

- Zookeeper
    - kafka 이전 버전에서 주로 사용
    - Kafka와 별도로 Zookeeper를 설치 및 관리 해야함
    - 기존의 표준 아키텍처지만, 별도 클러스터 관리가 필요.
- KRaft
    - Kafka만 설치하여 사용 가능하여 운영 복잡도 감소
    - KRaft 방식은 Kafka 자체에서 메타데이터를 관리 향후 표준 예정..?

### 3.2. Producer

- Producer
    - Kafka 토픽에 메시지를 생성 하는 주체

#### 메시지 발행 방식

- Patition Key를 통해 특정 Patition에 메시지를 전송 ( 순서 보장 시 )
- Round-robin 방식으로 메시지를 분산 ( 순서 보장 필요 없는 경우 )
- 일반적으로 Serializer를 통해 메시지를 직렬화 하여 전송

#### 배치 처리

- Kafka Producer는 메시지를 배치로 모아서 한번에 전송 가능
- 네트워크 요청 횟수 감소로 처리량 향상
- batch.size, linger.ms, compression.type 등의 설정을 이용하여 성능 튜닝 가능
- latency와 throughput 사이의 trade-off 고려 필요

#### 동기 vs 비동기 전송

- 동기: Producer가 메시지를 전송하고 ACK를 기다리는 경우
- 비동기: Producer가 메시지를 전송하고 response를 응답하고 내부 스레드가 백그라운드에서 이후 처리

#### ACK 모드 (Acknowledgment Mode)

- Ack 모드
    - acks=all
        - Producer가 메시지를 전송할 때 Ack 모드를 설정하여 데이터 손실을 방지
            - 모든 복제본이 메시지를 받았을 때 Producer에 성공 응답을 보냄
            - 안전한 메시지 전달이 가능하나 Latency 비용 발생
    - acks=1
        - Leader만 메시지를 받았을 때 성공 응답을 보냄
        - Leader가 다운되면 데이터 손실 발생 가능
    - acks=0
        - Producer가 메시지를 전송하고 Ack를 기다리지 않음
        - 데이터 손실 가능성이 높음

### 3.3. Consumer

#### Consumer Group, 파티션 할당

##### Consumer Group

- Consumer Group은 하나 이상의 Consumer로 구성
- Kafka 토픽에서 메시지를 읽을 때 그룹단위로 파티션을 나눠서 할당

##### 파티션 할당

- 자동 할당
    - 새로운 Consumer가 그룹에 합류하거나, 기존 Consumer가 종료되면 Rebalance 발생
    - Group Coordinator가 파티션 할당을 재조정
    - Consumer는 파티션을 할당 받아 메시지 처리
- 할당 전략
    - RangeAssignor: 파티션을 연속적 범위로 할당.
    - RoundRobinAssignor: 파티션을 라운드 로빈 방식으로 분배.
    - StickyAssignor: 기존 할당을 최대한 유지하면서 새 Consumer나 제거된 Consumer만 재할당

#### 오프셋 커밋(Commit) 전략 (자동/수동)

##### 오프셋 커밋

- Consumer가 파티션을 읽을 때 어느 지점까지(offset) 메시지를 처리 했는지 Broker에 저장하는 과정을 commit이라 함

##### 자동 커밋

- Consumer 라이브러리가 주기적으로 커밋을 수행

##### 수동 커밋

- 어플리케이션 코드에서 명시적으로 commitSync(), commitAsync()를 호출하여 커밋
- 트랜잭션 처리 로직과 연계하여 `처리가 끝나면 그 때 오프셋 커밋` 가능

##### 커밋 방식

- commitSync()
    - Broker로 부터 응답을 기다린 뒤 커밋 완료 여부를 확인
    - 커밋 실패시 예외 처리 가능하여 안전한 처리 가능
- commitAsync()
    - 비동기 방식으로 커밋
    - 성공 확인 여부가 어려움

### 3.4. Controller(KRaft) / Zookeeper

- KRaft: Kafka 내부에서 Raft Consensus 알고리즘을 사용하여 클러스터 메타데이터(토픽, 파티션, ACL, 리더) 관리
    - Zookeeper 없이 Kfaka만으로 클러스터 관리 가능
- Zookeeper: Kafka 이전 버전에서 사용되던 클러스터 메타데이터 관리 시스템

#### KRaft 모드에서의 컨트롤러 노드 역할

- KRaft 모드에서 브로커중 일부가 Controller Quorum을 구성하여 그중 하나가 Leader Controller 역할을 수행
- Leader Election: Controller Quorum 중 Leader가 다운되면 다른 노드가 Leader로 선출
- 메타데이터 저장 및 전파: 메타데이터 구성 정보를 Raft Log에 기록하고 Follower Controller에 Sync
- 클러스터 상태 관리: 브로커 추가/삭제, Rebalance, Consumer Group Coordinator 배정

## 동작 흐름 (Data Flow & Processes)

### 4.1. 생산(Producer) → 브로커 → 소비(Consumer)

#### Producer가 메시지를 분산시키는 방식 (Partition Key)

- Producer가 메시지를 분산시키는 방식 ( Partition Key )을 통해 메시지를 전송
    - 파티션 키 지정
        - Producer는 메시지를 보낼 때 파티션 키를 설정
        - Kafka Producer 라이브러리는 파티션 키를 해시(Hash)하여, 특정 파티션에 메시지를 매핑
        - 같은 키를 가진 메시지는 항상 동일 파티션에 저장 → 순서 보장
    - 라운드 로빈 방식
        - 파티션 키가 없거나 null인 경우, 보통 라운드 로빈 등으로 메시지를 균등하게 분산
        - 부하 분산(Load Balancing)에 유리하지만, 글로벌 순서는 보장되지 않음
    - 배치 처리 전송
        - Producer는 배치(batch) 설정(batch.size, linger.ms)을 통해 여러 메시지를 모아서 전송
        - 메시지 전송 후, ACK 모드(acks=all 등)에 따라 브로커 응답을 기다리거나 비동기로 처리

#### Broker 내부 처리(파티션, 레플리카)

- 파티션 할당
    - Producer가 전송한 메시지는 해당 파티션에 브로커에 기록
    - 파티션은 디스크상의 로그 파일 형태로 메시지가 오프셋 순서대로 쌓임
- 레플리카 복제
    - 설정된 Replication Factor에 따라 복제본을 생성
    - ISR
- Broker간 통신
    - ISR 모든 팔로워가 동기화 되면 Producer에게 ACK 전송
    - 리더는 메시지를 받아 로그에 기록 후 팔로워 브로커에 복제본을 전송

#### Consumer Group에서 파티션을 할당받고 메시지를 읽는 과정

- Consumer Group
    - 여러 Consumer 인스턴스가 하나의 그룹(group.id)으로 묶여서 특정 토픽을 병렬 처리
    - 장점: 파티션 단위로 분산 처리 → 처리량 스케일 아웃
- 파티션 할당(Rebalance)
    - Consumer 그룹 내 새로운 Consumer가 합류하거나, 기존 Consumer가 종료되면, Group Coordinator가 Rebalance를 트리거
    - 각 파티션은 오직 하나의 Consumer에게만 할당 → 중복 처리를 방지
- 오프셋(Offset) 관리
    - Consumer는 메시지를 읽은 위치(Offset)를 Broker에 주기적으로 커밋(Commit)하거나 애플리케이션 로직에 따라 수동 커밋
    - 장애 복구 시 커밋된 Offset부터 재시작 가능
