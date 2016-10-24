package dlc.code;

/**
 * $Id: CodeRTException.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� Exception ��� ��������� ���������� ���������� ������� ����������.
 */

public class CodeRTException extends Exception{

    public CodeToken m_source;

    /**
     * �����������
     * @param msg ��������� �� ������
     * @param source ����� ������������� ������
     */
    public CodeRTException( String msg, CodeToken source ){
        super(msg);
        m_source = source;
    }
/*
    public CodeRTException( String msg, int a, int b, int c ){
        super(msg);
        System.out.println("WARNING: using of invalid CodeRTException!, Use 2-arg ctor!!!" );
        m_source = new CodeToken( 0, 0, 0, "" );
    }
*/

    /**
     * ����� ��� ��������� ���������� � �����, ��� �������� ������ ������� ����������
     * @return ��������� CodeToken
     */
    public CodeToken getSourceToken(){
        return m_source;
    }
}
