package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
import com.interswitch.fraudtransactionapp.dto.response.BlacklistResponse;
import com.interswitch.fraudtransactionapp.model.BlacklistedCard;
import com.interswitch.fraudtransactionapp.model.BlacklistedIp;
import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;
import com.interswitch.fraudtransactionapp.repository.BlackListedIpRepository;
import com.interswitch.fraudtransactionapp.repository.BlacklistedCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlacklistServiceImplTest {

    @InjectMocks
    private BlacklistServiceImpl blacklistService;

    @Mock
    private BlacklistedCardRepository cardRepository;

    @Mock
    private BlackListedIpRepository ipRepository;

    @Mock
    private BlackListedMerchantDao blackListedMerchantDao;

    private BlacklistedCard card;
    private BlacklistedIp ip;
    private BlacklistedMerchant merchant;

    @BeforeEach
    void setUp() {
        card = new BlacklistedCard();
        card.setCardNo("1234567890123456");

        ip = new BlacklistedIp();
        ip.setIp("192.168.1.1");

        merchant = new BlacklistedMerchant();
        merchant.setMerchantId("M123");
    }

    @Test
    void shouldGetAllCards() {
        when(cardRepository.findAll()).thenReturn(List.of(card));

        List<BlacklistedCard> cards = blacklistService.getAllCards();

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getCardNo()).isEqualTo("1234567890123456");
    }

    @Test
    void shouldGetCardByCardNo() {
        when(cardRepository.findByCardNo("1234567890123456")).thenReturn(Optional.of(card));

        BlacklistedCard result = blacklistService.getCard("1234567890123456");

        assertThat(result).isNotNull();
        assertThat(result.getCardNo()).isEqualTo("1234567890123456");
    }

    @Test
    void shouldThrowWhenCardNotFound() {
        when(cardRepository.findByCardNo("9999999999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blacklistService.getCard("9999999999999999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Card not found");
    }

    @Test
    void shouldGetAllIps() {
        when(ipRepository.findAll()).thenReturn(List.of(ip));

        List<BlacklistedIp> ips = blacklistService.getAllIps();

        assertThat(ips).hasSize(1);
        assertThat(ips.get(0).getIp()).isEqualTo("192.168.1.1");
    }

    @Test
    void shouldGetIpById() {
        when(ipRepository.findById("192.168.1.1")).thenReturn(Optional.of(ip));

        BlacklistedIp result = blacklistService.getIp("192.168.1.1");

        assertThat(result).isNotNull();
        assertThat(result.getIp()).isEqualTo("192.168.1.1");
    }

    @Test
    void shouldThrowWhenIpNotFound() {
        when(ipRepository.findById("10.0.0.1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blacklistService.getIp("10.0.0.1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("IP not found");
    }

    @Test
    void shouldGetAllMerchants() {
        when(blackListedMerchantDao.findAll()).thenReturn(List.of(merchant));

        List<BlacklistedMerchant> merchants = blacklistService.getAllMerchants();

        assertThat(merchants).hasSize(1);
        assertThat(merchants.get(0).getMerchantId()).isEqualTo("M123");
    }

    @Test
    void shouldGetMerchantById() {
        when(blackListedMerchantDao.findById("M123")).thenReturn(Optional.of(merchant));

        BlacklistedMerchant result = blacklistService.getMerchant("M123");

        assertThat(result).isNotNull();
        assertThat(result.getMerchantId()).isEqualTo("M123");
    }

    @Test
    void shouldThrowWhenMerchantNotFound() {
        when(blackListedMerchantDao.findById("M999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blacklistService.getMerchant("M999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Merchant not found");
    }

    @Test
    void shouldReturnAllBlacklist() {
        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(ipRepository.findAll()).thenReturn(List.of(ip));
        when(blackListedMerchantDao.findAll()).thenReturn(List.of(merchant));

        BlacklistResponse response = blacklistService.getAllBlacklist();

        assertThat(response.getCards()).hasSize(1);
        assertThat(response.getIps()).hasSize(1);
        assertThat(response.getMerchants()).hasSize(1);
    }
}