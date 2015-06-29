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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;


public class DocProcessingConfigPresets {
	
	
	private static final String NAME_ATTR_NAME= "Preset.name"; //$NON-NLS-1$
	private static final String STEPS_ATTR_NAME= "Preset.steps"; //$NON-NLS-1$
	
	static boolean isInternalArgument(final String name) {
		return (name == NAME_ATTR_NAME || name == STEPS_ATTR_NAME);
	}
	
	static String getName(final ILaunchConfiguration preset) {
		try {
			return preset.getAttribute(NAME_ATTR_NAME, (String) null);
		}
		catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	static BitSet getSteps(final ILaunchConfiguration preset) {
		try {
			final List<String> steps= preset.getAttribute(STEPS_ATTR_NAME, (List<String>) null);
			final BitSet bitSet= new BitSet();
			for (final String step : steps) {
				final int num= Integer.parseInt(step);
				bitSet.set(num);
			}
			return bitSet;
		}
		catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private final ILaunchConfigurationType type;
	
	private final List<ILaunchConfiguration> presets= new ArrayList<>();
	
	
	public DocProcessingConfigPresets(final String typeId) {
		this.type= DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(typeId);
	}
	
	
	public ILaunchConfigurationWorkingCopy add(final String name, final int... steps) {
		if (name == null) {
			throw new NullPointerException("name"); //$NON-NLS-1$
		}
		try {
			final ILaunchConfigurationWorkingCopy config= this.type.newInstance(null, "template").getWorkingCopy(); //$NON-NLS-1$
			config.setAttribute(NAME_ATTR_NAME, name);
			
			final String[] stepStrings= new String[steps.length];
			for (int i= 0; i < steps.length; i++) {
				stepStrings[i]= Integer.toString(steps[i]);
			}
			config.setAttribute(STEPS_ATTR_NAME, ImCollections.newList(stepStrings));
			
			this.presets.add(config);
			
			return config;
		}
		catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ImList<ILaunchConfiguration> toList() {
		return ImCollections.toList(this.presets);
	}
	
}
