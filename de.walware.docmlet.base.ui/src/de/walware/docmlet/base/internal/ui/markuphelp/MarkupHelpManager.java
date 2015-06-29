/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.internal.ui.markuphelp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpContentProvider;
import de.walware.docmlet.base.ui.markuphelp.MarkupHelpContent;


public class MarkupHelpManager {
	
	
	private static final String EXTENSION_POINT_ID= "de.walware.docmlet.base.markupHelp"; //$NON-NLS-1$
	
	private static final String CONTENT_PROVIDER_ELEMENT_NAME= "contentProvider"; //$NON-NLS-1$
	private static final String CLASS_ATTR_NAME= "class"; //$NON-NLS-1$
	
	
	private static final Comparator<MarkupHelpContent> CONTENT_UI_COMPARATOR= new Comparator<MarkupHelpContent>() {
		
		@Override
		public int compare(final MarkupHelpContent o1, final MarkupHelpContent o2) {
			return o1.getTitle().compareTo(o2.getTitle());
		}
		
	};
	
	
	private Map<String, MarkupHelpContent> idContents;
	
	private List<MarkupHelpContent> topics;
	
	
	public MarkupHelpManager() {
		
		loadContributions();
	}
	
	
	private void loadContributions() {
		final Map<String, MarkupHelpContent> map= new HashMap<>();
		final List<MarkupHelpContent> topics= new ArrayList<>();
		
		final IConfigurationElement[] elements= Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (final IConfigurationElement element : elements) {
			if (element.getName().equals(CONTENT_PROVIDER_ELEMENT_NAME)) {
				try {
					final IMarkupHelpContentProvider contentProvider=
							(IMarkupHelpContentProvider) element.createExecutableExtension(CLASS_ATTR_NAME);
					final Collection<MarkupHelpContent> providedTopics= contentProvider.getHelpTopics();
					for (final MarkupHelpContent content : providedTopics) {
						map.put(content.getId(), content);
						topics.add(content);
					}
				}
				catch (final CoreException e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
							"An error occurred when loading markup help contentProvider.",
							e ));
				}
			}
		}
		
		Collections.sort(topics, CONTENT_UI_COMPARATOR);
		
		this.idContents= map;
		this.topics= Collections.unmodifiableList(topics);
	}
	
	
	public List<MarkupHelpContent> getTopicList() {
		return this.topics;
	}
	
	public MarkupHelpContent getContent(final String id) {
		return this.idContents.get(id);
	}
	
	public synchronized void disable(final String id) {
		final MarkupHelpContent content= this.idContents.remove(id);
		if (content != null) {
			final List<MarkupHelpContent> topics= new ArrayList<>(this.topics);
			topics.remove(content);
			this.topics= Collections.unmodifiableList(topics);
		}
	}
	
}
