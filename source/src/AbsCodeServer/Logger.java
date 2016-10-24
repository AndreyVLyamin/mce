package AbsCodeServer;

/**
 * Класс, реализующий запись в журнал ошибок
 * (для каждой записи добавляется дата события)
 */
public class Logger {
    
	/**
	 * Метод для записи Exception в журнал ошибок
	 */
    public static void log( Exception exc ){
        System.out.println( "ERROR at " + new java.util.Date() + ": " );
        exc.printStackTrace();
    }

	/**
	 * Метод для добавления записи в журнал ошибок
	 */
    public static void log( String msg ){
        System.out.println( "LOG: " + new java.util.Date() + ": " + msg );
    }
}
