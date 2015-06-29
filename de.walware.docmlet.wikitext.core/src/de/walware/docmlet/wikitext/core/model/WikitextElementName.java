/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.model;

import de.walware.ecommons.ltk.IElementName;


public abstract class WikitextElementName implements IElementName {
	
	
	public static final int RESOURCE=                       0x0f;
	public static final int TITLE=                          0x11;
	
	
	private static class Default extends WikitextElementName {
		
		
		protected final int type;
		protected final String segment;
		
		
		private Default(final int type, final String name) {
			this.type= type;
			this.segment= name;
		}
		
		
		@Override
		public int getType() {
			return this.type;
		}
		
		@Override
		public WikitextElementName getNextSegment() {
			return null;
		}
		
		@Override
		public String getSegmentName() {
			return this.segment;
		}
		
		@Override
		public String getDisplayName() {
			return this.segment;
		}
		
	}
	
	
	public static WikitextElementName create(final int type, final String name) {
		return new Default(type, name);
	}
	
	
	@Override
	public abstract WikitextElementName getNextSegment();
	
	
	@Override
	public int hashCode() {
		final String name= getSegmentName();
		final IElementName next= getNextSegment();
		if (next != null) {
			return getType() * ((name != null) ? name.hashCode() : 1) * (next.hashCode()+7);
		}
		else {
			return getType() * ((name != null) ? name.hashCode() : 1);
		}
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof WikitextElementName)) {
			return false;
		}
		final IElementName other= (WikitextElementName) obj;
		final String thisName= getSegmentName();
		final String otherName= other.getSegmentName();
		return ((getType() == other.getType())
				&& ((thisName != null) ? 
						(thisName == otherName || (otherName != null && thisName.hashCode() == otherName.hashCode() && thisName.equals(otherName)) ) : 
						(null == other.getSegmentName()) )
				&& ((getNextSegment() != null) ? 
						(getNextSegment().equals(other.getNextSegment())) :
						(null == other.getNextSegment()) ) );
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
	
}
