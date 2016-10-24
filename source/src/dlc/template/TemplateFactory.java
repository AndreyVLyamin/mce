package dlc.template;

//import org.dom4j.*;
//import org.dom4j.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;

/**
 * $Id: TemplateFactory.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ������� ������ �����������. ���������� � ������ ����������� �������� �� �������� XML-����� ������������
 */
public interface TemplateFactory {


    /**
     * �������� ������ �������� ����
     * @return ������ �������� ����
     */
    public Vector getKeywords();

    /**
     * �������� ������ ��������� ����������� (��� If, While, DoWhile)
     */
    public Vector getPlaceholders();

    /**
     * �������� ������ ��������� ����������� (��� If, While, DoWhile)
     */
    public Vector getAutoComplete();

    /**
     * �������� ������ ������ �����������
     */
    public String[] getStyles();

    /**
     * ��������� ����� ����� (�� ������� �����) �� ���������� �����
     * (��������� ����������� � �������� CodeStyle/@nameAlias)
     * @param aliasStyle �������� ����������
     * @return �������� �����
     */
    public String getStyleByAlias( String aliasStyle );

    /**
     * ��������� �������� ���������� ����� (��� �������� �� ����)
     * @return ��������� �������� �����
     */
    public String getCurrentStyleAlias();

    /**
     * ������������ ����� � ����������������� ��������
     * �� ������ ����� style
     */
    public boolean switchStyle( String style );

    /**
     * �������� ������ (��� �������� �����)
     */
    public IfTemplate getIfTemplate();

    /**
     * �������� ������ (��� �������� �����)
     */
    public WhileTemplate getWhileTemplate();

    /**
     * �������� ������ (��� �������� �����)
     */
    public DoWhileTemplate getDoWhileTemplate();

    /**
     * �������� ������ (��� �������� �����)
     */
    public ExpressionTemplate getExpressionTemplate();

    /**
     * �������� ������� �����
     */
    public String getCurrentStyle();

    /**
     * �������� �� ��������� ����� ���������?
     */
    public boolean isFree();
}
