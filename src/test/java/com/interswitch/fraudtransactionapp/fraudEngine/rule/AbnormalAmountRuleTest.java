package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.*;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AbnormalAmountRuleTest {

    private AbnormalAmountRule rule;

    @BeforeEach
    void setUp() {
        rule = new AbnormalAmountRule();
    }

    private TransactionRequest buildRequest(BigDecimal amount) {
        return new TransactionRequest(
                "4111111111111111",
                amount,
                "M123",
                Instant.now(),
                "WEB",
                "192.168.1.1",
                "device-xyz",
                "Mozilla/5.0"
        );
    }

    private RuleContext buildContext(BigDecimal amount, BigDecimal avg) {

        TransactionRequest request = buildRequest(amount);

        FraudProfile profile = new FraudProfile(
                null,
                null,
                false,
                avg,
                0,
                0,
                0
        );

        return new RuleContext(request, profile);
    }

    @Test
    void shouldReturnHighScore_whenAmountVeryHigh() {

        FraudRuleResult result = rule.evaluate(
                buildContext(
                        new BigDecimal("200000"),
                        new BigDecimal("10000")
                )
        );

        System.out.println("result: " + result.getScore());
        assertEquals(60, result.getScore());
        assertEquals("ABNORMAL_AMOUNT_HIGH", result.getReason());
    }

    @Test
    void shouldReturnHighScore_whenRatioAboveFive() {

        FraudRuleResult result = rule.evaluate(
                buildContext(
                        new BigDecimal("30000"),
                        new BigDecimal("5000")
                )
        );

        assertEquals(60, result.getScore());
        assertEquals("ABNORMAL_AMOUNT_HIGH", result.getReason());
    }

    @Test
    void shouldReturnReviewScore_whenMediumCase() {

        FraudRuleResult result = rule.evaluate(
                buildContext(
                        new BigDecimal("60000"),
                        new BigDecimal("20000")
                )
        );

        assertEquals(40, result.getScore());
        assertEquals("ABNORMAL_AMOUNT_REVIEW", result.getReason());
    }

    @Test
    void shouldReturnZeroScore_whenNormalTransaction() {

        FraudRuleResult result = rule.evaluate(
                buildContext(
                        new BigDecimal("1000"),
                        new BigDecimal("500")
                )
        );

        assertEquals(0, result.getScore());
        assertNull(result.getReason());
    }

    @Test
    void shouldReturnZeroScore_whenAvgIsNull() {

        TransactionRequest request = buildRequest(new BigDecimal("100000"));

        FraudProfile profile = new FraudProfile(
                null,
                null,
                false,
                null,
                0,
                0,
                0
        );

        FraudRuleResult result = rule.evaluate(
                new RuleContext(request, profile)
        );

        assertEquals(0, result.getScore());
        assertNull(result.getReason());
    }
}