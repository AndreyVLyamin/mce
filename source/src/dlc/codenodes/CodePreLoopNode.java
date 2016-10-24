package dlc.codenodes;

import dlc.expression.INode;
import dlc.code.CodeToken;
import dlc.code.CodeRTException;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Ѕлок цикла с предварительным условием
 */
public class CodePreLoopNode extends CodeNode{
    INode         expression;
    CodeBlockNode innerBlock;


	/**  онструктор
	 * @param source исходный блок кода
	 * @param expression выражение оператора цикла
	 * @param innerBlock блок выражений в цикле
	 */
    public CodePreLoopNode( CodeToken source, INode expression, CodeBlockNode innerBlock ){
        super( source );
        this.expression = expression;
        this.innerBlock = innerBlock;
    }

	/** Ќе используетс€ */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

    /**
     * ћетод дл€ получени€ лийнейного блока кода дл€ дерева, построенного в ходе компил€ции
     * @return список инструкций 
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
