package dlc.template;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

/**
 * $Id: TemplateFactory.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, фабрику стилей кодирования. Информация о стилях кодирования читается из внешнего XML-файла конфигурации
 */
public class TemplateFactoryXml2 implements TemplateFactory{

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
     * Конструктор
     * @param confPath путь к файлу конфигурации
     */
    public TemplateFactoryXml2( String confPath ){
        try{

System.out.println("confPath: " + confPath );

            InputStream is = this.getClass().getClassLoader().getResourceAsStream( confPath );

//            System.out.println("confPath 1: " + is.toString() );

            DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();

//            System.out.println("confPath 2: " + docBuild.toString() );

            m_doc = docBuild.parse( is );
        }catch( Exception exc ){
            exc.printStackTrace();
			System.out.println("TemplateFactory.ctor FAILED: " + exc.getMessage() );
        }
    }

    /**
     * Обработка escape-символов (перенос строки, возврат каретки, табуляция)
     * @param in исходная строка
     * @return преобразованная строка
     */
    private String processEscape( String in ){
        return in.replaceAll("\\\\r", "\r" ).
                replaceAll("\\\\n", "\n").
                replaceAll("\\\\t", "\t");
    }

    /**
     * Получить массив ключевых слов
     * @return массив ключевых слов
     */
    public Vector getKeywords(){
        return keywords;
    };

    /**
     * Получить список выражений подстановки (для If, While, DoWhile)
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
     * Получить список выражений подстановки (для If, While, DoWhile)
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
     * Инициализация шаблона для конструкции Do/While
     * на основе стиля style
     */
    private DoWhileTemplate initDoWhile( String style ){
        DoWhileTemplate res = new DoWhileTemplate();

        try{

            XPath path = XPathFactory.newInstance().newXPath();
            Node n = (Node)path.evaluate( "//CodeStyle[@name='" + style + "']", m_doc, XPathConstants.NODE );

            res.doToken = processEscape( path.evaluate( "DoWhile/@entry", n ) );
            res.blockStart = processEscape( path.evaluate( "DoWhile/Block/@start", n ) );
            res.blockEnd = processEscape( path.evaluate( "DoWhile/Block/@end", n ) );
            res.whileToken = processEscape( path.evaluate( "DoWhile/While", n ) );
            res.conditionStart = processEscape( path.evaluate("DoWhile/Condition/@start", n) );
            res.conditionEnd = processEscape( path.evaluate("DoWhile/Condition/@end", n ) );
            res.dowhileInsert = processEscape( path.evaluate("DoWhile/DoWhileInsert", n ) );
            res.bInverse = Boolean.valueOf( path.evaluate("DoWhile/While/@bInverse", n ) );
/*
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
*/
        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * Инициализация шаблона для конструкции While
     * на основе стиля style
     */
    private WhileTemplate initWhile( String style ){
        WhileTemplate res = new WhileTemplate();

        try{

            XPath path = XPathFactory.newInstance().newXPath();
            Node n = (Node)path.evaluate( "//CodeStyle[@name='" + style + "']", m_doc, XPathConstants.NODE );

            res.blockStart = processEscape( path.evaluate("While/Block/@start", n) );
            res.blockEnd = processEscape( path.evaluate("While/Block/@end", n) );
            res.whileToken = processEscape( path.evaluate("While/@entry", n) );
            res.conditionStart = processEscape( path.evaluate("While/Condition/@start", n ) );
            res.conditionEnd = processEscape( path.evaluate("While/Condition/@end", n ) );
            res.whileInsert = processEscape( path.evaluate("While/WhileInsert", n ) );
/*
            Node n = (Node)m_doc.selectSingleNode("//CodeStyle[@name='" + style + "']");
            res.blockStart = processEscape( n.selectSingleNode("While/Block/@start").getText() );
            res.blockEnd = processEscape( n.selectSingleNode("While/Block/@end").getText() );
            res.whileToken = processEscape( n.selectSingleNode("While/@entry").getText() );
            res.conditionStart = processEscape( n.selectSingleNode("While/Condition/@start").getText() );
            res.conditionEnd = processEscape( n.selectSingleNode("While/Condition/@end").getText() );
            res.whileInsert = processEscape( n.selectSingleNode("While/WhileInsert").getText() );
*/
        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * Инициализация шаблона для конструкции If
     * на основе стиля style
     */
    private IfTemplate initIf( String style ){
        IfTemplate res = new IfTemplate();

        try{

            XPath path = XPathFactory.newInstance().newXPath();
            Node n = (Node)path.evaluate( "//CodeStyle[@name='" + style + "']", m_doc, XPathConstants.NODE );
            res.ifToken = processEscape( path.evaluate("If/@entry", n ) );
            res.blockStart = processEscape( path.evaluate("If/Block/@start", n) );
            res.blockEnd = processEscape( path.evaluate("If/Block/@end", n) );
            res.conditionStart = processEscape( path.evaluate("If/Condition/@start", n) );
            res.conditionEnd = processEscape( path.evaluate("If/Condition/@end", n) );
            res.elseifToken = processEscape( path.evaluate("If/Elseif", n) );
            res.elseToken = processEscape( path.evaluate("If/Else", n) );
            res.ifInsert = processEscape( path.evaluate("If/IfInsert", n ) );
            res.ifelseInsert = processEscape( path.evaluate("If/IfElseInsert", n ) );
/*
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
*/
        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }

    /**
     * Инициализация шаблона для конструкций операторов выражений
     * на основе стиля style
     */
    private ExpressionTemplate initExpressions( String style ){
        ExpressionTemplate res = new ExpressionTemplate();

        try{
            XPath path = XPathFactory.newInstance().newXPath();
            Node n = (Node)path.evaluate( "//CodeStyle[@name='" + style + "']/Expressions", m_doc, XPathConstants.NODE );

            res.assign = processEscape( path.evaluate("Assign", n) );
            res.eq = processEscape( path.evaluate("Eq", n) );
            res.neq = processEscape( path.evaluate("Neq", n) );
            res.lt = processEscape( path.evaluate("Lt", n) );
            res.gt = processEscape( path.evaluate("Gt", n) );
            res.lte = processEscape( path.evaluate("Lte", n) );
            res.gte = processEscape( path.evaluate("Gte", n) );
            res.not = processEscape( path.evaluate("Not", n) );
            res.or = processEscape( path.evaluate("Or", n) );
            res.and = processEscape( path.evaluate("And", n) );
            res.mod = processEscape( path.evaluate("Mod", n) );
            res.arrIndexStart = processEscape( path.evaluate("ArrayIndex/@start", n) );
            res.arrIndexEnd = processEscape( path.evaluate("ArrayIndex/@end", n) );
            res.blockEnd = processEscape( path.evaluate("BlockEnd", n) );
            res.groupStart = processEscape( path.evaluate("Group/@start", n) );
            res.groupEnd = processEscape( path.evaluate("Group/@end", n) );
            res.singleQuote = processEscape( path.evaluate("Quotes/@single", n) );
            res.doubleQuote = processEscape( path.evaluate("Quotes/@double", n) );
            res.singleLineComment = processEscape( path.evaluate("Comment/@single", n) );
            res.multiLineCommentStart = processEscape( path.evaluate("Comment/@multiStart", n) );
            res.multiLineCommentEnd = processEscape( path.evaluate("Comment/@multiEnd", n) );
            res.singleLineCommentInsert = processEscape( path.evaluate("Comment/SingleInsert", n) );
            res.multiLineCommentInsert = processEscape( path.evaluate("Comment/MultilineInsert", n) );

            if( path.evaluate("UnaryAdd", n) != null )
                res.unaryAdd = path.evaluate("UnaryAdd", n);
            if( path.evaluate("UnaryAddOne", n) != null )
                res.unaryAddOne = path.evaluate("UnaryAddOne", n);
            if( path.evaluate("UnarySub", n) != null )
                res.unarySub = path.evaluate("UnarySub", n);
            if( path.evaluate("UnarySubOne", n) != null )
                res.unarySubOne = path.evaluate("UnarySubOne", n);
            if( path.evaluate("UnaryMul", n) != null )
                res.unaryMul = path.evaluate("UnaryMul", n);
            if( path.evaluate("UnaryDiv", n) != null )
                res.unaryDiv = path.evaluate("UnaryDiv", n);
/*
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
*/
			res.funcSqrt = path.evaluate("Function/Sqrt", n);
			res.funcSqrtIns = res.funcSqrt + safeSelectXPath( "Function/Sqrt/@suffix", n );
			res.funcSin = path.evaluate("Function/Sin", n);
			res.funcSinIns = res.funcSin + safeSelectXPath( "Function/Sin/@suffix", n );
			res.funcCos = path.evaluate("Function/Cos", n);
			res.funcCosIns = res.funcCos + safeSelectXPath( "Function/Cos/@suffix", n );
			res.funcTan = path.evaluate("Function/Tan", n);
			res.funcTanIns = res.funcTan + safeSelectXPath( "Function/Tan/@suffix", n );
			res.funcAsin = path.evaluate("Function/Asin", n);
			res.funcAsinIns = res.funcAsin + safeSelectXPath( "Function/Asin/@suffix", n );
			res.funcAcos= path.evaluate("Function/Acos", n);
			res.funcAcosIns = res.funcAcos + safeSelectXPath( "Function/Acos/@suffix", n );
			res.funcAtan = path.evaluate("Function/Atan", n);
			res.funcAtanIns = res.funcAtan + safeSelectXPath( "Function/Atan/@suffix", n );
			res.funcRound = path.evaluate("Function/Round", n);
			res.funcRoundIns = res.funcRound + safeSelectXPath( "Function/Round/@suffix", n );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * Инициализация списка ключевых слов
     * на основе стиля style
     */
    private Vector initKeywords( String style ){
        Vector res = new Vector();
        try{
            XPath path = XPathFactory.newInstance().newXPath();
            NodeList kwords = (NodeList)path.evaluate( "//CodeStyle[@name='" + style + "']/Keyword/@name", m_doc, XPathConstants.NODESET );
            for( int i = 0; i < kwords.getLength(); i++ ){
                res.addElement( kwords.item(i).getTextContent() );
            }
        }catch( Exception exc ){
            //exc.printStackTrace();
            return null;
        }
        return res;
    }

    /**
     * Получить список стилей кодирования
     */
    public String[] getStyles(){
        String []res = null;
        try{
            XPath path = XPathFactory.newInstance().newXPath();
            NodeList styleNodes = (NodeList)path.evaluate( "//CodeStyle/@name", m_doc, XPathConstants.NODESET );
            res = new String[ styleNodes.getLength() ];
            for( int i = 0; i < styleNodes.getLength(); i++ ){
                res[i] = styleNodes.item(i).getTextContent();
            }

        }catch( Exception exc ){
            //exc.printStackTrace();
        }
        return res;
    }

    /**
     * Получение имени стиля (на русском языке) по псевдониму стиля
     * (псевдоним указывается в атрибуте CodeStyle/@nameAlias)
     * @param aliasStyle название псевдонима
     * @return название стиля
     */
    public String getStyleByAlias( String aliasStyle ){
        String res = "";
        try{
            XPath path = XPathFactory.newInstance().newXPath();
            res = path.evaluate( "//CodeStyle[@aliasName='" + aliasStyle + "']/@name", m_doc );
        }catch( Exception exc ){
			System.out.println("Failed to TemplateFactory.getStyleByAlias( [" + aliasStyle + "] )" );
            exc.printStackTrace();
            return aliasStyle;
        }
        return res;
    }

    /**
     * Получение текущего псевдонима стиля (для передачи по сети)
     * @return псевдоним текущего стиля
     */
    public String getCurrentStyleAlias(){
        return m_currentStyleAlias;
    }

    /**
     * Переключение стиля и переинициализация шаблонов
     * на основе стиля style
     */
    public boolean switchStyle( String style ){
        try{
            XPath path = XPathFactory.newInstance().newXPath();

            m_bIsFree = Boolean.valueOf( path.evaluate("//CodeStyle[@name='" + style + "']/@isFree", m_doc) );
            m_currentStyleAlias = path.evaluate( "//CodeStyle[@name='" + style + "']/@aliasName", m_doc );

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
     * Получить шаблон (для текущего стиля)
     */
    public IfTemplate getIfTemplate(){
        return m_ifTpl;
    }
    /**
     * Получить шаблон (для текущего стиля)
     */
    public WhileTemplate getWhileTemplate(){
        return m_whileTpl;
    }
    /**
     * Получить шаблон (для текущего стиля)
     */
    public DoWhileTemplate getDoWhileTemplate(){
        return m_dowhileTpl;
    }
    /**
     * Получить шаблон (для текущего стиля)
     */
    public ExpressionTemplate getExpressionTemplate(){
        return m_expTpl;
    }
    /**
     * Получить текущий стиль
     */
    public String getCurrentStyle(){
        return m_currentStyle;
    }

    /**
     * Является ли выбранный стиль свободным?
     */
    public boolean isFree(){
        return m_bIsFree;
    }

	private String safeSelectXPath( String path, Node n ){

        XPath xpath = XPathFactory.newInstance().newXPath();
        try{
            if( xpath.evaluate(path, n) == null )
                return "";
            return xpath.evaluate(path, n);
        }catch( Exception exc ){

        }
        return "";
	}
}
