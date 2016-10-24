package AbsCodeServer;

import java.util.Vector;
import java.util.Hashtable;

/**
 * $Id: Config.java,v 1 2007/02/20
 * <br/>
 * Author: Бердников Е.В.
 * <br/>
 * Класс для хранения данных о конфигурации.
 */
public class Config {
    private Vector m_userInfo = new Vector();
    private int m_Port;

    private Hashtable m_timeouts = new Hashtable();

	/** Установка параметров учетной записи пользователей данной установки (элементы - классы UserInfo) */
    public void setUserInfo(Vector userInfo) {
        m_userInfo = userInfo;
    }

	/** Получение списка учетных записей пользователей (элементы списка - классы UserInfo) */
    public Vector getUserInfo() {
        return m_userInfo;
    }

	/** Установка TCP порта */
    public void setPort(int port) {
        m_Port = port;
    }

	/** Получение TCP порта */
    public int getPort() {
        return m_Port;
    }

	/** Установка ограничения по времени с заданным именем, используется, например, для дополнительного ограничения времени на компиляцию */
    public void addTimeout( String name, long value ){
        m_timeouts.put( name, new Long(value) );
    }
	/** Получение хэш-таблицы (key=имя, value=ограничение_по_времени) с ограничениями по времени с заданными именами. Используется, например, для дополнительного ограничения времени на компиляцию */
    public Hashtable getTimeouts(){
        return m_timeouts;
    }
}