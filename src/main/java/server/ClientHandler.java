package server;

import constants.Constants;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private final int MAX_LINES_ON_HISTORYLOG = 100;

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
            executorService.execute(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
        } catch (IOException ioe) {
            throw new RuntimeException("Проблемы при создании обработчика");
        } finally {
            executorService.shutdown();
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

                file = new File("history_" + login + ".txt");
                try {
                    changeHistoryLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /**
                 * Запись истории чата в тексовой файл history_[login].txt
                 */
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                /**
                 * Вывод истории чата из текстового файла
                 */
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
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

    /**
     * Для перезаписи истории чата с ограничением в 100 строк
     * @throws Exception
     */
    public void changeHistoryLog() throws Exception {
        List<String> listHistoryLog = new ArrayList<>();
        Files.lines(Paths.get(String.valueOf(file))).forEach(listHistoryLog::add);
        System.out.println(listHistoryLog.size());
        if (listHistoryLog.size() >= MAX_LINES_ON_HISTORYLOG) {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("");
            for (int i = listHistoryLog.size() - MAX_LINES_ON_HISTORYLOG; i < listHistoryLog.size(); i++) {
                bufferedWriter.write(listHistoryLog.get(i) + "\n");
            }
            bufferedWriter.close();
        }
    }

    public void sendMessage(String message) {
        /**
         * Запись истории чата в тексовой файл history_[login].txt
         */
        try {
            bufferedWriter.write(message + "\n");
        } catch (Exception e) {
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

