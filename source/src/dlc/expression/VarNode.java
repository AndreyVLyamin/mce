package dlc.expression;

import dlc.codenodes.VarObject;
import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Узел с переменной */
public class VarNode extends INode{

	/** INode.PRIOR_VAL */
    public int priority = INode.PRIOR_VAL;
    boolean    bNegative = false;

    public VarNode( CodeToken source, INode parent, String name, boolean bNegative ){
        super( source, parent );
        this.val = name;
        this.bNegative = bNegative;
    }
/*
    public VarNode( CodeToken source, String name ){
        super( source, null );
        this.val = name;
    }
*/
    public Object proceed( VariableContainer vars ) throws Exception{
        Object res = null;
        try{
            Variable var = ((Variable)vars.get( ((String)val).toLowerCase() ) );
            //res = ((VarObject)vars.get( ((String)val).toLowerCase()) ).value;
            res = var.value;

            if( bNegative ){
                switch( var.iType ){
                    case Variable.TYPE_INT:
                        return new Integer( -((Integer)res).intValue() );
                    case Variable.TYPE_DEC:
                        return new Double( -((Double)res).doubleValue() );
                    default:
                        throw new CodeRTException( ERROR_CANTNEGATIVE, getSourceToken() );
                }
            }

        }catch( Exception e ){
            throw new CodeRTException( ERROR_NOVAR, getSourceToken() );
        }
        return res;
    }
    public boolean addChild( INode node ){
        return false;
    }
    public INode getLeft(){
        return null;
    }
    public INode getRight(){
        return null;
    }
    public boolean replaceRight( INode newNode ){
        return false;
    }
    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}
