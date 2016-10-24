package AbsCodeServer;

import java.util.Vector;
import java.util.Hashtable;

/**
 * $Id: Config.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�.
 * <br/>
 * ����� ��� �������� ������ � ������������.
 */
public class Config {
    private Vector m_userInfo = new Vector();
    private int m_Port;

    private Hashtable m_timeouts = new Hashtable();

	/** ��������� ���������� ������� ������ ������������� ������ ��������� (�������� - ������ UserInfo) */
    public void setUserInfo(Vector userInfo) {
        m_userInfo = userInfo;
    }

	/** ��������� ������ ������� ������� ������������� (�������� ������ - ������ UserInfo) */
    public Vector getUserInfo() {
        return m_userInfo;
    }

	/** ��������� TCP ����� */
    public void setPort(int port) {
        m_Port = port;
    }

	/** ��������� TCP ����� */
    public int getPort() {
        return m_Port;
    }

	/** ��������� ����������� �� ������� � �������� ������, ������������, ��������, ��� ��������������� ����������� ������� �� ���������� */
    public void addTimeout( String name, long value ){
        m_timeouts.put( name, new Long(value) );
    }
	/** ��������� ���-������� (key=���, value=�����������_��_�������) � ������������� �� ������� � ��������� �������. ������������, ��������, ��� ��������������� ����������� ������� �� ���������� */
    public Hashtable getTimeouts(){
        return m_timeouts;
    }
}