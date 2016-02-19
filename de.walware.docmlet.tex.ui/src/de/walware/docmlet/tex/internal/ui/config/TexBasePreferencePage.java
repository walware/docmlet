/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.util.LayoutUtil;


public class TexBasePreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public TexBasePreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() {
		return new TexBaseConfigurationBlock(createStatusChangedListener());
	}
	
}


class TexBaseConfigurationBlock extends ManagedConfigurationBlock {
	
	
	public TexBaseConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		{	final Composite composite= createEditorInfo(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		{	final Composite composite= createToolsInfo(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
	}
	
	private Composite createEditorInfo(final Composite composite) {
		final Group group= new Group(composite, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(1));
		group.setText(Messages.Base_Editors_label + ':');
		
		final Link link= addLinkControl(group, Messages.Base_Editors_SeeAlso_info +
				"\n   • All <a href=\"org.eclipse.ui.preferencePages.GeneralTextEditor\">Text Editors</a> (Eclipse, StatET)" );
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
		applyWrapWidth(gd);
		link.setLayoutData(gd);
		
		return group;
	}
	
	private Composite createToolsInfo(final Composite composite) {
		final Group group= new Group(composite, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(1));
		group.setText(Messages.Base_Tools_label + ':');
		
		final Link link= addLinkControl(group, Messages.Base_Tools_SeeAlso_info +
				"\n   • <a href=\"TexlipseBuilderPreferencePage\">Builder Settings</a> (Texlipse)" );
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
		applyWrapWidth(gd);
		link.setLayoutData(gd);
		
		return group;
	}
	
}
