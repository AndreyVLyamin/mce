package dlc.util;

/**
 * $Id: HtmlParamEscaper.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� �������������/���������������� ����������� ��������
 * ��� �������� ��������� ��� �� ������� ������� � �������.
 */
public class HtmlParamEscaper {
	/** ������������ �������� param */
    public static String escapeParam( String param ){
        return escapeParam( param, false );
    }
	/** ������������ �������� param
	 * @param bXML ������� �� �������� ������� �������� ����� �� ���� 
	 */
    public static String escapeParam( String param, boolean bXML ){
        String res = param.replaceAll( "&", "&amp;" );
        res = res.replaceAll( "<", "&lt;" );
        res = res.replaceAll(">", "&gt;");
        res = res.replaceAll("-", "&amp;minus;");
        res = res.replaceAll( "\"", "&quot;" );
        if( !bXML ){
            res = res.replaceAll( "\r\n", "&lt;br/&gt;");
            res = res.replaceAll( "\r", "&lt;br/&gt;");
            res = res.replaceAll( "\n", "&lt;br/&gt;");
        }
        return res;
    }
	/** ��������������� �������� param */
    public static String unescapeParam( String param ){
        String res = param.replaceAll( "&quot;", "\"" );
        res = res.replaceAll( "&lt;br/&gt;", "\r\n");
        res = res.replaceAll( "&lt;", "<" );
        res = res.replaceAll( "&gt;", ">" );
        res = res.replaceAll( "&minus;", "-" );
        res = res.replaceAll( "&amp;", "&" );

        return res;
    }
}
