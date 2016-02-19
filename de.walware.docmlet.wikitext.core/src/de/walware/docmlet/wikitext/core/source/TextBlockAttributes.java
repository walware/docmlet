/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.jface.text.IRegion;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;

import de.walware.jcommons.collections.ImList;


public class TextBlockAttributes extends Attributes {
	
	
	private ImList<? extends IRegion> textRegions;
	
	
	public TextBlockAttributes(final ImList<? extends IRegion> textRegions) {
		this.textRegions= textRegions;
	}
	
	
	public void setTextRegions(final ImList<? extends IRegion> textRegions) {
		this.textRegions= textRegions;
	}
	
	public ImList<? extends IRegion> getTextRegions() {
		return this.textRegions;
	}
	
}
