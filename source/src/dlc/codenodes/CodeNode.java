package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.*;

/**
 * $Id: CodeNode.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Корневой класс для описания узлов программы времени выполнения.
 */
public abstract class CodeNode {

    /**
     * Исходная лексема
     */
    CodeToken m_source;

    /**
     * Конструктор
     * @param source лексема, соответствующая данному узлу
     */
    public CodeNode( CodeToken source ){
        m_source = source;
    }

    /**
     * Версия метода execute для пошаговой отладки
     * @param vars - хэш-таблица переменных
     * @param runtime - информация о ходе выполнения программы, CodeNode меняет текущий Instruction Pointer (IP)
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{
        System.out.println("ERROR: CodeNode base class should not be instantiated!");
        throw new NullPointerException( "CodeNode.execute - nothing to do" );
    }

    /**
     * не используется
     */
    public void execute( Hashtable vars ) throws Exception{
        System.out.println("ERROR: CodeNode base class should not be instantiated!");
        throw new NullPointerException( "CodeNode.execute - nothing to do" );
    }

    /**
     * Метод возвращает информацию о лексеме
     * @return
     */
    public CodeToken getSourceToken(){
        return m_source;
    }

    /**
     * Метод для получения лийнейного блока кода для дерева, построенного в ходе компиляции
     * @return список инструкций 
     */
    public abstract Vector getLinearCode() throws Exception;
}

