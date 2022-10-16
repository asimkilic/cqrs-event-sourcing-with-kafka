package com.asimkilic.cqrs.core.infrastructure;

import com.asimkilic.cqrs.core.commands.BaseCommand;
import com.asimkilic.cqrs.core.commands.CommandHandlerMethod;

public interface CommandDispatcher {
    <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler);

    void send(BaseCommand command);
}
