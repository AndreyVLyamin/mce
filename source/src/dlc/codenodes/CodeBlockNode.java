package dlc.codenodes;

import dlc.code.CodeToken;
import dlc.code.CodeRTException;

import java.util.Vector;
import java.util.Hashtable;

/**
 * Блок выражений
 */
public class CodeBlockNode extends CodeNode{

    Vector codeNodes = new Vector();

	/**
	 * Конструктор
	 * @param source исходный блок кода
	 */
    public CodeBlockNode( CodeToken source ){
        super( source );
    }
	/** Конструктор копирования */
    public CodeBlockNode( CodeBlockNode src ){
        this( src.getSourceToken() );
        codeNodes = new Vector( src.getNodes() );
    }

	/** Добавление блока кода */
    public void addNode( CodeNode node ){
        codeNodes.addElement( node );
    }
	/** Получить список всех блоков кода (типа CodeToken) */
    public Vector getNodes(){
        return codeNodes;
    }

	/** Метод для выполнения. Вызывает исключение */
    public void execute( Hashtable vars ) throws Exception{
        throw new CodeRTException( "", getSourceToken() );
    }

	/** Преобразует все выражения в блоке выражений в список и возвращает его */
    public Vector getLinearCode() throws Exception{
        Vector res = new Vector();
        for( int i = 0; i < codeNodes.size(); i++ ){
            CodeNode cn = (CodeNode)codeNodes.elementAt(i);
            res.addAll( ((CodeNode)codeNodes.elementAt(i)).getLinearCode() );
        }
        return res;
    }

	/** Возвращает строковое представление всех выражений данного блока */
    public String toString(){
        return "" + codeNodes;
    }
}
