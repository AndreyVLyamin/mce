package dlc.util;

import java.util.*;


/**
 * $Id: VC_Enumerator.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий итератор для контейнера переменных.
 */
class VC_Enumerator implements Enumeration{

    int      m_iterator = 0;
    Vector   m_elements;

	/** Конструктор */
    public VC_Enumerator( Vector values ){
        m_elements = values;
        m_iterator = 0;
    }

	/** Реализация метода Enumeration */
    public boolean hasMoreElements(){
        return m_iterator < m_elements.size();
    }

	/** Реализация метода Enumeration */
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
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий контейнер для переменных программы.
 */
public class VariableContainer{

    public final static int NAME = 0;
    public final static int VALUE = 1;

    private final int iStride = 2;

    Vector vars = new Vector();

    /**
     * Получение пар ИмяПеременной-Значение в виде списка
     */
    public Vector getVarVector(){
        return vars;
    }

    /**
     * Конструктор копирования
     */
    public VariableContainer( VariableContainer src ){
        vars = new Vector();
        vars.addAll( src.getVarVector() );
    }
    /**
     * Конструктор по умолчанию
     */
    public VariableContainer(){}

    /**
     * Очистка контейнера
     */
    public void clear(){
        vars.clear();
    }

    /**
     * Заполнить содержимое на основе другого экземпляра
     */
    public void putAll( VariableContainer src ){
        vars = new Vector();
        vars.addAll( src.getVarVector() );
    }

    /**
     * Получить список названий переменных
     */
    public Enumeration keys(){
        Vector keys = new Vector();
        for( int i = 0; i < vars.size(); i += iStride ){
            keys.addElement( vars.elementAt(i + NAME) );
        }
        return new VC_Enumerator( keys );
    }
    /**
     * Получить список значений переменных
     */
    public Enumeration elements(){
        Vector keys = new Vector();
        for( int i = 0; i < vars.size(); i += iStride ){
            keys.addElement( vars.elementAt(i + VALUE) );
        }
        return new VC_Enumerator( keys );
    }

    /**
     * Получить значение по названию переменной
     */
    public Object get( Object key ){
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i + NAME), key ) )
                return vars.elementAt(i+VALUE);
        }
        return null;
    }

    /**
     * Сохранить значения для переменной с именем key или добавить новую переменную.
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
     * Удалить переменную с именем key
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
     * Проверить наличие переменной в контейнере
     */
    public boolean containsKey( Object key ){
        for( int i = 0; i < vars.size(); i += iStride ){
            if( isEqual( vars.elementAt(i+NAME), key ) )
                return true;
        }
        return false;
    }

    /**
     * Метод проверки (для строк используется регистронезависимая проверка на равенство)
     */
    private boolean isEqual( Object o1, Object o2 ){
        if( o1 instanceof String && o2 instanceof String )
            return ((String)o1).equalsIgnoreCase( (String)o2 );
        return o1.equals( o2 );
    }

    /**
     * Возвращает количество переменных
     */
    public int size(){
        return (int)vars.size() / iStride;
    }

    /**
     * Проверка двух контейнеров на равенство
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
