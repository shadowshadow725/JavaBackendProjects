package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import dagger.Module;
import dagger.Provides;

@Module
public class ServerModule {
    // TODO Complete This Module
    public HttpServer server;
    @Provides
    public HttpServer providesServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return server;
    }
}
