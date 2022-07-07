pragma solidity>=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

contract HelloWorld {
    string name;

    struct KVField {
        string key;
        string value;
    }

    KVField field;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory,KVField memory field) {
        return (name, field);
    }

    function set(string memory n, KVField memory field) public {
        name = n;
        field = field;
    }
}