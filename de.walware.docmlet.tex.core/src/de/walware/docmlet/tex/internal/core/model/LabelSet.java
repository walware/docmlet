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

package de.walware.docmlet.tex.internal.core.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.walware.ecommons.collections.ImCollections;

import de.walware.docmlet.tex.core.model.ITexLabelSet;
import de.walware.docmlet.tex.core.model.TexLabelAccess;
import de.walware.docmlet.tex.internal.core.model.RefLabelAccess.Shared;


public class LabelSet implements ITexLabelSet {
	
	
	private final List<String> labelsSorted;
	private final Map<String, RefLabelAccess.Shared> map;
	
	
	public LabelSet(final Map<String, RefLabelAccess.Shared> map) {
		final Set<String> labelSet= map.keySet();
		final String[] labelArray= labelSet.toArray(new String[labelSet.size()]);
		Arrays.sort(labelArray);
		this.labelsSorted= ImCollections.newList(labelArray);
		this.map= map;
	}
	
	
	@Override
	public List<String> getAccessLabels() {
		return this.labelsSorted;
	}
	
	@Override
	public List<TexLabelAccess> getAllAccessOf(final String label) {
		final Shared shared= this.map.get(label);
		return (shared != null) ? shared.getAll() : null;
	}
	
}
