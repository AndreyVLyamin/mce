package dlc.expression;

import dlc.expression.Variable;
import dlc.code.CodeRTException;

import java.util.Vector;

/** ������ ���������� */
public class VariableArray extends Variable{

	/** ������ ��������� ������� */
    public Vector elems = new Vector();

	/** ������������ ������ ������� = 100 */
    public final static int MAX_SIZE = 100;

	/** ������������� ������� */
    public void init() throws Exception{
        if( iType < TYPE_ARR_INT || iType > TYPE_ARR_STR )
            throw new Exception( INode.ERROR_INVALIDTYPE );

        /**
         * ���������� ������ ���� ���������������� ��� �������������� ����������
         * (���� ������ � ������� ���� �� �����������)
         */

        for( int i = 0; i < elems.size(); i++ ){
            if( !isValidElement(i) )
                throw new Exception( INode.ERROR_INVALIDINIT + "(" + name + ")" );
        }
    }

	/** ����������� */
    public VariableArray( String name, int type ){
        super( name );
        iType = type;
    }
	/** ����������� */
    public VariableArray( VariableArray va ){
        super( va.name, va.bInput, va.bOutput, va.bInner, va.iType, va.sDef );
        iType = va.iType;
        elems = new Vector( va.elems );
    }

	/** ��������� �������� �� ������� ������� */
    public void setFrom( VariableArray va ){
        elems.clear();
        name = va.name;
        bInput = va.bInput;
        bOutput = va.bOutput;
        bInner = va.bInner;
        iType = va.iType;
        sDef = va.sDef;
        elems = new Vector( va.elems );
    }

	/** ���������� �������� */
    public void addElement( String val ) throws Exception{
        Object nVal = null;
        switch( iType ){
            case TYPE_ARR_INT:
                nVal = new Integer( "" + val );
                break;
            case TYPE_ARR_DEC:
                nVal = new Double( "" + val );
                break;
            case TYPE_ARR_STR:
                //nVal = new String( val );
                if( val.length() != 1 )
                    //throw new Exception("val is not Character, is it String? (" + val + ")" );
					System.out.println("val is not Character, is it String? (" + val + ")" );
                nVal = new Character( val.charAt(0) );
                break;
            default:
                break;
        }
        elems.addElement( nVal );
    }

	/** ���������� ������� �������� */
    public void addElement(){
        Object add = null;
        switch( iType ){
            case TYPE_ARR_INT:
                add = new Integer(0);
                break;
            case TYPE_ARR_DEC:
                add = new Double(0.0);
                break;
            case TYPE_ARR_STR:
//                add = new String("");
                add = new Character(' ');
                break;
            default:
                break;
        }
        elems.addElement( add );
    }

	/** �������� ������������ �������� ���� ������� */
    public boolean isValidElement( int index ){
        try{
            Object val = elems.elementAt( index );
            if( iType == TYPE_ARR_INT && val instanceof Integer ||
                iType == TYPE_ARR_DEC && val instanceof Double ||
//                iType == TYPE_ARR_STR && val instanceof String )
                iType == TYPE_ARR_STR && val instanceof Character ){

                return true;
            }
        }catch( Exception exc ){}
        return false;
    }

	/** �������� �������� */
    public void removeElement( int index ) throws Exception{
        elems.removeElementAt( index );
    }
    /**
     * ����� ��� ������ � �������� ArrayEditDlg. ��������� �������� val ��� ������� index
     */
    public void setElementAt( String val, int index ) throws Exception{
        Object nVal = null;
        switch( iType ){
            case TYPE_ARR_INT:
                nVal = new Integer( "" + val );
                break;
            case TYPE_ARR_DEC:
                nVal = new Double( "" + val );
                break;
            case TYPE_ARR_STR:
                //nVal = new String( val );
                if( val.length() != 1 )
                    throw new Exception("val is not Character, is it String?");
                nVal = new Character( val.charAt(0) );
                break;
            default:
                break;
        }
        elems.setElementAt( nVal, index );
    }
    /**
     * ����� ��� ������ � CodeNode (CodeAssignNode). ��������� �������� val ��� ������� index
     */
    public void setElementAt( Object val, int index ) throws Exception{
        if( (iType == TYPE_ARR_INT && (val instanceof Integer || val instanceof Character) ) ||
            iType == TYPE_ARR_DEC && (val instanceof Double || val instanceof Integer) ||
//            iType == TYPE_ARR_STR && val instanceof String ){
            iType == TYPE_ARR_STR && (val instanceof Character || val instanceof Integer) ){

            switch( iType ){
                case TYPE_ARR_INT:
                    if( val instanceof Integer )
                        elems.setElementAt( new Integer(val.toString()), index );
                    else
                        elems.setElementAt( new Integer( ((Character)val).charValue() ), index );
                    break;
                case TYPE_ARR_DEC:
                    elems.setElementAt( new Double(val.toString()), index );
                    break;
                case TYPE_ARR_STR:
//                    elems.setElementAt( new String(val.toString()), index );
                    if( val instanceof Integer )
                        elems.setElementAt( "" + new Character( (char)((Integer)val).intValue() ), index );
                    else
                        elems.setElementAt( val, index );
                    break;
                default:
                    throw new Exception( INode.ERROR_INVALIDTYPE + "(" + name + ")" );
            }
        }
        else
            throw new Exception();
    }

	/** ���������� �������� �� ������� */
    public Object elementAt( int index ) throws Exception{
        return elems.elementAt( index );
    }

	/** �������� ��������� ���� �������� */
    public boolean equals( Object obj ){
        try{
            VariableArray var = (VariableArray)obj;

            boolean bElemEquals = true;
            for( int i = 0; i < elems.size(); i++ ){
                if( !elems.elementAt(i).equals( var.elems.elementAt(i) ) )
                    return false;
            }

            return name.equals( var.name ) &&
                    bInput == var.bInput &&
                    bOutput == var.bOutput &&
                    bInner == var.bInner &&
                    iType == var.iType;
        }catch( Exception exc ){}
        return false;
    }
}
