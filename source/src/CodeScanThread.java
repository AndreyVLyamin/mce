
import java.lang.*;
import java.util.Vector;
import java.util.Hashtable;

import dlc.code.*;
import dlc.template.*;
import dlc.util.VariableContainer;

/**
 * $Id: CodeScanThread.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ����� ������������ ��������� ���� �� ������� ������ �� �����
 * ��������������.
 */
public class CodeScanThread extends Thread{

    private boolean        m_bStopped = false;
    private SyntaxDocument m_doc;
    private ErrorLog       m_log;
    private VariableContainer m_vars;
    private TemplateFactory   m_codeTemplateFactory;

	/**
	 * �����������.
	 * @param doc �������� � ���������� ����������
     * @param log ������ ��� �������� ��������� ������
	 * @param codeVars ������� ������ ����������, �������� � ��������� (��� �������� ����������)
	 */
    public CodeScanThread( SyntaxDocument doc, ErrorLog log, VariableContainer codeVars ){
        m_doc = doc;
        m_log = log;
        m_vars = codeVars;
    }

	/**
	 * �������� ���� ������ - ������������ ���������� ��� �� ���������� � ��������� ������ (������ - 1.5 �������)
	 */
    public void run(){
        while( !m_bStopped ){
            try{

                final long CHECK_TIME = 500;

                if( System.currentTimeMillis() - m_doc.getLastModifyTime() > CHECK_TIME ){

                    if( m_codeTemplateFactory == null ){
                        System.out.println( "CodeTemplateFactory == null!!! Cant check program!" );
                    }
                    else{
                        CodeTokenizer ct = new CodeTokenizer( m_codeTemplateFactory );
                        try{
                            Vector tokens = ct.parse2( m_doc.getText( 0, m_doc.getLength() ) );
                            ct.parseCodeTokens( new VariableContainer(m_vars), tokens, new Vector(), new int[1] );
                            m_doc.resetErrors();
                            m_log.showMessage( " " );
                        }catch( CompileException ce ){
                            System.out.println("ERROR: compile time exception: " + ce.getMessage() );
                            m_doc.highlightError( ce );
                            m_log.showError( ce.getMessage() );
                        }
                    }
                }

                yield();
                sleep( 1500 );
            }catch( Exception exc ){}
        }
    }

	/**
	 * ����� ��� ��������� ������
	 */
    public void stopThread(){
        m_bStopped = true;
    }
}
