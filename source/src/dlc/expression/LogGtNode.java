package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Оператор больше
 */
public class LogGtNode extends OpNode{

	/** INode.PRIOR_EQ */
    public int priority = INode.PRIOR_EQ;

    public LogGtNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: LogGtNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );
        Boolean res = null;

        try{
            Object []_v = new Object[2];
            if( v[0] instanceof Character )
                _v[0] = new Integer( (int)((Character)v[0]).charValue() );
            else
                _v[0] = new Double( "" + v[0] );
            if( v[1] instanceof Character )
                _v[1] = new Integer( (int)((Character)v[1]).charValue() );
            else
                _v[1] = new Double( "" + v[1] );

            res = new Boolean( Double.parseDouble("" + _v[0]) > Double.parseDouble("" + _v[1]) );
        }catch( Exception exc ){
            System.out.println("ERROR: LogGtNode.proceed() - unknown type of nodes: " + v[0].getClass().getName() );
            throw new CodeRTException( ERROR_NEEDNUMBER, getSourceToken() );
        }
        return res;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
