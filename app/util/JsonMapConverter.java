package util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonMapConverter {

    static final transient Logger.ALogger LOG = Logger.of(JsonMapConverter.class);

    public static Map<String, Object> toMap(String json) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            final Map<String, Object> map = mapper.readValue(json, new TypeReference<HashMap<String,Object>>(){});
            LOG.debug("map=[{}]", map);
            return map;
        } catch (IOException e) {
            final String errorMessageTemplate = "Unable to convert json: %s to map";
            throw new JsonConversionException(String.format(errorMessageTemplate, json), e);
        }
    }

    public static Map<String, Object> toMap(JsonNode jsonNode) {
        return toMap(jsonNode.toString());
    }

    public static String toJson(Map<String, Object> map) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            final String json = mapper.writeValueAsString(map);
            LOG.debug("json=[{}]", json);
            return json;
        } catch (JsonProcessingException e) {
            final String errorMessageTemplate = "Unable to convert map: %s to json";
            throw new JsonConversionException(String.format(errorMessageTemplate, map), e);
        }
    }

    public static JsonNode toJsonNode(Map<String, Object> map) {
        final String json = toJson(map);
        return Json.parse(json);
    }

}
