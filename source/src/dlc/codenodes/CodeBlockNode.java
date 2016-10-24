package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.code.CodeRTException;

import java.util.Vector;
import java.util.Hashtable;

/**
 * ���� ���������
 */
public class CodeBlockNode extends CodeNode{

    Vector codeNodes = new Vector();

	/**
	 * �����������
	 * @param source �������� ���� ����
	 */
    public CodeBlockNode( CodeToken source ){
        super( source );
    }
	/** ����������� ����������� */
    public CodeBlockNode( CodeBlockNode src ){
        this( src.getSourceToken() );
        codeNodes = new Vector( src.getNodes() );
    }

	/** ���������� ����� ���� */
    public void addNode( CodeNode node ){
        codeNodes.addElement( node );
    }
	/** �������� ������ ���� ������ ���� (���� CodeToken) */
    public Vector getNodes(){
        return codeNodes;
    }

	/** ����� ��� ����������. �������� ���������� */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

	/** ����������� ��� ��������� � ����� ��������� � ������ � ���������� ��� */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        for( int i = 0; i < codeNodes.size(); i++ ){
            CodeNode cn = (CodeNode)codeNodes.elementAt(i);
            res.addAll( ((CodeNode)codeNodes.elementAt(i)).getLinearCode() );
        }
        return res;
    }

	/** ���������� ��������� ������������� ���� ��������� ������� ����� */
    public String toString(){
        return "" + codeNodes;
    }
}
