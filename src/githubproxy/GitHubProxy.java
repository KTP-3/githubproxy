package githubproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.proxy.ProxyHandler;
import org.eclipse.jetty.proxy.ProxyHandler.Forward;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

public class GitHubProxy {

	public static void main(String[] args) throws Exception {
		Server server = new Server(9090);
		
		Forward handler = new ProxyHandler.Forward() {
			@Override
			protected HttpURI rewriteHttpURI(Request clientToProxyRequest) {
			    String uri = clientToProxyRequest.getHttpURI().toString();
			    
				if (uri.contains("service=git-upload-pack")) {
					boolean hasUncommitted=true;
			        System.out.println("Pull/fetch detected: " + uri);
			        String workspaceId = "06f10733-c0a0-450e-89ae-de7fa0dbc424";
			        HttpClient client = HttpClient.newHttpClient();
			        String url = "http://localhost:8080/api/v1/proxy-manager/workspaces/" + workspaceId + "/uncommitted-changes";
			        HttpRequest request = HttpRequest.newBuilder()
			                .uri(URI.create(url))
			                .header(HttpHeader.ACCEPT.asString(), "application/json")
			                .GET()
			                .build();
			        try {
			            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			            hasUncommitted = Boolean.parseBoolean(response.body());
			        }
			        catch(IOException | InterruptedException e) {
			        	
			        }
			        
			        if (hasUncommitted) {
			            System.out.println("Uncommitted changes found → committing + pushing first");
			            try {
			            URL pushUrl = new URL("http://localhost:8080/api/v1/proxy-manager/workspaces/" + workspaceId + "/push");
			            HttpURLConnection pushConn = (HttpURLConnection) pushUrl.openConnection();
			            pushConn.setRequestMethod("POST");
			            pushConn.setDoOutput(true);
			            int code = pushConn.getResponseCode();
			            System.out.println("Push endpoint returned " + code);
			            pushConn.disconnect();
			            }
			            catch(IOException e) {
				        	
				        }
			        }

			    } else if (uri.contains("service=git-receive-pack")) {
			        System.out.println("Push detected: " + uri);
			        boolean hasUncommitted=true;
			        String workspaceId = "06f10733-c0a0-450e-89ae-de7fa0dbc424";
			        try {
			        URL url = new URL("http://localhost:8080/api/v1/proxy-manager/workspaces/" + workspaceId + "/uncommitted-changes");
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			        conn.setRequestMethod("GET");

			        int responseCode = conn.getResponseCode();
			        if (responseCode == 200) {
			            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			                String result = reader.readLine(); // "true" or "false"
			                hasUncommitted = Boolean.parseBoolean(result);
			                System.out.println("Uncommitted changes? " + hasUncommitted);
			            }
			        }
			        }
			        catch(IOException e) {
			        	
			        }
			        if (hasUncommitted) {
			            System.out.println("Uncommitted changes found → committing + pushing first");
			            try {
			            URL pushUrl = new URL("http://localhost:8080/api/v1/proxy-manager/workspaces/" + workspaceId + "/push");
			            HttpURLConnection pushConn = (HttpURLConnection) pushUrl.openConnection();
			            pushConn.setRequestMethod("POST");
			            pushConn.setDoOutput(true);
			            int code = pushConn.getResponseCode();
			            System.out.println("Push endpoint returned " + code);
			            pushConn.disconnect();
			            }
			            catch(IOException e) {
				        	
				        }
			           //Reject the push from git user

		                return null;
			        }
			        else {
			        	System.out.println("No uncommitted changes found → updating sirius web");
			           
			        }
			    }

			    return super.rewriteHttpURI(clientToProxyRequest);
			}
		};

		
		handler.setProxyToServerHost("github.com");
		server.setHandler(handler);
		server.start();
	}
}