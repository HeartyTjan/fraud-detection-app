//package com.interswitch.fraudtransactionapp.fraudEngine.rule;
//
//import com.interswitch.fraudtransactionapp.dao.FraudDao;
//import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
//import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
//import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class GeoVelocityRuleTest {
//
//    @Mock
//    private FraudDao fraudDao;
//
//    @InjectMocks
//    private GeoVelocityRule geoVelocityRule;
//
//    private TransactionRequest buildRequest(
//            double lat,
//            double lon,
//            Instant time,
//            String cardNo
//    ) {
//        TransactionRequest request = new TransactionRequest(
//                cardNo,
//                new BigDecimal("1000"),
//                "M123",
//                time,
//                "WEB",
//                "192.168.1.1",
//                "device",
//                "agent"
//        );
//
//        request.setLatitude(lat);
//        request.setLongitude(lon);
//
//        return request;
//    }
//
//    private RuleContext buildContext(TransactionRequest request) {
//        RuleContext context = new RuleContext();
//        context.setRequest(request);
//        return context;
//    }
//
//    @Test
//    void shouldReturnZeroScore_whenLocationIsMissing() {
//        TransactionRequest request = buildRequest(0, 0, Instant.now(), "123");
//        request.setLatitude(null);
//
//        RuleContext context = buildContext(request);
//
//        FraudRuleResult result = geoVelocityRule.evaluate(context);
//
//        assertEquals(0, result.getScore());
//    }
//
//    @Test
//    void shouldReturnZeroScore_whenNoPreviousTransaction() {
//        TransactionRequest request = buildRequest(6.5, 3.3, Instant.now(), "123");
//
//        when(fraudDao.getLastTransactionWithLocation("123"))
//                .thenReturn(null);
//
//        RuleContext context = buildContext(request);
//
//        FraudRuleResult result = geoVelocityRule.evaluate(context);
//
//        assertEquals(0, result.getScore());
//    }
//
//    @Test
//    void shouldFlagImpossibleTravel() {
//        Instant now = Instant.now();
//
//        TransactionRequest request = buildRequest(6.5, 3.3, now, "123");
//
//        TransactionRequest lastTx = new TransactionRequest(
//                "123",
//                new BigDecimal("1000"),
//                "M123",
//                now.minusSeconds(3600),
//                "WEB",
//                "ip",
//                "device",
//                "agent"
//        );
//
//        lastTx.setLatitude(51.5);
//        lastTx.setLongitude(-0.1);
//
//        when(fraudDao.getLastTransactionWithLocation("123"))
//                .thenReturn(lastTx);
//
//        RuleContext context = buildContext(request);
//
//        FraudRuleResult result = geoVelocityRule.evaluate(context);
//
//        assertEquals(80, result.getScore());
//        assertTrue(result.getReason().contains("IMPOSSIBLE_TRAVEL"));
//    }
//
//    @Test
//    void shouldFlagSuspiciousTravel() {
//        Instant now = Instant.now();
//
//        TransactionRequest request = buildRequest(6.5, 3.3, now, "123");
//
//        TransactionRequest lastTx = new TransactionRequest(
//                "123",
//                new BigDecimal("1000"),
//                "M123",
//                now.minusSeconds(7200),
//                "WEB",
//                "ip",
//                "device",
//                "agent"
//        );
//
//        lastTx.setLatitude(40.7);
//        lastTx.setLongitude(-74.0);
//
//        when(fraudDao.getLastTransactionWithLocation("123"))
//                .thenReturn(lastTx);
//
//        RuleContext context = buildContext(request);
//
//        FraudRuleResult result = geoVelocityRule.evaluate(context);
//
//        assertEquals(40, result.getScore());
//        assertEquals("SUSPICIOUS_TRAVEL_SPEED", result.getReason());
//    }
//
//    @Test
//    void shouldReturnZeroScore_forNormalTravel() {
//        Instant now = Instant.now();
//
//        TransactionRequest request = buildRequest(6.5, 3.3, now, "123");
//
//        TransactionRequest lastTx = new TransactionRequest(
//                "123",
//                new BigDecimal("1000"),
//                "M123",
//                now.minusSeconds(86400),
//                "WEB",
//                "ip",
//                "device",
//                "agent"
//        );
//
//        lastTx.setLatitude(6.6);
//        lastTx.setLongitude(3.4);
//
//        when(fraudDao.getLastTransactionWithLocation("123"))
//                .thenReturn(lastTx);
//
//        RuleContext context = buildContext(request);
//
//        FraudRuleResult result = geoVelocityRule.evaluate(context);
//
//        assertEquals(0, result.getScore());
//    }
//}