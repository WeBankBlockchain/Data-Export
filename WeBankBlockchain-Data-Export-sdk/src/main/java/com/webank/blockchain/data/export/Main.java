package com.webank.blockchain.data.export;

import org.fisco.bcos.sdk.crypto.CryptoSuite;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/09
 */
public class Main {

    public static void main(String[] args) throws Exception{
        CryptoSuite cryptoSuite = new CryptoSuite(0);

        System.out.println(cryptoSuite.hash("set(uint256)"));
    }

}
