
import dlc.expression.Variable;
import dlc.expression.VariableArray;
import dlc.util.VariableContainer;

import javax.swing.*;
import java.util.*;

/**
 * $Id: VarListModel.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий ListModel для списка переменных в главном окне редактора кода.
 */
public class VarListModel extends AbstractListModel{

    VariableContainer m_vars;
    int       m_listType;

	/** Входная переменная */
    public final static int TYPE_INPUT = 1;
	/** Выходная переменная */
    public final static int TYPE_OUTPUT = 2;
	/** Внутренняя переменная */
    public final static int TYPE_INNER = 3;

	/** Метод для установки исходного контейнера переменных */
    public void setVarList( VariableContainer vars ){
        m_vars = vars;
    }

	/** Конструктор
	 * @param vars контейнер переменных
	 * @param listType одна из констант TYPE_***
	 */
    public VarListModel( VariableContainer vars, int listType ){
        m_vars = vars;
        m_listType = listType;
    }

	/** Получить количество переменных в этом списке */
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

	/** Возвращает строковое описание переменной по заданному индексу */
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
