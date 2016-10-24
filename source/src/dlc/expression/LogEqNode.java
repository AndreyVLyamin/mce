package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Оператор логического сравнения
 */
public class LogEqNode extends OpNode{

	/** INode.PRIOR_EQ */
    public int priority = INode.PRIOR_EQ;
    boolean    bNeq = false;

    public LogEqNode( CodeToken source ){
        super( source );
    }
    public LogEqNode( CodeToken source, boolean bNeq ){
        super( source );
        this.bNeq = bNeq;
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: LogEqNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        Object []_v = new Object[2];
        if( v[0] instanceof Character )
            _v[0] = new String( "" + v[0] );
        else
            _v[0] = new Double( "" + v[0] );
        if( v[1] instanceof Character )
            _v[1] = new String( "" + v[1] );
        else
            _v[1] = new Double( "" + v[1] );

        Boolean bRes = null;

        if( bNeq )
            bRes = new Boolean( !_v[0].equals( _v[1] ) );
        else
            bRes = new Boolean( _v[0].equals( _v[1] ) );

        System.out.println( "v[0] is " + v[0].getClass().getName() + " v[1] is " + v[1].getClass().getName() );
        System.out.println( "_v[0] = " + _v[0]+ " _v[1] = " + _v[1]+ " bNeq = " + bNeq + " res = " + bRes );
        return bRes;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
