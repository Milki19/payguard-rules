package com.payguard.core.rule;

import com.payguard.core.model.Transaction;

public interface PaymentRule {

    RuleResult evaluate(Transaction transaction);

}
