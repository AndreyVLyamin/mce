import dlc.expression.Variable;
import dlc.expression.VariableArray;
import dlc.util.VariableContainer;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * $Id: SingleStringDialog.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ������ ��� ������ ������������� ��������.
 */
class SingleStringQuestion extends JDialog implements ActionListener{

    JButton m_ok = new JButton();
    JButton m_back = new JButton("�����");
    JDialog m_backDlg;
    JTextField m_tf = new JTextField();
    Object  m_typeCheck;

    boolean    m_bOK = false;

    boolean    m_bIsSizeDlg = false;
    boolean    m_bStringInit = false;

	/**
     * �����������
	 * @param back ���������� ������
	 * @param question ����� �������
	 * @param bFinish ������� ���������� ������� � �������
	 * @param typeCheck ��������� ������, � �������� ������� �������� ��������� �������� ��� ��������
	 * @param bSizeDlg ������� ����, ��� ��� ������ ��� ����� ������� ������� (�������������� ����������� �� ��������)
	 * @param bStringInit ������� ����, ��� �������� �������� - ������ �������� (����������� �� �����)
	 */
    public SingleStringQuestion( JDialog back, String question, boolean bFinish, Object typeCheck, boolean bSizeDlg, boolean bStringInit ){
        super();

        m_bIsSizeDlg = bSizeDlg;
        m_bStringInit = bStringInit;

        m_backDlg = back;
        m_typeCheck = typeCheck;
        setModal( true );

        getContentPane().add( new JLabel(question), BorderLayout.NORTH );
        getContentPane().add( m_tf );
        getContentPane().add( initButtons(bFinish), BorderLayout.SOUTH );

        m_ok.addActionListener( this );
        m_back.addActionListener( this );

        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

        setSize( 400, 300 );
        pack();

        int cw = getWidth();
        int ch = getHeight();
        int cx = -(cw>>1) + (getToolkit().getScreenSize().width>>1);
        int cy = -(ch>>1) + (getToolkit().getScreenSize().height>>1);

        setLocation( cx, cy );
        setSize( cw, ch );
        setResizable( false );
        setVisible( true );
    }

	/**
	 * �����������
	 */
    public SingleStringQuestion( JDialog back, String question, boolean bFinish ){
        this( back, question, bFinish, null, false, false );
    }

	/**
	 * �����������
	 */
    public SingleStringQuestion( JDialog back, String question, boolean bFinish, Object typeCheck ){

        this( back, question, bFinish, typeCheck, false, false );
    }

    JPanel initButtons( boolean bFinish ){

        if( bFinish )
            m_ok.setText( "������" );
        else
            m_ok.setText( "�����" );

        JPanel res = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        res.add( m_back );
        res.add( m_ok );
        return res;
    }

	/**
	 * ��������� �������
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_ok ){

            if( m_bStringInit ){
                String val = getValue();
                if( val.length() >= Variable.MAX_STR_LEN ){
                    JOptionPane.showMessageDialog( this, "����� ������ ������ ���� ������ " + Variable.MAX_STR_LEN + " ��������", "������", JOptionPane.ERROR_MESSAGE );
                    return;
                }
            }

            if( m_typeCheck != null ){
                if( m_typeCheck instanceof Integer ){
                    try{
                        int iValue = Integer.parseInt( getValue() );
                        if( m_bIsSizeDlg ){
                            if( iValue < 1 ){
                                JOptionPane.showMessageDialog( this, "��������� ������� ����� ������������� �����", "������", JOptionPane.ERROR_MESSAGE );
                                return;
                            }
                            if( iValue > VariableArray.MAX_SIZE ){
                                JOptionPane.showMessageDialog( this, "������ ������� ������ ���� �� ����� " + VariableArray.MAX_SIZE, "������", JOptionPane.ERROR_MESSAGE );
                                return;
                            }
                        }
                    }catch( Exception exc ){
                        JOptionPane.showMessageDialog( this, "��������� ������� ����� �����", "������", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                }
                else if( m_typeCheck instanceof Double ){
                    try{
                        Double.parseDouble( getValue() );
                    }catch( Exception exc ){
                        JOptionPane.showMessageDialog( this, "��������� ������� ������������ �����", "������", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                }
            }
            m_bOK = true;
            dispose();
        }
        else if( e.getSource() == m_back ){
            m_bOK = false;
            dispose();
//            m_backDlg.setVisible( true );
        }
    }

	/**
	 * ����� ��� ��������� ��������
	 */
    public String getValue(){
        return m_tf.getText();
    }

	/**
	 * �������� ������� ����� �������� �������
	 */
    public boolean isOk(){
        return m_bOK;
    }
}

/**
 * $Id: CreateVarDialog.java,v 1 2007/02/20
 * <br/>
 * Author: �������� �.�.
 * <br/>
 * �����, ����������� ������ �������� ����� ����������.
 */
public class CreateVarDialog extends JDialog implements ActionListener{
    JButton       m_ok = new JButton("�����");
    JButton       m_cancel = new JButton("��������");
    JTextField    m_tf = new JTextField();
    Choice        m_chType = new Choice();

    String        m_newName;
    boolean       m_bOK = false;

    VariableContainer m_vars;

    Variable      m_result;

	/**
	 * �����������
     * @param parent ������ �� ������������ ������ (������ ����������)
	 * @param oldName �������� �������� ����������
	 * @param vars ������ ���� ���������� (��� �������� ������������ ����)
	 */
    public CreateVarDialog( JDialog parent, String oldName, VariableContainer vars ){
        super( parent, "��� ����������" );
        m_vars = vars;

        JPanel buttonPane = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        buttonPane.add( m_cancel );
        buttonPane.add( m_ok );

        m_ok.addActionListener( this );
        m_cancel.addActionListener( this );

        m_tf.setText( oldName );
        m_newName = ""+oldName;


        GridBagLayout l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.001;
        c.insets = new Insets( 3, 3, 3, 3 );
        JPanel middle = new JPanel( l );

        {
            c.weightx = 0.001;
            JLabel lName = new JLabel("��� ����������:");
            l.setConstraints( lName, c );
            middle.add( lName );
            c.weightx = 1.0;
            c.gridwidth = c.REMAINDER;
            l.setConstraints( m_tf, c );
            middle.add( m_tf );

            for( int i = 0; i < Variable.sTypes.length; i++ ){
				//disable STRING
				//disable STRING
				//disable STRING
				if( i == Variable.TYPE_STR )
					continue;
                m_chType.add( Variable.sTypes[i] );
            }
        }

        {
            c.weightx = 0.001;
            c.gridwidth = 1;
            JLabel lType = new JLabel("���:");
            l.setConstraints( lType, c );
            middle.add( lType );
            c.weightx = 1.0;
            c.gridwidth = c.REMAINDER;
            l.setConstraints( m_chType, c );
            middle.add( m_chType );
        }
        getContentPane().add( middle );
        getContentPane().add( buttonPane, BorderLayout.SOUTH );

        setModal( true );
        setSize( 400, 100 );
        addWindowListener( new WindowAdapter(){
            public void windowClosing( WindowEvent e ){
                m_result = null;
                dispose();
            }
        });
        pack();

        int cw = getWidth();
        int ch = getHeight();
        int cx = -(cw>>1) + (getToolkit().getScreenSize().width>>1);
        int cy = -(ch>>1) + (getToolkit().getScreenSize().height>>1);

        setLocation( cx, cy );
        setSize( cw, ch );
        setResizable( false );
        setVisible( true );
    }

	private int getSelectedType(){
		int res = m_chType.getSelectedIndex();
		//disable STRING
		//disable STRING
		//disable STRING
		if( res >= Variable.TYPE_STR ) res++;
		return res;
	}

    private boolean isValidName( String var ){

        if( var.trim().length() == 0 ){
            JOptionPane.showMessageDialog( this, "������� ��� ����������",
                    "������", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else if( var.trim().length() >= Variable.MAX_NAME_LEN ){
            JOptionPane.showMessageDialog( this, "����� ����� ���������� ������ ���� ����� " + Variable.MAX_NAME_LEN + " ��������",
                    "������", JOptionPane.ERROR_MESSAGE );
            return false;
        }

        try{
            String toCompare = var.toLowerCase();
            for( int i = 0; i < toCompare.length(); i++ ){
                if( toCompare.charAt(i) < 'a' || toCompare.charAt(i) > 'z' )
                    throw new Exception();
            }
        }catch( Exception exc ){
            JOptionPane.showMessageDialog( this, "��� ���������� ������ ���� ������ ���������� ���������",
                    "������", JOptionPane.ERROR_MESSAGE );
            return false;
        }

        Enumeration en = m_vars.keys();
        while( en.hasMoreElements() ){
            String varName = (String)en.nextElement();
            if( varName.equalsIgnoreCase(var) ){
                JOptionPane.showMessageDialog( this, "��� ���������� ������ ���� ���������� (��� ����� ��������)",
                        "������", JOptionPane.ERROR_MESSAGE );
                return false;
            }
        }

        return true;
    }

    private boolean showArrSizeDialog(){
        SingleStringQuestion dlgSize = new SingleStringQuestion( null, "������� ������ �������", false, new Integer(0), true, false );
        do{
            if( dlgSize.isOk() ){
                if( showArrInitDialog( dlgSize.getValue() ) ){
                    return true;
                }
                else
                    dlgSize.setVisible( true );
            }
        }while( dlgSize.isOk() );

        return false;
    }
    private boolean showArrInitDialog( String size ){
        SingleStringQuestion dlgInit = new SingleStringQuestion( null, "������� ��������� ��������",
                false,
                Variable.getRefType( /*m_chType.getSelectedIndex()*/ getSelectedType() ),
                false, ( /*m_chType.getSelectedIndex()*/ getSelectedType() == Variable.TYPE_ARR_STR) );
        do{
            if( dlgInit.isOk() ){
                if( showArrSetDialog( dlgInit, size, dlgInit.getValue() ) ){
                    return true;
                }
                else
                    dlgInit.setVisible( true );
            }
        }while( dlgInit.isOk() );

        return false;
    }
    private boolean showArrSetDialog( JDialog back, String size, String initVal ){
        VariableArray var = new VariableArray( m_newName, /*m_chType.getSelectedIndex()*/ getSelectedType() );
        for( int i = 0; i < Integer.parseInt( size ); i++ ){
            try{
                var.addElement( initVal );
            }catch( Exception exc ){
                JOptionPane.showMessageDialog( back, "������ ������������� ��������� �������", "������", JOptionPane.ERROR_MESSAGE );
                return false;
            }
        }
        ArrayEditDlg dlgEdit = new ArrayEditDlg( var, back );
        if( dlgEdit.isOk() ){
            m_result = var;
            return true;
        }
        return false;
    }

	/**
	 * ���������� �������
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_ok ){

            String varName = m_tf.getText().toLowerCase();
            if( isValidName(varName) ){
                m_newName = varName;

                setVisible( false );

                if( /*m_chType.getSelectedIndex()*/ getSelectedType() >= Variable.TYPE_ARR_INT ){
                    if( showArrSizeDialog() ){
                        m_bOK = true;
                    }
                    else{
                        setVisible( true );
                        return;
                    }
                }

                else{

                    Object castObj = Variable.getRefType( /*m_chType.getSelectedIndex()*/ getSelectedType() );
                    SingleStringQuestion dlg = new SingleStringQuestion( this, "������� ��������� ��������", true, castObj, false,
                            (/*m_chType.getSelectedIndex()*/ getSelectedType() == Variable.TYPE_STR) );
                    if( dlg.isOk() ){
                        m_result = new Variable( getNewName(), false, false, true, getSelectedType() /*m_chType.getSelectedIndex()*/, dlg.getValue() );
                        m_bOK = true;
                    }
                    else{
                        setVisible( true );
                        return;
                    }
                }
                dispose();
            }
            else{
            }
        }
        else if( e.getSource() == m_cancel ){
            //m_newName remains the same as old
            setVisible( false );
            dispose();
        }
    }

	/**
	 * ����� ��� ��������� ������ ����� ����������
	 */
    public String getNewName(){
        return m_newName;
    }
	/**
	 * �������� ������� ����� �������� �������
	 */
    public boolean isOK(){
        return m_bOK;
    }

	/**
	 * �������� ��������� ����� ����������
	 */
    public Variable getResult(){
        return m_result;
    }
}
