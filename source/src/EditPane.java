
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Document;

import dlc.code.*;
import dlc.codenodes.*;
import dlc.expression.*;
import dlc.template.*;
import dlc.util.HtmlParamEscaper;
import dlc.util.ProgramSerializer;
import dlc.util.VariableContainer;
import dlc.executor.ProgramThread;
import dlc.executor.CompileThread;

//import org.dom4j.*;
//import org.dom4j.io.*;

/**
 * $Id: CodeTreeNode.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий узел дерева JTree с описанием оператора.
 */
class CodeTreeNode extends DefaultMutableTreeNode{

    String m_insert;

	/**
	 * Конструктор
	 * @param title название узла дерева
	 * @param insert код для вставки в область редактирования кода (шаблонная строка)
	 */
    public CodeTreeNode( String title, String insert ){
        super( title );
        m_insert = insert;
    }

	/**
	 * Метод для получения шаблонной строки
	 */
    public String getInsertString(){
        return m_insert;
    }
}

/**
 * $Id: EditPane.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий основную панель среды ВЛР (для добавления в аплет или приложение).
 */

public class EditPane extends JPanel implements ActionListener, CaretListener,
        ItemListener,
        ErrorLog{

    JSplitPane  hSplit1;
    JSplitPane  hSplit2;
    JSplitPane  vSplit;
    JTextArea   m_input = new JTextArea( "" );
    JEditorPane m_code = new JEditorPane( "text/lang", "<h2>Code!</h2>" );
    JTextArea   m_output = new JTextArea( "" );

    //Списки переменных
    JList        m_list1, m_list2, m_list3;
    JLabel       m_codePosition = new JLabel("1:1");

    TemplateFactory m_codeTemplateFactory;

    int         m_runSleepAmount = 0;
    int         m_debugSleepAmount = 1;
    int         m_relPriority = 1; // was -1
    int         m_relCompPriority = -1;
/*
    JLabel      m_status = new JLabel( "Для отладки программы нажмите 'Выполнить'" ){
        public void paint( Graphics g ){
            Color c = g.getColor();
            g.setColor( Color.lightGray );
            g.fillRect( 0, 0, getWidth(), getHeight() );
            g.setColor( c );
            super.paint( g );
        }
    };
*/
    JButton     m_start = null;
    JButton     m_debug = null;
    JButton     m_step = null;
//    JButton     m_go = null;
    JButton     m_stop = null;

    //Choice      m_chStyle = new Choice();
	JComboBox   m_cbStyle = null;
    JButton     m_bVars = new JButton( "Редактировать" );
    JPanel      m_btnPane = new JPanel( new FlowLayout() );
    JPanel      m_treePane = new JPanel( new GridLayout(1,1) );
    JTree       m_opTree;

    boolean               m_bFirstInit = true;
    VariableContainer     m_vars = new VariableContainer();
    /**
     * Поток для запуска программы
     */
    ProgramThread         m_prgThread;
    /**
     * Поток для компиляции программы
     */
    CompileThread         m_compThread;
    /**
     * Поток для выполнения пошаговой отладки
     */
    ProgramThread         m_stepThread;

    /**
     * Признак начала отладки (true) или выполнения программы (false)
     */
    boolean               m_bDebug;

    /**
     * Режимы функционирования UI-оболочки
     * Режим редактирования кода
     */
    static int STATE_EDIT = 1;
    /**
     * Режимы функционирования UI-оболочки
     * Режим компиляции
     */
    static int STATE_COMPILE = 2;
    /**
     * Режимы функционирования UI-оболочки
     * Режим выполнения
     */
    static int STATE_RUN = 3;
    /**
     * Режимы функционирования UI-оболочки
     * Режим отладки
     */
    static int STATE_DEBUG = 4;

    /**
     * Текущее состояние UI-оболочки
     */
    int m_uiState = STATE_EDIT;

    Vector      m_codeNodes;
    CodeRuntime m_runtime;

    int m_stepDebugLine = -1;
    int m_stepBreakLine = -1;

    private void setupByAppletParameter( String param ){
        try{

            Hashtable deserialized = new Hashtable();
/*
&lt;?xml version=&quot;1.0&quot; encoding=&quot;Windows&amp;minus;1251&quot;?&gt; &lt;ProgramEnvironment&gt; &lt;Variable input=&quot;false&quot; output=&quot;false&quot; inner=&quot;true&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;a&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value&gt;&lt;!&amp;minus;&amp;minus;15&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;false&quot; output=&quot;false&quot; inner=&quot;true&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;b&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value&gt;&lt;!&amp;minus;&amp;minus;11&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;false&quot; output=&quot;false&quot; inner=&quot;true&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;01a&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value&gt;&lt;!&amp;minus;&amp;minus;11&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;true&quot; output=&quot;false&quot; inner=&quot;false&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;inx&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;3&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value N=&quot;0&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;1&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;2&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;3&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;4&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;5&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;6&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;7&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;8&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;9&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;false&quot; output=&quot;true&quot; inner=&quot;false&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;outx&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;3&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value N=&quot;0&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;1&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;2&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;3&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;4&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;5&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;6&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;7&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;8&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;9&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;false&quot; output=&quot;true&quot; inner=&quot;false&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;outy&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;3&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value N=&quot;0&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;1&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;2&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;3&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;4&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;5&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;6&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;7&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;8&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;9&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Variable input=&quot;true&quot; output=&quot;false&quot; inner=&quot;false&quot;&gt; &lt;Name&gt;&lt;!&amp;minus;&amp;minus;iny&amp;minus;&amp;minus;&gt;&lt;/Name&gt; &lt;Type&gt;&lt;!&amp;minus;&amp;minus;3&amp;minus;&amp;minus;&gt;&lt;/Type&gt; &lt;Value N=&quot;0&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;1&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;2&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;3&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;4&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;5&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;6&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;7&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;8&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;Value N=&quot;9&quot;&gt;&lt;!&amp;minus;&amp;minus;0&amp;minus;&amp;minus;&gt;&lt;/Value&gt; &lt;/Variable&gt; &lt;Code style=&quot;C&quot;&gt;&lt;!&amp;minus;&amp;minus;i = 0;&amp;lt;br/&amp;gt;&amp;lt;br/&amp;gt;max = in[0];&amp;lt;br/&amp;gt;maxIndex = 0;&amp;lt;br/&amp;gt;min = in[0];&amp;lt;br/&amp;gt;minIndex = 0;&amp;lt;br/&amp;gt;&amp;lt;br/&amp;gt;while( i &amp;lt; 30 ){&amp;lt;br/&amp;gt;   out[i] = in[i];&amp;lt;br/&amp;gt;   if( in[i] &amp;lt; min ){&amp;lt;br/&amp;gt;        min = in[i];&amp;lt;br/&amp;gt;        minIndex = i;&amp;lt;br/&amp;gt;   };&amp;lt;br/&amp;gt;   if( in[i] &amp;gt; max ){&amp;lt;br/&amp;gt;        max = in[i];&amp;lt;br/&amp;gt;        maxIndex = i;&amp;lt;br/&amp;gt;   };&amp;lt;br/&amp;gt;   i = i + 1;&amp;lt;br/&amp;gt;};&amp;lt;br/&amp;gt;&amp;lt;br/&amp;gt;i = maxIndex+1;&amp;lt;br/&amp;gt;while( i &amp;lt; minIndex ){&amp;lt;br/&amp;gt;    j = maxIndex+1;&amp;lt;br/&amp;gt;    while( j &amp;lt; i ){&amp;lt;br/&amp;gt;        if( out[i] &amp;gt; out[j] ){&amp;lt;br/&amp;gt;            tmp = out[j];&amp;lt;br/&amp;gt;            out[j] = out[i];&amp;lt;br/&amp;gt;            out[i] = tmp;&amp;lt;br/&amp;gt;        };&amp;lt;br/&amp;gt;        j = j + 1;&amp;lt;br/&amp;gt;    };&amp;lt;br/&amp;gt;    i = i + 1;&amp;lt;br/&amp;gt;};&amp;lt;br/&amp;gt;&amp;lt;br/&amp;gt;&amp;minus;&amp;minus;&gt;&lt;/Code&gt; &lt;/ProgramEnvironment&gt;
*/
            if( param != null && param.trim().length() > 0 ){

                param = HtmlParamEscaper.unescapeParam( param );

                if( !ProgramSerializer.deserialize( param, deserialized ) ){
                    System.out.println("ERROR: cant deserialize param-string");
                }
                else{

                    String sStyle = (String)deserialized.get("STYLE");
                    sStyle = m_codeTemplateFactory.getStyleByAlias( sStyle );
					m_cbStyle.setSelectedItem( sStyle );
					m_codeTemplateFactory.switchStyle( ""+m_cbStyle.getSelectedItem() );

			        ((SyntaxDocument)m_code.getDocument()).setKeywords( m_codeTemplateFactory.getKeywords() );
			        ((SyntaxDocument)m_code.getDocument()).setAutoComplete( m_codeTemplateFactory.getAutoComplete() );
                    /**
                     * TemplateFactory возвращает список строк (ifInsert, ifelseInsert, dowhileInsert, whileInsert),
                     *    в которых могут содержаться "заглушки" типа &lt;условие&gt;
                     * setPlaceholders() вытаскивает из таких выражений только заглушки и заполняет
                     * список placeholders в классе SyntaxDocument
                     */
			        ((SyntaxDocument)m_code.getDocument()).setPlaceholders( m_codeTemplateFactory.getPlaceholders() );

			        if( !m_codeTemplateFactory.isFree() ){
			            ((SyntaxDocument)m_code.getDocument()).setCommentsSyntax(
				             m_codeTemplateFactory.getExpressionTemplate().singleLineComment,
				             m_codeTemplateFactory.getExpressionTemplate().multiLineCommentStart,
				             m_codeTemplateFactory.getExpressionTemplate().multiLineCommentEnd );
			        }

                    m_code.setText( (String)deserialized.get("CODE") );
                    m_vars = new VariableContainer( (VariableContainer)deserialized.get("VARS") );
                }
            }
            else{

                //m_chStyle.select( 0 );
				m_cbStyle.setSelectedIndex( 0 );
                m_codeTemplateFactory.switchStyle( ""+m_cbStyle.getSelectedItem() );
                m_vars = new VariableContainer();
            }

//            updateVarLists();
//            codingStyleChanged();

        }catch( Exception exc ){
            //exc.printStackTrace();
        }
    }

    public EditPane( String appletParam ){
        setLayout( new BorderLayout() );

/*
        m_start.setIcon( new ImageIcon("resources/execute.png") );
        m_debug.setIcon( new ImageIcon("resources/startDebugger.png") );
        m_step.setIcon( new ImageIcon("resources/traceOver.png") );
        m_stop.setIcon( new ImageIcon("resources/suspend.png") );
*/
        ImageIcon img = new ImageIcon( getClass().getClassLoader().getResource("resources/execute.gif") );
        m_start = new JButton( img );
        img = new ImageIcon( getClass().getClassLoader().getResource("resources/startDebugger.gif") );
        m_debug = new JButton( img );
        img = new ImageIcon( getClass().getClassLoader().getResource("resources/traceOver.gif") );
        m_step = new JButton( img );
        //img = new ImageIcon( getClass().getClassLoader().getResource("resources/execute.gif") );
        //m_go = new JButton( img );
        img = new ImageIcon( getClass().getClassLoader().getResource("resources/suspend.gif") );
        m_stop = new JButton( img );

        EditorKit editorKit = new StyledEditorKit()
        {
            public Document createDefaultDocument()
            {
                return new SyntaxDocument();
            }
        };
        m_code.setEditorKitForContentType( "text/lang", editorKit );
        m_code.setContentType( "text/lang" );

        /**
         * hSplit1 - input и codenodes
         * vSplit  - hSplit и leftPane
         * hSplit2 - vSplit и output
         */
        hSplit1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        hSplit2 = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        vSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );

        { //input + codenodes => hSplit1
            hSplit1.add( initVarPane() );

            JPanel p2 = new JPanel( new BorderLayout() );
            p2.add( new JLabel("Исходный код:"), BorderLayout.NORTH );
            p2.add( new JScrollPane(m_code) );

            JPanel p3 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
            p3.add( m_codePosition );
            p2.add( p3, BorderLayout.SOUTH );

            hSplit1.add( p2 );
        }

        { //hSplit1( input + codenodes ) + leftPane => vSplit
            vSplit.add( hSplit1 );
//            vSplit.add( new JScrollPane(m_help) );//initLeftPane() );
//            m_treePane.add( initRightPane() );
            vSplit.add( m_treePane );
        }

        { //vSplit + m_output => hSplit2
            JPanel p = new JPanel( new BorderLayout() );
            p.add( new JLabel("Сообщения:"), BorderLayout.NORTH );
            p.add( new JScrollPane(m_output) );
            hSplit2.add( vSplit );
            hSplit2.add( p );
        }

        hSplit1.setResizeWeight( 0.1 );
        hSplit2.setResizeWeight( 0.2 );
        vSplit.setResizeWeight( 0.5 );

        add( hSplit2, BorderLayout.CENTER );
        add( initBottomPane(), BorderLayout.SOUTH );

        m_input.setTabSize( 4 );
        m_output.setTabSize( 4 );
        m_output.setEditable( false );

//        m_input.addFocusListener( this );
//        m_code.addFocusListener( this );
//        m_output.addFocusListener( this );

/*
        m_start.setIcon( new ImageIcon("resources/execute.png") );
        m_debug.setIcon( new ImageIcon("resources/startDebugger.png") );
        m_step.setIcon( new ImageIcon("resources/traceOver.png") );
        m_stop.setIcon( new ImageIcon("resources/suspend.png") );
*/
        m_start.addActionListener( this );
        m_debug.addActionListener( this );
        m_step.addActionListener( this );
        m_stop.addActionListener( this );
        m_bVars.addActionListener( this );
        //m_go.addActionListener( this );
        //m_cbStyle.addItemListener( this );

        m_start.setToolTipText("Запустить программу");
        m_debug.setToolTipText("Отладить программу в пошаговом режиме");
        m_step.setToolTipText("Выполнить до следующей строки");
        m_stop.setToolTipText("Остановить отладку");
        //m_go.setToolTipText("Продолжить выполнение программы");

        m_code.addCaretListener( this );

        initDefaultCodeTemplate();

//DEBUG - 27.10.2008 - is applet hangs ???
//DEBUG - 27.10.2008 - is applet hangs ???
//DEBUG - 27.10.2008 - is applet hangs ???
		codingStyleChanged();

        setupByAppletParameter( appletParam );

        switchUI( STATE_EDIT );

		m_cbStyle.addActionListener( this );


/*
        m_code.addMouseListener( new MouseAdapter(){
            public void mouseClicked( MouseEvent e ){
/*
 * Включение этого режима позволяет выделить строку, до которой программа
 * будет выполнена после нажатия кнопки "продолжить выполнение"


                int p = m_code.viewToModel( e.getPoint() );

                if( m_uiState == STATE_DEBUG ){
                    int line = ((SyntaxDocument)m_code.getDocument()).getLineByCaret( p );

                    if( line >= 0 ){
                        m_stepBreakLine = line;
                        ((SyntaxDocument)m_code.getDocument()).highlightDebugTargetString(
                                m_stepBreakLine
                        );
                    }
                }
*/
//            }
//        });


	//DEBUG
	//DEBUG
	//DEBUG
        {
/*
            int w = getWidth();
            int h = getHeight();

            hSplit1.setDividerLocation( (int)h/5 );
            hSplit2.setDividerLocation( (int)4*h/5 );
            if( m_codeTemplateFactory != null && !m_codeTemplateFactory.isFree() )
                vSplit.setDividerLocation( (int)4*w/6 );
            else
                vSplit.setDividerLocation( w );
            m_bFirstInit = false;

            m_code.requestFocus();
*/
        }

    }

    private void initDefaultCodeTemplate(){
        try{
            m_codeTemplateFactory = new TemplateFactoryXml2( "conf-new.xml" );
//            m_codeTemplateFactory = new TemplateFactoryTxt( "conf-new.txt" );
            String []styles = m_codeTemplateFactory.getStyles();

//			m_cbStyle = new JComboBox();

            for( int i = 0; i < styles.length; i++ )
                m_cbStyle.addItem( styles[i] );

            if( !m_codeTemplateFactory.switchStyle(styles[0]) ){
                throw new Exception();
            }

            //codingStyleChanged();

        }catch( Exception exc ){

		//DEBUG
		//DEBUG
		//DEBUG
            exc.printStackTrace();

            JOptionPane.showMessageDialog( this, "Ошибка инициализации стиля кодирования", "Ошибка", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void codingStyleChanged(){

        ((SyntaxDocument)m_code.getDocument()).setKeywords( m_codeTemplateFactory.getKeywords() );
        ((SyntaxDocument)m_code.getDocument()).setAutoComplete( m_codeTemplateFactory.getAutoComplete() );
        /**
         * TemplateFactory возвращает список строк (ifInsert, ifelseInsert, dowhileInsert, whileInsert),
         *    в которых могут содержаться "заглушки" типа &lt;условие&gt;
         * setPlaceholders() вытаскивает из таких выражений только заглушки и заполняет
         * список placeholders в классе SyntaxDocument
         */
        ((SyntaxDocument)m_code.getDocument()).setPlaceholders( m_codeTemplateFactory.getPlaceholders() );

        if( !m_codeTemplateFactory.isFree() ){

            ((SyntaxDocument)m_code.getDocument()).setCommentsSyntax(
             m_codeTemplateFactory.getExpressionTemplate().singleLineComment,
             m_codeTemplateFactory.getExpressionTemplate().multiLineCommentStart,
             m_codeTemplateFactory.getExpressionTemplate().multiLineCommentEnd );

            m_treePane.removeAll();
            m_treePane.add( initRightPane() );
            m_treePane.revalidate();

            m_btnPane.add( m_debug );
            m_btnPane.add( m_step );
            m_btnPane.add( m_start );
            m_btnPane.add( m_stop );
            m_btnPane.revalidate();
            vSplit.setDividerLocation( (int)4*getWidth()/6 );
        }
        else{
            m_treePane.removeAll();
            m_treePane.revalidate();
            vSplit.setDividerLocation( getWidth() );
            m_btnPane.removeAll();
            m_btnPane.revalidate();
        }

        showMessage( "Стиль кодирования изменен на: " + m_cbStyle.getSelectedItem() );
    }

    private void updateVarLists(){
        updateVarLists( true );
    }

    private void updateVarLists( boolean bRecreate ){

        if( bRecreate ){
            m_list1.setModel( new VarListModel(m_vars, VarListModel.TYPE_INPUT) );
            m_list2.setModel( new VarListModel(m_vars, VarListModel.TYPE_OUTPUT) );
            m_list3.setModel( new VarListModel(m_vars, VarListModel.TYPE_INNER) );
        }

        m_list1.repaint();
        m_list2.repaint();
        m_list3.repaint();
    }

	/**
	 * Перегруженный метод для установки размеров SplitPanes
	 */
    public void paintComponent( Graphics g ){

		super.paintComponent( g );

//System.out.println( "debug::paintComponent() - begin" );

        if( m_bFirstInit ){
            m_bFirstInit = false;

//System.out.println( "(1)" );

			updateVarLists();
//System.out.println( "(2)" );
			codingStyleChanged();
//System.out.println( "(3)" );

            int w = getWidth();
            int h = getHeight();

//System.out.println( "(4)" );
            hSplit1.setDividerLocation( (int)h/5 );
            hSplit2.setDividerLocation( (int)4*h/5 );
            if( m_codeTemplateFactory != null && !m_codeTemplateFactory.isFree() )
                vSplit.setDividerLocation( (int)4*w/6 );
            else
                vSplit.setDividerLocation( w );

//System.out.println( "(5)" );

            m_code.requestFocus();
        }

//		super.paintComponent( g );


//System.out.println( "debug::paintComponent()" );
//        super.paint( g );

    }

    private JPanel initVarPane(){
        JPanel res = new JPanel();

        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.001;

        res.setLayout( l );

        JLabel lInput = new JLabel("Входные переменные");
        JLabel lOutput = new JLabel("Выходные переменные");
        JLabel lBlock = new JLabel("Внутренние переменные");
        l.setConstraints( lInput, c );
        res.add( lInput );
        l.setConstraints( lOutput, c );
        res.add( lOutput );
        c.gridwidth = c.REMAINDER;
        l.setConstraints( lBlock, c );
        res.add( lBlock );
        c.gridwidth = 1;

        String []l1 = new String[10];
        String []l2 = new String[15];
        String []l3 = new String[5];

        for( int i = 0; i < l1.length; i++ )
            l1[i] = "X" + i;
        for( int i = 0; i < l2.length; i++ )
            l2[i] = "Y" + i;
        for( int i = 0; i < l3.length; i++ )
            l3[i] = "i" + i;

        m_list1 = new JList();
        m_list2 = new JList();
        m_list3 = new JList();

        m_list1.setModel( new VarListModel(m_vars, VarListModel.TYPE_INPUT) );
        m_list2.setModel( new VarListModel(m_vars, VarListModel.TYPE_OUTPUT) );
        m_list3.setModel( new VarListModel(m_vars, VarListModel.TYPE_INNER) );

        JScrollPane jsp1 = new JScrollPane( m_list1 );
        JScrollPane jsp2 = new JScrollPane( m_list2 );
        JScrollPane jsp3 = new JScrollPane( m_list3 );

        c.weighty = 1.0;
        l.setConstraints( jsp1, c );
        res.add( jsp1 );
        l.setConstraints( jsp2, c );
        res.add( jsp2 );
        c.gridwidth = c.REMAINDER;
        l.setConstraints( jsp3, c );
        res.add( jsp3 );

        JPanel varPane = new JPanel( new BorderLayout() );
        JPanel varPaneButtons = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        varPaneButtons.add( m_bVars );
        varPane.add( res );
        varPane.add( varPaneButtons, BorderLayout.SOUTH );

        return varPane;
    }

    private JPanel initBottomPane(){

		m_cbStyle = new JComboBox();

        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.001;
        c.insets = new Insets( 3, 3, 3, 3 );

        JPanel res = new JPanel( l );

        JPanel pStyle = new JPanel( new FlowLayout() );
        pStyle.add( new JLabel("Стиль кодирования: " ) );
        pStyle.add( m_cbStyle );
        c.fill = c.NONE;
        c.anchor = c.WEST;
        l.setConstraints( pStyle, c );
        res.add( pStyle );

        c.anchor = c.EAST;
        c.gridwidth = c.REMAINDER;
        l.setConstraints( m_btnPane, c );
        res.add( m_btnPane );

        c.fill = c.BOTH;

        return res;
    }

    private JScrollPane initRightPane(){

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Библиотека" );
        JTree jTree = new JTree( root );

        DefaultMutableTreeNode controls = new DefaultMutableTreeNode("Управляющие конструкции" );
        DefaultMutableTreeNode ariphmetic = new DefaultMutableTreeNode("Арифметические операторы" );
		DefaultMutableTreeNode ariphFuncs = new DefaultMutableTreeNode("Функции" );
        DefaultMutableTreeNode logic = new DefaultMutableTreeNode("Логические операторы" );
        DefaultMutableTreeNode equal = new DefaultMutableTreeNode("Операторы сравнения" );
        DefaultMutableTreeNode comms = new DefaultMutableTreeNode("Комментарии" );
		DefaultMutableTreeNode assign = new DefaultMutableTreeNode("Оператор присваивания");

		CodeTreeNode nodeEq = new CodeTreeNode(":=", m_codeTemplateFactory.getExpressionTemplate().assign);
		assign.add( nodeEq );

        controls.add( new CodeTreeNode( "Если", m_codeTemplateFactory.getIfTemplate().ifInsert ) );
        controls.add( new CodeTreeNode("Если/Иначе", m_codeTemplateFactory.getIfTemplate().ifelseInsert ) );
        controls.add( new CodeTreeNode("Цикл с постусловием", m_codeTemplateFactory.getDoWhileTemplate().dowhileInsert ) );
        controls.add( new CodeTreeNode("Цикл с предусловием", m_codeTemplateFactory.getWhileTemplate().whileInsert ) );

        ariphmetic.add( new CodeTreeNode("+", m_codeTemplateFactory.getExpressionTemplate().add ) );
        ariphmetic.add( new CodeTreeNode("-", m_codeTemplateFactory.getExpressionTemplate().sub) );
        ariphmetic.add( new CodeTreeNode("*", m_codeTemplateFactory.getExpressionTemplate().mul) );
        ariphmetic.add( new CodeTreeNode("/", m_codeTemplateFactory.getExpressionTemplate().div) );
        ariphmetic.add( new CodeTreeNode("mod", m_codeTemplateFactory.getExpressionTemplate().mod) );
//        ariphmetic.add( new CodeTreeNode(":=", m_codeTemplateFactory.getExpressionTemplate().assign) );

		ariphFuncs.add( new CodeTreeNode("SQRT", m_codeTemplateFactory.getExpressionTemplate().funcSqrtIns ) );
		ariphFuncs.add( new CodeTreeNode("SIN", m_codeTemplateFactory.getExpressionTemplate().funcSinIns ) );
		ariphFuncs.add( new CodeTreeNode("COS", m_codeTemplateFactory.getExpressionTemplate().funcCosIns ) );
		ariphFuncs.add( new CodeTreeNode("TAN", m_codeTemplateFactory.getExpressionTemplate().funcTanIns ) );
		ariphFuncs.add( new CodeTreeNode("ASIN", m_codeTemplateFactory.getExpressionTemplate().funcAsinIns ) );
		ariphFuncs.add( new CodeTreeNode("ACOS", m_codeTemplateFactory.getExpressionTemplate().funcAcosIns ) );
		ariphFuncs.add( new CodeTreeNode("ATAN", m_codeTemplateFactory.getExpressionTemplate().funcAtanIns ) );
		ariphFuncs.add( new CodeTreeNode("ROUND", m_codeTemplateFactory.getExpressionTemplate().funcRoundIns ) );

        logic.add( new CodeTreeNode("И", m_codeTemplateFactory.getExpressionTemplate().and) );
        logic.add( new CodeTreeNode("ИЛИ", m_codeTemplateFactory.getExpressionTemplate().or) );
        logic.add( new CodeTreeNode("НЕ", m_codeTemplateFactory.getExpressionTemplate().not) );

        equal.add( new CodeTreeNode("=", m_codeTemplateFactory.getExpressionTemplate().eq) );
        equal.add( new CodeTreeNode("<>", m_codeTemplateFactory.getExpressionTemplate().neq) );
        equal.add( new CodeTreeNode("<", m_codeTemplateFactory.getExpressionTemplate().lt) );
        equal.add( new CodeTreeNode("<=", m_codeTemplateFactory.getExpressionTemplate().lte) );
        equal.add( new CodeTreeNode(">", m_codeTemplateFactory.getExpressionTemplate().gt) );
        equal.add( new CodeTreeNode(">=", m_codeTemplateFactory.getExpressionTemplate().gte) );

		comms.add( new CodeTreeNode( "Однострочные", m_codeTemplateFactory.getExpressionTemplate().singleLineCommentInsert ) );
		if( m_codeTemplateFactory.getExpressionTemplate().multiLineCommentInsert.length() > 0 )
			comms.add( new CodeTreeNode( "Многострочные", m_codeTemplateFactory.getExpressionTemplate().multiLineCommentInsert ) );
/*
        root.add( controls );
        root.add( ariphmetic );
		root.add( ariphFuncs );
        root.add( logic );
        root.add( equal );
		root.add( comms );
*/
		root.add( controls );
		root.add( assign );
		root.add( ariphmetic );
		root.add( logic );
		root.add( equal );
		root.add( comms );
		root.add( ariphFuncs );

        jTree.addMouseListener( new MouseAdapter(){
            public void mouseClicked( MouseEvent e ){
                if( e.getClickCount() == 2 ){
                    //System.out.println( "e.src: " + e.getSource() );
                    JTree jTree = (JTree)e.getSource();

					if( !jTree.isEnabled() )
						return;

                    Object node = jTree.getSelectionPath().getLastPathComponent();
                    if( node instanceof CodeTreeNode ){
                        String ins = ((CodeTreeNode)node).getInsertString();
                        int    p = m_code.getCaretPosition();
                        try{
                            ((SyntaxDocument)m_code.getDocument()).insertString( p, ins, null );
                        }catch( Exception exc ){
                            //System.out.println("p(<0???): " + p + ", String to insert: " + ((CodeTreeNode)node).getInsertString() );
                        }
                    }
                }
            }
        });

        m_opTree = jTree;

        jTree.expandPath( new TreePath( root.getPath() ) );
        jTree.expandPath( new TreePath( controls.getPath() ) );
        jTree.expandPath( new TreePath( ariphmetic.getPath() ) );
        jTree.expandPath( new TreePath( ariphFuncs.getPath() ) );
        jTree.expandPath( new TreePath( logic.getPath() ) );
        jTree.expandPath( new TreePath( equal.getPath() ) );
        jTree.expandPath( new TreePath( comms.getPath() ) );
		jTree.expandPath( new TreePath( assign.getPath() ) );

        return new JScrollPane( jTree );
    }

	/**
	 * Активация/деактивация 
	 */
    public void switchUI( int state ){

        m_uiState = state;

        if( state == STATE_EDIT ){
            m_debug.setEnabled( true );
            m_start.setEnabled( true );
            m_stop.setEnabled( false );
            m_bVars.setEnabled( true );
            m_step.setEnabled( false );

            m_code.setEditable( true );
            if( m_opTree != null )
                m_opTree.setEnabled( true );
            m_bVars.setEnabled( true );
            m_cbStyle.setEnabled( true );

            m_start.setToolTipText( "Запустить программу" );
        }
        else if( state == STATE_COMPILE ){
            m_debug.setEnabled( false );
            m_start.setEnabled( false );
            m_step.setEnabled( false );
            m_stop.setEnabled( true );
            m_bVars.setEnabled( false );

            m_code.setEditable( false );
            if( m_opTree != null )
                m_opTree.setEnabled( false );
            m_bVars.setEnabled( false );
            m_cbStyle.setEnabled( false );
        }
        else if( state == STATE_RUN ){
            m_debug.setEnabled( false );
            m_start.setEnabled( false );
            m_step.setEnabled( false );
            m_stop.setEnabled( true );
            m_bVars.setEnabled( false );

            m_code.setEditable( false );
            if( m_opTree != null )
                m_opTree.setEnabled( false );
            m_bVars.setEnabled( false );
            m_cbStyle.setEnabled( false );
        }
        else if( state == STATE_DEBUG ){
            m_debug.setEnabled( false );
            m_start.setEnabled( true );
            m_step.setEnabled( true );
            m_stop.setEnabled( true );
            m_bVars.setEnabled( false );

            m_code.setEditable( false );
            if( m_opTree != null )
                m_opTree.setEnabled( false );
            m_bVars.setEnabled( false );
            m_cbStyle.setEnabled( false );

            m_start.setToolTipText( "Продолжить выполнение" );
        }

//System.out.println( "debug::switchUI - revalidate..." );

//        m_btnPane.revalidate();

//System.out.println( "debug::switchUI - end." );
    }

	/**
	 * Обработчик событий
	 */
    public void actionPerformed( ActionEvent e ){

        if( e.getSource() == m_bVars ){
            VarsDialog dlg = new VarsDialog( m_vars );
            updateVarLists();
        }

        else if( e.getSource() == m_cbStyle ){
            showMessage( "" );

            if( JOptionPane.showOptionDialog( this,
                    "При смене стиля произойдет очистка кода, продолжить?",
                    "Внимание", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    new String[]{ "Да", "Нет" }, null ) == JOptionPane.YES_OPTION ){

                m_code.setText( "" );
                m_codeTemplateFactory.switchStyle( "" + m_cbStyle.getSelectedItem() );

                codingStyleChanged();
            }
            else
                m_cbStyle.setSelectedItem( m_codeTemplateFactory.getCurrentStyle() );
        }

        else if( e.getSource() == m_stepThread ){

            boolean bExceptionOrFinish = false;

            try{

                m_stepBreakLine = -1;

                if( m_stepThread.getException() != null )
                    throw m_stepThread.getException();

                if( m_runtime.IP < m_codeNodes.size() ){
                    int iCurrLine = ((CodeNode)m_codeNodes.elementAt(m_runtime.IP)).getSourceToken().line;
                    ((SyntaxDocument)m_code.getDocument()).highlightDebugString( iCurrLine );
                    showMessage("Оператор выполнен." );

                    if( m_runtime.IP < m_codeNodes.size() )
                        m_stepDebugLine = iCurrLine;
                    else
                        m_stepDebugLine = -1;
                }
                else{
                    ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );
                    bExceptionOrFinish = true;
                    showMessage("Программа выполнена." );
                }
            }catch( CodeRTException ce ){
                showError( "Ошибка выполнения: " + ce.getMessage() );
                m_code.requestFocus();
				try{
	                m_code.setCaretPosition( ce.getSourceToken().position );
	                ((SyntaxDocument)m_code.getDocument()).highlightError( ce.getSourceToken().line );
				}catch( Exception posExc ){}
                bExceptionOrFinish = true;
            }catch( Exception exc ){
//                exc.printStackTrace();
                showError( "Ошибка времени выполнения." );
                bExceptionOrFinish = true;
            }

            updateVarLists();

            if( bExceptionOrFinish ){
                switchUI( STATE_EDIT );
                m_stepThread = null;
            }
            else
                switchUI( STATE_DEBUG );
        }
        else if( e.getSource() == m_step ){

            switchUI( STATE_RUN );

            m_stepThread = new ProgramThread( m_runtime, m_codeNodes, m_vars, this, m_debugSleepAmount );
            m_stepThread.setPriority( Thread.currentThread().getPriority() + m_relPriority );
            m_stepThread.setToLine( m_stepDebugLine );
            m_stepThread.setBreakLine( m_stepBreakLine );
            m_stepThread.start();
//System.out.println("step to line: " + m_stepDebugLine );
//System.out.println("\tstep break line: " + m_stepBreakLine );
        }
/*
        else if( e.getSource() == m_go ){
            switchUI( STATE_RUN );

            m_stepThread = new ProgramThread( m_runtime, m_codeNodes, m_vars, this );
            m_stepThread.setPriority( Thread.currentThread().getPriority()-1 );
            m_stepThread.setToLine( -1 );
            m_stepThread.setBreakLine( m_stepBreakLine );
            m_stepThread.start();
        }
*/
        else if( e.getSource() == m_stop ){

            if( m_compThread != null ){
                try{
                    m_compThread.stopThread();
                    m_compThread.wait( 1000 );
                    m_compThread.interrupt();
                }catch( Exception exc ){}
                m_compThread = null;
            }

            if( m_prgThread != null ){
                try{
                    m_prgThread.stopThread();
                    m_prgThread.wait();
                }catch( Exception exc ){

                }
                m_prgThread = null;
            }

            showMessage( "Выполнение программы прервано.");

            ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );

            switchUI( STATE_EDIT );
            updateVarLists();
        }
        else if( e.getSource() == m_debug ){

            showMessage( "Включен режим пошаговой отладки." );

            ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );
            ((SyntaxDocument)m_code.getDocument()).highlightError( -1 );

            m_stepDebugLine = 1;

            m_bDebug = true;

            switchUI( STATE_COMPILE );

            if( !prepareToRun() ){
                switchUI( STATE_EDIT );
                return;
            }
        }
        else if( e.getSource() == m_compThread ){

            try{
                if( m_compThread.getException() != null ){
                    switchUI( STATE_EDIT );
                    throw m_compThread.getException();
                }
                ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );
            }catch( CompileException compExc ){
                showError( "Ошибка компиляции: " + compExc.getMessage() );
//compExc.printStackTrace();
                m_code.requestFocus();
                try{
                    m_code.setCaretPosition( compExc.p );
                    ((SyntaxDocument)m_code.getDocument()).highlightError( compExc );
                }catch( Exception exc ){}
                return;
            }catch( Exception exc ){
//exc.printStackTrace();
                showError( "Ошибка компиляции." );
                return;
            }
            m_compThread = null;

            //updateVarLists();

            if( m_bDebug ){
                //Начало отладки программы
                m_runtime = new CodeRuntime();
                try{
					m_stepDebugLine = ((CodeNode)m_codeNodes.elementAt(m_runtime.IP)).getSourceToken().line;
                    ((SyntaxDocument)m_code.getDocument()).highlightDebugString( ((CodeNode)m_codeNodes.elementAt(m_runtime.IP)).getSourceToken().line );
                }catch( Exception hlExc ){
                    m_stepDebugLine = -1;
                }

                switchUI( STATE_DEBUG );
            }
            else{
                //Начало работы программы
                switchUI( STATE_RUN );
                updateVarLists();

                m_runtime   = new CodeRuntime();
                m_prgThread = new ProgramThread( m_runtime, m_codeNodes, m_vars, this, m_runSleepAmount );
                m_prgThread.setPriority( Thread.currentThread().getPriority() + m_relPriority );
                m_prgThread.start();
            }
        }
        else if( e.getSource() == m_prgThread ){
            //Программа выполнена
            switchUI( STATE_EDIT );

            try{

                if( m_prgThread.getException() != null )
                    throw m_prgThread.getException();

                ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );
                showMessage( "Программа выполнена." );
            }catch( CodeRTException ce ){
                showError( "Ошибка выполнения: " + ce.getMessage() );
                m_code.requestFocus();
                m_code.setCaretPosition( ce.getSourceToken().position );
                ((SyntaxDocument)m_code.getDocument()).highlightError( ce.getSourceToken().line );
            }catch( Exception exc ){
//                exc.printStackTrace();
                showError( "Ошибка времени выполнения." );
            }

            m_prgThread = null;

            updateVarLists();
        }
        else if( e.getSource() == m_start ){

            if( m_uiState == STATE_DEBUG ){
                switchUI( STATE_RUN );

                m_stepThread = new ProgramThread( m_runtime, m_codeNodes, m_vars, this, m_runSleepAmount );
                m_stepThread.setPriority( Thread.currentThread().getPriority() + m_relPriority );
                m_stepThread.setToLine( -1 );
                m_stepThread.setBreakLine( m_stepBreakLine );
                m_stepThread.start();
            }
            else{
                showMessage( "" );
                ((SyntaxDocument)m_code.getDocument()).highlightDebugString( -1 );
                ((SyntaxDocument)m_code.getDocument()).highlightError( -1 );

                switchUI( STATE_COMPILE );

                m_bDebug = false;

                if( !prepareToRun() ){
                    switchUI( STATE_EDIT );
                    return;
                }
            }
        }
    }

	/**
	 * Обработка события - смена стиля кодирования
	 */
    public void itemStateChanged( ItemEvent e ){
        if( e.getSource() == m_cbStyle ){
            showMessage( "" );

            if( JOptionPane.showOptionDialog( this,
                    "При смене стиля произойдет очистка кода, продолжить?",
                    "Внимание", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    new String[]{ "Да", "Нет" }, null ) == JOptionPane.YES_OPTION ){

                m_code.setText( "" );
                m_codeTemplateFactory.switchStyle( "" + m_cbStyle.getSelectedItem() );

                codingStyleChanged();
            }
            else
                m_cbStyle.setSelectedItem( m_codeTemplateFactory.getCurrentStyle() );
        }
    }

    private boolean prepareToRun(){

        ((SyntaxDocument)m_code.getDocument()).highlightError( -1 );

        String progText = m_code.getText();
        if( progText.trim().length() == 0 ){
            JOptionPane.showMessageDialog( this, "Текст программы пуст.", "Ошибка", JOptionPane.WARNING_MESSAGE );
            return false;
        }
        else{

            progText += "\r\n";

            if( m_vars.size() == 0 ){
                JOptionPane.showMessageDialog( this, "Список переменных пуст", "Внимание", JOptionPane.WARNING_MESSAGE );
                VarsDialog dlg = new VarsDialog( m_vars );

                if( m_vars.size() == 0 ){
                    return false;
                }
            }
            for( Enumeration en = m_vars.elements(); en.hasMoreElements(); ){
                try{
                    ((Variable)en.nextElement()).init();
                }catch( Exception exc ){}
            }              
            updateVarLists();

            m_codeNodes = new Vector();
            m_compThread = new CompileThread( progText, m_codeTemplateFactory, m_codeNodes, m_vars, this );
            m_compThread.setPriority( Thread.currentThread().getPriority() + m_relCompPriority );
            m_compThread.start();
        }
        return true;
    }

	/**
	 * Перегруженный метод - обновление положения каретки
	 */
    public void caretUpdate( CaretEvent e ){
        if( e.getSource() == m_code ){
            int p = e.getDot();
            int line = m_code.getDocument().getDefaultRootElement().getElementIndex(p);
            p -= m_code.getDocument().getDefaultRootElement().getElement(line).getStartOffset();

            p++;
            line++;
            m_codePosition.setText( line + ":" + p );
        }
    }

	/**
	 * Показать сообщение об ошибке в окне сообщений
	 */
    public void showError( String error ){
        m_output.setText( error );
    }
	/**
	 * Показать сообщение в окне сообщений
	 */
    public void showMessage( String message ){
        m_output.setText( message );
    }
	/**
	 * Показать предупреждение в окне сообщений
	 */
    public void showWarning( String message ){
        m_output.setText( message );
    }

	/**
	 * Получить результат (код + переменные) в сериализованном виде (XML)
	 */
    public String getResults(){
        if( m_code.getText().trim().length() == 0 && m_vars.size() == 0 ){
            return "";
        }

        //return ProgramSerializer.serialize( m_codeTemplateFactory.getCurrentStyle(), m_code.getText(), m_vars );
        return ProgramSerializer.serialize( m_codeTemplateFactory.getCurrentStyleAlias(), m_code.getText(), m_vars );
    }
}
