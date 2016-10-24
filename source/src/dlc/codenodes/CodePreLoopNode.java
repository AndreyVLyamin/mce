package dlc.codenodes;

import dlc.expression.INode;
import dlc.code.CodeToken;
import dlc.code.CodeRTException;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ���� ����� � ��������������� ��������
 */
public class CodePreLoopNode extends CodeNode{
    INode         expression;
    CodeBlockNode innerBlock;


	/** �����������
	 * @param source �������� ���� ����
	 * @param expression ��������� ��������� �����
	 * @param innerBlock ���� ��������� � �����
	 */
    public CodePreLoopNode( CodeToken source, INode expression, CodeBlockNode innerBlock ){
        super( source );
        this.expression = expression;
        this.innerBlock = innerBlock;
    }

	/** �� ������������ */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

    /**
     * ����� ��� ��������� ���������� ����� ���� ��� ������, ������������ � ���� ����������
     * @return ������ ���������� 
     */
    public Vector getLinearCode() throws Exception{

        CodeBlockNode tmpInnerBlock = new CodeBlockNode( innerBlock );

        CodeJumpNode  preJump = new CodeJumpNode( getSourceToken(), -2 - tmpInnerBlock.getLinearCode().size() );
        tmpInnerBlock.addNode( preJump );
        CodeIfNode    preIf = new CodeIfNode( getSourceToken(), expression, tmpInnerBlock );
        Vector res = preIf.getLinearCode();

        return res;
    }
}
