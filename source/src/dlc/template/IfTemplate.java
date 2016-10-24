package dlc.template;

/**
 * Класс для описания строковых маркеров условия if/else
 */
public class IfTemplate {
	/** Маркер начала условия */
    public String ifToken = "if";
	/** Маркер начала оператора условия */
    public String conditionStart = "(";
	/** Маркер окончания оператора условия */
    public String conditionEnd = ")";
	/** Маркер начала блока условия */
    public String blockStart = "{";
	/** Маркер окончания блока условия */
    public String blockEnd = "}";
	/** Маркер начала блока elseIf */
    public String elseifToken = "else if";
	/** Маркер начала блока else */
    public String elseToken = "else";

	/** Шаблон для блока if */
    public String ifInsert = "if( <условие> ){\n\t\n};";
	/** Шаблон для блока if/else */
    public String ifelseInsert = "if( <условие> ){\n\t\n};\nelse{\n\t\n};\n";

	/** Получить маркер начала условия */
    public String getEntry(){
        return ifToken;
    }

	/** Получить массив маркеров */
    public String []getTerms(){
        return new String[]{
            ifToken, blockStart, blockEnd, elseifToken, elseToken, conditionStart, conditionEnd
        };
    }
}
