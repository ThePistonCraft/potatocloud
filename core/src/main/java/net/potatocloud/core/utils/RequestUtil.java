package net.potatocloud.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@UtilityClass
public class RequestUtil {

    @SneakyThrows
    public JsonObject request(String url) {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest buildRequest = HttpRequest.newBuilder(URI.create(url)).build();
        final HttpResponse<String> buildResponse = client.send(buildRequest, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(buildResponse.body()).getAsJsonObject();
    }
}
