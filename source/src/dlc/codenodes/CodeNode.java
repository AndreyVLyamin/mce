package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * $Id: CodeNode.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �������� ����� ��� �������� ����� ��������� ������� ����������.
 */
public abstract class CodeNode {

    /**
     * �������� �������
     */
    CodeToken m_source;

    /**
     * �����������
     * @param source �������, ��������������� ������� ����
     */
    public CodeNode( CodeToken source ){
        m_source = source;
    }

    /**
     * ������ ������ execute ��� ��������� �������
     * @param vars - ���-������� ����������
     * @param runtime - ���������� � ���� ���������� ���������, CodeNode ������ ������� Instruction Pointer (IP)
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{
        System.out.println("ERROR: CodeNode base class should not be instantiated!");
        throw new NullPointerException( "CodeNode.execute - nothing to do" );
    }

    /**
     * �� ������������
     */
    public void execute( Hashtable vars ) throws Exception{
        System.out.println("ERROR: CodeNode base class should not be instantiated!");
        throw new NullPointerException( "CodeNode.execute - nothing to do" );
    }

    /**
     * ����� ���������� ���������� � �������
     * @return
     */
    public CodeToken getSourceToken(){
        return m_source;
    }

    /**
     * ����� ��� ��������� ���������� ����� ���� ��� ������, ������������ � ���� ����������
     * @return ������ ���������� 
     */
    public abstract Vector getLinearCode() throws Exception;
}

