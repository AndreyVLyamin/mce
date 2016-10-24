package AbsCodeServer;

/**
 * $Id: UserInfo.java,v 1 2007/02/20
 * <br/>
 * Author: Бердников Е.В.
 * <br/>
 * Класс, содержащий информацию о пользователе,запросившем ресурсы сервера.
 */
public class UserInfo {

    private String m_login = "";
    private String m_password = "";

	/** Установить логин */
    public void setLogin(String login) {
        m_login = login;
    }

	/** Установить пароль */
    public void setPassword(String password) {
        m_password = password;
    }

	/** Получить логин */
    public String getLogin() {
        return m_login;
    }

	/** Получить пароль */
    public String getPassword() {
        return m_password;
    }
}
