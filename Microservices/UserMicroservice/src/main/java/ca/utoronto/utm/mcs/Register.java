package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */


    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("name") && body.has("email") && body.has("password")) {
            boolean emailExist = false ;
            // look for this email address in the database
            // respond with 500 if something goes wrong in the query
            try{

                emailExist = this.dao.checkEmailExist(body.getString("email"));
            }
            catch (Exception e ){
                JSONObject resp = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                resp.put("data", data);
                this.sendResponse(r, resp, 500);
                return;
            }
            if (emailExist){
                // making the response
                JSONObject resp = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("status", "CONFLICT");
                resp.put("data", data);
                this.sendResponse(r, resp, 400);
                return;
            }
            try {

                ResultSet rs = this.dao.registerUser(body.getString("email"), body.getString("name"), body.getString("password"));
                rs.next();

                JSONObject data = new JSONObject();
                data.put("status", "OK");
                data.put("uid", rs.getInt("uid"));

                this.sendResponse(r, data, 200);

            } catch (Exception e) {
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                this.sendResponse(r, data, 500);
                return ;
            }
        } else {

            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");
            this.sendResponse(r, data, 400);
            return;
        }
    }
}
