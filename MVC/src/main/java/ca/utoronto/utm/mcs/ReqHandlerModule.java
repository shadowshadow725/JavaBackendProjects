package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.*;

import java.io.IOException;
import java.net.InetSocketAddress;

@Module
public class ReqHandlerModule {
    public Driver d;
    public Neo4jDAO dao;

    @Provides
    public Driver providesDriver(){
        return d;
    }



    @Provides
    public Neo4jDAO providesNeo4jDAO(){
        return dao;
    }
    public ReqHandler hand;


    @Provides
    public ReqHandler providesReqHandler() {

        final String password = "123456";
        final String username = "neo4j";
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        String uriDb = "bolt://" + addr + ":" + "7687";
        System.out.println(addr);
        d = GraphDatabase.driver(uriDb, AuthTokens.basic(username, password));
        dao = new Neo4jDAO(d);


        try {
            hand = new ReqHandler(dao);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return hand;

    }


}
