package com.interswitch.fraudtransactionapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fraud")
@Getter
@Setter
public class FraudConfig {

    private Threshold threshold = new Threshold();
    private Velocity velocity = new Velocity();
    private Risk risk = new Risk();

    @Getter
    @Setter
    public static class Threshold {
        private int block = 70;
        private int review = 30;
    }

    @Getter
    @Setter
    public static class Velocity {
        private CardVelocity card = new CardVelocity();
        private IpVelocity ip = new IpVelocity();
    }

    @Getter
    @Setter
    public static class CardVelocity {
        private int oneMin = 5;
        private int oneHour = 20;
        private int twentyFourHour = 100;
    }

    @Getter
    @Setter
    public static class IpVelocity {
        private int oneMin = 10;
        private int oneHour = 50;
    }

    @Getter
    @Setter
    public static class Risk {
        private IpRisk ip = new IpRisk();
        private MerchantRisk merchant = new MerchantRisk();
    }

    @Getter
    @Setter
    public static class IpRisk {
        private int threshold = 50;
    }

    @Getter//
    @Setter
    public static class MerchantRisk {
        private int threshold = 50;
    }
}
