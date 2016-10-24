package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.code.CodeRTException;
import dlc.util.VariableContainer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Ѕлок безусловного перехода
 */
public class CodeJumpNode extends CodeNode{
	/** ќтносительный адрес дл€ перехода */
    public int m_relativeAddress = 1;

	/**
	 *  онструктор
	 * @param source исходный блок кода
	 * @param relativeAddress относительный адрес
	 */
    public CodeJumpNode( CodeToken source, int relativeAddress ){
        super( source );
        m_relativeAddress = relativeAddress;
    }

	/** ћетод дл€ выполнени€ данного блока кода
	 * @param vars переменные контекста
	 * @param runtime описание среды выполнени€
     */
    public void execute( VariableContainer vars, CodeRuntime runtime ) throws Exception{
        runtime.IP += m_relativeAddress;
    }

	/** ћетод выполнени€ данного блока кода (не реализован, вызывает исключение)
	 * @param vars переменные
	 */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

	/** ѕреобразует блоки выполнени€ к лийненому виду */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        res.addElement( this );
        return res;
    }
}
