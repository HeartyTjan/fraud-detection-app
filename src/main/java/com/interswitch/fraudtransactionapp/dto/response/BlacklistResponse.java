package com.interswitch.fraudtransactionapp.dto.response;

import com.interswitch.fraudtransactionapp.model.BlacklistedCard;
import com.interswitch.fraudtransactionapp.model.BlacklistedIp;
import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlacklistResponse {

    private List<BlacklistedCard> cards;
    private List<BlacklistedIp> ips;
    private List<BlacklistedMerchant> merchants;
}
