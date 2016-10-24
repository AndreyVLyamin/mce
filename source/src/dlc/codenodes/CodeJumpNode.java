package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.code.CodeRTException;
import dlc.util.VariableContainer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ���� ������������ ��������
 */
public class CodeJumpNode extends CodeNode{
	/** ������������� ����� ��� �������� */
    public int m_relativeAddress = 1;

	/**
	 * �����������
	 * @param source �������� ���� ����
	 * @param relativeAddress ������������� �����
	 */
    public CodeJumpNode( CodeToken source, int relativeAddress ){
        super( source );
        m_relativeAddress = relativeAddress;
    }

	/** ����� ��� ���������� ������� ����� ����
	 * @param vars ���������� ���������
	 * @param runtime �������� ����� ����������
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{
        runtime.IP += m_relativeAddress;
    }

	/** ����� ���������� ������� ����� ���� (�� ����������, �������� ����������)
	 * @param vars ����������
	 */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

	/** ����������� ����� ���������� � ��������� ���� */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        res.addElement( this );
        return res;
    }
}
