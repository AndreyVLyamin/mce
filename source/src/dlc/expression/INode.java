package dlc.expression;

import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * $Id: INode.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий узел выражения.
 */
public abstract class INode {
    public Object val;
    public INode  parent;
    public int    priority = -1;

    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_LOG = 1;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_LOG_AND = 2;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_LOG_NOT = 3;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_EQ  = 4;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_ADD = 5;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_MUL = 6;
    /**
     * приоритеты операций в выражении
     */
    public static final int PRIOR_VAL = 999;

    /** Ошибки при разборе выражений */
    public static String ERROR_2OPERANDS = "Операция требует 2-х операндов";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDNUMBER = "Требуется число";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDNUMBERORSTRING = "Требуется число или строка";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDBOOL = "Требуется булево значение";
    /** Ошибки при разборе выражений */
    public static String ERROR_OPERAND = "Операция требует операнд";
    /** Ошибки при разборе выражений */
    public static String ERROR_NOVAR = "Переменная не объявлена";
    /** Ошибки при разборе выражений */
    public static String ERROR_OUTOFRANGE = "Индекс выходит за границы массива";
    /** Ошибки при разборе выражений */
    public static String ERROR_DIVBYZERO = "Деление на ноль";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDINTINDEX = "Требуется целочисленный индекс";
    /** Ошибки при разборе выражений */
    public static String ERROR_INVALIDINIT = "Переменная не инициализирована";
    /** Ошибки при разборе выражений */
    public static String ERROR_INVALIDTYPE = "Неизвестный тип переменной";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDARRAY = "Несоответствие типов, переменная не является массивом";
    /** Ошибки при разборе выражений */
    public static String ERROR_CANTNEGATIVE = "Операция арифметического отрицания доступна только для чисел";
    /** Ошибки при разборе выражений */
    public static String ERROR_NEEDINTFORMOD = "Операция MOD требует целочисленных операндов";
    /** Ошибки при разборе выражений */
    public static String ERROR_STRINGLEN = "Длина строки должна быть меньше 256 символов";
    /** Ошибки при разборе выражений */
	public static String ERROR_INVALIDFUNCARG = "Некорректный аргумент функции";

    protected CodeToken m_source;

    /**
     * Конструктор
     * @param source исходная лексема
     * @param parent корневой узел
     */
    public INode( CodeToken source, INode parent ){
        this.parent = parent;
        m_source = source;
    }
    /**
     * Конструктор
     * @param source исходная лексема
     */
    public INode( CodeToken source ){
        m_source = source;
    }

    /**
     * Метод, реализующий обход дерева выражения и вычисляющий результат
     * @param vars переменные программы
     * @return экземпляр вычисленного значения (в зависимости от типа операндов)
     */
    public abstract Object  proceed( VariableContainer vars ) throws Exception;
    /**
     * Добавление дочернего узла
     */
    public abstract boolean addChild( INode node );
    /**
     * Метод для получения левого узла
     */
    public abstract INode   getLeft();
    /**
     * Метод для получения правого узла
     */
    public abstract INode   getRight();

    /**
     * Метод для замены правого узла.
     * Для более приоритетных операций, чем текущая, в узле текущей операции
     * заменяется дочерний элемент данным. Сам дочерний элемент перемещается в newNode
     */
    public abstract boolean replaceRight( INode newNode );

    /**
     * Получить родительский узел
     */
    public INode getParent(){
        return parent;
    }
    /**
     * Установить родительский узел
     */
    public void setParent( INode parent ){
        this.parent = parent;
    }

    /**
     * Получить приоритет
     */
    public int getPriority(){ return priority; }
    /**
     * Установить приоритет
     */
    public void setPriority( int p ){ priority = p; }

    /**
     * Получить исходную лексему
     */
    public CodeToken getSourceToken(){ return m_source; }
}

