# GitHub Proxy Demo

This is a demonstration of using a Jetty-based proxy to intercept pull/fetch etc. events for an existing GitHub repository. To run the demo:

- Run `GitHubProxy.java`
- In your terminal run `git clone http://localhost:8080/eclipse-epsilon/epsilon epsilon-githubproxy`
- This will clone the `https://github.com/eclipse-epsilon/epsilon` repository through the proxy in a local `epsilon-githubproxy` directory
- Go into the cloned `epsilon-githubproxy` directory and run `git fetch`
- Note how the fetch command is intercepted in your Java console as shown below

```
rewriteHttpURI: GET@0 http://localhost:8080/eclipse-epsilon/epsilon/info/refs?service=git-upload-pack HTTP/1.1
rewriteHttpURI: GET@0 http://github.com/eclipse-epsilon/epsilon/info/refs?service=git-upload-pack HTTP/1.1
```
