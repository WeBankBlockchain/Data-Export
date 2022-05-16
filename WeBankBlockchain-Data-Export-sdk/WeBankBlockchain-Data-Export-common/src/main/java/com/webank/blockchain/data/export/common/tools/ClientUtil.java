package com.webank.blockchain.data.export.common.tools;

import com.google.common.collect.Maps;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/17
 */
public class ClientUtil {


    public static Client getClient(ChainInfo chainInfo) throws ConfigException {
        BcosSDK sdk = getSDK(chainInfo);
        return sdk.getClient(String.valueOf(chainInfo.getGroupId()));
    }

    public static BcosSDK getSDK(ChainInfo chainInfo) throws ConfigException {
        ConfigProperty configProperty = new ConfigProperty();
        setPeers(configProperty,chainInfo);
        setCertPath(configProperty,chainInfo);
        ConfigOption option = new ConfigOption(configProperty);
        return new BcosSDK(option);
    }

    public static void setPeers(ConfigProperty configProperty, ChainInfo chainInfo) {
        String[] nodes = StringUtils.split(chainInfo.getNodeStr(), ";");
        List<String> peers = Arrays.asList(nodes);
        Map<String, Object> network = Maps.newHashMapWithExpectedSize(1);
        network.put("peers", peers);
        configProperty.setNetwork(network);
    }

    public static void setCertPath(ConfigProperty configProperty, ChainInfo chainInfo) {
        Map<String, Object> cryptoMaterial = Maps.newHashMapWithExpectedSize(1);
        cryptoMaterial.put("certPath", chainInfo.getCertPath());
        cryptoMaterial.put("useSMCrypto", chainInfo.getCryptoTypeConfig() == 1? "true" : "false");
        configProperty.setCryptoMaterial(cryptoMaterial);
    }
}
