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

package de.walware.docmlet.tex.internal.ui.sourceediting;

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
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.TextEdit;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractAutoEditStrategy;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.text.BasicHeuristicTokenScanner;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.ITokenScanner;
import de.walware.ecommons.text.TextUtil;
import de.walware.ecommons.text.core.ITextRegion;
import de.walware.ecommons.text.core.input.StringParserInput;
import de.walware.ecommons.text.core.input.TextParserInput;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;
import de.walware.ecommons.ui.ISettingsChangedHandler;

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
import de.walware.docmlet.tex.ui.sourceediting.TexEditingSettings;


/**
 * Auto edit strategy for TeX code
 */
public class LtxAutoEditStrategy extends AbstractAutoEditStrategy {
	
	
	public static class Settings implements ISmartInsertSettings, ISettingsChangedHandler {
		
		private final ITexCoreAccess coreAccess;
		
		private boolean enabledByDefault;
		private TabAction tabAction;
		private boolean closeBrackets;
		private boolean closeParenthesis;
		private boolean closeMathDollar;
		
		private boolean hardWrapText;
		
		
		public Settings(final ITexCoreAccess coreAccess) {
			this.coreAccess= coreAccess;
			updateSettings();
		}
		
		
		@Override
		public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
			if (groupIds == null || groupIds.contains(TexEditingSettings.SMARTINSERT_GROUP_ID)) {
				updateSettings();
			}
		}
		
		private void updateSettings() {
			final IPreferenceAccess prefs= this.coreAccess.getPrefs();
			this.enabledByDefault= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF);
			this.tabAction= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_TAB_ACTION_PREF);
			this.closeBrackets= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF);
			this.closeParenthesis= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF);
			this.closeMathDollar= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF);
			this.hardWrapText= prefs.getPreferenceValue(TexEditingSettings.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF);
		}
		
		@Override
		public boolean isSmartInsertEnabledByDefault() {
			return this.enabledByDefault;
		}
		
		@Override
		public TabAction getSmartInsertTabAction() {
			return this.tabAction;
		}
		
	}
	
	
	private static final StringParserInput DEFAULT_PARSER_INPUT= new StringParserInput();
	
	
	private final ITexCoreAccess texCoreAccess;
	private final Settings settings;
	
	private LtxHeuristicTokenScanner scanner;
	private TexCodeStyleSettings texCodeStyle;
	private LtxSourceIndenter indenter;
	
	private final HardLineWrap hardLineWrap;
	
	
	public LtxAutoEditStrategy(final ITexCoreAccess coreAccess, final ISourceEditor editor) {
		super(editor);
		assert (coreAccess != null);
		
		this.texCoreAccess= coreAccess;
		this.settings= new Settings(coreAccess);
		this.hardLineWrap= new HardLineWrap();
	}
	
	
	@Override
	public Settings getSettings() {
		return this.settings;
	}
	
	@Override
	protected IIndentSettings getCodeStyleSettings() {
		return this.texCodeStyle;
	}
	
	
	@Override
	protected final TreePartition initCustomization(final int offset, final int ch)
			throws BadLocationException, BadPartitioningException {
		if (this.scanner == null) {
			this.scanner= createScanner();
		}
		this.texCodeStyle= this.texCoreAccess.getTexCodeStyle();
		
		return super.initCustomization(offset, ch);
	}
	
	protected LtxHeuristicTokenScanner createScanner() {
		return LtxHeuristicTokenScanner.create(getDocumentContentInfo());
	}
	
	@Override
	protected ITextRegion computeValidRange(final int offset, final TreePartition partition, final int ch) {
		ITreePartitionNode node= partition.getTreeNode();
		if (node.getType() instanceof LtxPartitionNodeType) {
			if (getDocumentContentInfo().getPrimaryType() == ITexDocumentConstants.LTX_PARTITIONING) {
				return super.computeValidRange(offset, partition, ch);
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
	
	@Override
	protected BasicHeuristicTokenScanner getScanner() {
		return this.scanner;
	}
	
	@Override
	protected final void quitCustomization() {
		super.quitCustomization();
		
		this.texCodeStyle= null;
	}
	
	
	private final boolean isClosedBracket(final int backwardOffset, final int forwardOffset,
			final String currentPartition, final int searchType) {
		int[] balance= new int[3];
		balance[searchType]++;
		this.scanner.configure(getDocument(), currentPartition);
		balance= this.scanner.computeBracketBalance(backwardOffset, forwardOffset, balance, searchType);
		return (balance[searchType] <= 0);
	}
	
	private boolean isValueChar(final int offset) throws BadLocationException {
		final int ch= getChar(offset);
		return (ch != -1 && Character.isLetterOrDigit(ch));
	}
	
	
	@Override
	protected char isCustomizeKey(KeyEvent event) {
		switch (event.character) {
		case '{':
		case '(':
		case '[':
		case '$':
			return event.character;
		case '\t':
			if (event.stateMask == 0) {
				return '\t';
			}
			break;
		case 0x0A:
		case 0x0D:
			if (getEditor3() != null) {
				return '\n';
			}
			break;
		default:
			break;
		}
		return 0;
	}
	
	@Override
	protected void doCustomizeKeyCommand(char ch, DocumentCommand command,
			TreePartition partition) throws Exception {
		final String contentType= partition.getType();
		final int cEnd= command.offset+command.length;
		int linkedModeType= -1;
		int linkedModeOffset= -1;
		
		KEY: switch (ch) {
		case '\t':
			if (LTX_ANY_CONTENT_CONSTRAINT.matches(contentType)
					&& isRegularTabCommand(command) ) {
				command.text= "\t"; //$NON-NLS-1$
				smartInsertOnTab(command,
						(contentType != ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE) );
				break KEY;
			}
			return;
		case '{':
			if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
					&& !LtxHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "{"; //$NON-NLS-1$
				if (this.settings.closeBrackets && !isValueChar(cEnd)) {
					if (!isClosedBracket(command.offset, cEnd, contentType, CURLY_BRACKET_TYPE)) {
						command.text= "{}"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					else if (getChar(cEnd) == '}') {
						linkedModeType= 2;
					}
				}
				break KEY;
			}
			return;
		case '[':
			if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
					&& !LtxHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "["; //$NON-NLS-1$
				if (this.settings.closeBrackets && !isValueChar(cEnd)) {
					if (!isClosedBracket(command.offset, cEnd, contentType, SQUARE_BRACKET_TYPE)) {
						command.text= "[]"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					else if (getChar(cEnd) == ']') {
						linkedModeType= 2;
					}
				}
				break KEY;
			}
			return;
		case '(':
			if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
					&& !LtxHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "("; //$NON-NLS-1$
				if (this.settings.closeParenthesis && !isValueChar(cEnd)) {
					if (!isClosedBracket(command.offset, cEnd, contentType, PARATHESIS_TYPE)) {
						command.text= "()"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
					else if (getChar(cEnd) == ')') {
						linkedModeType= 2;
					}
				}
				break KEY;
			}
			return;
		case '$':
			if (contentType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
					&& !LtxHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "$"; //$NON-NLS-1$
				if (this.settings.closeMathDollar
						&& partition.getOffset() == command.offset - 1
						&& getChar(command.offset - 1) == '$'
						&& TextUtil.countForward(getDocument(), cEnd, '$') == 1) {
					command.text= "$$"; //$NON-NLS-1$
					linkedModeType= 3 | AUTODELETE;
					break KEY;
				}
			}
			if ((LTX_DEFAULT_CONTENT_CONSTRAINT.matches(contentType))
					&& !LtxHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "$"; //$NON-NLS-1$
				if (this.settings.closeMathDollar && !isValueChar(cEnd)) {
					command.text= "$$"; //$NON-NLS-1$
					linkedModeType= 2 | AUTODELETE;
				}
				break KEY;
			}
			return;
		case '\n':
			if (LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT.matches(contentType)
					|| contentType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE ) {
				command.text= TextUtilities.getDefaultLineDelimiter(getDocument());
				smartIndentOnNewLine(command);
				break KEY;
			}
			return;
		default:
			assert (false);
			return;
		}
		
		if (command.doit && command.text.length() > 0 && getEditor().isEditable(true)) {
			getViewer().getTextWidget().setRedraw(false);
			try {
				applyCommand(command);
				updateSelection(command);
				
				if (linkedModeType >= 0) {
					if (linkedModeOffset < 0) {
						linkedModeOffset= command.offset;
					}
					createLinkedMode(linkedModeOffset, ch, linkedModeType).enter();
				}
			}
			finally {
				getViewer().getTextWidget().setRedraw(true);
			}
		}
	}
	
	@Override
	protected void doCustomizeOtherCommand(DocumentCommand command, TreePartition partition)
			throws Exception {
		final String contentType= partition.getType();
		
		if (LTX_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
			if (command.length == 0 && TextUtilities.equals(getDocument().getLegalLineDelimiters(), command.text) != -1) {
				smartIndentOnNewLine(command);
			}
			else if (this.settings.hardWrapText && command.length == 0) {
				smartLineWrap(command);
			}
		}
	}
	
	
	private void smartIndentOnNewLine(final DocumentCommand command) throws Exception {
		final AbstractDocument doc= getDocument();
		final IRegion line= doc.getLineInformationOfOffset(command.offset);
		int backward= command.offset;
		final ITypedRegion partition= doc.getPartition(getDocumentContentInfo().getPartitioning(),
				backward, true );
		if (partition.getType() == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) {
			backward= partition.getOffset();
		}
		int forward;
		this.scanner.configure(doc);
		if (backward >= line.getOffset()
				&& (forward= this.scanner.findAnyNonBlankForward(command.offset + command.length, LtxHeuristicTokenScanner.UNBOUND, false)) != ITokenScanner.NOT_FOUND
				&& forward + 6 < doc.getLength()
				&& doc.get(forward, 4).equals("\\end") //$NON-NLS-1$
				&& (backward= this.scanner.findAnyNonBlankBackward(backward, line.getOffset(), false)) != ITokenScanner.NOT_FOUND
				&& doc.getChar(backward) == '}'
				&& (backward= this.scanner.scanBackward(backward, line.getOffset() - 1, '\\')) != ITokenScanner.NOT_FOUND
				&& (backward == 0 || doc.getChar(backward - 1) != '\\')
				&& doc.get(backward + 1, 5).equals("begin") ) { //$NON-NLS-1$
			command.text= command.text+command.text;
		}
		
		smartIndentLine2(command, false, 1, null);
	}
	
	private int searchParseStart(final int offset) throws BadLocationException, BadPartitioningException {
		final AbstractDocument doc= getDocument();
		final ITypedRegion partition= doc.getPartition(getDocumentContentInfo().getPartitioning(),
				offset, false );
		if (partition.getType() == ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE) {
			return offset;
		}
		if (partition.getType() == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE ) {
			return LtxHeuristicTokenScanner.getSafeMathPartitionOffset(
					doc.getDocumentPartitioner(getDocumentContentInfo().getPartitioning()),
					offset );
		}
		if (partition.getType() == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
				|| partition.getType() == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE ) {
			return partition.getOffset() + partition.getLength();
		}
		return -1;
	}
	
	/**
	 * Generic method to indent lines using the 
	 * @param command handle to read and save the document informations
	 * @param indentCurrentLine
	 * @param setCaret positive values indicates the line to set the caret
	 * @param traceCursor offset to update and return (offset at state after insertion of c.text)
	 */
	private Position[] smartIndentLine2(final DocumentCommand command, final boolean indentCurrentLine,
			final int setCaret, final Position[] tracePos) throws BadLocationException, BadPartitioningException, CoreException {
		if (getEditor3() == null) {
			return tracePos;
		}
		final AbstractDocument doc= getDocument();
		final IRegion validRegion= getValidRange();
		
		// new algorithm using RSourceIndenter
		final int cEnd= command.offset+command.length;
		if (cEnd > validRegion.getOffset()+validRegion.getLength()) {
			return tracePos;
		}
		this.scanner.configure(doc);
		final int smartEnd;
		final String smartAppend;
		if (endsWithNewLine(command.text)) {
			final IRegion cEndLine= doc.getLineInformationOfOffset(cEnd);
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
		if (command.offset < validRegion.getOffset()
				|| command.offset > validRegion.getOffset()+validRegion.getLength()) {
			return tracePos;
		}
		if (command.offset > 2500) {
			final int line= doc.getLineOfOffset(command.offset) - 40;
			if (line >= 10) {
				final int lineOffset= doc.getLineOffset(line);
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
					(command.offset-shift) +
					command.text.length() +
					(smartEnd-cEnd) +
					smartAppend.length() +
					(dummyDocEnd-smartEnd) );
			s.append(doc.get(shift, command.offset-shift));
			s.append(command.text);
			if (smartEnd-cEnd > 0) {
				s.append(doc.get(cEnd, smartEnd-cEnd));
			}
			s.append(smartAppend);
			s.append(doc.get(smartEnd, dummyDocEnd-smartEnd));
			text= s.toString();
		}
		
		// Create temp doc to compute indent
		int dummyCoffset= command.offset-shift;
		int dummyCend= dummyCoffset+command.text.length();
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
		final Position cPos= new Position(dummyCoffset, command.text.length());
		dummyDoc.addPosition(cPos);
		if (tracePos != null) {
			for (int i= 0; i < tracePos.length; i++) {
				tracePos[i].offset -= shift;
				dummyDoc.addPosition(tracePos[i]);
			}
		}
		
		command.length= command.length+edit.getLength()
				// add space between two replacement regions
				// minus overlaps with c.text
				-TextUtil.overlaps(edit.getOffset(), edit.getExclusiveEnd(), dummyCoffset, dummyCend);
		if (edit.getOffset() < dummyCoffset) { // move offset, if edit begins before c
			dummyCoffset= edit.getOffset();
			command.offset= shift+dummyCoffset;
		}
		edit.apply(dummyDoc, TextEdit.NONE);
		
		// Read indent for real doc
		int dummyChangeEnd= edit.getExclusiveEnd();
		dummyCend= cPos.getOffset()+cPos.getLength();
		if (!cPos.isDeleted && dummyCend > dummyChangeEnd) {
			dummyChangeEnd= dummyCend;
		}
		command.text= dummyDoc.get(dummyCoffset, dummyChangeEnd-dummyCoffset);
		if (setCaret != 0) {
			command.caretOffset= shift+this.indenter.getNewIndentOffset(dummyFirstLine+setCaret-1);
			command.shiftsCaret= false;
		}
		this.indenter.clear();
		if (tracePos != null) {
			for (int i= 0; i < tracePos.length; i++) {
				tracePos[i].offset += shift;
			}
		}
		return tracePos;
	}
	
	protected void smartLineWrap(final DocumentCommand command)
			throws BadLocationException, BadPartitioningException, CoreException {
		if (command.length != 0) {
			return;
		}
		this.hardLineWrap.doWrapB(getDocument(), command, this.texCodeStyle.getLineWidth());
	}
	
	private LinkedModeUI createLinkedMode(final int offset, final char type, final int mode)
			throws BadLocationException {
		final LinkedModeModel model= new LinkedModeModel();
		int pos= 0;
		
		final LinkedPositionGroup group= new LinkedPositionGroup();
		final LinkedPosition position= TexBracketLevel.createPosition(type, getDocument(),
				offset + 1, 0, pos++ );
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final TexBracketLevel level= new TexBracketLevel(
				getDocument(), getDocumentContentInfo().getPartitioning(),
				ImCollections.newList(position), (mode & 0xffff0000) );
		
		/* create UI */
		final LinkedModeUI ui= new LinkedModeUI(model, getViewer());
		ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		ui.setExitPosition(getViewer(), offset + (mode & 0xff), 0, pos);
		ui.setSimpleMode(true);
		ui.setExitPolicy(level);
		return ui;
	}
	
}
