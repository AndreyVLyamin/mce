package dlc.util;

import dlc.code.CodeToken;

import java.util.Vector;
import java.util.Hashtable;

/**
 * $Id: TokenUtils.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий вспомогательные методы для разбора лексем.
 */
public class TokenUtils {

    /**
     * Поиск выражения до встречи лексемы toToken
     */
    public static Vector findSubtoken( Vector tokens, int []start_end, String toToken ) throws Exception{
        start_end[1] = start_end[0];
        Vector res = new Vector();
        while( start_end[1] < tokens.size() && !((CodeToken)tokens.elementAt(start_end[1])).t.equalsIgnoreCase(toToken) ){
            res.addElement( tokens.elementAt(start_end[1]) );
            start_end[1]++;
        }

        if( start_end[1] == tokens.size() )
            throw new Exception();

        return res;
    }

    /**
     * Поиск выражения
     */
    public static Vector findSubtoken( Vector tokens, int []start_end ) throws Exception{
        return findSubtoken( tokens, start_end, ((CodeToken)tokens.elementAt(start_end[0])).t, null );
    }

    /**
     * Поиск выражения от лексемы startToken до лексемы endToken
     */
    public static Vector findSubtoken( Vector tokens, int []start_end, String startToken, String endToken ) throws Exception{

//System.out.println("findSubtokens.tokens: " + tokens.subList( start_end[0], tokens.size()-1 ) );

        int counter = 0;
        Vector res = new Vector();

        Hashtable delims = new Hashtable();
        if( startToken != null && endToken != null ){
            delims.put( startToken, endToken );
        }
        else{
            delims.put( "(", ")" );
            delims.put( "{", "}" );
            delims.put( "[", "]" );
            delims.put( "\"", "\"" );
            delims.put( "\'", "\'" );
        }

        String delim = startToken;
        String delimEnd = (String)delims.get( startToken );

        StringBuffer tokenHistory = new StringBuffer();

        try{

            if( !delim.equalsIgnoreCase( ((CodeToken)tokens.elementAt(start_end[0])).t ) )
                counter = 1;

            int p = start_end[0];
            start_end[1] = p;
            while( p < tokens.size() ){
                String t = ((CodeToken)tokens.elementAt(p)).t;

                tokenHistory.append( t );

                /*
                 * Порядок if/else важен при поиске открывающих/закрывающих ковычек
                 */
                if( tokenHistory.length() > 0 && tokenHistory.toString().endsWith( delimEnd ) ){
                    counter--;
//                    System.out.println("Meet " + delimEnd );
                }
                else if( tokenHistory.length() > 0 && tokenHistory.toString().endsWith( delim ) ){
                    counter++;
//                    System.out.println("Meet " + delim );
                }

                if( (counter == 1 && tokenHistory.length() > 0 && !tokenHistory.toString().endsWith( delim )) ||
                    counter > 1 )
                    res.addElement( tokens.elementAt(p) );

                if( counter <= 0 ) break;
                p++;
            }

            if( counter > 0 ){
                //System.out.println("Expected symbol " + delimEnd );
                throw new NullPointerException();
            }

            if( endToken != null && endToken.length() > 1 ){ //Удаляем из res лексемы, относящиеся к endToken
                int      p1 = res.size() - 1;
                String   resTokens = ((CodeToken)res.elementAt(p1)).t;
                int      lastCutCount = 1;
                while( p1 > 0 && (endToken.toLowerCase().indexOf(resTokens.toLowerCase()) != 0) ){
                    p1--;
                    resTokens = ((CodeToken)res.elementAt(p1)).t + resTokens;
                    lastCutCount++;
                }
                if( endToken.toLowerCase().indexOf(resTokens.toLowerCase()) == 0 ){
//System.out.println("Cutting last elements from res, count: " + lastCutCount );
                    Vector tmp = new Vector( res.subList( 0, res.size()-lastCutCount ) );
                    res = tmp;
                }
            }

//System.out.println("RES (before trim): " + res );
//            if( delim.length() > 0 )
//                res.removeElementAt( 0 );
//            if( delimEnd.length() > 0 )
//                res.removeElementAt( res.size()-1 );

//System.out.println("RES (after trim): " + res );

            start_end[1] = p;

        }catch( Exception e ){
            //e.printStackTrace();
            throw e;
        }

        return res;
    }

}
