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

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.impl.AbstractSourceModelInfo;

import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ITexLabelSet;
import de.walware.docmlet.tex.core.model.ITexSourceElement;
import de.walware.docmlet.tex.internal.core.model.RefLabelAccess.Shared;


public class LtxSourceUnitModelInfo extends AbstractSourceModelInfo implements ILtxModelInfo {
	
	
	private final ITexSourceElement sourceElement;
	
	private final int minSectionLevel;
	private final int maxSectionLevel;
	
	private final ITexLabelSet labels;
	
	private final Map<String, TexCommand> customCommands;
	private final Map<String, TexCommand> customEnvs;
	
	
	LtxSourceUnitModelInfo(final AstInfo ast, final ITexSourceElement unitElement,
			final int minSectionLevel, final int maxSectionLevel, final Map<String, Shared> labels,
			final Map<String, TexCommand> customCommands, final Map<String, TexCommand> customEnvs) {
		super(ast);
		this.sourceElement= unitElement;
		
		this.minSectionLevel= minSectionLevel;
		this.maxSectionLevel= maxSectionLevel;
		
		this.labels= new LabelSet(labels);
		this.customCommands= customCommands;
		this.customEnvs= customEnvs;
	}
	
	
	@Override
	public ITexSourceElement getSourceElement() {
		return this.sourceElement;
	}
	
	
	@Override
	public int getMinSectionLevel() {
		return this.minSectionLevel;
	}
	
	@Override
	public int getMaxSectionLevel() {
		return this.maxSectionLevel;
	}
	
	@Override
	public ITexLabelSet getLabels() {
		return this.labels;
	}
	
	@Override
	public Map<String, TexCommand> getCustomCommandMap() {
		return this.customCommands;
	}
	
	@Override
	public Map<String, TexCommand> getCustomEnvMap() {
		return this.customEnvs;
	}
	
}