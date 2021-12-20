package server;

import constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {

    public static final Logger logger = LogManager.getLogger(EchoServer.class);
    private AuthService authService;
    private List<ClientHandler> clients;

    public AuthService getAuthService() {
        return authService;
    }

    public EchoServer() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            authService = new SQLBaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            logger.info("Server is running.");
            while (true) {
                //System.out.println("Сервер ожидает подключения...");
                logger.info("Server is waiting for the client.");
                Socket socket = serverSocket.accept();
                //System.out.println("Клиент подключился");
                logger.info("Client is connected.");
                new ClientHandler(this, socket);
            }
        } catch (IOException ioe) {
            //System.out.println("Ошибка в работе сервера");
            logger.error("Server error.");
            ioe.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    /**
     * Логика персональной отправки сообщений участнику чата
     */
    public synchronized void personalSendMessage(String nickname, String message) {
        clients.stream()
                .filter(client -> client.getNickname().equals(nickname))
                .forEach(client -> client.sendMessage(message));
    }

    public synchronized void broadcastMessage(String message) {
        clients.forEach(client -> client.sendMessage(message));
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
        logger.info(client.getNickname() + " is connected to chat");
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
        logger.info(client.getNickname() + " is disconnected from chat");
    }
}
