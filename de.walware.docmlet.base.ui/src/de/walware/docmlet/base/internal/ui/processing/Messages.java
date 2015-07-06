/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.internal.ui.processing;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String Variable_SourceFilePath_description;
	public static String Variable_InFileResourceVars_description_Resource_term;
	public static String Variable_InFilePath_description;
	public static String Variable_OutFilePath_description;
	public static String Variable_OutFileExt_description;
	
	public static String Format_SourceDoc_label;
	public static String Format_AutoByInDocYaml_label;
	public static String Format_Other_label;
	public static String Format_Other_Info_label;
	public static String Format_Output_label;
	
	public static String MainTab_name;
	public static String MainTab_Overview_label;
	public static String MainTab_Overview_Step_header;
	public static String MainTab_Overview_Run_header;
	public static String MainTab_Overview_Detail_header;
	public static String MainTab_WorkingDir_label;
	
	public static String StepTab_Enabled_label;
	public static String StepTab_Operations_label;
	
	public static String StepTab_In_label;
	public static String StepTab_Out_label;
	public static String StepTab_Out_Format_label;
	public static String StepTab_Out_FileExt_label;
	public static String StepTab_Out_FilePath_label;
	public static String StepTab_PostActions_label;
	
	public static String Preview_label;
	public static String PreviewTab_name;
	public static String PreviewTab_Operations_label;
	
	public static String ProcessingOperation_RunLaunchConfigSettings_List_label;
	public static String ProcessingOperation_RunLaunchConfigSettings_New_label;
	public static String ProcessingOperation_RunLaunchConfigSettings_error_NoConfigSelected_message;
	public static String ProcessingOperation_RunLaunchConfigSettings_error_NewConfigFailed_message;
	public static String ProcessingOperation_RunLaunchConfig_task;
	public static String ProcessingOperation_RunLaunchConfig_ForPreview_task;
	public static String ProcessingOperation_RunLaunchConfig_Config_error_SpecMissing_message;
	public static String ProcessingOperation_RunLaunchConfig_Config_error_DefMissing_message;
	
	public static String ProcessingOperation_RunExternalProgram_label;
	public static String ProcessingOperation_RunExternalProgram_Wd_error_SpecInvalid_message;
	public static String ProcessingOperation_RunExternalProgram_Args_error_SpecInvalid_message;
	
	public static String ProcessingOperation_CheckOutput_label;
	public static String ProcessingOperation_CheckOutput_task;
	public static String ProcessingOperation_CheckOutput_error_FileNotExists_message;
	
	public static String ProcessingOperation_CloseInDocViewer_label;
	public static String ProcessingOperation_OpenUsingDocViewer_label;
	
	public static String ProcessingOperation_OpenUsingEclipse_label;
	public static String ProcessingOperation_OpenUsingEclipse_task;
	public static String ProcessingOperation_OpenUsingEclipse_error_message;
	
	public static String StepTab_OpenFile_Disabled_label;
	public static String StepTab_OpenFile_SingleStep_label;
	public static String StepTab_OpenFile_Always_label;
	
	public static String ProcessingAction_ProcessAndPreview_label;
	public static String ProcessingAction_ProcessDoc_label;
	public static String ProcessingAction_Weave_label;
	public static String ProcessingAction_ProduceOutput_label;
	public static String ProcessingAction_PreviewOutput_label;
	public static String ProcessingAction_ActivateConfig_label;
	public static String ProcessingAction_EditConfig_label;
	public static String ProcessingAction_CreateEditConfigs_label;
	
	public static String ProcessingConfig_error_NoFileSelected_message;
	public static String ProcessingConfig_error_MissingViewerConfig_message;
	public static String ProcessingConfig_InitOperation_error_Failed_message;
	public static String Processing_Launch_error_message;
	public static String ProcessingProcess_label;
	public static String ProcessingProcess_error_UnexpectedError_message;
	public static String ProcessingProcess_RunInContext_error_Failed_message;
	public static String ProcessingProcess_RunInContext_error_UnexpectedError_message;
	public static String ProcessingProcess_RunOperation_error_Failed_message;
	public static String ProcessingProcess_RunOperation_error_Cancelled_message;
	public static String ProcessingProcess_RunOperation_error_UnexpectedError_message;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
