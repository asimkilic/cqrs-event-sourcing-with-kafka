package com.asimkilic.account.cmd.infrastructure;

import com.asimkilic.account.cmd.domain.AccountAggregate;
import com.asimkilic.cqrs.core.domain.AggregateRoot;
import com.asimkilic.cqrs.core.handlers.EventSourcingHandler;
import com.asimkilic.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {
    @Autowired
    private EventStore eventStore;

    @Override
    public void save(AggregateRoot aggregateRoot) {
        eventStore.saveEvents(aggregateRoot.getId(), aggregateRoot.getUncommittedChanges(), aggregateRoot.getVersion());
        aggregateRoot.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getById(String id) {
        var aggregate = new AccountAggregate();
        var events = eventStore.getEvents(id);
        if (events != null && !events.isEmpty()) {
            aggregate.replayEvents(events);
            var latestVersion = events.stream().map(x -> x.getVersion()).max(Comparator.naturalOrder());
            aggregate.setVersion(latestVersion.get());
        }
        return aggregate;
    }
}
