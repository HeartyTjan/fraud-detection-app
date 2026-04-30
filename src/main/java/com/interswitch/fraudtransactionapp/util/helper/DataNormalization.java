package com.interswitch.fraudtransactionapp.util.helper;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataNormalization {

    private final double[] means;
    private final double[] stdDevs;


    public double[] normalizeTransactionData(double[] transactionData) {
        return new double[]{
                (transactionData[0] - means[0]) / stdDevs[0],
                (transactionData[1] - means[1]) / stdDevs[1],
                (transactionData[2] - means[2]) / stdDevs[2]
        };
    }
}