package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * Оператор суммирования
 */
public class AddNode extends OpNode{

	/** INode.PRIOR_ADD */
    public int priority = INode.PRIOR_ADD;

    public AddNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: AddNode.proceed() - left or right is null!");
            System.out.println("\tleft: " + left + ", right: " + right );
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        if( v[0] instanceof String ){
            return "" + v[0] + v[1];
        }
        else if( v[0] instanceof Character ){
            if( v[1] instanceof Integer || v[1] instanceof Double ){
                int ival = (int)Double.parseDouble("" + v[1]);
                return new Character( (char)(((Character)v[0]).charValue() + (char)ival) );
            }
            else if( v[1] instanceof Character ){
                char cval = ((Character)v[1]).charValue();
                return new Character( (char)( ((Character)v[0]).charValue() + cval ) );
            }
        }
        else if( v[0] instanceof Integer || v[0] instanceof Double ){
            if( v[1] instanceof Character ){
                return new Double(
                            Double.parseDouble("" + v[0]) + Double.parseDouble("" + (int)((Character)v[1]).charValue() ) );
            }
            return new Double(
                        Double.parseDouble("" + v[0]) + Double.parseDouble("" + v[1]) );
        }
        System.out.println("ERROR: AddNode.proceed() - unknown type of nodes: " + v[0].getClass().getName() );
        throw new CodeRTException( ERROR_NEEDNUMBERORSTRING, getSourceToken() );
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
