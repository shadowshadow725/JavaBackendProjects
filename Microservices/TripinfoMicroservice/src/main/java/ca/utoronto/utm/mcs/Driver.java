package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String [] params = r.getRequestURI().toString().split("/");
        String driverid = params[3];

        if (params.length != 4 || driverid.isEmpty()){
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            this.sendResponse(r, data, 400);
            return;
        }
        JSONArray trips = this.dao.getDriverTrips(driverid);
        if (trips == null){
            // something went wrong
            JSONObject data = new JSONObject();
            data.put("status", "INTERNAL SERVER ERROR");
            this.sendResponse(r, data, 500);
            return;
        }
        if (trips.length() == 0){
            JSONObject rbody = new JSONObject();
            rbody.put("status", "NOT FOUND");
            this.sendResponse(r, rbody, 404);
            return;
        }
        JSONObject resp = new JSONObject();
        resp.put("status", "OK");
        resp.put("trips", trips);
        this.sendResponse(r, resp, 200);
        return ;
    }
}
