//package com.interswitch.fraudtransactionapp.client;
//
//import com.interswitch.fraudtransactionapp.client.dto.ProviderBlacklistedIPDTO;
//import com.interswitch.fraudtransactionapp.client.dto.ProviderResponse;
//import com.interswitch.fraudtransactionapp.client.model.ProviderBlacklistedIP;
//import com.interswitch.fraudtransactionapp.config.TrackExecution;
//import com.interswitch.fraudtransactionapp.repository.ProviderBlacklistedIPRepository;
//import com.interswitch.fraudtransactionapp.util.mapper.ProviderBlacklistedIPMapper;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//import reactor.util.retry.Retry;
//
//import java.time.Duration;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@TrackExecution
//public class SikkariProvider implements IPBlacklistedProvider {
//
//    private final Logger log = LoggerFactory.getLogger(SikkariProvider.class);
//
//    private final ProviderBlacklistedIPRepository providerBlacklistedIPRepository;
//    private final WebClient webClient;
//
//    @Value("${sikkari.api.key}")
//    private String apiKey;
//
//    @Value("${sikkari.base.url}")
//    private String baseUrl;
//
//    @Override
//    public void getBlacklistedIpAndSaveToDB() {
//        int scoreMinimum = 70;
//        int limit = 2000;
//        String url = String.format("%s/v1/key/blacklist?scoreMinimum=%d&limit=%d", baseUrl, scoreMinimum, limit);
//
//        log.info("Sending request to Sikkari API: {}{}", baseUrl, url);
//
//        webClient.get()
//                .uri(url)
//                .headers(headers -> headers.setBearerAuth(apiKey))
//                .retrieve()
//                .bodyToMono(ProviderResponse.class)
//                .flatMapMany(res -> Flux.fromIterable(res.getData()))
//
//                .buffer(100)
//
//                .flatMap(batch ->
//                        Mono.fromRunnable(() -> saveBlacklistedIPs(batch))
//                                .subscribeOn(Schedulers.boundedElastic())
//                )
//
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
//                        .filter(t -> t instanceof WebClientResponseException.TooManyRequests))
//
//                .doOnError(e -> log.error("Error fetching blacklisted IPs", e))
//                .doOnComplete(() -> log.info("Blacklist fetch completed"))
//                .subscribe();
//    }
//
//    @Transactional
//    public void saveBlacklistedIPs(List<ProviderBlacklistedIPDTO> dtos) {
//
//        List<ProviderBlacklistedIP> entities = dtos.stream()
//                .map(ProviderBlacklistedIPMapper::toEntity)
//                .toList();
//
//        providerBlacklistedIPRepository.saveAll(entities);
//    }
//}