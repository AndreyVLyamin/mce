package dlc.codenodes;

import dlc.expression.INode;
import dlc.expression.VariableArray;
import dlc.expression.Variable;
import dlc.code.CodeRTException;
import dlc.code.CodeToken;
import dlc.util.VariableContainer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ���� ��������� � ���������� ������������
 */
public class CodeAssignNode extends CodeNode{

    INode   expression;
    INode   indexExpression;
    String  varName;

	/** �����������
	 * @param source �������� ���� ����
	 * @param expression ��������� ������ �� ��������� ������������
	 * @param varName ��� ��������� �����
	 */
    public CodeAssignNode( CodeToken source, INode expression, String varName ){
        super( source );
        this.expression = expression;
        this.varName = varName;
        indexExpression = null;
    }
	/** �����������
	 * @param source �������� ���� ����
	 * @param expression ��������� ������ �� ��������� ������������
	 * @param varName ��� ��������� �����
	 * @param indexExpr ��������� ����� �� ��������� ������������ (�������� ������� � �������)
	 */
    public CodeAssignNode( CodeToken source, INode expression, String varName, INode indexExpr ){
        this( source, expression, varName );
        indexExpression = indexExpr;
    }

	/** ����� ��� ���������� ������� ����� ����
	 * @param vars ���������� ���������
	 * @param runtime �������� ����� ����������
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{

        Object val = expression.proceed( vars );
        /**
         * ������� �������� val ��������������� � Integer, Double (���� ��� �� ������)
         */
        if( !(val instanceof String || val instanceof Character) ){
            try{
                double delta = Double.parseDouble( ""+val );
                delta -= (int)delta;
                if( delta == 0.0 )
                    val = new Integer( (int)Double.parseDouble(""+val) );
                else
                    val = new Double( "" + val );
            }catch( Exception exc ){}
        }

        if( !vars.containsKey(varName.toLowerCase()) ){
            throw new CodeRTException( INode.ERROR_NOVAR, getSourceToken() );
        }

        if( indexExpression != null ){
            //Array variable
//            System.out.println("CodeAssignNode[" + varName + "[" + indexExpression + "]=" + val + "]" );
            Object key = indexExpression.proceed( vars );

            int index = -1;
            try{
                /**
                 * TODO:
                 * ���� ���������� �� �������������� ������� ���!
                 * �.�. ��� ���������� ����� � �������������� ���������
                 * �.�. ��� ���������� ����� � �������������� ���������
                 * �.�. ��� ���������� ����� � �������������� ���������
                 * �.�. ��� ���������� ����� � �������������� ���������
                 */
                Double iIndex = new Double(0.0);
                if( key instanceof Character )
                    iIndex = new Double( (int)((Character)key).charValue() );
                else
                    iIndex = new Double( key.toString() );
                index = (int)iIndex.doubleValue();
            }catch( Exception exc ){
                throw new CodeRTException( INode.ERROR_NEEDINTINDEX, getSourceToken() );
            }

            VariableArray var = null;
            try{
                var = (VariableArray)vars.get( varName.toLowerCase() );
            }catch( Exception exc ){
                throw new CodeRTException( "�������������� �����, ��������� ���������� ���� '������'", getSourceToken() );
            }
            try{
                var.setElementAt( val, index );
            }catch( ArrayIndexOutOfBoundsException oobExc ){
                throw new CodeRTException( "����� �� ������� ������� (������=" + index + ")", getSourceToken() );
            }catch( Exception exc ){
                exc.printStackTrace();
System.out.println("val type: " + val.getClass().getName() );
                throw new CodeRTException( "�������������� �����, ��� �������: " +
                        Variable.sTypes[ var.iType ], getSourceToken() );
            }
        }
        else{
            //Non-array variable
//            System.out.println("CodeAssignNode[" + varName + "=" + val + "]" );

            Variable var = null;
            try{
                var = (Variable)vars.get( varName.toLowerCase() );
                if( var instanceof VariableArray )
                    throw new Exception();
            }catch( Exception exc ){
                throw new CodeRTException( "�������������� �����, ��������� ���������� �������� ����", getSourceToken() );
            }
            try{
                var.setValue( val );
            }catch( Exception exc ){
                throw new CodeRTException( "�������������� �����, ���������� '" + var.name + "' ����� ���: " +
                        Variable.sTypes[ var.iType ], getSourceToken() );
            }
        }

        runtime.IP++;
    }

	/** ����� ���������� ������� ����� ���� (�� ����������, �������� ����������)
	 * @param vars ����������
	 */
    public void execute( Hashtable vars ) throws Exception{
        //execute( vars, -1 );
        throw new CodeRTException( "", getSourceToken() );
    }

	/** ����������� ��������� ������ � ������� ������ � ���������� ������ */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        res.addElement( this );
        return res;
    }
}
