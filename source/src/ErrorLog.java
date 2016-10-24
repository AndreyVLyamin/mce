
/**
 * $Id: ErrorLog.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * ���������, ����������� ������ ��� ������ ��������� �� �������.
 */
public interface ErrorLog {
	/**
	 * �������� ��������� �� ������
	 */
    public void showError( String error );
	/**
	 * �������� ���������
	 */
    public void showMessage( String message );
	/**
	 * �������� ��������������
	 */
    public void showWarning( String message );
}
