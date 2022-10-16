package com.asimkilic.account.cmd.api.commands;

import com.asimkilic.cqrs.core.commands.BaseCommand;
import lombok.Data;

@Data
public class WithdrawFundsCommand extends BaseCommand {
    private double amount;
}
