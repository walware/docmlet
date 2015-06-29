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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpContextProvider;
import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpView;
import de.walware.docmlet.base.ui.markuphelp.MarkupHelpContent;


public class MarkupHelpView extends ViewPart implements IMarkupHelpView {
	
	
	private static void appendCssColor(final StringBuilder sb, final RGB color) {
		sb.append('#');
		String s = Integer.toHexString(color.red);
		if (s.length() == 1) {
			sb.append('0');
		}
		sb.append(s);
		s = Integer.toHexString(color.green);
		if (s.length() == 1) {
			sb.append('0');
		}
		sb.append(s);
		s = Integer.toHexString(color.blue);
		if (s.length() == 1) {
			sb.append('0');
		}
		sb.append(s);
	}
	
	
	private Browser browser;
	
	private IPartListener2 partListener;
	
	private String lastHtml;
	
	
	public MarkupHelpView() {
	}
	
	
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(LayoutUtil.applySashDefaults(new GridLayout(), 1));
		
		this.browser= new Browser(parent, SWT.NONE);
		this.browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Display display= this.browser.getDisplay();
		this.browser.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		this.browser.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		this.browser.addOpenWindowListener(new OpenWindowListener() {
			@Override
			public void open(final WindowEvent event) {
				event.browser= MarkupHelpView.this.browser;
//				event.required= true; // Cancel opening of new windows
			}
		});
		this.browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(final LocationEvent event) {
				if (event.location != null && !event.location.startsWith("about:")) { //$NON-NLS-1$
					openExternal(event.location);
					event.doit= false;
				}
			}
			@Override
			public void changed(final LocationEvent event) {
			}
		});
		
		final IViewSite site= getViewSite();
		initActions(site, null);
		contributeToActionBars(site, site.getActionBars(), null);
		
		initLinking();
	}
	
	private void initLinking() {
		this.partListener= new IPartListener2() {
			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partBroughtToTop(final IWorkbenchPartReference partRef) {
			}
			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {
				final IWorkbenchPart part= partRef.getPart(false);
				if (part instanceof IEditorPart) {
					activated((IEditorPart) part);
				}
			}
		};
		
		getSite().getPage().addPartListener(this.partListener);
		
		UIAccess.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				activated(getSite().getPage().getActiveEditor());
			}
		});
	}
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
	}
	
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		final IMenuManager menuManager= actionBars.getMenuManager();
		
		{	final List<MarkupHelpContent> topics= DocBaseUIPlugin.getInstance().getMarkupHelpManager().getTopicList();
			for (final MarkupHelpContent topic : topics) {
				menuManager.add(new SimpleContributionItem(topic.getTitle(), null) {
					final String topicId= topic.getId();
					@Override
					protected void execute(final Event event) throws ExecutionException {
						show(this.topicId);
					}
				});
			}
		}
	}
	
	@Override
	public void dispose() {
		if (this.partListener != null) {
			getSite().getPage().removePartListener(this.partListener);
			this.partListener= null;
		}
		
		super.dispose();
	}
	
	private void activated(final IEditorPart editor) {
		final IMarkupHelpContextProvider contextProvider= (IMarkupHelpContextProvider)
				editor.getAdapter(IMarkupHelpContextProvider.class);
		if (contextProvider != null) {
			show(contextProvider.getHelpContentId());
		}
	}
	
	@Override
	public void setFocus() {
		this.browser.setFocus();
	}
	
	@Override
	public void show(final String id) {
		final MarkupHelpContent content= DocBaseUIPlugin.getInstance().getMarkupHelpManager().getContent(id);
		if (content != null) {
			setContent(content);
		}
	}
	
	private void setContent(final MarkupHelpContent content) {
		String html;
		try {
			html= content.getContent();
		}
		catch (final IOException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
					NLS.bind("An error occurred while loading markup help content {0}).", content),
					e ));
			DocBaseUIPlugin.getInstance().getMarkupHelpManager().disable(content.getId());
			return;
		}
		html= style(html);
		
		if (!html.equals(this.lastHtml)) {
			this.lastHtml= html;
			this.browser.setText(html);
		}
	}
	
	private String style(final String html) {
		final StringBuilder sb= new StringBuilder(html.length() + 200);
		final int idx= html.indexOf("</head>"); //$NON-NLS-1$
		sb.append(html, 0, idx);
		sb.append("<style type=\"text/css\">\n");
		collectCss(sb);
		sb.append("</style>");
		sb.append(html, idx, html.length());
		return sb.toString();
	}
	
	private void collectCss(final StringBuilder sb) {
		final RGB foregroundColor = JFaceResources.getColorRegistry().getRGB("de.walware.workbench.themes.DocViewColor"); //$NON-NLS-1$
		final RGB docBackgroundColor = JFaceResources.getColorRegistry().getRGB("de.walware.workbench.themes.DocViewBackgroundColor"); //$NON-NLS-1$
		
		final FontDescriptor docFontDescr = JFaceResources.getFontDescriptor("de.walware.workbench.themes.DocViewFont"); //$NON-NLS-1$
		final FontData fontData = docFontDescr.getFontData()[0];
			
		{	sb.append("body { font-family: '"); //$NON-NLS-1$
			sb.append(fontData.getName());
			sb.append("'; font-size: "); //$NON-NLS-1$
			sb.append(fontData.getHeight());
			sb.append("pt; color: "); //$NON-NLS-1$
			appendCssColor(sb, foregroundColor);
			sb.append("; background: "); //$NON-NLS-1$
			appendCssColor(sb, docBackgroundColor);
			sb.append("; }\n"); //$NON-NLS-1$
		}
		{	sb.append("body { margin: "); //$NON-NLS-1$
			sb.append(LayoutUtil.defaultVSpacing());
			sb.append("px "); //$NON-NLS-1$
			sb.append(LayoutUtil.defaultHSpacing());
			sb.append("px; }\n"); //$NON-NLS-1$
		}
		{	sb.append("table, tr, th, td { font-size: "); //$NON-NLS-1$
			sb.append(fontData.getHeight());
			sb.append("pt; }\n"); //$NON-NLS-1$
		}
	}
	
	private void openExternal(final String location) {
		final IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			final IWebBrowser externalBrowser = browserSupport.createBrowser(
					IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS,
					"de.walware.docmlet.base.MarkupHelp", null, null);
			final URL url = new URL(location);
			externalBrowser.openURL(url);
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
					"An error occurred while opening the page in an external browser.",
					e ));
		}
	}
	
}
