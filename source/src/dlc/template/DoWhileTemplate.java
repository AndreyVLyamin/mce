package dlc.template;

/**
 * ����� � ����������� ��� �������� ����� do-while
 */
public class DoWhileTemplate {
	/** ������ ������ ����� */
    public String doToken = "do";
	/** ������ ������ ���� ����� */
    public String blockStart = "{";
	/** ������ ��������� ���� ����� */
    public String blockEnd = "}";
	/** ������ ��������� ����� */
    public String whileToken = "while";
	/** ������ ������ ���������� ����� */
    public String conditionStart = "(";
	/** ������ ��������� ���������� ����� */
    public String conditionEnd = ")";

	/** ��������� ��������� */
    public String dowhileInsert = "do{\nwhile( <�������> );\n";

    /**
     * ������� ��������� ������� � ����������
     * (������������ � repeat/until)
     */
    public boolean bInverse = false;

	/** ������� ����� ������ ����� */
    public String getEntry(){
        return doToken;
    }

	/** �������� ������ �������� */
    public String []getTerms(){
        return new String[]{
            doToken, blockStart, blockEnd, whileToken, conditionStart, conditionEnd
        };
    }
}
