package com.hhplush.eCommerce.domain.event;

import java.util.List;

public interface IEventRepository {

    List<OutboxEvent> findByProcessState(ProcessState processState);

    void save(OutboxEvent outboxEvent);

}
