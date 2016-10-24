package dlc.code;

import dlc.expression.*;
import dlc.codenodes.*;
import dlc.util.*;
import dlc.template.*;
import java.util.*;

/**
 * $Id: CodeTokenizer.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * ����� ��� ������� ��������� ����.
 */
public class CodeTokenizer{

    DoWhileTemplate     m_dowhileTpl;
    IfTemplate          m_ifTpl;
    WhileTemplate       m_whileTpl;
    ExpressionTemplate  m_expTpl;

    /**
     * �����������
     * @param codeTemplateFactory ��������� ������ � �������� ������ �����������
     */
    public CodeTokenizer( TemplateFactory codeTemplateFactory ){
        m_dowhileTpl = codeTemplateFactory.getDoWhileTemplate();
        m_ifTpl = codeTemplateFactory.getIfTemplate();
        m_whileTpl = codeTemplateFactory.getWhileTemplate();
        m_expTpl = codeTemplateFactory.getExpressionTemplate();
    }

    /**
     * ����� ��� �������� ������� ���������� � ������ �����������
     */
    private boolean isVarExist( String varName, VariableContainer vars ){
        return vars.containsKey( varName );
    }

    /**
     * ����� ��� ������� ����������� IF
     */
    private CodeIfNode parseIf( Vector tokens, VariableContainer vars, int []start_end ) throws Exception{
        int c1 = start_end[0] + 1;

        CodeToken ct = (CodeToken)tokens.elementAt(c1);
        Vector exprTokens = null;
        Vector ifTokens = null;
        Vector elseTokens = null;
        CodeToken codeIfToken = ct;

        CodeBlockNode _ifCode = null;

        /**
         * ������ ������� vvv
         */
        int []expr_stend = new int[]{ c1, 0 };
        if( m_ifTpl.conditionStart.length() > 0 && m_ifTpl.conditionEnd.length() > 0 )
            exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_ifTpl.conditionStart, m_ifTpl.conditionEnd );
        else if( m_ifTpl.conditionStart.length() > 0 && !ct.t.equalsIgnoreCase( m_ifTpl.conditionStart ) )
            throw new CompileException( "�������� ������ \'" + m_ifTpl.conditionStart + "\', ��������: " + ct.t,
                    ct.line, ct.column, ct.position );
        else if( m_ifTpl.conditionEnd.length() > 0 ){
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_ifTpl.conditionEnd );
            }catch( Exception e1 ){
                throw new CompileException( "�������� ������ \'" + m_ifTpl.conditionEnd + "\', ��������: " + ct.t,
                        ct );
            }
        }
        else{
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_ifTpl.blockStart );
                expr_stend[1]--;
            }catch( Exception e2 ){
                throw new CompileException( "��������� ������ �����, ������: \'" + m_ifTpl.blockStart + "\'", ct );
            }
        }
        /**
         * ������ ������� ^^^
         */
        expr_stend[1]++;
        if( expr_stend[1] >= tokens.size() )
            throw new CompileException( "��������� ������ �����, ������: \'" + m_ifTpl.blockStart + "\'", ct.line, ct.column, ct.position );

        ct = (CodeToken)tokens.elementAt(expr_stend[1]);

        /**
         * ������ ����� ������� vvv
         */
        if( m_ifTpl.blockStart.length() > 0 && ct.t.equals( m_ifTpl.blockStart ) &&
            m_ifTpl.blockEnd.length() > 0 ){

            int []ifEnd = new int[1];
            Vector vEndBlocks = new Vector();
            vEndBlocks.addElement( m_ifTpl.elseifToken );
            vEndBlocks.addElement( m_ifTpl.elseToken );
            vEndBlocks.addElement( m_ifTpl.blockEnd );
            expr_stend[1]++;

            _ifCode = parseCodeTokens( vars, new Vector(tokens.subList( expr_stend[1], tokens.size() )), vEndBlocks, ifEnd );

            start_end[1] = expr_stend[1] + ifEnd[0];

            if( ((CodeToken)tokens.elementAt( start_end[1])).t.equals( m_ifTpl.blockEnd) )
                start_end[1]++;
            while( start_end[1] < tokens.size() && ((CodeToken)tokens.elementAt( start_end[1])).t.equals( m_expTpl.blockEnd) )
                start_end[1]++;
        }
        else{
            throw new CompileException( "��������� ������ �����, ������: \'" + m_ifTpl.blockStart + "\'", ct.line, ct.column, ct.position );
        }
        /**
         * ������ ����� ������� ^^^
         */

        CodeIfNode     elseIfNode = null;
        CodeBlockNode  elseNode = null;

        if( start_end[1] < tokens.size() ){
            ct = (CodeToken)tokens.elementAt(start_end[1]);
            /**
             * ������ elseIf vvv
             */
            if( ct.t.equalsIgnoreCase( m_ifTpl.elseifToken ) ){
                start_end[0] = start_end[1];
                elseIfNode = parseIf( tokens, vars, start_end );
//                start_end[1]--;
            }

            /**
             * ������ else vvv
             */
            else if( ct.t.equalsIgnoreCase( m_ifTpl.elseToken ) ){
                start_end[1]++;
                ct = (CodeToken)tokens.elementAt( start_end[1] );

                if( m_ifTpl.blockStart.length() > 0 && ct.t.equalsIgnoreCase( m_ifTpl.blockStart ) &&
                    m_ifTpl.blockEnd.length() > 0 ){

                    start_end[1]++;
                }

                int []ifEnd = new int[1];
                Vector vEndBlocks = new Vector();
                vEndBlocks.addElement( m_ifTpl.blockEnd );
                Vector tokElse = new Vector(tokens.subList( start_end[1], tokens.size() ));

                elseNode = parseCodeTokens( vars, tokElse, vEndBlocks, ifEnd );

                start_end[1] += ifEnd[0];

                if( ((CodeToken)tokens.elementAt( start_end[1])).t.equals( m_ifTpl.blockEnd) )
                    start_end[1]++;
                while( start_end[1] < tokens.size() && ((CodeToken)tokens.elementAt( start_end[1])).t.equals( m_expTpl.blockEnd) )
                    start_end[1]++;
            }
        }

        INode ifExpr = new ExprParser().buildExprTree( m_expTpl, vars, exprTokens );
        CodeIfNode ifCode = null;
        if( elseIfNode != null )
            ifCode = new CodeIfNode( codeIfToken, ifExpr, _ifCode, elseIfNode );
        else if( elseNode != null )
            ifCode = new CodeIfNode( codeIfToken, ifExpr, _ifCode, elseNode );
        else
            ifCode = new CodeIfNode( codeIfToken, ifExpr, _ifCode );

        return ifCode;
    }

    /**
     * ����� ��� ������� ����������� WHILE
     */
    private CodeNode parseWhile( Vector tokens, VariableContainer vars, int []start_end ) throws Exception{
        int c1 = start_end[0] + 1;

        CodeToken ct = (CodeToken)tokens.elementAt(c1);
        Vector    exprTokens = null;
        Vector    ifTokens = null;

        CodeBlockNode _whileCode = null;

        int []expr_stend = new int[]{ c1, c1 };

        if( m_whileTpl.conditionStart.length() > 0 ){
            if( !ct.t.equals( m_whileTpl.conditionStart ) ){
                throw new CompileException( "��������� \'" + m_whileTpl.conditionStart + "\', ��������: " + tokens.elementAt(c1),
                    ct.line, ct.column, ct.position );
            }
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_whileTpl.conditionStart, m_whileTpl.conditionEnd );
            }catch( Exception conditionExc ){
                throw new CompileException( "��������� ������ \'" + m_whileTpl.conditionEnd + "\'", ct );
            }
            expr_stend[1]++;
        }
        else if( m_whileTpl.conditionEnd.length() > 0 ){

            exprTokens = new Vector();
            //expr_stend[1]++;
            while( !((CodeToken)tokens.elementAt(expr_stend[1])).t.equalsIgnoreCase(m_whileTpl.conditionEnd) ){
                exprTokens.addElement( (CodeToken)tokens.elementAt(expr_stend[1]) );
                expr_stend[1]++;
            }
            expr_stend[1]++;

            if( expr_stend[1] >= tokens.size() )
                throw new CompileException( "��������� \'" + m_whileTpl.conditionEnd + "\'", ct );
        }
        else{
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_whileTpl.blockStart );
            }catch( Exception exc ){
                throw new CompileException( "��������� ������ �����, ������: \'" + m_whileTpl.blockStart + "\'", ct.line, ct.column, ct.position );
            }
        }

        ct = (CodeToken)tokens.elementAt(expr_stend[1]);
        if( ct.t.equals( m_whileTpl.blockStart ) ){
            int []whileEnd = new int[1];
            Vector vEndBlocks = new Vector();
            vEndBlocks.addElement(m_whileTpl.blockEnd);
            expr_stend[1]++;
            Vector wTokens = new Vector(tokens.subList(expr_stend[1], tokens.size()));

            _whileCode = parseCodeTokens( vars, wTokens, vEndBlocks, whileEnd );

            //while.endBlock + exp.endBlock (cause of +1)
            start_end[1] = expr_stend[1] + whileEnd[0] + 1;
        }
        else{
            throw new CompileException( "��������� ������ �����, ������: \'" + m_whileTpl.blockStart + "\'", ct.line, ct.column, ct.position );
        }

		//���� _code ����� ��������� ��� ��������� ����� + ��� �������� �������
        INode ifExpr = new ExprParser().buildExprTree( m_expTpl, vars, exprTokens );
        CodePreLoopNode whileCode = new CodePreLoopNode( (CodeToken)tokens.elementAt(c1), ifExpr, _whileCode );

        return whileCode;
    }

    /**
     * ����� ��� ������� ����������� DO/WHILE
     */
    private CodeNode parseDo( Vector tokens, VariableContainer vars, int []start_end ) throws Exception{
        int c1 = start_end[0] + 1;

        CodeToken ct = (CodeToken)tokens.elementAt( c1 );
        Vector exprTokens = null;
        int []expr_stend = new int[]{ c1, c1 };

        CodeBlockNode _doCode = null;

        if( m_dowhileTpl.blockStart.length() > 0 ){
            if( !ct.t.equals( m_dowhileTpl.blockStart ) ){
                throw new CompileException( "��������� \'" + m_dowhileTpl.blockStart + "\', ��������: " + ct.t,
                        ct );
            }

            expr_stend[1]++;

            int []doEnd = new int[1];
            Vector vDoEnd = new Vector();
            vDoEnd.addElement( m_dowhileTpl.blockEnd );
            vDoEnd.addElement( m_dowhileTpl.whileToken );
            Vector doTokens = new Vector( tokens.subList( expr_stend[1], tokens.size() ) );

            _doCode = parseCodeTokens( vars, doTokens, vDoEnd, doEnd );

            expr_stend[1] += doEnd[0];

            while( expr_stend[1] < tokens.size() &&
                    ((CodeToken)tokens.elementAt(expr_stend[1])).t.equalsIgnoreCase(m_dowhileTpl.blockEnd) )
                expr_stend[1]++;

            ct = (expr_stend[1] < tokens.size()) ? (CodeToken)tokens.elementAt(expr_stend[1]) :
                    (CodeToken)tokens.elementAt(tokens.size()-1);
        }
        else{
            try{

                int []doEnd = new int[1];
                Vector vDoEnd = new Vector();
                vDoEnd.addElement( m_dowhileTpl.whileToken );
                Vector doTokens = new Vector( tokens.subList( expr_stend[1], tokens.size() ) );

                _doCode = parseCodeTokens( vars, doTokens, vDoEnd, doEnd );

                expr_stend[1] += doEnd[0];

                ct = (CodeToken)doTokens.elementAt( doTokens.size()-1 );

            }catch( CompileException compExc ){
                throw compExc;
            }catch( Exception exc ){
                throw new CompileException( "��������� \'" + m_dowhileTpl.whileToken + "\', ��������: " + ct.t, ct );
            }
        }

        if( expr_stend[1] >= tokens.size() ||
            !((CodeToken)tokens.elementAt(expr_stend[1])).t.equalsIgnoreCase( m_dowhileTpl.whileToken ) ){
            throw new CompileException( "��������� \'" + m_dowhileTpl.whileToken + "\', ��������: " + ct.t,
                    ct );
        }

        ct = (CodeToken)tokens.elementAt(expr_stend[1]);
        expr_stend[1]++;

        if( m_dowhileTpl.conditionStart.length() > 0 &&
                (expr_stend[1] >= tokens.size() ||
                !((CodeToken)tokens.elementAt(expr_stend[1])).t.equalsIgnoreCase( m_dowhileTpl.conditionStart )) ){
            throw new CompileException( "��������� \'" + m_dowhileTpl.conditionStart + "\'", ct );
        }

        ct = (CodeToken)tokens.elementAt(expr_stend[1]);
        expr_stend[0] = expr_stend[1];

        if( m_dowhileTpl.conditionEnd.length() > 0 ){
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_dowhileTpl.conditionStart, m_dowhileTpl.conditionEnd );
            }catch( Exception exc ){
                throw new CompileException( "��������� '" + m_dowhileTpl.conditionEnd + "'", ct );
            }
        }
        else{
            try{
                exprTokens = TokenUtils.findSubtoken( tokens, expr_stend, m_expTpl.blockEnd );
                expr_stend[1]--;
            }catch( Exception exc ){
                throw new CompileException( "��������� '" + m_expTpl.blockEnd + "'", ct.line, ct.column, ct.position );
            }
        }
        expr_stend[1]++;

        INode ifExpr = new ExprParser().buildExprTree( m_expTpl, vars, exprTokens );
        CodePostLoopNode doCode = new CodePostLoopNode( ct, ifExpr, _doCode, m_dowhileTpl.bInverse );

        start_end[1] = expr_stend[1];

        return doCode;
    }

    /**
     * ����� ��� ������� ��������� ������������
     */
    private CodeAssignNode parseAssignment( VariableContainer vars, Vector tokens, int start, int end ) throws Exception{

        INode  expr;
        Vector exprTokens = new Vector();
        String varName = ((CodeToken)tokens.elementAt( start )).t;

        String []assignOps = m_expTpl.getTerms();

        int i = start+1;

        CodeToken ctOp = (CodeToken)tokens.elementAt(i);
        String op = ((CodeToken)tokens.elementAt(i)).t;

        boolean bOp = false;
        for( int j = 0; j < assignOps.length; j++ ){
            if( op.equals( assignOps[j] ) ){
                bOp = true;
                break;
            }
        }

        if( !bOp )
            throw new CompileException( "��������� ������ ����������, ������� ������ '" +
                    ((CodeToken)tokens.elementAt(i)).t, (CodeToken)tokens.elementAt(i) );

        if( op.equals(m_expTpl.unaryAdd) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.add ) );
        }
        else if( op.equals( m_expTpl.unaryAddOne ) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.add ) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, "1" ) );
        }
        else if( op.equals(m_expTpl.unarySub) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.sub ) );
        }
        else if( op.equals( m_expTpl.unarySubOne ) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.sub ) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, "1" ) );
        }
        else if( op.equals(m_expTpl.unaryMul) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.mul ) );
        }
        else if( op.equals(m_expTpl.unaryDiv) ){
            exprTokens.addElement( tokens.elementAt(start) );
            exprTokens.addElement( new CodeToken( ctOp.line, ctOp.column, ctOp.position, m_expTpl.div ) );
        }

        for( i++; i < end; i++ )
            exprTokens.addElement( tokens.elementAt(i) );

        try{
            expr = new ExprParser().buildExprTree( m_expTpl, vars, exprTokens );
        }catch( CompileException compExc ){
            throw compExc;
        }catch( Exception exc ){
            if( tokens.size() > 0 ){
                CodeToken ct = (CodeToken)tokens.elementAt( start );
                throw new CompileException("����������� ���������",
                       ct.line, ct.column, ct.position );
            }
            else
                throw new Exception("����������� ����� ������");
        }

        if( !isVarExist( varName, vars ) ){
            CodeToken ct = (CodeToken)tokens.elementAt( start );
            throw new CompileException("���������� " + varName + " �� ���������", ct.line, ct.column, ct.position );
        }

        return new CodeAssignNode( (CodeToken)tokens.elementAt( start ), expr, varName );
    }

    /**
     * ����� ��� ������� ����������� ������������ (��� ��������)
     */
    private CodeAssignNode parseArrayAssignment( VariableContainer vars, Vector tokens, int start, int end ) throws Exception{

        INode  expr, indexExpr;
        Vector exprTokens = new Vector();
        Vector indexTokens = new Vector();
        String varName = ((CodeToken)tokens.elementAt( start )).t;

        int c1 = start + 1;

        //���� ��������� ��� �������
        {
            CodeToken ct = (CodeToken)tokens.elementAt(c1);
            if( !ct.t.equals( m_expTpl.arrIndexStart ) ){
                throw new CompileException( "��������� ������ \'" + m_expTpl.arrIndexStart + "\'", ct.line, ct.column, ct.position );
            }
            int []index_stend = new int[]{
                start + 1, 0
            };
            indexTokens = TokenUtils.findSubtoken( tokens, index_stend, m_expTpl.arrIndexStart, m_expTpl.arrIndexEnd );
            if( indexTokens == null || indexTokens.size() == 0 ){
                throw new CompileException( "��������� ������� ������ � �������", ct.line, ct.column, ct.position );
            }
            index_stend[1]++;
            c1 = index_stend[1];
        }

        /**
         * � ���� ������ ����� �������� ������� �������� � �����������
         */
        String []assignOps = new String[]{
            m_expTpl.assign
        };

        boolean bOp = false;
        for( int j = 0; j < assignOps.length; j++ ){
            if( ((CodeToken)tokens.elementAt(c1)).t.equals( assignOps[j] ) ){
                bOp = true;
                break;
            }
        }

        if( !bOp )
            throw new CompileException( "��������� ������ ����������, ������� ������ '" +
                    ((CodeToken)tokens.elementAt(c1)).t, (CodeToken)tokens.elementAt(c1) );

        for( c1++; c1 < end; c1++ )
            exprTokens.addElement( tokens.elementAt(c1) );

        /**
         * ������ ��������� ��� �������� ����������
         */
        try{
            expr = new ExprParser().buildExprTree( m_expTpl, vars, exprTokens );
            indexExpr = new ExprParser().buildExprTree( m_expTpl, vars, indexTokens );
        }catch( CompileException compExc ){
            throw compExc;
        }catch( Exception exc ){
            if( tokens.size() > 0 ){
                CodeToken ct = (CodeToken)tokens.elementAt(0);
                throw new CompileException("����������� ���������",
                       ct.line, ct.column, ct.position );
            }
            else
                throw new Exception("����������� ����� ������");
        }

//        vars.put( varName, "" );

        return new CodeAssignNode( (CodeToken)tokens.elementAt(start), expr, varName, indexExpr );
    }

    /**
     * ����� ��� ������������ ������ �� ���������� java.util.List
     */
    private String buildString( List tokens ){
        String res = "";
        for( int i = 0; i < tokens.size(); i++ )
            res += ((CodeToken)tokens.get(i)).t;
        return res;
    }

    /**
     * ����� ��� ������� ����� ����
     * @param vars ���������� ���������
     * @param tokens �������
     * @return ��������� CodeBlockNode
     */
    public CodeBlockNode parseCodeTokens( VariableContainer vars, Vector tokens, Vector blockEnds, int []endPosition ) throws Exception{

        CodeBlockNode _block;
        if( tokens.size() == 0 )
            _block = new CodeBlockNode( new CodeToken(0,0,0,"") );
        else
            _block = new CodeBlockNode( (CodeToken)tokens.elementAt(0) );

        int prev_i = 0, i = 0;
        String t = "";
        CodeToken lastToken = null;

        boolean bHasEndBlock = false;

        for( i = 0; i < tokens.size(); i++ ){
            CodeToken ct = (CodeToken)tokens.elementAt(i);
            lastToken = ct;
            t = ct.t;

            for( int j = 0; j < blockEnds.size(); j++ ){
                if( t.equals( blockEnds.elementAt(j) ) ){

                    if( !bHasEndBlock ){
                        throw new CompileException( "��������� ������: '" + m_expTpl.blockEnd + "'",
                                  (CodeToken)tokens.elementAt(prev_i) );
                    }

                    endPosition[0] = i;

                    return _block;
                }
            }

            bHasEndBlock = false;

            if( t.equals( m_expTpl.blockEnd ) ){
                bHasEndBlock = true;

                if( (i - prev_i) <= 0 ){
                    prev_i = i+1;
                }
                else{
                    String s1 = ((CodeToken)tokens.elementAt( prev_i + 1 )).t;

                    if( m_expTpl.isAssign( s1 ) ){
                        CodeAssignNode _code = parseAssignment( vars, tokens, prev_i, i );
                        prev_i = i+1;
                        _block.addNode( _code );
                    }
                    else if( s1.equals( m_expTpl.arrIndexStart ) ){
                        //������
                        CodeAssignNode _code = parseArrayAssignment( vars, tokens, prev_i, i );
                        prev_i = i+1;
                        _block.addNode( _code );
                    }
                    else if( s1.equals( m_expTpl.groupStart ) ){
                        //����� �������
                        throw new CompileException( "����� ������� �� ����������", ct.line, ct.column, ct.position );
                    }
                    else{
                        throw new CompileException( "����������� ���������: '" + buildString( tokens.subList(prev_i, i) ) + "'",
                                (CodeToken)tokens.elementAt(prev_i) );
                    }
                }
            }
            else if( isNewline(t) ){
                /**
                 * � Basic newline � blockEnd - ��� ���� � �� �� ���������,
                 * ��������� ��� Basic � ���������� elseif (blockEnd)
                 */
                prev_i = i+1;
            }
            else if( t.equals( m_ifTpl.getEntry() ) ){
                int []start_end = new int[]{ i, 0 };
                _block.addNode( parseIf( tokens, vars, start_end ) );
                i = start_end[1] - 1;
                prev_i = i+1;
                bHasEndBlock = true;
            }
            else if( t.equals( m_dowhileTpl.getEntry() ) ){
                int []start_end = new int[]{ i, 0 };
                _block.addNode( parseDo( tokens, vars, start_end ) );
                i = start_end[1] - 1;
                prev_i = i+1;
                bHasEndBlock = true;
            }
            else if( t.equals( m_whileTpl.getEntry() ) ){
                int []start_end = new int[]{ i, 0 };
                _block.addNode( parseWhile( tokens, vars, start_end ) );
                i = start_end[1] - 1;
                prev_i = i+1;

                bHasEndBlock = true;
            }
        }

        if( !bHasEndBlock ){
            throw new CompileException( "��������� ������: '" + m_expTpl.blockEnd + "'",
                      (CodeToken)tokens.elementAt(prev_i) );
        }

        if( blockEnds.size() > 0 ){
            String blockNames = "";
            for( i = 0; i < blockEnds.size(); i++ ){
                blockNames += (i>0) ? ("," + blockEnds.elementAt(i)) : blockEnds.elementAt(i);
            }
            throw new CompileException( "��������� ��������� �����: (" + blockNames + ")", (CodeToken)tokens.elementAt( tokens.size()-1 ) );
        }

        return _block;
    }

    /**
     * ����� ��� �������� - �������� �� ������ ch ��������� ��� ���������� ����� �����������
     */
    private boolean isQuote( char ch ){
        return isQuote( ch, null );
    }

    /**
     * ����� ��� �������� - �������� �� ������ ch ��������� ��� ���������� ����� �����������
     * (� ������ �������������)
     */
	private boolean isQuote( char ch, CodeToken prevToken ){
		try{
            return (prevToken != null && !prevToken.t.equals("\\") && m_expTpl.isQuote(""+ch)) ||
                   (prevToken == null && m_expTpl.isQuote(""+ch));
		}catch( Exception e ){
			//e.printStackTrace();
		}
		return false;
	}

/*
    private Vector parse( String s ) throws Exception{

        int      p = 0;
        String   token = "";
        String   tterm = "";
        boolean  bWS = false;
        String   ws = " \t\r\n";
        String   term = "=+-/\\*<>()[];:.,!#$%^&|{}\"'";

        String   singles = "{}()[];";
        boolean  bIsSingle = false;

        boolean  bHasTerm = false;
		boolean  bInQuote = false;

        Vector   res = new Vector();

        int line = 1;
        int column = 1;
        boolean bPrevCR = false;
        int prevWScolumn = 0;

        while( p < s.length() ){

            bWS = false;
            bHasTerm = false;
            bIsSingle = false;

            if( s.charAt(p) == '\r' ||
                (!bPrevCR && s.charAt(p) == '\n' ) ){
                column = 1;
                line++;
                bPrevCR = s.charAt(p) == '\r';
            }
            else{
                bPrevCR = false;
                column++;
            }

            for( int i = 0; i < ws.length(); i++ ){
				//Is it a white space? (whitespace is a terminator in quotes)
                if( s.charAt(p) == ws.charAt(i) ){
                    bWS = true;
					bHasTerm = bInQuote;
                    prevWScolumn = column;
                    break;
                }
            }

            for( int i = 0; !bWS && i < term.length(); i++ ){
				//Is it an operation? (terminator)
                if( s.charAt(p) == term.charAt(i) ){
                    bHasTerm = true;
                    break;
                }
            }

            for( int i = 0; !bWS && i < singles.length(); i++ ){
				//Is it an operation? (terminator)
                if( s.charAt(p) == singles.charAt(i) ){
                    bIsSingle = true;
                    break;
                }
            }

            if( bWS && bHasTerm ){
                res.addElement( new CodeToken( line, prevWScolumn, p, "" + s.charAt(p) ) );
            }
            else if( bIsSingle ){
                if( token.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
                    token = "";
                }
                else if( tterm.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, tterm ) );
                    tterm = "";
                }
                res.addElement( new CodeToken( line, prevWScolumn, p, "" + s.charAt(p) ) );
            }
            else if( bWS && token.length() > 0 ){
				//Adding token and (optional) operation

                res.addElement( new CodeToken( line, prevWScolumn, p, token ) );

                token = "";
            }
            else if( bWS && tterm.length() > 0 ){
                res.addElement( new CodeToken( line, prevWScolumn, p, tterm ) );
                tterm = "";
            }
            else if( !bWS && bHasTerm ){
                if( token.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
                    token = "";
                }
                tterm += s.charAt(p);
            }
            else if( !bWS ){
                if( tterm.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, tterm ) );
                    tterm = "";
                }
                token += s.charAt(p);
            }

			if( isQuote( s.charAt(p), (res.size()>0 ? (CodeToken)res.elementAt(res.size()-1) : null) ) )
				bInQuote = !bInQuote;

            p++;
        }

        if( token.length() > 0 ){
            res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
        }
        else if( tterm.length() > 0 ){
            res.addElement( new CodeToken( line, prevWScolumn, p, tterm ) );
        }
        else if( token.length() > 0 && tterm.length() > 0 ){
            System.out.println("ERROR: after parse loop token & tterm is not empty!!!");
            throw new Exception( "Token & Tterm is not empty" );
        }
        return res;
    }
*/







    /**
     * ������ ������ �������� �� �������
     * @param s ������ � �������� �����
     * @return ����� ������
     */
    public Vector parse2( String s ) throws Exception{

        int      p = 0;
        String   token = "";
        String   tterm = "";
        boolean  bWS = false;

        String   ws = " \t";

        String []term = null;
        {
            String []expTerms = m_expTpl.getTerms();
            String []doTerms = m_dowhileTpl.getTerms();
            String []whileTerms = m_whileTpl.getTerms();
            String []ifTerms = m_ifTpl.getTerms();
            term = new String[ expTerms.length + doTerms.length + whileTerms.length + ifTerms.length ];
            int index = 0, i = 0;
            for( i = 0; i < expTerms.length; i++ ){
                term[ index + i ] = expTerms[i];
            }
            index += i;
            for( i = 0; i < doTerms.length; i++ ){
                term[ index + i ] = doTerms[i];
            }
            index += i;
            for( i = 0; i < whileTerms.length; i++ ){
                term[ index + i ] = whileTerms[i];
            }
            index += i;
            for( i = 0; i < ifTerms.length; i++ ){
                term[ index + i ] = ifTerms[i];
            }
        }



        term = sortOperationArray( term );
        String   sTerm = "/=+-/\\*<>()[];:,!#$%^&|{}\"'\r\n";

        boolean  bHasTerm = false;

        Vector   res = new Vector();

        int line = 1;
        int column = 1;
        boolean bPrevCR = false;
        int prevWScolumn = 0;

        while( p < s.length() ){

            bWS = false;
            bHasTerm = false;

            if( s.charAt(p) == '\r' ||
                (!bPrevCR && s.charAt(p) == '\n' ) ){
                column = 1;
                line++;
                bPrevCR = s.charAt(p) == '\r';
            }
            else{
                bPrevCR = false;
                column++;
            }

            for( int i = 0; i < ws.length(); i++ ){
				//Is it a white space?
                if( s.charAt(p) == ws.charAt(i) ){
                    bWS = true;
                    prevWScolumn = column;
                    break;
                }
            }

            for( int i = 0; !bWS && i < sTerm.length(); i++ ){
				//Is it an operation? (terminator)
                if( s.charAt(p) == sTerm.charAt(i) ){
                    bHasTerm = true;
                    break;
                }
            }

            if( bHasTerm &&
                    (m_expTpl.singleLineComment.length() > 0 && (tterm+s.charAt(p)).endsWith(m_expTpl.singleLineComment) ||
                     m_expTpl.multiLineCommentStart.length() > 0 && (tterm+s.charAt(p)).endsWith(m_expTpl.multiLineCommentStart)) ){

                /**
                 * TODO: ��������� ������������ �� ��������!!!
                 * ������:
                 * <�������������>
                 *   adasdasd
                 * </�������������>
                 * <������������/>ssssssssssssss
                 * ... (��� ��������� �������� ������������������)
                 */

                int termLastIndex = tterm.length();
                String endComment = "";
                if( (tterm + s.charAt(p)).endsWith( m_expTpl.singleLineComment) ){
                    termLastIndex -= (m_expTpl.singleLineComment.length() - 1);
                    endComment = "\n";
                }
                else{
                    termLastIndex -= (m_expTpl.multiLineCommentStart.length() - 1);
                    endComment = m_expTpl.multiLineCommentEnd;
                }

                if( termLastIndex >= 0 && termLastIndex <= tterm.length() ){
                    tterm = tterm.substring( 0, termLastIndex );
                }

                if( tterm.length() > 0 ){
                    res.addAll( parseTermChars( tterm, term, line, prevWScolumn, p - 1 ) );
                    tterm = "";
                    prevWScolumn = column;
                }
                else if( token.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p - 1, token ) );
                    token = "";
                    prevWScolumn = column;
                }

                String inCommentStr = "";

                while( p < s.length() && !inCommentStr.endsWith(endComment) ){

                    if( s.charAt(p) == '\r' ||
                        (!bPrevCR && s.charAt(p) == '\n' ) ){

                        column = 1;
                        line++;
                        bPrevCR = s.charAt(p) == '\r';
                    }
                    else{
                        bPrevCR = false;
                        column++;
                    }

                    inCommentStr += s.charAt(p);
                    p++;
                }

//				if( inCommentStr.endsWith("\n") )

				p--;

				{
					System.out.println( "Tokens after comment: [" + s.substring(p) + "]" );
				}

//System.out.println( "Last line after comment: " + line );

                prevWScolumn = column;
//				line++;
            }
            else if( bHasTerm && isQuote( s.charAt(p), (res.size()>0 ? (CodeToken)res.elementAt(res.size()-1) : null) ) ){
                /* ��������� ����� */
                /* ��������� ����� */
                /* ��������� ����� */
                if( tterm.length() > 0 ){
                    res.addAll( parseTermChars( tterm, term, line, prevWScolumn, p ) );
                    tterm = "";
                    prevWScolumn = column;
                }
                else if( token.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
                    token = "";
                    prevWScolumn = column;
                }

                boolean bEscape = false;
                StringBuffer  stringValue = new StringBuffer();
                stringValue.append( s.charAt(p) );
                int start_p = p;
                do{
                    p++;
                    if( p >= s.length() )
                        throw new CompileException( "��������� ��������� ������", line, prevWScolumn, p );

                    bEscape = false;
                    if( s.charAt(p) == '\\' ){
                        bEscape = true;
                        p++;
                        if( p >= s.length() )
                            throw new CompileException( "��������� ��������� ������", line, prevWScolumn, p );
                    }

                    /**
                     * ���� ��� ������� ����� �� �������� ���� � ��������������
                     * (��� ����������� ������� ������ ����� � ��������� �������������� � ���������� �������)
                     */
                    if( /*!bEscape &&*/ isNewline( "" + s.charAt(p) ) ){
                        throw new CompileException( "��������� ��������� ������", line, prevWScolumn, p );
                    }

                    stringValue.append( s.charAt(p) );

                }while( bEscape || !isQuote( s.charAt(p) ) );

                res.addElement( new CodeToken( line, prevWScolumn, p, stringValue.toString() ) );
                prevWScolumn = column + (p - start_p);
            }
            else if( bWS && token.length() > 0 ){

                res.addElement( new CodeToken( line, prevWScolumn, p, token ) );

                token = "";
            }
            else if( bWS && tterm.length() > 0 ){
                res.addAll( parseTermChars( tterm, term, line, prevWScolumn, p ) );
                tterm = "";
            }
            else if( !bWS && bHasTerm ){
                if( token.length() > 0 ){
                    res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
                    token = "";
                    prevWScolumn = column;
                }
                tterm += s.charAt(p);
            }
            else if( !bWS ){
                if( tterm.length() > 0 ){
                    res.addAll( parseTermChars( tterm, term, line, prevWScolumn, p ) );
                    tterm = "";
                    prevWScolumn = column;
                }
                token += s.charAt(p);
            }

            p++;
        }

        if( token.length() > 0 ){
            res.addElement( new CodeToken( line, prevWScolumn, p, token ) );
        }
        else if( tterm.length() > 0 ){
            res.addAll( parseTermChars( tterm, term, line, prevWScolumn, p ) );
        }
        else if( token.length() > 0 && tterm.length() > 0 ){
            System.out.println("ERROR: after parse loop token & tterm is not empty!!!");
            throw new Exception( "Token & Tterm is not empty" );
        }

//System.out.println("RES :" + res );

        return res;
    }

    private String []sortOperationArray( String []termTokens ){
        String []res = new String[ termTokens.length ];
        System.arraycopy( termTokens, 0, res, 0, res.length );
        for( int i = 0; i < res.length; i++ ){
            for( int j = 0; j < i; j++ ){
                if( res[i].length() > res[j].length() ){
                    String tmp = res[i];
                    res[i] = res[j];
                    res[j] = tmp;
                }
            }
        }
        return res;
    }

    /**
     * ����� ������� ������ ������������ �������� �� ���������, ��������� � �������� Operations
     * �������� ���������� � ������� termTokens
     * ��� ���������� ������ ������ ������ ���� ������������ � ������� �������� ����� �����
     */
    private Vector parseTermChars( String terms, String []termTokens, int line, int startColumn, int position )
        throws Exception{

        Vector res = new Vector();
        boolean bFound = false;

        for( int i = 0; i < terms.length(); i++ ){
            bFound = false;
            for( int j = 0; j < termTokens.length; j++ ){
                if( termTokens[j].length() > 0 && terms.substring(i).startsWith(termTokens[j]) ){
                    i += termTokens[j].length() - 1;
                    res.addElement( new CodeToken( line, startColumn + i, position, termTokens[j] ) );
                    bFound = true;
                    break;
                }
            }

            if( !bFound && !isNewline(terms.substring(i)) ){
                throw new CompileException( "����������� ������: '" + terms.substring(i,i+1) + "'", line, startColumn, position );
            }
        }

        return res;
    }

    /**
     * ����� ��� ��������, ���������� �� ������ str � ������� �������� ������
     */ 
    private boolean isNewline( String str ){
        return str.startsWith("\r\n") ||
                str.charAt(0) == '\r' ||
                str.charAt(0) == '\n';
    }
}
