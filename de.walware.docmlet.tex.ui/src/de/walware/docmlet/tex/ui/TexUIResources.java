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

import static de.walware.docmlet.tex.internal.ui.TexUIPlugin.PLUGIN_ID;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class TexUIResources {
	
	
	public static final String OBJ_PREAMBLE_IMAGE_ID= PLUGIN_ID + "/image/obj/preamble"; //$NON-NLS-1$
	public static final String OBJ_PART_IMAGE_ID= PLUGIN_ID + "/image/obj/sectioning-part"; //$NON-NLS-1$
	public static final String OBJ_CHAPTER_IMAGE_ID= PLUGIN_ID + "/image/obj/sectioning-chapter"; //$NON-NLS-1$
	public static final String OBJ_SECTION_IMAGE_ID= PLUGIN_ID + "/image/obj/sectioning-section"; //$NON-NLS-1$
	public static final String OBJ_SUBSECTION_IMAGE_ID= PLUGIN_ID + "/image/obj/sectioning-subsection"; //$NON-NLS-1$
	public static final String OBJ_SUBSUBSECTION_IMAGE_ID= PLUGIN_ID + "/image/obj/sectioning-subsubsection"; //$NON-NLS-1$
	
	public static final String OBJ_LABEL_IMAGE_ID= PLUGIN_ID + "/image/obj/label"; //$NON-NLS-1$
	
	
	public static final TexUIResources INSTANCE= new TexUIResources();
	
	
	private final ImageRegistry registry;
	
	private Map<String, String> commandImages;
	
	
	private TexUIResources() {
		this.registry= TexUIPlugin.getDefault().getImageRegistry();
	}
	
	public ImageDescriptor getImageDescriptor(final String id) {
		return this.registry.getDescriptor(id);
	}
	
	public Image getImage(final String id) {
		return this.registry.get(id);
	}
	
	public String getCommandImageId(final TexCommand command) {
		if (commandImages == null) {
			commandImages= TexUIPlugin.getDefault().getCommandImages();
		}
		return commandImages.get(command.getControlWord());
	}
	
}
