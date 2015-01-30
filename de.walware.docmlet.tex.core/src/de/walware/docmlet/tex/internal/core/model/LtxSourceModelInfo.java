/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import java.util.Map;

import de.walware.ecommons.ltk.AbstractSourceModelInfo;
import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.ISourceStructElement;

import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ITexLabelSet;
import de.walware.docmlet.tex.internal.core.model.RefLabelAccess.Shared;


public class LtxSourceModelInfo extends AbstractSourceModelInfo implements ILtxModelInfo {
	
	
	private final ISourceStructElement fSourceElement;
	
	private final int fMinSectionLevel;
	private final int fMaxSectionLevel;
	
	private final ITexLabelSet fLabels;
	
	private final Map<String, TexCommand> fCustomCommands;
	private final Map<String, TexCommand> fCustomEnvs;
	
	
	LtxSourceModelInfo(final AstInfo ast, final ISourceStructElement unitElement,
			final int minSection, final int maxSection, final Map<String, Shared> labels,
			final Map<String, TexCommand> customCommands, final Map<String, TexCommand> customEnvs) {
		super(ast);
		fSourceElement = unitElement;
		fMinSectionLevel = minSection;
		fMaxSectionLevel = maxSection;
		fLabels = new LabelSet(labels);
		fCustomCommands = customCommands;
		fCustomEnvs = customEnvs;
	}
	
	
	@Override
	public ISourceStructElement getSourceElement() {
		return fSourceElement;
	}
	
	
	@Override
	public int getMinSectionLevel() {
		return fMinSectionLevel;
	}
	
	@Override
	public int getMaxSectionLevel() {
		return fMaxSectionLevel;
	}
	
	@Override
	public ITexLabelSet getLabels() {
		return fLabels;
	}
	
	@Override
	public Map<String, TexCommand> getCustomCommandMap() {
		return fCustomCommands;
	}
	
	@Override
	public Map<String, TexCommand> getCustomEnvMap() {
		return fCustomEnvs;
	}
	
}
