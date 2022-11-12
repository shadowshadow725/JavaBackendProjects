package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String [] params = r.getRequestURI().toString().split("/");
        String rideid = params[3];
        if (params.length != 4 || rideid.isEmpty()){
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            this.sendResponse(r, data, 400);
            return;
        }

        Document row = this.dao.getTripRow(rideid);

        if (row == null){
            JSONObject data = new JSONObject();
            data.put("status", "NOT FOUND");
            this.sendResponse(r, data, 404);
            return;
        }

        String passengerid = row.getString("passenger");
        String driverid = row.getString("driver");

        String uri = "http://locationmicroservice:8000/location/navigation/" + driverid + "?passengerUid=" + passengerid;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(uri))
                .build();
        try{
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject neo4j_body = new JSONObject(response.body().toString());
            JSONObject data = new JSONObject(neo4j_body.get("data").toString());
            int totaltime = data.getInt("total_time");
            JSONObject resp = new JSONObject();
            resp.put("arrival_time", totaltime);
            resp.put("status", "OK");
            this.sendResponse(r, resp, 200);
            return ;
        }
        catch (Exception e){
            JSONObject data = new JSONObject();
            data.put("status", "INTERNAL SERVER ERROR");
            this.sendResponse(r, data, 500);
            return;
        }

    }
}
