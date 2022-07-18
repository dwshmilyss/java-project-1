package com.yiban.javaBase.dev.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.util.List;

public class JacksonDemo {
    private static final String DEFAULT_PROPS_NAME = "props";

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = "{\"action\":\"set\",\"domain\":\"contact\",\"data\":{\"arr\":[\"徐汇\",\"上海\",\"南京\"],\"city\":\"徐汇\",\"lastUpdated\":1629431024000,\"mobilePhone\":\"15951112027\"},\"tenantId\":1,\"ts\":1629431024260,\"id\":5677199}";
//        String jsonStr = "{\"action\":\"track\",\"domain\":\"event\",\"data\":{\"anonymousId\":\"wmv2dLDQAAGegNzmP8MTL6aLGouIZ6mg\",\"eventAlias\":\"WORKWECHAT_PRO__FOLLOW\",\"contactIdentityId\":5671125,\"lastUpdated\":1630305016000,\"debugMode\":\"false\",\"contactId\":5680055,\"event\":\"WORKWECHAT_PRO__FOLLOW\",\"props\":{\"s_employeeID\":\"CaiLei\",\"s_name\":\"蔡蕾\"},\"eventDate\":1630305015000,\"dateCreated\":1630305016000,\"channelId\":570},\"tenantId\":1,\"dateCreated\":1630305016000,\"ts\":1630308201351,\"id\":1907846,\"version\":2}";
        JsonNode node = objectMapper.readTree(jsonStr);
        System.out.println("node.get(\"tenantId\") = " + node.get("tenantId"));
        System.out.println("node.get(\"domain\") = " + node.get("domain"));
        JsonNode arrNode = new ArrayNode(new JsonNodeFactory(false)).add(node);
//        getValue("city", arrNode);
//        getValue("attr14", arrNode);
        JsonNode v = getValue("mobilePhone", arrNode);
        System.out.println("v = " + v.toString());
    }

    private static JsonNode getValue(String key, JsonNode node) {
        List<JsonNode> jsonNodeList = node.findValues(key);
        JsonNode v = null;
        try {
            if (jsonNodeList.size() >= 1) {
                if (node.get(0).get("data").get(DEFAULT_PROPS_NAME) != null) {
                    JsonNode p_v = node.get(0).get("data").get(DEFAULT_PROPS_NAME).findValue(key);
                    if (true) {
                        jsonNodeList.remove(p_v);
                        v = jsonNodeList.get(0);
                    } else {
                        v = p_v;
                    }
                } else {
                    v = jsonNodeList.get(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }
}
