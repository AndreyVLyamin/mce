package dlc.codenodes;

import java.util.*;

/**
 * �����-��������� ��� �������� ����������
 * ���-�������, ����������� �� ����� ���������� ���������
 * ������ �������� ���������� � ��������� ������ �������.
 * ��� ����������, ���� ���������� ���� � ����,
 * �������� �������� ������� �������,
 * ����� �������, ��������� �������� ���������� �� ���������� �����
 * ������ ��� � �� ������� �����.
 */
public class VarArrayObject extends VarObject{

	/** �����������
     * @param varName ��� ����������
     */
    public VarArrayObject( String varName ){
        super( varName, new Hashtable() );
    }

	/** ��������� �������� �� ������� (key) */
    public Object get( Object key ) throws Exception{
        return ((Hashtable)value).get( key );
    }
	/** ��������� �������� ��� ������� (key) */
    public void set( Object key, Object val ) throws Exception{
        ((Hashtable)value).put( key, val );
    }
}
