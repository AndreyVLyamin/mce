package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Оператор Отрицания
 */
public class LogNotNode extends OpNode{

	/** INode.PRIOR_LOG_NOT */
    public int priority = INode.PRIOR_LOG_NOT;

    public LogNotNode( CodeToken source ){
        super( source );
    }

    public boolean addChild( INode node ){
        if( right == null )
            right = node;
        else
            return false;

        node.setParent( this );

        return true;
    }
    public boolean replaceRight( INode newNode ){
        if( right == null ){
            right = newNode;
            newNode.setParent( this );
        }
        else{
            newNode.replaceRight( right );
            right = newNode;
            newNode.setParent( this );
        }
        return true;
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( right == null ){
            System.out.println("ERROR: LogNotNode.proceed() - right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object v = right.proceed( vars );

        if( v instanceof Boolean ){
            return new Boolean(
                    !((Boolean)v).booleanValue() );
        }
        else{
            System.out.println("ERROR: LogNotNode.proceed() - unknown type of node: " + v.getClass().getName() );
            throw new CodeRTException( ERROR_NEEDBOOL, getSourceToken() );
        }
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
