package com.asimkilic.cqrs.core.producers;

import com.asimkilic.cqrs.core.events.BaseEvent;

public interface EventProducer {
    void produce(String topic, BaseEvent event);
}
