/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.internal.ui.viewer;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String MainTab_name;
	public static String MainTab_LoadPreset_label;
	public static String MainTab_Program_label;
	public static String MainTab_ProgramPath_label;
	public static String MainTab_ProgramPath_name;
	public static String MainTab_ProgramArgs_label;
	
	public static String ProgramArgs_error_Other_message;
	
	public static String MainTab_DDE_ViewOutput_label;
	public static String MainTab_DDE_PreProduceOutput_label;
	public static String MainTab_DDECommand_label;
	public static String MainTab_DDEApplication_label;
	public static String MainTab_DDETopic_label;
	
	public static String DDE_ViewOutput_label;
	public static String DDE_PreProduceOutput_label;
	public static String DDECommand_error_Other_message;
	public static String DDEApplication_error_Other_message;
	public static String DDETopic_error_Other_message;
	
	public static String PreProduceOutput_task; 
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
