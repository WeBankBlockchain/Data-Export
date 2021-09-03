package com.webank.blockchain.data.export.plugin.enums;

public enum ContractEnum {

    PROPOSAL_NAME("ProposalController"),
    METADATA_NAME("MetaDataController"),
    ACCOUNT_NAME("AccountController"),
    COMMITTEE_NAME("GovernorController");

    private final String value;

    ContractEnum(String value) {
        this.value = value;
    }
}