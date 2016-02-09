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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.ui.ColorSelectorObservableValue;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.wikitext.internal.ui.sourceediting.EmbeddedHtml;


public class WikitextBasePreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public WikitextBasePreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() {
		return new WikitextBaseConfigurationBlock(createStatusChangedListener());
	}
	
}


class WikitextBaseConfigurationBlock extends ManagedConfigurationBlock {
	
	
	private ColorSelector htmlBackgroundColorSelector;
	private ColorSelector htmlCommentColorSelector;
	
	
	public WikitextBaseConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs= new HashMap<>();
		
		prefs.put(EmbeddedHtml.HTML_BACKGROUND_COLOR_PREF, null);
		prefs.put(EmbeddedHtml.HTML_COMMENT_COLOR_PREF, null);
		
		setupPreferenceManager(prefs);
		
		{	final Composite composite= createEditorGroup(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		initBindings();
		updateControls();
	}
	
	private Composite createEditorGroup(final Composite parent) {
		final Group composite= new Group(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createGroupGrid(2));
		composite.setText(Messages.Base_Editors_label + ':');
		
		{	final Link link= addLinkControl(composite, Messages.Base_Editors_SeeAlso_info +
					"\n   • All <a href=\"org.eclipse.ui.preferencePages.GeneralTextEditor\">Text Editors</a> (Eclipse, StatET)" +
					"\n   • <a href=\"org.eclipse.mylyn.wikitext.ui.editor.preferences.EditorPreferencePage\">Wikitext Syntax Hightlighting</a> (Mylyn)" +
					"\n   • <a href=\"org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.WikiTextTemplatePreferencePage\">Wikitext User Templates</a> (Mylyn)" );
			final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
			applyWrapWidth(gd);
			link.setLayoutData(gd);
		}
		
		LayoutUtil.addSmallFiller(composite, false);
		{	final Label label= new Label(composite, SWT.NONE);
			label.setText("Supplementary preferences:");
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		
		{	final Label label= new Label(composite, SWT.NONE);
			label.setText("Background color for &HTML ranges:");
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			
			final ColorSelector selector= new ColorSelector(composite);
			selector.getButton().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			this.htmlBackgroundColorSelector= selector;
		}
		{	final Label label= new Label(composite, SWT.NONE);
			label.setText("Text color for HTML c&omments:");
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			
			final ColorSelector selector= new ColorSelector(composite);
			selector.getButton().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			this.htmlCommentColorSelector= selector;
		}
		return composite;
	}
	
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		db.getContext().bindValue(
				new ColorSelectorObservableValue(this.htmlBackgroundColorSelector),
				createObservable(EmbeddedHtml.HTML_BACKGROUND_COLOR_PREF),
				null, null );
		db.getContext().bindValue(
				new ColorSelectorObservableValue(this.htmlCommentColorSelector),
				createObservable(EmbeddedHtml.HTML_COMMENT_COLOR_PREF),
				null, null );
	}
	
}
