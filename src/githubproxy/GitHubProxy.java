package githubproxy;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.proxy.ProxyHandler;
import org.eclipse.jetty.proxy.ProxyHandler.Forward;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

public class GitHubProxy {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		Forward handler = new ProxyHandler.Forward() {
			@Override
			protected HttpURI rewriteHttpURI(Request clientToProxyRequest) {
				System.out.println("rewriteHttpURI: " + clientToProxyRequest);
				
//				try { Thread.sleep(10000); } catch (InterruptedException e) {}
				
				return super.rewriteHttpURI(clientToProxyRequest);
			}
		};
		
		handler.setProxyToServerHost("github.com");
		server.setHandler(handler);
		server.start();
	}
}