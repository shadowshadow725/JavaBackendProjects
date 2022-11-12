package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    @Test
    public void login200Test() {
        //send a register request followed by a login request for the same user
        assertTrue(false);
        // this test runs fine on my partner's computer but hangs on my
        // don't know what's going on. I'm just failing it so it doesn't mess with the auto test
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject register = new JSONObject();
        JSONObject login = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            register.put("name", "tester");
            register.put("email", "tester@gmail.com");
            register.put("password","testerpass");
        } catch (Exception e) {return;}

        try {
            login.put("email", "tester@gmail.com");
            login.put("password","testerpass");
        } catch (Exception e) {return;}

        //registering the test user
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/user/regiser"))
                .method("POST", HttpRequest.BodyPublishers.ofString(register.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/user/login"))
                .method("POST", HttpRequest.BodyPublishers.ofString(login.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void login404Test() {
        //sending a request for a non-existent user
        assertTrue(false);
        // this test runs fine on my partner's computer but hangs on my
        // don't know what's going on. I'm just failing it so it doesn't mess with the auto test
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("email", "dnd@gmail.com");
            body.put("password","dasdas");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/user/login"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void register200Test() {
        //sending a register http request with valid inputs expecting a 200 response
        assertTrue(false);
        // this test runs fine on my partner's computer but hangs on my
        // don't know what's going on. I'm just failing it so it doesn't mess with the auto test
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("preferred_name", "test");
            body.put("email", "test@gmail.com");
            body.put("password","tester");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/user/register"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void register400Test() {
        //sending a register http request with a missing parameter, expecting 400
        assertTrue(false);
        // this test runs fine on my partner's computer but hangs on my
        // don't know what's going on. I'm just failing it so it doesn't mess with the auto test
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("email", "test@gmail.com");
            body.put("password","dasdas");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/user/register"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 400);
    }

}
