package AbsCodeServer;

//

import dlc.expression.*;
import dlc.code.*;
import dlc.codenodes.*;
import dlc.template.*;
import dlc.executor.*;
import dlc.util.*;

import java.util.*;
import java.awt.event.*;

/**
 * $Id: Program.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий среду выполнения программы на стороне проверяющего сервера.
 */

public class Program implements ActionListener {

	/** Имя в хэш-таблице имен, по которому определяет ограничение по времени на компиляцию */
    public static final String TIMEOUT_NAME = "TranslationSeconds";

    /**
     * проверяющие наборы, максимальное время выполнения и идентификатор входного/выходного наборов
     * (экземпляры ConditionForChecking)
     */
    private Vector m_Conditions = new Vector();//условия( входные и выходные)

    /**
     * Результаты работы для всех элементов m_Conditions
     * (экземпляры CheckingResult)
     */
    private Vector m_Response = new Vector();

    /**
     * Текст программы
     */
    private String m_program = "";

    /**
     * Переменные программы, заданные клиентом
     */
    private VariableContainer m_vars;

    /**
     * Стиль кодирования
     */
    private String m_style = "";

    /**
     * Экземпляр потока, в котором будет выполнена программа
     */
    private ProgramThread m_progThread;

    /**
     * Экземпляр потока, в котором будет скомпилирована программа
     */
    private CompileThread m_compThread;

    /**
     * Признак своевременного останова программы (устанавливается по команде из ProgramThread)
     */
    private boolean m_bProgramStopped = false;

    /**
     * Признак своевременного останова компиляции (устанавливается по команде из CompileThread)
     */
    private boolean m_bCompileStopped = false;

    /**
     * Информация о таймаутах, указанная в файле конфигурации
     */
    private Hashtable m_timeouts = new Hashtable();

    /**
     * Добавление входного условия
     */
    public void addCondition(ConditionForChecking cond) {
        m_Conditions.add((ConditionForChecking) cond);
    }

    /**
     * Метод для установки текста программы
     * @param code исходный код
     */
    public void setCode( String code ){
        if( code == null )
            m_program = "";
        else
            m_program = code;
    }

    /**
     * Установка информации о таймаутах
     */
    public void setTimeouts( Hashtable timeouts ){
        m_timeouts = timeouts;
    }

    /**
     * Метод для установки переменных после десериализации состояния ВЛР, отправленной с клиента
     * @param vars контейнер переменных
     */
    public void setVars( VariableContainer vars ){
        if( vars == null )
            m_vars = new VariableContainer();
        else
            m_vars = vars;
    }

    /**
     * Метод, устанавливающий стиль кодирования
     * @param style стиль кодирования из списка перечисленных в XML-файле
     */
    public void setStyle( String style ){
        if( style == null )
            m_style = "";
        else
            m_style = style;
    }

	private void processInputData( String checkInput, VariableContainer vars, Vector vOut, String testID ){

		String errorHeader = "ERROR: Program.processInputData, testID=" + testID;

		if( AnswerChecker.isOld( checkInput ) ){
			//Последовательная установка значений переменных по значениям в наборе
			//...
            Vector inputVariableSet = parseInput( checkInput );
            int    inputSetIndex = 0;

            String sInput = "";

            int inputLoopCounter = 0;

            for( Enumeration en = vars.elements(); en.hasMoreElements(); ){
                try{
                    Variable var = (Variable)en.nextElement();

                    if( var.bInput && var instanceof VariableArray ){
                        VariableArray vArr = (VariableArray)var;

                        inputLoopCounter = 0;
                        for( int j = 0; j < vArr.elems.size(); j++ ){
							try{
	                            vArr.setElementAt( (String)inputVariableSet.elementAt(j + inputSetIndex), j );
	                            sInput += (sInput.length() > 0 ? " ":"") + vArr.elementAt(j);
	                            inputLoopCounter++;
							}catch( Exception arrElemExc ){
								Logger.log( errorHeader );
								Logger.log( arrElemExc );
							}
                        }
                        inputSetIndex += inputLoopCounter;
                    }
                    else if( var.bInput && var instanceof Variable ){
                        var.sDef = (String)inputVariableSet.elementAt( inputSetIndex );

                        sInput += (sInput.length() > 0 ? " ":"") + var.sDef;

                        inputSetIndex++;
                    }

                }catch( Exception exc ){

                    inputSetIndex += inputLoopCounter;

					Logger.log( errorHeader );
					Logger.log( exc );
                }
            }
		}else{
			HashMap<String, String> refSet = AnswerChecker.buildVariablesMap( checkInput );
			for( Map.Entry<String, String> refVar : refSet.entrySet() ){
				if( vars.get( refVar.getKey() ) == null ){
					Logger.log( "WARNING: Program.processInputData - variable [" + refVar.getKey() + "] not found in userAnswer" );
					continue; 
				}

				try{

    				Variable var = (Variable)vars.get( refVar.getKey() );
    				if( var.bInput && var instanceof VariableArray ){

    					VariableArray vArr = (VariableArray)var;

    					ArrayList<String> arrValues = AnswerChecker.parseArray( refVar.getValue() );

    					for( int j = 0; arrValues != null && j < vArr.elems.size() && j < arrValues.size(); j++ ){
							try{
//System.out.println( var.name + "[" + j + "]=" + arrValues.get(j) );
								vArr.setElementAt( arrValues.get(j), j );
							}catch( Exception arrElemExc ){
								Logger.log( errorHeader );
								Logger.log( arrElemExc );
							}
    					}
    				}else if( var.bInput && var instanceof Variable ){
    					var.sDef = refVar.getValue();
    				}

				}catch( Exception exc ){
					Logger.log( "Program.processInputData - FAILED to init variable (" + refVar.getKey() + ")" );
					Logger.log( exc );
				}
			}
		}

        for( Enumeration en = vars.elements(); en.hasMoreElements(); ){
	        try{
				Variable var = (Variable)en.nextElement();

				if( var.bOutput )
					vOut.addElement( var );

				var.init();
			}catch( Exception exc ){
				Logger.log( exc );
			}
		}
	}

    /**
     * Метод, запускающий программу на выполнение для всех проверяющих наборов
     * @return список экземпляров CheckingResult
     */
    public Vector runForCheck() {

        String sCode = m_program + "\r\n";
        TemplateFactory tFactory = new TemplateFactoryXml( "conf-new.xml" );

        m_style = tFactory.getStyleByAlias( m_style );

        if( !tFactory.switchStyle( m_style ) ){

            System.out.println("ERROR: Program.runForCheck() - cant switch style!");

            CheckingResult cr = new CheckingResult();
            cr.setID( ((ConditionForChecking)m_Conditions.elementAt(0)).getID() );
            cr.setResult( "0" );
            cr.setOutput( "0" );
            cr.setTime( 0 );
            m_Response.addElement( cr );
            return m_Response;
        }

        if( tFactory.isFree() ){
            System.out.println("WARNING: Program.runForCheck() - cant process free style coding!");

            CheckingResult cr = new CheckingResult();
            cr.setID( ((ConditionForChecking)m_Conditions.elementAt(0)).getID() );
            cr.setResult( "0" );
            cr.setOutput( "0" );
            cr.setTime( 0 );
            m_Response.addElement( cr );
            return m_Response;
        }
        else
            System.out.println("Program.runForCheck() - style is not free");

        if( tFactory.isFree() ){

            System.out.println("WARNING: Program.runForCheck() - free style coding! Cant run program");

            CheckingResult cr = new CheckingResult();
            cr.setID( ((ConditionForChecking)m_Conditions.elementAt(0)).getID() );
            cr.setResult( "0" );
            cr.setOutput( "0" );
            cr.setTime( 0 );
            m_Response.addElement( cr );
            return m_Response;
        }

System.out.println("Program.runForCheck(), style=" + tFactory.getCurrentStyle() );

System.out.println("\t - Initializing variables..." );
        /**
         * Проверка инициализации переменных
         */
        for( Enumeration en = m_vars.elements(); en.hasMoreElements(); ){
            Variable var = null;
            try{
                var = ((Variable)en.nextElement());
                var.init();
            }catch( Exception exc ){
System.out.println("\t - ERROR: Program.runForCheck() FAILED to initialize variable [" + var.name + "]" );
                CheckingResult cr = new CheckingResult();
                cr.setID( ((ConditionForChecking)m_Conditions.elementAt(0)).getID() );
                cr.setResult( "0" );
                cr.setTime( 0 );
                cr.setOutput( "0" );
                m_Response.addElement( cr );
                return m_Response;
            }
        }

System.out.println("\t - compiling...");
        /**
         * Компиляция (один раз для всех ConditionForChecking)
         */
        Vector linearCode = null;
        try{
            linearCode = new Vector();
            m_compThread = new CompileThread( sCode, tFactory, linearCode, m_vars, this );
            m_compThread.start();


            long compileTimeLimit = 5;
            try{
                compileTimeLimit = ((Long)m_timeouts.get( TIMEOUT_NAME )).longValue();
            }catch( Exception timeoutexc ){
                compileTimeLimit = 5;
            }

            compileTimeLimit *= 1000;

            long startTime = System.currentTimeMillis();
            boolean bTimeout = true;
            while( (System.currentTimeMillis() - startTime) < compileTimeLimit ){
                try{
                    Thread.yield();
                    Thread.sleep( 1 );
                }catch( Exception sleepExc ){}

                if( m_bCompileStopped ){
                    bTimeout = false;
                    break;
                }
            }
            m_bCompileStopped = false;

            if( bTimeout ){
                try{
                    m_compThread.stopThread();
                    m_compThread.wait( 1000 );
                    m_compThread.interrupt();
                }catch( Exception compThreadStop ){}
                throw new Exception( "Compile timeout" );
            }

            if( m_compThread.getException() != null )
                throw m_compThread.getException();

        }catch( Exception exc ){
            //exc.printStackTrace();
System.out.println("Program.runForCheck() - compile error: " + exc.getMessage() );

            CheckingResult cr = new CheckingResult();
            cr.setID( ((ConditionForChecking)m_Conditions.elementAt(0)).getID() );
            cr.setResult( "0" );
            cr.setTime( 0 );
            cr.setOutput( "0" );
            m_Response.addElement( cr );
            return m_Response;
        }

System.out.println("\t - Starting. Total count of condition: " + m_Conditions.size() );

        for( int i = 0; i < m_Conditions.size(); i++ ){

            CheckingResult chResult = new CheckingResult();
            ConditionForChecking cfCheck = (ConditionForChecking)m_Conditions.elementAt(i);
            chResult.setID( cfCheck.getID() );
            chResult.setTime( 0 );
            chResult.setOutput( "0" );

            long timeLimit = cfCheck.getTime() * 1000;

System.out.println("\t - running for condition: " + i + ", ID: " + cfCheck.getID() );

//            VariableArray vOut = null;
            Vector vOut = new Vector();

			//2008_05_19
			processInputData( cfCheck.getInput(), m_vars, vOut, cfCheck.getID() );

			//2008_05_19

            try{

                int sleepAmount = 0;

                m_bProgramStopped = false;
                m_progThread = new ProgramThread( new CodeRuntime(), linearCode, m_vars, this, sleepAmount );
                m_progThread.start();

//System.out.println("(1)");

                long startTime = System.currentTimeMillis();
                boolean bTimeout = true;
                while( (System.currentTimeMillis() - startTime) < timeLimit ){
                    try{
                        Thread.yield();
                        Thread.sleep( 1 );
                    }catch( Exception sleepExc ){}

                    if( m_bProgramStopped ){
                        bTimeout = false;
                        break;
                    }
                }
                m_bProgramStopped = false;

//System.out.println("(2)");

                chResult.setTime( (long)(System.currentTimeMillis() - startTime)/1000 );

//System.out.println("(3)");

                try{
                    m_progThread.stopThread();
                    m_progThread.wait();
                }catch( Exception stopExc ){}
                if( m_progThread.getException() != null )
                    throw m_progThread.getException();
                m_progThread = null;

				if( AnswerChecker.isOld( cfCheck.getOutput() ) ){
                    String sOutput = "";
                    for( int j = 0; j < vOut.size(); j++ ){
                        Variable var = (Variable)vOut.elementAt(j);
                        if( var instanceof VariableArray ){
                            VariableArray vArr = (VariableArray)var;
                            for( int k = 0; k < vArr.elems.size(); k++ ){
                                //(sOutput.length() > 0 ? " " : "") +
                                sOutput += (sOutput.length() > 0 ? " " : "") + vArr.elems.elementAt(k);
                            }
                        }
                        else{
                            sOutput += (sOutput.length() > 0 ? " " : "") + var.value;
                        }
                    }

					sOutput = sOutput.trim();

                    if( sOutput.length() > 0 )
                        chResult.setOutput( sOutput );

//	                chResult.setResult( "" + processResults( m_vars, cfCheck, chResult, bTimeout ) );

Logger.log( "Start checking by OldMethod...[testID=" + cfCheck.getID() + "]\n" +
				"\tREF : " + cfCheck.getOutput() + "\n" +
				"\tANS : " + sOutput );

					AnswerChecker aCheck = new AnswerChecker( cfCheck.getOutput().trim() );

					chResult.setResult( aCheck.check( sOutput ) ? "1" : "0" );

				}else{
					StringBuffer sOutput = new StringBuffer();

					for( int j = 0; j < vOut.size(); j++ ){
						Variable var = (Variable)vOut.elementAt(j);
						if( var instanceof VariableArray ){
							VariableArray vArr = (VariableArray)var;
							StringBuffer vArrStr = new StringBuffer();
							for( int k = 0; k < vArr.elems.size(); k++ ){

								String outValue = "" + vArr.elems.elementAt(k);
								outValue = ProgramSerializer.checkForUnicode( outValue );

								vArrStr.append( (vArrStr.length() > 0) ? (", " + outValue) : outValue );
							}

							sOutput.append( " " + var.name + "=[" + vArrStr.toString() + "]" );
						}else{

							String vValue = ProgramSerializer.checkForUnicode( "" + var.value );

							if( var.iType == Variable.TYPE_STR )
								sOutput.append( " " + var.name + "=\"" + vValue + "\"" );
							else
								sOutput.append( " " + var.name + "=" + vValue );
						}

						String strOut = sOutput.toString().trim();
						if( strOut.length() > 0 )
							chResult.setOutput( strOut );

Logger.log( "Start checking by NewMethod...[testID=" + cfCheck.getID() + "]\n" +
				"\tREF : " + cfCheck.getOutput() + "\n" +
				"\tANS : " + strOut );

						AnswerChecker aCheck = new AnswerChecker( cfCheck.getOutput() );

						chResult.setResult( (aCheck.check(strOut) ? "1" : "0") );
					}
				}

                m_Response.add( chResult );

            }catch( Exception exc ){

                Logger.log("ERROR: Program.runForCheck() FAILED for Condition.ID=" + cfCheck.getID() + ", error=" + exc.getMessage() );
				Logger.log( exc );

                chResult.setResult( "0" );
                chResult.setOutput( "0" );
                m_Response.add( chResult );

//System.out.println("\t - Condition: " + i + ", ID: " + cfCheck.getID() + " FAILED, result: " + chResult.getResult() );

                continue;
            }
        }

        return m_Response;
    }

    /**
     * Метод, разбирающий входной тестовый набор на отдельные элементы
     * (входной набор - это строка, например, &quot;1 2 3 4&quot;)
     * @param input строка чисел, разделенная пробелом
     * @return список чисел
     */
    private Vector parseInput( String input ){
        Vector res = new Vector();

        StringTokenizer st = new StringTokenizer( input, " ", true );
        int spaceCounter = 0;
        while( st.hasMoreTokens() ){
            String sTok = st.nextToken();
            if( sTok.equals(" ") ){
                spaceCounter++;

                if( spaceCounter >= 2 ){
                    res.addElement( " " );
                    spaceCounter = 0;
                }
            }
            else{
                res.addElement( sTok );
                spaceCounter = 0;
            }
        }

        return res;
    }

    /**
     * Обработка результатов выполнения программы
     * @param vars переменные, заданные на стороне клиента
     * @param cfc текущее входное условие
     * @param cRes результат проверки
     * @param bTimeout признак принудительной остановки
     * @return целочисленный результат (сейчас 0 или 1)
     */
    private int processResults( VariableContainer vars, ConditionForChecking cfc, CheckingResult cRes, boolean bTimeout ){
        if( bTimeout ){
            System.out.println("Program.processResults() - it was TIMEOUT while program run, result = 0");
            return 0;
        }

        String input = cfc.getInput();
        String output = cfc.getOutput();
        String userOut = cRes.getOutput();

        System.out.println(" - INPUT: " + input );
        System.out.println(" - OUTPUT : " + output );
        System.out.println(" - USEROUT: " + userOut );

        return output.trim().equals(userOut.trim()) ? 1 : 0;
    }

    /**
     * Метод для вывода на консоль отладочных данных интерпретатора
     */
    private void PrintInfo() {
/*
        for (int i = 0; i < m_Conditions.size(); i++) {
            System.out.println("Condition" + Integer.toString(i + 1) + ": ");
            ((ConditionForChecking) m_Conditions.elementAt(i)).getInputOutput();
        }
        System.out.println("");
        System.out.println("Input G(beta) size: " + Integer.toString(m_Commands.size()));
        for (int i = 0; i < m_Commands.size(); i++) {
            System.out.println((FPoint)m_Commands.elementAt(i));
        }
*/
        System.out.println("Program.printInfo() - EMPTY" );
    }

    /**
     * Обработчик событий. В методе реализована обработка события от потока ProgramThread
     * @param e событие
     */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_progThread ){
            m_bProgramStopped = true;
        }
        else if( e.getSource() == m_compThread ){
            m_bCompileStopped = true;
        }
    }
}

