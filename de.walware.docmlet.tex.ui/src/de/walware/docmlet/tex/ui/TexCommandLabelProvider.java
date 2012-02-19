/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.walware.docmlet.tex.core.commands.LtxCommandCategories;
import de.walware.docmlet.tex.core.commands.TexCommand;


public class TexCommandLabelProvider extends LabelProvider {
	
	
	@Override
	public Image getImage(final Object element) {
		if (element instanceof TexCommand) {
			final String key = TexImages.getCommandImageKey((TexCommand) element);
			return (key != null) ? TexImages.getImageRegistry().get(key) : null;
		}
		return null;
	}
	
	@Override
	public String getText(final Object element) {
		if (element instanceof LtxCommandCategories.Category) {
			return ((LtxCommandCategories.Category) element).getLabel();
		}
		if (element instanceof TexCommand) {
			final TexCommand command = (TexCommand) element;
			return command.getControlWord() + " – " + command.getDescription();
		}
		return super.getText(element);
	}
	
}
