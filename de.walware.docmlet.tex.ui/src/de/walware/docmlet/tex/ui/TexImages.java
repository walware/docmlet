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

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;

import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class TexImages {
	
	
	public static final String OBJ_PREAMBLE = TexUIPlugin.PLUGIN_ID + "/image/obj/preamble"; //$NON-NLS-1$
	public static final String OBJ_PART = TexUIPlugin.PLUGIN_ID + "/image/obj/sectioning-part"; //$NON-NLS-1$
	public static final String OBJ_CHAPTER = TexUIPlugin.PLUGIN_ID + "/image/obj/sectioning-chapter"; //$NON-NLS-1$
	public static final String OBJ_SECTION = TexUIPlugin.PLUGIN_ID + "/image/obj/sectioning-section"; //$NON-NLS-1$
	public static final String OBJ_SUBSECTION = TexUIPlugin.PLUGIN_ID + "/image/obj/sectioning-subsection"; //$NON-NLS-1$
	public static final String OBJ_SUBSUBSECTION = TexUIPlugin.PLUGIN_ID + "/image/obj/sectioning-subsubsection"; //$NON-NLS-1$
	
	public static final String OBJ_LABEL = TexUIPlugin.PLUGIN_ID + "/image/obj/label"; //$NON-NLS-1$
	
	
	private static Map<String, String> COMMAND_IMAGES;
	
	
	public static final String getCommandImageKey(final TexCommand command) {
		if (COMMAND_IMAGES == null) {
			COMMAND_IMAGES = TexUIPlugin.getDefault().getCommandImages();
		}
		return COMMAND_IMAGES.get(command.getControlWord());
	}
	
	public static final ImageRegistry getImageRegistry() {
		return TexUIPlugin.getDefault().getImageRegistry();
	}
	
}
