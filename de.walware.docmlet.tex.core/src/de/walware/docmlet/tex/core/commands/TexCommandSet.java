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

package de.walware.docmlet.tex.core.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.walware.ecommons.collections.ConstList;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.Preference.StringSetPref;

import de.walware.docmlet.tex.core.TexCore;


public class TexCommandSet {
	
	
	public static final String QUALIFIER = TexCore.PLUGIN_ID + "/tex.commands"; //$NON-NLS-1$
	
	public static final Preference<Set<String>> MASTER_COMMANDS_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.CommandsMaster.include"); //$NON-NLS-1$
	public static final Preference<Set<String>> PREAMBLE_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.PreambleCommands.include"); //$NON-NLS-1$
	public static final Preference<Set<String>> TEXT_COMMANDS_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.TextCommands.include"); //$NON-NLS-1$
	public static final Preference<Set<String>> MATH_COMMANDS_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.MathCommands.include"); //$NON-NLS-1$
	
	public static final Preference<Set<String>> TEXT_ENVS_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.TextEnvs.include"); //$NON-NLS-1$
	public static final Preference<Set<String>> MATH_ENVS_INCLUDE_PREF = new StringSetPref(
			QUALIFIER, "LtxCommands.MathEnvs.include"); //$NON-NLS-1$
	
	public static final String GROUP_ID = "ltx/ltx.commands"; //$NON-NLS-1$
	
	
	private List<TexCommand> fAllCommands;
	
	private List<TexCommand> fAllEnvs;
	
	private Map<String, TexCommand> fTextEnvMap;
	private List<TexCommand> fTextEnvListASorted;
	
	private Map<String, TexCommand> fTextCommandMap;
	private List<TexCommand> fTextCommandListASorted;
	
	private Map<String, TexCommand> fPreambleCommandMap;
	private List<TexCommand> fPreambleCommandListASorted;
	
	private Map<String, TexCommand> fMathEnvMap;
	private List<TexCommand> fMathEnvListASorted;
	
	private Map<String, TexCommand> fMathCommandMap;
	private List<TexCommand> fMathCommandListASorted;
	
	
	public TexCommandSet(final IPreferenceAccess prefs) {
		final Set<String> master = prefs.getPreferenceValue(MASTER_COMMANDS_INCLUDE_PREF);
		final Set<String> preamble = prefs.getPreferenceValue(PREAMBLE_INCLUDE_PREF);
		final Set<String> text = prefs.getPreferenceValue(TEXT_COMMANDS_INCLUDE_PREF);
		final Set<String> textEnvs = prefs.getPreferenceValue(TEXT_ENVS_INCLUDE_PREF);
		final Set<String> math = prefs.getPreferenceValue(MATH_COMMANDS_INCLUDE_PREF);
		final Set<String> mathEnvs = prefs.getPreferenceValue(MATH_ENVS_INCLUDE_PREF);
		
		init(master, preamble, text, textEnvs, math, mathEnvs);
	}
	
	
	private void init(final Set<String> master, final Set<String> preamble,
			final Set<String> textCommands, final Set<String> textEnvs,
			final Set<String> mathCommands, final Set<String> mathEnvs) {
		final TexCommand[] sortedCommands;
		final TexCommand[] sortedEnvs;
		{	final TexCommand[] filteredCommands = new TexCommand[master.size()];
			final List<TexCommand> allCommands = LtxCommandDefinitions.getAllCommands(); // defined order
			int j = 0;
			for (int i = 0; i < allCommands.size(); i++) {
				final TexCommand command = allCommands.get(i);
				if (master.contains(command.getControlWord())) {
					filteredCommands[j++] = command;
				}
			}
			fAllCommands = new ConstList<TexCommand>((j == filteredCommands.length) ?
					filteredCommands : Arrays.copyOfRange(filteredCommands, 0, j) );
			
			sortedCommands = Arrays.copyOfRange(filteredCommands, 0, j);
			Arrays.sort(sortedCommands);
		}
		{	final List<TexCommand> allEnvs = LtxCommandDefinitions.getAllEnvs();
			fAllEnvs = allEnvs;
			sortedEnvs = allEnvs.toArray(new TexCommand[allEnvs.size()]);
			Arrays.sort(sortedEnvs);
		}
		{	final TexCommand[] commands = new TexCommand[textCommands.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<String, TexCommand>(textCommands.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (textCommands.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fTextCommandMap = Collections.unmodifiableMap(set);
			fTextCommandListASorted = new ConstList<TexCommand>((j == commands.length) ?
					commands : Arrays.copyOfRange(commands, 0, j) );
		}
		{	final TexCommand[] commands = new TexCommand[textEnvs.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<String, TexCommand>(textEnvs.size());
			int j = 0;
			for (int i = 0; i < sortedEnvs.length; i++) {
				final TexCommand command = sortedEnvs[i];
				if (textEnvs.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fTextEnvMap = Collections.unmodifiableMap(set);
			fTextEnvListASorted = new ConstList<TexCommand>((j == commands.length) ?
					commands : Arrays.copyOfRange(commands, 0, j) );
		}
		{	final TexCommand[] commands = new TexCommand[preamble.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<String, TexCommand>(preamble.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (preamble.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fPreambleCommandMap = Collections.unmodifiableMap(set);
			fPreambleCommandListASorted = new ConstList<TexCommand>((j == commands.length) ?
					commands : Arrays.copyOfRange(commands, 0, j) );
		}
		{	final TexCommand[] commands = new TexCommand[mathCommands.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<String, TexCommand>(mathCommands.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (mathCommands.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fMathCommandMap = Collections.unmodifiableMap(set);
			fMathCommandListASorted = new ConstList<TexCommand>((j == commands.length) ?
					commands : Arrays.copyOfRange(commands, 0, j) );
		}
		{	final TexCommand[] commands = new TexCommand[mathEnvs.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<String, TexCommand>(mathEnvs.size());
			int j = 0;
			for (int i = 0; i < sortedEnvs.length; i++) {
				final TexCommand command = sortedEnvs[i];
				if (mathEnvs.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fMathEnvMap = Collections.unmodifiableMap(set);
			fMathEnvListASorted = new ConstList<TexCommand>((j == commands.length) ?
					commands : Arrays.copyOfRange(commands, 0, j) );
		}
	}
	
	
	public List<TexCommand> getAllLtxCommands() {
		return fAllCommands;
	}
	
	public List<TexCommand> getAllLtxEnvs() {
		return fAllEnvs;
	}
	
	public Map<String, TexCommand> getLtxTextEnvMap() {
		return fTextEnvMap;
	}
	
	public List<TexCommand> getLtxTextEnvsASorted() {
		return fTextEnvListASorted;
	}
	
	public Map<String, TexCommand> getLtxTextCommandMap() {
		return fTextCommandMap;
	}
	
	public List<TexCommand> getLtxTextCommandsASorted() {
		return fTextCommandListASorted;
	}
	
	public Map<String, TexCommand> getLtxPreambleCommandMap() {
		return fPreambleCommandMap;
	}
	
	public List<TexCommand> getLtxPreambleCommandsASorted() {
		return fPreambleCommandListASorted;
	}
	
	public Map<String, TexCommand> getLtxMathEnvMap() {
		return fMathEnvMap;
	}
	
	public List<TexCommand> getLtxMathEnvsASorted() {
		return fMathEnvListASorted;
	}
	
	public Map<String, TexCommand> getLtxMathCommandMap() {
		return fMathCommandMap;
	}
	
	public List<TexCommand> getLtxMathCommandsASorted() {
		return fMathCommandListASorted;
	}
	
}
