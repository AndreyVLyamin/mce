package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Оператор Вычитание */
public class SubNode extends OpNode{

	/** INode.PRIOR_ADD */
    public int priority = INode.PRIOR_ADD;

    public SubNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: SubNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        try{
            Double []_v = new Double[2];
            if( v[0] instanceof Character )
                _v[0] = new Double( (double)((Character)v[0]).charValue() );
            else
                _v[0] = new Double( "" + v[0] );
            if( v[1] instanceof Character )
                _v[1] = new Double( (double)((Character)v[1]).charValue() );
            else
                _v[1] = new Double( "" + v[1] );

            return new Double( new Double( "" + _v[0] ).doubleValue() - new Double( "" +  _v[1] ).doubleValue() );
        }catch( Exception exc ){
            System.out.println("ERROR: SubNode.proceed() - unknown type of nodes: " + v[0].getClass().getName() );
            throw new CodeRTException( ERROR_NEEDNUMBER, getSourceToken() );
        }
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
