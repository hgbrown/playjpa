package util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JsonMapConverterTest {

    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String EXPECTED_NAME = "john";
    public static final String EXPECTED_AGE = "29";

    final String json = "{\"name\":\"john\",\"age\":\"29\"}";
    final Map<String, Object> map = new HashMap<>();
    final JsonNode jsonNode = Json.parse(json);

    @Before
    public void setUp() throws Exception {
        map.put(NAME, EXPECTED_NAME);
        map.put(AGE, EXPECTED_AGE);
    }

    @Test
    public void shouldBeAbleToConvertStringToMap() throws Exception {
        final Map<String, Object> data = JsonMapConverter.toMap(json);

        assertThatMapIsAsExpected(data);
    }

    @Test
    public void shouldBeAbleToConvertJsonNodeToMap() throws Exception {
        final Map<String, Object> data = JsonMapConverter.toMap(jsonNode);

        assertThatMapIsAsExpected(data);
    }

    @Test
    public void shouldBeAbleToConvertMapToString() throws Exception {
        final String data = JsonMapConverter.toJson(map);

        assertThat(data, equalTo(json));
    }

    @Test
    public void shouldBeAbleToConvertMapToJsonNode() throws Exception {
        final JsonNode data = JsonMapConverter.toJsonNode(map);

        assertEquals(jsonNode, data);
    }

    private void assertThatMapIsAsExpected(Map<String, Object> data) {
        assertThat(data, notNullValue());
        assertThat(data.size(), equalTo(2));
        assertTrue(data.containsKey(NAME));
        assertTrue(data.containsKey(AGE));
        assertThat(data.get(NAME), equalTo(EXPECTED_NAME));
        assertThat(data.get(AGE), equalTo(EXPECTED_AGE));
    }

}
