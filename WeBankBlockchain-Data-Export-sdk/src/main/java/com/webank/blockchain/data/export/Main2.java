package com.webank.blockchain.data.export;

import com.webank.blockchain.data.export.plugin.model.DecodedEvent;
import com.webank.blockchain.data.export.plugin.utils.DecoderHelper;
import org.fisco.bcos.sdk.abi.TypeEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class Main2 {

    public static void main(String[] args) throws Exception{
//        CryptoSuite cryptoSuite = new CryptoSuite(0);
//        String proposalAbiStr = "[{\"constant\":true,\"inputs\":[{\"name\":\"proposalType\",\"type\":\"uint8\"},{\"name\":\"resourceId\",\"type\":\"bytes32\"}],\"name\":\"findProposalByTypeAndResourceId\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"governor\",\"type\":\"address\"},{\"name\":\"proposalType\",\"type\":\"uint8\"},{\"name\":\"resourceId\",\"type\":\"bytes32\"}],\"name\":\"createProposal\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalMapping\",\"type\":\"address\"}],\"name\":\"setProposalMapping\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_accountController\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalStatus\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_proposalMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"revokeProposal\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalInfo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"proposalId\",\"type\":\"uint256\"},{\"name\":\"agree\",\"type\":\"bool\"}],\"name\":\"voteThenExecute\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"accountController\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"governor\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposer\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposalId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"proposalType\",\"type\":\"uint8\"},{\"indexed\":false,\"name\":\"resource\",\"type\":\"bytes32\"}],\"name\":\"CreateProposal\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"voter\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"proposalId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"agree\",\"type\":\"bool\"}],\"name\":\"Vote\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"revoker\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"RevokeProposal\",\"type\":\"event\"}]";
//
//        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
//        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(proposalAbiStr);
//        Utf8String utf8String = new Utf8String("aaa");
//
//        ABIDefinition abiDefinition = contractABIDefinition.getEvents().get("CreateProposal").get(0);
//        ABIDefinition.NamedType namedType = new ABIDefinition.NamedType();
//        namedType.setType("string");
//        DecodedEvent decodeResult = DecoderHelper.decodeOne(namedType,         TypeEncoder.encode(utf8String));
//        System.out.println(decodeResult.getJavaValue());
    }

}
