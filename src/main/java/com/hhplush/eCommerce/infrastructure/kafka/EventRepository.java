package com.hhplush.eCommerce.infrastructure.kafka;

import com.hhplush.eCommerce.domain.event.IEventRepository;
import com.hhplush.eCommerce.domain.event.OutboxEvent;
import com.hhplush.eCommerce.domain.event.ProcessState;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository implements IEventRepository {

    private final IOutboxEventJPARepository outboxEventJPARepository;

    @Override
    public List<OutboxEvent> findByProcessState(ProcessState processState) {
        return outboxEventJPARepository.findByProcessState(processState);
    }

    @Override
    public void save(OutboxEvent outboxEvent) {
        outboxEventJPARepository.save(outboxEvent);
    }
}
