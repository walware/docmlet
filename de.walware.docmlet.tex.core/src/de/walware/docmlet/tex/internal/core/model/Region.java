/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import org.eclipse.jface.text.IRegion;


class Region implements IRegion {
	
	
	int fOffset;
	int fLength;
	
	
	public Region(final int offset, final int length) {
		fOffset = offset;
		fLength = length;
	}
	
	@Override
	public int getLength() {
		return fLength;
	}
	
	@Override
	public int getOffset() {
		return fOffset;
	}
	
	
	@Override
	public int hashCode() {
		return (fOffset << 24) | (fLength << 16);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IRegion) {
			final IRegion other = (IRegion) obj;
			return (fOffset == other.getOffset() && fLength == other.getLength());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "offset: " + fOffset + ", length: " + fLength; //$NON-NLS-1$ //$NON-NLS-2$;
	}
	
}
