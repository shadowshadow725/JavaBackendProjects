package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {

        String [] params = r.getRequestURI().toString().split("/");
        System.out.println(params[3]);
        String [] arg = params[3].split("\\?");


        if (params.length != 4 || params[3].isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", "BAD REQUEST");
            this.sendResponse(r, resp, 400);
            return;
        }


        String uid;
        int radius;
        try{

//                uid = params[3];
//                radius = deserialized.getInt("radius");
                uid = arg[0];
                radius = Integer.parseInt(arg[1].replaceAll("radius=", ""));

                Result rs = this.dao.getUserByUid(uid);
                if (!rs.hasNext()){
                    JSONObject resp = new JSONObject();
                    resp.put("status", "NOT FOUND");
                    this.sendResponse(r, resp, 404);

                }

                rs = this.dao.getUserLocationByUid(uid);
                if (rs.hasNext()){
                    Record record = rs.next();
                    double lng = Double.parseDouble(record.get("n.longitude").toString());
                    double lat = Double.parseDouble(record.get("n.latitude").toString());

                    rs = this.dao.getDistanceAll(lng, lat, radius);
                    JSONObject resp = new JSONObject();
                    JSONObject data = new JSONObject();

                    while (rs.hasNext()){
                        record = rs.next();
                        JSONObject driver = new JSONObject();
                        driver.put("longitude", record.get("n.longitude").toString());
                        driver.put("latitude", record.get("n.latitude").toString());
                        driver.put("street", record.get("n.street").toString().replaceAll("\"", ""));
                        data.put(record.get("n.uid").toString().replaceAll("\"", ""), driver);
                    }

                    resp.put("data", data);
                    resp.put("status", "OK");
                    this.sendResponse(r, resp, 200);
                    return;
                }


        }
        catch (Exception e){
            JSONObject resp = new JSONObject();
            resp.put("status", "INTERNAL SERVER ERROR");
            this.sendResponse(r, resp, 500);
        }



        



    }
}
