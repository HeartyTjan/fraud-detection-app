package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Order(6)
public class GeoVelocityRule implements FraudRule {

    private static final double MAX_TRAVEL_SPEED_KMH = 900.0;
    private static final int IMPOSSIBLE_TRAVEL_SCORE = 80;
    private static final int SUSPICIOUS_TRAVEL_SCORE = 40;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        var profile = context.fraudProfile();
        var request = context.request();

        if (profile == null || request == null) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        var lastTx = profile.getLastTransaction();

        if (request.getLatitude() == null ||
                request.getLongitude() == null ||
                request.getTransactionTime() == null) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        if (lastTx == null ||
                lastTx.getLatitude() == null ||
                lastTx.getLongitude() == null ||
                lastTx.getTransactionTime() == null) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        long timeDiffSeconds =
                request.getTransactionTime().getEpochSecond()
                        - lastTx.getTransactionTime().getEpochSecond();

        if (timeDiffSeconds <= 0) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        double timeDiffHours = timeDiffSeconds / 3600.0;

        if (timeDiffHours <= 0) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        double distanceKm = haversineDistance(
                lastTx.getLatitude(),
                lastTx.getLongitude(),
                request.getLatitude(),
                request.getLongitude()
        );

        double requiredSpeedKmh = distanceKm / timeDiffHours;

        if (requiredSpeedKmh > MAX_TRAVEL_SPEED_KMH) {
            return FraudRuleMapper.mapToResult(
                    IMPOSSIBLE_TRAVEL_SCORE,
                    "IMPOSSIBLE_TRAVEL:" + distanceKm + "km_" + timeDiffHours + "h",
                    false
            );
        }

        if (requiredSpeedKmh > 500) {
            return FraudRuleMapper.mapToResult(
                    SUSPICIOUS_TRAVEL_SCORE,
                    "SUSPICIOUS_TRAVEL_SPEED",
                    false
            );
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {

        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}