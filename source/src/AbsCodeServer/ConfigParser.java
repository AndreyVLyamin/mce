package AbsCodeServer;

import org.xml.sax.*;
import org.xml.sax.helpers.ParserAdapter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * $Id: ConditionForChecing.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�.
 * <br/>
 * �����, ����������� XML-���� ������������.
 */

public class ConfigParser {

    private MyHandler handler;
    private ParserAdapter saxParser;
    private boolean ErrorWhenParse;

	/** �����-���������� ������� �� XML SAXParser */
    class MyHandler implements ContentHandler, ErrorHandler {

        private PrintWriter out;

        private Vector userInfo = new Vector();
        private Vector tmp1 = new Vector();
        private String CurrentElement = "";
        private Config config = new Config();

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void endPrefixMapping(java.lang.String prefix) {
        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void setDocumentLocator(Locator locator) {
        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void skippedEntity(java.lang.String name) {
        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) {
        }


//======================================================
// ��������������� ������
//======================================================
        /** ���������� ������-��������� */
        public Config getConfig() {
            return config;
        }


//=======================================================
// ����������� �������. ������ ���������� DocumentHandler
//========================

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void startDocument() {
            System.out.println("In StartDocument ");
        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void endDocument() {
            System.out.println("In endDocument ");
        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void startElement(String a, String name, String qName, Attributes attrs) {
            CurrentElement = name;

            if (name.equals("UserInfo")) {

                int len = attrs.getLength();
                UserInfo info = new UserInfo();
                for (int i = 0; i < len; i++) {
                    if (attrs.getLocalName(i).equals("login")) {
                        info.setLogin(attrs.getValue(i).trim());
                    } else {
                        info.setPassword(attrs.getValue(i).trim());
                    }
                }
                userInfo.add(info);
            }

            else if (name.equals("Port")) {
                config.setPort(Integer.parseInt(attrs.getValue(0)));
            }

            else if(name.equals("Timeout")){
                config.addTimeout( (String)attrs.getValue("name"), Long.parseLong(attrs.getValue("value")) );
            }

        }


		/** ������ ����������� ContentHandler, ErrorHandler */
        public void endElement(String URLname, String name, String qName) {
            if (name.equals("Config")) {
                config.setUserInfo(userInfo);
            }

        }

		/** ������ ����������� ContentHandler, ErrorHandler */
        public void characters(char ch[], int start, int length) {
        }

        // ���������������� �������(��������, ���������� ������ CDATA)
		/** ������ ����������� ContentHandler, ErrorHandler */
        public void ignorableWhitespace(char ch[], int start, int length) {
            characters(ch, start, length);
        }

        // ���������� XML-����������
		/** ������ ����������� ContentHandler, ErrorHandler */
        public void processingInstruction(String target, String data) {
        }

//===================================================
// ������ ���������� ErrorHandler
//===============================

        // ��������� ��������������
		/** ������ ����������� ContentHandler, ErrorHandler */
        public void warning(SAXParseException ex) {
            System.out.println("In warning");
            System.out.println("Warning at " +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
        }

        // ��������� ������
		/** ������ ����������� ContentHandler, ErrorHandler */
        public void error(SAXParseException ex) {
            System.out.println("In error");
            System.out.println("Error at {" +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
            ErrorWhenParse = true;
        }

        // ����� ������ ��������� ��� ������
		/** ������ ����������� ContentHandler, ErrorHandler */
        public void fatalError(SAXParseException ex) throws SAXException {
            System.out.println("fatal");
            System.out.println("Fatal error at {" +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
            ErrorWhenParse = true;
            throw ex;
        }
    }//end of MyHandler class

//==========================================================
//������ ������ MyParser
//==========================================================

	/** ����������� */
    public ConfigParser() throws InstantiationException {


        try {
//      System.setProperty("org.xml.sax.parser", "javax.xml.parsers.SAXParser");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(true);
            SAXParser sp = spf.newSAXParser();

            saxParser = new ParserAdapter(sp.getParser());


            handler = new MyHandler();
            saxParser.setContentHandler(handler);
            saxParser.setErrorHandler(handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ����� ��� ������� XML-����� ������������ �� ��������� ���� is */
    public Config parse(String is) throws SAXException {
        try {
            saxParser.parse(is);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (ErrorWhenParse) {
            return null;
        }
        return handler.getConfig();
    }
}//end of MyParser class