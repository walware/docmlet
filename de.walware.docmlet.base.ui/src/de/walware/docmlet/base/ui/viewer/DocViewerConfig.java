/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.viewer;


public class DocViewerConfig {
	
	
	public static final String TYPE_ID= "de.walware.docmlet.base.launchConfigurations.DocViewer"; //$NON-NLS-1$
	
	
/*[ Attributes ]===============================================================*/
	
	public static final String BASE_MAIN_ATTR_QUALIFIER= "de.walware.docmlet.base/viewer"; //$NON-NLS-1$
	
	public static final String PROGRAM_FILE_ATTR_NAME= BASE_MAIN_ATTR_QUALIFIER + '/' +
			"ProgramFile.path"; //$NON-NLS-1$
	public static final String PROGRAM_ARGUMENTS_ATTR_NAME= BASE_MAIN_ATTR_QUALIFIER + '/' +
			"ProgramArguments.string"; //$NON-NLS-1$
	
	
	public static final String DDE_COMMAND_ATTR_KEY= "DDE.Command.message"; //$NON-NLS-1$
	public static final String DDE_APPLICATION_ATTR_KEY= "DDE.Application.name"; //$NON-NLS-1$
	public static final String DDE_TOPIC_ATTR_KEY= "DDE.Topic.name"; //$NON-NLS-1$
	
	public static final String TASK_VIEW_OUTPUT_ATTR_QUALIFIER= BASE_MAIN_ATTR_QUALIFIER + '/' +
			"ViewOutput"; //$NON-NLS-1$
	public static final String TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER= BASE_MAIN_ATTR_QUALIFIER + '/' +
			"PreProduceOutput"; //$NON-NLS-1$
	
	
}
