package com.hhplush.eCommerce.domain.event;

import com.hhplush.eCommerce.domain.IEventPublisher;
import com.hhplush.eCommerce.infrastructure.kafka.EventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final IEventPublisher eventPublisher;

    public void saveEvent(OutboxEvent outboxEvent) {
        // 이벤트 저장소에 저장
        eventRepository.save(outboxEvent);
        log.info("Event saved: {}", outboxEvent.outboxEventId);
    }

    public List<OutboxEvent> findByProcessState(ProcessState processState) {
        return eventRepository.findByProcessState(processState);
    }

    public void publishEvent(String topic, String message) {
        // 이벤트 발행
        eventPublisher.publishString(topic, message);
        log.info("Event published: {}", message);
    }
}
