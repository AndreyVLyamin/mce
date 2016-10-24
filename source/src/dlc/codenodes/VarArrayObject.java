package dlc.codenodes;

import java.util.*;

/**
 * Класс-контейнер для хранения переменных
 * Хэш-таблица, создаваемая на время выполнения программы
 * хранит название переменной и экземпляр такого объекта.
 * При присвоении, если переменная есть в хэше,
 * меняется свойство данного объекта,
 * таким образом, изменение значения переменной во внутреннем блоке
 * меняет его и во внешнем блоке.
 */
public class VarArrayObject extends VarObject{

	/** Конструктор
     * @param varName имя переменной
     */
    public VarArrayObject( String varName ){
        super( varName, new Hashtable() );
    }

	/** Получение значения по индексу (key) */
    public Object get( Object key ) throws Exception{
        return ((Hashtable)value).get( key );
    }
	/** Установка значения для индекса (key) */
    public void set( Object key, Object val ) throws Exception{
        ((Hashtable)value).put( key, val );
    }
}
