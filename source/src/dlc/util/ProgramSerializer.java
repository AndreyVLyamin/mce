package dlc.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

import dlc.expression.Variable;
import dlc.expression.VariableArray;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

/**
 * $Id: ProgramSerializer2.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ������ ������������/�������������� ��������� ���.
 */
public class ProgramSerializer {
    /**
     * ����� ��� �������������� ��������� ���, ����������� � �������
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
/*
            SAXReader r = new SAXReader();
			r.setEncoding( "Windows-1251" );
            org.dom4j.Document doc = r.read( new StringReader(instructions.toString()) );
*/
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                 new ByteArrayInputStream( instructions.toString().getBytes() )
            );

            XPath xpath = XPathFactory.newInstance().newXPath();
            XPath tmpXPath = XPathFactory.newInstance().newXPath();

            NodeList variables = (NodeList)xpath.evaluate( "//Variable", doc, XPathConstants.NODESET );
            for( int i = 0; i < variables.getLength(); i++ ){
                Node n = variables.item(i);

                int iType = Integer.parseInt( tmpXPath.evaluate( "Type/comment()", n ) );
                String varName = HtmlParamEscaper.unescapeParam(
                        tmpXPath.evaluate( "Name/comment()", n ) );

                boolean bInput = Boolean.parseBoolean( tmpXPath.evaluate( "@input", n ) );
                boolean bOutput = Boolean.parseBoolean( tmpXPath.evaluate( "@output", n) );
                boolean bInner = Boolean.parseBoolean( tmpXPath.evaluate("@inner", n) );

                Variable var = null;
                if( iType >= Variable.TYPE_ARR_INT ){
                    var = new VariableArray( varName, iType );
                    var.bInput = bInput;
                    var.bOutput = bOutput;
                    var.bInner = bInner;
                    NodeList values = (NodeList)tmpXPath.evaluate( "Value", n, XPathConstants.NODESET );
                    for( int j = 0; j < values.getLength(); j++ ){
                        Node vNode = values.item(j);
                        String val = "";
                        //TODO: WARNING using tmpXPath inside a loop with tmpXPath
                        if( tmpXPath.evaluate("comment()", vNode ) != null )
                            val = tmpXPath.evaluate("comment()", vNode);
                        ((VariableArray)var).addElement( HtmlParamEscaper.unescapeParam(val) );
                    }
                }
                else{
                    String val = "";
                    if( tmpXPath.evaluate("Value/comment()", n) != null )
                        val = tmpXPath.evaluate("Value/comment()", n);
                    String defValue = HtmlParamEscaper.unescapeParam( tmpXPath.evaluate("Value/comment()", n) );
                    var = new Variable( varName, bInput, bOutput, bInner, iType, defValue );
                    try{
                        var.init();
                    }catch( Exception initExc ){
                        //System.out.println("Init " + varName + " FAILED" );
                    }
                }
                vars.put( varName, var );
            }

            Node codeNode = (Node)tmpXPath.evaluate( "//ProgramEnvironment/Code", doc, XPathConstants.NODE );
            result.put( "STYLE", tmpXPath.evaluate( "@style", codeNode ) );
            result.put( "VARS", vars );
            try{
                String sCode = HtmlParamEscaper.unescapeParam(tmpXPath.evaluate("comment()", codeNode));
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
	 * ��������� ������� - �������� ���� �������� �� unicode
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
     * ����� ��� ������������ ��������� ���
     * @param style ����� �����������
     * @param code �������� ���
     * @param vars ���������� ���������
     * @return ������, ���������� ��������������� ��������� ���������
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
