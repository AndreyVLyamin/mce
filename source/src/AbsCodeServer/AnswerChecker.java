package AbsCodeServer;

import java.io.*;
import java.util.*;

/**
 * Класс для проверки ответа на соответствие проверяющему набору
 */
public class AnswerChecker{

	String m_refResult;

	/**
	 * Допустимая ошибка на значения типа double
	 */
	public final static double m_delta = 1.1;

	/**
	 * Конструктор
	 * @param refResult эаталонный набор данных
	 */
	public AnswerChecker( String refResult ){
		m_refResult = refResult;
	}

	/**
	 * Метод для проверки, описан ли набор данных в старом формате (последовательно указанные значения) или
	 * в новом (имя_перенной=значение)
	 * @param str строка с данными
	 * @return признак старого формата данных
	 */
	public static boolean isOld( String str ){
/*
AnswerKey   : ([a-z]|[a-Z])+([0-9])*([\\[]][0-9]+[\\]])??
           (=)
AnswerValue : ([\\\"]|[\\\']|[0-9]|[a-z]|[A-Z]|[\\\\]|[\\[]|[\\]]|[.]|[,]|[*]|[/]|[\\<]|[\\>]|[\\{]|[\\}])*
*/

		String answerMatch = "(([a-z]|[A-Z])+([0-9])*)([\\[][0-9]+[\\]])??(([\\{])([0-9]*)([\\}]))?(=)";
		//TODO: [ ]|[=] - можно использовать только в [\\"] !!
		//TODO: [ ]|[=] - можно использовать только в [\\"] !!
		//TODO: [ ]|[=] - можно использовать только в [\\"] !!
		answerMatch += "([\\\"]|[\\\']|[0-9]|[a-z]|[A-Z]|[\\\\]|[\\[]|[\\]]|[.]|[,]|[\t]|[\r]|[\n]|[\\-]|[\\+]|[*]|[/]|[\\<]|[\\>]|[\\{]|[\\}]|[ ]|[\\;]|[=])*";
		answerMatch = "((" + answerMatch + ")*([ ]|[\t]|[\r]|[\n])??)+";

		boolean bRes = !str.matches( answerMatch );

System.out.println( "IsOld? [" + str + "] " + bRes );

		return bRes;
	}

	private static boolean isQuote( String token ){
		return token.equals("\"") || token.equals("\'");
	}
	private static boolean isWhitespace( String str, String ws ){
		for( int i = 0; i < str.length(); i++ ){

			boolean bWhitespace = false;

			for( int j = 0; j < ws.length(); j++ ){
				if( str.charAt(i) == ws.charAt(j) ){
					bWhitespace = true;
					break;
				}
			}

			if( !bWhitespace ) return false;
		}
		return true;
	}

	/**
	 * Метод для разбора массива значений
	 * @param str список значений [1,2 ,3, 4,5, "aa,bb,cc"] или [1 2 3 "aa bb dd" 5]
	 * @return набор значений (например, [1,2,3,"aa bb dd",5]
	 */
	public static ArrayList<String> parseArray( String str ){

		if( str.startsWith("[") ) str = str.substring( 1 );
		if( str.endsWith("]") ) str = str.substring( 0, str.length()-1 );

		String sWS = " \t\r\n";

		ArrayList<String> res = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer( str, sWS + ",\'\\", true );
		boolean inQuotes = false;
		boolean afterEscape = false;

		StringBuffer value = new StringBuffer();

		while( st.hasMoreTokens() ){
			String token = st.nextToken();

			if( !inQuotes && isWhitespace(token, sWS) )
				continue;

//System.out.println( "Token: " + token );
			if( !inQuotes && token.equals(",") ){

//System.out.println("Termination...");

   				if( value.toString().length() > 0 ){
   					res.add( value.toString() );
//    System.out.println(" Adding [" + value.toString().trim() + "]" );
   				}

				value = new StringBuffer();
			}else{

   				if( !afterEscape && isQuote(token) )
    				inQuotes = !inQuotes;
    			else if( !afterEscape && token.equals( "\\" ) )
    				afterEscape = true;
    			else{
    				afterEscape = false;

    				value.append( token );
    			}
			}
		}

		if( value.toString().trim().length() > 0 )
			res.add( value.toString().trim() );

		return res;
	}

	private static boolean isTerm( String token, String terms ){
		for( int i = 0; i < terms.length(); i++ ){
			if( token.equals("" + terms.charAt(i)) )
				return true;
		}
		return false;
	}


	/**
	 * Метод для создания карты переменных (имя, значение)
	 * @param str строка с описанием переменных (только новый формат)
	 * @return карта переменных
	 */
	public static HashMap<String, String> buildVariablesMap( String str ){

		HashMap<String, Double> precision = new HashMap<String, Double>();

		return buildVariablesMap( str, precision );
	}

	/**
	 * Метод для создания карты переменных (имя, значение)
	 * @param str строка с описанием переменных (только новый формат)
	 * @param precision таблица значений точности переменных (в процентах)
	 * @return карта переменных
	 */
	public static HashMap<String, String> buildVariablesMap( String str, HashMap<String, Double> precision ){

		String sWS = " \r\n\t";

		StringTokenizer st = new StringTokenizer( str, sWS + "=;\"[]{}", true );
		String termsVar = sWS + ";";
		String termsArr = "]";
		String terms = "";
		String key = "";

		boolean bArray = false;

		HashMap<String, String> res = new HashMap<String, String>();

		while( st.hasMoreTokens() ){
			String token = st.nextToken();

////////////////////////////////////////////////////////
			if( isWhitespace( token, sWS + ";" ) )
				continue;

			if( token.equals( "{" ) ){

//System.out.println( "Find precision syntax..." );

				do{ token = st.nextToken(); }while( isWhitespace( token, sWS ) );
				String prec = token;
				do{ token = st.nextToken(); }while( isWhitespace( token, sWS ) );

//				String prec = st.nextToken();
//				token = st.nextToken();

				if( token.equals( "}" ) ){
					try{
						precision.put( key, new Double(prec) );
						Logger.log( "Precision for " + key + " is " + prec + "%" );
					}catch( Exception exc ){
						Logger.log( "Invalid precision for " + key + " (" + prec + ")" );
						precision.put( key, new Double( m_delta ) );
					}
				}else{
//System.out.println( "Cant find closure \"}\"" );
				}
			}else if( token.equals("=") ){

				terms = "";

				bArray = false;

				do{ token = st.nextToken(); }while( isWhitespace( token, sWS ) );

				StringBuffer value = new StringBuffer();
				boolean inQuotes = false;
				boolean afterEscape = false;

				String val = token;

				while( 2 == 2 ){

//System.out.println( "Value token: " + val );

					if( terms.length() == 0 ){
						if( val.equals("[") ){
							bArray = true;
							terms = termsArr;

							if( !st.hasMoreTokens() )
								break;
							val = st.nextToken();

							continue;
						}
						else terms = termsVar;
					}

					if( !inQuotes && isTerm(val, terms) ){
//						value.append( val );
						break;
					}
					if( !afterEscape && val.equals("\"") )
						inQuotes = !inQuotes;

					afterEscape = val.equals("\\");

					value.append( val );
/////
					if( !st.hasMoreTokens() )
						break;
					val = st.nextToken();
				}

				String strValue = value.toString();

				if( bArray ){
					strValue = "[" + strValue + "]";
System.out.println( "ARRAY: " + strValue );
				}
				else strValue = strValue.trim();

				res.put( key.trim(), strValue );
				key = "";
			}else{
				key += token;
			}
		}

		return res;
	}

	/**
	 * Метод для проверки ответа (разбора переменных или последовательности значений)
	 * @param res данный (в старом или новом формате)
	 * @return признак совпадения наборов res и эталонный набор, переданный через конструктор
	 */
	public boolean check( String res ){

		if( isOld( m_refResult ) ){

System.out.println("Checking by checkOld..." );

			return checkOld( res );
		}

System.out.println("Checking by newMethod..." );

//		String []answers = res.split( "=" );
//		String []refs = m_refResult.split( "=" );

		HashMap<String, Double> precision = new HashMap<String, Double>();

		HashMap<String, String> hmAnswers = buildVariablesMap( res );
		HashMap<String, String> hmRefs = buildVariablesMap( m_refResult, precision );

System.out.println( "Answers  : " + hmAnswers );
System.out.println( "Reference: " + hmRefs );

		if( hmRefs.size() != hmAnswers.size() )
			return false;

		for( Map.Entry<String, String> refEntry : hmRefs.entrySet() ){

//System.out.println( "\t - " + refEntry.getKey() );

			if( hmAnswers.get( refEntry.getKey() ) == null )
				return false;

			String aValue = hmAnswers.get( refEntry.getKey() );
			String rValue = refEntry.getValue();

			double dPrecision = m_delta;
			if( precision.get( refEntry.getKey() ) != null )
				dPrecision = (double)precision.get( refEntry.getKey() );

			if( rValue.startsWith("[") ){

System.out.println( "- array-ref: " + rValue );
				ArrayList<String> refArray = parseArray( rValue );
System.out.println( "- array-ans: " + aValue );
				ArrayList<String> ansArray = parseArray( aValue );

/*
				if( ansArray.size() > refArray.size() ){
					boolean bCutAnswer = true;
					for( int i = refArray.size(); i < ansArray.size(); i++ ){
						if( !refArray.get(i).equals(" ") ){
							bCutAnswer = false;
							break;
						}
					}

					if( bCutAnswer ){
						ansArray = ansArray.subList( 0, refArray.size() );
					}
				}
*/

System.out.println( "Checking arrays" );
System.out.println( "\t [" + refArray.size() + "/" + ansArray.size() + "]: " + refArray + " vs " + ansArray );

				if( refArray.size() != ansArray.size() )
					return false;

				for( int i = 0; i < refArray.size(); i++ ){

//System.out.println( "Checking [" + i + "]: " + refArray.get(i) + " vs " + ansArray.get(i) );

					if( !checkValue( refArray.get(i), ansArray.get(i), dPrecision ) )
						return false;
				}
			}else{
				if( !checkValue( rValue, aValue, dPrecision ) )
					return false;
			}
		}

		return true;
	}

	private boolean checkValue( String a, String b ){
		return checkValue( a, b, m_delta );
	}

	/**
	 * Метод проверки значений.
	 * @param a эатлонное значение
	 * @param b значение из ответа
	 * @return true если значения равны или в пределах ошибки m_delta (1%)
	 */
	private boolean checkValue( String a, String b, double precision ){
		try{
			double dA = Double.parseDouble( a );
			double dB = Double.parseDouble( b );

			double dDelta = (Math.abs(dA)*precision)/100.0;

			double val = Math.abs( dA - dB );

			if( val > dDelta ){
				return false;
			}
		}catch( Exception excNumber ){
    		if( !a.equals(b) )
    			return false;
		}

		return true;
	}

	private boolean checkOld( String res ){
		String []answers = res.split( "\\s" );
		String []refs = m_refResult.split( "\\s" );

		if( answers.length != refs.length )
			return false;

		for( int i = 0; i < answers.length; i++ ){
			String answ = answers[i];
			String ref = refs[i];

			if( !checkValue( ref, answ ) )
				return false;
		}

		return true;
	}
/*
	public static void main( String []args ){

//		String refSet = "a=1 b=2 c=\"3 4 5\" d=4";
//		String resSet = "a=1.001 d=4 b=2 c=\"3 4 5\"";

//		String refSet = "";
//		String resSet = "";

//		ArrayList<String> res = parseArray( "[1 2 3\"aa,bb , \\\'cd\\\"\'    5 a]" );
//		for( String s : res ){
//			System.out.println( s );
//		}

//		HashMap<String, String> vars = buildVariablesMap( "in=[0 1 2 3 4 5 6]" );
//		System.out.println( vars );

		String refSet = "a=[1, ' ', 3];\n\t b\t=\t'1'\r\n";
		String resSet = "a=[1, ' ', 3]\t\n; b\t=\r\n'1'";

//System.out.println("Val: " + (1.0E-9) );

		AnswerChecker aCheck = new AnswerChecker( refSet );
		System.out.println( "Result: " + aCheck.check( resSet ) );

	}
*/

}
