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

package de.walware.docmlet.wikitext.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.ltk.core.model.IEmbeddedForeignElement;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.ui.IElementLabelProvider;

import de.walware.docmlet.base.ui.DocBaseUIResources;

import de.walware.eutils.yaml.core.model.YamlModel;

import de.walware.docmlet.wikitext.core.model.IWikitextElement;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikitextLabelProvider extends StyledCellLabelProvider
		implements IElementLabelProvider, ILabelProvider {
	
	
	private DocBaseUIResources docBaseResources;
	
	
	public WikitextLabelProvider() {
		this.docBaseResources= DocBaseUIResources.INSTANCE;
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		
		this.docBaseResources= null;
	}
	
	
	@Override
	public Image getImage(final Object element) {
		if (element instanceof IModelElement) {
			return getImage((IModelElement) element);
		}
		if (element instanceof WikitextNameAccess) {
			return getImage(element);
		}
		return null;
	}
	
	@Override
	public Image getImage(final IModelElement element) {
		switch (element.getElementType() & IModelElement.MASK_C3) {
		case IWikitextElement.C2_PREAMBLE:
			return getDocBaseImage(DocBaseUIResources.OBJ_PREAMBLE_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 1:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING1_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 2:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING2_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 3:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING3_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 4:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING4_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 5:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING5_IMAGE_ID);
		case IWikitextElement.C2_SECTIONING | 6:
			return getDocBaseImage(DocBaseUIResources.OBJ_HEADING6_IMAGE_ID);
		case IWikitextElement.C1_EMBEDDED: {
			final ISourceStructElement foreignElement= ((IEmbeddedForeignElement) element).getForeignElement();
			return (foreignElement != null) ? getEmbeddedForeignImage(foreignElement) : null; }
		default:
			return null;
		}
	}
	
	protected Image getDocBaseImage(final String imageId) {
		return this.docBaseResources.getImage(imageId);
	}
	
	protected Image getEmbeddedForeignImage(final IModelElement element) {
		if (element.getModelTypeId() == YamlModel.YAML_TYPE_ID) {
			return getDocBaseImage(DocBaseUIResources.OBJ_PREAMBLE_IMAGE_ID);
		}
		return null;
	}
	
	public Image getImage(final WikitextNameAccess access) {
//		if (access.getType() == WikitextNameAccess.LABEL) {
//			return this.wikitextResources.getImage(WikitextUIResources.OBJ_LABEL_IMAGE_ID);
//		}
		return null;
	}
	
	@Override
	public String getText(final Object element) {
		if (element instanceof IModelElement) {
			return getText((IModelElement) element);
		}
		if (element instanceof WikitextNameAccess) {
			return getText(element);
		}
		return null;
	}
	
	@Override
	public String getText(final IModelElement element) {
		return element.getElementName().getDisplayName();
	}
	
	public String getText(final WikitextNameAccess access) {
		return access.getDisplayName();
	}
	
	@Override
	public StyledString getStyledText(final IModelElement element) {
		return new StyledString(element.getElementName().getDisplayName());
	}
	
	@Override
	public void update(final ViewerCell cell) {
		final Object cellElement= cell.getElement();
		if (cellElement instanceof IModelElement) {
			final IModelElement element= (IModelElement) cellElement;
			cell.setImage(getImage(element));
			final StyledString styledText= getStyledText(element);
			cell.setText(styledText.getString());
			cell.setStyleRanges(styledText.getStyleRanges());
			super.update(cell);
		}
		else {
			cell.setImage(null);
			cell.setText(cellElement.toString());
			cell.setStyleRanges(null);
			super.update(cell);
		}
	}
	
}
