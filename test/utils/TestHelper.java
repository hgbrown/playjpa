package utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.ArrayList;

import static play.test.Helpers.contentAsString;

public class TestHelper {
    private TestHelper() {
    }

    public static FakeApplication getFakeApplication() {
        return new FakeApplication(new java.io.File("."), Helpers.class.getClassLoader(),
                ImmutableMap.of("play.http.router", "router.Routes"), new ArrayList<>(), null);
    }

    public static <E> E resultToClass(final Result result, Class<E> expectedClass) {
        final String content = contentAsString(result);
        final JsonNode jsonNode = Json.parse(content);
        final E expectedInstance = Json.fromJson(jsonNode, expectedClass);
        return expectedInstance;
    }

}
