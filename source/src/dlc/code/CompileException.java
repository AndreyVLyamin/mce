package dlc.code;

/**
 * $Id: CompileException.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, �������� ���������� � ����� ������������� ������ ����������.
 */
public class CompileException extends Exception{
    public int l, c, p;

    /**
     * �����������
     * @param msg ��������� �� ������
     * @param l ������
     * @param c �������
     * @param p ���������� ��������� ������� � ������
     */
    public CompileException( String msg, int l, int c, int p ){
        super(msg);
        this.l = l;
        this.c = c;
        this.p = p;
    }

	/**
	 * �����������
	 * @param msg ���������
	 * @param ct ���� ����, � ������� ��������� ����������
	 */
    public CompileException( String msg, CodeToken ct ){
        super(msg);
        this.l = ct.line;
        this.c = ct.column;
        this.p = ct.position;
    }
}
