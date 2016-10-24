package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Оператор деления
 */
public class DivNode extends OpNode{

	/** INode.PRIOR_MUL */
    public int priority = INode.PRIOR_MUL;

    public DivNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: DivNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        Double res = null;

        try{
            res = new Double(
                Double.parseDouble( v[0].toString() ) / Double.parseDouble( v[1].toString() )
            );
            if( Double.isNaN(res.doubleValue()) || Double.isInfinite(res.doubleValue()) ){
                throw new CodeRTException( ERROR_DIVBYZERO, getSourceToken() );
            }
        }catch( CodeRTException crte ){
            throw crte;
        }catch( Exception exc ){
            throw new CodeRTException( ERROR_NEEDNUMBER, getSourceToken() );
        }
        return res;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
