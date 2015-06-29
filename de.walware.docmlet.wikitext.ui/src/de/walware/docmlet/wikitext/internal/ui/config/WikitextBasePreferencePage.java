/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.config;

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


public class WikitextBasePreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public WikitextBasePreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() {
		return new WikitextBaseConfigurationBlock(createStatusChangedListener());
	}
	
}


class WikitextBaseConfigurationBlock extends ManagedConfigurationBlock {
	
	
	public WikitextBaseConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		{	final Composite composite= createEditorInfo(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
	}
	
	private Composite createEditorInfo(final Composite composite) {
		final Group group= new Group(composite, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(1));
		group.setText(Messages.Base_Editors_label + ':');
		
		final Link link= addLinkControl(group, Messages.Base_Editors_SeeAlso_info +
				"\n   • All <a href=\"org.eclipse.ui.preferencePages.GeneralTextEditor\">Text Editors</a> (Eclipse, StatET)" +
				"\n   • <a href=\"org.eclipse.mylyn.wikitext.ui.editor.preferences.EditorPreferencePage\">Wikitext Syntax Hightlighting</a> (Mylyn)" +
				"\n   • <a href=\"org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.WikiTextTemplatePreferencePage\">Wikitext User Templates</a> (Mylyn)" );
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
		applyWrapWidth(gd);
		link.setLayoutData(gd);
		
		return group;
	}
	
}
