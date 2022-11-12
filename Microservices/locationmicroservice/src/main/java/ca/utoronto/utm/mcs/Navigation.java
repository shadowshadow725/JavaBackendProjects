package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.internal.shaded.io.netty.handler.codec.json.JsonObjectDecoder;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {

        String body = Utils.convert(r.getRequestBody());
        String passengerUid;
        String driverUid;
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", "BAD REQUEST");
            this.sendResponse(r, resp, 400);
            return;
        }
        try{
                String [] arg = params[3].split("\\?");
                passengerUid = arg[1].replaceAll("passengerUid=", "");
                driverUid = arg[0];

                if (checkUidExist(driverUid) && checkUidExist(passengerUid)){
                    // both uid is fine
                    // find the street the driver and the passenger is on

                    String passengerStreet, driverStreet;
                    passengerStreet = getStreet(passengerUid);
                    driverStreet = getStreet(driverUid);

                    if (passengerStreet == null || driverStreet == null){
                        // if for some reason lookup fo the street fails return internal server error
                        JSONObject resp = new JSONObject();
                        resp.put("status", "INTERNAL SERVER ERROR");
                        this.sendResponse(r, resp, 500);

                        return ;
                    }

                    Result rs = this.dao.getShortestPath(passengerStreet, driverStreet);
                    if (rs.hasNext()){
                        Record rd = rs.next();
                        int totalCost = rd.get("totalCost").asInt();
                        List<Object> costs = new ArrayList<>();
                        List<Object> roads = new ArrayList<>();
                        List<Object> traffic = new ArrayList<>();

                        costs = rd.get("costs").asList();
                        roads = rd.get("nodeNames").asList();
                        traffic = rd.get("traffic").asList();

                        JSONArray return_route = new JSONArray();

                        int path_len = costs.size();

                        for ( int i = 0;i<path_len;i++){
                            JSONObject step = new JSONObject();

                            step.put("street", roads.get(i).toString());
                            step.put("time", (int)Double.parseDouble(costs.get(i).toString()));
                            if (traffic.get(i).toString().equals("false")){
                                step.put("has_traffic", false);
                            }
                            else {
                                step.put("has_traffic", true);
                            }

                            return_route.put(step);
//                          "street": String,
//                          "has_traffic": Boolean,
//                          "time": Integer
                        }

                        JSONObject data = new JSONObject();
                        data.put("total_time", totalCost);
                        data.put("route", return_route);
                        JSONObject resp = new JSONObject();
                        resp.put("status", "OK");
                        resp.put("data", data);
                        this.sendResponse(r, resp, 200);
                        return ;

                    }
                    else{
                        JSONObject resp = new JSONObject();
                        resp.put("status", "INTERNAL SERVER ERROR");

                        this.sendResponse(r, resp, 500);
                    }
                }
                else{
                    JSONObject resp = new JSONObject();
                    resp.put("status", "BAD REQUEST");
                    this.sendResponse(r, resp, 400);
                }
        }
        catch (Exception e){
            JSONObject resp = new JSONObject();
            resp.put("status", "INTERNAL SERVER ERROR");
            this.sendResponse(r, resp, 500);
        }

    }

    public boolean checkUidExist(String uid ){
        try {
            Result rs = this.dao.getUserByUid(uid);
            if (rs.hasNext()){
                // the uid is correct
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e ){
            return false;
        }

    }

    public String getStreet(String uid){
        Result rs = this.dao.getUserLocationByUid(uid);
        if (rs.hasNext()){
            Record rd = rs.next();
            return rd.get("n.street").asString();

        }

        return null;
    }

}
