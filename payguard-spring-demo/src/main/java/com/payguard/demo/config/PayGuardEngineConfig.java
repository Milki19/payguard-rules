package com.payguard.demo.config;

import com.payguard.core.engine.PaymentRuleEngine;
import com.payguard.core.model.Channel;
import com.payguard.core.rule.impl.AmountLimitRule;
import com.payguard.core.rule.impl.ChannelAllowedRule;
import com.payguard.core.rule.impl.CountryBlockedRule;
import com.payguard.core.rule.impl.CurrencyAllowedRule;
import com.payguard.core.rule.impl.RiskLevelRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Configuration
public class PayGuardEngineConfig {

    @Bean
    public PaymentRuleEngine paymentRuleEngine() {
        return new PaymentRuleEngine(List.of(
                new CurrencyAllowedRule(Set.of("EUR", "USD", "RSD")),
                new CountryBlockedRule(Set.of("RU", "KP", "IR")),
                new ChannelAllowedRule(Set.of(Channel.ONLINE, Channel.POS)),
                new AmountLimitRule(new BigDecimal("10000.00")),
                new RiskLevelRule(new BigDecimal("7500.00"))
        ));
    }
}