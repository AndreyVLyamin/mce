package dlc.expression;

import dlc.code.CodeRTException;
import dlc.code.CodeToken;

/** ����� ��� �������� ��������� */
public class Variable {

	/** ��� ���������� */
    public String   name = "";
	/** ������� ���������� */
    public boolean  bInput = false;
	/** �������� ���������� */
    public boolean  bOutput = false;
	/** ���������� ���������� */
    public boolean  bInner = true;
	/** ��� ���������� */
    public int      iType = TYPE_INT;
	/** �������� �� ��������� */
    public String   sDef = "";
	/** �������� */
    public Object   value;

	/** ��� ����� ����� */
    public final static int TYPE_INT = 0;
	/** ��� ������������ ����� */
    public final static int TYPE_DEC = 1;
	/** ��� ����� ������ */
    public final static int TYPE_STR = 2;
	/** ��� ������ ����� ����� */
    public final static int TYPE_ARR_INT = 3;
	/** ��� ������ ������������ ����� */
    public final static int TYPE_ARR_DEC = 4;
	/** ��� ������ �������� */
    public final static int TYPE_ARR_STR = 5;

	/** ������������ ����� ����� = 32 */
    public final static int MAX_NAME_LEN = 32;

	/** ������������ ����� ������ = 256 */
    public final static int MAX_STR_LEN = 256;

	/** ��������� �������� ����� ���������� */
    public final static String []sTypes = new String[]{
        "����� �����",
        "������������ �����",
        "������ ��������",
        "������ ����� �����",
        "������ ������������ �����",
        "������ ��������"
    };

	/** ������������� ��������� */
    public void init() throws Exception{
        switch( iType ){
            case TYPE_INT:
                value = new Integer( sDef );
                break;
            case TYPE_DEC:
                value = new Double( sDef );
                break;
            case TYPE_STR:
                value = new String( sDef );
                break;
            case TYPE_ARR_INT:
            case TYPE_ARR_DEC:
            case TYPE_ARR_STR:
                throw new Exception( INode.ERROR_INVALIDINIT + "(" + name + ")" );
            default:
                throw new Exception( INode.ERROR_INVALIDTYPE + "(" + name + ")" );
        }
    }
	/** ��������� �������� */
    public void setValue( Object val ) throws Exception{
        if( val instanceof Integer && (iType == TYPE_INT || iType == TYPE_DEC) ||
            val instanceof Double && iType == TYPE_DEC ||
            val instanceof String && iType == TYPE_STR ){

            switch( iType ){
                case TYPE_INT:
                    value = new Integer( "" + val );
                    break;
                case TYPE_DEC:
                    value = new Double( "" + val );
                    break;
                case TYPE_STR:
                    if( new String(""+val).length() >= MAX_STR_LEN )
                        throw new Exception( INode.ERROR_STRINGLEN );
                    value = new String( "" + val );
                    break;
                default:
                    throw new Exception( INode.ERROR_INVALIDTYPE + "(" + name + ")" );
            }
        }
        else
            throw new Exception( INode.ERROR_INVALIDINIT + "(" + name + ")" );
    }

	/** ����������� */
    public Variable( String name ){
        this.name = name;
    }

	/** ����������� */
    public Variable( String name, boolean input, boolean output, boolean inner, int type, String def ){
        this.name = name;
        bInput = input;
        bOutput = output;
        bInner = inner;
        iType = type;
        sDef = def;
    }

	/** �������������� � ������ - ���������� ������� */
    public String toString(){
        return "name: " + name + ", input: " + bInput + ", output: " + bOutput + ", inner: " + bInner + ", type: " + iType +
                ", sDef: " + sDef;
    }

	/** �������� ��������� ��� ��� �������� ���������� (��� ��������, ��� ��� ��������� �����) */
    public static Object getRefType( int type ){
        switch( type ){
            case TYPE_INT:
            case TYPE_ARR_INT:
                return new Integer(0);
            case TYPE_DEC:
            case TYPE_ARR_DEC:
                return new Double(0.0);
            case TYPE_STR:
            case TYPE_ARR_STR:
                return new String("");
        }
        return null;
    }

	/** ������������� ����� ��� ��������� ���������� */
    public boolean equals( Object obj ){
        try{
            Variable var = (Variable)obj;
            return name.equals( var.name ) &&
                    bInput == var.bInput &&
                    bOutput == var.bOutput &&
                    bInner == var.bInner &&
                    iType == var.iType &&
                    sDef.equals( var.sDef );
        }catch( Exception exc ){}
        return false;
    }
}
