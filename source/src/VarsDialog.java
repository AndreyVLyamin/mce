
import dlc.expression.Variable;
import dlc.expression.VariableArray;
import dlc.util.VariableContainer;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.util.*;

/**
 * $Id: VarsDialog.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий диалог для редактирования списка переменных.
 */
public class VarsDialog extends JDialog implements ActionListener, ItemListener{

    VariableContainer vars;
    VariableContainer m_origVars;

    JButton   m_add = new JButton( "Добавить" );
    JButton   m_remove = new JButton( "Удалить" );
    JButton   m_close = new JButton( "Закрыть" );
    JButton   m_save = new JButton( "Сохранить" );

    ButtonGroup   m_selCBG = new ButtonGroup();
    Hashtable     m_selNames = new Hashtable();
    Hashtable     m_choiceNames = new Hashtable();

    /**
     * Используется для обновления списка переменных
     */
    JPanel        m_contentPane;
    /**
     * Используется для получения значений, указанных пользователем в списке переменных
     */
    Container     m_listPane;

    Hashtable     m_varButtons;

    private void addVar(){

        storeChanges();

        CreateVarDialog dlg = new CreateVarDialog( this, "", vars );
        if( dlg.isOK() ){
            vars.put( dlg.getNewName(), dlg.getResult() );
            reinitContent();
        }
    }
    private void removeVar(){
        storeChanges();

        String varName = null;
        for( Enumeration en = m_selNames.keys(); en.hasMoreElements(); ){
            JRadioButton jb = (JRadioButton)en.nextElement();
            if( jb.isSelected() ){
                varName = (String)m_selNames.get( jb );
                break;
            }
        }

        if( varName == null ){
            JOptionPane.showMessageDialog( this, "Выберите переменную", "Внимание", JOptionPane.WARNING_MESSAGE );
            return;
        }

        vars.remove( varName );
        //m_selCBG.setSelectedCheckbox( null );

        reinitContent();
    }
    private void doClose(){

        storeChanges();

        if( !m_origVars.equals(vars) ){
/*
            int res = JOptionPane.showOptionDialog( this,
                    "Сохранить изменения?",
                    "Внимание",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, new String[]{ "Да", "Нет", "Отмена"},
                    null );
*/
            int res = JOptionPane.showOptionDialog( this,
                    "Сохранить изменения?",
                    "Внимание",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, new String[]{ "Да", "Нет" },
                    null );
            if( res == JOptionPane.YES_OPTION ){
                saveAndClose();
            }
            else if( res == JOptionPane.NO_OPTION ){
                dispose();
            }

            /** cancel **/
        }
        else
            dispose();
    }
    private void saveAndClose(){

        if( !storeChanges() ){
            return;
        }

        for( Enumeration en = vars.keys(); en.hasMoreElements(); ){
            String key = (String)en.nextElement();
            Variable var = (Variable)vars.get( key );

            try{

                if( var.iType == Variable.TYPE_STR ){
                    if( ((String)var.sDef).length() >= Variable.MAX_STR_LEN ){
                        JOptionPane.showMessageDialog( this, "Длина строки не должна превышать " + Variable.MAX_STR_LEN + " символов (переменная: " + var.name + ")", "Ошибка",
                                JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                }

                var.init();
            }catch( Exception exc ){
                JOptionPane.showMessageDialog( this, "Данные для переменной '" + var.name + "' не соответствуют типу переменной ("+
                        Variable.sTypes[ var.iType ] + ")", "Ошибка", JOptionPane.ERROR_MESSAGE );
                return;
            }

            vars.put( key, var );
        }

        m_origVars.clear();
        m_origVars.putAll( vars );
        dispose();
    }

    private boolean storeChanges(){
        int iStride = 6;

        if( (m_listPane.getComponentCount() % iStride) != 0 ){
            //System.out.println("VarsDialog.saveAndClose() - Wrong number of property components" );
            return false;
        }

        VariableContainer _vars = new VariableContainer();
        for( int i = iStride; i < m_listPane.getComponentCount(); i += iStride ){
            JRadioButton  varName = (JRadioButton)m_listPane.getComponent(i);
            JCheckBox  bInput = (JCheckBox)m_listPane.getComponent(i+1);
            JCheckBox  bOutput = (JCheckBox)m_listPane.getComponent(i+2);
            JComboBox  chType = (JComboBox)m_listPane.getComponent(i+3);
            JTextField tfDef = (JTextField)m_listPane.getComponent(i+4);

            Variable forStore = null;
            if( /*chType.getSelectedIndex()*/ getVarType( chType ) >= Variable.TYPE_ARR_INT ){
                Object current = vars.get( varName.getText() );
                if( current instanceof VariableArray ){
                    //System.out.println("varname(cb): " + varName );
                    //System.out.println("m_varButtons.get(cb): " + m_selNames.get(varName) );
                    forStore = new VariableArray( (VariableArray)vars.get( m_selNames.get(varName) ) );
                }
                else{
                    forStore = new VariableArray( ((Variable)current).name, /*chType.getSelectedIndex()*/ getVarType( chType ) );
                }
                forStore.bInput = bInput.isSelected();
                forStore.bOutput = bOutput.isSelected();
                forStore.bInner = !(bInput.isSelected() || bOutput.isSelected());
                forStore.iType = getVarType( chType ); //chType.getSelectedIndex();

                forStore.sDef = tfDef.getText();
            }
            else{
                forStore = new Variable(varName.getText(),
                    bInput.isSelected(), bOutput.isSelected(), !(bInput.isSelected() || bOutput.isSelected()),
                    /*chType.getSelectedIndex()*/ getVarType( chType ), tfDef.getText());
            }

            _vars.put( varName.getText(), forStore );
        }
        vars = new VariableContainer( _vars );
        return true;
    }

    private void reinitContent(){
        m_contentPane.removeAll();
        m_contentPane.add( new JScrollPane(initContent()) );
        m_contentPane.revalidate();
    }

    private JPanel initContent(){
        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets( 3, 3, 3, 3 );
        c.anchor = c.NORTH;

        JPanel res = new JPanel();
        res.setLayout( l );

        class MyLabel extends JLabel{
            public MyLabel( String title ){
                super( title );
            }
            public void paint( Graphics g ){
                g.setColor( Color.lightGray );
                g.fillRect( 0, 0, getWidth(), getHeight() );
                super.paint(g);
            }
        }

        JLabel []lTitle = new JLabel[]{
            new MyLabel("Переменная"),
            new MyLabel("Входная"),
            new MyLabel("Выходная"),
            new MyLabel("Тип"),
            new MyLabel("Начальное значение"),
            new MyLabel("")
        };
        for( int i = 0; i < lTitle.length; i++ ){
            if( i == (lTitle.length-1) ){
                c.gridwidth = c.REMAINDER;
                c.weightx = 0.0;
            }
            l.setConstraints( lTitle[i], c );
            res.add( lTitle[i] );
        }

        m_varButtons = new Hashtable();
        m_selNames = new Hashtable();
        m_choiceNames = new Hashtable();

        Enumeration en = vars.keys();
        while( en.hasMoreElements() ){

            c.anchor = c.NORTH;

            c.weightx = 1.0;
            c.gridwidth = 1;
            c.weighty = 0.0;

            String varName = (String)en.nextElement();

            JPanel pVar = new JPanel( new GridLayout(1,2) );
            JRadioButton cb = new JRadioButton( varName );
            cb.getModel().setGroup( m_selCBG );
            pVar.add( cb );
            l.setConstraints( cb, c );
            res.add( cb );
            m_selNames.put( cb, varName );

            JCheckBox cbIn = new JCheckBox();
            JCheckBox cbOut = new JCheckBox();

			//disable STRING
			//disable STRING
			//disable STRING
			String []_sTypes = new String[ Variable.sTypes.length-1 ];
			int      _sTypes_index = 0;
			for( int i = 0; i < Variable.sTypes.length; i++ ){
				if( i == Variable.TYPE_STR ) continue;
				_sTypes[ _sTypes_index++ ] = Variable.sTypes[i];
			}

            JComboBox chType = new JComboBox( _sTypes );
            m_choiceNames.put( chType, varName );
            chType.addItemListener( this );
            JTextField tf = new JTextField( 10 );

            Variable oVar = (Variable)vars.get( varName );
            cbIn.setSelected( oVar.bInput );
            cbOut.setSelected( oVar.bOutput );

			setVarType( chType, oVar.iType );
            //chType.setSelectedIndex( oVar.iType );

            if( oVar instanceof VariableArray ){
                tf.setText( "..." );
                tf.setEditable( false );
            }
            else
                tf.setText( oVar.sDef );

            l.setConstraints( cbIn, c );
            res.add( cbIn );
            l.setConstraints( cbOut, c );
            res.add( cbOut );

            l.setConstraints( chType, c );
            res.add( chType );

            l.setConstraints( tf, c );
            res.add( tf );

            c.weightx = 0.001;
            c.gridwidth = c.REMAINDER;
            JButton jb = new JButton("...");
            l.setConstraints( jb, c );
            res.add( jb );

            if( !(((Variable)vars.get(varName)) instanceof VariableArray) ){
                jb.setEnabled( false );
            }

            jb.addActionListener( this );

            m_varButtons.put( jb, varName );
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

        JPanel p = new JPanel( l1 );
        l1.setConstraints( res, c1 );
        p.add( res );

        m_listPane = res;

        return p;
    }

	/**
     * Конструктор
	 * @param vars контейнер переменных
	 */
    public VarsDialog( VariableContainer vars ){
        super();
        setModal( true );
        setTitle( "Список переменных" );

        this.vars = new VariableContainer(vars);
        m_origVars = vars;

        JPanel buttonPane = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        buttonPane.add( m_add );
        buttonPane.add( m_remove );
        buttonPane.add( new JLabel(" ") );
//        buttonPane.add( m_close );
        buttonPane.add( m_save );

        JTextArea jta = new JTextArea();
        jta.setMinimumSize( new Dimension( 500,500 ) );
        jta.setPreferredSize( new Dimension( 500, 500 ) );

        m_contentPane = new JPanel( new GridLayout(1,1) );
        m_contentPane.add( new JScrollPane(initContent()) );

        getContentPane().add( m_contentPane, BorderLayout.CENTER );
        getContentPane().add( buttonPane, BorderLayout.SOUTH );

        m_add.addActionListener( this );
        m_remove.addActionListener( this );
        m_save.addActionListener( this );
        m_close.addActionListener( this );

        int cw = 650;
        int ch = 450;
        int cx = -(cw>>1) + (getToolkit().getScreenSize().width>>1);
        int cy = -(ch>>1) + (getToolkit().getScreenSize().height>>1);

        addWindowListener( new WindowAdapter(){
            public void windowClosing( WindowEvent e ){
                doClose();
            }
        });

        setResizable( false );
        setLocation( cx, cy );
        setSize( cw, ch );
        setVisible( true );
    }

	/** Обработчик событий от UI-элементов управления */
    public void actionPerformed( ActionEvent e ){

        if( e.getSource() == m_add ){
            addVar();
        }
        else if( e.getSource() == m_remove ){
            removeVar();
        }
        else if( e.getSource() == m_save ){
            saveAndClose();
        }
        else if( e.getSource() == m_close ){
            doClose();
        }
        else{
            try{
                if( !storeChanges() ){
                    //System.out.println("ERROR: VarsDialog.actionPerformed, storeChanges FAILED!");
                    throw new NullPointerException();
                }
                String varName = (String)m_varButtons.get( e.getSource() );
                if( varName != null ){
                    Variable oVar  = (Variable)vars.get( varName );

                    if( oVar.iType >= Variable.TYPE_ARR_INT ){
                        ArrayEditDlg dlg = new ArrayEditDlg( new VariableArray( (VariableArray)vars.get(varName) ) );
                        if( dlg.isOk() )
                            vars.put( varName, dlg.getResultArray() );
                    }
                }

            }catch( Exception exc ){
                //exc.printStackTrace();
            }
        }
    }

	//disable STRING
	//disable STRING
	//disable STRING
	private void setVarType( JComboBox combo, int varType ){
		if( varType >= Variable.TYPE_STR )
			varType--;
		combo.setSelectedIndex( varType );
	}
	//disable STRING
	//disable STRING
	//disable STRING
	private int getVarType( JComboBox combo ){
		int res = combo.getSelectedIndex();
		if( res >= Variable.TYPE_STR )
			res++;
		return res;
	}

	/** Обработчик событий */
    public void itemStateChanged( ItemEvent e ){
    }
/*
    public static void main( String []args ){

        VariableContainer vc = new VariableContainer();
        for( int i = 0; i < 100; i++ ){
            Variable var = new Variable( "a" + i, true, false, false, Variable.TYPE_INT,  "0");
            vc.put( var.name, var );
        }

        new VarsDialog( vc );
    }
*/
}
