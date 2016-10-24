import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * $Id: EditPaneApplet.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий аплет ВЛР.
 */
public class EditPaneApplet extends JApplet
    implements ActionListener{

    EditPane m_pane;
    JButton  m_bTest = new JButton("Отправить запрос...");
    JButton  m_bRes = new JButton("getResults()");

	/**
	 * Инициализация аплета
	 */
    public void init(){

        m_pane = new EditPane( getParameter("parameter") );
        this.getContentPane().add( m_pane );
//		add( m_pane );
/*
		JPanel jp = new JPanel( new BorderLayout() );
		jp.add( new JTextArea("text text text") );
		jp.add( new JButton("btn"), BorderLayout.SOUTH );

		add( jp );
*/
        if( getParameter("debug-on") != null && getParameter("debug-on").equals("true") ){
            //DEBUG
            JPanel btns = new JPanel( new FlowLayout() );
            btns.add( m_bTest );
            btns.add( m_bRes );
            m_bTest.addActionListener( this );
            m_bRes.addActionListener( this );
            getContentPane().add( btns, BorderLayout.SOUTH );
        }
    }

	/**
	 * Получение результата в сериализованном виде (делегирование EditPane.getResults())
	 */
    public String getResults(){
		String res = m_pane.getResults();
System.out.println( res );
        return res;
    }

	/**
	 * Обработка событий
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_bTest ){
            //String res = getResults();
            doCheckPost();
        }
        else if( e.getSource() == m_bRes ){
            String res = getResults();

            JDialog dlg = new JDialog();
            dlg.setModal( true );
            dlg.setSize( 400, 300 );
            JTextArea jta = new JTextArea( res );
            dlg.getContentPane().add( jta );
            dlg.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
            dlg.setVisible( true );
        }
    }

	String chooseServerIP(){
		JDialog frm = new JDialog();
		frm.setModal( true );
		frm.setTitle( "Введите IP сервера" );
		frm.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frm.setSize( 400, 150 );

		JTextArea jTA = new JTextArea();
		frm.getContentPane().add( jTA );

		frm.setVisible( true );

		return jTA.getText();
	}

	void statusCheckPost( String text ){
		JDialog frm = new JDialog();
		frm.setTitle( "Ответ от сервера" );
		frm.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frm.setSize( 400, 300 );

		JTextArea jTA = new JTextArea( text );
		frm.getContentPane().add( jTA );

		frm.setVisible( true );
	}


    void doCheckPost(){

          int    srv_port = 2003;
		  String Ip_Port = "192.168.0.7";


          try{
              String NL = "\r\n";

/*
Проверяющие наборы для лаб1: (когерентность лазерного излучения)
noice: шум
k: заданный интервал корелляции
bcount: мин. кол-во точек
blimitbottom, blimittop: минимальный предел для расстояний в ответе
noice=5;k=0.84;bcount=10;blimitbottom=-3.0;blimittop=3.0

------------------------------------------------------
Проверяющие наборы для лаб2: (когерентность излучения некогерентного источника)
cohfuncnoice: ошибка при измерении функции когерентности (также оценивается интервал корреляции по первому заданию)
intervalnoice: ошибка при измерении интервала корелляции
cohcount: мин. кол-во точек при измерении модуля ф-ции когерентности

------------------------------------------------------
Проверяющие наборы для лаб3: (контраст спекл-картины)
noice: шум (допустимая ошибка)
*/

/*
C1.1

      "<Input><!-- 10 15 12 18 17 100 41 45 47 51 42 55 --></Input>" +
      "<Output>" +
      "<!-- 10 12 15 17 18 100 55 51 47 45 42 41 -->" +
      "</Output>" +

C2.1
      "<Input><!-- 10 15 12 18 17 0 41 45 47 51 42 55 --></Input>" +
      "<Output>" +
      "<!-- 18 17 15 12 10 0 41 42 45 47 51 55 -->" +
      "</Output>" +

C3.1
      "<Input><!-- 67 57 88 60 20 85 58 19 99 93 58 44 84 81 91 37 64 111 111 75 48 108 76 55 50 101 86 99 30 104 --></Input>" +
      "<Output>" +
      "<!-- 67 57 88 60 20 85 58 19 37 44 58 64 81 84 91 93 99 111 111 75 48 108 76 55 50 101 86 99 30 104 -->" +
      "</Output>" +

C4.1
      "<Input><!-- 67 57 88 60 20 85 58 111 99 93 58 44 84 81 91 37 64 19 110 75 48 108 76 55 50 101 86 99 30 104 --></Input>" +
      "<Output>" +
      "<!-- 67 57 88 60 20 85 58 111 99 93 91 84 81 64 58 44 37 19 110 75 48 108 76 55 50 101 86 99 30 104 -->" +
      "</Output>" +
*/

              String req = "" +
              "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
              "<!DOCTYPE Request SYSTEM \"http://de.ifmo.ru/--DTD/Request.dtd\">" +
              "<Request>" +
                  "<Conditions>" +
                      "<ConditionForChecking id=\"1\" Time=\"5\">" +
                          "<Input>" +
//"<!-- 48 78 74 57 2 46 65 83 74 14 13 26 86 27 74 76 34 49 82 90 45 68 58 79 0 58 29 12 77 74 -->" +
                      //"<!--  -5 42 -6 24 13 -32 -27 -29 -58 44 -54 -1 -62 -26 -41 -59 9 18 -12 -54 39 -12 -3 -63 -46 -19 -15 18 -19 30 -->" +
        //              "<!-- -5 42 -6 24 13 -32 -27 -29 -58 44 -54 -1 -62 -26 -41 -59 9 18 -12 -54 39 -12 -3 -63 -46 -19 -15 18 -19 30 -->" +
        //                      "<!-- W k h   v w d w h p h q w v   d u h   d o o   o h j d o .  -->" +
        //                    "<!-- Q e b   p q x q b j b k q p   x o b   x i i   i b d x i . -->" +
                            "<!-- А Б В Г Д Е Ж З И К -->" +
//                              "<!-- J x u   i j q j u c u d j i   q h u   q b b   b u w q b . -->" +
                          "</Input>" +
                          "<Output>" +
								"<!-- А Б В Г Д Е Ж З И К -->" +
//                      "<!--104-->" +
//"<!-- 74 77 58 79 58 68 90 82 76 74 86 74 83 65 57 74 78 0 0 0 0 0 0 0 0 0 0 0 0 0 -->" +
                      //"<!--30 18 39 18 9 44 13 24 42 -5 -6 -32 -27 -29 -58 -54 -1 -62 -26 -41 -59 -12 -54 -12 -3 -63 -46 -19 -15 -19-->" +
                      //"<!-- -19 -15 -19 -46 -63 -3 -12 -54 -12 -59 -41 -26 -62 -1 -54 -58 -29 -27 -32 -6 -5 42 24 13 44 9 18 39 18 30 -->" +

                          "</Output>" +
                      "</ConditionForChecking>" +
                  "</Conditions>"+
                  "<Instructions>"+
              "<!--";

              req += getResults();

              req += "-->"+
                  "</Instructions>"+
              "</Request>";

              String toSend = "check" + NL +
                      "url:rlcp://ove:ove@192.168.0.7:2003" + NL +
                      "content-length:" + req.length() + NL + NL +
                      req + NL;

			  Ip_Port = chooseServerIP();
			  try{
					srv_port = Integer.parseInt( Ip_Port.substring( Ip_Port.indexOf(":")+1 ) );
					Ip_Port = Ip_Port.substring( 0, Ip_Port.indexOf(":") );
			  }catch( Exception ipExc ){

ipExc.printStackTrace();

					srv_port = 2003;
					Ip_Port = "localhost";
              }

              Socket s = new Socket( Ip_Port, srv_port );
              OutputStream os = s.getOutputStream();
              InputStream  is = s.getInputStream();

              os.write( toSend.getBytes() );

              int ch = 0;
              StringBuffer sb = new StringBuffer();
              while( (ch=is.read()) != -1  )
                  sb.append( (char)ch );

              s.close();

              System.out.println( "result from server:\n" + sb.toString() );

              statusCheckPost( "Ответ от: " + Ip_Port + ":" + srv_port + "\n" + sb.toString() );

          }catch( Exception e ){
              e.printStackTrace();
              statusCheckPost( "Ошибка соединения с сервером: " + Ip_Port + ":" + srv_port );
          }
      }
      //^^^^^^^^^^^^^^^^^^^^^^^^
      //DDD_DEBUG_DDD

}
