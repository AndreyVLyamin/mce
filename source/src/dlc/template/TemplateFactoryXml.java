package dlc.template;

import org.dom4j.*;
import org.dom4j.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;

/**
 * $Id: TemplateFactory.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ������� ������ �����������. ���������� � ������ ����������� �������� �� �������� XML-����� ������������
 */
public class TemplateFactoryXml implements TemplateFactory{

    Document m_doc;
    DoWhileTemplate m_dowhileTpl;
    WhileTemplate   m_whileTpl;
    IfTemplate      m_ifTpl;
    ExpressionTemplate m_expTpl;

    Vector keywords = new Vector();

    String          m_currentStyle = "";
    String          m_currentStyleAlias = "";
    boolean         m_bIsFree = false;

    /**
     * �����������
     * @param confPath ���� � ����� ������������
     */
    public TemplateFactoryXml( String confPath ){
        try{

System.out.println("confPath: " + confPath );

            InputStream is = getClass().getClassLoader().getResourceAsStream( confPath );
            SAXReader r = new SAXReader();
            m_doc = r.read( is );
        }catch( Exception exc ){
            exc.printStackTrace();
			System.out.println("TemplateFactory.ctor FAILED: " + exc.getMessage() );
        }
    }

    /**
     * ��������� escape-�������� (������� ������, ������� �������, ���������)
     * @param in �������� ������
     * @return ��������������� ������
     */
    private String processEscape( String in ){
        return in.replaceAll("\\\\r", "\r" ).
                replaceAll("\\\\n", "\n").
                replaceAll("\\\\t", "\t");
    }

    /**
     * �������� ������ �������� ����
     * @return ������ �������� ����
     */
    public Vector getKeywords(){
        return keywords;
    };

    /**
     * �������� ������ ��������� ����������� (��� If, While, DoWhile)
     */
    public Vector getPlaceholders(){
        Vector res = new Vector();
        try{
            res.addElement( m_ifTpl.ifInsert );
            res.addElement( m_ifTpl.ifelseInsert );
            res.addElement( m_dowhileTpl.dowhileInsert );
            res.addElement( m_whileTpl.whileInsert );
			res.addElement( m_expTpl.funcSqrtIns );
			res.addElement( m_expTpl.funcSinIns );
			res.addElement( m_expTpl.funcCosIns );
			res.addElement( m_expTpl.funcTanIns );
			res.addElement( m_expTpl.funcAsinIns );
			res.addElement( m_expTpl.funcAcosIns );
			res.addElement( m_expTpl.funcAtanIns );
			res.addElement( m_expTpl.singleLineCommentInsert );
			res.addElement( m_expTpl.multiLineCommentInsert );
        }catch( Exception exc ){
            res = new Vector();
        }
        return res;
    }
    /**
     * �������� ������ ��������� ����������� (��� If, While, DoWhile)
     */
    public Vector getAutoComplete(){
        Vector res = new Vector();
        try{
            res.addElement( m_ifTpl.ifInsert );
            res.addElement( m_ifTpl.ifelseInsert );
            res.addElement( m_dowhileTpl.dowhileInsert );
            res.addElement( m_whileTpl.whileInsert );

			res.addElement( m_expTpl.funcSqrtIns );
			res.addElement( m_expTpl.funcSinIns );
			res.addElement( m_expTpl.funcCosIns );
			res.addElement( m_expTpl.funcTanIns );
			res.addElement( m_expTpl.funcAsinIns );
			res.addElement( m_expTpl.funcAcosIns );
			res.addElement( m_expTpl.funcAtanIns );
        }catch( Exception exc ){}
        return res;
    }

    /**
     * ������������� ������� ��� ����������� Do/While
     * �� ������ ����� style
     */
    private DoWhileTemplate initDoWhile( String style ){
        DoWhileTemplate res = new DoWhileTemplate();

        try{
            Node n = (Node)m_doc.selectSingleNode("//CodeStyle[@name='" + style + "']");
            res.doToken = processEscape( n.selectSingleNode( "DoWhile/@entry" ).getText() );
            res.blockStart = processEscape( n.selectSingleNode("DoWhile/Block/@start").getText() );
            res.blockEnd = processEscape( n.selectSingleNode("DoWhile/Block/@end").getText() );
            res.whileToken = processEscape( n.selectSingleNode("DoWhile/While").getText() );
            res.conditionStart = processEscape( n.selectSingleNode("DoWhile/Condition/@start").getText() );
            res.conditionEnd = processEscape( n.selectSingleNode("DoWhile/Condition/@end").getText() );
            res.dowhileInsert = processEscape( n.selectSingleNode("DoWhile/DoWhileInsert").getText() );
            res.bInverse = false;
            try{
                String sInverse = n.selectSingleNode("DoWhile/While/@bInverse").getText();
                if( sInverse.equalsIgnoreCase("true") )
                    res.bInverse = true;
            }catch( Exception invExc ){}

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * ������������� ������� ��� ����������� While
     * �� ������ ����� style
     */
    private WhileTemplate initWhile( String style ){
        WhileTemplate res = new WhileTemplate();

        try{
            Node n = (Node)m_doc.selectSingleNode("//CodeStyle[@name='" + style + "']");
            res.blockStart = processEscape( n.selectSingleNode("While/Block/@start").getText() );
            res.blockEnd = processEscape( n.selectSingleNode("While/Block/@end").getText() );
            res.whileToken = processEscape( n.selectSingleNode("While/@entry").getText() );
            res.conditionStart = processEscape( n.selectSingleNode("While/Condition/@start").getText() );
            res.conditionEnd = processEscape( n.selectSingleNode("While/Condition/@end").getText() );
            res.whileInsert = processEscape( n.selectSingleNode("While/WhileInsert").getText() );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * ������������� ������� ��� ����������� If
     * �� ������ ����� style
     */
    private IfTemplate initIf( String style ){
        IfTemplate res = new IfTemplate();

        try{
            Node n = (Node)m_doc.selectSingleNode("//CodeStyle[@name='" + style + "']");
            res.ifToken = processEscape( n.selectSingleNode("If/@entry").getText() );
            res.blockStart = processEscape( n.selectSingleNode("If/Block/@start").getText() );
            res.blockEnd = processEscape( n.selectSingleNode("If/Block/@end").getText() );
            res.conditionStart = processEscape( n.selectSingleNode("If/Condition/@start").getText() );
            res.conditionEnd = processEscape( n.selectSingleNode("If/Condition/@end").getText() );
            res.elseifToken = processEscape( n.selectSingleNode("If/Elseif").getText() );
            res.elseToken = processEscape( n.selectSingleNode("If/Else").getText() );
            res.ifInsert = processEscape( n.selectSingleNode("If/IfInsert").getText() );
            res.ifelseInsert = processEscape( n.selectSingleNode("If/IfElseInsert").getText() );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }

    /**
     * ������������� ������� ��� ����������� ���������� ���������
     * �� ������ ����� style
     */
    private ExpressionTemplate initExpressions( String style ){
        ExpressionTemplate res = new ExpressionTemplate();

        try{
            Node n = (Node)m_doc.selectSingleNode("//CodeStyle[@name='" + style + "']/Expressions");
            res.assign = processEscape( n.selectSingleNode("Assign").getText() );
            res.eq = processEscape( n.selectSingleNode("Eq").getText() );
            res.neq = processEscape( n.selectSingleNode("Neq").getText() );
            res.lt = processEscape( n.selectSingleNode("Lt").getText() );
            res.gt = processEscape( n.selectSingleNode("Gt").getText() );
            res.lte = processEscape( n.selectSingleNode("Lte").getText() );
            res.gte = processEscape( n.selectSingleNode("Gte").getText() );
            res.not = processEscape( n.selectSingleNode("Not").getText() );
            res.or = processEscape( n.selectSingleNode("Or").getText() );
            res.and = processEscape( n.selectSingleNode("And").getText() );
            res.mod = processEscape( n.selectSingleNode("Mod").getText() );
            res.arrIndexStart = processEscape( n.selectSingleNode("ArrayIndex/@start").getText() );
            res.arrIndexEnd = processEscape( n.selectSingleNode("ArrayIndex/@end").getText() );
            res.blockEnd = processEscape( n.selectSingleNode("BlockEnd").getText() );
            res.groupStart = processEscape( n.selectSingleNode("Group/@start").getText() );
            res.groupEnd = processEscape( n.selectSingleNode("Group/@end").getText() );
            res.singleQuote = processEscape( n.selectSingleNode("Quotes/@single").getText() );
            res.doubleQuote = processEscape( n.selectSingleNode("Quotes/@double").getText() );
            res.singleLineComment = processEscape( n.selectSingleNode("Comment/@single").getText() );
            res.multiLineCommentStart = processEscape( n.selectSingleNode("Comment/@multiStart").getText() );
            res.multiLineCommentEnd = processEscape( n.selectSingleNode("Comment/@multiEnd").getText() );

			res.singleLineCommentInsert = processEscape( n.selectSingleNode("Comment/SingleInsert").getText() );
			res.multiLineCommentInsert = processEscape( n.selectSingleNode("Comment/MultilineInsert").getText() );

            if( n.selectSingleNode("UnaryAdd") != null )
                res.unaryAdd = n.selectSingleNode("UnaryAdd").getText();
            if( n.selectSingleNode("UnaryAddOne") != null )
                res.unaryAddOne = n.selectSingleNode("UnaryAddOne").getText();
            if( n.selectSingleNode("UnarySub") != null )
                res.unarySub = n.selectSingleNode("UnarySub").getText();
            if( n.selectSingleNode("UnarySubOne") != null )
                res.unarySubOne = n.selectSingleNode("UnarySubOne").getText();
            if( n.selectSingleNode("UnaryMul") != null )
                res.unaryMul = n.selectSingleNode("UnaryMul").getText();
            if( n.selectSingleNode("UnaryDiv") != null )
                res.unaryDiv = n.selectSingleNode("UnaryDiv").getText();

			res.funcSqrt = n.selectSingleNode("Function/Sqrt").getText();
			res.funcSqrtIns = res.funcSqrt + safeSelectXPath( "Function/Sqrt/@suffix", n );
			res.funcSin = n.selectSingleNode("Function/Sin").getText();
			res.funcSinIns = res.funcSin + safeSelectXPath( "Function/Sin/@suffix", n );
			res.funcCos = n.selectSingleNode("Function/Cos").getText();
			res.funcCosIns = res.funcCos + safeSelectXPath( "Function/Cos/@suffix", n );
			res.funcTan = n.selectSingleNode("Function/Tan").getText();
			res.funcTanIns = res.funcTan + safeSelectXPath( "Function/Tan/@suffix", n );
			res.funcAsin = n.selectSingleNode("Function/Asin").getText();
			res.funcAsinIns = res.funcAsin + safeSelectXPath( "Function/Asin/@suffix", n );
			res.funcAcos= n.selectSingleNode("Function/Acos").getText();
			res.funcAcosIns = res.funcAcos + safeSelectXPath( "Function/Acos/@suffix", n );
			res.funcAtan = n.selectSingleNode("Function/Atan").getText();
			res.funcAtanIns = res.funcAtan + safeSelectXPath( "Function/Atan/@suffix", n );
			res.funcRound = n.selectSingleNode("Function/Round").getText();
			res.funcRoundIns = res.funcRound + safeSelectXPath( "Function/Round/@suffix", n );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * ������������� ������ �������� ����
     * �� ������ ����� style
     */
    private Vector initKeywords( String style ){
        Vector res = new Vector();
        try{
            List list = m_doc.selectNodes( "//CodeStyle[@name='" + style + "']/Keyword/@name" );
            for( Iterator it = list.iterator(); it.hasNext(); ){
                res.addElement( ((Node)it.next()).getText() );
            }
        }catch( Exception exc ){
            //exc.printStackTrace();
            return null;
        }
        return res;
    }

    /**
     * �������� ������ ������ �����������
     */
    public String[] getStyles(){
        String []res = null;
        try{
            Vector vres = new Vector();
            List l = m_doc.selectNodes("//CodeStyle/@name");
            for( Iterator it = l.iterator(); it.hasNext(); ){
                String value = ((Node)it.next()).getText();
                vres.addElement( value );
            }

            res = new String[ vres.size() ];
            for( int i = 0; i < vres.size(); i++ )
                res[i] = (String)vres.elementAt(i);
        }catch( Exception exc ){
            //exc.printStackTrace();
        }
        return res;
    }

    /**
     * ��������� ����� ����� (�� ������� �����) �� ���������� �����
     * (��������� ����������� � �������� CodeStyle/@nameAlias)
     * @param aliasStyle �������� ����������
     * @return �������� �����
     */
    public String getStyleByAlias( String aliasStyle ){
        String res = "";
        try{
            res = m_doc.selectSingleNode( "//CodeStyle[@aliasName='" + aliasStyle + "']/@name" ).getText();
        }catch( Exception exc ){
			System.out.println("Failed to TemplateFactory.getStyleByAlias( [" + aliasStyle + "] )" );
            exc.printStackTrace();
            return aliasStyle;
        }
        return res;
    }

    /**
     * ��������� �������� ���������� ����� (��� �������� �� ����)
     * @return ��������� �������� �����
     */
    public String getCurrentStyleAlias(){
        return m_currentStyleAlias;
    }

    /**
     * ������������ ����� � ����������������� ��������
     * �� ������ ����� style
     */
    public boolean switchStyle( String style ){
        try{

            Node n = m_doc.selectSingleNode( "//CodeStyle[@name='" + style + "']/@isFree");
            if( n != null && n.getText().equalsIgnoreCase("true") ){
                m_bIsFree = true;
            }
            else
                m_bIsFree = false;

            m_currentStyleAlias = m_doc.selectSingleNode( "//CodeStyle[@name='" + style + "']/@aliasName" ).getText();

            if( !m_bIsFree ){
                m_dowhileTpl = initDoWhile( style );
                m_whileTpl = initWhile( style );
                m_ifTpl = initIf( style );
                m_expTpl = initExpressions( style );
            }
            else{
                m_dowhileTpl = null;
                m_whileTpl = null;
                m_ifTpl = null;
                m_expTpl = null;
            }
            keywords = initKeywords( style );
            m_currentStyle = style;

        }catch( Exception exc ){
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * �������� ������ (��� �������� �����)
     */
    public IfTemplate getIfTemplate(){
        return m_ifTpl;
    }
    /**
     * �������� ������ (��� �������� �����)
     */
    public WhileTemplate getWhileTemplate(){
        return m_whileTpl;
    }
    /**
     * �������� ������ (��� �������� �����)
     */
    public DoWhileTemplate getDoWhileTemplate(){
        return m_dowhileTpl;
    }
    /**
     * �������� ������ (��� �������� �����)
     */
    public ExpressionTemplate getExpressionTemplate(){
        return m_expTpl;
    }
    /**
     * �������� ������� �����
     */
    public String getCurrentStyle(){
        return m_currentStyle;
    }

    /**
     * �������� �� ��������� ����� ���������?
     */
    public boolean isFree(){
        return m_bIsFree;
    }

	private String safeSelectXPath( String path, Node n ){
		Node res = n.selectSingleNode( path );
		try{
			return res.getText();
		}catch( Exception exc ){
		}
		return "";
	}
}
