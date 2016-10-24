package dlc.codenodes;

import dlc.expression.INode;
import dlc.code.CodeToken;
import dlc.code.CodeRTException;
import dlc.util.VariableContainer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ���� �������
 */
public class CodeIfNode extends CodeNode{
    INode         expression;
    CodeBlockNode innerBlock;
    CodeNode      elseBlock;

	/**
	 * �����������
	 * @param source �������� ���� ����
	 * @param expression �������� ��������
	 * @param innerBlock ���� ��������� � �����, ��������������� ���������� �������
	 * @param elseBlock ���� ��������� � �����, ���������������� ������������ �������
	 */
    public CodeIfNode( CodeToken source, INode expression, CodeBlockNode innerBlock, CodeNode elseBlock ){
        this( source, expression, innerBlock );
        this.elseBlock = elseBlock;
    }
	/**
	 * �����������
	 * @param source �������� ���� ����
	 * @param expression �������� ��������
	 * @param innerBlock ���� ��������� � �����, ��������������� ���������� �������
	 */
    public CodeIfNode( CodeToken source, INode expression, CodeBlockNode innerBlock ){
        super( source );
        this.expression = expression;
        this.innerBlock = innerBlock;
    }

	/** ����� ��� ���������� ������� ����� ����
	 * @param vars ���������� ���������
	 * @param runtime �������� ����� ����������
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{
        if( ((Boolean)expression.proceed( vars )).booleanValue() ){
            //goto next after JumpNode
            System.out.println( "goto next after JumpNode" );
            runtime.IP += 2;
        }
        else{
            //goto JumpNode
            System.out.println( "goto JumpNode" );
            runtime.IP++;
        }
    }

	/** ����� ���������� ������� ����� ���� (�� ����������, �������� ����������)
	 * @param vars ����������
	 */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

	/** ����������� ������������� ��������� ������ � ��������� ���� � ������������ JMP */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        Vector sub = innerBlock.getLinearCode();
        Vector elseSub = new Vector();

        if( elseBlock != null ){
            elseSub = elseBlock.getLinearCode();
            sub.addElement( new CodeJumpNode( getSourceToken(), elseSub.size() + 1 ) );
        }

        res.addElement( this );
        res.addElement( new CodeJumpNode( getSourceToken(), sub.size() + 1 ) );
        res.addAll( sub );
        if( elseSub.size() > 0 )
            res.addAll( elseSub );
        return res;
    }
}
