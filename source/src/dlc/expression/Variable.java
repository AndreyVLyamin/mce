package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;

/** Класс для описания перемнной */
public class Variable {

	/** Имя переменной */
    public String   name = "";
	/** Входная переменная */
    public boolean  bInput = false;
	/** Выходная переменная */
    public boolean  bOutput = false;
	/** Внутренняя переменная */
    public boolean  bInner = true;
	/** Тип переменной */
    public int      iType = TYPE_INT;
	/** Значение по умолчанию */
    public String   sDef = "";
	/** Значение */
    public Object   value;

	/** Тип Целое Число */
    public final static int TYPE_INT = 0;
	/** Тип Вещественное Число */
    public final static int TYPE_DEC = 1;
	/** Тип Целое Строка */
    public final static int TYPE_STR = 2;
	/** Тип Массив Целых Чисел */
    public final static int TYPE_ARR_INT = 3;
	/** Тип Массив Вещественных Чисел */
    public final static int TYPE_ARR_DEC = 4;
	/** Тип Массив Символов */
    public final static int TYPE_ARR_STR = 5;

	/** Максимальная длина имени = 32 */
    public final static int MAX_NAME_LEN = 32;

	/** Максимальная длина строки = 256 */
    public final static int MAX_STR_LEN = 256;

	/** Строковое описание типов переменных */
    public final static String []sTypes = new String[]{
        "Целое число",
        "Вещественное число",
        "Строка символов",
        "Массив целых чисел",
        "Массив вещественных чисел",
        "Массив символов"
    };

	/** Инициализация перемнной */
    public void init() throws Exception{
        switch( iType ){
            case TYPE_INT:
                value = new Integer( sDef );
                break;
            case TYPE_DEC:
                value = new Double( sDef );
                break;
            case TYPE_STR:
                value = new String( sDef );
                break;
            case TYPE_ARR_INT:
            case TYPE_ARR_DEC:
            case TYPE_ARR_STR:
                throw new Exception( INode.ERROR_INVALIDINIT + "(" + name + ")" );
            default:
                throw new Exception( INode.ERROR_INVALIDTYPE + "(" + name + ")" );
        }
    }
	/** Установка значения */
    public void setValue( Object val ) throws Exception{
        if( val instanceof Integer && (iType == TYPE_INT || iType == TYPE_DEC) ||
            val instanceof Double && iType == TYPE_DEC ||
            val instanceof String && iType == TYPE_STR ){

            switch( iType ){
                case TYPE_INT:
                    value = new Integer( "" + val );
                    break;
                case TYPE_DEC:
                    value = new Double( "" + val );
                    break;
                case TYPE_STR:
                    if( new String(""+val).length() >= MAX_STR_LEN )
                        throw new Exception( INode.ERROR_STRINGLEN );
                    value = new String( "" + val );
                    break;
                default:
                    throw new Exception( INode.ERROR_INVALIDTYPE + "(" + name + ")" );
            }
        }
        else
            throw new Exception( INode.ERROR_INVALIDINIT + "(" + name + ")" );
    }

	/** Конструктор */
    public Variable( String name ){
        this.name = name;
    }

	/** Конструктор */
    public Variable( String name, boolean input, boolean output, boolean inner, int type, String def ){
        this.name = name;
        bInput = input;
        bOutput = output;
        bInner = inner;
        iType = type;
        sDef = def;
    }

	/** Преобразование к строке - отладочный вариант */
    public String toString(){
        return "name: " + name + ", input: " + bInput + ", output: " + bOutput + ", inner: " + bInner + ", type: " + iType +
                ", sDef: " + sDef;
    }

	/** Получить эталонный тип для заданной переменной (для массивов, как для скалярных типов) */
    public static Object getRefType( int type ){
        switch( type ){
            case TYPE_INT:
            case TYPE_ARR_INT:
                return new Integer(0);
            case TYPE_DEC:
            case TYPE_ARR_DEC:
                return new Double(0.0);
            case TYPE_STR:
            case TYPE_ARR_STR:
                return new String("");
        }
        return null;
    }

	/** Перегруженный метод для сравнения переменных */
    public boolean equals( Object obj ){
        try{
            Variable var = (Variable)obj;
            return name.equals( var.name ) &&
                    bInput == var.bInput &&
                    bOutput == var.bOutput &&
                    bInner == var.bInner &&
                    iType == var.iType &&
                    sDef.equals( var.sDef );
        }catch( Exception exc ){}
        return false;
    }
}
