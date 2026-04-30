package com.interswitch.fraudtransactionapp.client;

import com.interswitch.fraudtransactionapp.client.dto.ProviderResponse;

import java.io.IOException;

public interface IPBlacklistedProvider {

    void getBlacklistedIpAndSaveToDB() throws IOException, InterruptedException;
}
