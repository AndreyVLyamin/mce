package AbsCodeServer;


/**
 * $Id: ConditionForChecing.java,v 1 2007/02/20
 * <br/>
 * Author: Бердников Е.В.
 * <br/>
 * Класс, реализующий контейнер для набора условий выполнения программы.
 */
public class ConditionForChecking {

    /**
     * Максимальное время выполнения
     */
    private long m_nTime;
    /**
     * Идентификатор наборов
     */
    private int  m_ID;

    /**
     * Входной набор данных
     */
    private String m_Input;
    /**
     * Ожидаемый выходной набор данных
     */
    private String m_Output;

    /** устанавливаем входные данные( в виде набора строк ) */
    public void setInput(String input) {
        m_Input = input;
    }

    /** устанавливаем выходные данные( в виде набора строк ) */
    public void setOutput(String output) {
        m_Output = output;
    }

    /** устанавливаем время */
    public void setTime(long time) {
        m_nTime = time;
    }

	/** Получить время */
    public long getTime() {
        return m_nTime;
    }

    /** устанавливаем идентификатор условия */
    public void setID(int id) {
        m_ID = id;
    }

	/** Получить эталонный входной набор */
    public String getInput() {
        return m_Input;
    }

	/** Получить эталонный выходной набор */
    public String getOutput() {
        return m_Output;
    }

	/** Получить идентификатор условия */
    public String getID() {
        return Integer.toString(m_ID);
    }

    /**
     * Вывод отладочной информации
     */
    public void dumpInputOutput() {
        System.out.println(" ID: " + Integer.toString(m_ID));
        System.out.println(" Time: " + Long.toString(m_nTime));
        System.out.println(" Input: " + m_Input);
        System.out.println(" Output: " + m_Output);

    }
}