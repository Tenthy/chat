package server;

/**
 * Сервис аутентификации
 */
public interface AuthService {

    /**
     * Запустить сервис
     */
    void start();

    /**
     * Отключить сервис
     */
    void stop();

    /**
     * Получить никнейм по логину/паролю
     *
     * @param login
     * @param password
     * @return никнейм если найден или null, если такого нет
     */
    String getNickByLoginAndPass(String login, String password);

    /**
     * Сменить никнейм
     *
     * @param password
     * @param login
     * @param oldNickname
     * @param newNickname
     */
    String setNickname(String newNickname, String oldNickname, String login, String password);
}
