package com.webank.blockchain.data.export.plugin.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
@Slf4j
public class JsonHelper {

    public static JsonNode toJsonNode(String jsonString){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonString);
            return actualObj;
        }
        catch (Exception ex){
            log.error("error deserialize json",ex);
            return null;
        }
    }
//
//    public  static boolean containsKey(JsonNode jsonNode){
//        JsonNodeType jsonNodeType = jsonNode.getNodeType();
//        jsonNode.has("")
//    }
}
