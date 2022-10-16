package com.asimkilic.account.cmd.api.commands;

import com.asimkilic.cqrs.core.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
    public CloseAccountCommand(String id) {
        super(id);
    }
}
