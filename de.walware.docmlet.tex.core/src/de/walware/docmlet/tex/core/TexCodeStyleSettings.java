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

package de.walware.docmlet.tex.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import de.walware.ecommons.preferences.AbstractPreferencesModelObject;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.Preference.BooleanPref;
import de.walware.ecommons.preferences.Preference.EnumPref;
import de.walware.ecommons.preferences.Preference.IntPref;
import de.walware.ecommons.preferences.Preference.StringSetPref;
import de.walware.ecommons.text.IIndentSettings;


/**
 * Settings for style of R code.
 */
public class TexCodeStyleSettings extends AbstractPreferencesModelObject
		implements IIndentSettings {
	
	
	public static final String QUALIFIER = TexCore.PLUGIN_ID + "/codestyle/tex"; //$NON-NLS-1$
	public static final String INDENT_GROUP_ID = "tex/tex.codestyle/indent"; //$NON-NLS-1$
	
	
	public static final String[] ALL_GROUP_IDS = new String[] { INDENT_GROUP_ID };
	
	
	public static final IntPref TAB_SIZE_PREF = new IntPref(
			QUALIFIER, "Tab.size"); //$NON-NLS-1$
	
	public static final EnumPref<IndentationType> INDENT_DEFAULT_TYPE_PREF = new EnumPref<>(
			QUALIFIER, "Indent.type", IndentationType.class); //$NON-NLS-1$
	
	public static final IntPref INDENT_SPACES_COUNT_PREF = new IntPref(
			QUALIFIER, "Indent.Level.spaces_count"); //$NON-NLS-1$
	
	public static final BooleanPref REPLACE_CONVERSATIVE_PREF = new BooleanPref(
			QUALIFIER, "Indent.ReplaceConservativ.enabled"); //$NON-NLS-1$
	
	public static final BooleanPref REPLACE_TABS_WITH_SPACES_PREF = new BooleanPref(
			QUALIFIER, "Indent.ReplaceOtherTabs.enabled"); //$NON-NLS-1$
	
	public static final Preference<Integer> WRAP_LINE_WIDTH_PREF = new IntPref(
			QUALIFIER, "Wrap.LineWidth.max"); //$NON-NLS-1$
	
	
	public static final IntPref INDENT_BLOCK_DEPTH_PREF = new IntPref(
			QUALIFIER, "IndentBlockDepth.level"); //$NON-NLS-1$
	public static final String INDENT_BLOCK_DEPTH_PROP = "indentBlockDepth"; //$NON-NLS-1$
	
	public static final IntPref INDENT_ENV_DEPTH_PREF = new IntPref(
			QUALIFIER, "IndentEnvDepth.level"); //$NON-NLS-1$
	public static final String INDENT_ENV_DEPTH_PROP = "indentEnvDepth"; //$NON-NLS-1$
	
	public static final Preference<Set<String>> INDENT_ENV_LABELS_PREF = new StringSetPref(
			QUALIFIER, "IndentEnvFor.labels"); //$NON-NLS-1$
	public static final String INDENT_ENV_LABELS_PROP = "indentEnvLabels"; //$NON-NLS-1$
	
	
	private int fTabSize;
	private IndentationType fIndentDefaultType;
	private int fIndentSpacesCount;
	private boolean fReplaceOtherTabsWithSpaces;
	private boolean fReplaceConservative;
	
	private int fLineWidth;
	
	private int fIndentBlockDepth;
	private int fIndentEnvDepth;
	private Set<String> fIndentEnvLabels;
	
	
	/**
	 * Creates an instance with default settings.
	 */
	public TexCodeStyleSettings(final int mode) {
		if (mode >= 1) {
			installLock();
		}
		loadDefaults();
		resetDirty();
	}
	
	
	@Override
	public String[] getNodeQualifiers() {
		return new String[] { QUALIFIER };
	}
	
	@Override
	public void loadDefaults() {
		setTabSize(4);
		setIndentDefaultType(IndentationType.SPACES);
		setIndentSpacesCount(2);
		setReplaceConservative(false);
		setReplaceOtherTabsWithSpaces(false);
		setLineWidth(80);
		setIndentBlockDepth(1);
		setIndentEnvDepth(1);
		setIndentEnvLabels(new HashSet<>(Arrays.asList(new String[] {
				"itemize", "enumerate", "description", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		})));
	}
	
	@Override
	public void load(final IPreferenceAccess prefs) {
		setTabSize(prefs.getPreferenceValue(TAB_SIZE_PREF));
		setIndentDefaultType(prefs.getPreferenceValue(INDENT_DEFAULT_TYPE_PREF));
		setIndentSpacesCount(prefs.getPreferenceValue(INDENT_SPACES_COUNT_PREF));
		setReplaceConservative(prefs.getPreferenceValue(REPLACE_CONVERSATIVE_PREF));
		setReplaceOtherTabsWithSpaces(prefs.getPreferenceValue(REPLACE_TABS_WITH_SPACES_PREF));
		setLineWidth(prefs.getPreferenceValue(WRAP_LINE_WIDTH_PREF));
		setIndentBlockDepth(prefs.getPreferenceValue(INDENT_BLOCK_DEPTH_PREF));
		setIndentEnvDepth(prefs.getPreferenceValue(INDENT_ENV_DEPTH_PREF));
		setIndentEnvLabels(prefs.getPreferenceValue(INDENT_ENV_LABELS_PREF));
	}
	
	public void load(final TexCodeStyleSettings source) {
		final Lock writeLock = getWriteLock();
		final Lock sourceLock = source.getReadLock();
		try {
			sourceLock.lock();
			writeLock.lock();
			
			setTabSize(source.fTabSize);
			setIndentDefaultType(source.fIndentDefaultType);
			setIndentSpacesCount(source.fIndentSpacesCount);
			setReplaceConservative(source.fReplaceConservative);
			setReplaceOtherTabsWithSpaces(source.fReplaceOtherTabsWithSpaces);
			setIndentBlockDepth(source.fIndentBlockDepth);
			setIndentEnvDepth(source.fIndentEnvDepth);
			setIndentEnvLabels(source.fIndentEnvLabels);
		}
		finally {
			sourceLock.unlock();
			writeLock.unlock();
		}
	}
	
	@Override
	public Map<Preference<?>, Object> deliverToPreferencesMap(final Map<Preference<?>, Object> map) {
		map.put(TAB_SIZE_PREF, getTabSize());
		map.put(INDENT_DEFAULT_TYPE_PREF, getIndentDefaultType());
		map.put(INDENT_SPACES_COUNT_PREF, getIndentSpacesCount());
		map.put(REPLACE_CONVERSATIVE_PREF, getReplaceConservative());
		map.put(REPLACE_TABS_WITH_SPACES_PREF, getReplaceOtherTabsWithSpaces());
		map.put(WRAP_LINE_WIDTH_PREF, getLineWidth());
		map.put(INDENT_BLOCK_DEPTH_PREF, getIndentBlockDepth());
		map.put(INDENT_ENV_DEPTH_PREF, getIndentEnvDepth());
		map.put(INDENT_ENV_LABELS_PREF, getIndentEnvLabels());
		return map;
	}
	
	
/*-- Properties --------------------------------------------------------------*/
	
	public void setTabSize(final int size) {
		final int oldValue = fTabSize;
		fTabSize = size;
		firePropertyChange(TAB_SIZE_PROP, oldValue, size);
	}
	@Override
	public int getTabSize() {
		return fTabSize;
	}
	
	public void setIndentDefaultType(final IndentationType type) {
		final IndentationType oldValue = fIndentDefaultType;
		fIndentDefaultType = type;
		firePropertyChange(INDENT_DEFAULT_TYPE_PROP, oldValue, type);
	}
	@Override
	public IndentationType getIndentDefaultType() {
		return fIndentDefaultType;
	}
	
	public void setIndentSpacesCount(final int count) {
		final int oldValue = fIndentSpacesCount;
		fIndentSpacesCount = count;
		firePropertyChange(INDENT_SPACES_COUNT_PROP, oldValue, count);
	}
	@Override
	public int getIndentSpacesCount() {
		return fIndentSpacesCount;
	}
	
	public void setReplaceConservative(final boolean enable) {
		final boolean oldValue = fReplaceConservative;
		fReplaceConservative = enable;
		firePropertyChange(REPLACE_CONSERVATIVE_PROP, oldValue, enable);
	}
	@Override
	public boolean getReplaceConservative() {
		return fReplaceConservative;
	}
	
	public void setReplaceOtherTabsWithSpaces(final boolean enable) {
		final boolean oldValue = fReplaceOtherTabsWithSpaces;
		fReplaceOtherTabsWithSpaces = enable;
		firePropertyChange(REPLACE_TABS_WITH_SPACES_PROP, oldValue, getReplaceOtherTabsWithSpaces());
	}
	@Override
	public boolean getReplaceOtherTabsWithSpaces() {
		return fReplaceOtherTabsWithSpaces;
	}
	
	public void setLineWidth(final int max) {
		final boolean oldValue = fReplaceOtherTabsWithSpaces;
		fLineWidth = max;
		firePropertyChange(WRAP_LINE_WIDTH_PROP, oldValue, max);
	}
	@Override
	public int getLineWidth() {
		return fLineWidth;
	}
	
	
	public final void setIndentBlockDepth(final int depth) {
		final int oldValue = fIndentBlockDepth;
		fIndentBlockDepth = depth;
		firePropertyChange(INDENT_BLOCK_DEPTH_PROP, oldValue, depth);
	}
	public final int getIndentBlockDepth() {
		return fIndentBlockDepth;
	}
	
	public final void setIndentEnvDepth(final int depth) {
		final int oldValue = fIndentEnvDepth;
		fIndentEnvDepth = depth;
		firePropertyChange(INDENT_ENV_DEPTH_PROP, oldValue, depth);
	}
	public final int getIndentEnvDepth() {
		return fIndentEnvDepth;
	}
	
	public void setIndentEnvLabels(final Set<String> labels) {
		fIndentEnvLabels = labels;
	}
	public Set<String> getIndentEnvLabels() {
		return fIndentEnvLabels;
	}
	
}
