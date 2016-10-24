
import dlc.expression.VariableArray;
import dlc.expression.Variable;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Random;
import java.util.Enumeration;
import javax.swing.*;

/**
 * $Id: ArrayEditDlg.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий диалог для редактирования массивов.
 */

public class ArrayEditDlg extends JDialog implements ActionListener{

    VariableArray   m_arr, m_origVar;

    JButton         m_add = new JButton("Добавить");
    JButton         m_remove = new JButton("Удалить");
    JButton         m_save = new JButton("Сохранить");
    JButton         m_back = new JButton("Назад");
    JButton         m_fillRandom = new JButton("Случайно");

    Hashtable       m_cbIndices = new Hashtable();
    JRadioButton    m_cbPrevious = null;
    ButtonGroup     m_cbg = new ButtonGroup();

    Container       m_contentList;
    Container       m_contentPane;
    JDialog         m_backDialog;
    boolean         m_bOK = false;

	/**
	 * Конструктор
	 * @param arr исходный массив (с заранее заданным количеством элементов)
	 * @param back диалог для перехода по кнопке "Назад"
	 */
    public ArrayEditDlg( VariableArray arr, JDialog back ){
        super();
        setModal( true );
        setTitle( "Редактирование массива" );
        m_arr = new VariableArray( arr );
        m_origVar = arr;
        m_backDialog = back;

        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
/*
        addWindowListener( new WindowAdapter(){
            public void windowClosing( WindowEvent e ){
                dispose();
            }
        });
*/
        if( m_backDialog != null ){
            m_save.setText( "Готово" );
            m_back.setVisible( true );
        }
        else{
            m_back.setVisible( false );
        }

        init();

        m_add.addActionListener( this );
        m_remove.addActionListener( this );
        m_save.addActionListener( this );
        m_back.addActionListener( this );
        m_fillRandom.addActionListener( this );

        int cw = 500;
        int ch = 400;
        int cx = -(cw>>1) + (getToolkit().getScreenSize().width>>1);
        int cy = -(ch>>1) + (getToolkit().getScreenSize().height>>1);

        setLocation( cx, cy );
        setSize( cw, ch );
        setResizable( false );
        setVisible( true );
    }

    public ArrayEditDlg( VariableArray arr ){
        this( arr, null );
    }

    /**
     * Инициализация панели с элементами массива (с прокруткой) 
     */
    private JPanel initContent(){

        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.001;
        c.insets = new Insets( 3, 3, 3, 3 );

        JPanel res = new JPanel();
        res.setLayout( l );

        m_cbIndices = new Hashtable();

        for( int i = 0; i < m_arr.elems.size(); i++ ){
            JRadioButton cb = new JRadioButton( m_arr.name + "[" + i + "] := " );
            JTextField tf = new JTextField( "" + m_arr.elems.elementAt(i) );
            m_cbIndices.put( cb, new Integer(i) );
            c.weightx = 0.001;
            c.gridwidth = 1;
            l.setConstraints( cb, c );
            res.add( cb );
            c.weightx = 1.0;
            c.gridwidth = c.REMAINDER;
            l.setConstraints( tf, c );
            res.add( tf );

            cb.getModel().setGroup( m_cbg );
        }

        GridBagLayout l1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = c1.HORIZONTAL;
        c1.gridheight = 1;
        c1.gridwidth = 1;
        c1.weightx = 1.0;
        c1.weighty = 0.001;
        c1.insets = new Insets( 3, 3, 3, 3 );
        c1.anchor = c1.NORTH;

        m_contentPane = res;

        JPanel p = new JPanel( l1 );
        l1.setConstraints( res, c1 );
        p.add( res );

        return p;
    }

    private void init(){

        JPanel res = initContent();
        m_contentList = res;

        getContentPane().add( new JScrollPane( res ) );

        JPanel btn = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        btn.add( m_add );
        btn.add( m_remove );
        btn.add( m_fillRandom );
        btn.add( new JLabel(" ") );
        btn.add( m_back );
        btn.add( m_save );

        getContentPane().add( btn, BorderLayout.SOUTH );
    }

    private void storeData( boolean bSilent ) throws Exception{
        int stride = 2;
        for( int i = 0; i < m_contentPane.getComponentCount(); i += stride ){
            JTextField tf = (JTextField)m_contentPane.getComponent(i + 1);
            try{
                m_arr.setElementAt( tf.getText(), (int)(i/stride) );
            }catch( Exception exc ){
                if( bSilent ){
                    m_arr.elems.setElementAt( tf.getText(), (int)(i/stride) );
                }
                else{
                    JOptionPane.showMessageDialog( this, "Тип данных элемента " + (int)(i/stride) + " не соответствует типу массива '" +
                            Variable.sTypes[ m_arr.iType ] + "'" );
                }
            }
        }
    }

	/**
	 * Обработчик событий
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_add ){
            if( m_arr.elems.size() >= VariableArray.MAX_SIZE ){
                JOptionPane.showMessageDialog( this, "Превышен лимит на количество элементов массива", "Ошибка", JOptionPane.ERROR_MESSAGE );
                return;
            }
            m_arr.addElement();
            JScrollPane c = (JScrollPane)m_contentList.getParent().getParent();
            c.getViewport().remove( m_contentList );
            c.setViewportView( (m_contentList=initContent()) );
            m_contentList.validate();
        }
        else if( e.getSource() == m_fillRandom ){
            Random rnd = new Random();
            if( m_arr.iType == Variable.TYPE_ARR_STR ){
                int iDiff = 'Z' - 'A';
                for( int i = 0; i < m_arr.elems.size(); i++ ){
                    int itCount = 50;
                    Character newChar = new Character( (char)(rnd.nextInt( iDiff ) + 'A') );
                    while( itCount > 0 && m_arr.elems.contains(newChar) ){
                        newChar = new Character( (char)(rnd.nextInt( iDiff ) + 'A') );
                        itCount--;
                    }
                    m_arr.elems.setElementAt( newChar, i );
                }
            }
            else{
                for( int i = 0; i < m_arr.elems.size(); i++ ){
                    int itCount = 50;
                    Integer newInt = new Integer( rnd.nextInt(200) );
                    while( itCount > 0 && m_arr.elems.contains(newInt) ){
                        newInt = new Integer( rnd.nextInt(200) );
                        itCount--;
                    }
                    m_arr.elems.setElementAt( newInt, i );
                }
            }
            JScrollPane c = (JScrollPane)m_contentList.getParent().getParent();
            c.getViewport().remove( m_contentList );
            c.setViewportView( (m_contentList=initContent()) );
            m_contentList.validate();
        }
        else if( e.getSource() == m_remove ){
            try{

                storeData( true );
                Integer index = null;
                for( Enumeration en = m_cbIndices.keys(); en.hasMoreElements(); ){
                    JRadioButton jb = (JRadioButton)en.nextElement();
                    if( jb.isSelected() ){
                        index = (Integer)m_cbIndices.get( jb );
                    }
                }

                if( index == null )
                    throw new Exception();

                m_arr.removeElement( index.intValue() );

                JScrollPane c = (JScrollPane)m_contentList.getParent().getParent();
                c.getViewport().remove( m_contentList );
                c.setViewportView( (m_contentList=initContent()) );
                m_contentList.validate();
            }catch( Exception exc ){
                //exc.printStackTrace();

                JOptionPane.showMessageDialog(this, "Выберите элемент массива", "Внимание", JOptionPane.WARNING_MESSAGE );
            }
        }
        else if( e.getSource() == m_save ){

            try{
                storeData( true );
            }catch( Exception exc ){
//                exc.printStackTrace();
            }

            if( m_arr.elems.size() == 0 ){
                JOptionPane.showMessageDialog( this, "Укажите хотя бы один элемент массива", "Внимание", JOptionPane.ERROR_MESSAGE );
                return;
            }

            String indices = "";
            for( int i = 0; i < m_arr.elems.size(); i++ ){
                if( !m_arr.isValidElement(i) ){
                    indices += (indices.length() > 0 ? ",":"") + i;
                }
            }
            if( indices.length() > 0 ){
                JOptionPane.showMessageDialog( this, "Требуется заполнить все элементы в соответствии с типом массива (" +
                        Variable.sTypes[ m_arr.iType ] + ")", "Внимание",
                        JOptionPane.ERROR_MESSAGE );
                return;
            }

            m_origVar.setFrom( m_arr );
            m_bOK = true;

            dispose();
        }
        else if( e.getSource() == m_back ){
            dispose();
        }
    }

	/**
	 * Проверка статуса закрытия диалога
     */
    public boolean isOk(){
        return m_bOK;
    }

	/**
	 * Получить сформированную переменную-массив
	 */
    public VariableArray getResultArray(){
        return m_origVar;
    }
/*
    public static void main( String []args ){
        VariableArray vArr = new VariableArray( "a", Variable.TYPE_ARR_INT );
        try{
            for( int i = 0; i < 100; i++ ){
                vArr.addElement( "0" );
            }
            new ArrayEditDlg( vArr );
        }catch( Exception exc ){
            exc.printStackTrace();
        }
    }
*/

}
