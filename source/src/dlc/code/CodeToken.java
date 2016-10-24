package dlc.code;

/**
 * $Id: CodeToken.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * ����� ��� �������� ���������� � ��������� �������� � �������� ����.
 */
public class CodeToken{

	/** ��������� ��������� */
    public String t;
	/** ����� ������ */
    public int line;
	/** ����� ������� */
    public int column;
	/** ���������� ������� */
    public int position;

	/**
	 * �����������
	 * @param line ����� ������
	 * @param column ����� �������
     * @param position ���������� �������
	 * @param t ��������� ���������
	 */
    public CodeToken( int line, int column, int position, String t ){
        this.t = t;
        this.line = line;
        this.column = column;
        this.position = position;
    }

	/** �������������� � ���������� ����. ���������� t */
    public String toString(){
        return t;
    }
};
