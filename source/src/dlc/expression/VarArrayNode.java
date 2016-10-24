package dlc.expression;

import dlc.codenodes.*;
import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Элемент массива в качестве оператора */
public class VarArrayNode extends VarNode{

    INode  m_indexExpression;

    public VarArrayNode( CodeToken source, INode parent, String name, INode indexExpr, boolean bNegative ){
        super( source, parent, name, bNegative );
        this.val = name;
        this.m_indexExpression = indexExpr;
        this.bNegative = bNegative;
    }
/*
    public VarArrayNode( CodeToken source, String name, INode indexExpr ){
        super( source, name );
        this.val = name;
        this.m_indexExpression = indexExpr;
    }
*/
    public Object proceed( VariableContainer vars ) throws Exception{
        Object res = null;
        Object oIndex = m_indexExpression.proceed( vars );

        int index = -1;
        try{
            Integer iIndex = new Integer( oIndex.toString() );
            index = iIndex.intValue();
        }catch( Exception exc ){
            //throw new CodeRTException( ERROR_NEEDINTINDEX, getSourceToken() );
            try{
                index = (int)((Double)oIndex).doubleValue();
            }catch( Exception exc2 ){
                throw new CodeRTException( ERROR_NEEDINTINDEX, getSourceToken() );
            }
        }

        VariableArray array = null;
        try{
            array = (VariableArray)vars.get( ((String)val).toLowerCase() );
        }catch( Exception e ){
            throw new CodeRTException( ERROR_NEEDARRAY, getSourceToken() );
        }
        try{
            res = array.elementAt( index );
            if( res == null ) throw new NullPointerException();
        }catch( Exception e ){
            throw new CodeRTException( ERROR_OUTOFRANGE, getSourceToken() );
        }

        if( bNegative ){
            switch( array.iType ){
                case Variable.TYPE_ARR_INT:
                    return new Integer( -((Integer)res).intValue() );
                case Variable.TYPE_ARR_DEC:
                    return new Double( -((Double)res).doubleValue() );
                default:
                    throw new CodeRTException( ERROR_CANTNEGATIVE, getSourceToken() );
            }
        }

        return res;
    }
}
