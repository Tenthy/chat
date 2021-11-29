package server;

import constants.Constants;

import java.io.*;
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
    private String login;
    private String password;
    private final int MAX_LINES_IN_HISTORYLOG = 100;

    private File file;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader = null;

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
            login = tokens[0];
            password = tokens[1];
            String tempNickname = server.getAuthService().getNickByLoginAndPass(login, password);
            if (tempNickname != null) {
                nickname = tempNickname;

                /**
                 * Запись истории чата в тексовой файл history_[login].txt
                 */
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter("history_" + login + ".txt", true));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                /**
                 * Вывод истории чата из текстового файла
                 */
                try {
                    bufferedReader = new BufferedReader(new FileReader("history_" + login + ".txt"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        outputStream.writeUTF(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bufferedReader.close();

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
        /**
         * Запись истории чата в тексовой файл history_[login].txt
         */
        try {
            bufferedWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
             * Для того чтобы сменить никнейм
             */
            if (messageFromClient.startsWith(Constants.SET_NICKNAME_COMMAND)) {
                String[] tokens = messageFromClient.split("\\s+");
                nickname = server.getAuthService().setNickname(tokens[1], nickname, login, password);
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
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

