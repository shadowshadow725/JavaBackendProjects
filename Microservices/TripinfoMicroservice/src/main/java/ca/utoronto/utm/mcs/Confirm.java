package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            String driver = body.getString("driver");
            String passenger = body.getString("passenger");
            int starttime = body.getInt("startTime");
            String id = this.dao.tripConfirm(passenger, driver, starttime);

            if (id == null){
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                this.sendResponse(r, data, 500);
                return;
            }

            JSONObject resp = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject _id = new JSONObject();
            resp.put("status", "OK");
            _id.put("$oid", id);
            data.put("_id", _id);
            resp.put("data", data);

            this.sendResponse(r, resp, 200);
            return ;


        }
        else {
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            this.sendResponse(r, data, 400);
            return;
        }
    }
}
