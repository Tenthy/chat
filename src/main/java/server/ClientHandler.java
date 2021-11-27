package server;

import constants.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик для конкретного клиента
 */
public class ClientHandler {

    private EchoServer server;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public ClientHandler(EchoServer server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException ioe) {
            throw new RuntimeException("Проблемы при создании обработчика");
        }
    }

    private void authentication() throws IOException {
        while (true) {
            String loginAndPassword = inputStream.readUTF();
            String[] tokens = loginAndPassword.split("\\s+");
            String tempNickname = server.getAuthService().getNickByLoginAndPass(tokens[0], tokens[1]);
            if (tempNickname != null) {
                nickname = tempNickname;
                sendMessage("Вы вошли в общий чат");
                server.broadcastMessage(nickname + " вошёл в чат");
                server.subscribe(this);
                return;
            } else {
                sendMessage("Неверный логин или пароль");
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String messageFromClient = inputStream.readUTF();
            System.out.println("Сообщение от " + nickname + ": " + messageFromClient);

            /**
             * Для того чтобы закрыть чат
             */
            if (messageFromClient.equals(Constants.END_COMMAND)) {
                break;
            }

            /**
             * Логика отправки персонального сообщения участнику чата
             */
            if (messageFromClient.startsWith(Constants.W_COMMAND)) {
                String[] tokens = messageFromClient.split("\\s+");
                List<String> list = new ArrayList<>();
                for (int i = 2; i < tokens.length; i++) {
                    list.add(tokens[i]);
                }
                String forNickname = tokens[1];
                String message = String.join(" ", list);
                server.personalSendMessage(forNickname, "Лично от " + nickname + ": " + message);
                outputStream.writeUTF("Лично для " + forNickname + ": " + message);
            } else {
                server.broadcastMessage(nickname + ": " + messageFromClient);
            }
        }
    }

    private void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(nickname + " вышел из чата");
        try {
            inputStream.close();
        } catch (IOException ex) {
            //Ignore
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            //Ignore
        }
        try {
            socket.close();
        } catch (IOException ex) {
            //Ignore
        }
    }
}

