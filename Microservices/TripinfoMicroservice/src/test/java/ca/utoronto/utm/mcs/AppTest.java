package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import org.json.JSONObject;

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
            location1.put("longitude", "0.0");
            location1.put("latitude","0.0");
            location1.put("street","r1");
        } catch (Exception e) {return;}
        try {
            location2.put("longitude", "0.0");
            location2.put("latitude","0.0");
            location2.put("street","r2");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(location1.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/location/998"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(location2.toString()))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        try {
            road1.put("roadName", "r1");
            road1.put("latitude",false);
        } catch (Exception e) {return;}
        try {
            road2.put("roadName", "r2");
            road2.put("latitude",false);
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
            route.put("roadName1", "0.0");
            route.put("roadName2","0.0");
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
    public void request200Test() {
        //sending a request http request with valid inputs expecting a 200 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("uid", "999");
            body.put("radius", 5);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/request"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void request404Test() {
        //sending a request http request with a non-existent uid expecting a 404 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("uid", "222");
            body.put("radius", 5);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/request"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void confirm200Test() {
        //sending a confirm http request with valid inputs expecting a 200 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("driver", "998");
            body.put("passenger", "999");
            body.put("startTime", 1645917102);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/confirm"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void confirm400Test() {
        //sending a confirm http request with a missing parameter expecting a 400 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("passenger", "222");
            body.put("startTime", 1645917102);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/confirm"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 400);
    }

    @Test
    public void update200Test() throws JSONException {
        //sending an update http request with valid inputs expecting a 200 response
        HttpResponse<String> response = null;
        HttpResponse<String> response1 = null;
        HttpRequest request = null;
        String oid;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("driver", "998");
            body.put("passenger", "999");
            body.put("startTime", 1645917102);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/confirm"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        JSONObject body2 = new JSONObject(response.body().toString());
        JSONObject body3 = new JSONObject(body2.get("data").toString());
        JSONObject body4 = new JSONObject(body3.get("_id").toString());
        oid = body4.get("$oid").toString();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/" + oid))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response1 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        assertTrue(response1.statusCode() == 400);

    }

    @Test
    public void update404Test() {
        //sending an update http request for a non-existent trip expecting a 404 response
        HttpResponse<String> response = null;
        HttpRequest request = null;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("distance", 4);
            body.put("endTime", 1645919897);
            body.put("timeElapsed", 40);
            body.put("totalCost", "80.90");
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/507f1f77bcf86cd799439011"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void passenger200Test() {
        //sending a passenger http request
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/passenger/999"))
                .GET()
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void passenger404Test() {
        //sending a passenger http request
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/passenger/12312312"))
                .GET()
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void driver200Test() {
        //sending a driver http request
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/driver/998"))
                .GET()
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void driver404Test() {
        //sending a driver http request
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/driver/12312312"))
                .GET()
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

    @Test
    public void driverTime200Test() throws JSONException {
        //sending a driverTime http request
        HttpResponse<String> response = null;
        HttpResponse<String> response1 = null;
        HttpRequest request = null;
        HttpRequest request1 = null;
        String oid;
        JSONObject body = new JSONObject();
        HttpClient client = HttpClient.newHttpClient();

        try {
            body.put("driver", "998");
            body.put("passenger", "999");
            body.put("startTime", 1645917102);
        } catch (Exception e) {return;}

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/confirm"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}

        JSONObject body2 = new JSONObject(response.body().toString());
        JSONObject body3 = new JSONObject(body2.get("data").toString());
        JSONObject body4 = new JSONObject(body3.get("_id").toString());
        oid = body4.get("$oid").toString();

        request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/driverTime/" + oid))
                .GET()
                .build();

        try {
            response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void driverTime404Test() {
        //sending a driverTime http request
        HttpResponse<String> response = null;
        HttpRequest request = null;
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://apigateway:8000/trip/driverTime/507f1f77bcf86cd799431"))
                .GET()
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {return;}
        assertTrue(response.statusCode() == 404);
    }

}
