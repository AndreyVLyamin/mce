package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Оператор Логического ИЛИ */
public class LogOrNode extends OpNode{

	/** INode.PRIOR_LOG */
    public int priority = INode.PRIOR_LOG;

    public LogOrNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: LogOrNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];

        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        if( v[0] instanceof Boolean ){
            return new Boolean(
                    ((Boolean)v[0]).booleanValue() || ((Boolean)v[1]).booleanValue() );
        }
        else{
            System.out.println("ERROR: LogOrNode.proceed() - unknown type of nodes: " + v[0].getClass().getName() );
            throw new CodeRTException( ERROR_NEEDBOOL, getSourceToken() );
        }
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
