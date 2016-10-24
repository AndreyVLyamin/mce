package AbsCodeServer;

/**
 * $Id: CheckingResult.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�.
 * <br/>
 * �����, ����������� ��������� ���������� ��� ����������� �������� ������ ������
 */
public class CheckingResult {

    /**
     * ������, ���������� �� ������ ���������
     */
    private String m_sOutput = "";
    /**
     * ������������� ���� ��������/��������� �������
     */
    private String m_sID = "";
    /**
     * ����� ���������� ���������
     */
    private String m_sTime = "";
    /**
     * ��������� ���������� ��������� (������)
     */
    private String m_sResult = "";

	/**
	 * ���������� ID ������
	 */
    public void setID(String ID) {
        m_sID = ID;
    }

	/**
	 * �������� ID ������
	 */
    public String getID() {
        return m_sID;
    }

	/**
	 * ���������� ����� ���������� ������
	 */
    public void setTime(long Time) {
        m_sTime = Long.toString(Time);
    }

	/**
	 * �������� ����� ���������� ������
	 */
    public String getTime() {
        return m_sTime;
    }

	/**
	 * ���������� ��������� ���������� ������
	 */
    public void setResult(String Result) {
        m_sResult = Result;
    }

	/**
	 * �������� ��������� ���������� ������
	 */
    public String getResult() {
        return m_sResult;
    }

	
	/**
	 * ���������� ��������� ����������� ��������� ����� ���������� ������
	 */
    public void setOutput(String Output) {
        m_sOutput = Output;
    }

	/**
	 * ���������� ��������� ����������� ��������� ����� ���������� ������
	 */
    public String getOutput() {
        return m_sOutput;
    }

    /**
     * ����� ���������� ����������
     */
    public void PrintRes() {
        System.out.println("ID: " + m_sID);
        System.out.println("Time: " + m_sTime);
        System.out.println("Output: " + m_sOutput);
        System.out.println("Result: " + m_sResult);
    }
}