package dlc.template;

/**
 * ����� ��� �������� ��������� �������� ������� if/else
 */
public class IfTemplate {
	/** ������ ������ ������� */
    public String ifToken = "if";
	/** ������ ������ ��������� ������� */
    public String conditionStart = "(";
	/** ������ ��������� ��������� ������� */
    public String conditionEnd = ")";
	/** ������ ������ ����� ������� */
    public String blockStart = "{";
	/** ������ ��������� ����� ������� */
    public String blockEnd = "}";
	/** ������ ������ ����� elseIf */
    public String elseifToken = "else if";
	/** ������ ������ ����� else */
    public String elseToken = "else";

	/** ������ ��� ����� if */
    public String ifInsert = "if( <�������> ){\n\t\n};";
	/** ������ ��� ����� if/else */
    public String ifelseInsert = "if( <�������> ){\n\t\n};\nelse{\n\t\n};\n";

	/** �������� ������ ������ ������� */
    public String getEntry(){
        return ifToken;
    }

	/** �������� ������ �������� */
    public String []getTerms(){
        return new String[]{
            ifToken, blockStart, blockEnd, elseifToken, elseToken, conditionStart, conditionEnd
        };
    }
}
