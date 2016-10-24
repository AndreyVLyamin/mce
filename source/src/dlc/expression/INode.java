package dlc.expression;

import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * $Id: INode.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ���� ���������.
 */
public abstract class INode {
    public Object val;
    public INode  parent;
    public int    priority = -1;

    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_LOG = 1;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_LOG_AND = 2;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_LOG_NOT = 3;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_EQ  = 4;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_ADD = 5;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_MUL = 6;
    /**
     * ���������� �������� � ���������
     */
    public static final int PRIOR_VAL = 999;

    /** ������ ��� ������� ��������� */
    public static String ERROR_2OPERANDS = "�������� ������� 2-� ���������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDNUMBER = "��������� �����";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDNUMBERORSTRING = "��������� ����� ��� ������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDBOOL = "��������� ������ ��������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_OPERAND = "�������� ������� �������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NOVAR = "���������� �� ���������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_OUTOFRANGE = "������ ������� �� ������� �������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_DIVBYZERO = "������� �� ����";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDINTINDEX = "��������� ������������� ������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_INVALIDINIT = "���������� �� ����������������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_INVALIDTYPE = "����������� ��� ����������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDARRAY = "�������������� �����, ���������� �� �������� ��������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_CANTNEGATIVE = "�������� ��������������� ��������� �������� ������ ��� �����";
    /** ������ ��� ������� ��������� */
    public static String ERROR_NEEDINTFORMOD = "�������� MOD ������� ������������� ���������";
    /** ������ ��� ������� ��������� */
    public static String ERROR_STRINGLEN = "����� ������ ������ ���� ������ 256 ��������";
    /** ������ ��� ������� ��������� */
	public static String ERROR_INVALIDFUNCARG = "������������ �������� �������";

    protected CodeToken m_source;

    /**
     * �����������
     * @param source �������� �������
     * @param parent �������� ����
     */
    public INode( CodeToken source, INode parent ){
        this.parent = parent;
        m_source = source;
    }
    /**
     * �����������
     * @param source �������� �������
     */
    public INode( CodeToken source ){
        m_source = source;
    }

    /**
     * �����, ����������� ����� ������ ��������� � ����������� ���������
     * @param vars ���������� ���������
     * @return ��������� ������������ �������� (� ����������� �� ���� ���������)
     */
    public abstract Object  proceed( VariableContainer vars ) throws Exception;
    /**
     * ���������� ��������� ����
     */
    public abstract boolean addChild( INode node );
    /**
     * ����� ��� ��������� ������ ����
     */
    public abstract INode   getLeft();
    /**
     * ����� ��� ��������� ������� ����
     */
    public abstract INode   getRight();

    /**
     * ����� ��� ������ ������� ����.
     * ��� ����� ������������ ��������, ��� �������, � ���� ������� ��������
     * ���������� �������� ������� ������. ��� �������� ������� ������������ � newNode
     */
    public abstract boolean replaceRight( INode newNode );

    /**
     * �������� ������������ ����
     */
    public INode getParent(){
        return parent;
    }
    /**
     * ���������� ������������ ����
     */
    public void setParent( INode parent ){
        this.parent = parent;
    }

    /**
     * �������� ���������
     */
    public int getPriority(){ return priority; }
    /**
     * ���������� ���������
     */
    public void setPriority( int p ){ priority = p; }

    /**
     * �������� �������� �������
     */
    public CodeToken getSourceToken(){ return m_source; }
}

