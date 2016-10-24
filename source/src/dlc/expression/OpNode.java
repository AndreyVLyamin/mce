package dlc.expression;

import dlc.code.CodeToken;

/** Выражение с двумя операндами */
public abstract class OpNode extends INode{

    INode left, right;
	/** INode.PRIOR_ADD */
    public int priority = INode.PRIOR_ADD;

    public OpNode( CodeToken source ){
        super( source );
    }

    public boolean addChild( INode node ){
        if( left == null )
            left = node;
        else if( right == null )
            right = node;
        else
            return false;

        node.setParent( this );

        return true;
    }
    public INode getLeft(){
        return left;
    }
    public INode getRight(){
        return right;
    }
    public boolean replaceRight( INode newNode ){
        if( right == null ){
            left = newNode;
            newNode.setParent( this );
        }
        else{
            newNode.replaceRight( right );
            right = newNode;
            newNode.setParent( this );
        }
        return true;
    }

    public int getPriority(){ return priority; }
    public void setPriority( int p ){ priority = p; }
}

