/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.IElementName;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.model.IEmbeddedForeignElement;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.model.WikitextElementName;
import de.walware.docmlet.wikitext.core.model.WikitextModel;


public abstract class WikitextSourceElement implements IWikitextSourceElement, IRegion {
	
	
	private static final ImList<WikitextSourceElement> NO_CHILDREN= ImCollections.emptyList();
	
	static final List<? extends ISourceStructElement> getChildren(final List<? extends ISourceStructElement> children, final IModelElement.Filter filter) {
		if (filter == null) {
			return children;
		}
		else {
			final ArrayList<ISourceStructElement> filtered= new ArrayList<>(children.size());
			for (final ISourceStructElement child : children) {
				if (filter.include(child)) {
					filtered.add(child);
				}
			}
			return filtered;
		}
	}
	
	static final boolean hasChildren(final List<? extends ISourceStructElement> children, final IModelElement.Filter filter) {
		if (filter == null) {
			return (!children.isEmpty());
		}
		else {
			for (final ISourceStructElement child : children) {
				if (filter.include(child)) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	public abstract static class Container extends WikitextSourceElement {
		
		
		List<WikitextSourceElement> children= NO_CHILDREN;
		IRegion nameRegion;
		
		private final WikitextAstNode astNode;
		
		
		public Container(final int type, final WikitextAstNode astNode) {
			super(type);
			this.astNode= astNode;
		}
		
		
		@Override
		public IRegion getNameSourceRange() {
			return this.nameRegion;
		}
		
		@Override
		public boolean hasSourceChildren(final Filter filter) {
			return hasChildren(this.children, filter);
		}
		
		@Override
		public List<? extends ISourceStructElement> getSourceChildren(final Filter filter) {
			return getChildren(this.children, filter);
		}
		
		@Override
		public abstract Container getModelParent();
		
		@Override
		public boolean hasModelChildren(final Filter filter) {
			return hasChildren(this.children, filter);
		}
		
		@Override
		public List<? extends IModelElement> getModelChildren(final Filter filter) {
			return getChildren(this.children, filter);
		}
		
		@Override
		public Object getAdapter(final Class required) {
			if (IAstNode.class.equals(required)) {
				return this.astNode;
			}
			return super.getAdapter(required);
		}
		
	}
	
	public static class SourceContainer extends Container {
		
		
		private final ISourceUnit sourceUnit;
		
		
		public SourceContainer(final int type, final ISourceUnit su, final WikitextAstNode astNode) {
			super(type, astNode);
			this.sourceUnit= su;
		}
		
		
		@Override
		public String getId() {
			return this.sourceUnit.getId();
		}
		
		@Override
		public WikitextElementName getElementName() {
			final IElementName elementName= this.sourceUnit.getElementName();
			if (elementName instanceof WikitextElementName) {
				return (WikitextElementName) elementName;
			}
			return WikitextElementName.create(WikitextElementName.RESOURCE, elementName.getSegmentName());
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return this.sourceUnit;
		}
		
		@Override
		public boolean exists() {
			final ISourceUnitModelInfo modelInfo= getSourceUnit().getModelInfo(WikitextModel.WIKIDOC_TYPE_ID, 0, null);
			return (modelInfo != null && modelInfo.getSourceElement() == this);
		}
		
		@Override
		public boolean isReadOnly() {
			return this.sourceUnit.isReadOnly();
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return null;
		}
		
		@Override
		public Container getModelParent() {
			return null;
		}
		
	}
	
	public static class StructContainer extends Container {
		
		
		private final Container parent;
		
		
		public StructContainer(final int type, final Container parent, final WikitextAstNode astNode) {
			super(type, astNode);
			this.parent= parent;
			
			this.offset= astNode.getOffset();
			this.length= astNode.getLength();
		}
		
		
		@Override
		public ISourceUnit getSourceUnit() {
			return this.parent.getSourceUnit();
		}
		
		@Override
		public boolean exists() {
			return this.parent.exists();
		}
		
		@Override
		public boolean isReadOnly() {
			return this.parent.isReadOnly();
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return this.parent;
		}
		
		@Override
		public Container getModelParent() {
			return this.parent;
		}
		
	}
	
	public static class EmbeddedRef extends WikitextSourceElement implements IEmbeddedForeignElement {
		
		
		private final Container parent;
		private final String externType;
		private ISourceStructElement foreign;
		
		private final Embedded astNode;
		
		
		protected EmbeddedRef(final String externType, final Container parent,
				final Embedded astNode) {
			super(IModelElement.C1_EMBEDDED);
			this.externType= externType;
			this.parent= parent;
			
			this.astNode= astNode;
		}
		
		
		@Override
		public String getId() {
			final String name= getElementName().getDisplayName();
			final StringBuilder sb= new StringBuilder(name.length() + 32);
			sb.append(Integer.toHexString(getElementType() & MASK_C2));
			sb.append(':');
			sb.append(name);
			sb.append('#');
			sb.append(this.occurrenceCount);
			return sb.toString();
		}
		
		@Override
		public IElementName getElementName() {
			return (this.foreign != null) ? this.foreign.getElementName() : WikitextElementName.create(0, "");
		}
		
		@Override
		public IRegion getNameSourceRange() {
			return (this.foreign != null) ? this.foreign.getNameSourceRange() : null;
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return this.parent.getSourceUnit();
		}
		
		@Override
		public boolean exists() {
			return this.parent.exists();
		}
		
		@Override
		public boolean isReadOnly() {
			return this.parent.isReadOnly();
		}
		
		@Override
		public IWikitextSourceElement getModelParent() {
			return this.parent;
		}
		
		@Override
		public boolean hasModelChildren(final Filter filter) {
			return false;
		}
		
		@Override
		public List<? extends IModelElement> getModelChildren(final Filter filter) {
			return null;
		}
		
		@Override
		public ISourceStructElement getForeignElement() {
			return this.foreign;
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return this.parent;
		}
		
		@Override
		public boolean hasSourceChildren(final Filter filter) {
			return (this.foreign != null && (filter == null || filter.include(this.foreign)));
		}
		
		@Override
		public List<? extends ISourceStructElement> getSourceChildren(final Filter filter) {
			return (this.foreign != null && (filter == null || filter.include(this.foreign))) ?
					ImCollections.<ISourceStructElement>newList(this.foreign) : NO_CHILDREN;
		}
		
		public void setForeign(final ISourceStructElement foreign) {
			this.foreign= foreign;
		}
		
		@Override
		public Object getAdapter(final Class required) {
			if (IAstNode.class.equals(required)) {
				return this.astNode;
			}
			{	final Object adapter= super.getAdapter(required);
				if (adapter != null) {
					return adapter;
				}
			}
			return this.foreign.getAdapter(required);
		}
		
		
		@Override
		public int hashCode() {
			int h= (IModelElement.C1_EMBEDDED & MASK_C2) * this.externType.hashCode() + this.occurrenceCount;
			if (this.foreign != null) {
				h =+ this.foreign.hashCode() * 23917;
			}
			return h;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof EmbeddedRef)) {
				return false;
			}
			final EmbeddedRef other= (EmbeddedRef) obj;
			return ((getSourceParent().equals(other.getSourceParent()) )
					&& ((this.foreign != null) ? this.foreign.equals(other.foreign) : null == other.foreign) );
		}
		
	}
	
	
	private final int type;
	WikitextElementName name;
	int occurrenceCount;
	
	int offset;
	int length;
	
	
	protected WikitextSourceElement(final int type) {
		this.type= type;
	}
	
	
	@Override
	public final String getModelTypeId() {
		return WikitextModel.WIKIDOC_TYPE_ID;
	}
	
	@Override
	public final int getElementType() {
		return this.type;
	}
	
	@Override
	public IElementName getElementName() {
		return this.name;
	}
	
	@Override
	public String getId() {
		final String name= getElementName().getDisplayName();
		final StringBuilder sb= new StringBuilder(name.length() + 16);
		sb.append(Integer.toHexString(getElementType() & MASK_C2));
		sb.append(':');
		sb.append(name);
		sb.append('#');
		sb.append(this.occurrenceCount);
		return sb.toString();
	}
	
	@Override
	public IRegion getSourceRange() {
		return this;
	}
	
	@Override
	public int getOffset() {
		return this.offset;
	}
	
	@Override
	public int getLength() {
		return this.length;
	}
	
	@Override
	public IRegion getDocumentationRange() {
		return null;
	}
	
	
	@Override
	public Object getAdapter(final Class adapter) {
		return null;
	}
	
	
	@Override
	public int hashCode() {
		return (this.type & MASK_C2) * getElementName().hashCode() + this.occurrenceCount;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof WikitextSourceElement)) {
			return false;
		}
		final WikitextSourceElement other= (WikitextSourceElement) obj;
		return ( (this.type & MASK_C2) == (other.type & MASK_C2))
				&& (this.occurrenceCount == other.occurrenceCount)
				&& ( ((this.type & MASK_C1) == C1_SOURCE) || (getSourceParent().equals(other.getSourceParent())) )
				&& (getElementName().equals(other.getElementName()) );
	}
	
}
