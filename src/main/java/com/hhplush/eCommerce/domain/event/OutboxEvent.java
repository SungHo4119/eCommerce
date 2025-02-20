package com.hhplush.eCommerce.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_event_id")
    Long outboxEventId;  // 이벤트 ID

    @Column(name = "aggregate_id")
    String aggregateId; // 이벤트 발생 주체 ID

    @Column(name = "event_type")
    String eventType; // 이벤트 타입 ( 토픽 )

    @Column(name = "event_payload")
    String eventPayload;

    @Column(name = "process_state")
    ProcessState processState;

    @CreationTimestamp
    private LocalDateTime createdAt;
}


