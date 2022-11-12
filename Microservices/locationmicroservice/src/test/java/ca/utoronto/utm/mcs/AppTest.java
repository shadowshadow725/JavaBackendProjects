package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    @BeforeAll
    public static void fillDB() {
        //inserting a few test case users, roads, and route
        HttpRequest request = null;
        JSONObject user1 = new JSONObject();
        JSONObject user2 = new JSONObject();
        JSONObject location1 = new JSONObject();
        JSONObject location2 = new JSONObject();
        JSONObject road1 = new JSONObject();
        JSONObject road2 = new JSONObject();
        JSONObject route = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;

        try {
            user1.put("uid", "999");
            user1.put("is_driver",false);
        } catch (Exception e) {return;}
        try {
            user2.put("uid", "998");
            user2.put("is_driver",true);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/user"))
                .method("PUT", HttpRequest.BodyPublishers.ofString(user1.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/user"))
                .method("PUT", HttpRequest.BodyPublishers.ofString(user2.toString()))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        try {
            location1.put("street","r1");
            location1.put("longitude", 0.0);
            location1.put("latitude",0.0);
        } catch (Exception e) {return;}
        try {
            location2.put("street","r2");
            location2.put("longitude", 0.0);
            location2.put("latitude",0.0);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(location1.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {assertTrue(response.statusCode() == 200);}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/998"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(location2.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        try {
            road1.put("roadName", "r1");
            road1.put("hasTraffic",false);
        } catch (Exception e) {return;}
        try {
            road2.put("roadName", "r2");
            road2.put("hasTraffic",false);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/road"))
                .method("PUT", HttpRequest.BodyPublishers.ofString(road1.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/road"))
                .method("PUT", HttpRequest.BodyPublishers.ofString(road2.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        try {
            route.put("roadName1", "r1");
            route.put("roadName2","r2");
            route.put("hasTraffic",false);
            route.put("time",4);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/hasRoute"))
                .method("POST", HttpRequest.BodyPublishers.ofString(route.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
    }

    @AfterAll
    public static void tearDown() {
        //deleting all test cases
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();
        JSONObject user1 = new JSONObject();
        JSONObject user2 = new JSONObject();
        JSONObject route = new JSONObject();

        try {
            user1.put("uid", "999");
        } catch (Exception e) {return;}

        try {
            user2.put("uid", "998");
        } catch (Exception e) {return;}

        try {
            route.put("roadName1", "r1");
            route.put("roadName2","r2");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/user"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(user1.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/user"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(user2.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/route"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(route.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
    }

    @Test
    public void nearbyDriver200Test() {
        //sending a http request to get nearby drivers to uid 999
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/nearbyDriver/999?radius=5"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void nearbyDriver404Test() {
        //sending a http request to get nearby drivers of a non-existent user, expecting 404 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/nearbyDriver/4?radius=3"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void navigation200Test() {
        //sending a http request to navigate between driver 998 and passenger 999
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/navigation/998?passengerUid=999"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void navigation404Test() {
        //sending a http request to navigate, but driver is a non-existent user
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/navigation/3?passengerUid=999"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

}
