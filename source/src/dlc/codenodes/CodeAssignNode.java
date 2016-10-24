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
 * Блок выражений с оператором присваивания
 */
public class CodeAssignNode extends CodeNode{

    INode   expression;
    INode   indexExpression;
    String  varName;

	/** Конструктор
	 * @param source исходный блок кода
	 * @param expression выражение справа от оператора присваивания
	 * @param varName имя пременной слева
	 */
    public CodeAssignNode( CodeToken source, INode expression, String varName ){
        super( source );
        this.expression = expression;
        this.varName = varName;
        indexExpression = null;
    }
	/** Конструктор
	 * @param source исходный блок кода
	 * @param expression выражение справа от оператора присваивания
	 * @param varName имя пременной слева
	 * @param indexExpr выражение слева от оператора присваивания (описание индекса в массиве)
	 */
    public CodeAssignNode( CodeToken source, INode expression, String varName, INode indexExpr ){
        this( source, expression, varName );
        indexExpression = indexExpr;
    }

	/** Метод для выполнения данного блока кода
	 * @param vars переменные контекста
	 * @param runtime описание среды выполнения
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{

        Object val = expression.proceed( vars );
        /**
         * Попытка привести val последовательно к Integer, Double (если это не строка)
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
                 * Пока требований по целочисленному индексу нет!
                 * Т.к. нет привидения типов в арифметических операциях
                 * Т.к. нет привидения типов в арифметических операциях
                 * Т.к. нет привидения типов в арифметических операциях
                 * Т.к. нет привидения типов в арифметических операциях
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
                throw new CodeRTException( "Несоответствие типов, требуется переменная типа 'Массив'", getSourceToken() );
            }
            try{
                var.setElementAt( val, index );
            }catch( ArrayIndexOutOfBoundsException oobExc ){
                throw new CodeRTException( "Выход за границы массива (индекс=" + index + ")", getSourceToken() );
            }catch( Exception exc ){
                exc.printStackTrace();
System.out.println("val type: " + val.getClass().getName() );
                throw new CodeRTException( "Несоответствие типов, тип массива: " +
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
                throw new CodeRTException( "Несоответствие типов, требуется переменная простого типа", getSourceToken() );
            }
            try{
                var.setValue( val );
            }catch( Exception exc ){
                throw new CodeRTException( "Несоответствие типов, переменная '" + var.name + "' имеет тип: " +
                        Variable.sTypes[ var.iType ], getSourceToken() );
            }
        }

        runtime.IP++;
    }

	/** Метод выполнения данного блока кода (не реализован, вызывает исключение)
	 * @param vars переменные
	 */
    public void execute( Hashtable vars ) throws Exception{
        //execute( vars, -1 );
        throw new CodeRTException( "", getSourceToken() );
    }

	/** Преобразует экземпляр класса в элемент списка и возвращает список */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        res.addElement( this );
        return res;
    }
}
