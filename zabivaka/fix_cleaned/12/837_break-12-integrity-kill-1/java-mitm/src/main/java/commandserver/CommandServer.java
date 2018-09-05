package commandserver;

import com.squareup.moshi.Moshi;
import models.Response;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CommandServer {
    private final HttpUrl url;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    private final Moshi moshi = new Moshi.Builder().build();

    public CommandServer(String commandHost, int commandPort) {
        url = new HttpUrl.Builder()
            .scheme("http")
            .host(commandHost)
            .port(commandPort)
            .build();
    }

    public Response send(Object command) {
        String json = moshi.<Object>adapter(command.getClass()).toJson(command);
        String requestJson = moshi.adapter(models.Request.class).toJson(new models.Request(json));
        Request request = new Request.Builder()
            .post(RequestBody.create(null, requestJson))
            .url(url)
            .build();
        for (int i = 0; ; i++) {
            try {
                okhttp3.Response httpResponse = httpClient.newCall(request).execute();
                String response = httpResponse.body().string();
                System.err.println("Sent json: " + requestJson + "\nReceived response: " + response);
                return moshi.adapter(Response.class).fromJson(response);
            } catch (IOException e) {
                if (i == 2) {
                    throw new RuntimeException(e);
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }
        }
    }
}
