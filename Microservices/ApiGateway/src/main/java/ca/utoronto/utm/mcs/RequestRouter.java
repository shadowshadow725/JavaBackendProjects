package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RequestRouter implements HttpHandler {
	
    /**
     * You may add and/or initialize attributes here if you 
     * need.
     */
	public RequestRouter() {

	}

	public void reply(String uri, HttpExchange r) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		InputStreamReader isr =  new InputStreamReader(r.getRequestBody(),"utf-8");
		BufferedReader br = new BufferedReader(isr);

		// From now on, the right way of moving from bytes to utf-8 characters:

		int b;
		StringBuilder buf = new StringBuilder(20000);
		while ((b = br.read()) != -1) {
			buf.append((char) b);
		}

		br.close();
		isr.close();
		String body = buf.toString();
		HttpRequest request;
		switch (r.getRequestMethod()) {
			case "GET":
				request = HttpRequest.newBuilder().GET()
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;

			case "PUT":
				request = HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;


			case "PATCH":
				request = HttpRequest.newBuilder().method("PATCH", HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;

			case "POST":
				request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;

			case "DELETE":
				request = HttpRequest.newBuilder().method("DELETE", HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;
			default:
				request = HttpRequest.newBuilder().method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.uri(URI.create(uri))
						.build();
				break;
		}




		HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

		r.sendResponseHeaders(response.statusCode(), response.body().toString().getBytes().length);
		OutputStream os = r.getResponseBody();
		os.write(response.body().toString().getBytes());
		os.close();


	}

	@Override
	public void handle(HttpExchange r) throws IOException {


		String requesturi = r.getRequestURI().toString();
		if (requesturi.contains("location")){
			// location
			String newuri = "http://locationmicroservice:" + "8000" + requesturi;
			System.out.println(newuri);
			try {
				reply(newuri, r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ;
		}
		else if (requesturi.contains("user")){
			String newuri = "http://usermicroservice:" + "8000" + requesturi;
			System.out.println(newuri);
			try {
				reply(newuri, r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if (requesturi.contains("trip")){
			String newuri = "http://tripinfomicroservice:" + "8000" + requesturi;
			try {
				reply(newuri, r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
