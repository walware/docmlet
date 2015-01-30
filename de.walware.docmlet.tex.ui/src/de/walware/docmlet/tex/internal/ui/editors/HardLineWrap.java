/*=============================================================================#
 # Copyright (c) 2004-2015 TeXlipse-Project (texlipse.sf.net) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Antti Pirinen, Oskar Ojala, Boris von Loesch - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


/**
 * This class handles the line wrapping.
 */
public class HardLineWrap {
	
	
	private static final Pattern simpleCommandPattern =
			Pattern.compile("\\\\(\\w+|\\\\)\\s*(\\[.*?\\]\\s*)*(\\{.*?\\}\\s*)*");
	
	
	public HardLineWrap(){
	}
	
	
	/**
	 * Removes all whitespaces from the beginning of the String
	 * @param str The string to wrap
	 * @return trimmed version of the string
	 */
	private static String trimBegin (final String str) {
		int i = 0;
		while (i < str.length() && (Character.isWhitespace(str.charAt(i)))) {
			i++;
		}
		return str.substring(i);
	}
	
	/**
	 * Removes all whitespaces and the first "% " from the beginning of the 
	 * String.
	 * 
	 * Examples:
	 * "   hello world" will return "hello world"
	 * "   % hello" will return "hello"
	 * "   %hello" will return "hello"
	 * "   % % hello" will return "% hello"
	 * "   %% hello" will return "% hello"
	 * 
	 * @param str The string to trim
	 * @return trimmed version of the string
	 */
	private static String trimBeginPlusComment (final String str) {
		int i = 0;
		while (i < str.length() && (Character.isWhitespace(str.charAt(i)))) {
			i++;
		}
		if (i < str.length() && str.charAt(i) == '%') {
			i++;
		}
		if (i < str.length() && str.charAt(i) == ' ') {
			i++;
		}
		return str.substring(i);
	}
	
	/**
	 * Removes all whitespaces from the end of the String
	 * @param str The string to wrap
	 * @return trimmed version of the string
	 */
	private static String trimEnd (final String str) {
		int i = str.length() - 1;
		//while (i >= 0 && (str.charAt(i) == ' ' || str.charAt(i) == '\t')) 
		while (i >= 0 && (Character.isWhitespace(str.charAt(i)))) {
			i--;
		}
		return str.substring(0, i + 1);
	}
	
	/**
	 * This method checks, whether <i>line</i> should stay alone on one line.<br />
	 * Examples:
	 * <ul>
	 * <li>\begin{env}</li>
	 * <li>% Comments</li>
	 * <li>\command[...]{...}{...}</li>
	 * <li>(empty line)</li>
	 * <li>\\[2em]</li>
	 * </ul>
	 * 
	 * @param line
	 * @return
	 */
	private static boolean isSingleLine(final String line) {
		if (line.length() == 0) {
			return true;
		}
		if (line.startsWith("%")) {
			return true;
		}
		if ((line.startsWith("\\") && line.length() == 2))
		 {
			return true; // e.g. \\ or \[
		}
		if (line.startsWith("\\item")) {
			return true;
		}
		final Matcher m = simpleCommandPattern.matcher(line);
		if (m.matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Finds the best position in the given String to make a line break
	 * @param line
	 * @param MAX_LENGTH
	 * @return
	 */
	private static int getLineBreakPosition(final String line, final int MAX_LENGTH) {
		int offset = 0;
		//Ignore indentation
		while (offset < line.length() && (line.charAt(offset) == ' ' || line.charAt(offset) == '\t')) {
			offset++;
		}
		
		int breakOffset = -1;
		while (offset < line.length()) {
			if (offset > MAX_LENGTH && breakOffset != -1) {
				break;
			}
			if (line.charAt(offset) == ' ' || line.charAt(offset) == '\t') {
				breakOffset = offset;
			}
			offset++;
		}
		return breakOffset;
	}
	
	/**
	 * New line wrapping strategy.	
	 * The actual wrapping method. Based on the <code>IDocument d</code>
	 * and <code>DocumentCommand c</code> the method determines how the
	 * line must be wrapped. 
	 * <p>
	 * If there is more than <code>MAX_LENGTH</code>
	 * characters at the line, the method tries to detect the last white
	 * space before <code> MAX_LENGTH</code>. In case there is none, the 
	 * method finds the first white space after <code> MAX_LENGTH</code>.
	 * Normally it adds the rest of the currentline to the next line. 
	 * Exceptions are empty lines, commandlines, commentlines, and special lines like \\ or \[.
	 * 
	 * @param d             IDocument
	 * @param c             DocumentCommand
	 * @param MAX_LENGTH    How many characters are allowed at one line.
	 */
	public void doWrapB(final IDocument d, final DocumentCommand c, final int MAX_LENGTH) {
		try {
			// Get the line of the command excluding delimiter
			final IRegion commandRegion = d.getLineInformationOfOffset(c.offset);
			
			// Ignore texts with line endings
			if (commandRegion.getLength() + c.text.length() <= MAX_LENGTH || 
					c.text.indexOf("\n") >= 0 || c.text.indexOf("\r") >= 0) {
				return;
			}
			
			final String line = d.get(commandRegion.getOffset(), commandRegion.getLength());
			
			final int lineNr = d.getLineOfOffset(c.offset);
			final int cursorOnLine = c.offset - commandRegion.getOffset();
			
			//Create the newLine, we rewrite the whole currentline
			final StringBuffer newLineBuf = new StringBuffer();
			
			newLineBuf.append(line.substring(0, cursorOnLine));
			newLineBuf.append (c.text);
			newLineBuf.append(trimEnd(line.substring(cursorOnLine)));
			
			//Special case if there are white spaces at the end of the line
			if (trimEnd(newLineBuf.toString()).length() <= MAX_LENGTH) {
				return;
			}
			
			
			String delim = d.getLineDelimiter(lineNr);
			boolean isLastLine = false;
			if (delim == null) {
				//This is the last line in the document
				isLastLine = true;
				if (lineNr > 0) {
					delim = d.getLineDelimiter(lineNr - 1);
				}
				else {
					//Last chance
					final String delims[] = d.getLegalLineDelimiters();
					delim = delims[0];
				}
			}
			//String indent = tools.getIndentation(d, c); // TODO check if inside comment
			final String indent = TexEditorTools.getIndentationWithComment(line);

			int length = line.length();

			final String nextline = TexEditorTools.getStringAt(d, c, false, 1);
			final String nextTrimLine = nextline.trim(); 
			boolean isWithNextline = false;
			
			// Figure out whether the next line should be merged with the wrapped text
			
			// 1st case: wrapped text ends with . or :
			if (line.trim().endsWith(".") || line.trim().endsWith(":") || line.trim().endsWith("\\\\")){
				newLineBuf.append(delim); // do not merge
			} else {
				// 2nd case: merge comment lines
				if (TexEditorTools.getIndexOfComment(line) >= 0 // wrapped text contains a comment,
					&& TexEditorTools.isLineCommentLine(nextTrimLine) // next line is also a comment line, 
					&& TexEditorTools.getIndentation(line).equals(TexEditorTools.getIndentation(nextline)) // with the same indentation!
					&& !isSingleLine(trimBeginPlusComment(nextTrimLine))) // but not an empty comment line, commented command line, etc.
				{ 
					// merge!
					newLineBuf.append(' ');
					newLineBuf.append(trimBeginPlusComment(nextline));
					length += nextline.length();
					isWithNextline = true;
					// 3th case: Wrapped text is comment, next line is not (otherwise case 2)
				} else if (TexEditorTools.getIndexOfComment(line) >= 0) {
					newLineBuf.append(delim);
					// 4rd case: Next line is a comment/command
				} else if (isSingleLine(nextTrimLine)){
					newLineBuf.append(delim);
					// all other cases
				} else {
					// merge: Add the whole next line
					newLineBuf.append(' ');
					newLineBuf.append(trimBegin(nextline));
					length += nextline.length();
					isWithNextline = true;
				}
			}
			
			// TODO: if line has a comment at the end, this might be wrapped onto a non-comment line
			// TODO: newLine might need wrapping as well if too long
			
			if (!isLastLine) {
				length += delim.length(); //delim.length();
			}
			final String newLine = newLineBuf.toString();
			
			final int breakpos = getLineBreakPosition(newLine, MAX_LENGTH);
			if (breakpos < 0) {
				return;
			}
			
			c.length = length;
			
			c.shiftsCaret = false;
			c.caretOffset = c.offset + c.text.length() + indent.length();
			if (breakpos >= cursorOnLine + c.text.length()){ 
				c.caretOffset -= indent.length();
			}
			if (breakpos < cursorOnLine + c.text.length()){
				//Line delimiter - one white space
				c.caretOffset += delim.length() - 1;
			}
			
			c.offset = commandRegion.getOffset();
			
			final StringBuffer buf = new StringBuffer();
			buf.append(newLine.substring(0, breakpos));
			buf.append(delim);
			buf.append(indent);
			// Are we wrapping a comment onto the next line without its %?
			if (TexEditorTools.getIndexOfComment(newLine.substring(0,breakpos)) >= 0 && TexEditorTools.getIndexOfComment(indent) == -1) {
				buf.append("% ");
			}
			buf.append(trimBegin(newLine.substring(breakpos)));
			
			// Remove unnecessary characters from buf
			int i=0;
			while (i < line.length() && line.charAt(i) == buf.charAt(i)) {
				i++;
			}
			buf.delete(0, i);
			c.offset += i;
			c.length -= i;
			if (isWithNextline) {
				i=0;
				while (i < nextline.length() && 
						nextline.charAt(nextline.length()-i-1) == buf.charAt(buf.length()-i-1)) {
					i++;
				}
				buf.delete(buf.length()-i, buf.length());
				c.length -= i;
			}
			
			c.text = buf.toString();
			
		} catch(final BadLocationException e) {
			TexEditorTools.log("Problem with hard line wrap", e);
		}
	}
	
}


/**
 * Offers general tools for different TexEditor features.
 * Tools are used mainly to implement the word wrap and the indentation
 * methods.
 * 
 * @author Laura Takkinen 
 * @author Antti Pirinen
 * @author Oskar Ojala
 */
class TexEditorTools {
	
	
	public TexEditorTools() {
	}
	
	
	static void log(final String message, final Throwable e) {
		StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUIPlugin.PLUGIN_ID,
				message, e ));
	}
	
	
	/**
	 * Returns a length of a line.
	 * @param document 	IDocument that contains the line.
	 * @param command 	DocumentCommand that determines the line.
	 * @param delim 	are line delimiters counted to the line length 
	 * @param target 	-1 = previous line, 0 = current line, 1 = next line etc... 
	 * @return 			the line length 
	 */
	public static int getLineLength(final IDocument document, final DocumentCommand command, 
			final boolean delim, final int target) {
		int line;
		
		int length = 0;
		try {
			line = document.getLineOfOffset(command.offset) + target;
			if (line < 0 || line >= document.getNumberOfLines()){
				//line = document.getLineOfOffset(command.offset);
				return 0;
			}
			
			length = document.getLineLength(line);
			if (length == 0){
				return 0;
			}
			if (!delim){
				final String txt = document.get(document.getLineOffset(line), document.getLineLength(line));
				final String[] del = document.getLegalLineDelimiters();
				final int cnt = TextUtilities.endsWith(del ,txt);
				if (!delim && cnt > -1){
					length = length - del[cnt].length();				
				}
			}
		} catch (final BadLocationException e){
			log("TexEditorTools.getLineLength:",e);
		}
		return length;
	}
	
	/**
	 * Returns a text String of the (line + <code>lineDif</code>). 
	 * @param document 	IDocument that contains the line.
	 * @param command 	DocumentCommand that determines the line.
	 * @param delim 	are delimiters included
	 * @param lineDif 	0 = current line, 1 = next line, -1 previous line etc...
	 * @return 			the text of the line. 
	 */
	public static String getStringAt(final IDocument document, 
			final DocumentCommand command, final boolean delim, final int lineDif) {
		String line = "";
		int lineBegin, lineLength;
		try {
			if (delim) {
				lineLength = getLineLength(document, command, true, lineDif);
			} else {
				lineLength = getLineLength(document, command, false, lineDif);
			}
			if (lineLength > 0) {
				lineBegin = document.getLineOffset(document
						.getLineOfOffset(command.offset) + lineDif);
				line = document.get(lineBegin, lineLength);
			}
		} catch (final BadLocationException e) {
			log("TexEditorTools.getStringAt", e);
		}
		return line;
	}
	
	
	/**
	 * Checks if the target txt is a comment line
	 * @param text 	source text
	 * @return 		<code>true</code> if line starts with %-character, 
	 * 				<code>false</code> otherwise
	 */
	public static boolean isLineCommentLine(final String text) {
		return text.trim().startsWith("%");
	}
	
	/**
	 * This method will return the starting index of first
	 * comment on the given line or -1 if non is found.
	 * 
	 * This method looks for the first occurrence of an unescaped %
	 * 
	 * No special treatment of newlines is done.
	 * 
	 * @param line The line on which to look for a comment.
	 * @return the index of the first % which marks the beginning of a comment
	 *         or -1 if there is no comment on the given line.
	 */
	public static int getIndexOfComment(final String line) {
		int p = 0;
		final int n = line.length();
		while (p < n) {
			final char c = line.charAt(p);
			if (c == '%') {
				return p;
			} else if (c == '\\') {
				p++; // Ignore next character
			}
			p++;
		}
		return -1; // not found
	}
	
	
	// Oskar's additions
	
	/**
	 * Returns the indentation of the given string taking
	 * into account if the string starts with a comment.
	 * The comment character is included in the output.
	 * 
	 * @param text      source where to find the indentation
	 * @return          The indentation of the line including the comment
	 */
	public static String getIndentationWithComment(final String text) {
		final StringBuffer indentation = new StringBuffer();
		final char[] array = text.toCharArray();
		
		if (array.length == 0) {
			return indentation.toString();
		}
		
		int i = 0;
		while (i < array.length
				&& (array[i] == ' ' || array[i] == '\t')) {
			indentation.append(array[i]);
			i++;
		}
		if (i < array.length && array[i] == '%') {
			indentation.append("% ");
		}
		
		return indentation.toString();
	}
	
	/**
	 * Returns the indentation of the given string but keeping tabs.
	 * 
	 * @param text      source where to find the indentation
	 * @return          The indentation of the line
	 */
	public static String getIndentation(final String text) {
		final StringBuffer indentation = new StringBuffer();
		final char[] array = text.toCharArray();
		
		int i = 0;
		while (i < array.length
				&& (array[i] == ' ' || array[i] == '\t')) {
			indentation.append(array[i]);
			i++;
		}
		return indentation.toString();
	}
	
}
