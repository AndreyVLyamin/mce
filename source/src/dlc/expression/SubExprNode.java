package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Оператор Скобки */
public class SubExprNode extends OpNode{

	/** INode.PRIOR_VAL */
    public int priority = INode.PRIOR_VAL;
	
	private boolean m_bNeg = false;

    public SubExprNode( CodeToken source, boolean bNeg ){
        super( source );
		m_bNeg = bNeg;
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null ){
            System.out.println("ERROR: SubExprNode.proceed() - left is null!");
            throw new CodeRTException( ERROR_OPERAND, getSourceToken() );
        }


		Object val = left.proceed( vars );
		if( val instanceof Integer )
			val = new Integer( m_bNeg ? -((Integer)val).intValue() : ((Integer)val).intValue() );
		else if( val instanceof Double )
			val = new Double( m_bNeg ? -((Double)val).doubleValue() : ((Double)val).doubleValue() );
		else if( m_bNeg )
			throw new CodeRTException( ERROR_CANTNEGATIVE, getSourceToken() );

        return val;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}