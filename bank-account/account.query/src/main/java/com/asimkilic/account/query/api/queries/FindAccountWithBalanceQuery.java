package com.asimkilic.account.query.api.queries;

import com.asimkilic.account.query.api.dto.EqualityType;
import com.asimkilic.cqrs.core.queries.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindAccountWithBalanceQuery extends BaseQuery {
    private EqualityType equalityType;
    private double balance;
}
