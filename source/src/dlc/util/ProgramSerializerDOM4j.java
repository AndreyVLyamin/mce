package dlc.util;

import org.dom4j.io.SAXReader;
import org.dom4j.Node;
import org.dom4j.DocumentHelper;

import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.StringReader;

import dlc.expression.Variable;
import dlc.expression.VariableArray;

/**
 * $Id: ProgramSerializer.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий методы сериализации/десериализации состояния ВЛР.
 */
public class ProgramSerializerDOM4j {
    /**
     * Метод для десериализации состояния ВЛР, полученного с сервера
     * result.get("STYLE") = style
     * result.get("CODE") = code listing
     * result.get("VARS") = vars (hashtable)
     */
    public static boolean deserialize( String instructions, Hashtable result ){
        try{

			String code = instructions;
            code = code.replace( "%26", "&" );
            code = code.replace( "%3B", ";" );
            code = code.replace( "%3F", "?" );
            code = code.replace( "%20", " " );
            code = code.replace( "%23", "#" );
            code = code.replace( "%2F", "/" );
            code = code.replace( "%5B", "[" );
            code = code.replace( "%5D", "]" );
            code = code.replace( "%7B", "{" );
            code = code.replace( "%7D", "}" );
            code = code.replace( "%3D", "=" );
            code = code.replace( "%3D", "=" );
			code = code.replace( "%2B", "+" );
            code = code.replace( "%0D%0A", "\r\n" );
            code = code.replace( "%0D", "\r\n" );
            code = code.replace( "%0A", "\r\n" );
			instructions = code;

            VariableContainer vars = new VariableContainer();

            System.out.println("Instructions[unescaped]:\n" + instructions + "\n======================" );

			//todo: proceed non-unicode chars
			StringBuffer modInstr = new StringBuffer();
			for( int i = 0; i < instructions.length(); i++ ){
				if( instructions.charAt(i) == 0x12 ) modInstr.append( " " );
				else modInstr.append( instructions.charAt(i) );
			}
			instructions = modInstr.toString();
			//////////////////////////////

			if( !(instructions.indexOf( "<?xml version=" ) >= 0 && instructions.indexOf( "Windows-1251" ) > 0) )
	            instructions = HtmlParamEscaper.unescapeParam( instructions ).trim();

			if( !(instructions.indexOf( "<?xml version=" ) >= 0 && instructions.indexOf( "Windows-1251" ) > 0) )
	            instructions = HtmlParamEscaper.unescapeParam( instructions ).trim();

//            while( instructions.startsWith("&amp;") || instructions.startsWith("&lt;") )
//                instructions = HtmlParamEscaper.unescapeParam( instructions );
            System.out.println("Instructions:\n" + modInstr.toString() + "\n======================" );

            SAXReader r = new SAXReader();
			r.setEncoding( "Windows-1251" );
            org.dom4j.Document doc = r.read( new StringReader(instructions.toString()) );

            List variables = doc.selectNodes( "//Variable" );
            for( Iterator it = variables.iterator(); it.hasNext(); ){
                Node n = (Node)it.next();
                int iType = Integer.parseInt( n.selectSingleNode("Type/comment()").getText() );
                String varName = HtmlParamEscaper.unescapeParam( n.selectSingleNode("Name/comment()").getText() );

                boolean bInput = Boolean.parseBoolean( n.selectSingleNode("@input").getText() );
                boolean bOutput = Boolean.parseBoolean( n.selectSingleNode( "@output").getText() );
                boolean bInner = Boolean.parseBoolean( n.selectSingleNode("@inner").getText() );

                Variable var = null;
                if( iType >= Variable.TYPE_ARR_INT ){
                    var = new VariableArray( varName, iType );
                    var.bInput = bInput;
                    var.bOutput = bOutput;
                    var.bInner = bInner;
                    List values = n.selectNodes( "Value" );
                    for( Iterator it2 = values.iterator(); it2.hasNext(); ){
                        Node vNode = (Node)it2.next();
                        String val = "";
                        if( vNode.selectSingleNode("comment()") != null )
                            val = vNode.selectSingleNode("comment()").getText();
                        ((VariableArray)var).addElement( HtmlParamEscaper.unescapeParam(val) );
                    }
                }
                else{
                    String val = "";
                    if( n.selectSingleNode("Value/comment()") != null )
                        val = n.selectSingleNode("Value/comment()").getText();
                    String defValue = HtmlParamEscaper.unescapeParam( n.selectSingleNode("Value/comment()").getText() );
                    var = new Variable( varName, bInput, bOutput, bInner, iType, defValue );
                    try{
                        var.init();
                    }catch( Exception initExc ){
                        //System.out.println("Init " + varName + " FAILED" );
                    }
                }
                vars.put( varName, var );
            }

            Node codeNode = doc.selectSingleNode( "//ProgramEnvironment/Code");
            result.put( "STYLE", codeNode.selectSingleNode( "@style" ).getText() );
            result.put( "VARS", vars );
            try{
                String sCode = HtmlParamEscaper.unescapeParam(codeNode.selectSingleNode("comment()").getText());
                result.put( "CODE", sCode );
            }catch( Exception codeExc ){
                result.put( "CODE", "" );
            }

            return true;
        }catch( Exception exc ){
exc.printStackTrace();
            System.out.println("ERROR: ProgramSerializer.deserialize() FAILED: " + exc.getMessage() );
        }

        return false;
    }

	/**
	 * Временное решение - проверка всех символов на unicode
	 */
	public static String checkForUnicode( String in ){

		String answerMatch = "([a-z]|[A-Z]|[ ]|[\\.]|[\\,]|[\\:]|[\\;]|[0-9]|[\\[]|[\\]]|[\\{]|[\\}]|[\\=]|[\\-]|[*]|[\\+])*";
		if( !in.matches( answerMatch ) ){
System.out.println("NOT MATCH: " + in );
			return "?";
		}
		return in;
	}

    /**
     * Метод для сериализации состояния ВЛР
     * @param style стиль кодирования
     * @param code исходный код
     * @param vars переменные программы
     * @return строка, содержащая сериализованное состояния программы
     */
    public static String serialize( String style, String code, VariableContainer vars ){
        String NL = "\r\n";
        StringBuffer res = new StringBuffer( "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" + NL );

        res.append( "<ProgramEnvironment>" + NL );

        try{
            for( Enumeration en = vars.keys(); en.hasMoreElements(); ){
                String varName = (String)en.nextElement();
                Variable var = (Variable)vars.get( varName );

                res.append( "<Variable input=\"" + var.bInput + "\" output=\"" + var.bOutput + "\" inner=\"" + var.bInner + "\">" + NL + "<Name><!--" +
                        HtmlParamEscaper.escapeParam(varName) + "--></Name>" + NL );

                res.append( "<Type><!--" + var.iType + "--></Type>" + NL );

                if( var instanceof VariableArray ){
                    VariableArray arr = (VariableArray)var;
                    for( int i = 0; i < arr.elems.size(); i++ ){

//todo: just debug - should process nonunicode chars
						String arrValue = "" + arr.elementAt(i);
//						arrValue = checkForUnicode( arrValue );
                        res.append( "<Value N=\"" + i + "\"><!--" +
                                HtmlParamEscaper.escapeParam( "" + arrValue ) + "--></Value>" + NL );
                    }
                }
                else{
//todo: just debug - should process nonunicode chars
					String sValue = "" + var.sDef;
//					sValue = checkForUnicode( sValue );
                    res.append( "<Value><!--" + HtmlParamEscaper.escapeParam( "" + sValue ) + "--></Value>" + NL );
                }
                res.append( "</Variable>" + NL );
            }

            res.append( "<Code style=\"" + style + "\"><!--" );
            res.append( HtmlParamEscaper.escapeParam(code) );
            res.append( "--></Code>" + NL );
            res.append( "</ProgramEnvironment>" + NL );

            res = new StringBuffer( HtmlParamEscaper.escapeParam( res.toString(), true ) );
        }catch( Exception exc ){
            //exc.printStackTrace();
        }
        return res.toString();
    }
}
