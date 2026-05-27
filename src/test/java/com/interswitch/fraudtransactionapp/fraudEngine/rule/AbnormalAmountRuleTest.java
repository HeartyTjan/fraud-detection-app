package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AbnormalAmountRuleTest {

    private FraudDao fraudDao;

    private AbnormalAmountRule abnormalAmountRule;

    @BeforeEach
    void setUp() {
        fraudDao = Mockito.mock(FraudDao.class);
        abnormalAmountRule = new AbnormalAmountRule(fraudDao);
    }

    private RuleContext buildContext(String cardNo, BigDecimal amount) {

        TransactionRequest request = new TransactionRequest(
                cardNo,
                amount,
                "M123",
                Instant.now(),
                "WEB",
                "192.168.1.1",
                "device-xyz",
                "Mozilla/5.0"
        );


        return new RuleContext(request);
    }

    @Test
    void evaluate_shouldReturnBlockResult_whenDaoReturnsBlock() {
        String cardNo = "4111111111111111";
        BigDecimal amount = new BigDecimal("5000000.00");

        when(fraudDao.checkAbnormalAmount(eq(cardNo), eq(amount))).thenReturn("BLOCK");

        RuleContext context = buildContext(cardNo, amount);

        FraudRuleResult result = abnormalAmountRule.evaluate(context);

        assertNotNull(result);
        assertEquals(60, result.getScore());
        assertEquals("ABNORMAL_AMOUNT_BLOCK", result.getReason());
    }

    @Test
    void evaluate_shouldReturnReviewResult_whenDaoReturnsReview() {
        String cardNo = "4111111111111111";
        BigDecimal amount = new BigDecimal("1000.00");

        when(fraudDao.checkAbnormalAmount(eq(cardNo), eq(amount))).thenReturn("REVIEW");

        RuleContext context = buildContext(cardNo, amount);

        FraudRuleResult result = abnormalAmountRule.evaluate(context);

        assertNotNull(result);
        assertEquals(40, result.getScore());
        assertEquals("ABNORMAL_AMOUNT_REVIEW", result.getReason());
    }

    @Test
    void evaluate_shouldReturnZeroScore_whenDaoReturnsOtherOrNull() {
        String cardNo = "4111111111111111";
        BigDecimal amount = new BigDecimal("100.00");

        when(fraudDao.checkAbnormalAmount(eq(cardNo), eq(amount))).thenReturn(String.valueOf(0));

        RuleContext context = buildContext(cardNo, amount);

        FraudRuleResult result = abnormalAmountRule.evaluate(context);

        assertNotNull(result);
        assertEquals(0, result.getScore());
        assertNull(result.getReason());
    }
}