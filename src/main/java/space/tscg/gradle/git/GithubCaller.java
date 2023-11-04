package space.tscg.gradle.git;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GithubCaller
{
    private static final OkHttpClient CLIENT       = new OkHttpClient.Builder().build();
    private static final ObjectMapper MAPPER       = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true).configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

    static String githubUrl = "https://api.github.com/repos/%s/%s";

    public static GithubRepository info(String owner, String name)
    {
        return call(githubUrl.formatted(owner, name), GithubRepository.class);
    }

    static <T> T call(String url, Class<T> clazz)
    {
        var request = new Request.Builder().url(url).build();
        try (var r = CLIENT.newCall(request).execute())
        {
            if (r.isSuccessful())
                return fromJson(r.body().string(), clazz);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException
    {
        return MAPPER.readValue(json, clazz);
    }
}
