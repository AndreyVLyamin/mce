package dlc.expression;

import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/** Операнд выражения */
public class ValNode extends INode{

	/** INode.PRIOR_VAL */
    public int priority = INode.PRIOR_VAL;

    public ValNode( CodeToken source, INode parent, Object val ){
        super( source, parent );
        this.val = val;
    }

    public ValNode( CodeToken source, Object val ){
        super( source, null );
        this.val = val;
    }

    public Object proceed( VariableContainer vars ) throws Exception{
        return val;
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
