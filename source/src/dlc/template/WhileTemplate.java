package dlc.template;

/**
 * ����� � ��������� ������� ����� while
 */
public class WhileTemplate {
	/** ������ ������ ����� */
    public String whileToken = "while";
	/** ������ ������ ���������� ����� */
    public String conditionStart = "(";
	/** ������ ��������� ���������� ����� */
    public String conditionEnd = ")";
	/** ������ ������ ����� */
    public String blockStart = "{";
	/** ������ ��������� ����� */
    public String blockEnd = "}";

	/** ��������� ��������� */
    public String whileInsert = "while( <�������> ){\n\t\n};\n";

	/** �������� ������ ������ ����� */
    public String getEntry(){
        return whileToken;
    }

	/** �������� ������ �������� */
    public String []getTerms(){
        return new String[]{
            whileToken, blockStart, blockEnd, whileToken, conditionStart, conditionEnd
        };
    }
}
