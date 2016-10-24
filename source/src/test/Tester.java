package test;

import java.net.*;
import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * Класс реализующий тестовый модуль для передачи проверяющих наборов серверу.
 * Путь к файлу конфигурации указывается, как параметр при запуске приложения.
 */
public class Tester{

	//url:rlcp://ove:ove@127.0.0.1:2003
	String m_url = "";
	String m_req = "";

	private String readStringFromIS( InputStream is ) throws Exception{
		int ch = 0;
		int byteCount = 0;

		int initBufSize = 64000;

		byte []buffer = new byte[ initBufSize  ];

		while( (ch = is.read()) >= 0 ){
			buffer[byteCount++] = (byte)ch;
			if( byteCount >= buffer.length ){
				byte []newBuffer = new byte[ buffer.length + initBufSize ];
				System.arraycopy( buffer, 0, newBuffer, 0, buffer.length );
				buffer = newBuffer;
			}
		}

		return new String( buffer, 0, byteCount );
	}

	/**
	 * Конструктор
	 * @param config файл с конфигурацией
	 * @param outPath файл для записи результата выполнения (в формате RLCP-response)
	 */
	public Tester( File config, String outPath ) throws Exception{
		readConfig( config.getAbsolutePath() );

		try{

			String NL = "\r\n";

			m_req = "<?xml version=\"1.0\" encoding=\"" + java.nio.charset.Charset.defaultCharset() + "\"?>" + NL + m_req;

            String toSend = "check" + NL +
                    m_url + NL +
                    "content-length:" + m_req.length() + NL + NL +
					m_req + NL;

			int srv_port = 2003;
			StringTokenizer st = new StringTokenizer( m_url, ":/@", true );
			String sHost = "";
			String sPort = "";
			String sLogin = "";
			String sPasswd = "";
			int iState = 0;

			while( st.hasMoreTokens() ){

				String token = st.nextToken();

				if( token.equals("/") ){
					st.nextToken();
					sLogin = st.nextToken();
					st.nextToken();
					sPasswd = st.nextToken();
					st.nextToken();
					sHost = st.nextToken();
					st.nextToken();
					sPort = st.nextToken();
					break;
				}
			}

//System.out.println( "sHost: " + sHost );
//System.out.println( "sPort: " + sPort );
//System.out.println( "sLogin: " + sLogin );
//System.out.println( "sPasswd: " + sPasswd );

			srv_port = Integer.parseInt( sPort );

            Socket s = new Socket( sHost, srv_port );
            OutputStream os = s.getOutputStream();
            InputStream  is = s.getInputStream();

            os.write( toSend.getBytes("Windows-1251") );

            int ch = 0;
            StringBuffer sb = new StringBuffer();
/*
            while( (ch=is.read()) != -1  )
                sb.append( (char)ch );
*/
			sb.append( readStringFromIS( is ) );
            s.close();

//System.out.println("RESULT");
//System.out.println( sb );

			String xml = sb.toString();
			if( xml.indexOf("<") >= 0 ){
				xml = xml.substring( xml.indexOf("<") );

				saveOutputDocument( outPath + File.separator + config.getName(), xml, true );
			}else{
				saveOutputDocument( outPath + File.separator + config.getName(), xml, false );
			}

		}catch( Exception exc ){
			exc.printStackTrace();
			throw exc;
		}
	}

	/**
	 * Метод для чтения конфигурации тестового модуля
	 */
	private void readConfig( String path ) throws Exception{
		Document doc = new SAXReader().read( path );
		m_url = doc.selectSingleNode( "//URL" ).getText();
		m_req = doc.selectSingleNode( "//RLCPRequest/.//*" ).asXML();
	}

	/** Класс для поиска XML-файлов в заданном каталоге */
	public static class XMLFilterAdapter implements FilenameFilter{
		/** Метод FilenameFilter */
		public boolean accept( File dir, String name ){
			int p = name.indexOf( "." );
			if( p < 0 ) return false;
			if( (p+1) >= name.length() ) return false;
			String ext = name.substring( p+1 );
			return ext.equalsIgnoreCase( "xml" );
		}
	}

	private void saveOutputDocument( String path, String doc, boolean bReformat ) throws Exception{
		if( bReformat ){

for( int i = 0; i < doc.length(); i++ ){
	System.out.print( doc.charAt(i) );
	if( doc.charAt(i) == 0x12 ){
		System.out.println("\nHERE!!!" );
		break;
	}
}

			Document xdoc = new SAXReader().read( new StringReader(doc) );
			XMLWriter wr = new XMLWriter( new FileOutputStream( path ), OutputFormat.createPrettyPrint() );
			wr.write( xdoc );
			wr.close();
		}else{
			FileOutputStream fos = new FileOutputStream( path );
			fos.write( doc.getBytes() );
			fos.close();
		}
	}

	/**
	 * Метод для запуска модуля.
	 * Аргумент args[0] - путь к каталогу с тест-наборами
	 * Аргумент args[1] - путь к каталогу с выходными файлами
	 */
	public static void main( String []args ){

		if( args.length < 2 ){
			System.out.println("USAGE: java test.Tester <testset-dir> <output-dir>" );
			return;
		}

		new File( args[1] ).mkdirs();

		File []tsFiles = new File( args[0] ).listFiles( new XMLFilterAdapter() );
		for( int i = 0; tsFiles != null && i < tsFiles.length; i++ ){
			try{
				new Tester( tsFiles[i], args[1] );
			}catch( Exception exc ){
				System.out.println( "test.Tester FAILED at " + tsFiles[i].getAbsolutePath() );
				System.out.println( "\t" + exc.getMessage() );
			}
		}

		System.out.println( "Thats all. Look at " + args[1] + " to see servers output!" );
	}
}
