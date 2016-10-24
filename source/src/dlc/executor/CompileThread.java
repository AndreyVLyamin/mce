package dlc.executor;

import dlc.codenodes.CodeRuntime;
import dlc.codenodes.CodeNode;
import dlc.codenodes.CodeBlockNode;
import dlc.codenodes.CodeJumpNode;
import dlc.util.VariableContainer;
import dlc.code.CodeTokenizer;
import dlc.code.CompileException;
import dlc.template.TemplateFactory;

import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * $Id: CompileThread.java,v 1 2007/04/10
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ����� ���������� ���������.
 */
public class CompileThread extends Thread {
    boolean   m_bStop = false;
    Vector    m_codeNodes;
    Exception m_exception;
    VariableContainer m_vars;
    ActionListener    m_listener;
    TemplateFactory   m_codeTemplateFactory;
    String            m_code;

    /**
     * �����������
     * @param codeStr �������� ����� ���������
     * @param codeTemplateFactory ��������� ���������� ����� �����������
     * @param codeNodes �������� ������ ����������
     * @param vars ���������� ���������
     * @param listener ��������� ������� �� �������� ����������
     */
    public CompileThread( String codeStr, TemplateFactory codeTemplateFactory, Vector codeNodes, VariableContainer vars, ActionListener listener ){
        m_codeTemplateFactory = codeTemplateFactory;
        m_codeNodes = codeNodes;
        m_vars = vars;
        m_listener = listener;
        m_code = codeStr;
    }

    /**
     * �����, ����������� ��������� ���� ������
     */
    public void run(){
        try{
            m_codeNodes.clear();

            CodeTokenizer ct = new CodeTokenizer( m_codeTemplateFactory );
            long []times = new long[]{
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
            };

            Vector tokens = ct.parse2( m_code );
            CodeBlockNode _code = ct.parseCodeTokens( new VariableContainer(m_vars), tokens, new Vector(), new int[1] );

            times[1] = System.currentTimeMillis() - times[0];

            if( _code != null ){
                //������� ����������

                Vector linearCode = _code.getLinearCode();
                for( int i = 0; i < linearCode.size(); i++ ){
                    if( linearCode.elementAt(i) instanceof CodeJumpNode ){
                        CodeJumpNode cjn = (CodeJumpNode)linearCode.elementAt(i);
                    }
                }

                m_codeNodes.addAll( linearCode );
            }

            if( m_codeNodes == null || m_codeNodes.size() == 0 ){
                throw new CompileException("��������� �� �������� ����������", 0, 0, 0 );
            }

            times[2] = System.currentTimeMillis() - times[0];
        }catch( Exception exc ){
            m_exception = exc;
        }
        if( !m_bStop )
            m_listener.actionPerformed( new ActionEvent(this, 0, "") );

        m_bStop = true;
    }
    /**
     * �������������� ��������� ������
     */
    public void stopThread(){
        m_bStop = true;
    }

    /**
     * ����� ��� ��������� ���������� � ���� ���������� ���������
     * @return null ��� ��������� Exception, ������������ � �������� ����������
     */
    public Exception getException(){
        return m_exception;
    }
}
