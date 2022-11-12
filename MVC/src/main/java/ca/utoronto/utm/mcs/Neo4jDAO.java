package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;

import javax.inject.Inject;

// All your database transactions or queries should 
// go in this class
public class Neo4jDAO {
    public final Session session;
    public final Driver driver;




    private ArrayList path = new ArrayList();
    private ArrayList seenMovie = new ArrayList();
    private ArrayList seenActor = new ArrayList();
    private ArrayList actorLayer = new ArrayList();
    private ArrayList movieLayer = new ArrayList();
    private HashMap<String, String> mapam = new HashMap<>(); // actor, movie
    private HashMap<String, String> mapma = new HashMap<>(); // movie, actor
    private final String BACON = "nm0000102";

    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
        this.session = this.driver.session();

    }

    public void computeBaconNumberhandle(HttpExchange r) throws IOException{
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String actorId;
            if (deserialized.length() == 1 && deserialized.has("actorId")) {
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            JSONObject dbResponse;
            try {
                dbResponse = computeBaconNumber(actorId);
            } catch (Exception e) {
                r.sendResponseHeaders(404, -1);
                e.printStackTrace();
                return;
            }
            if (dbResponse != null){
                r.getResponseHeaders().set("Content-Type", "application/json");
                r.sendResponseHeaders(200, dbResponse.toString().length());
                OutputStream os = r.getResponseBody();
                os.write(dbResponse.toString().getBytes());
                os.close();
                return ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
        r.sendResponseHeaders(500, -1);
    }

    public JSONObject computeBaconNumber (String id) throws JSONException{
        ArrayList a = findBaconPath(id);
        if(a == null){
            return null;
        }
        JSONObject r = new JSONObject();
        r.put("baconNumber", a.size()/2);
        return  r;
    }

    public void findBaconPathhandle(HttpExchange r) throws IOException{

        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String actorId;
            if (deserialized.length() == 1 && deserialized.has("actorId")) {
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            ArrayList dbResponse;
            try {
                dbResponse = findBaconPath(actorId);
            } catch (Exception e) {
                r.sendResponseHeaders(404, -1);
                e.printStackTrace();
                return;
            }
            if (dbResponse != null){
                r.getResponseHeaders().set("Content-Type", "application/json");

                OutputStream os = r.getResponseBody();
                JSONObject j = new JSONObject();
                JSONArray ja = new JSONArray();
                for (Object i: dbResponse){
                    ja.put(i.toString());

                }
                j.put("baconPath", ja);
                r.sendResponseHeaders(200, j.toString().length());
                os.write(j.toString().getBytes());
                os.close();
                return ;
            }

        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
        r.sendResponseHeaders(500, -1);
    }

    public ArrayList findBaconPath(String id){
        if (id.equals(BACON)){
            ArrayList a = new ArrayList();
            a.add(BACON);
            return a ;
        }

        this.path = new ArrayList();
        this.seenMovie = new ArrayList();
        this.seenActor = new ArrayList();
        this.actorLayer = new ArrayList();
        this.movieLayer = new ArrayList();
        this.mapam = new HashMap<>(); // actor, movie
        this.mapma = new HashMap<>(); // movie, actor
        return startFindPath(id);
    }

    public String lookAtAllActorsInThisMovie(String prevActor, String movieid) throws IOException, JSONException{
        JSONObject json;
        JSONArray jsonarr;

        try{
            json = getMovie(movieid);
            jsonarr = (JSONArray) json.get("actors");
            for(int i = 0;i<jsonarr.length();i++){
                String ator = jsonarr.get(i).toString().replaceAll("\"", "");
                if (!seenActor.contains(ator)){
                    mapam.put(ator, movieid);
                    if (ator.equals(BACON)){
                        this.path.add(ator);
                        this.path.add(movieid);
                        return ator;
                    }
                    actorLayer.add(ator);
                    movieLayer.add(movieid);

                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return "";
    }

    public boolean actor1Layer(){
        ArrayList currentActorLayer = (ArrayList) actorLayer.clone();
        actorLayer.clear();
        movieLayer.clear();
        for (Object objActor: currentActorLayer){
            String startActor = (String)objActor;
            JSONObject json;
            JSONArray jsonarr;
            seenActor.add(startActor);
            try{
                json = getActor(startActor);
                jsonarr = (JSONArray) json.get("movies");
                for(int i = 0;i<jsonarr.length();i++){
                    String moie = jsonarr.getString(i).replaceAll("\"", "");
                    String nextStep = "";
                    if (!seenMovie.contains(moie)){
                        seenMovie.add(moie);
                        mapma.put(moie, startActor);
                        nextStep = lookAtAllActorsInThisMovie(startActor, moie);
                    }
                    if (!nextStep.equals("")){
                        // found
                        this.path.add(startActor);
                        return true;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean backprop(String start){
        // this builds the "path" using the hash map
        String curr_id = (String)this.path.get(this.path.size()-1);
        if (curr_id.equals(start)){
            return true;
        }
        while (mapam.containsKey(curr_id) || mapma.containsKey(curr_id)){
            if (mapam.containsKey(curr_id)){
                this.path.add(mapam.get(curr_id));
            }
            else{
                this.path.add(mapma.get(curr_id));
            }
            curr_id = (String)this.path.get(this.path.size()-1);
            if (curr_id.equals(start)){
                return true;
            }
        }
        return false;
    }

    public ArrayList startFindPath(String startActor){
        actorLayer.add(startActor);

        while (!actorLayer.isEmpty()){
            boolean r = actor1Layer();
            if (r){
                // found
                backprop(startActor);
                return this.path;
            }
        }

        return null;

    }


    public boolean addActor(String actorId, String name){
        String prequery = "MATCH (a: actor { actorId: \"" + actorId + "\"}) RETURN a.actorId;";
        Result result = this.session.run(prequery);
        while ( result.hasNext() )
        {
            Record record = result.next();
            String aid = record.get("a.actorId").toString();
            if (aid.equals("\""+actorId+"\"")){
                return false;
            }
        }
        String query = "CREATE (a: actor {name:\"" + name + "\", actorId: \"" + actorId +  "\" }) RETURN a;";
        this.session.run(query);
        return true;

    }

    public void addActorhandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String name, actorId;
            if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("actorId")) {
                name = deserialized.getString("name");
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            boolean dbResponse = false;
            try {
                dbResponse = addActor(actorId, name);
            } catch (Exception e) {
                r.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }
            if (!dbResponse){
                r.sendResponseHeaders(400, -1);
                return;
            }
            r.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
    }

    public boolean addMovie(String movieId, String name){
        String prequery = "MATCH (a: movie { movieId: \"" + movieId + "\"}) RETURN a.movieId;";
        Result result = this.session.run(prequery);
        while ( result.hasNext() )
        {
            Record record = result.next();
            String aid = record.get("a.movieId").toString();
            if (aid.equals("\""+movieId+"\"")){
                return false;
            }
        }
        String query = "CREATE (a: movie {name:\"" + name + "\", movieId: \"" + movieId + "\" }) RETURN a;";
        this.session.run(query);
        return true;

    }

    public void addMoviehandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String name, movieId;
            if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("movieId")) {
                name = deserialized.getString("name");
                movieId = deserialized.getString("movieId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            boolean dbResponse = false;
            try {
                dbResponse = addMovie(movieId, name);
            } catch (Exception e) {
                r.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }
            if (!dbResponse){
                r.sendResponseHeaders(400, -1);
                return;
            }
            r.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
    }

    public boolean addRelationship(String actorId, String movieId){
        String prequery = "MATCH (m { movieId: \"" + movieId + "\"}), ( a{actorId: \"" + actorId + "\"})-[r:ACTED_IN]->()\n" +
                "RETURN a.id, m.id;";
        Result result = this.session.run(prequery);
        while ( result.hasNext() )
        {
            Record record = result.next();
            String aid = record.get("a.id").toString().replaceAll("\"","");
            String mid = record.get("m.id").toString().replaceAll("\"","");
            if (aid.equals(actorId) && mid.equals(movieId)){
                return false;
            }
        }


        String query = "MATCH (a: actor), (m: movie)\n" +
                "WHERE a.actorId = \"" + actorId + "\" AND m.movieId = \"" + movieId +"\"\n" +
                "CREATE (a)-[r:ACTED_IN]->(m)\n" +
                "RETURN a.id, m.id;";

        this.session.run(query);
        return true ;

    }

    public void addRelationshiphandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String movieId, actorId;
            if (deserialized.length() == 2 && deserialized.has("movieId") && deserialized.has("actorId")) {
                movieId = deserialized.getString("movieId");
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            boolean dbResponse = false;
            try {
                dbResponse = addRelationship(actorId, movieId);
            } catch (Exception e) {
                r.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }
            if (!dbResponse){
                r.sendResponseHeaders(400, -1);
                return;
            }
            r.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
    }

    public JSONObject getActor(String actorId){
        String query1, query2;
        query1 = "MATCH (a)-[:ACTED_IN]->(m)\n" +
                " WHERE a.id = \"" + actorId + "\"" +
                " RETURN a.name, m.id;";
        query2 = "MATCH (a:actor)\n" +
                "WHERE a.id = \"" + actorId + "\"" +
                "RETURN a.name";
        Result result = this.session.run(query1);
        Result result2 = this.session.run(query2);
        JSONObject obj = new JSONObject();
        JSONArray allDataArray = new JSONArray();
        String name = "";
        while ( result.hasNext() )
        {
            Record record = result.next();
            allDataArray.put(record.get("m.id").toString().replaceAll("\"",""));
        }
        Record record2 = result2.next();
        name = record2.get( "a.name" ).asString();
        try {
            obj.put("actorId", actorId);
            obj.put("name", name);
            obj.put("movies", allDataArray);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }


        return obj;

    }

    public void getActorhandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String actorId;
            if (deserialized.length() == 1 && deserialized.has("actorId")) {
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            JSONObject dbResponse;
            try {
                dbResponse = getActor(actorId);
            } catch (Exception e) {
                r.sendResponseHeaders(404, -1);
                e.printStackTrace();
                return;
            }
            if (dbResponse != null){
                r.getResponseHeaders().set("Content-Type", "application/json");
                r.sendResponseHeaders(200, dbResponse.toString().length());
                OutputStream os = r.getResponseBody();
                os.write(dbResponse.toString().getBytes());
                os.close();
                return ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
        r.sendResponseHeaders(500, -1);
    }

    public JSONObject getMovie(String movieId){
        String query, query2;
        query = "MATCH (a)-[:ACTED_IN]->(m)\n" +
                " WHERE m.id = \"" + movieId + "\"" +
                " RETURN m.name, a.id;";
        query2 = "MATCH (m:movie)\n" +
                "WHERE m.id = \"" + movieId + "\"" +
                "RETURN m.name";

        Result result = this.session.run(query);
        Result result2 = this.session.run(query2);
        JSONObject obj = new JSONObject();
        JSONArray allDataArray = new JSONArray();
        String name = "";
        while ( result.hasNext() )
        {
            Record record = result.next();
            allDataArray.put(record.get("a.id").toString().replaceAll("\"",""));
        }
        Record record2 = result2.next();
        name = record2.get( "m.name" ).asString();

        try {
            obj.put("movieId", movieId);
            obj.put("name", name);
            obj.put("actors", allDataArray);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void getMoviehandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String movieId;
            if (deserialized.length() == 1 && deserialized.has("movieId")) {
                movieId = deserialized.getString("movieId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            JSONObject dbResponse;
            try {
                dbResponse = getMovie(movieId);
            } catch (Exception e) {
                r.sendResponseHeaders(404, -1);
                e.printStackTrace();
                return;
            }
            if (dbResponse != null){
                r.getResponseHeaders().set("Content-Type", "application/json");
                r.sendResponseHeaders(200, dbResponse.toString().length());
                OutputStream os = r.getResponseBody();
                os.write(dbResponse.toString().getBytes());
                os.close();
                return ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
        r.sendResponseHeaders(500, -1);
    }

    public JSONObject hasRelationship(String movieId, String actorId){
        String query;
        query = "MATCH  (a:actor), (m:movie)\n" +
                " WHERE m.id = \"" + movieId + "\" AND a.id = \"" + actorId + "\"" +
                " RETURN a.id, m.id, exists( (a)-[:ACTED_IN]-(m) );";

        Result result = this.session.run(query);
        JSONObject obj = new JSONObject();
        Record record = result.next();
        String hasRelationship = record.get( "exists( (a)-[:ACTED_IN]-(m) )" ).toString();

        try {
            obj.put("actorID", actorId);
            obj.put("movieId", movieId);
            obj.put("hasRelationship", hasRelationship);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void hasRelationshiphandle(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);
            String movieId, actorId;
            if (deserialized.length() == 2 && deserialized.has("movieId") && deserialized.has("actorId")) {
                movieId = deserialized.getString("movieId");
                actorId = deserialized.getString("actorId");
            } else {
                r.sendResponseHeaders(400, -1);
                return;
            }
            JSONObject dbResponse;
            try {
                dbResponse = hasRelationship(movieId, actorId);
            } catch (Exception e) {
                r.sendResponseHeaders(404, -1);
                e.printStackTrace();
                return;
            }
            if (dbResponse != null){
                r.getResponseHeaders().set("Content-Type", "application/json");
                r.sendResponseHeaders(200, dbResponse.toString().length());
                OutputStream os = r.getResponseBody();
                os.write(dbResponse.toString().getBytes());
                os.close();
                return ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.sendResponseHeaders(500, -1);
        }
        r.sendResponseHeaders(500, -1);
    }



}
