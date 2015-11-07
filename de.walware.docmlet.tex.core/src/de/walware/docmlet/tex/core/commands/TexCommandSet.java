/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import static de.walware.docmlet.tex.core.commands.LtxCommandDefinitions.add;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.core.Preference.StringSetPref;

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
	
	private static final Map<String, TexCommand> LTX_INTERN_ENVS = new IdentityHashMap<>();
	
	static {
		add(LTX_INTERN_ENVS, IEnvDefinitions.ENV_Sinput_BEGIN);
		add(LTX_INTERN_ENVS, IEnvDefinitions.ENV_Souput_BEGIN);
	}
	
	
	private final List<TexCommand> fAllCommands;
	
	private final List<TexCommand> fAllEnvs;
	
	private final Map<String, TexCommand> fTextEnvMap;
	private final List<TexCommand> fTextEnvListASorted;
	
	private final Map<String, TexCommand> fTextCommandMap;
	private final List<TexCommand> fTextCommandListASorted;
	
	private final Map<String, TexCommand> fPreambleCommandMap;
	private final List<TexCommand> fPreambleCommandListASorted;
	
	private final Map<String, TexCommand> fMathEnvMap;
	private final List<TexCommand> fMathEnvListASorted;
	
	private final Map<String, TexCommand> fMathCommandMap;
	private final List<TexCommand> fMathCommandListASorted;
	
	private final Map<String, TexCommand> fInternEnvMap;
	
	
	public TexCommandSet(final IPreferenceAccess prefs) {
		final Set<String> master = prefs.getPreferenceValue(MASTER_COMMANDS_INCLUDE_PREF);
		final Set<String> preamble = prefs.getPreferenceValue(PREAMBLE_INCLUDE_PREF);
		final Set<String> textCommands = prefs.getPreferenceValue(TEXT_COMMANDS_INCLUDE_PREF);
		final Set<String> textEnvs = prefs.getPreferenceValue(TEXT_ENVS_INCLUDE_PREF);
		final Set<String> mathCommands = prefs.getPreferenceValue(MATH_COMMANDS_INCLUDE_PREF);
		final Set<String> mathEnvs = prefs.getPreferenceValue(MATH_ENVS_INCLUDE_PREF);
		
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
			fAllCommands= ImCollections.newList(filteredCommands, 0, j);
			
			sortedCommands = Arrays.copyOfRange(filteredCommands, 0, j);
			Arrays.sort(sortedCommands);
		}
		{	final List<TexCommand> allEnvs = LtxCommandDefinitions.getAllEnvs();
			fAllEnvs = allEnvs;
			sortedEnvs = allEnvs.toArray(new TexCommand[allEnvs.size()]);
			Arrays.sort(sortedEnvs);
		}
		{	final TexCommand[] commands = new TexCommand[textCommands.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<>(textCommands.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (textCommands.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fTextCommandMap = Collections.unmodifiableMap(set);
			fTextCommandListASorted= ImCollections.newList(commands, 0, j);
		}
		{	final TexCommand[] commands = new TexCommand[textEnvs.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<>(textEnvs.size());
			int j = 0;
			for (int i = 0; i < sortedEnvs.length; i++) {
				final TexCommand command = sortedEnvs[i];
				if (textEnvs.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fTextEnvMap = Collections.unmodifiableMap(set);
			fTextEnvListASorted= ImCollections.newList(commands, 0, j);
		}
		{	final TexCommand[] commands = new TexCommand[preamble.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<>(preamble.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (preamble.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fPreambleCommandMap = Collections.unmodifiableMap(set);
			fPreambleCommandListASorted= ImCollections.newList(commands, 0, j);
		}
		{	final TexCommand[] commands = new TexCommand[mathCommands.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<>(mathCommands.size());
			int j = 0;
			for (int i = 0; i < sortedCommands.length; i++) {
				final TexCommand command = sortedCommands[i];
				if (mathCommands.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fMathCommandMap = Collections.unmodifiableMap(set);
			fMathCommandListASorted= ImCollections.newList(commands, 0, j);
		}
		{	final TexCommand[] commands = new TexCommand[mathEnvs.size()];
			final Map<String, TexCommand> set = new IdentityHashMap<>(mathEnvs.size());
			int j = 0;
			for (int i = 0; i < sortedEnvs.length; i++) {
				final TexCommand command = sortedEnvs[i];
				if (mathEnvs.contains(command.getControlWord())) {
					commands[j++] = command;
					set.put(command.getControlWord(), command);
				}
			}
			fMathEnvMap = Collections.unmodifiableMap(set);
			fMathEnvListASorted= ImCollections.newList(commands, 0, j);
		}
		
		fInternEnvMap = LTX_INTERN_ENVS;
	}
	
	public TexCommandSet(final List<TexCommand> allCommands, final List<TexCommand> allEnvs,
			final Map<String, TexCommand> textEnvMap, final List<TexCommand> textEnvList,
			final Map<String, TexCommand> textCommandMap, final List<TexCommand> textCommandList,
			final Map<String, TexCommand> preambleCommandMap, final List<TexCommand> preambleCommandList,
			final Map<String, TexCommand> mathEnvMap, final List<TexCommand> mathEnvList,
			final Map<String, TexCommand> mathCommandMap, final List<TexCommand> mathCommandList,
			final Map<String, TexCommand> internEnvMap) {
		fAllCommands = allCommands;
		fAllEnvs = allEnvs;
		fTextEnvMap = textEnvMap;
		fTextEnvListASorted = textEnvList;
		fTextCommandMap = textCommandMap;
		fTextCommandListASorted = textCommandList;
		fPreambleCommandMap = preambleCommandMap;
		fPreambleCommandListASorted = preambleCommandList;
		fMathEnvMap = mathEnvMap;
		fMathEnvListASorted = mathEnvList;
		fMathCommandMap = mathCommandMap;
		fMathCommandListASorted = mathCommandList;
		fInternEnvMap = internEnvMap;
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
	
	public Map<String, TexCommand> getLtxInternEnvMap() {
		return fInternEnvMap;
	}
	
}
