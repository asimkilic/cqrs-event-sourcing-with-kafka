package com.asimkilic.account.cmd.api.commands;

import com.asimkilic.cqrs.core.commands.BaseCommand;
import lombok.Data;

@Data
public class DepositFundsCommand extends BaseCommand {
    private double amount;
}
