package dlc.code;

/**
 * $Id: CodeRTException.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, расширяющий Exception для поддержки отладочной информации времени выполнения.
 */

public class CodeRTException extends Exception{

    public CodeToken m_source;

    /**
     * Конструктор
     * @param msg сообщение об ошибке
     * @param source место возникновения ошибки
     */
    public CodeRTException( String msg, CodeToken source ){
        super(msg);
        m_source = source;
    }
/*
    public CodeRTException( String msg, int a, int b, int c ){
        super(msg);
        System.out.println("WARNING: using of invalid CodeRTException!, Use 2-arg ctor!!!" );
        m_source = new CodeToken( 0, 0, 0, "" );
    }
*/

    /**
     * Метод для получения информации о месте, где возникла ошибка времени выполнения
     * @return экземпляр CodeToken
     */
    public CodeToken getSourceToken(){
        return m_source;
    }
}
