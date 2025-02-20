package com.hhplush.eCommerce.infrastructure.kafka;

import com.hhplush.eCommerce.domain.event.OutboxEvent;
import com.hhplush.eCommerce.domain.event.ProcessState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOutboxEventJPARepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByProcessState(ProcessState processState); // 처리되지 않은 메시지만 조회


}
