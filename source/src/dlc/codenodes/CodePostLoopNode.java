package dlc.codenodes;

import dlc.expression.INode;
import dlc.expression.LogNotNode;
import dlc.code.CodeToken;
import dlc.code.CodeRTException;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ���� ����� � ����-��������
 */
public class CodePostLoopNode extends CodeNode{
    INode         expression;
    CodeBlockNode innerBlock;

	/** �����������
	 * @param source �������� ���� ����
	 * @param expression ��������� ��������� �����
	 * @param innerBlock ���� ��������� � �����
	 * @param bInverse ������� ��������� ��������� ����� ���������� �����
	 */
    public CodePostLoopNode( CodeToken source, INode expression, CodeBlockNode innerBlock, boolean bInverse ){
        super( source );
        this.expression = expression;
        this.innerBlock = innerBlock;

        if( bInverse ){
            LogNotNode lnn = new LogNotNode( expression.getSourceToken() );
            lnn.addChild( expression );
            this.expression = lnn;
        }
    }

    /**
     * �� ������������
     */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

    /**
     * ����� ��� ��������� ���������� ����� ���� ��� ������, ������������ � ���� ����������
     * @return ������ ���������� 
     */
    public Vector getLinearCode() throws Exception{
        Vector sub = innerBlock.getLinearCode();

        CodeBlockNode innerJumpBlock = new CodeBlockNode( getSourceToken() );
        CodeJumpNode  innerJumpNode = new CodeJumpNode( getSourceToken(), -2 - sub.size() );
        innerJumpBlock.addNode( innerJumpNode );
        CodeIfNode    postIfNode = new CodeIfNode( getSourceToken(), expression, innerJumpBlock );

        Vector        condNode = postIfNode.getLinearCode();

        Vector res = new Vector();
        res.addAll( sub );
        res.addAll( condNode );

        return res;
    }
}
