import dlc.code.CodeTokenizer;
import dlc.code.CompileException;
import dlc.codenodes.CodeBlockNode;
import dlc.codenodes.CodeJumpNode;
import dlc.codenodes.CodeNode;
import dlc.codenodes.CodeRuntime;
import dlc.expression.Variable;
import dlc.template.TemplateFactory;
import dlc.template.TemplateFactoryXml2;
import dlc.util.VariableContainer;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by LAV on 22.10.2016.
 */
public class MainTester {


    public static void main(String[] args) {
        Exception m_exception;

//            Vector m_codeNodes = new Vector(  );

        TemplateFactory m_codeTemplateFactory;
        VariableContainer m_vars = new VariableContainer();

        m_codeTemplateFactory = new TemplateFactoryXml2( "conf-new.xml" );
        m_codeTemplateFactory.switchStyle( "C" );

        String iVar = "i";
        Variable vVar = new Variable( "i", true, true, false, Variable.TYPE_INT, "0" );
        try {
            vVar.setValue( new Integer(3) );
        } catch (Exception e) {
            e.printStackTrace();
        }

        m_vars.put( iVar, vVar );

        String m_code = "if((i % 2) != 0)\r\n" +
                        "{i=0;};\r\n" +
                        "else\r\n" +
                        "{i=1;};\r\n";
//            String m_code = "";
        System.out.println( m_code );
        Vector m_codeNodes = new Vector();

        try {
            m_codeNodes.clear();

            CodeTokenizer ct = new CodeTokenizer( m_codeTemplateFactory );

            Vector tokens = ct.parse2( m_code );
            CodeBlockNode _code = ct.parseCodeTokens( new VariableContainer( m_vars ), tokens, new Vector(), new int[1] );

            if (_code != null) {
                //Входные переменные

                Vector linearCode = _code.getLinearCode();

                m_codeNodes.addAll( linearCode );
            }

            if (m_codeNodes == null || m_codeNodes.size() == 0) {
                throw new CompileException( "Программа не содержит инструкций", 0, 0, 0 );
            }

        } catch (Exception exc) {
            m_exception = exc;
            System.out.println( exc.toString() );
            exc.printStackTrace();
        }

        System.out.println( "Size: " + m_codeNodes.size() );

        Iterator it = m_codeNodes.iterator();

        System.out.println( "Code Nodes :" );
        while (it.hasNext())
            System.out.println( "" + it.next() );

        int m_sleepAmount = 1;

        int loopCount = 0;

        CodeRuntime m_runtime = new CodeRuntime();

        try {
            CodeNode currentNode = null;

            System.out.println( "In ProgramThread.run..." );
            System.out.println( "Total nodes: " + m_codeNodes.size() );
            System.out.println( "Sleep amount: " + m_sleepAmount );


            while (m_runtime.IP < m_codeNodes.size()) {
                currentNode = (CodeNode) m_codeNodes.elementAt( m_runtime.IP );

                loopCount++;

                currentNode.execute( m_vars, m_runtime );

            }
        } catch (Exception exc) {
            m_exception = exc;
            System.out.println( exc.toString() );
            exc.printStackTrace();
        }

        System.out.println( "Total loop count: " + loopCount );

        Variable var = null;
        var = (Variable) m_vars.get( "i" );
        System.out.println( "Результат: " + var.value );

/*
        Character c1, c2;
        c1 = 'к';
        c2 = 'В';
        System.out.println( "Логическое выражение : " + c1.equals( c2 ));
*/

    }

}

