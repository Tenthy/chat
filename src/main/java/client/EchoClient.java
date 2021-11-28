package client;

import constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EchoClient extends JFrame {

    private JTextArea messageTextArea;
    private JTextField messageTextField;
    private JTextField loginTextField;
    private JTextField passwordTextField;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public EchoClient() {
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareUI();
    }

    private void openConnection() throws IOException {
        socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) {
                    String messageFromServer = dataInputStream.readUTF();
                    if (messageFromServer.equals("/end")) {
                        break;
                    }
                    messageTextArea.append(messageFromServer);
                    messageTextArea.append("\n");
                }
                messageTextArea.append("Соединение разорвано");
                messageTextField.setEnabled(false);
                closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void closeConnection() {
        try {
            dataOutputStream.close();
        } catch (Exception ex) {

        }
        try {
            dataInputStream.close();
        } catch (Exception ex) {

        }
        try {
            socket.close();
        } catch (Exception ex) {

        }
    }

    private void sendMessage() {
        if (messageTextField.getText().trim().isEmpty()) {
            return;
        }
        try {
            dataOutputStream.writeUTF(messageTextField.getText());
            messageTextField.setText("");
            messageTextField.grabFocus();
        } catch (Exception ex) {
            messageTextArea.append("Ошибка: Вы не авторизовались");
        }
    }

    private void prepareUI() {
        setBounds(200, 200, 500, 500);
        setTitle("EchoClient");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        add(contentPane);
        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        messageTextArea.setLineWrap(true);
        contentPane.add(new JScrollPane(messageTextArea), BorderLayout.CENTER);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(messagePanel, BorderLayout.SOUTH);
        messageTextField = new JTextField();
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        JButton sendButton = new JButton();
        sendButton.setText("Send");
        messagePanel.add(sendButton, BorderLayout.EAST);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(loginPanel, BorderLayout.NORTH);
        JPanel loginPasswordPanel = new JPanel();
        loginPasswordPanel.setLayout(new BorderLayout(0, 0));
        loginPanel.add(loginPasswordPanel, BorderLayout.CENTER);
        loginTextField = new JTextField();
        loginPasswordPanel.add(loginTextField, BorderLayout.NORTH);
        passwordTextField = new JTextField();
        loginPasswordPanel.add(passwordTextField, BorderLayout.SOUTH);
        JPanel loginButtonPanel = new JPanel();
        loginButtonPanel.setLayout(new BorderLayout(0, 0));
        loginPanel.add(loginButtonPanel, BorderLayout.EAST);
        JButton loginButton = new JButton();
        loginButton.setText("Login");
        loginButtonPanel.add(loginButton, BorderLayout.NORTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataOutputStream.writeUTF(loginTextField.getText() + " " + passwordTextField.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messageTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EchoClient::new);
    }
}
