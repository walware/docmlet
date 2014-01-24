/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.ltk.IElementName;
import de.walware.ecommons.ltk.IModelElement;
import de.walware.ecommons.ltk.ISourceStructElement;
import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.ast.IAstNode;

import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.IEmbeddedForeignElement;
import de.walware.docmlet.tex.core.model.ILtxSourceElement;
import de.walware.docmlet.tex.core.model.TexElementName;
import de.walware.docmlet.tex.core.model.TexModel;


public abstract class LtxSourceElement implements ILtxSourceElement, IRegion {
	
	
	private static final List<LtxSourceElement> NO_TEXSOURCE_CHILDREN = Collections.emptyList();
	
	static final List<? extends ISourceStructElement> getChildren(final List<? extends ISourceStructElement> children, final IModelElement.Filter filter) {
		if (filter == null) {
			return children;
		}
		else {
			final ArrayList<ISourceStructElement> filtered = new ArrayList<ISourceStructElement>(children.size());
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
	
	
	public abstract static class Container extends LtxSourceElement {
		
		
		List<LtxSourceElement> fChildren = NO_TEXSOURCE_CHILDREN;
		IRegion fNameRegion;
		
		private final TexAstNode fAstNode;
		
		
		public Container(final int type, final TexAstNode astNode) {
			super(type);
			fAstNode = astNode;
		}
		
		
		@Override
		public IRegion getNameSourceRange() {
			return fNameRegion;
		}
		
		@Override
		public boolean hasSourceChildren(final Filter filter) {
			return hasChildren(fChildren, filter);
		}
		
		@Override
		public List<? extends ISourceStructElement> getSourceChildren(final Filter filter) {
			return getChildren(fChildren, filter);
		}
		
		@Override
		public abstract Container getModelParent();
		
		@Override
		public boolean hasModelChildren(final Filter filter) {
			return hasChildren(fChildren, filter);
		}
		
		@Override
		public List<? extends IModelElement> getModelChildren(final Filter filter) {
			return getChildren(fChildren, filter);
		}
		
		@Override
		public Object getAdapter(final Class required) {
			if (IAstNode.class.equals(required)) {
				return fAstNode;
			}
			return super.getAdapter(required);
		}
		
	}
	
	public static class SourceContainer extends Container {
		
		
		private final ISourceUnit fSourceUnit;
		
		
		public SourceContainer(final int type, final ISourceUnit su, final TexAstNode astNode) {
			super(type, astNode);
			fSourceUnit = su;
		}
		
		
		@Override
		public String getId() {
			return fSourceUnit.getId();
		}
		
		@Override
		public TexElementName getElementName() {
			final IElementName elementName = fSourceUnit.getElementName();
			if (elementName instanceof TexElementName) {
				return (TexElementName) elementName;
			}
			return TexElementName.create(TexElementName.RESOURCE, elementName.getSegmentName());
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return fSourceUnit;
		}
		
		@Override
		public boolean exists() {
			final ISourceUnitModelInfo modelInfo = getSourceUnit().getModelInfo(TexModel.LTX_TYPE_ID, 0, null);
			return (modelInfo != null && modelInfo.getSourceElement() == this);
		}
		
		@Override
		public boolean isReadOnly() {
			return fSourceUnit.isReadOnly();
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
		
		
		private final Container fParent;
		
		
		public StructContainer(final int type, final Container parent, final TexAstNode astNode) {
			super(type, astNode);
			fParent = parent;
			
			fOffset = astNode.getOffset();
			fLength = astNode.getLength();
		}
		
		
		@Override
		public String getId() {
			return fName.getSegmentName();
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return fParent.getSourceUnit();
		}
		
		@Override
		public boolean exists() {
			return fParent.exists();
		}
		
		@Override
		public boolean isReadOnly() {
			return fParent.isReadOnly();
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return fParent;
		}
		
		@Override
		public Container getModelParent() {
			return fParent;
		}
		
	}
	
	public static class EmbeddedRef extends LtxSourceElement implements IEmbeddedForeignElement {
		
		
		private final Container fParent;
		private final String fExternType;
		private ISourceStructElement fForeign;
		
		private final Embedded fAstNode;
		
		
		protected EmbeddedRef(final String externType, final Container parent,
				final Embedded astNode) {
			super(IModelElement.C1_EMBEDDED);
			fExternType = externType;
			fParent = parent;
			
			fAstNode = astNode;
		}
		
		
		@Override
		public String getId() {
			return "noweb:"+fExternType; //$NON-NLS-1$
		}
		
		@Override
		public IElementName getElementName() {
			return (fForeign != null) ? fForeign.getElementName() : TexElementName.create(0, "");
		}
		
		@Override
		public IRegion getNameSourceRange() {
			return (fForeign != null) ? fForeign.getNameSourceRange() : null;
		}
		
		@Override
		public ISourceUnit getSourceUnit() {
			return fParent.getSourceUnit();
		}
		
		@Override
		public boolean exists() {
			return fParent.exists();
		}
		
		@Override
		public boolean isReadOnly() {
			return fParent.isReadOnly();
		}
		
		@Override
		public ILtxSourceElement getModelParent() {
			return fParent;
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
			return fForeign;
		}
		
		@Override
		public ISourceStructElement getSourceParent() {
			return fParent;
		}
		
		@Override
		public boolean hasSourceChildren(final Filter filter) {
			return (fForeign != null && (filter == null || filter.include(fForeign)));
		}
		
		@Override
		public List<? extends ISourceStructElement> getSourceChildren(final Filter filter) {
			return (fForeign != null && (filter == null || filter.include(fForeign))) ?
					Collections.singletonList(fForeign) : NO_TEXSOURCE_CHILDREN;
		}
		
		public void setForeign(final ISourceStructElement foreign) {
			fForeign = foreign;
		}
		
		@Override
		public Object getAdapter(final Class required) {
			if (IAstNode.class.equals(required)) {
				return fAstNode;
			}
			{	final Object adapter = super.getAdapter(required);
				if (adapter != null) {
					return adapter;
				}
			}
			return fForeign.getAdapter(required);
		}
		
		
		@Override
		public int hashCode() {
			int h = (IModelElement.C1_EMBEDDED & MASK_C2) * fExternType.hashCode() + fOccurrenceCount;
			if (fForeign != null) {
				h =+ fForeign.hashCode() * 23917;
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
			final EmbeddedRef other = (EmbeddedRef) obj;
			return ((getSourceParent().equals(other.getSourceParent()) )
					&& ((fForeign != null) ? fForeign.equals(other.fForeign) : null == other.fForeign) );
		}
		
	}
	
	
	private final int fType;
	TexElementName fName;
	int fOccurrenceCount;
	
	int fOffset;
	int fLength;
	
	
	protected LtxSourceElement(final int type) {
		fType = type;
	}
	
	
	@Override
	public final String getModelTypeId() {
		return TexModel.LTX_TYPE_ID;
	}
	
	@Override
	public final int getElementType() {
		return fType;
	}
	
	@Override
	public IElementName getElementName() {
		return fName;
	}
	
	@Override
	public IRegion getSourceRange() {
		return this;
	}
	
	@Override
	public int getOffset() {
		return fOffset;
	}
	
	@Override
	public int getLength() {
		return fLength;
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
		return (fType & MASK_C2) * getElementName().hashCode() + fOccurrenceCount;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LtxSourceElement)) {
			return false;
		}
		final LtxSourceElement other = (LtxSourceElement) obj;
		return ( (fType & MASK_C2) == (other.fType & MASK_C2))
				&& (fOccurrenceCount == other.fOccurrenceCount)
				&& ( ((fType & MASK_C1) == C1_SOURCE) || (getSourceParent().equals(other.getSourceParent())) )
				&& (getElementName().equals(other.getElementName()) );
	}
	
}
