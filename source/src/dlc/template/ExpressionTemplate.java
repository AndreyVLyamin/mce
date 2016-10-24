package dlc.template;

/** Класс для описания строковых маркеров выражений */
public class ExpressionTemplate {
    public String assign = "=";
    public String eq = "==";
    public String neq = "!=";
    public String lt = "<";
    public String gt = ">";
    public String lte = "<=";
    public String gte = ">=";
    public String not = "not";

    public String or = "||";
    public String and = "&&";

    public String arrIndexStart = "[";
    public String arrIndexEnd = "]";

    public String groupStart = "(";
    public String groupEnd = ")";

    public String blockEnd = ";";

    public String add = "+";
    public String sub = "-";
    public String mul = "*";
    public String div = "/";
    public String mod = "mod";

    public String unaryAdd = "";
    public String unaryAddOne = "";
    public String unarySub = "";
    public String unarySubOne = "";
    public String unaryMul = "";
    public String unaryDiv = "";

    public String singleQuote = "'";
    public String doubleQuote = "\"";

    public String singleLineComment = "//";
    public String multiLineCommentStart = "/*";
    public String multiLineCommentEnd = "*/";

	public String singleLineCommentInsert = "";
	public String multiLineCommentInsert = "";

	public String funcSqrt = "sqrt";
	public String funcSin = "sin";
	public String funcCos = "cos";
	public String funcTan = "tan";
	public String funcAsin = "asin";
	public String funcAcos = "acos";
	public String funcAtan = "atan";
	public String funcRound = "round";
	// К Ins-ам прибавляются названия функций
	public String funcSqrtIns = "()";
	public String funcSinIns = "()";
	public String funcCosIns = "()";
	public String funcTanIns = "()";
	public String funcAsinIns = "()";
	public String funcAcosIns = "()";
	public String funcAtanIns = "()";
	public String funcRoundIns = "()";

	/** Получить массив маркеров */
    public String []getTerms(){
        return new String[]{
            assign, eq, neq, lt, gt, lte, gte, not,
            or, and, arrIndexStart, arrIndexEnd,
            groupStart, groupEnd, blockEnd, add, mod,
            sub, mul, div, singleQuote, doubleQuote,
            singleLineComment, multiLineCommentStart, multiLineCommentEnd,
            unaryAdd, unaryAddOne,
            unarySub, unarySubOne,
            unaryMul, unaryDiv,
			funcSqrt, funcSin, funcCos, funcTan,
			funcAsin, funcAcos, funcAtan, funcRound
        };
    }
	/** Является ли маркер - маркером присваивания (=, -=, +=, *=, /=) */
    public boolean isAssign( String term ){
        /**
         * Можно сравнивать с операторами +=, -=...
         */
        return term.equals( assign ) ||
                term.equals( unaryAdd ) ||
                term.equals( unaryAddOne ) ||
                term.equals( unarySub ) ||
                term.equals( unarySubOne ) ||
                term.equals( unaryMul ) ||
                term.equals( unaryDiv );
    }
	/** Является ли term маркером строкового выражения */
    public boolean isQuote( String term ){
        return term.equals( singleQuote ) || term.equals( doubleQuote );
    }
}
