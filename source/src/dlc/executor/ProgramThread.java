package dlc.executor;

import dlc.codenodes.*;
import dlc.util.*;
import java.util.*;
import java.awt.event.*;

/**
 * $Id: ProgramThread.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий поток выполнения программы.
 */
public class ProgramThread extends Thread{
    boolean m_bStop = false;
    CodeRuntime m_runtime;
    Vector m_codeNodes;
    Exception m_exception;
    VariableContainer m_vars;
    ActionListener m_listener;
    int m_toLine = -1;
    int m_breakLine = -1;
    int m_sleepAmount = 1;



    /**
     * Конструктор
     * @param runtime экземпляр с данными времения выполнения
     * @param codeNodes линейный список инструкций
     * @param vars переменные программы
     * @param listener слушатель события об успешном выполнении
     */
    public ProgramThread( CodeRuntime runtime, Vector codeNodes, VariableContainer vars, ActionListener listener,
                          int sleepAmount ){
        m_runtime = runtime;
        m_codeNodes = codeNodes;
        m_vars = vars;
        m_listener = listener;
        m_sleepAmount = sleepAmount;
    }

    public void setToLine( int line ){
        m_toLine = line;
    }
    public void setBreakLine( int line ){
        m_breakLine = line;
    }

    /**
     * Метод, реализующий жизненный цикл потока
     */
    public void run(){

        boolean bAtDebugLine = false;

int loopCount = 0;

        try{
            CodeNode  currentNode = null;

//DEBUG
//DEBUG
//DEBUG
System.out.println("In ProgramThread.run..." );
System.out.println("Total nodes: " + m_codeNodes.size() );
System.out.println("Sleep amount: " + m_sleepAmount );


            while( !m_bStop && m_runtime.IP < m_codeNodes.size() ){
                currentNode = (CodeNode)m_codeNodes.elementAt(m_runtime.IP);

				loopCount++;

//System.out.println( "\t IP=" + m_runtime.IP + "[" + currentNode.getSourceToken().line + ", " + currentNode.getSourceToken().column + "]" );

                if( m_breakLine >= 0 && currentNode.getSourceToken().line == m_breakLine ){
                    break;
                }
                else if( m_toLine >= 0 && currentNode.getSourceToken().line == m_toLine )
                    bAtDebugLine = true;

                currentNode.execute( m_vars, m_runtime );

                yield();
                if( m_sleepAmount >= 0 ){
                    try{
                        sleep( m_sleepAmount );
                    }catch( Exception e ){}
                }

                if( m_runtime.IP < m_codeNodes.size() ){
                    currentNode = (CodeNode)m_codeNodes.elementAt(m_runtime.IP);

                    if( m_toLine >= 0 && bAtDebugLine && !(currentNode instanceof CodeJumpNode) &&
                            currentNode.getSourceToken().line != m_toLine ){
                        break;
                    }
                }
            }
        }catch( Exception exc ){
            m_exception = exc;
        }
        if( !m_bStop ){
            m_listener.actionPerformed( new ActionEvent(this, 0, "") );
            System.out.println( "Program normally stopped." );
            System.out.println( m_vars.get( new String("i") ) );
		}
		else{
			//DEBUG
			//DEBUG
			//DEBUG
			System.out.println("Program stopped, IP=" + m_runtime.IP );
		}

System.out.println("Total loop count: " + loopCount );

        m_bStop = true;
    }
    /**
     * Принудительная остановка потока
     */
    public void stopThread(){
        m_bStop = true;
    }

    /**
     * Метод для получения информации о ходе выполнения программы 
     * @return null или экземпляр Exception, выброшенного в процессе выполнения
     */
    public Exception getException(){
        return m_exception;
    }
}
