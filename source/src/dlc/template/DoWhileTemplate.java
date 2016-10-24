package dlc.template;

/**
 * Класс с выражениями для описания цикла do-while
 */
public class DoWhileTemplate {
	/** Маркер начала цикла */
    public String doToken = "do";
	/** Маркер начала тела цикла */
    public String blockStart = "{";
	/** Маркер окончания тела цикла */
    public String blockEnd = "}";
	/** Маркер окончания цикла */
    public String whileToken = "while";
	/** Маркер начала инварианта цикла */
    public String conditionStart = "(";
	/** Маркер окончания инварианта цикла */
    public String conditionEnd = ")";

	/** Шаблонное включения */
    public String dowhileInsert = "do{\nwhile( <условие> );\n";

    /**
     * Признак отрицания условия в инварианте
     * (используется в repeat/until)
     */
    public boolean bInverse = false;

	/** Получит метку начала цикла */
    public String getEntry(){
        return doToken;
    }

	/** Получить массив маркеров */
    public String []getTerms(){
        return new String[]{
            doToken, blockStart, blockEnd, whileToken, conditionStart, conditionEnd
        };
    }
}
