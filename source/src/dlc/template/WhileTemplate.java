package dlc.template;

/**
 * Класс с описанием маркера цикла while
 */
public class WhileTemplate {
	/** Маркер начала цикла */
    public String whileToken = "while";
	/** Маркер начала инварианта цикла */
    public String conditionStart = "(";
	/** Маркер окончания инварианта цикла */
    public String conditionEnd = ")";
	/** Маркер начала блока */
    public String blockStart = "{";
	/** Маркер окончания блока */
    public String blockEnd = "}";

	/** Шаблонное включение */
    public String whileInsert = "while( <условие> ){\n\t\n};\n";

	/** Получить маркер начала цикла */
    public String getEntry(){
        return whileToken;
    }

	/** Получить массив маркеров */
    public String []getTerms(){
        return new String[]{
            whileToken, blockStart, blockEnd, whileToken, conditionStart, conditionEnd
        };
    }
}
