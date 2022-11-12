package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // TODO
        try{
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

            if (body.has("distance") && body.has("endTime") && body.has("timeElapsed") && body.has("totalCost")) {
                String [] params = r.getRequestURI().toString().split("/");
                String rideid = params[2];
                int distance = body.getInt("distance");
                int endtime = body.getInt("endTime");
                int timeElapsed = body.getInt("timeElapsed");
                double totalCost = Double.parseDouble(body.getString("totalCost"));

                boolean success = this.dao.updateTrip(endtime, distance, timeElapsed, totalCost, rideid);

                if (success){
                    JSONObject resp = new JSONObject();
                    resp.put("status", "OK");
                    this.sendResponse(r, resp, 200);
                    return;
                }
                else {

                    JSONObject rbody = new JSONObject();
                    rbody.put("status", "NOT FOUND");
                    this.sendResponse(r, rbody, 404);
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
        catch (Exception e){
            JSONObject data = new JSONObject();
            data.put("status", "INTERNAL SERVER ERROR");
            this.sendResponse(r, data, 500);
            return;
        }


    }
}
