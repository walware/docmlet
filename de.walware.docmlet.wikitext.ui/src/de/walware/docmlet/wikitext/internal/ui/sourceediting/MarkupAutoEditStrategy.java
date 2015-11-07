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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import static de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_CONSTRAINT;
import static de.walware.ecommons.text.ui.BracketLevel.AUTODELETE;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.text.edits.TextEdit;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractAutoEditStrategy;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.IndentUtil.ILineIndent;
import de.walware.ecommons.text.IndentUtil.IndentEditAction;
import de.walware.ecommons.text.core.ITextRegion;
import de.walware.ecommons.text.core.TextRegion;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;
import de.walware.ecommons.text.core.treepartitioner.TreePartitionUtil;
import de.walware.ecommons.text.core.util.NonDeletingPositionUpdater;
import de.walware.ecommons.ui.ISettingsChangedHandler;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCodeStyleSettings;
import de.walware.docmlet.wikitext.core.ast.Block;
import de.walware.docmlet.wikitext.core.ast.SourceComponent;
import de.walware.docmlet.wikitext.core.ast.WikidocParser;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.source.HardLineWrap;
import de.walware.docmlet.wikitext.core.source.IMarkupSourceFormatAdapter;
import de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants;
import de.walware.docmlet.wikitext.core.source.WikidocDocumentSetupParticipant;
import de.walware.docmlet.wikitext.core.source.WikitextHeuristicTokenScanner;
import de.walware.docmlet.wikitext.core.source.WikitextPartitionNodeType;
import de.walware.docmlet.wikitext.core.source.extdoc.AbstractMarkupConfig;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;
import de.walware.docmlet.wikitext.ui.sourceediting.WikitextEditingSettings;


/**
 * Auto edit strategy for Wikitext markup
 */
public class MarkupAutoEditStrategy extends AbstractAutoEditStrategy {
	
	
	public static final class Settings implements ISmartInsertSettings, ISettingsChangedHandler {
		
		private final IWikitextCoreAccess coreAccess;
		
		private boolean enabledByDefault;
		private TabAction tabAction;
		private boolean closeBrackets;
		private boolean closeParenthesis;
		private boolean closeMathDollar;
		
		private boolean hardWrapText;
		private HardWrapMode hardWrapMode;
		
		
		public Settings(final IWikitextCoreAccess coreAccess) {
			this.coreAccess= coreAccess;
			updateSettings();
		}
		
		
		@Override
		public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
			if (groupIds == null || groupIds.contains(WikitextEditingSettings.SMARTINSERT_GROUP_ID)) {
				updateSettings();
			}
		}
		
		private void updateSettings() {
			final IPreferenceAccess prefs= this.coreAccess.getPrefs();
			this.enabledByDefault= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF);
			this.tabAction= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_TAB_ACTION_PREF);
			this.closeBrackets= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF);
			this.closeParenthesis= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF);
			this.closeMathDollar= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF);
			this.hardWrapText= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF);
			this.hardWrapMode= prefs.getPreferenceValue(WikitextEditingSettings.SMARTINSERT_HARDWRAP_MODE_PREF);
		}
		
		@Override
		public boolean isSmartInsertEnabledByDefault() {
			return this.enabledByDefault;
		}
		
		@Override
		public TabAction getSmartInsertTabAction() {
			return this.tabAction;
		}
		
		public HardWrapMode getSmartInsertHardWrapMode() {
			return this.hardWrapMode;
		}
		
	}
	
	private static final char[] CURLY_BRACKET_TYPE= new char[] { '{', '}' };
	private static final char[] SQUARE_BRACKET_TYPE= new char[] { '[', ']' };
	private static final char[] PARATHESIS_TYPE= new char[] { '[', ']' };
	
	private static final WikidocParser DEFAULT_PARSER= new WikidocParser(null);
	
	private static final String POSITION_CATEGORY= "de.walware.docmlet.wikitext.MarkupAutoEdit"; //$NON-NLS-1$
	private static final IPositionUpdater POSITION_UPDATER= new NonDeletingPositionUpdater(POSITION_CATEGORY);
	
	
	private final IWikitextCoreAccess wikitextCoreAccess;
	private final Settings settings;
	
	private WikitextHeuristicTokenScanner scanner;
	private WikitextCodeStyleSettings wikitextCodeStyle;
	
	private final HardLineWrap hardLineWrap;
	
	
	public MarkupAutoEditStrategy(final IWikitextCoreAccess coreAccess, final ISourceEditor editor) {
		super(editor);
		assert (coreAccess != null);
		
		this.wikitextCoreAccess= coreAccess;
		this.settings= new Settings(coreAccess);
		this.hardLineWrap= new HardLineWrap(getDocumentContentInfo(), this.wikitextCoreAccess);
	}
	
	
	@Override
	public Settings getSettings() {
		return this.settings;
	}
	
	@Override
	protected IIndentSettings getCodeStyleSettings() {
		return this.wikitextCodeStyle;
	}
	
	
	@Override
	protected TreePartition initCustomization(final int offset, final int ch)
			throws BadLocationException, BadPartitioningException {
		if (this.scanner == null) {
			this.scanner= createScanner();
		}
		this.wikitextCodeStyle= this.wikitextCoreAccess.getWikitextCodeStyle();
		
		return super.initCustomization(offset, ch);
	}
	
	protected WikitextHeuristicTokenScanner createScanner() {
		return WikitextHeuristicTokenScanner.create(getDocumentContentInfo());
	}
	
	@Override
	protected ITextRegion computeValidRange(final int offset, final TreePartition partition, final int ch) {
		ITreePartitionNode node= partition.getTreeNode();
		if (node.getType() instanceof WikitextPartitionNodeType) {
			if (getDocumentContentInfo().getPrimaryType() == IWikitextDocumentConstants.WIKIDOC_PARTITIONING) {
				return super.computeValidRange(offset, partition, ch);
			}
			else {
				ITreePartitionNode parent;
				while ((parent= node.getParent()) != null
						&& parent.getType() instanceof WikitextPartitionNodeType) {
					node= parent;
				}
				return node;
			}
		}
		return null;
	}
	
	@Override
	protected WikitextHeuristicTokenScanner getScanner() {
		return this.scanner;
	}
	
	@Override
	protected void quitCustomization() {
		super.quitCustomization();
		
		this.wikitextCodeStyle= null;
	}
	
	
	private final boolean isClosedBracket(final int backwardOffset, final int forwardOffset,
			final String currentPartition, final char[] type) {
		try {
			final AbstractDocument doc= getDocument();
			this.scanner.configure(doc, currentPartition);
			final IRegion line= doc.getLineInformationOfOffset(forwardOffset);
			final int balance= this.scanner.computePairBalance(
					backwardOffset, line.getOffset(),
					forwardOffset, line.getOffset() + line.getLength(),
					1, type, '\\' );
			return (balance <= 0);
		}
		catch (final BadLocationException e) {
			return true;
		}
	}
	
	private boolean isValueChar(final int offset) throws BadLocationException {
		final int ch= getChar(offset);
		return (ch != -1 && Character.isLetterOrDigit(ch));
	}
	
	protected final IMarkupLanguage getMarkupLanguage() {
		return WikidocDocumentSetupParticipant.getMarkupLanguage(getDocument(),
				getDocumentContentInfo().getPartitioning() );
	}
	
	protected final IMarkupConfig getMarkupConfig() {
		final IMarkupLanguage markupLanguage= getMarkupLanguage();
		if (markupLanguage != null) {
			return markupLanguage.getMarkupConfig();
		}
		return null;
	}
	
	protected final IExtdocMarkupLanguage getExtdocMarkupLanguage() {
		final IMarkupLanguage markupLanguage= getMarkupLanguage();
		if (markupLanguage instanceof IExtdocMarkupLanguage) {
			return (IExtdocMarkupLanguage) markupLanguage;
		}
		return null;
	}
	
	
	@Override
	protected char isCustomizeKey(final KeyEvent event) {
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
	protected void doCustomizeKeyCommand(final char ch, final DocumentCommand command,
			final TreePartition partition) throws Exception {
		final String contentType= partition.getType();
		final int cEnd= command.offset + command.length;
		int linkedModeType= -1;
		int linkedModeOffset= -1;
		
		KEY: switch (ch) {
		case '\t':
			if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& isRegularTabCommand(command) ) {
				command.text= "\t"; //$NON-NLS-1$
				smartInsertOnTab(command, true);
				break KEY;
			}
			return;
		case '{':
			if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& !WikitextHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
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
			if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& !WikitextHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
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
			if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)
					&& !WikitextHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
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
			if ((WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType))
					&& !WikitextHeuristicTokenScanner.isEscaped(getDocument(), command.offset) ) {
				command.text= "$"; //$NON-NLS-1$
				if (this.settings.closeMathDollar && !isValueChar(cEnd)) {
					final IMarkupConfig markupConfig= getMarkupConfig();
					if (markupConfig instanceof AbstractMarkupConfig<?>
							&& ((AbstractMarkupConfig) markupConfig).isTexMathDollarsEnabled() ) {
						command.text= "$$"; //$NON-NLS-1$
						linkedModeType= 2 | AUTODELETE;
					}
				}
				break KEY;
			}
			return;
		case '\n':
			if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
				command.text= TextUtilities.getDefaultLineDelimiter(getDocument());
				smartIndentOnNewLine(command);
				break KEY;
			}
			break;
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
	protected void doCustomizeOtherCommand(final DocumentCommand command, final TreePartition partition)
			throws Exception {
		final String contentType= partition.getType();
		
		if (WIKIDOC_DEFAULT_CONTENT_CONSTRAINT.matches(contentType)) {
			if (command.length == 0 && TextUtilities.equals(getDocument().getLegalLineDelimiters(), command.text) != -1) {
				smartIndentOnNewLine(command);
			}
			else if (this.settings.hardWrapText) {
				smartLineWrap(command);
			}
		}
	}
	
	protected void smartIndentOnNewLine(final DocumentCommand command) throws Exception {
		customizeCommandDefault(command);
	}
	
	protected void smartLineWrap(final DocumentCommand command)
			throws Exception {
		
		final AbstractDocument doc= getDocument();
		final int lineNum= doc.getLineOfOffset(command.offset);
		final IRegion lineInfo= doc.getLineInformation(lineNum);
		IndentUtil indentUtil= null;
		
		byte processMode= 0;
		switch (this.settings.getSmartInsertHardWrapMode()) {
		case UPTO_CURSOR:
			if (!containsControl(command.text)) {
				indentUtil= createIndentUtil(doc);
				int column= indentUtil.getColumn(lineNum, command.offset);
				column= indentUtil.getColumn(command.text, command.text.length(), column);
				if (column > this.wikitextCodeStyle.getLineWidth()) {
					processMode= HardLineWrap.SELECTION_STRICT;
				}
			}
			break;
		case MERGE:
			if (containsControl(command.text) // multiline command
					|| (command.offset + command.length > lineInfo.getOffset() + lineInfo.getLength()) ) {
				processMode= HardLineWrap.SELECTION_MERGE1;
			}
			else {
				indentUtil= createIndentUtil(doc);
				int column= indentUtil.getColumn(lineNum, command.offset);
				column= indentUtil.getColumn(command.text, command.text.length(), column);
				final String tail= doc.get(command.offset + command.length,
						(lineInfo.getOffset() + lineInfo.getLength()) - (command.offset + command.length));
				column= indentUtil.getColumn(tail, tail.length(), column);
				if (column > this.wikitextCodeStyle.getLineWidth()) {
					processMode= HardLineWrap.SELECTION_MERGE1;
				}
			}
			break;
		default:
			break;
		}
		
		if (processMode != 0) {
			if (indentUtil == null) {
				indentUtil= createIndentUtil(doc);
			}
			wrapLine(command, processMode, indentUtil);
		}
	}
	
	protected void wrapLine(final DocumentCommand command, final byte mode,
			final IndentUtil indentUtil) throws Exception {
		final IExtdocMarkupLanguage markupLanguage= getExtdocMarkupLanguage();
		final IMarkupSourceFormatAdapter formatAdapter;
		if (markupLanguage == null
				|| (formatAdapter= markupLanguage.getSourceFormatAdapter()) == null) {
			return;
		}
		final AbstractDocument doc= getDocument();
		
		final TextEdit textEdit;
		{	final ITextRegion workRegion= getFastParseRegion(command);
			final SourceContent sourceContent= createSourceContent(doc, workRegion, command);
			final Document workDoc= new Document(sourceContent.getText());
			
			final WikidocParser parser= DEFAULT_PARSER;
			parser.setMarkupLanguage(markupLanguage);
			final SourceComponent sourceNode= parser.parse(sourceContent);
			
			textEdit= this.hardLineWrap.createTextEdit(workDoc, sourceNode, new TextRegion(
							doc.getLineOffset(doc.getLineOfOffset(command.offset)) - workRegion.getOffset(),
							command.offset + command.text.length() - workRegion.getOffset() ),
					mode, formatAdapter, createIndentUtil(workDoc) );
			if (textEdit == null) {
				return;
			}
			textEdit.moveTree(workRegion.getOffset());
		}
		
		final DocumentRewriteSession rewriteSession= doc.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED_SMALL);
		try {
			doc.addPositionCategory(POSITION_CATEGORY);
			doc.addPositionUpdater(POSITION_UPDATER);
			
			applyCommand(command);
			
			final Position offsetPosition= new Position(command.offset, doc.getLength() - command.offset);
			doc.addPosition(POSITION_CATEGORY, offsetPosition);
			if (command.caretOffset == -1) {
				command.caretOffset= command.offset + command.text.length();
			}
			final Position caretPosition= new Position(command.caretOffset, doc.getLength() - command.caretOffset);
			doc.addPosition(POSITION_CATEGORY, caretPosition);
			
			textEdit.apply(doc, TextEdit.NONE); 
			
			command.offset= offsetPosition.offset;
			command.caretOffset= caretPosition.offset;
			updateSelection(command);
		}
		finally {
			doc.stopRewriteSession(rewriteSession);
			doc.removePositionUpdater(POSITION_UPDATER);
			doc.removePositionCategory(POSITION_CATEGORY);
		}
	}
	
	@Override
	protected void correctIndent(final DocumentCommand command, final int minColumn,
			final IndentUtil indentUtil) throws Exception {
		// At moment only single line for tab indent
		final IExtdocMarkupLanguage markupLanguage= getExtdocMarkupLanguage();
		final IMarkupSourceFormatAdapter formatAdapter;
		if (markupLanguage == null
				|| (formatAdapter= markupLanguage.getSourceFormatAdapter()) == null) {
			return;
		}
		final AbstractDocument doc= getDocument();
		final int lineNum= doc.getLineOfOffset(command.offset);
		final int lineOffset= doc.getLineOffset(lineNum);
		
		final String prefixText;
		{	final ITextRegion workRegion= getFastParseRegion(command);
			final SourceContent sourceContent= createSourceContent(doc, workRegion, command);
			final Document workDoc= new Document(sourceContent.getText());
			
			final WikidocParser parser= DEFAULT_PARSER;
			parser.setMarkupLanguage(markupLanguage);
			final SourceComponent sourceNode= parser.parse(sourceContent);
			
			final int offsetInAst= lineOffset - workRegion.getOffset();
			final AstSelection astSelection= AstSelection.search(sourceNode, offsetInAst, offsetInAst, AstSelection.MODE_COVERING_SAME_LAST);
			final WikitextAstNode blockNode= getBlockNode(astSelection.getCovering(), offsetInAst);
			if (blockNode == null) {
				return;
			}
			
			prefixText= formatAdapter.getPrefixCont(blockNode, createIndentUtil(workDoc));
			if (prefixText == null) {
				return;
			}
		}
		
		final ILineIndent indent= indentUtil.getIndent(prefixText);
		if (indent.getIndentColumn() < minColumn) {
			return;
		}
		indentUtil.changeIndent(lineNum, lineNum, new IndentEditAction(indent.getIndentColumn()) {
			@Override
			public void doEdit(final int line, final int lineOffset, final int length, final StringBuilder text)
					throws BadLocationException {
				command.offset= lineOffset;
				command.length= length;
				command.text= (text != null) ? text.toString() : "";
			}
		});
	}
	
	private ITextRegion getFastParseRegion(final DocumentCommand command) {
		final AbstractDocument doc= getDocument();
		final IRegion validRange= getValidRange();
		final ITreePartitionNode rootNode= TreePartitionUtil.getRootNode(doc,
				getDocumentContentInfo().getPartitioning() );
		int childIdx= rootNode.indexOfChild(command.offset);
		if (childIdx < 0) {
			childIdx= -(childIdx + 1);
		}
		
		int beginOffset= 0;
		{	ITreePartitionNode child;
			if (childIdx > 0) {
				child= rootNode.getChild(childIdx - 1);
				if (child.getType() instanceof WikitextPartitionNodeType) {
					beginOffset= child.getOffset();
				}
				else {
					beginOffset= child.getOffset() + child.getLength();
				}
			}
		}
		if (beginOffset < validRange.getOffset()) {
			beginOffset= validRange.getOffset();
		}
		
		int endOffset= Integer.MAX_VALUE;
		if (childIdx < rootNode.getChildCount()) {
			final ITreePartitionNode child= rootNode.getChild(childIdx);
			if (command.offset + command.length > child.getOffset() + child.getLength()) {
				childIdx= rootNode.indexOfChild(command.offset + command.length);
				if (childIdx < 0) {
					childIdx= -(childIdx + 1);
				}
			}
		}
		{	ITreePartitionNode child;
			if (childIdx + 1 < rootNode.getChildCount()) {
				child= rootNode.getChild(childIdx + 1);
				if (child.getType() instanceof WikitextPartitionNodeType) {
					endOffset= child.getOffset() + child.getLength();
				}
				else {
					endOffset= child.getOffset();
				}
			}
		}
		if (endOffset > validRange.getOffset() + validRange.getLength()) {
			endOffset= validRange.getOffset() + validRange.getLength();
		}
		return new TextRegion(beginOffset, endOffset);
	}
	
	private WikitextAstNode getBlockNode(IAstNode node, final int offset) {
		while (node != null) {
			if (node instanceof Block && node.getOffset() < offset) {
				return (Block) node;
			}
			node= node.getParent();
		}
		return null;
	}
	
	
	private LinkedModeUI createLinkedMode(final int offset, final char type, final int mode)
			throws BadLocationException {
		final LinkedModeModel model= new LinkedModeModel();
		int pos= 0;
		
		final LinkedPositionGroup group= new LinkedPositionGroup();
		final LinkedPosition position= WikitextBracketLevel.createPosition(type, getDocument(),
				offset + 1, 0, pos++ );
		group.addPosition(position);
		model.addGroup(group);
		
		model.forceInstall();
		
		final WikitextBracketLevel level= new WikitextBracketLevel(
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
