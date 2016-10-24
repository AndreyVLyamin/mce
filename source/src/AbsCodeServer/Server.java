package AbsCodeServer;

//�������� ����� ������������ �������

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * $Id: Server.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�.
 * <br/>
 * �����, ����������� ������ �������� ����������� ������������ ������.
 */
public class Server {

	/** ����������� */
    public Server() {}

	/**
	 * �������� ����� ������� ������������ �������
	 */
    public static void main(String args[]) {
        System.out.println("In main");
        Server server = new Server();

        try {
            //������ ���� ������������
            ConfigParser parser = new ConfigParser();
            Config config = parser.parse("Config.xml");
            if (config == null) {
                System.exit(0);
            }
            ServerSocket ss = null;
            Socket sock = null;

            int Port = config.getPort();
            System.out.println("Port:" + Integer.toString(Port));

            ss = new ServerSocket(Port);
            while (true) {
                //������� ����
                sock = ss.accept();

                //��������� �����, ��������������� ������������ ��� �������� ��������
                new ThreadForRequest(sock, config).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


/**
 * $Id: ThreadForRequest.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�., �������� �.�.
 * <br/>
 * �����, ����������� ����� ��������� ���������� ��������.
 */
class ThreadForRequest extends Thread {

    /**
     * ������������� ������ CHECK
     */
    private final int CHECK = 0;
    /**
     * ������������� ������ CALCULATE
     */
    private final int CALCULATE = 1;

    /**
     * ������� ������ � �������
     */
    private boolean bErrorInRequest = false;

    /**
     * ��� ������
     */
    private int ErrorCode;

    /**
     * Socket ���������� � �������� (� ����� ������, ����� � ���������)
     */
    private Socket m_socket;

    /**
     * ����� ������ ������� (CHECK ��� CALCULATE)
     */
    private int m_Method;

    /**
     * ��� �������
     */
    private String Login = "";
    /**
     * ������ �������
     */
    private String Password = "";

    /**
     * ��������� ������ Config � ����������� � ������������, ���������� �� ����� ������������
     */
    private Config m_config;

	/** ����������� 
	 * @param sock �����, ��������� ��� ����������� �������
	 * @param config ��������� ������ Config, ��������� ��� ������������� �������
	 */
    public ThreadForRequest(Socket sock, Config config) {
        m_socket = sock;
        m_config = config;
    }

//��������� 2 ������ ���� ������(��� ������� ���� 
//Check ��� �������� ���� ������� ����� �� ����)

    private boolean check(Program prg) {
        return true;
    }

    private boolean calculate(Vector msg) {
        System.out.println("In Calculate ");
        return true;
    }

	/**
	 * ��������� ������ �� �������� ������ � ����������� ��������� ��������
	 */
	public String readStringFromIS( InputStream is, int size ) throws Exception{
		int ch = 0;
		int byteCount = 0;

		byte []buffer = new byte[ size ];

//System.out.println( "Expected size: " + size );

		while( byteCount < size && (ch = is.read()) >= 0 ){
			buffer[byteCount++] = (byte)ch;
//System.out.println("recv byte #: " + byteCount );
		}

		return new String( buffer, 0, byteCount );
	}

//������� ���������� XML-����� ������� �� ��������
    private String getXML(InputStream in, int length) {
        System.out.println("ThreadForRequest::getXML() inputStream has " + Integer.toString(length) + " bytes");
        String str = "";

        try {
            while (length != 0) {
                m_socket.setSoTimeout(10000);

                int c = in.read();

                if (c == '\b') {
                    if (str.length() == 0) {
                        continue;
                    }
                    str = str.substring(0, str.length() - 1);
                } else {
                    str += (char) c;
                    length--;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * �������� ���� ������ ��������� ��������
     */
    public void run() {

        OutputStream out = null;
        InputStream in = null;

        try {
            out = m_socket.getOutputStream();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)), true);
            in = m_socket.getInputStream();

            //��������� ������ ��� ������������ ��������� ������
            String tmp = new String();

            int c;

            int nFieldCount = 0;   //����� ������ � �������

            Hashtable htFields = new Hashtable();

/*
 ������ ����������
*/

            String str = "";
            while (true) {
                m_socket.setSoTimeout(10000);

                c = in.read();
                if (c == -1) {
                    System.out.println("ThreadForRequest::run - ERROR: Connection is closed ");
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }

                if (c == 13) {
                    c = in.read();
                    nFieldCount++;

                    if (nFieldCount == 1) {
                        tmp = tmp.trim();
                        System.out.println("METOD: " + tmp);
                        if (tmp.toUpperCase().equals("CHECK")) {
                            m_Method = CHECK;
                        }
                        //���� ��� ����� CALCULATE �� ��������������
                        //������� ������������ ��� ������ 501(������� ���������������� ����� � �������)
                        else if (tmp.toUpperCase().equals("CALCULATE")) {
                            m_Method = CALCULATE;
                            out.write(("501" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        } else {
                            out.write(("403" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        }
                    }
                    if (nFieldCount > 1) {
                        StringTokenizer strTok = null;
                        if (tmp.equals("")) {
                            String szURL = (String) htFields.get("url");
                            if (szURL == null) {
                                out.write(("404" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                return;
                            }
                            strTok = new StringTokenizer(szURL, "@");

                            if (strTok.countTokens() != 2) {
                                out.write(("402" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                System.out.println("ERROR: error parse URL, cant find '@'");
                                return;
                            } else {

System.out.println("URL token: " + szURL );
/**
 * TODO: fix BUG!!!!
 */

                                String a1 = strTok.nextToken();
                                strTok = new StringTokenizer( a1, "//" );
                                String a2 = strTok.nextToken();
                                //TODO: nexttoken - nexttoken BUG!!!
                                strTok = new StringTokenizer(strTok.nextToken(), ":");

System.out.println("a1: " + a1 + ", a2: " + a2 );

                                if (strTok.countTokens() != 2) {
                                    out.write(("402" + "\r\n").getBytes());
                                    out.flush();
                                    in.close();
                                    out.close();
                                    m_socket.close();
                                    System.out.println("ERROR: error at token URL parse" );
                                    return;
                                } else {
                                    String szLogin = strTok.nextToken();
                                    String szPassword = strTok.nextToken();

                                    for (int i = 0; i < m_config.getUserInfo().size(); i++) {
                                        if (szLogin.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getLogin())) {
                                            if (szPassword.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getPassword())) {
                                                break;
                                            }
/*
                                            else {
                                                System.out.println("ERROR: Incorrect Login information");
                                                out.write(("402" + "\r\n").getBytes());
                                                out.flush();
                                                in.close();
                                                out.close();
                                                m_socket.close();
                                                return;
                                            }
*/
                                        }

                                        if (i == (m_config.getUserInfo().size() - 1)) {
                                            out.write(("402" + "\r\n").getBytes());
                                            out.flush();
                                            in.close();
                                            out.close();
                                            System.out.println("ERROR: No user accounts specified in Config.xml");
                                            m_socket.close();
                                            return;
                                        }
                                    }
                                }
                            }

                            String szLength = (String) htFields.get("content-length");
                            if (szLength == null) {
                                out.write(("404" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                return;
                            }
                            //���������� � ���������� str XML-�������� �� �������

                            //str = getXML(in, Integer.parseInt(szLength));
							str = readStringFromIS( in, Integer.parseInt(szLength) );
                            //System.out.println(str);
                            break;
                        }

                        System.out.println("Find : at\n\t" + tmp);

                        strTok = new StringTokenizer(tmp, ":", true);
                        if (strTok.countTokens() == 1 || strTok.countTokens() == 2) {
                            out.write(("405" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        } else {

                            String szKey = strTok.nextToken().trim().toLowerCase();
                            strTok.nextToken();
                            String szValue = "";
                            while (strTok.hasMoreTokens()) {
                                szValue += strTok.nextToken().trim();
                            }
                            htFields.put(szKey, szValue);
                        }
                    }

                    tmp = "";
                    continue;
                } else if (c == '\b') {
                    if (tmp.length() == 0) {
                        continue;
                    }
                    tmp = tmp.substring(0, tmp.length() - 1);
                } else {
                    tmp += (char) c;
                }
            }

            m_socket.setSoTimeout(0);
            System.out.println("Exit From While");

            if (m_Method == CHECK) {

                // ���� ���������� ��� �� �������� ������������� � ����� Check
                InputStream is = new ByteArrayInputStream(str.getBytes());

                //ReqParser parser = new ReqParser();

/*
    ������ XML-���������
*/

                //������ ����� ������������
//System.out.println("Input UserAnswer:\n" + str + "\n==================");

                Program prg = new ReqParser2().parse( str );
                if (prg == null) {
                    out.write(("400" + "\r\n").getBytes());
                    out.flush();
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }
                prg.setTimeouts( m_config.getTimeouts() );

//    ������ ��������� (������ ��������) �� ����������

                Vector Res = new Vector();
                try{
                    Res = prg.runForCheck();
                }catch( Exception e ){
                    //e.printStackTrace();
                    System.out.println("ERROR: ThreadForRequest processing FAILED (at runForCheck): " + e.getMessage() );
                }

                if (Res == null) {
                    out.write(("401" + "\r\n").getBytes());
                    out.flush();
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }

                //���������� XML-����� ��������
                Document document = DocumentHelper.createDocument();
                document.addDocType("Response", null, "http://de.ifmo.ru/--DTD/Response.dtd");

                Element response = document.addElement("Response");
                for (int i = 0; i < Res.size(); i++) {
                    Element ChRes = response.addElement("CheckingResult");
                    ChRes.addAttribute("id", ((CheckingResult) Res.elementAt(i)).getID());
                    ChRes.addAttribute("Time", ((CheckingResult) Res.elementAt(i)).getTime());
                    ChRes.addAttribute("Result", ((CheckingResult) Res.elementAt(i)).getResult());

					String sResult = ((CheckingResult) Res.elementAt(i)).getOutput();
					sResult = sResult.replaceAll( "&", "&amp;" );
					sResult = sResult.replaceAll( "-", "&#0045;" );

                    ChRes.addComment((String) 
						//dlc.util.HtmlParamEscaper.escapeParam( ((CheckingResult) Res.elementAt(i)).getOutput(), true )
						sResult
					);
                }

                int iLength = document.asXML().getBytes().length;
                String XMLResponse = "200" + "\r\n";
                XMLResponse += "Content-Length:" + Integer.toString(iLength) + "\r\n\r\n";
                XMLResponse += document.asXML();

                System.out.println("Response:\n" + XMLResponse);
                out.write(XMLResponse.getBytes());
/*
                FileOutputStream fos = new FileOutputStream( "c:\\temp\\cohlab_server.out" );
                fos.write( XMLResponse.getBytes() );
                fos.close();
*/
            } else if (m_Method == CALCULATE) {
                //calculate( message );
            } else {
                out.write("Error in Request".getBytes());
                System.out.println("Error in request");
                bErrorInRequest = true;
            }

            out.flush();
            in.close();
            out.close();

            m_socket.close();

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                m_socket.close();
            } catch (Exception ex) {
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
