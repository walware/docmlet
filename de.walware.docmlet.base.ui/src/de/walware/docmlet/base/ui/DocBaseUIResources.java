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

package de.walware.docmlet.base.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;


public class DocBaseUIResources {
	
	
	private static final String NS= "de.walware.docmlet.base"; //$NON-NLS-1$
	
	
	public static final String OBJ_PREAMBLE_IMAGE_ID= NS + "/image/obj/Preamble"; //$NON-NLS-1$
	
	public static final String OBJ_HEADING1_IMAGE_ID= NS + "/image/obj/Sectioning-H1"; //$NON-NLS-1$
	public static final String OBJ_HEADING2_IMAGE_ID= NS + "/image/obj/Sectioning-H2"; //$NON-NLS-1$
	public static final String OBJ_HEADING3_IMAGE_ID= NS + "/image/obj/Sectioning-H3"; //$NON-NLS-1$
	public static final String OBJ_HEADING4_IMAGE_ID= NS + "/image/obj/Sectioning-H4"; //$NON-NLS-1$
	public static final String OBJ_HEADING5_IMAGE_ID= NS + "/image/obj/Sectioning-H5"; //$NON-NLS-1$
	public static final String OBJ_HEADING6_IMAGE_ID= NS + "/image/obj/Sectioning-H6"; //$NON-NLS-1$
	
	public static final String VIEW_MARKUP_HELP_IMAGE_ID= NS + "/image/view/MarkupHelp"; //$NON-NLS-1$
	
	public static final String TOOL_PROCESS_IMAGE_ID= NS + "/image/tool/Process"; //$NON-NLS-1$
	public static final String TOOL_PROCESSANDPREVIEW_IMAGE_ID= NS + "/image/tool/ProcessAndPreview"; //$NON-NLS-1$
	public static final String TOOL_PREVIEW_IMAGE_ID= NS + "/image/tool/Preview"; //$NON-NLS-1$
	
	
	public static final DocBaseUIResources INSTANCE= new DocBaseUIResources();
	
	
	private final ImageRegistry registry;
	
	
	private DocBaseUIResources() {
		this.registry= DocBaseUIPlugin.getInstance().getImageRegistry();
	}
	
	public ImageDescriptor getImageDescriptor(final String id) {
		return this.registry.getDescriptor(id);
	}
	
	public Image getImage(final String id) {
		return this.registry.get(id);
	}
	
}
