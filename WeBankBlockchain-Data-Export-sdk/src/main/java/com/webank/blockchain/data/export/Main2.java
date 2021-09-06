package com.webank.blockchain.data.export;

import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import com.webank.blockchain.data.export.plugin.model.DecodedEvent;
import com.webank.blockchain.data.export.plugin.utils.DecoderHelper;
import org.fisco.bcos.sdk.abi.TypeEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.TransactionReceipt;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class Main2 {

    public static void main(String[] args) throws Exception{
        CryptoSuite cryptoSuite = new CryptoSuite(0);
        String proposalAbiStr = "[{\"constant\":true,\"inputs\":[{\"name\":\"proposalType\",\"type\":\"uint8\"},{\"name\":\"resourceId\",\"type\":\"bytes32\"}],\"name\":\"findProposalByTypeAndResourceId\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"governor\",\"type\":\"address\"},{\"name\":\"proposalType\",\"type\":\"uint8\"},{\"name\":\"resourceId\",\"type\":\"bytes32\"}],\"name\":\"createProposal\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalMapping\",\"type\":\"address\"}],\"name\":\"setProposalMapping\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_accountController\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalStatus\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_proposalMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"revokeProposal\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalInfo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"},{\"name\":\"agree\",\"type\":\"bool\"}],\"name\":\"voteThenExecute\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"accountController\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"governor\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposer\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposalId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"proposalType\",\"type\":\"uint8\"},{\"indexed\":false,\"name\":\"resource\",\"type\":\"bytes32\"}],\"name\":\"CreateProposal\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"voter\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposalId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"agree\",\"type\":\"bool\"}],\"name\":\"Vote\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"revoker\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"RevokeProposal\",\"type\":\"event\"}]";

        String logs = "{\n" +
                "\t\"address\": \"0x88dd24e93a8dc33c3f5c71102a2ad96b96173e21\",\n" +
                "\t\"topics\": [\"0x7bf4f57c9ce98aef284f9e88f8379483670ff457a3a90f465bc59638ebec4b90\", \"0x000000000000000000000000b437e8e2111301253231103e7de4b38eee7f9b8b\", \"0x0000000000000000000000005aabf874b98038d0e371e3abe6cb67a661630561\", \"0x0000000000000000000000000000000000000000000000000000000000000001\"],\n" +
                "\t\"data\": \"0x00000000000000000000000000000000000000000000000000000000000000019e23bacceb14ccff6113ae3ae1cd14b4df3eb4ff64bb60328d5c1c8e23223769\",\n" +
                "\t\"blockNumber\": null\n" +
                "}";
        TransactionReceipt.Logs eventLogs = JacksonUtils.fromJson(logs, TransactionReceipt.Logs.class);
        ContractABIDefinition contractABIDefinition = new ABIDefinitionFactory(cryptoSuite).loadABI(proposalAbiStr);

        ABIDefinition abiDefinition = contractABIDefinition.getEvents().get("CreateProposal").get(0);

        DecoderHelper.decodeEvent(eventLogs, abiDefinition);
    }

}
