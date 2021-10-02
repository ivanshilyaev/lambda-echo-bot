package com.ivanshilyaev;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private String extractToken() throws Exception {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(""))
                .getPath();
        String appConfigPath = rootPath + "app.properties";
        Properties properties = new Properties();
        properties.load(new FileInputStream(appConfigPath));

        return properties.getProperty("token");
    }

    private void sendMessage(String text, String chatId) throws Exception {
        String url = "https://api.telegram.org/bot" + extractToken()
                + "/sendMessage?text=" + text
                + "&chat_id=" + chatId;

        var client = new OkHttpClient();
        var request = new Request.Builder().url(url).build();
        client.newCall(request).execute();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        try {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);
            JsonObject body = JsonParser.parseString(event.getBody()).getAsJsonObject();
            String chatId = body.getAsJsonObject("message").getAsJsonObject("chat").get("id").getAsString();
            String reply = body.getAsJsonObject("message").get("text").getAsString();
            log.info("Message has been received");
            sendMessage(reply, chatId);
            log.info("Message has been sent");

            return response.withStatusCode(200);
        } catch (Exception e) {
            log.error("Can't process request", e);
            return new APIGatewayProxyResponseEvent().withHeaders(headers).withStatusCode(500);
        }
    }
}
