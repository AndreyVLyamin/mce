package AbsCodeServer;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.Hashtable;

import org.dom4j.*;
import org.dom4j.io.*;
import dlc.util.ProgramSerializerDOM4j;
import dlc.util.HtmlParamEscaper;
import dlc.util.VariableContainer;

/**
 * Класс, реализующий разбор состояния ВЛР, отправленной с клиента в сериализованном виде.
 */
public class ReqParser2 {

    /**
     * Метод для разбора программы, полученной с клиента
     * @param reqXML код программы в формате XML
     * @return Экземпляр класса Program
     */
    public Program parse( String reqXML ){
        Program res = null;

        try{

//System.out.println("=========Parse program code==========>\n" + reqXML + "\n===========[EnD]===========");

            SAXReader r = new SAXReader();
            org.dom4j.Document doc = r.read( new StringReader(reqXML) );

/*
<?xml version="1.0" encoding="Windows-1251"?>
<!DOCTYPE Request SYSTEM "http://de.ifmo.ru/--DTD/Request.dtd">
<Request>
<Conditions>
<ConditionForChecking id="1" Time="5">
  <Input><!--cohfuncnoice=0.09;intervalnoice=0.09;cohcount=2--></Input>
  <Output><!-- dummy --></Output>
</ConditionForChecking>
</Conditions>
<Instructions>
<!--&lt;?xml version=&quot;1.0&quot; encoding=&quot;Cp1251&quot;?&gt;
&lt;ProgramEnvironment&gt;
&lt;Code style=&quot;?????????&quot;&gt;&lt;!&amp;minus;&amp;minus;asdadasdasdasdasd&amp;minus;&amp;minus;&gt;&lt;/Code&gt;
&lt;/ProgramEnvironment&gt;
--></Instructions>
</Request>
*/
//            System.out.println( "InXML:\n" + reqXML );
            res = new Program();

            List nodes = doc.selectNodes("//ConditionForChecking");

            for( Iterator it = nodes.iterator(); it.hasNext(); ){
                Node n = (Node)it.next();

				String input = n.selectSingleNode("Input/comment()").getText();
				String output = n.selectSingleNode("Output/comment()").getText();
				input = input.replaceAll("&#0045;", "-" ).
					replaceAll("&amp;", "&");
				output = output.replaceAll("&#0045;", "-" ).
					replaceAll("&amp;", "&");

                ConditionForChecking cfc = new ConditionForChecking();
                cfc.setID( Integer.parseInt( n.selectSingleNode("@id").getText() ) );
                cfc.setTime( Long.parseLong( n.selectSingleNode("@Time").getText() ) );
                cfc.setInput( input );
                cfc.setOutput( output );
                res.addCondition( cfc );
            }

            if( nodes.size() == 0 ){
                throw new Exception( "ReqParser2.parse() FAILED - no ConditionForChecking received");
            }

            Hashtable deserialized = new Hashtable();
            String    instructions = "";
            try{
				instructions = doc.selectSingleNode("//Instructions/comment()").getText();
				instructions.replaceAll("&#0045;", "-").replaceAll("&amp;", "&");
            }catch( Exception instrExc ){}
            /**
             * Требуется двойной вызов unescape, т.к. в случае с параметром аплета,
             * один из unescape проводит сам обозреватель
             */


            ProgramSerializerDOM4j.deserialize( HtmlParamEscaper.unescapeParam(instructions), deserialized );
/*
            if( deserialized.size() != 3 ||
                    deserialized.get("CODE") == null ||
                    deserialized.get("VARS") == null ||
                    deserialized.get("STYLE") == null ){
                throw new Exception( "ReqParser2.parse() FAILED to get CODE, VARS or STYLE");
            }
*/
//System.out.println("ReqParser2.parse(), VARS=" + deserialized.get("VARS") );

System.out.println("===============CODE================");
System.out.println( deserialized.get("CODE" ) );
System.out.println("===============CODE=====^^^^^======");

            res.setCode( (String)deserialized.get("CODE") );
            res.setVars( (VariableContainer)deserialized.get("VARS") );
            res.setStyle( (String)deserialized.get("STYLE") );

        }catch( Exception exc ){

            exc.printStackTrace();

            System.out.println("ERROR: ReqParser2.parse() FAILED: " + exc.getMessage() );
            return null;
        }
        return res;
    }
}
