
import dlc.expression.Variable;
import dlc.expression.VariableArray;
import dlc.util.VariableContainer;

import javax.swing.*;
import java.util.*;

/**
 * $Id: VarListModel.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ListModel ��� ������ ���������� � ������� ���� ��������� ����.
 */
public class VarListModel extends AbstractListModel{

    VariableContainer m_vars;
    int       m_listType;

	/** ������� ���������� */
    public final static int TYPE_INPUT = 1;
	/** �������� ���������� */
    public final static int TYPE_OUTPUT = 2;
	/** ���������� ���������� */
    public final static int TYPE_INNER = 3;

	/** ����� ��� ��������� ��������� ���������� ���������� */
    public void setVarList( VariableContainer vars ){
        m_vars = vars;
    }

	/** �����������
	 * @param vars ��������� ����������
	 * @param listType ���� �� �������� TYPE_***
	 */
    public VarListModel( VariableContainer vars, int listType ){
        m_vars = vars;
        m_listType = listType;
    }

	/** �������� ���������� ���������� � ���� ������ */
    public int getSize(){
        int res = 0;
        for( Enumeration en = m_vars.keys(); en.hasMoreElements(); ){
            String   key = (String)en.nextElement();
            Variable var = (Variable)m_vars.get( key );
            if( isCorrectType(var) ){
                if( var instanceof VariableArray )
                    res += ((VariableArray)var).elems.size();
                else
                    res++;
            }
        }
        return res;
    }

	/** ���������� ��������� �������� ���������� �� ��������� ������� */
    public Object getElementAt( int index ){
        for( Enumeration en = m_vars.keys(); en.hasMoreElements(); ){
            String   key = (String)en.nextElement();
            Variable var = (Variable)m_vars.get( key );
            if( isCorrectType(var) ){
                if( var instanceof VariableArray ){
                    VariableArray vArr = (VariableArray)var;
                    for( int i = 0; i < vArr.elems.size(); i++ ){
                        if( index == 0 ){
                            return var.name + "[" + i + "]=" + vArr.elems.elementAt(i);
                        }
                        index--;
                    }
                }
                else{
                    if( index == 0 ){
                        return var.name + "=" + var.value;
                    }
                    index--;
                }
            }
        }
        return "NO-ITEM";
    }

    private boolean isCorrectType( Variable var ){
        return m_listType == TYPE_INPUT && var.bInput ||
                m_listType == TYPE_OUTPUT && var.bOutput ||
                m_listType == TYPE_INNER && var.bInner;
    }
}
