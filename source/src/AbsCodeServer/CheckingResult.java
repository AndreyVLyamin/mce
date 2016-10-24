package AbsCodeServer;

/**
 * $Id: CheckingResult.java,v 1 2007/02/20
 * <br/>
 * Author: Бердников Е.В.
 * <br/>
 * Класс, реализующий результат выполнения для конкретного входного набора данных
 */
public class CheckingResult {

    /**
     * Данные, полученные на выходе программы
     */
    private String m_sOutput = "";
    /**
     * Идентификатор пары входного/выходного наборов
     */
    private String m_sID = "";
    /**
     * Время выполнения программы
     */
    private String m_sTime = "";
    /**
     * Результат выполнения программы (оценка)
     */
    private String m_sResult = "";

	/**
	 * Установить ID набора
	 */
    public void setID(String ID) {
        m_sID = ID;
    }

	/**
	 * Получить ID набора
	 */
    public String getID() {
        return m_sID;
    }

	/**
	 * Установить время выполнения набора
	 */
    public void setTime(long Time) {
        m_sTime = Long.toString(Time);
    }

	/**
	 * Получить время выполнения набора
	 */
    public String getTime() {
        return m_sTime;
    }

	/**
	 * Установить результат выполнения набора
	 */
    public void setResult(String Result) {
        m_sResult = Result;
    }

	/**
	 * Получить результат выполнения набора
	 */
    public String getResult() {
        return m_sResult;
    }

	
	/**
	 * Установить состояние виртуальной установки после выполнения набора
	 */
    public void setOutput(String Output) {
        m_sOutput = Output;
    }

	/**
	 * Установить состояние виртуальной установки после выполнения набора
	 */
    public String getOutput() {
        return m_sOutput;
    }

    /**
     * Вывод отладочной информации
     */
    public void PrintRes() {
        System.out.println("ID: " + m_sID);
        System.out.println("Time: " + m_sTime);
        System.out.println("Output: " + m_sOutput);
        System.out.println("Result: " + m_sResult);
    }
}