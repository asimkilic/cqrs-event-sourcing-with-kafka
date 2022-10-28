package com.asimkilic.cqrs.core.handlers;

import com.asimkilic.cqrs.core.domain.AggregateRoot;

public interface EventSourcingHandler<T> {
    void save(AggregateRoot aggregateRoot);

    T getById(String id);

    void republishEvents();
}
