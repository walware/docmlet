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

package de.walware.docmlet.base.internal.ui.viewer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import de.walware.ecommons.debug.ui.config.LaunchConfigPresets;

import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class DocViewerConfigTabGroup extends AbstractLaunchConfigurationTabGroup {
	
	
	private static final LaunchConfigPresets PRESETS;
	static {
		final LaunchConfigPresets presets= new LaunchConfigPresets(
				DocViewerConfig.TYPE_ID );
		
		final boolean win32= Platform.getOS().equals(Platform.OS_WIN32);
		
		if (win32) {
			final ILaunchConfigurationWorkingCopy config= presets.add("Acrobat Reader DC");
			config.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					"${env_var:PROGRAMFILES(X86)}\\Adobe\\Acrobat Reader DC\\Reader\\AcroRd32.exe" );
			config.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, "");
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocOpen(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewR15" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocClose(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewR15" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
		}
		if (win32) {
			final ILaunchConfigurationWorkingCopy config= presets.add("Acrobat Reader 11");
			config.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					"${env_var:PROGRAMFILES(X86)}\\Adobe\\Reader 11.0\\Reader\\AcroRd32.exe" );
			config.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, "");
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocOpen(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewR11" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocClose(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewR11" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
		}
		if (win32) {
			final ILaunchConfigurationWorkingCopy config= presets.add("Acrobat DC");
			config.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					"${env_var:PROGRAMFILES(X86)}\\Adobe\\Acrobat DC\\Acrobat\\Acrobat.exe" );
			config.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, "");
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocOpen(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewA15" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocClose(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroViewA15" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
		}
		if (win32) {
			final ILaunchConfigurationWorkingCopy config= presets.add("Acrobat 9");
			config.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					"${env_var:PROGRAMFILES(X86)}\\Adobe\\Acrobat 9\\Acrobat\\Acrobat.exe" );
			config.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, "");
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocOpen(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroView" );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					"[DocClose(\"${resource_loc}\")]" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					"AcroView" );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					"Control" );
		}
		if (win32) {
			final ILaunchConfigurationWorkingCopy config= presets.add("Yap (MiKTeX)");
			config.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					"${env_var:MIKTEX_HOME}\\miktex\\bin\\yap.exe" );
			config.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, "-1 \"${resource_loc}\"");
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
			config.setAttribute(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
			config.setAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
					LaunchConfigPresets.UNDEFINED_VALUE );
		}
		
		PRESETS= presets;
	}
	
	
	public DocViewerConfigTabGroup() {
	}
	
	
	@Override
	public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
		final DocViewerMainTab mainTab= new DocViewerMainTab(PRESETS);
		final EnvironmentTab envTab= new EnvironmentTab();
		
		final ILaunchConfigurationTab[] tabs= new ILaunchConfigurationTab[] {
				mainTab,
				envTab,
				new CommonTab() {
					@Override
					public void setDefaults(final ILaunchConfigurationWorkingCopy config) {
						super.setDefaults(config);
						config.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, false);
					};
				}
		};
		
		setTabs(tabs);
	}
	
}
