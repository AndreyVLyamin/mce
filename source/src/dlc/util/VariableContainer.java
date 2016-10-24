package dlc.util;

import java.util.*;


/**
 * $Id: VC_Enumerator.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� �������� ��� ���������� ����������.
 */
class VC_Enumerator implements Enumeration{

    int      m_iterator = 0;
    Vector   m_elements;

	/** ����������� */
    public VC_Enumerator( Vector values ){
        m_elements = values;
        m_iterator = 0;
    }

	/** ���������� ������ Enumeration */
    public boolean hasMoreElements(){
        return m_iterator < m_elements.size();
    }

	/** ���������� ������ Enumeration */
    public Object nextElement() throws NoSuchElementException{
        Object res = null;
        try{
            res = m_elements.elementAt(m_iterator);
            m_iterator++;
        }catch( Exception exc ){
            throw new NoSuchElementException();
        }
        return res;
    }
}

/**
 * $Id: VariableContainer.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ��������� ��� ���������� ���������.
 */
public class VariableContainer{

    public final static int NAME = 0;
    public final static int VALUE = 1;

    private final int iStride = 2;

    Vector vars = new Vector();

    /**
     * ��������� ��� �������������-�������� � ���� ������
     */
    public Vector getVarVector(){
        return vars;
    }

    /**
     * ����������� �����������
     */
    public VariableContainer( VariableContainer src ){
        vars = new Vector();
        vars.addAll( src.getVarVector() );
    }
    /**
     * ����������� �� ���������
     */
    public VariableContainer(){}

    /**
     * ������� ����������
     */
    public void clear(){
        vars.clear();
    }

    /**
     * ��������� ���������� �� ������ ������� ����������
     */
    public void putAll( VariableContainer src ){
        vars = new Vector();
        vars.addAll( src.getVarVector() );
    }

    /**
     * �������� ������ �������� ����������
     */
    public Enumeration keys(){
        Vector keys = new Vector();
        for( int i = 0; i < vars.size(); i += iStride ){
            keys.addElement( vars.elementAt(i + NAME) );
        }
        return new VC_Enumerator( keys );
    }
    /**
     * �������� ������ �������� ����������
     */
    public Enumeration elements(){
        Vector keys = new Vector();
        for( int i = 0; i < vars.size(); i += iStride ){
            keys.addElement( vars.elementAt(i + VALUE) );
        }
        return new VC_Enumerator( keys );
    }

    /**
     * �������� �������� �� �������� ����������
     */
    public Object get( Object key ){
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i + NAME), key ) )
                return vars.elementAt(i+VALUE);
        }
        return null;
    }

    /**
     * ��������� �������� ��� ���������� � ������ key ��� �������� ����� ����������.
     */
    public void put( Object key, Object value ) throws NullPointerException{
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i + NAME), key ) ){
                vars.setElementAt( value, i + VALUE );
                return;
            }
        }

        vars.addElement( key );
        vars.addElement( value );
    }

    /**
     * ������� ���������� � ������ key
     */
    public Object remove( Object key ){
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i + NAME), key ) ){
                Object res = vars.elementAt(i + VALUE);
                vars.removeElementAt( i );
                vars.removeElementAt( i );
                return res;
            }
        }

        return null;
    }

    /**
     * ��������� ������� ���������� � ����������
     */
    public boolean containsKey( Object key ){
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i+NAME), key ) )
                return true;
        }
        return false;
    }

    /**
     * ����� �������� (��� ����� ������������ ������������������� �������� �� ���������)
     */
    private boolean isEqual( Object o1, Object o2 ){
        if( o1 instanceof String && o2 instanceof String )
            return ((String)o1).equalsIgnoreCase( (String)o2 );
        return o1.equals( o2 );
    }

    /**
     * ���������� ���������� ����������
     */
    public int size(){
        return (int)vars.size() / iStride;
    }

    /**
     * �������� ���� ����������� �� ���������
     */
    public boolean equals( VariableContainer vc ){
        try{
            Vector vc_vars = vc.getVarVector();

            if( vc_vars.size() != vars.size() )
                return false;

            for( int i = 0; i < vars.size(); i++ ){
                if( !vc_vars.elementAt(i).equals(vars.elementAt(i)) ){
                    return false;
                }
            }
        }catch( Exception exc ){
            return false;
        }

        return true;
    }
}
