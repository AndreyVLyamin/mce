package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Вызов функции
 */
public class FunctionNode extends SubExprNode{

	/** INode.PRIOR_VAL */
    public int priority = INode.PRIOR_VAL;

	/** Неопределенная функция */
	public static final int TYPE_UNDEF = -1;
	/** Функция SQRT */
	public static final int TYPE_SQRT = 0;
	/** Функция SIN */
	public static final int TYPE_SIN = 1;
	/** Функция COS */
	public static final int TYPE_COS = 2;
	/** Функция TAN */
	public static final int TYPE_TAN = 3;
	/** Функция ASIN */
	public static final int TYPE_ASIN = 4;
	/** Функция ACOS */
	public static final int TYPE_ACOS = 5;
	/** Функция ATAN */
	public static final int TYPE_ATAN = 6;
	/** Функция ROUND */
	public static final int TYPE_ROUND = 7;

	int m_function = TYPE_UNDEF;
	boolean m_bNeg = false;

    public FunctionNode( int function, CodeToken source, boolean bNeg ){
        super( source, false );
		m_function = function;
		m_bNeg = bNeg;
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null ){
            System.out.println("ERROR: FunctionNode.proceed() - left is null!");
            throw new CodeRTException( ERROR_OPERAND, getSourceToken() );
        }

        Object res = left.proceed( vars );
		Object val = null;
		double dVal = 0.0;

    	try{
    		dVal = Double.parseDouble( "" + res );
    	}catch( Exception exc ){
            System.out.println("ERROR: FunctionNode.proceed() - unknown type of node (need Int or Double): " + left.getClass().getName() );
            throw new CodeRTException( ERROR_NEEDNUMBER, getSourceToken() );
    	}

		try{
    		switch( m_function ){
    			case TYPE_SQRT:
    	   			dVal = Math.sqrt( dVal );
    				break;
    			case TYPE_SIN:
    				dVal = Math.sin( dVal );
    				break;
    			case TYPE_COS:
    				dVal = Math.cos( dVal );
    				break;
    			case TYPE_TAN:
    				dVal = Math.tan( dVal );
    				break;
    			case TYPE_ASIN:
    				dVal = Math.asin( dVal );
    				break;
    			case TYPE_ACOS:
    				dVal = Math.acos( dVal );
    				break;
    			case TYPE_ATAN:
    				dVal = Math.atan( dVal );
    				break;
    			case TYPE_ROUND:
    				dVal = Math.round( dVal );
    				break;
    		}
			if( Double.isNaN( dVal ) ) throw new Exception();

		}catch( Exception exc ){
			throw new CodeRTException( ERROR_INVALIDFUNCARG, getSourceToken() );
		}

		if( Math.abs( ((double)Math.round(dVal)) - dVal ) > 0.0 )
			val = new Double( m_bNeg ? -dVal : dVal );
		else
			val = new Integer( m_bNeg ? -(int)dVal : (int)dVal );

		return val;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
