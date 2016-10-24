package dlc.code;

/**
 * $Id: CompileException.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, хранящий информацию о месте возникновения ошибки компиляции.
 */
public class CompileException extends Exception{
    public int l, c, p;

    /**
     * Конструктор
     * @param msg сообщение об ошибке
     * @param l строка
     * @param c колонка
     * @param p абсолютное положение каретки в тексте
     */
    public CompileException( String msg, int l, int c, int p ){
        super(msg);
        this.l = l;
        this.c = c;
        this.p = p;
    }

	/**
	 * Конструктор
	 * @param msg сообщение
	 * @param ct блок кода, в котором произошло исключение
	 */
    public CompileException( String msg, CodeToken ct ){
        super(msg);
        this.l = ct.line;
        this.c = ct.column;
        this.p = ct.position;
    }
}
