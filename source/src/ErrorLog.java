
/**
 * $Id: ErrorLog.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Интерфейс, описывающий методы для вывода сообщений об ошибках.
 */
public interface ErrorLog {
	/**
	 * Показать сообщение об ошибке
	 */
    public void showError( String error );
	/**
	 * Показать сообщение
	 */
    public void showMessage( String message );
	/**
	 * Показать предупреждение
	 */
    public void showWarning( String message );
}
