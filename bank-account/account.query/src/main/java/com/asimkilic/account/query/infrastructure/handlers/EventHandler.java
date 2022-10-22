package com.asimkilic.account.query.infrastructure.handlers;

import com.asimkilic.account.common.events.AccountClosedEvent;
import com.asimkilic.account.common.events.AccountOpenedEvent;
import com.asimkilic.account.common.events.FundsDepositedEvent;
import com.asimkilic.account.common.events.FundsWithdrawnEvent;

public interface EventHandler {
    void on(AccountOpenedEvent event);

    void on(FundsDepositedEvent event);

    void on(FundsWithdrawnEvent event);

    void on(AccountClosedEvent event);
}
