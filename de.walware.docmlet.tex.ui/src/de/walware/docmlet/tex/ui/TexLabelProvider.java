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

package de.walware.docmlet.tex.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.ltk.IModelElement;
import de.walware.ecommons.ltk.ui.IElementLabelProvider;

import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.model.ILtxSourceElement;
import de.walware.docmlet.tex.core.model.TexLabelAccess;


public class TexLabelProvider extends StyledCellLabelProvider implements IElementLabelProvider, ILabelProvider {
	
	
	private ImageRegistry fTexImageRegistry;
	
	
	public TexLabelProvider() {
		fTexImageRegistry = TexImages.getImageRegistry();
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		fTexImageRegistry = null;
	}
	
	
	@Override
	public Image getImage(final Object element) {
		if (element instanceof IModelElement) {
			return getImage((IModelElement) element);
		}
		if (element instanceof TexLabelAccess) {
			return getImage((TexLabelAccess) element);
		}
		return null;
	}
	
	@Override
	public Image getImage(final IModelElement element) {
		switch (element.getElementType() & IModelElement.MASK_C3) {
		case ILtxSourceElement.C2_PREAMBLE:
			return fTexImageRegistry.get(TexImages.OBJ_PREAMBLE);
		case ILtxSourceElement.C2_SECTIONING+TexCommand.PART_LEVEL:
			return fTexImageRegistry.get(TexImages.OBJ_PART);
		case ILtxSourceElement.C2_SECTIONING+TexCommand.CHAPTER_LEVEL:
			return fTexImageRegistry.get(TexImages.OBJ_CHAPTER);
		case ILtxSourceElement.C2_SECTIONING+TexCommand.SECTION_LEVEL:
			return fTexImageRegistry.get(TexImages.OBJ_SECTION);
		case ILtxSourceElement.C2_SECTIONING+TexCommand.SUBSECTION_LEVEL:
			return fTexImageRegistry.get(TexImages.OBJ_SUBSECTION);
		case ILtxSourceElement.C2_SECTIONING+TexCommand.SUBSUBSECTION_LEVEL:
			return fTexImageRegistry.get(TexImages.OBJ_SUBSUBSECTION);
		default:
			return null;
		}
	}
	
	public Image getImage(final TexLabelAccess access) {
		if (access.getType() == TexLabelAccess.LABEL) {
			return fTexImageRegistry.get(TexImages.OBJ_LABEL);
		}
		return null;
	}
	
	@Override
	public String getText(final Object element) {
		if (element instanceof IModelElement) {
			return getText((IModelElement) element);
		}
		if (element instanceof TexLabelAccess) {
			return getText((TexLabelAccess) element);
		}
		return null;
	}
	
	@Override
	public String getText(final IModelElement element) {
		return element.getElementName().getDisplayName();
	}
	
	public String getText(final TexLabelAccess access) {
		return access.getDisplayName();
	}
	
	@Override
	public StyledString getStyledText(final IModelElement element) {
		return new StyledString(element.getElementName().getDisplayName());
	}
	
	@Override
	public void update(final ViewerCell cell) {
		final Object cellElement = cell.getElement();
		if (cellElement instanceof IModelElement) {
			final IModelElement element = (IModelElement) cellElement;
			cell.setImage(getImage(element));
			final StyledString styledText = getStyledText(element);
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
