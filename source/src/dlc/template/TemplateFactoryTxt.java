package dlc.template;

//import org.dom4j.*;
//import org.dom4j.io.*;
import java.util.*;
import java.io.*;

/**
 * $Id: TemplateFactory.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, фабрику стилей кодирования. Информация о стилях кодирования читается из внешнего XML-файла конфигурации
 */
public class TemplateFactoryTxt implements TemplateFactory{

	String m_confPath;
    DoWhileTemplate m_dowhileTpl;
    WhileTemplate   m_whileTpl;
    IfTemplate      m_ifTpl;
    ExpressionTemplate m_expTpl;

//	Document m_doc;

    Vector keywords = new Vector();

    String          m_currentStyle = "";
    String          m_currentStyleAlias = "";
    boolean         m_bIsFree = false;

	class StyleDesc{
		public String styleName = "";
		public String styleAlias = "";
		public String isFree = "";
		public int    startLine = 0;

		public StyleDesc( String name, String isFree, String alias, int line ){
			styleName = name;
			styleAlias = alias;
			startLine = line;
			this.isFree = isFree;
		}
	}

	HashMap<String, StyleDesc> m_styles = new HashMap<String, StyleDesc>();

    /**
     * Конструктор
     * @param confPath путь к файлу конфигурации
     */
    public TemplateFactoryTxt( String confPath ){
        try{

			m_confPath = confPath;

System.out.println("confPath: " + confPath );

            InputStream is = getClass().getClassLoader().getResourceAsStream( confPath );

			LineNumberReader reader = new LineNumberReader( new InputStreamReader( is ) );
			String tmp = "";
			StyleDesc currDesc = null;

			final int FIND_BEGIN = 0;
			final int FIND_END = 3;

			int state = FIND_BEGIN;

			while( (tmp = reader.readLine()) != null ){
				if( state == FIND_BEGIN && tmp.equals("code.style.begin") ){
					int startLine = reader.getLineNumber();
					currDesc = new StyleDesc(
						reader.readLine(), 
						reader.readLine(), 
						reader.readLine(), 
						startLine
					);
					state = FIND_END;
				}else if( state == FIND_END && tmp.equals("code.style.end") ){
					m_styles.put( currDesc.styleName, currDesc );
					state = FIND_BEGIN;
				}
			}
			reader.close();

System.out.println( "Find " + m_styles.size() + " styles" );

        }catch( Exception exc ){
            exc.printStackTrace();
			System.out.println("TemplateFactoryTxt.ctor FAILED: " + exc.getMessage() );
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
    private DoWhileTemplate initDoWhile( LineNumberReader reader, String endToken ){

		String style = "tmp";

        DoWhileTemplate res = new DoWhileTemplate();

        try{
/*
					ps.println( getValue( expNode, "@entry" ) );
					ps.println( getValue( expNode, "DoWhileInsert" ) );
					ps.println( getValue( expNode, "Block/@start" ) );
					ps.println( getValue( expNode, "Block/@end" ) );
					ps.println( getValue( expNode, "While/@bInverse" ) );
					ps.println( getValue( expNode, "While" ) );
					ps.println( getValue( expNode, "Condition/@start" ) );
					ps.println( getValue( expNode, "Condition/@end" ) );
*/

			res.doToken = processEscape( reader.readLine() );
			res.dowhileInsert = processEscape( reader.readLine() );
			res.blockStart = processEscape( reader.readLine() );
			res.blockEnd = processEscape( reader.readLine() );
			res.bInverse = Boolean.valueOf( processEscape( reader.readLine() ) );
			res.whileToken = processEscape( reader.readLine() );
			res.conditionStart = processEscape( reader.readLine() );
			res.conditionEnd = processEscape( reader.readLine() );

			String lastLine = reader.readLine();
			System.out.println( "initDoWhile [" + lastLine + "]" );
/*

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
    private WhileTemplate initWhile( LineNumberReader reader, String endToken ){

		String style = "tmp";

        WhileTemplate res = new WhileTemplate();

        try{
/*
					ps.println( getValue( expNode, "@entry" ) );
					ps.println( getValue( expNode, "WhileInsert" ) );
					ps.println( getValue( expNode, "Condition/@start" ) );
					ps.println( getValue( expNode, "Condition/@end" ) );
					ps.println( getValue( expNode, "Block/@start" ) );
					ps.println( getValue( expNode, "Block/@end" ) );
*/
			res.whileToken = processEscape( reader.readLine() );
			res.whileInsert = processEscape( reader.readLine() );
			res.conditionStart = processEscape( reader.readLine() );
			res.conditionEnd = processEscape( reader.readLine() );
			res.blockStart = processEscape( reader.readLine() );
			res.blockEnd = processEscape( reader.readLine() );

			String lastLine = reader.readLine();
			System.out.println( "initWhile [" + lastLine + "]" );
						

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
    private IfTemplate initIf( LineNumberReader reader, String endToken ){
        IfTemplate res = new IfTemplate();

        try{
/*
					ps.println( getValue( expNode, "@entry") );
					ps.println( getValue( expNode, "IfInsert") );
					ps.println( getValue( expNode, "IfElseInsert") );
					ps.println( getValue( expNode, "Condition/@start" ) );
					ps.println( getValue( expNode, "Condition/@end" ) );
					ps.println( getValue( expNode, "Block/@start") );
					ps.println( getValue( expNode, "Block/@end") );
					ps.println( getValue( expNode, "Elseif") );
					ps.println( getValue( expNode, "Else") );
*/
			res.ifToken = processEscape( reader.readLine() );
			res.ifInsert = processEscape( reader.readLine() );
			res.ifelseInsert = processEscape( reader.readLine() );
			res.conditionStart = processEscape( reader.readLine() );
			res.conditionEnd = processEscape( reader.readLine() );
			res.blockStart = processEscape( reader.readLine() );
			res.blockEnd = processEscape( reader.readLine() );
			res.elseifToken = processEscape( reader.readLine() );
			res.elseToken = processEscape( reader.readLine() );

			String lastLine = reader.readLine();
			System.out.println( "initIf [" + lastLine + "]" );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }

    /**
     * Инициализация шаблона для конструкций операторов выражений
     * на основе стиля style
     */
    private ExpressionTemplate initExpressions( LineNumberReader reader, String endToken ){

		String style = "tmp";

        ExpressionTemplate res = new ExpressionTemplate();

        try{

/*
					ps.println( getValue( expNode, "Assign" ) );
					ps.println( getValue( expNode, "Eq" ) );
					ps.println( getValue( expNode, "Neq" ) );
					ps.println( getValue( expNode, "Lt" ) );
					ps.println( getValue( expNode, "Gt" ) );
					ps.println( getValue( expNode, "Lte" ) );
					ps.println( getValue( expNode, "Gte" ) );
					ps.println( getValue( expNode, "Not" ) );
					ps.println( getValue( expNode, "Or" ) );
					ps.println( getValue( expNode, "And" ) );
					ps.println( getValue( expNode, "Mod" ) );
					ps.println( getValue( expNode, "Comment/@single" ) );
					ps.println( getValue( expNode, "Comment/@multiStart" ) );
					ps.println( getValue( expNode, "Comment/@multiEnd" ) );
					ps.println( getValue( expNode, "Comment/SingleInsert" ) );
					ps.println( getValue( expNode, "Comment/MultilineInsert" ) );
					ps.println( getValue( expNode, "Group/@start" ) );
					ps.println( getValue( expNode, "Group/@end" ) );
					ps.println( getValue( expNode, "ArrayIndex/@start" ) );
					ps.println( getValue( expNode, "ArrayIndex/@end" ) );
					ps.println( getValue( expNode, "BlockEnd" ) );
					ps.println( getValue( expNode, "Quotes/@single" ) );
					ps.println( getValue( expNode, "Quotes/@double" ) );
					ps.println( "funcs.begin" );
					List<Node> funcNodes = expNode.selectNodes( "Function/*" );
					for( Node funcNode : funcNodes ){
						ps.println( getNodeText( funcNode ) );
						ps.println( getValue( funcNode, "@suffix" ) );
					}
					ps.println( "funcs.end" );
*/			

			res.assign = processEscape( reader.readLine() );
			res.eq = processEscape( reader.readLine() );
			res.neq = processEscape( reader.readLine() );
			res.lt = processEscape( reader.readLine() );
			res.gt = processEscape( reader.readLine() );
			res.lte = processEscape( reader.readLine() );
			res.gte = processEscape( reader.readLine() );
			res.not = processEscape( reader.readLine() );
			res.or = processEscape( reader.readLine() );
			res.and = processEscape( reader.readLine() );
			res.mod = processEscape( reader.readLine() );
			res.singleLineComment = processEscape( reader.readLine() );
			res.multiLineCommentStart = processEscape( reader.readLine() );
			res.multiLineCommentEnd = processEscape( reader.readLine() );
			res.singleLineCommentInsert = processEscape( reader.readLine() );
			res.multiLineCommentInsert = processEscape( reader.readLine() );
			res.groupStart = processEscape( reader.readLine() );
			res.groupEnd = processEscape( reader.readLine() );
			res.arrIndexStart = processEscape( reader.readLine() );
			res.arrIndexEnd = processEscape( reader.readLine() );
			res.blockEnd = processEscape( reader.readLine() );
			res.singleQuote = processEscape( reader.readLine() );
			res.doubleQuote = processEscape( reader.readLine() );

			String tmp = "";
			while( (tmp = reader.readLine()) != null && !tmp.equals("funcs.begin") );

			res.funcSqrt = processEscape( reader.readLine() );
			res.funcSqrtIns = res.funcSqrt + processEscape( reader.readLine() );
			res.funcSin = processEscape( reader.readLine() );
			res.funcSinIns = res.funcSin + processEscape( reader.readLine() );
			res.funcCos = processEscape( reader.readLine() );
			res.funcCosIns = res.funcCos + processEscape( reader.readLine() );
			res.funcTan = processEscape( reader.readLine() );
			res.funcTanIns = res.funcTan + processEscape( reader.readLine() );
			res.funcAsin = processEscape( reader.readLine() );
			res.funcAsinIns = res.funcAsin + processEscape( reader.readLine() );
			res.funcAcos = processEscape( reader.readLine() );
			res.funcAcosIns = res.funcAcos + processEscape( reader.readLine() );
			res.funcAtan = processEscape( reader.readLine() );
			res.funcAtanIns = res.funcAtan + processEscape( reader.readLine() );
			res.funcRound = processEscape( reader.readLine() );
			res.funcRoundIns = res.funcRound + processEscape( reader.readLine() );

			String lastLine = reader.readLine();
			System.out.println( "initExpressions [" + lastLine + "]" );

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
*/

        }catch( Exception exc ){
            //exc.printStackTrace();
        }

        return res;
    }
    /**
     * Инициализация списка ключевых слов
     * на основе стиля style
     */
    private Vector initKeywords( LineNumberReader reader, String endToken ){
        Vector res = new Vector();

		try{
			String tmp = "";
			while( (tmp = reader.readLine()) != null && !tmp.equals(endToken) ){
				res.addElement( tmp );
			}
		}catch( Exception exc ){
			//exc.printStackTrace();
		}

        return res;
    }

    /**
     * Получить список стилей кодирования
     */
    public String[] getStyles(){

		String []res = new String[ m_styles.size() ];
		int i = 0;
		for( String key : m_styles.keySet() ){
			res[i] = key;
			i++;
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

		for( Map.Entry<String, StyleDesc> e : m_styles.entrySet() ){
			StyleDesc desc = e.getValue();
			if( desc.styleAlias.equals(aliasStyle) )
				return e.getKey();
		}
        return "";
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

			StyleDesc desc = m_styles.get( style );
			m_bIsFree = Boolean.valueOf( desc.isFree );
			m_currentStyleAlias = desc.styleAlias;

            InputStream is = getClass().getClassLoader().getResourceAsStream( m_confPath );
			LineNumberReader reader = new LineNumberReader( new InputStreamReader( is ) );

			//search style:
			while( reader.getLineNumber() <= desc.startLine )
				reader.readLine();

			String tmp = "";
			while( (tmp = reader.readLine()) != null && !tmp.equals("keywords.begin") );
			keywords = initKeywords( reader, "keywords.end" );

			if( !m_bIsFree ){
    			while( (tmp = reader.readLine()) != null && !tmp.equals("if.begin") );
    			m_ifTpl = initIf( reader, "if.end" );

    			while( (tmp = reader.readLine()) != null && !tmp.equals("while.begin") );
    			m_whileTpl = initWhile( reader, "while.end" );

    			while( (tmp = reader.readLine()) != null && !tmp.equals("dowhile.begin") );
    			m_dowhileTpl = initDoWhile( reader, "dowhile.end" );
    			
    			while( (tmp = reader.readLine()) != null && !tmp.equals("exp.begin") );
    			m_expTpl = initExpressions( reader, "exp.end" );
			}else{
                m_dowhileTpl = null;
                m_whileTpl = null;
                m_ifTpl = null;
                m_expTpl = null;
            }
            m_currentStyle = style;

			reader.close();

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
/*
	private String safeSelectXPath( String path, Node n ){
		Node res = n.selectSingleNode( path );
		try{
			return res.getText();
		}catch( Exception exc ){
		}
		return "";
	}
*/
}
