package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Оператор умножения */
public class MulNode extends OpNode{

	/** INode.PRIOR_MUL */
    public int priority = INode.PRIOR_MUL;

    public MulNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: MulNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        Double res = null;

        try{
            res = new Double( Double.parseDouble("" + v[0]) * Double.parseDouble("" + v[1]) );
        }catch( Exception exc ){
            System.out.println("ERROR: MulNode.proceed() - unknown type of nodes: " + v[0].getClass().getName() );
            throw new CodeRTException( ERROR_NEEDNUMBER, getSourceToken() );
        }
        return res;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
