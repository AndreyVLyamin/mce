import dlc.code.CompileException;
import dlc.code.CodeToken;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * $Id: SyntaxDocument.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, реализующий тип документа с подсветкой синтаксиса.
 */
class SyntaxDocument extends DefaultStyledDocument
{
	private DefaultStyledDocument doc;
	private javax.swing.text.Element rootElement;

	private boolean multiLineComment;
	private MutableAttributeSet normal;
	private MutableAttributeSet keyword;
    private MutableAttributeSet stdFunc;
	private MutableAttributeSet comment;
	private MutableAttributeSet quote;
    private MutableAttributeSet holders;
    private MutableAttributeSet errors;
    private MutableAttributeSet debug;
    private MutableAttributeSet debugTarget;

//    private HashSet   stdFuncs;
    private Hashtable keywords = new Hashtable();
    private Vector    autoComplete = new Vector();
    private Vector    placeholders = new Vector();
//    private Hashtable funcs;

    private String m_multiLineCommentStart = "/*";
    private String m_multiLineCommentEnd = "*/";
    private String m_singleLineComment = "//";

//    SyntaxDocumentConfig conf = new SyntaxDocumentConfig();

    String  m_tabIndent = "    ";

    long    m_lastModTime = 0;
/*
    public SyntaxDocumentConfig getConfig(){
        return conf;
    }
*/
    public void setKeywords( Vector keywords ){
        this.keywords.clear();
        for( int i = 0; i < keywords.size(); i++ )
            this.keywords.put( keywords.elementAt(i), "!" );
    }
    public void setAutoComplete( Vector autoComplete ){
        this.autoComplete.clear();
        this.autoComplete.addAll( autoComplete );
    }
    public void setPlaceholders( Vector placeholders ){
        for( int i = 0; i < placeholders.size(); i++ ){
            this.placeholders.addAll( getHoldersFromAppends( (String)placeholders.elementAt(i)) );
        }
    }
    public void setCommentsSyntax( String singleStart, String multiStart, String multiEnd ){
        m_multiLineCommentStart = multiStart;
        m_multiLineCommentEnd = multiEnd;
        m_singleLineComment = singleStart;
    }

	public SyntaxDocument()
	{
		doc = this;

		rootElement = doc.getDefaultRootElement();
		putProperty( DefaultEditorKit.EndOfLineStringProperty, "\n" );

		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal, Color.black);

		comment = new SimpleAttributeSet();
		StyleConstants.setForeground(comment, Color.gray);
		StyleConstants.setItalic(comment, true);

		keyword = new SimpleAttributeSet();
		StyleConstants.setForeground(keyword, Color.blue);
        StyleConstants.setBold(keyword, true);

        stdFunc = new SimpleAttributeSet();
        StyleConstants.setForeground(stdFunc, Color.RED );
        StyleConstants.setBold(stdFunc, true);

		quote = new SimpleAttributeSet();
		StyleConstants.setForeground(quote, Color.green);

        holders = new SimpleAttributeSet();
        StyleConstants.setBackground( holders, Color.lightGray );

        errors = new SimpleAttributeSet();
        StyleConstants.setBackground( errors, Color.red );

        debug = new SimpleAttributeSet();
        StyleConstants.setBackground( debug, Color.yellow );

        debugTarget = new SimpleAttributeSet();
        StyleConstants.setBackground( debugTarget, Color.GREEN );
/*
        if( !conf.readConfig("conf.xml") ){
            //System.out.println("TOTAL FAILED! CANT GET CONFIG FILE!!!!!!");
        }
*/
        updateModTime();
	}

	/*
	 *  Override to apply syntax highlighting after the document has been updated
	 */
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
	{
        str = str.replace( "\t", m_tabIndent );
        {
            int []start_end = new int[2];
            if( isInPlaceholder(offset, start_end) ){
                super.remove( start_end[0]-1, (start_end[1]-start_end[0])+2 );
                offset = start_end[0]-1;
            }
        }

		super.insertString(offset, str, a);
		processChangedLines(offset, str.length());

        updateModTime();
	}

    public String prepareAppendString( String appender ){
        appender = appender.replaceAll( "\\\\n", "\n" );
        appender = appender.replaceAll( "\\\\r", "\r" );
        appender = appender.replaceAll( "\\\\t", m_tabIndent );
        return appender;
    }

	/*
	 *  Override to apply syntax highlighting after the document has been updated
	 */
	public void remove(int offset, int length) throws BadLocationException
	{
        int []start_end = new int[2];
        if( isInPlaceholder(offset, start_end) ){
            super.remove(start_end[0]-1, (start_end[1] - start_end[0])+2 );
        }
        else
		    super.remove(offset, length);
		processChangedLines(offset, 0);

        updateModTime();
	}

	/*
	 *  Determine how many lines have been changed,
	 *  then apply highlighting to each line
	 */
	public void processChangedLines(int offset, int length)
		throws BadLocationException
	{
		String content = doc.getText(0, doc.getLength());

		//  The lines affected by the latest document update

		int startLine = rootElement.getElementIndex( offset );
		int endLine = rootElement.getElementIndex( offset + length );

		//  Make sure all comment lines prior to the start line are commented
		//  and determine if the start line is still in a multi line comment

		setMultiLineComment( commentLinesBefore( content, startLine ) );

		//  Do the actual highlighting

		for (int i = startLine; i <= endLine; i++)
		{
			applyHighlighting(content, i);
		}

		//  Resolve highlighting to the next end multi line delimiter

		if (isMultiLineComment())
			commentLinesAfter(content, endLine);
		else
			highlightLinesAfter(content, endLine);
	}

	/*
	 *  Highlight lines when a multi line comment is still 'open'
	 *  (ie. matching end delimiter has not yet been encountered)
	 */
	private boolean commentLinesBefore(String content, int line)
	{
/*
		int offset = rootElement.getElement( line ).getStartOffset();

		//  Start of comment not found, nothing to do

		int startDelimiter = lastIndexOf( content, getStartDelimiter(), offset - 2 );

		if (startDelimiter < 0)
			return false;

		//  Matching start/end of comment found, nothing to do

		int endDelimiter = indexOf( content, getEndDelimiter(), startDelimiter );

		if (endDelimiter < offset && endDelimiter != -1)
			return false;

		//  End of comment not found, highlight the lines

//		doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
		doc.setCharacterAttributes(startDelimiter, endDelimiter - startDelimiter + 1, comment, false);
		return true;
*/

/*
		int startOffset = rootElement.getElement( line ).getStartOffset();
		int endOffset = rootElement.getElement( line ).getEndOffset();

		if( content == null || content.length() < endOffset )
			return false;

		int delimiterStart = content.substring( 0, startOffset ).lastIndexOf( getStartDelimiter() );
		if (delimiterStart < 0)
			return false;

		int delimiterEnd = content.substring( delimiterStart, startOffset ).indexOf( getEndDelimiter() );
		if( delimiterEnd < 0 )
			return true;

		doc.setCharacterAttributes( delimiterStart, delimiterEnd - delimiterStart + 1, comment, true );
*/
		return false;
	}

	/*
	 *  Highlight comment lines to matching end delimiter
	 */
	private void commentLinesAfter(String content, int line)
	{
/*
		int offset = rootElement.getElement( line ).getEndOffset();

		//  End of comment not found, nothing to do

		int endDelimiter = indexOf( content, getEndDelimiter(), offset );

		if (endDelimiter < 0)
		{
//			doc.setCharacterAttributes(offset, content.length() - offset + 1, comment, true);
			return;
		}

		//  Matching start/end of comment found, comment the lines

		int startDelimiter = lastIndexOf( content, getStartDelimiter(), endDelimiter );

		if (startDelimiter < 0 || startDelimiter <= offset)
		{
			doc.setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
		}
*/
/*
		int endOffset = rootElement.getElement( line ).getEndOffset();

		if( content == null || content.length() < endOffset )
			return;

		int delimiterEnd = indexOf( content, getEndDelimiter(), endOffset );
		if( delimiterEnd < 0 ){
			doc.setCharacterAttributes( endOffset, content.length() - endOffset + 1, comment, true );
			return;
		}

		doc.setCharacterAttributes( endOffset, delimiterEnd - endOffset + 1, comment, true );
*/
	}

	/*
	 *  Highlight lines to start or end delimiter
	 */
	private void highlightLinesAfter(String content, int line)
		throws BadLocationException
	{
		int offset = rootElement.getElement( line ).getEndOffset();

		//  Start/End delimiter not found, nothing to do

		int startDelimiter = indexOf( content, getStartDelimiter(), offset );
		int endDelimiter = indexOf( content, getEndDelimiter(), offset );

		if (startDelimiter < 0)
			startDelimiter = content.length();

		if (endDelimiter < 0)
			endDelimiter = content.length();

		int delimiter = Math.min(startDelimiter, endDelimiter);

		if (delimiter < offset)
			return;

		//	Start/End delimiter found, reapply highlighting

		int endLine = rootElement.getElementIndex( delimiter );

		for (int i = line + 1; i < endLine; i++)
		{
			Element branch = rootElement.getElement( i );
			Element leaf = doc.getCharacterElement( branch.getStartOffset() );
			AttributeSet as = leaf.getAttributes();

			if ( as.isEqual(comment) )
				applyHighlighting(content, i);
		}
	}

	/*
	 *  Parse the line to determine the appropriate highlighting
	 */
	private void applyHighlighting(String content, int line)
		throws BadLocationException
	{
		int startOffset = rootElement.getElement( line ).getStartOffset();
		int endOffset = rootElement.getElement( line ).getEndOffset() - 1;

		int lineLength = endOffset - startOffset;
		int contentLength = content.length();

		if (endOffset >= contentLength)
			endOffset = contentLength - 1;

		//  check for multi line comments
		//  (always set the comment attribute for the entire line)

/*
		if( (delimiterEnd = content.substring( startOffset, endOffset ) ){
			int delimiterEnd = content.indexOf( getEndDelimiter(), startOffset );
			if( delimiterEnd >= 0 ){
				doc.setCharacterAttributes( startOffset, delimiterEnd - startOffset + 1, comment, true );
				startOffset = delimiterEnd + getEndDelimiter().length();
			}
		}else if( startingMultiLineComment( content, startOffset, endOffset ) ){
			int delimiterStart = content.indexOf( getEndDelimiter(), startOffset );
			if( delimiterStart < endOffset ){
				doc.setCharacterAttributes( delimiterStart, endOffset - delimiterStart + 1, comment, true );
				endOffset = delimiterStart;
			}
		}else if (isMultiLineComment()){
			int delimiterEnd = content.substring( startOffset, endOffset ).indexOf( getEndDelimiter() );
			if( delimiterEnd < 0 )
				delimiterEnd = endOffset;
			doc.setCharacterAttributes( startOffset, delimiterEnd - startOffset + 1, comment, true );
			startOffset = delimiterEnd + getEndDelimiter().length();
		}
*/

/*
		if (endingMultiLineComment(content, startOffset, endOffset)
		||  isMultiLineComment()
		||  startingMultiLineComment(content, startOffset, endOffset) )
		{

			int commStart = content.substring( 0, endOffset ).indexOf( getStartDelimiter() );
			int commEnd = content.substring( endOffset ).indexOf( getEndDelimiter() );
			if( commStart < startOffset ) commStart = startOffset;
			if( commEnd < startOffset ) commEnd = endOffset;

//			doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, true);
			doc.setCharacterAttributes(commStart, commEnd - commStart + 1, comment, true);
			return;
		}
*/
		//  set normal attributes for the line

		doc.setCharacterAttributes(startOffset, lineLength, normal, true);

		//  check for single line comment

		if (content != null && content.length() > 0)
		{
			int index = content.indexOf(getSingleLineDelimiter(), startOffset);
			if ((index > -1) && (index < endOffset))
			{
				doc.setCharacterAttributes( index, endOffset - index + 1, comment, true );
				endOffset = index - 1;
			}
		}

		if (content != null && content.length() > 0)
		{
			//1. Строчка в незакрытом комменте?
			//1.1 Строчка содержит закрывающий коммент
			//  goto 2.
			//1.2 Строчка не содержит закрывающий коммент
			//2. Строчка содержит открывающий коммент
			//2.1 Строчка содержит закрывающий коммент
			//  goto 2.
			//2.2 Строчка не содержит закрывающий коммент

			int delimiterStart = content.substring(0, startOffset).lastIndexOf(getStartDelimiter());
			int delimiterEnd = -1;
			boolean bHasComments = true;
			if (delimiterStart >= 0)
			{
				//1.
				delimiterEnd = content.indexOf(getEndDelimiter(), delimiterStart);
				if (delimiterEnd < endOffset)
				{
					//1.1
					doc.setCharacterAttributes(startOffset, delimiterEnd - startOffset + getEndDelimiter().length(), comment, true);
					startOffset = delimiterEnd + getEndDelimiter().length();
				}
				else
				{
					//1.2
					doc.setCharacterAttributes(startOffset, endOffset - startOffset + getEndDelimiter().length(), comment, true);
					bHasComments = false;
					//return;
				}
			}

			delimiterStart = startOffset;
			while (bHasComments)
			{
				delimiterStart = content.substring(0, endOffset).indexOf(getStartDelimiter(), delimiterStart);
				if (delimiterStart >= 0)
				{
					//2.
					delimiterEnd = content.substring(0, endOffset).indexOf(getEndDelimiter(), delimiterStart);
					if (delimiterEnd < 0)
					{
						//2.2
						doc.setCharacterAttributes(delimiterStart, endOffset - delimiterStart + 1, comment, true);
						break;
						//return;
					}
					else
					{
						//2.1
						doc.setCharacterAttributes(delimiterStart, delimiterEnd - delimiterStart + getEndDelimiter().length(), comment, true);
						delimiterStart = delimiterEnd + getEndDelimiter().length();
					}
				}
				else
				{
					bHasComments = false;
				}
			}
/*
			int delimiterStart = content.substring(0, endOffset).lastIndexOf(getStartDelimiter());
			int delimiterEnd = -1;
			if (delimiterStart >= 0)
			{
				delimiterEnd = content.indexOf(getEndDelimiter(), delimiterStart);

				if (delimiterEnd >= 0)
				{
					doc.setCharacterAttributes(delimiterStart, delimiterEnd - delimiterStart + 1, comment, true);
					if (delimiterEnd < endOffset)
					{
						startOffset = delimiterEnd + getEndDelimiter().length();
					}
					else
						return;
				}
				else
				{
					doc.setCharacterAttributes(delimiterStart, contentLength - delimiterStart + 1, comment, true);
					return;
				}
			}
*/
		}
//		lineLength = endOffset - startOffset;
/*
		startOffset = rootElement.getElement(line).getStartOffset();
		endOffset = rootElement.getElement(line).getEndOffset() - 1;
		lineLength = endOffset - startOffset;
		contentLength = content.length();
		if (endOffset >= contentLength)
			endOffset = contentLength - 1;
*/
        //Highlighting for constructions placeholders
        {
            Vector start_len = new Vector();
			start_len.addElement(new Integer(rootElement.getElement(line).getStartOffset()));
            if( getPlaceholders( content, start_len ) ){
                for( int i = 0; i < start_len.size(); i += 2 ){
                    int start = ((Integer)start_len.elementAt(i)).intValue();
                    int length = ((Integer)start_len.elementAt(i+1)).intValue();
                    doc.setCharacterAttributes( start, length, holders, false );
                }
            }
        }


		//  check for tokens
		checkForTokens(content, startOffset, endOffset);
	}

    private boolean isInPlaceholder( int position, int []start_end ){
        try{

            if( position >= doc.getLength() || position < 0 )
                return false;

            String content = doc.getText( 0, doc.getLength() );
            boolean bOut = false;
            int    left = position;
            int    right = position;
            while( left > 0 && content.charAt(left) != '<' ){
                if( content.charAt(left) == '>' ){
                    bOut = true;
                    break;
                }
                left--;
            }

            if( bOut && left == 0 ) return false;
            left++;

            while( right < content.length() && content.charAt(right) != '>' ){
                if( content.charAt(right) == '<' ){
                    bOut = true;
                    break;
                }
                right++;
            }

            if( bOut || right == content.length() ) return false;

            String holder = content.substring( left, right );

//System.out.println("IN-HOLDER: " + holder + "/pos: " + position );
            if( isPlaceholder(holder) ){
                start_end[0] = left;
                start_end[1] = right;
                return true;
            }

        }catch( Exception exc ){
            //exc.printStackTrace();
        }
        return false;
    }

    public boolean isInPlaceholder( int position ){
        int []start_end = new int[2];
        return isInPlaceholder( position, start_end );
    }

    private boolean isPlaceholder( String token ){
        try{
/*
            Enumeration en = conf.keywords.elements();
            while( en.hasMoreElements() ){
                String app = (String)en.nextElement();
                if( app.indexOf("<") < 0 && app.indexOf(">") < 0 )
                    continue;

                Vector holders = getHoldersFromAppends( app );
                for( int i = 0; i < holders.size(); i++ ){

                    String sHolder = (String)holders.elementAt(i);
                    if( token.equalsIgnoreCase(sHolder) )
                        return true;
                }
            }
*/
            for( int i = 0; i < placeholders.size(); i++ ){
                if( token.equalsIgnoreCase( (String)placeholders.elementAt(i)) )
                    return true;
            }
        }catch( Exception e ){
            //e.printStackTrace();
        }
        return false;
    }

    private boolean getPlaceholders( String content, Vector start_len ){

        try{
/*
            int    startOffset = ((Integer)start_len.elementAt(0)).intValue();
            String text = content.substring( startOffset );
            start_len.clear();

            Enumeration en = conf.keywords.elements();
            while( en.hasMoreElements() ){
                String app = (String)en.nextElement();
                if( app.indexOf("<") < 0 && app.indexOf(">") < 0 )
                    continue;

                Vector holders = getHoldersFromAppends( app );
                for( int i = 0; i < holders.size(); i++ ){

                    String sHolder = (String)holders.elementAt(i);
                    int start = text.indexOf( sHolder );

                    if( start >= 0 ){
                        start_len.addElement( new Integer( startOffset + start ) );
                        start_len.addElement( new Integer( sHolder.length() ) );
                    }
                }
            }
*/

            int    startOffset = ((Integer)start_len.elementAt(0)).intValue();
            String text = content.substring( startOffset );
            start_len.clear();

            for( int i = 0; i < placeholders.size(); i++ ){
                int start = text.indexOf( (String)placeholders.elementAt(i) );
                if( start >= 0 ){
                    start_len.addElement( new Integer( startOffset + start ) );
                    start_len.addElement( new Integer( ((String)placeholders.elementAt(i)).length() ) );
                }
            }
/*
            Enumeration en = conf.keywords.elements();
            while( en.hasMoreElements() ){
                String app = (String)en.nextElement();
                if( app.indexOf("<") < 0 && app.indexOf(">") < 0 )
                    continue;

                Vector holders = getHoldersFromAppends( app );
                for( int i = 0; i < holders.size(); i++ ){

                    String sHolder = (String)holders.elementAt(i);
                    int start = text.indexOf( sHolder );

                    if( start >= 0 ){
                        start_len.addElement( new Integer( startOffset + start ) );
                        start_len.addElement( new Integer( sHolder.length() ) );
                    }
                }
            }
*/
        }catch( Exception e ){
            //e.printStackTrace();
        }

        return start_len.size() > 1;
    }

    private Vector getHoldersFromAppends( String app ){
        Vector res = new Vector();
        try{
            StringTokenizer st = new StringTokenizer( app, "<>", true );
            boolean bInHolder = false;
            String  sHolder = "";
            while( st.hasMoreTokens() ){
                String token = st.nextToken();

                if( token.equals("<") ){
                    sHolder = "";
                    bInHolder = true;
                }
                else if( token.equals(">") ){
                    if( sHolder.length() > 0 ){
                        res.addElement( sHolder );
                    }
                    bInHolder = false;
                }

                if( bInHolder && !token.equals("<") ){
                    sHolder += token;
                }
            }
        }catch( Exception exc ){
            //exc.printStackTrace();
        }
        return res;
    }

	/*
	 *  Does this line contain the start delimiter
	 */
	private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
		throws BadLocationException
	{
		int index = indexOf( content, getStartDelimiter(), startOffset );

		if ( (index < 0) || (index > endOffset) )
			return false;
		else
		{
			setMultiLineComment( true );
			return true;
		}
	}

	/*
	 *  Does this line contain the end delimiter
	 */
	private boolean endingMultiLineComment(String content, int startOffset, int endOffset)
		throws BadLocationException
	{
		int index = indexOf( content, getEndDelimiter(), startOffset );

		if ( (index < 0) || (index > endOffset) )
			return false;
		else
		{
			setMultiLineComment( false );
			return true;
		}
	}

	/*
	 *  We have found a start delimiter
	 *  and are still searching for the end delimiter
	 */
	private boolean isMultiLineComment()
	{
		return multiLineComment;
	}

	private void setMultiLineComment(boolean value)
	{
		multiLineComment = value;
	}

	/*
	 *	Parse the line for tokens to highlight
	 */
	private void checkForTokens(String content, int startOffset, int endOffset)
	{
		while (startOffset <= endOffset)
		{
			//  skip the delimiters to find the start of a new token

			while ( isDelimiter( content.substring(startOffset, startOffset + 1) ) )
			{
				if (startOffset < endOffset)
					startOffset++;
				else
					return;
			}

			//  Extract and process the entire token

			if ( isQuoteDelimiter( content.substring(startOffset, startOffset + 1) ) )
				startOffset = getQuoteToken(content, startOffset, endOffset);
			else
				startOffset = getOtherToken(content, startOffset, endOffset);
		}
	}

	/*
	 *
	 */
	private int getQuoteToken(String content, int startOffset, int endOffset)
	{
		String quoteDelimiter = content.substring(startOffset, startOffset + 1);
		String escapeString = getEscapeString(quoteDelimiter);

		int index;
		int endOfQuote = startOffset;

		//  skip over the escape quotes in this quote

		index = content.indexOf(escapeString, endOfQuote + 1);

		while ( (index > -1) && (index < endOffset) )
		{
			endOfQuote = index + 1;
			index = content.indexOf(escapeString, endOfQuote);
		}

		// now find the matching delimiter

		index = content.indexOf(quoteDelimiter, endOfQuote + 1);

		if ( (index < 0) || (index > endOffset) )
			endOfQuote = endOffset;
		else
			endOfQuote = index;

		doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);

		return endOfQuote + 1;
	}

	/*
	 *
	 */
	private int getOtherToken(String content, int startOffset, int endOffset)
	{
		int endOfToken = startOffset + 1;

		while ( endOfToken <= endOffset )
		{
			if ( isDelimiter( content.substring(endOfToken, endOfToken + 1) ) )
				break;

			endOfToken++;
		}

		String token = content.substring(startOffset, endOfToken);

		if ( isKeyword( token ) )
		{
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
		}
        else if( isStdFunc( token ) )
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, stdFunc, false);

		return endOfToken + 1;
	}

	/*
	 *  Assume the needle will the found at the start/end of the line
	 */
	private int indexOf(String content, String needle, int offset)
	{
		int index;

		while ( (index = content.indexOf(needle, offset)) != -1 )
		{
			String text = getLine( content, index ).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index + 1;
		}

		return index;
	}

	/*
	 *  Assume the needle will the found at the start/end of the line
	 */
	private int lastIndexOf(String content, String needle, int offset)
	{
		int index;

		while ( (index = content.lastIndexOf(needle, offset)) != -1 )
		{
			String text = getLine( content, index ).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index - 1;
		}

		return index;
	}

	private String getLine(String content, int offset)
	{
		int line = rootElement.getElementIndex( offset );
		Element lineElement = rootElement.getElement( line );
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		return content.substring(start, end - 1);
	}

	/*
	 *  Override for other languages
	 */
	protected boolean isDelimiter(String character)
	{
		String operands = ";:{}()[]+-/%<=>!&|^~*";

		if (Character.isWhitespace( character.charAt(0) ) ||
			operands.indexOf(character) != -1 )
			return true;
		else
			return false;
	}

	/*
	 *  Override for other languages
	 */
	protected boolean isQuoteDelimiter(String character)
	{
		String quoteDelimiters = "\"'";

		if (quoteDelimiters.indexOf(character) < 0)
			return false;
		else
			return true;
	}

	/*
	 *  Override for other languages
	 */
	protected boolean isKeyword(String token)
	{
		return keywords.containsKey( token );
	}

    protected boolean isStdFunc( String token ){
        //return conf.funcs.containsKey( token );
        return false;
    }

	/*
	 *  Override for other languages
	 */
	protected String getStartDelimiter()
	{
		return m_multiLineCommentStart;
	}

	/*
	 *  Override for other languages
	 */
	protected String getEndDelimiter()
	{
		return m_multiLineCommentEnd;
	}

	/*
	 *  Override for other languages
	 */
	protected String getSingleLineDelimiter()
	{
		return m_singleLineComment;
	}

	/*
	 *  Override for other languages
	 */
	protected String getEscapeString(String quoteDelimiter)
	{
		return "\\" + quoteDelimiter;
	}

	/*
	 *
	 */
	protected String addMatchingBrace(int offset) throws BadLocationException
	{
		StringBuffer whiteSpace = new StringBuffer();
		int line = rootElement.getElementIndex( offset );
		int i = rootElement.getElement(line).getStartOffset();

		while (true)
		{
			String temp = doc.getText(i, 1);

			if (temp.equals(" ") || temp.equals("\t"))
			{
				whiteSpace.append(temp);
				i++;
			}
			else
				break;
		}

		return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
	}

    private void updateModTime(){
        m_lastModTime = System.currentTimeMillis();
    }

    public long getLastModifyTime(){
        return m_lastModTime;
    }

    Vector m_lastError = new Vector();

    public void resetErrors(){
        try{
            processChangedLines( 0, doc.getLength() );
        }catch( Exception exc ){
            //exc.printStackTrace();
        }
    }

    public int getLineByCaret( int caretPosition ){
        try{
            int line = doc.getDefaultRootElement().getElementIndex( caretPosition );
            return line+1;
        }catch( Exception exc ){

        }
        return -1;
    }

    public void highlightDebugTargetString( int line ){
        highlightDebugString( line, debugTarget );
    }

    public void highlightDebugString( int line ){
        highlightDebugString( line, debug );
    }

    public void highlightDebugString( int line, MutableAttributeSet attrs ){

//System.out.println( "Highlight line: " + line );

        try{
            if( m_lastError.size() > 0 ){
                int l = ((Integer)m_lastError.elementAt(0)).intValue();
                String content = doc.getText( 0, doc.getLength() );
                try{
                    applyHighlighting( content, l );
                }catch( Exception exc ){}
            }

            if( line < 0 ) return;

            int p = doc.getDefaultRootElement().getElement(line-1).getStartOffset();
            int e = doc.getDefaultRootElement().getElement(line-1).getEndOffset();

//System.out.println("Highlight line: " + line );

            doc.setCharacterAttributes( p, (e-p), attrs, false );

            m_lastError.clear();
            m_lastError.addElement( new Integer(line-1) );

        }catch( Exception exc ){
            //exc.printStackTrace();
        }
    }

    public void highlightError( int line ){
        try{
            if( m_lastError.size() > 0 ){
                int l = ((Integer)m_lastError.elementAt(0)).intValue();
                String content = doc.getText( 0, doc.getLength() );
                try{
                    applyHighlighting( content, l );
                }catch( Exception exc ){}
            }

            if( line >= 0 ){
                int p = doc.getDefaultRootElement().getElement(line-1).getStartOffset();
                int e = doc.getDefaultRootElement().getElement(line-1).getEndOffset();
                doc.setCharacterAttributes( p, (e-p), errors, false );
                m_lastError.clear();
                m_lastError.addElement( new Integer(line-1) );
            }

        }catch( Exception exc ){
            //exc.printStackTrace();
        }
    }

    public void highlightError( CompileException ce ){
        highlightError( ce.l );
/*
        try{
            if( m_lastError.size() > 0 ){
                int line = ((Integer)m_lastError.elementAt(0)).intValue();

                String content = doc.getText( 0, doc.getLength() );

                applyHighlighting( content, line );
            }

            int p = doc.getDefaultRootElement().getElement(ce.l-1).getStartOffset();
            int e = doc.getDefaultRootElement().getElement(ce.l-1).getEndOffset();

System.out.println("Highlight line: " + ce.l + ", p: " + p + ", e: " + e );
System.out.println("\tce.l: " + ce.l + ", ce.c: " + ce.c + ", ce.p: " + ce.p );

            doc.setCharacterAttributes( p, (e-p), errors, false );

            m_lastError.clear();
            m_lastError.addElement( new Integer(ce.l-1) );

        }catch( Exception exc ){
            exc.printStackTrace();
        }
*/
    }
}
