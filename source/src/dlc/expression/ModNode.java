package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Оператор Остаток от Деления */
public class ModNode extends OpNode{

	/** INode.PRIOR_MUL */
    public int priority = INode.PRIOR_MUL;

    public ModNode( CodeToken source ){
        super( source );
    }

    public Object proceed( VariableContainer vars ) throws Exception{

        if( left == null || right == null ){
            System.out.println("ERROR: ModNode.proceed() - left or right is null!");
            throw new CodeRTException( ERROR_2OPERANDS, getSourceToken() );
        }

        Object []v = new Object[2];
        v[0] = left.proceed( vars );
        v[1] = right.proceed( vars );

        Double res = null;

        try{
            int v0 = 0, v1 = 0;
            if( v[0] instanceof Character )
                v0 = (int)((Character)v[0]).charValue();
            else
                v0 = (int)Double.parseDouble( v[0].toString() );
            if( v[1] instanceof Character )
                v1 = (int)((Character)v[1]).charValue();
            else
                v1 = (int)Double.parseDouble( v[1].toString() );

            try{
                res = new Double(
                    (double)(v0 % v1) );
                System.out.println( "Остаток от деления a = " + v0 + " на b = " + v1 + " : " + res );
            }catch( Exception exc ){
                throw new CodeRTException( ERROR_DIVBYZERO, getSourceToken() );
            }
        }catch( CodeRTException crte ){
            throw crte;
        }catch( Exception exc ){
            throw new CodeRTException( ERROR_NEEDINTFORMOD, getSourceToken() );
        }
        return res;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
