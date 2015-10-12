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

package de.walware.docmlet.wikitext.core;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import de.walware.ecommons.preferences.AbstractPreferencesModelObject;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.Preference.BooleanPref;
import de.walware.ecommons.preferences.Preference.EnumPref;
import de.walware.ecommons.preferences.Preference.IntPref;
import de.walware.ecommons.text.IIndentSettings;


/**
 * Settings for style of R code.
 */
public class WikitextCodeStyleSettings extends AbstractPreferencesModelObject
		implements IIndentSettings {
	
	
	public static final String QUALIFIER= WikitextCore.PLUGIN_ID + "/codestyle/Wikitext"; //$NON-NLS-1$
	public static final String INDENT_GROUP_ID= "Wikitext/Wikitext.codestyle/indent"; //$NON-NLS-1$
	
	
	public static final String[] ALL_GROUP_IDS= new String[] { INDENT_GROUP_ID };
	
	
	public static final IntPref TAB_SIZE_PREF= new IntPref(
			QUALIFIER, "Tab.size"); //$NON-NLS-1$
	
	public static final EnumPref<IndentationType> INDENT_DEFAULT_TYPE_PREF= new EnumPref<>(
			QUALIFIER, "Indent.type", IndentationType.class); //$NON-NLS-1$
	
	public static final IntPref INDENT_SPACES_COUNT_PREF= new IntPref(
			QUALIFIER, "Indent.Level.spaces_count"); //$NON-NLS-1$
	
	public static final BooleanPref REPLACE_CONVERSATIVE_PREF= new BooleanPref(
			QUALIFIER, "Indent.ReplaceConservativ.enabled"); //$NON-NLS-1$
	
	public static final BooleanPref REPLACE_TABS_WITH_SPACES_PREF= new BooleanPref(
			QUALIFIER, "Indent.ReplaceOtherTabs.enabled"); //$NON-NLS-1$
	
	public static final Preference<Integer> WRAP_LINE_WIDTH_PREF= new IntPref(
			QUALIFIER, "Wrap.LineWidth.max"); //$NON-NLS-1$
	
	
	private int tabSize;
	private IndentationType indentDefaultType;
	private int indentSpacesCount;
	private boolean replaceOtherTabsWithSpaces;
	private boolean replaceConservative;
	
	private int lineWidth;
	
	
	/**
	 * Creates an instance with default settings.
	 */
	public WikitextCodeStyleSettings(final int mode) {
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
		setIndentSpacesCount(4);
		setReplaceConservative(false);
		setReplaceOtherTabsWithSpaces(false);
		setLineWidth(80);
	}
	
	@Override
	public void load(final IPreferenceAccess prefs) {
		setTabSize(prefs.getPreferenceValue(TAB_SIZE_PREF));
		setIndentDefaultType(prefs.getPreferenceValue(INDENT_DEFAULT_TYPE_PREF));
		setIndentSpacesCount(prefs.getPreferenceValue(INDENT_SPACES_COUNT_PREF));
		setReplaceConservative(prefs.getPreferenceValue(REPLACE_CONVERSATIVE_PREF));
		setReplaceOtherTabsWithSpaces(prefs.getPreferenceValue(REPLACE_TABS_WITH_SPACES_PREF));
		setLineWidth(prefs.getPreferenceValue(WRAP_LINE_WIDTH_PREF));
	}
	
	public void load(final WikitextCodeStyleSettings source) {
		final Lock writeLock= getWriteLock();
		final Lock sourceLock= source.getReadLock();
		try {
			sourceLock.lock();
			writeLock.lock();
			
			setTabSize(source.tabSize);
			setIndentDefaultType(source.indentDefaultType);
			setIndentSpacesCount(source.indentSpacesCount);
			setReplaceConservative(source.replaceConservative);
			setReplaceOtherTabsWithSpaces(source.replaceOtherTabsWithSpaces);
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
		return map;
	}
	
	
/*[ Properties ]===============================================================*/
	
	public void setTabSize(final int size) {
		final int oldValue= this.tabSize;
		this.tabSize= size;
		firePropertyChange(TAB_SIZE_PROP, oldValue, size);
	}
	@Override
	public int getTabSize() {
		return this.tabSize;
	}
	
	public void setIndentDefaultType(final IndentationType type) {
		final IndentationType oldValue= this.indentDefaultType;
		this.indentDefaultType= type;
		firePropertyChange(INDENT_DEFAULT_TYPE_PROP, oldValue, type);
	}
	@Override
	public IndentationType getIndentDefaultType() {
		return this.indentDefaultType;
	}
	
	public void setIndentSpacesCount(final int count) {
		final int oldValue= this.indentSpacesCount;
		this.indentSpacesCount= count;
		firePropertyChange(INDENT_SPACES_COUNT_PROP, oldValue, count);
	}
	@Override
	public int getIndentSpacesCount() {
		return this.indentSpacesCount;
	}
	
	public void setReplaceConservative(final boolean enable) {
		final boolean oldValue= this.replaceConservative;
		this.replaceConservative= enable;
		firePropertyChange(REPLACE_CONSERVATIVE_PROP, oldValue, enable);
	}
	@Override
	public boolean getReplaceConservative() {
		return this.replaceConservative;
	}
	
	public void setReplaceOtherTabsWithSpaces(final boolean enable) {
		final boolean oldValue= this.replaceOtherTabsWithSpaces;
		this.replaceOtherTabsWithSpaces= enable;
		firePropertyChange(REPLACE_TABS_WITH_SPACES_PROP, oldValue, getReplaceOtherTabsWithSpaces());
	}
	@Override
	public boolean getReplaceOtherTabsWithSpaces() {
		return this.replaceOtherTabsWithSpaces;
	}
	
	public void setLineWidth(final int max) {
		final boolean oldValue= this.replaceOtherTabsWithSpaces;
		this.lineWidth= max;
		firePropertyChange(WRAP_LINE_WIDTH_PROP, oldValue, max);
	}
	@Override
	public int getLineWidth() {
		return this.lineWidth;
	}
	
}
