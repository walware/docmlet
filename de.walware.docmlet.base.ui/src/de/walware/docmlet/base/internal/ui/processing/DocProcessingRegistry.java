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

package de.walware.docmlet.base.internal.ui.processing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.IDisposable;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingManager;


public class DocProcessingRegistry implements IDisposable {
	
	
	private static final String EXTENSION_POINT_ID= "de.walware.docmlet.base.docProcessing"; //$NON-NLS-1$
	
	private static final String PROCESSING_TYPE_ELEMENT_NAME= "processingType"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_ID_ATTR_NAME= "contentTypeId"; //$NON-NLS-1$
	private static final String CONFIG_TYPE_ID_ATTR_NAME= "configTypeId"; //$NON-NLS-1$
	private static final String MANAGER_CLASS_ATTR_NAME= "managerClass"; //$NON-NLS-1$
	
	
	public static abstract class TypeElement {
		
		
		protected abstract void init(String contentTypeId, String configTypeId);
		
	}
	
	private static class TypeEntry {
		
		private static final byte S_MANAGER_FAILED=         0b0_00000001;
		private static final byte S_DISPOSED=        (byte) 0b0_10000000;
		
		
		private final String contentTypeId;
		
		
		private final IConfigurationElement element;
		
		private byte state;
		
		
		private DocProcessingManager manager;
		
		
		public TypeEntry(final String contentTypeId, final IConfigurationElement element) {
			this.contentTypeId= contentTypeId.intern();
			this.element= element;
		}
		
		
		public String getContentTypeId() {
			return this.contentTypeId;
		}
		
		public DocProcessingManager getManager() {
			DocProcessingManager manager= this.manager;
			if (manager == null) {
				synchronized (this) {
					manager= this.manager;
					if (manager == null && (this.state & (S_MANAGER_FAILED | S_DISPOSED)) == 0) {
						try {
							final String configTypeId= this.element.getAttribute(CONFIG_TYPE_ID_ATTR_NAME);
							manager= (DocProcessingManager)
									this.element.createExecutableExtension(MANAGER_CLASS_ATTR_NAME);
							((TypeElement) manager).init(getContentTypeId(), configTypeId);
							this.manager= manager;
						}
						catch (final CoreException e) {
							this.state|= S_MANAGER_FAILED;
							DocBaseUIPlugin.log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
									NLS.bind("An error occurred when loading document processing type ''{0}''.", getContentTypeId()),
									e ));
							return null;
						}
					}
				}
			}
			return manager;
		}
		
		public void dispose() {
			synchronized (this) {
				this.state|= S_DISPOSED;
				final DocProcessingManager manager= this.manager;
				if (manager != null) {
					manager.dispose();
					this.manager= null;
				}
			}
		}
		
	}
	
	
	private Map<String, TypeEntry> entries;
	
	
	public DocProcessingRegistry() {
		
		loadContributions();
	}
	
	
	private void loadContributions() {
		final Map<String, TypeEntry> entries= new HashMap<>();
		final IConfigurationElement[] elements= Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (final IConfigurationElement element : elements) {
			if (element.getName().equals(PROCESSING_TYPE_ELEMENT_NAME)) {
				final String contentTypeId= element.getAttribute(CONTENT_TYPE_ID_ATTR_NAME);
				if (contentTypeId != null && !contentTypeId.isEmpty()) {
					final TypeEntry item= new TypeEntry(contentTypeId, element);
					entries.put(item.getContentTypeId(), item);
				}
			}
		}
		
		this.entries= entries;
	}
	
	public DocProcessingManager getDocProcessingManager(final String contentTypeId) {
		final TypeEntry item= this.entries.get(contentTypeId);
		return (item != null) ? item.getManager() : null;
	}
	
	@Override
	public void dispose() {
		for (final TypeEntry entry : this.entries.values()) {
			entry.dispose();
		}
	}
	
}
