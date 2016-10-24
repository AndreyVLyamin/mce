package dlc.code;

import dlc.expression.*;
import dlc.util.TokenUtils;
import dlc.util.VariableContainer;
import dlc.template.*;

import java.util.*;

/**
 * $Id: ExprParser.java,v 1 2007/02/20
 * <br/>
 * Author: ¬ашенков ќ.≈.
 * <br/>
 *  ласс дл€ разбора выражений.
 */
public class ExprParser{

    /**
     * ƒобавление узлы со значением
     * @param ct информаци€ о положении первой лексемы в исходном коде
     * @param root корневой узел дерева выражени€
     * @param vars переменные программы
     * @param value значение
     */
    private INode addValNode( CodeToken ct, INode root, VariableContainer vars, Object value ) throws Exception{
        ValNode val = new ValNode( ct, root, value );

        if( root == null )
            return val;
        else if( !root.addChild( val ) ){
            throw new Exception( "INode.addChild() FAILED" );
        }
        return root;
    }

    /**
     * ƒобавление узла со ссылкой на переменную-массив
     * @param ct информаци€ о положении первой лексемы в исходном коде
     * @param root корневой узел дерева выражени€
     * @param vars переменные программы
     * @param varName название переменной
     * @param indexExpr выражение дл€ вычислени€ индекса в массиве
     * @param bNegative признак необходимости отрицани€
     */
    private INode addVarNode( CodeToken ct, INode root, VariableContainer vars, String varName, INode indexExpr, boolean bNegative ) throws Exception{
        if( !vars.containsKey(varName) ){
            //throw new NullPointerException("Unknwon variable " + varName );
            throw new CompileException( "ѕеременна€ не определена: " + varName, ct.line, ct.column, ct.position );
        }

        VarNode val = null;
        if( indexExpr != null )
            val = new VarArrayNode( ct, root, varName.toLowerCase(), indexExpr, bNegative );
        else
            val = new VarNode( ct, root, varName.toLowerCase(), bNegative );

        if( root == null )
            return val;
        else if( !root.addChild( val ) ){
            throw new Exception( "INode.addChild() FAILED" );
        }
        return root;
    }
    /**
     * ƒобавление узла со ссылкой на переменную
     * @param ct информаци€ о положении первой лексемы в исходном коде
     * @param root корневой узел дерева выражени€
     * @param vars переменные программы
     * @param varName название переменной
     * @param bNegative признак необходимости отрицани€
     */
    private INode addVarNode( CodeToken ct, INode root, VariableContainer vars, String varName, boolean bNegative ) throws Exception{
        return addVarNode( ct, root, vars, varName, null, bNegative );
    }

    /**
     * ћетод дл€ построени€ дерева выражени€
     * @param expTpl шаблоны выражений дл€ заданного стил€ кодировани€
     * @param vars переменные программы
     * @param tokens лексемы
     * @return корневой элемент дерева выражени€
     */ 
	public INode buildExprTree( ExpressionTemplate expTpl, VariableContainer vars, Vector tokens ) throws Exception{
		return buildExprTree( expTpl, vars, tokens, false );
	}

    /**
     * ћетод дл€ построени€ дерева выражени€
     * @param expTpl шаблоны выражений дл€ заданного стил€ кодировани€
     * @param vars переменные программы
     * @param tokens лексемы
     * @param bRootNegative признак отрицани€ этого подвыражени€
     * @return корневой элемент дерева выражени€
     */ 
    private INode buildExprTree( ExpressionTemplate expTpl, VariableContainer vars, Vector tokens, boolean bRootNegative ) throws Exception{

        VariableContainer _vars = new VariableContainer( vars );

        boolean bOp = false;
        String  []ops = expTpl.getTerms();
/*
		System.out.println("Build expr tree: " );
		for( int i = 0; i < ops.length; i++ ){
			System.out.print( ops[i] + ", " );
		}
		System.out.println();
*/

        String opSymbols = "+-/*^&|\\";

        INode node = null;
        boolean bNegative = false;

		String func = "";

        try{
            for( int i = 0; i < tokens.size(); i++ ){

                CodeToken ct = (CodeToken)tokens.elementAt(i);
                String t = ct.t;

                bOp = false;
                for( int j = 0; j < ops.length; j++ ){
                    if( t.equals( ops[j] ) ){
                        bOp = true;
                        break;
                    }
                }

				if( func.length() > 0 && !t.equals( expTpl.groupStart ) ){
					throw new CompileException( "¬ызов функции, ожидаетс€ символ: " + expTpl.groupStart, ct );
				}

                if( bOp ){

//System.out.println("OP-NODE: " + t );

                    INode newNode = null;
                    if( t.equals( expTpl.lt ) )
                        newNode = new LogLtNode( ct );
                    else if( t.equals( expTpl.gt ) )
                        newNode = new LogGtNode( ct );
                    else if( t.equals( expTpl.lte ) )
                        newNode = new LogLtEqNode( ct );
                    else if( t.equals( expTpl.gte) )
                        newNode = new LogGtEqNode( ct );

					else if( t.equalsIgnoreCase( expTpl.funcSqrt ) )
						func = expTpl.funcSqrt;
					else if( t.equalsIgnoreCase( expTpl.funcSin ) )
						func = expTpl.funcSin;
					else if( t.equalsIgnoreCase( expTpl.funcCos ) )
						func = expTpl.funcCos;
					else if( t.equalsIgnoreCase( expTpl.funcTan ) )
						func = expTpl.funcTan;
					else if( t.equalsIgnoreCase( expTpl.funcAsin) )
						func = expTpl.funcAsin;
					else if( t.equalsIgnoreCase( expTpl.funcAcos) )
						func = expTpl.funcAcos;
					else if( t.equalsIgnoreCase( expTpl.funcAtan ) )
						func = expTpl.funcAtan;
					else if( t.equalsIgnoreCase( expTpl.funcRound ) )
						func = expTpl.funcRound;

                    else if( t.equals( expTpl.add ) ){
                        newNode = new AddNode( ct );
                    }
                    else if( t.equals( expTpl.sub ) ){

                        boolean bPrevOp = true;
                        try{
                            OpNode con = (OpNode)node;
                            if( node instanceof SubExprNode )
                                bPrevOp = false;
                        }catch( Exception opCastException ){
                            bPrevOp = false;
                        }

                        if( node == null || (bPrevOp && node.getRight() == null) ){
                            bNegative = true;
                            newNode = null;
                        }
                        else
                            newNode = new SubNode( ct );

//System.out.println("Processing SUB-operation (are we set bNeg? - " + bNegative );
                    }
                    else if( t.equals( expTpl.mul ) ){
                        newNode = new MulNode( ct );
                    }
                    else if( t.equals( expTpl.div ) )
                        newNode = new DivNode( ct );
                    else if( t.equals( expTpl.mod ) )
                        newNode = new ModNode( ct );
                    else if( t.equals( expTpl.or ) ){
                        newNode = new LogOrNode( ct );
                    }
                    else if( t.equals( expTpl.and ) ){
                        newNode = new LogAndNode( ct );
                    }
                    else if( t.equals( expTpl.eq ) ){
                        newNode = new LogEqNode( ct );
                    }
                    else if( t.equals( expTpl.neq ) ){
                        newNode = new LogEqNode( ct, true );
                    }
                    else if( t.equalsIgnoreCase( expTpl.not ) ){
                        newNode = new LogNotNode( ct );
                    }
                    else if( t.equals( expTpl.groupStart ) ){
                        int []inVars = new int[]{ i, 0 };
                        Vector subtokens = TokenUtils.findSubtoken( tokens, inVars, expTpl.groupStart, expTpl.groupEnd );
                        i = inVars[1];
						if( func.length() == 0 )
	                        newNode = buildExprTree( expTpl, _vars, subtokens, bNegative );
						else{

	                        newNode = buildExprTree( expTpl, _vars, subtokens );

							int iFunc = FunctionNode.TYPE_UNDEF;
							if( func.equalsIgnoreCase( expTpl.funcSqrt ) )
								iFunc = FunctionNode.TYPE_SQRT;
							else if( func.equalsIgnoreCase( expTpl.funcSin ) )
								iFunc = FunctionNode.TYPE_SIN;
							else if( func.equalsIgnoreCase( expTpl.funcCos ) )
								iFunc = FunctionNode.TYPE_COS;
							else if( func.equalsIgnoreCase( expTpl.funcTan ) )
								iFunc = FunctionNode.TYPE_TAN;
							else if( func.equalsIgnoreCase( expTpl.funcAsin ) )
								iFunc = FunctionNode.TYPE_ASIN;
							else if( func.equalsIgnoreCase( expTpl.funcAcos ) )
								iFunc = FunctionNode.TYPE_ACOS;
							else if( func.equalsIgnoreCase( expTpl.funcAtan ) )
								iFunc = FunctionNode.TYPE_ATAN;
							else if( func.equalsIgnoreCase( expTpl.funcRound ) )
								iFunc = FunctionNode.TYPE_ROUND;

							INode funcNode = new FunctionNode( iFunc, ct, bNegative );
							funcNode.addChild( newNode );
							newNode = funcNode;
						}

						bNegative = false;
						func = "";
                    }
                    else if( t.startsWith( expTpl.singleQuote ) || t.startsWith( expTpl.doubleQuote ) ){
                        newNode = addValNode( ct, node, _vars, t );
                    }
                    else{
                        System.out.println( "ERROR: unsupported operation: " + t + "(" + t.charAt(0) + ")" );
                        //throw new NullPointerException();
                        throw new CompileException( "Ќеизвестна€ операци€: " + t, ct.line, ct.column, ct.position );
                    }
/*
                    if( newNode == null ){
                        bNegative = true;
                    }
					else 
*/
					if( newNode == null ){
					}
                    else if( node == null ){
                        node = newNode;
                    }
                    else if( newNode.getPriority() > node.getPriority() ){
                        if( !node.addChild( newNode ) )
                            node.replaceRight( newNode );
                        node = newNode;
                    }
                    else{ //newNode.priority <= current_or_parent.priority (for each parent)

                        INode itNode = node;
                        while( itNode.getParent() != null && newNode.getPriority() <= itNode.getPriority() )
                            itNode = itNode.getParent();

//System.out.println( itNode.getParent() + ".<<replaceChild>>(" + newNode + ")" );

                        if( newNode.getPriority() > itNode.getPriority() ){
                            itNode.replaceRight( newNode );
                        }
                        else{
                            newNode.setParent( itNode.getParent() );
                            if( !newNode.addChild( itNode ) ){
                                System.out.println("ERROR: >2 operands for operation (near \'" + t + "\')");
                                //throw new NullPointerException();
                                throw new CompileException( "2 последовательных операнда без операции", ct.line, ct.column, ct.position );
                            }
                        }
                        node = newNode;
                    }

/*
                    else if( newNode.getPriority() >= node.getPriority() ){
                        if( !node.addChild( newNode ) )
                            node.replaceRight( newNode );
                        node = newNode;
                    }
                    else{ //newNode.priority <= current_or_parent.priority (for each parent)

                        INode itNode = node;
                        while( itNode.getParent() != null && newNode.getPriority() <= itNode.getPriority() )
                            itNode = itNode.getParent();

//System.out.println( itNode.getParent() + ".<<replaceChild>>(" + newNode + ")" );

                        if( newNode.getPriority() >= itNode.getPriority() ){
                            itNode.replaceRight( newNode );
                        }
                        else{
                            newNode.setParent( itNode.getParent() );
                            if( !newNode.addChild( itNode ) ){
                                System.out.println("ERROR: >2 operands for operation (near \'" + t + "\')");
                                //throw new NullPointerException();
                                throw new CompileException( "2 последовательных операнда без операции", ct.line, ct.column, ct.position );
                            }
                        }
                        node = newNode;
                    }
*/
                }
                else{
                    //value-node

//System.out.println("ARG: " + t );

                    /**
                     * ѕроверка синтаксиса в именах переменных или в значени€х
                     */
					if( !t.startsWith("\"") && !t.startsWith("\'") ){
                        for( int j = 0; j < t.length(); j++ ){
                            for( int k = 0; k < opSymbols.length(); k++ ){
                                if( t.charAt(j) == opSymbols.charAt(k) ){
                                    throw new CompileException( "Ќеизвестна€ операци€: " + t, ct.line, ct.column, ct.position );
                                }
                            }
                        }
					}

                    Object val = null;
                    try{
                        if( expTpl.isQuote( "" + t.charAt(0) ) )
                            throw new Exception();
                        val = new Integer( (bNegative ? "-":"") + t );
                    }catch( Exception intExc ){
                        try{
                            if( expTpl.isQuote( "" + t.charAt(0) ) )
                                throw new Exception();

                            val = new Double( (bNegative ? "-":"") + t );
                        }catch( Exception numberException ){

                            if( bNegative ){
                                throw new CompileException( "ќпераци€ арифметического отрицани€ применима только к числовым значени€м", ct );
                            }

                            if( t.equalsIgnoreCase("true") || t.equalsIgnoreCase("false") ){
                                val = new Boolean( t );
                            }
                            else if( expTpl.isQuote( "" + t.charAt(0) ) ){
                                val = t.substring( 1, t.length()-1 );
                                if( ((String)val).length() == 1 )
                                    val = new Character( ((String)val).charAt(0) );
                            }
                        }
                    }
                    finally{
                        bNegative = false;
                    }

                    if( val == null ){
                        if( (i+1) < tokens.size() ){
                            CodeToken nextCT = (CodeToken)tokens.elementAt(i+1);
                            if( nextCT.t.equals( expTpl.arrIndexStart) ){
                                int []index_stend = new int[]{
                                    i+1, 0
                                };
                                Vector indexTokens = TokenUtils.findSubtoken( tokens, index_stend, expTpl.arrIndexStart, expTpl.arrIndexEnd );
//                                index_stend[1]++;

                                i = index_stend[1];

                                INode indexNode = buildExprTree( expTpl, _vars, indexTokens );
                                node = addVarNode( ct, node, _vars, t, indexNode, bNegative );
                            }
                            else
                                node = addVarNode( ct, node, _vars, t, bNegative );
                        }
                        else{
                            node = addVarNode( ct, node, _vars, t, bNegative );
                        }

                        bNegative = false;
                    }
                    else{
                        node = addValNode( ct, node, _vars, val );
                    }
                }
            }

            if( node == null ){
                CodeToken ct = null;
                if( tokens.size() > 0 ){
                    ct = (CodeToken)tokens.elementAt(0);
                    throw new CompileException( "ќтсутствует выражение", ct.line, ct.column, ct.position );
                }
                else
                    throw new Exception( "ќтсутствует набор лексем дл€ разбора выражени€ (tokens.size() = 0)" );
            }

            while( node.getParent() != null )
                node = node.getParent();

            INode root = new SubExprNode( null, bRootNegative );
            root.addChild( node );
            return root;

        }catch( CompileException compExc ){
            throw compExc;
        }catch( Exception e ){
            throw e;
        }
    }
}
