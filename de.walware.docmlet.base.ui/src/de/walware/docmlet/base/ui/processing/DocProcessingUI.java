/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import org.eclipse.core.runtime.content.IContentType;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImIdentitySet;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.internal.ui.processing.DocProcessingRegistry;


public class DocProcessingUI {
	
	
	public static final String PROCESS_DOC_DEFAULT_COMMAND_ID= "de.walware.docmlet.base.commands.ProcessDocDefault"; //$NON-NLS-1$
	
	public static final String PREVIEW_DOC_DEFAULT_COMMAND_ID= "de.walware.docmlet.base.commands.PreviewDocDefault"; //$NON-NLS-1$
	
	public static final String PROCESS_AND_PREVIEW_DOC_DEFAULT_COMMAND_ID= "de.walware.docmlet.base.commands.ProcessAndPreviewDefault"; //$NON-NLS-1$
	
	
	public static final String WEAVE_STEP=                  "weave"; //$NON-NLS-1$
	
	public static final String PRODUCE_OUTPUT_STEP=         "produce_output"; //$NON-NLS-1$
	
	public static final String PREVIEW_OUTPUT_STEP=         "open_output"; //$NON-NLS-1$
	
	
	public static final String PROCESSING_STEPS_FLAG=       "processing_steps"; //$NON-NLS-1$
	
	
	public static class CommonFlags {
		
		public static final ImIdentitySet<String> PROCESS_AND_PREVIEW= ImCollections.newIdentitySet();
		public static final ImIdentitySet<String> PROCESS= ImCollections.newIdentitySet(PROCESSING_STEPS_FLAG);
		
		public static final ImIdentitySet<String> WEAVE= ImCollections.newIdentitySet(WEAVE_STEP);
		public static final ImIdentitySet<String> PRODUCE_OUTPUT= ImCollections.newIdentitySet(PRODUCE_OUTPUT_STEP);
		public static final ImIdentitySet<String> OPEN_OUTPUT= ImCollections.newIdentitySet(PREVIEW_OUTPUT_STEP);
		
	}
	
	
	public static final String TOGGLE_RUN_ON_SAVE_COMMAND_ID= "de.walware.docmlet.base.commands.ToggleRunDocProcessingOnSave"; //$NON-NLS-1$
	
	
/*[ DocProcessingToolConfig Attributes ]========================================================*/
	
	public static final String BASE_RUN_ATTR_QUALIFIER= "de.walware.docmlet.base/run"; //$NON-NLS-1$
	
	public static final String CONTENT_TYPE_ID_ATTR_NAME= BASE_RUN_ATTR_QUALIFIER + '/' + "ContentType.id"; //$NON-NLS-1$
	
	public static final String RUN_STEPS_ATTR_NAME= BASE_RUN_ATTR_QUALIFIER + '/' + "BuildSteps.set"; //$NON-NLS-1$
	
	public static final String TARGET_PATH_ATTR_NAME= BASE_RUN_ATTR_QUALIFIER + '/' + "Target.path"; //$NON-NLS-1$
	
	
/*[ Managers ]=================================================================*/
	
	public static DocProcessingManager getDocProcessingManager(final String contentTypeId) {
		final DocProcessingRegistry registry= DocBaseUIPlugin.getInstance().getDocProcessingRegistry();
		return registry.getDocProcessingManager(contentTypeId);
	}
	
	public static DocProcessingManager getDocProcessingManager(IContentType contentType,
			final boolean inherit) {
		final DocProcessingRegistry registry= DocBaseUIPlugin.getInstance().getDocProcessingRegistry();
		if (!inherit) {
			return registry.getDocProcessingManager(contentType.getId());
		}
		do {
			final DocProcessingManager manager= registry.getDocProcessingManager(contentType.getId());
			if (manager != null) {
				return manager;
			}
			contentType= contentType.getBaseType();
		}
		while (contentType != null);
		return null;
	}
	
	
/*[ Help Contexts ]============================================================*/
	
	public static final String ACTIONS_RUN_CONFIG_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsRunConfig"; //$NON-NLS-1$
	public static final String ACTIONS_RUN_CONFIG_PROCESS_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsRunConfigProcess"; //$NON-NLS-1$
	public static final String ACTIONS_RUN_CONFIG_STEP_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsRunConfigStep"; //$NON-NLS-1$
	public static final String ACTIONS_RUN_CONFIG_PREVIEW_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsRunConfigPreview"; //$NON-NLS-1$
	public static final String ACTIONS_ACTIVATE_CONFIG_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsActivateConfig"; //$NON-NLS-1$
	public static final String ACTIONS_EDIT_CONFIG_HELP_CONTEXT_ID= "de.walware.docmlet.doc.user.DocProcessingActionsEditConfig"; //$NON-NLS-1$
	
}
