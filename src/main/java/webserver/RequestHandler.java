package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.controller.HttpRequestController;
import http.controller.LoginController;
import http.controller.ResourceController;
import http.controller.UserCreateController;
import http.controller.UserListController;
import http.exceptions.FailToHandleRequest;
import http.request.HttpRequest;
import http.request.HttpRequestMapping;
import http.request.HttpRequestParser;
import http.response.HttpResponse;
import http.response.ResponseEntity;
import http.session.HttpSessionManager;
import http.session.RandomGenerateStrategy;
import http.session.SessionManager;
import view.HandleBarViewResolver;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HttpRequestController httpRequestController;
    private final SessionManager sessionManager;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.httpRequestController = setHttpRequestController();
        this.sessionManager = new HttpSessionManager(new RandomGenerateStrategy());
    }

    private HttpRequestController setHttpRequestController() {
        HttpRequestController httpRequestController = new HttpRequestController(
            new ResourceController(HttpRequestMapping.GET("/")));
        UserCreateController userCreateController = new UserCreateController(HttpRequestMapping.POST("/user/create"));
        LoginController loginController = new LoginController(HttpRequestMapping.POST("/user/login"));
        UserListController userListController = new UserListController(HttpRequestMapping.GET("/user/list"));

        httpRequestController.addController(userCreateController);
        httpRequestController.addController(loginController);
        httpRequestController.addController(userListController);
        return httpRequestController;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            handleRequest(in, out);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new FailToHandleRequest(e.getMessage());
        }
    }

    private void handleRequest(InputStream in, OutputStream out) {
        try {
            HttpRequest httpRequest = HttpRequestParser.parse(in);
            httpRequest.setSessionManager(this.sessionManager);
            HttpResponse httpResponse = HttpResponse.from(out);
            httpRequestController.doService(httpRequest, httpResponse);
            new ResponseEntity(new HandleBarViewResolver()).build(httpResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            sendError(out);
        }
    }

    private void sendError(OutputStream out) {
        HttpResponse httpResponse = HttpResponse.from(out);
        httpResponse.error();
        new ResponseEntity(new HandleBarViewResolver()).build(httpResponse);
    }
}
