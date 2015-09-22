/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;


/**
 * Supports:
 *   <li>Automatic setup of MarkupConfig for documents of workspace resources.</li>
 */
public abstract class MarkupLanguageDocumentSetupParticipant1
		extends MarkupLanguageDocumentSetupParticipant
		implements IDocumentSetupParticipantExtension {
	
	
	private final IMarkupLanguageManager1 markupLanguageManager;
	
	
	public MarkupLanguageDocumentSetupParticipant1(final IMarkupLanguage markupLanguage,
			final int markupLanguageMode) {
		super(markupLanguage, markupLanguageMode);
		
		this.markupLanguageManager= WikitextCore.getMarkupLanguageManager();
	}
	
	
	@Override
	public void setup(final IDocument document, final IPath location, final LocationKind locationKind) {
		IMarkupLanguage markupLanguage= getMarkupLanguage();
		if (locationKind == LocationKind.IFILE) {
			markupLanguage= this.markupLanguageManager.getLanguage(
					ResourcesPlugin.getWorkspace().getRoot().getFile(location),
					markupLanguage.getName(), true );
		}
		doSetup(document, markupLanguage);
	}
	
}
