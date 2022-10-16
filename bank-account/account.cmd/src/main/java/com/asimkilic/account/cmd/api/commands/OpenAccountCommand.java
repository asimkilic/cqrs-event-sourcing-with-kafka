package com.asimkilic.account.cmd.api.commands;

import com.asimkilic.account.common.dto.AccountType;
import com.asimkilic.cqrs.core.commands.BaseCommand;
import lombok.Data;

@Data
public class OpenAccountCommand extends BaseCommand {
    private String accountHolder;
    private AccountType accountType;
    private double openingBalance;
}
