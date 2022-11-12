package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    static int port = 8080;
    public static Neo4jDAO dao;
    public static void main(String[] args) throws IOException
    {
        // TODO Create Your Server Context Here, There Should Only Be One Context

        // This code is used to get the neo4j address, you must use this so that we can mark :)

        //creating sever with Dagger2
        ServerComponent svrcomp = DaggerServerComponent.create();
        Server svr = svrcomp.buildServer();
        //creating request handler
        ReqHandlerComponent reqcomp = DaggerReqHandlerComponent.create();
        ReqHandler req = reqcomp.buildHandler();

        svr.getServer().createContext("/api/v1/", req);
        svr.getServer().start();

        System.out.printf("Server started on port %d...\n", port);
    }
}
