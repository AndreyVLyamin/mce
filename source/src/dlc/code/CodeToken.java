package dlc.code;

/**
 * $Id: CodeToken.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс для хранения информации о положении лексеммы в исходном коде.
 */
public class CodeToken{

	/** текстовое выражение */
    public String t;
	/** номер строки */
    public int line;
	/** номер колонки */
    public int column;
	/** абсолютная позиция */
    public int position;

	/**
	 * Конструктор
	 * @param line номер строки
	 * @param column номер колонки
     * @param position абсолютная позиция
	 * @param t текстовое выражение
	 */
    public CodeToken( int line, int column, int position, String t ){
        this.t = t;
        this.line = line;
        this.column = column;
        this.position = position;
    }

	/** Преобразование к строковому виду. Возвращает t */
    public String toString(){
        return t;
    }
};
