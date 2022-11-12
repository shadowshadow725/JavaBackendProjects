package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class
    public Neo4jDAO dao;

    @Inject
    public ReqHandler(Neo4jDAO n4){
        this.dao = n4;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if(exchange.getRequestURI().toString().equals("/api/v1/getActor")){
                        this.dao.getActorhandle(exchange);
                    }
                    else if(exchange.getRequestURI().toString().equals("/api/v1/getMovie")){
                        this.dao.getMoviehandle(exchange);
                    }
                    else if(exchange.getRequestURI().toString().equals("/api/v1/hasRelationship")){
                        this.dao.hasRelationshiphandle(exchange);
                    }
                    else if(exchange.getRequestURI().toString().equals("/api/v1/computeBaconNumber")){
                        this.dao.computeBaconNumberhandle(exchange);
                    }
                    else if(exchange.getRequestURI().toString().equals("/api/v1/computeBaconPath")){
                        this.dao.findBaconPathhandle(exchange);
                    }
                    else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                    break;
                case "PUT":
                    if (exchange.getRequestURI().toString().equals("/api/v1/addActor")){
                        this.dao.addActorhandle(exchange);
                    }
                    else if (exchange.getRequestURI().toString().equals("/api/v1/addMovie")){
                        this.dao.addMoviehandle(exchange);
                    }
                    else if (exchange.getRequestURI().toString().equals("/api/v1/addRelationship")) {
                        this.dao.addRelationshiphandle(exchange);
                    }
                    else {
                        exchange.sendResponseHeaders(404, -1);
                    }

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}