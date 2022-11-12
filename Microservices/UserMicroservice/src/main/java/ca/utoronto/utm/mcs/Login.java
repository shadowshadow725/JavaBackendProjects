package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */



    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("email") && body.has("password")) {
            boolean emailExist = false;
            // look for this email address in the database
            // respond with 500 if something goes wrong in the query
            // if email is not in the db return 404
            try{
                emailExist = this.dao.checkEmailExist(body.getString("email"));
            }
            catch (Exception e ){
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                this.sendResponse(r, data, 500);

                return;
            }
            if (!emailExist){
                // making the response
                JSONObject data = new JSONObject();
                data.put("status", "NOT FOUND");
                this.sendResponse(r, data, 404);
                return;
            }
            try {
                int uid = this.dao.loginUser(body.getString("email"), body.getString("password"));
                if (uid > 0){
                    JSONObject data = new JSONObject();
                    data.put("status", "OK");
                    data.put("uid", uid);
                    this.sendResponse(r, data, 200);
                    return ;
                }

            } catch (Exception e) {
                JSONObject data = new JSONObject();
                data.put("status", "INTERNAL SERVER ERROR");
                this.sendResponse(r, data, 500);
                return ;
            }
            // email is found
            // email password pair is not found


            JSONObject data = new JSONObject();
            data.put("status", "UNAUTHORIZED");
            this.sendResponse(r, data, 401);
            return ;

        } else {
            JSONObject data = new JSONObject();
            data.put("status", "BAD REQUEST");

            this.sendResponse(r, data, 400);
            return;
        }
    }


}
