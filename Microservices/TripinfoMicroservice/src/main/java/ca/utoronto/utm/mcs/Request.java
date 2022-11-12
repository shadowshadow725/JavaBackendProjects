package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.JarURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        if (body.has("uid") && body.has("radius")) {
            String uid = body.getString("uid");
            String radius = body.getString("radius");

            if (Integer.parseInt(radius) < 0 ){
                JSONObject data = new JSONObject();
                data.put("status", "BAD REQUEST");
                this.sendResponse(r, data, 400);
                return;
            }

            String uri = "http://locationmicroservice:8000/location/nearbyDriver/" + uid + "?radius=" + radius;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .build();

            try {
                HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200){
                    JSONObject rbody = new JSONObject();
                    rbody.put("status", "NOT FOUND");
                    this.sendResponse(r, rbody, 404);
                    return;
                }
                JSONObject neo4j_body = new JSONObject(response.body().toString());
                JSONObject neo4j_body_data = (JSONObject) neo4j_body.get("data");




                JSONArray data = new JSONArray();
                JSONObject resp = new JSONObject();
                Iterator<String> it ;
                try{
                    it = neo4j_body_data.keys();
                }
                catch(Exception e){
                    JSONObject rbody = new JSONObject();
                    rbody.put("status", "NOT FOUND");
                    this.sendResponse(r, rbody, 404);
                    return;
                }

                while (it.hasNext()) {
                    String key = it.next();
                    data.put(key);
                }
                resp.put("data", data);
                resp.put("status", "OK");
                this.sendResponse(r, resp, 200);
                return;
            } catch (Exception e) {
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                this.sendResponse(r, data, 500);
                return;

            }
        }
        else {
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            this.sendResponse(r, data, 400);
            return;
        }
    }
}
