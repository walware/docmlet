/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.ui.sourceediting;

import static de.walware.docmlet.tex.core.source.ITexDocumentConstants.LTX_ANY_CONTENT_CONSTRAINT;
import static de.walware.docmlet.tex.core.source.ITexDocumentConstants.LTX_DEFAULT_CONTENT_CONSTRAINT;
import static de.walware.docmlet.tex.core.source.ITexDocumentConstants.LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT;
import static de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner.CURLY_BRACKET_TYPE;
import static de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner.PARATHESIS_TYPE;
import static de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner.SQUARE_BRACKET_TYPE;
import static de.walware.ecommons.text.ui.BracketLevel.AUTODELETE;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.texteditor.ITextEditorExtension3;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.text.ITokenScanner;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.TextUtil;
import de.walware.ecommons.text.core.input.StringParserInput;
import de.walware.ecommons.text.core.input.TextParserInput;
import de.walware.ecommons.text.core.sections.IDocContentSections;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;
import de.walware.ecommons.ui.ISettingsChangedHandler;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.ast.LtxParser;
import de.walware.docmlet.tex.core.ast.SourceComponent;
import de.walware.docmlet.tex.core.parser.NowebLtxLexer;
import de.walware.docmlet.tex.core.refactoring.LtxSourceIndenter;
import de.walware.docmlet.tex.core.source.ITexDocumentConstants;
import de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner;
import de.walware.docmlet.tex.core.source.LtxPartitionNodeType;
import de.walware.docmlet.tex.internal.ui.editors.HardLineWrap;
import de.walware.docmlet.tex.internal.ui.editors.TexBracketLevel;
import de.walware.docmlet.tex.ui.TexUI;


/**
 * Auto edit strategy for TeX code
 */
public class LtxAutoEditStrategy extends DefaultIndentLineAutoEditStrategy
		implements ISourceEditorAddon {
	
	
	protected static class SmartInsertSettings implements ISettingsChangedHandler {
		
		private final ITexCoreAccess coreAccess;
		
		private boolean byDefaultEnabled;
		private TabAction tabAction;
		private boolean closeBrackets;
		private boolean closeParenthesis;
		private boolean closeMathDollar;
		
		private boolean wrapLines;
		
		
		public SmartInsertSettings(final ITexCoreAccess coreAccess) {
			this.coreAccess= coreAccess;
		}
		
		
		@Override
		public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
			if (groupIds.contains(TexEditingSettings.SMARTINSERT_GROUP_ID)) {
				updateSettings();
			}
		}
		
		protected void updateSettings() {
			final IPreferenceAccess prefs= this.coreAccess.getPrefs();
			this.byDefaultEnabled= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF);
			this.tabAction= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_TAB_ACTION_PREF);
			this.closeBrackets= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF);
			this.closeParenthesis= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF);
			this.closeMathDollar= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF);
			this.wrapLines= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF);
		}
		
	}
	
	
	private static final StringParserInput DEFAULT_PARSER_INPUT= new StringParserInput();
	
	
	private class RealTypeListener implements VerifyKeyListener {
		@Override
		public void verifyKey(final VerifyEvent event) {
			if (!event.doit) {
				return;
			}
			switch (event.character) {
			case '{':
			case '(':
			case '[':
			case '$':
				event.doit= !customizeKeyPressed(event.character);
				return;
			case '\t':
				if (event.stateMask == 0) {
					event.doit= !customizeKeyPressed(event.character);
				}
				return;
			case 0x0A:
			case 0x0D:
				if (LtxAutoEditStrategy.this.editor3 != null) {
					event.doit= !customizeKeyPressed('\n');
				}
				return;
			default:
				return;
			}
		}
	};
	
	
	private final ISourceEditor editor;
	private final ITextEditorExtension3 editor3;
	private final IDocContentSections documentContentInfo;
	private final SourceViewer viewer;
	private final RealTypeListener typeListener;
	
	private final ITexCoreAccess texCoreAccess;
	final SmartInsertSettings editorSettings;
	
	private AbstractDocument document;
	private IRegion validRange;
	private LtxHeuristicTokenScanner scanner;
	private TexCodeStyleSettings texCodeStyle;
	private LtxSourceIndenter indenter;
	
	private boolean ignoreCommands= false;
	
	
	public LtxAutoEditStrategy(final ITexCoreAccess coreAccess, final ISourceEditor editor) {
		assert (coreAccess != null);
		assert (editor != null);
		
		this.editor= editor;
		this.documentContentInfo= this.editor.getDocumentContentInfo();
		
		this.texCoreAccess= coreAccess;
		this.editorSettings= new SmartInsertSettings(coreAccess);
//		assert (editorSettings != null);
		
		this.viewer= this.editor.getViewer();
		this.editor3= (editor instanceof SourceEditor1) ? (SourceEditor1) editor : null;
		this.typeListener= new RealTypeListener();
	}
	
	
	@Override
	public void install(final ISourceEditor editor) {
		assert (editor.getViewer() == this.viewer);
		this.viewer.prependVerifyKeyListener(this.typeListener);
		this.editorSettings.updateSettings();
	}
	
	@Override
	public void uninstall() {
		this.viewer.removeVerifyKeyListener(this.typeListener);
	}
	
	
	private final TreePartition initCustomization(final int offset, final int c)
			throws BadLocationException, BadPartitioningException {
		assert(this.document != null);
		if (this.scanner == null) {
			this.scanner= createScanner();
		}
		this.texCodeStyle= this.texCoreAccess.getTexCodeStyle();
		
		final TreePartition partition= (TreePartition) this.document.getPartition(
				this.scanner.getDocumentPartitioning(), offset, true );
		this.validRange= getValidRange(offset, partition, c);
		return (this.validRange != null) ? partition : null;
	}
	
	protected LtxHeuristicTokenScanner createScanner() {
		return LtxHeuristicTokenScanner.create(this.documentContentInfo);
	}
	
	protected IRegion getValidRange(final int offset, final TreePartition partition, final int c) {
		ITreePartitionNode node= partition.getTreeNode();
		if (node.getType() instanceof LtxPartitionNodeType) {
			if (this.documentContentInfo.getPrimaryType() == ITexDocumentConstants.LTX_PARTITIONING) {
				return new Region(0, this.document.getLength());
			}
			else {
				ITreePartitionNode parent;
				while ((parent= node.getParent()) != null
						&& parent.getType() instanceof LtxPartitionNodeType) {
					node= parent;
				}
				return node;
			}
		}
		return null;
	}
	
	protected final IDocument getDocument() {
		return this.document;
	}
	
	private final void quitCustomization() {
		this.document= null;
		this.texCodeStyle= null;
	}
	
	
	private final boolean isSmartInsertEnabled() {
		return ((this.editor3 != null) ?
				(this.editor3.getInsertMode() == ITextEditorExtension3.SMART_INSERT) :
				this.editorSettings.byDefaultEnabled);
	}
	
	private final boolean isBlockSelection() {
		final StyledText textWidget= this.viewer.getTextWidget();
		return (textWidget.getBlockSelection() && textWidget.getSelectionRanges().length > 2);
	}
	
	private final boolean isClosedBracket(final int backwardOffset, final int forwardOffset,
			final String currentPartition, final int searchType) {
		int[] balance= new int[3];
		balance[searchType]++;
		this.scanner.configure(this.document, currentPartition);
		balance= this.scanner.computeBracketBalance(backwardOffset, forwardOffset, balance, searchType);
		return (balance[searchType] <= 0);
	}
	
	private boolean isCharAt(final int offset, final char c) throws BadLocationException {
		return (offset >= this.validRange.getOffset() && offset < this.validRange.getOffset()+this.validRange.getLength()
				&& this.document.getChar(offset) == c);
	}
	
	private boolean isValueChar(final int offset) throws BadLocationException {
		if (offset >= this.validRange.getOffset() && offset < this.validRange.getOffset()+this.validRange.getLength()) {
			final int c= this.document.getChar(offset);
			return (Character.isLetterOrDigit(c));
		}
		return false;
	}
	
	
	private final HardLineWrap fWrap= new HardLineWrap();
	
	@Override
	public void customizeDocumentCommand(final IDocument d, final DocumentCommand c) {
		if (this.ignoreCommands || c.doit == false || c.text == null) {
			return;
		}
		if (!isSmartInsertEnabled() || isBlockSelection()) {
			super.customizeDocumentCommand(d, c);
			return;
		}
		
		try {
			this.document= (AbstractDocument) d;
			final TreePartition partition= initCustomization(c.offset, -1);
			if (partition == null) {
				return;
			}
			final String contentType= partition.getType();
			
			if (LTX_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
				if (c.length == 0 && TextUtilities.equals(d.getLegalLineDelimiters(), c.text) != -1) {
					smartIndentOnNewLine(c);
				}
				else if (c.length == 0 && this.editorSettings.wrapLines) {
					this.fWrap.doWrapB(this.document, c, this.texCodeStyle.getLineWidth());
				}
			}
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUI.PLUGIN_ID, -1,
					"An error occurred when customizing action for document command in LaTeX auto edit strategy.", e )); //$NON-NLS-1$
		}
		finally {
			quitCustomization();
		}
	}
	
	/**
	 * Second main entry method for real single key presses.
	 * 
	 * @return <code>true</code>, if key was processed by method
	 */
	private boolean customizeKeyPressed(final char c) {
		if (!isSmartInsertEnabled() || !UIAccess.isOkToUse(this.viewer) || isBlockSelection()) {
			return false;
		}
		
		try {
			this.document= (AbstractDocument) this.viewer.getDocument();
			ITextSelection selection= (ITextSelection) this.viewer.getSelection();
			final TreePartition partition= initCustomization(selection.getOffset(), c);
			if (partition == null) {
				return false;
			}
			final String contentType= partition.getType();
			this.ignoreCommands= true;
			
			final DocumentCommand command= new DocumentCommand() {};
			command.offset= selection.getOffset();
			command.length= selection.getLength();
			command.doit= true;
			command.shiftsCaret= true;
			command.caretOffset= -1;
			int linkedModeType= -1;
			int linkedModeOffset= -1;
			final int cEnd= command.offset+command.length;
			
			KEY: switch (c) {
			case '\t':
				if (LTX_ANY_CONTENT_CONSTRAINT.matches(contentType)) {
					if (contentType != ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE
							&& (command.length == 0 || this.document.getLineOfOffset(command.offset) == this.document.getLineOfOffset(cEnd)) ) {
						command.text= "\t"; //$NON-NLS-1$
						switch (smartIndentOnTab(command)) {
						case -1:
							return false;
						case 0:
							break;
						case 1:
							break KEY;
						}
					}
					if (this.texCodeStyle.getReplaceOtherTabsWithSpaces()) {
						final IndentUtil indent= new IndentUtil(this.document, this.texCodeStyle);
						command.text= indent.createTabSpacesCompletionString(indent.getColumnAtOffset(command.offset));
						break KEY;
					}
				}
				return false;
			case '{':
				if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
						&& !LtxHeuristicTokenScanner.isEscaped(this.document, command.offset) ) {
					command.text= "{"; //$NON-NLS-1$
					if (this.editorSettings.closeBrackets && !isValueChar(cEnd)) {
						if (!isClosedBracket(command.offset, cEnd, contentType, CURLY_BRACKET_TYPE)) {
							command.text= "{}"; //$NON-NLS-1$
							linkedModeType= 2 | AUTODELETE;
						}
						else if (isCharAt(cEnd, '}')) {
							linkedModeType= 2;
						}
					}
					break KEY;
				}
				return false;
			case '[':
				if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
						&& !LtxHeuristicTokenScanner.isEscaped(this.document, command.offset) ) {
					command.text= "["; //$NON-NLS-1$
					if (this.editorSettings.closeBrackets && !isValueChar(cEnd)) {
						if (!isClosedBracket(command.offset, cEnd, contentType, SQUARE_BRACKET_TYPE)) {
							command.text= "[]"; //$NON-NLS-1$
							linkedModeType= 2 | AUTODELETE;
						}
						else if (isCharAt(cEnd, ']')) {
							linkedModeType= 2;
						}
					}
					break KEY;
				}
				return false;
			case '(':
				if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
						&& !LtxHeuristicTokenScanner.isEscaped(this.document, command.offset) ) {
					command.text= "("; //$NON-NLS-1$
					if (this.editorSettings.closeParenthesis && !isValueChar(cEnd)) {
						if (!isClosedBracket(command.offset, cEnd, contentType, PARATHESIS_TYPE)) {
							command.text= "()"; //$NON-NLS-1$
							linkedModeType= 2 | AUTODELETE;
						}
						else if (isCharAt(cEnd, ')')) {
							linkedModeType= 2;
						}
					}
					break KEY;
				}
				return false;
			case '$':
				if (contentType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
						&& !LtxHeuristicTokenScanner.isEscaped(this.document, command.offset) ) {
					command.text= "$"; //$NON-NLS-1$
					if (this.editorSettings.closeMathDollar
							&& partition.getOffset() == command.offset - 1
							&& this.document.getChar(command.offset - 1) == '$'
							&& TextUtil.countForward(this.document, cEnd, '$') == 1) {
						command.text= "$$"; //$NON-NLS-1$
						linkedModeType= 3 | AUTODELETE;
						break KEY;
					}
				}
				if ((LTX_DEFAULT_CONTENT_CONSTRAINT.matches(contentType))
						&& !LtxHeuristicTokenScanner.isEscaped(this.document, command.offset) ) {
					command.text= "$"; //$NON-NLS-1$
					if (this.editorSettings.closeMathDollar && !isValueChar(cEnd)) {
						command.text= "$$"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					break KEY;
				}
				return false;
			case '\n':
				if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
						|| contentType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE ) {
					command.text= TextUtilities.getDefaultLineDelimiter(this.document);
					smartIndentOnNewLine(command);
					break KEY;
				}
				return false;
			default:
				assert (false);
				return false;
			}
			
			if (command.text.length() > 0 && this.editor.isEditable(true)) {
				this.viewer.getTextWidget().setRedraw(false);
				try {
					this.document.replace(command.offset, command.length, command.text);
					final int cursor= (command.caretOffset >= 0) ? command.caretOffset :
							command.offset+command.text.length();
					selection= new TextSelection(this.document, cursor, 0);
					this.viewer.setSelection(selection, true);
					
					if (linkedModeType >= 0) {
						if (linkedModeOffset < 0) {
							linkedModeOffset= command.offset;
						}
						createLinkedMode(linkedModeOffset, c, linkedModeType).enter();
					}
				}
				finally {
					this.viewer.getTextWidget().setRedraw(true);
				}
			}
			return true;
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUI.PLUGIN_ID, -1,
					"An error occurred when customizing action for pressed key in LaTeX auto edit strategy.", //$NON-NLS-1$
					e ));
		}
		finally {
			this.ignoreCommands= false;
			quitCustomization();
		}
		return false;
	}
	
	
	private int smartIndentOnTab(final DocumentCommand c) throws BadLocationException {
		final IRegion line= this.document.getLineInformation(this.document.getLineOfOffset(c.offset));
		int first;
		this.scanner.configure(this.document);
		first= this.scanner.findAnyNonBlankBackward(c.offset, line.getOffset()-1, false);
		if (first != ITokenScanner.NOT_FOUND) { // not first char
			return 0;
		}
		final IndentUtil indentation= new IndentUtil(this.document, this.texCodeStyle);
		final int column= indentation.getColumnAtOffset(c.offset);
		if (this.editorSettings.tabAction != ISmartInsertSettings.TabAction.INSERT_TAB_CHAR) {
			c.text= indentation.createIndentCompletionString(column);
		}
		return 1;
	}
	
	private void smartIndentOnNewLine(final DocumentCommand c) throws BadLocationException, BadPartitioningException, CoreException {
		final IRegion line= this.document.getLineInformationOfOffset(c.offset);
		int backward= c.offset;
		final ITypedRegion partition= this.document.getPartition(this.documentContentInfo.getPartitioning(),
				backward, true );
		if (partition.getType() == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) {
			backward= partition.getOffset();
		}
		int forward;
		this.scanner.configure(this.document);
		if (backward >= line.getOffset()
				&& (forward= this.scanner.findAnyNonBlankForward(c.offset + c.length, LtxHeuristicTokenScanner.UNBOUND, false)) != ITokenScanner.NOT_FOUND
				&& forward + 6 < this.document.getLength()
				&& this.document.get(forward, 4).equals("\\end") //$NON-NLS-1$
				&& (backward= this.scanner.findAnyNonBlankBackward(backward, line.getOffset(), false)) != ITokenScanner.NOT_FOUND
				&& this.document.getChar(backward) == '}'
				&& (backward= this.scanner.scanBackward(backward, line.getOffset() - 1, '\\')) != ITokenScanner.NOT_FOUND
				&& (backward == 0 || this.document.getChar(backward - 1) != '\\')
				&& this.document.get(backward + 1, 5).equals("begin") ) { //$NON-NLS-1$
			c.text= c.text+c.text;
		}
		
		smartIndentLine2(c, false, 1, null);
	}
	
	private int searchParseStart(final int offset) throws BadLocationException, BadPartitioningException {
		final ITypedRegion partition= this.document.getPartition(this.documentContentInfo.getPartitioning(),
				offset, false );
		if (partition.getType() == ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE) {
			return offset;
		}
		if (partition.getType() == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE ) {
			return LtxHeuristicTokenScanner.getSafeMathPartitionOffset(
					this.document.getDocumentPartitioner(this.documentContentInfo.getPartitioning()),
					offset );
		}
		if (partition.getType() == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE ) {
			return partition.getOffset() + partition.getLength();
		}
		return -1;
	}
	
	private final boolean endsWithNewLine(final String text) {
		for (int i= text.length()-1; i >= 0; i--) {
			final char c= text.charAt(i);
			if (c == '\r' || c == '\n') {
				return true;
			}
			if (c != ' ' && c != '\t') {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Generic method to indent lines using the 
	 * @param c handle to read and save the document informations
	 * @param indentCurrentLine
	 * @param setCaret positive values indicates the line to set the caret
	 * @param traceCursor offset to update and return (offset at state after insertion of c.text)
	 */
	private Position[] smartIndentLine2(final DocumentCommand c, final boolean indentCurrentLine,
			final int setCaret, final Position[] tracePos) throws BadLocationException, BadPartitioningException, CoreException {
		if (this.editor3 == null) {
			return tracePos;
		}
		final IRegion validRegion= this.validRange;
		
		// new algorithm using RSourceIndenter
		final int cEnd= c.offset+c.length;
		if (cEnd > validRegion.getOffset()+validRegion.getLength()) {
			return tracePos;
		}
		this.scanner.configure(this.document);
		final int smartEnd;
		final String smartAppend;
		if (endsWithNewLine(c.text)) {
			final IRegion cEndLine= this.document.getLineInformationOfOffset(cEnd);
			final int validEnd= (cEndLine.getOffset()+cEndLine.getLength() <= validRegion.getOffset()+validRegion.getLength()) ?
					cEndLine.getOffset()+cEndLine.getLength() : validRegion.getOffset()+validRegion.getLength();
			final int next= this.scanner.findAnyNonBlankForward(cEnd, validEnd, false);
			smartEnd= (next >= 0) ? next : validEnd;
			smartAppend= ""; //$NON-NLS-1$
		}
		else {
			smartEnd= cEnd;
			smartAppend= ""; //$NON-NLS-1$
		}
		
		int shift= 0;
		if (c.offset < validRegion.getOffset()
				|| c.offset > validRegion.getOffset()+validRegion.getLength()) {
			return tracePos;
		}
		if (c.offset > 2500) {
			final int line= this.document.getLineOfOffset(c.offset) - 40;
			if (line >= 10) {
				final int lineOffset= this.document.getLineOffset(line);
				shift= searchParseStart(lineOffset);
			}
		}
		if (shift < validRegion.getOffset()) {
			shift= validRegion.getOffset();
		}
		int dummyDocEnd= cEnd+1500;
		if (dummyDocEnd > validRegion.getOffset()+validRegion.getLength()) {
			dummyDocEnd= validRegion.getOffset()+validRegion.getLength();
		}
		final String text;
		{	final StringBuilder s= new StringBuilder(
					(c.offset-shift) +
					c.text.length() +
					(smartEnd-cEnd) +
					smartAppend.length() +
					(dummyDocEnd-smartEnd) );
			s.append(this.document.get(shift, c.offset-shift));
			s.append(c.text);
			if (smartEnd-cEnd > 0) {
				s.append(this.document.get(cEnd, smartEnd-cEnd));
			}
			s.append(smartAppend);
			s.append(this.document.get(smartEnd, dummyDocEnd-smartEnd));
			text= s.toString();
		}
		
		// Create temp doc to compute indent
		int dummyCoffset= c.offset-shift;
		int dummyCend= dummyCoffset+c.text.length();
		final AbstractDocument dummyDoc= new Document(text);
		final TextParserInput parserInput= (Display.getCurrent() == Display.getDefault()) ?
				DEFAULT_PARSER_INPUT.reset(text) : new StringParserInput(text);
		
		// Lines to indent
		int dummyFirstLine= dummyDoc.getLineOfOffset(dummyCoffset);
		final int dummyLastLine= dummyDoc.getLineOfOffset(dummyCend);
		if (!indentCurrentLine) {
			dummyFirstLine++;
		}
		if (dummyFirstLine > dummyLastLine) {
			return tracePos;
		}
		
		// Compute indent
		final LtxParser scanner= new LtxParser(new NowebLtxLexer(), null);
		final SourceComponent rootNode= scanner.parse(parserInput.init(),
				this.texCoreAccess.getTexCommandSet() );
		if (this.indenter == null) {
			this.indenter= new LtxSourceIndenter();
		}
		this.indenter.setup(this.texCoreAccess);
		final TextEdit edit= this.indenter.getIndentEdits(dummyDoc, rootNode, 0, dummyFirstLine, dummyLastLine);
		
		// Apply indent to temp doc
		final Position cPos= new Position(dummyCoffset, c.text.length());
		dummyDoc.addPosition(cPos);
		if (tracePos != null) {
			for (int i= 0; i < tracePos.length; i++) {
				tracePos[i].offset -= shift;
				dummyDoc.addPosition(tracePos[i]);
			}
		}
		
		c.length= c.length+edit.getLength()
				// add space between two replacement regions
				// minus overlaps with c.text
				-TextUtil.overlaps(edit.getOffset(), edit.getExclusiveEnd(), dummyCoffset, dummyCend);
		if (edit.getOffset() < dummyCoffset) { // move offset, if edit begins before c
			dummyCoffset= edit.getOffset();
			c.offset= shift+dummyCoffset;
		}
		edit.apply(dummyDoc, TextEdit.NONE);
		
		// Read indent for real doc
		int dummyChangeEnd= edit.getExclusiveEnd();
		dummyCend= cPos.getOffset()+cPos.getLength();
		if (!cPos.isDeleted && dummyCend > dummyChangeEnd) {
			dummyChangeEnd= dummyCend;
		}
		c.text= dummyDoc.get(dummyCoffset, dummyChangeEnd-dummyCoffset);
		if (setCaret != 0) {
			c.caretOffset= shift+this.indenter.getNewIndentOffset(dummyFirstLine+setCaret-1);
			c.shiftsCaret= false;
		}
		this.indenter.clear();
		if (tracePos != null) {
			for (int i= 0; i < tracePos.length; i++) {
				tracePos[i].offset += shift;
			}
		}
		return tracePos;
	}
	
	private LinkedModeUI createLinkedMode(final int offset, final char type, final int mode)
			throws BadLocationException {
		final LinkedModeModel model= new LinkedModeModel();
		int pos= 0;
		
		final LinkedPositionGroup group= new LinkedPositionGroup();
		final LinkedPosition position= TexBracketLevel.createPosition(type, this.document,
				offset + 1, 0, pos++ );
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final TexBracketLevel level= new TexBracketLevel(this.document,
				this.scanner.getDocumentPartitioning(),
				ImCollections.newList(position), (mode & 0xffff0000) );
		
		/* create UI */
		final LinkedModeUI ui= new LinkedModeUI(model, this.viewer);
		ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		ui.setExitPosition(this.viewer, offset + (mode & 0xff), 0, pos);
		ui.setSimpleMode(true);
		ui.setExitPolicy(level);
		return ui;
	}
	
}
