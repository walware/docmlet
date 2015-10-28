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

package de.walware.docmlet.tex.internal.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.templates.WaContributionContextTypeRegistry;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ui.settings.TextStyleManager;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.ImageRegistryUtil;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.internal.ui.editors.LtxDocumentProvider;
import de.walware.docmlet.tex.ui.TexUI;
import de.walware.docmlet.tex.ui.TexUIResources;
import de.walware.docmlet.tex.ui.sourceediting.TexEditingSettings;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;


public class TexUIPlugin extends AbstractUIPlugin {
	
	
	/** The shared instance */
	private static TexUIPlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static TexUIPlugin getInstance() {
		return instance;
	}
	
	
	private boolean started;
	
	private List<IDisposable> disposables;
	
	private IPreferenceStore editorPreferenceStore;
	
	private LtxDocumentProvider ltxDocumentProvider;
	
	private TextStyleManager ltxTextStyles;
	
	private ContextTypeRegistry ltxEditorTemplateContextTypeRegistry;
	private TemplateStore ltxEditorTemplateStore;
	
	private ContentAssistComputerRegistry ltxEditorContentAssistRegistry;
	
	private Map<String, String> commandImages;
	
	
	public TexUIPlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		this.disposables= new ArrayList<>();
		
		this.started= true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			if (this.ltxEditorTemplateStore != null) {
				this.ltxEditorTemplateStore.stopListeningForPreferenceChanges();
			}
			
			synchronized (this) {
				this.started= false;
				
				this.editorPreferenceStore= null;
				
				this.ltxDocumentProvider= null;
				this.ltxEditorTemplateStore= null;
				this.ltxEditorTemplateContextTypeRegistry= null;
				this.ltxEditorContentAssistRegistry= null;
			}
			
			for (final IDisposable listener : this.disposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, TexUI.PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN,
							"Error occured while disposing a module.", e )); 
				}
			}
			this.disposables= null;
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	public void addStoppingListener(final IDisposable listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.disposables.add(listener);
		}
	}
	
	
	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		if (!this.started) {
			throw new IllegalStateException("Plug-in is not started.");
		}
		final ImageRegistryUtil util= new ImageRegistryUtil(this);
		
		util.register(TexUIResources.OBJ_PART_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-part.png");
		util.register(TexUIResources.OBJ_CHAPTER_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-chapter.png");
		util.register(TexUIResources.OBJ_SECTION_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-section.png");
		util.register(TexUIResources.OBJ_SUBSECTION_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-subsection.png");
		util.register(TexUIResources.OBJ_SUBSUBSECTION_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-subsubsection.png");
		
		util.register(TexUIResources.OBJ_LABEL_IMAGE_ID, ImageRegistryUtil.T_OBJ, "label.png");
		
		final Map<String, String> commandMap= new HashMap<>();
		
		commandMap.put("S", TexUI.PLUGIN_ID + "/image/obj/c-00a7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dag", TexUI.PLUGIN_ID + "/image/obj/c-2020"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddag", TexUI.PLUGIN_ID + "/image/obj/c-2021"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textasciicircum", TexUI.PLUGIN_ID + "/image/obj/c-005e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textasciitilde", TexUI.PLUGIN_ID + "/image/obj/c-02dc"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("ldots", TexUI.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cdots", TexUI.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vdots", TexUI.PLUGIN_ID + "/image/obj/c-22ee"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("adots", TexUI.PLUGIN_ID + "/image/obj/c-22f0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddots", TexUI.PLUGIN_ID + "/image/obj/c-22f1"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("underline", TexUI.PLUGIN_ID + "/image/obj/c-0332"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("Alpha", TexUI.PLUGIN_ID + "/image/obj/greek-u-alpha"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("alpha", TexUI.PLUGIN_ID + "/image/obj/greek-l-alpha"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Beta", TexUI.PLUGIN_ID + "/image/obj/greek-u-beta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("beta", TexUI.PLUGIN_ID + "/image/obj/greek-l-beta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Gamma", TexUI.PLUGIN_ID + "/image/obj/greek-u-gamma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gamma", TexUI.PLUGIN_ID + "/image/obj/greek-l-gamma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Delta", TexUI.PLUGIN_ID + "/image/obj/greek-u-delta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("delta", TexUI.PLUGIN_ID + "/image/obj/greek-l-delta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Epsilon", TexUI.PLUGIN_ID + "/image/obj/greek-u-epsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("epsilon", TexUI.PLUGIN_ID + "/image/obj/greek-l-epsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varepsilon", TexUI.PLUGIN_ID + "/image/obj/greek-l-epsilon-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Zeta", TexUI.PLUGIN_ID + "/image/obj/greek-u-zeta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("zeta", TexUI.PLUGIN_ID + "/image/obj/greek-l-zeta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Eta", TexUI.PLUGIN_ID + "/image/obj/greek-u-eta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eta", TexUI.PLUGIN_ID + "/image/obj/greek-l-eta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Theta", TexUI.PLUGIN_ID + "/image/obj/greek-u-theta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("theta", TexUI.PLUGIN_ID + "/image/obj/greek-l-theta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartheta", TexUI.PLUGIN_ID + "/image/obj/greek-l-theta-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Iota", TexUI.PLUGIN_ID + "/image/obj/greek-u-iota"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("iota", TexUI.PLUGIN_ID + "/image/obj/greek-l-iota"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Kappa", TexUI.PLUGIN_ID + "/image/obj/greek-u-kappa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("kappa", TexUI.PLUGIN_ID + "/image/obj/greek-l-kappa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varkappa", TexUI.PLUGIN_ID + "/image/obj/greek-l-kappa-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Lambda", TexUI.PLUGIN_ID + "/image/obj/greek-u-lambda"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lambda", TexUI.PLUGIN_ID + "/image/obj/greek-l-lambda"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Mu", TexUI.PLUGIN_ID + "/image/obj/greek-u-mu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mu", TexUI.PLUGIN_ID + "/image/obj/greek-l-mu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Nu", TexUI.PLUGIN_ID + "/image/obj/greek-u-nu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nu", TexUI.PLUGIN_ID + "/image/obj/greek-l-nu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Xi", TexUI.PLUGIN_ID + "/image/obj/greek-u-xi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("xi", TexUI.PLUGIN_ID + "/image/obj/greek-l-xi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Omicron", TexUI.PLUGIN_ID + "/image/obj/greek-u-omicron"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("omicron", TexUI.PLUGIN_ID + "/image/obj/greek-l-omicron"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Pi", TexUI.PLUGIN_ID + "/image/obj/greek-u-pi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("pi", TexUI.PLUGIN_ID + "/image/obj/greek-l-pi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varpi", TexUI.PLUGIN_ID + "/image/obj/greek-l-pi-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Rho", TexUI.PLUGIN_ID + "/image/obj/greek-u-rho"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rho", TexUI.PLUGIN_ID + "/image/obj/greek-l-rho"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varrho", TexUI.PLUGIN_ID + "/image/obj/greek-l-rho-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Sigma", TexUI.PLUGIN_ID + "/image/obj/greek-u-sigma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sigma", TexUI.PLUGIN_ID + "/image/obj/greek-l-sigma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varsigma", TexUI.PLUGIN_ID + "/image/obj/greek-l-sigma-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Tau", TexUI.PLUGIN_ID + "/image/obj/greek-u-tau"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tau", TexUI.PLUGIN_ID + "/image/obj/greek-l-tau"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Upsilon", TexUI.PLUGIN_ID + "/image/obj/greek-u-upsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("upsilon", TexUI.PLUGIN_ID + "/image/obj/greek-l-upsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Phi", TexUI.PLUGIN_ID + "/image/obj/greek-u-phi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("phi", TexUI.PLUGIN_ID + "/image/obj/greek-l-phi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varphi", TexUI.PLUGIN_ID + "/image/obj/greek-l-phi-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Chi", TexUI.PLUGIN_ID + "/image/obj/greek-u-chi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("chi", TexUI.PLUGIN_ID + "/image/obj/greek-l-chi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Psi", TexUI.PLUGIN_ID + "/image/obj/greek-u-psi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("psi", TexUI.PLUGIN_ID + "/image/obj/greek-l-psi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Omega", TexUI.PLUGIN_ID + "/image/obj/greek-u-omega"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("omega", TexUI.PLUGIN_ID + "/image/obj/greek-l-omega"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("pm", TexUI.PLUGIN_ID + "/image/obj/c-00b1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mp", TexUI.PLUGIN_ID + "/image/obj/c-2213"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cdot", TexUI.PLUGIN_ID + "/image/obj/c-22c5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("times", TexUI.PLUGIN_ID + "/image/obj/c-00d7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ast", TexUI.PLUGIN_ID + "/image/obj/c-2217"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("star", TexUI.PLUGIN_ID + "/image/obj/c-22c6"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("diamond", TexUI.PLUGIN_ID + "/image/obj/c-22c4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("circ", TexUI.PLUGIN_ID + "/image/obj/c-2218"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bullet", TexUI.PLUGIN_ID + "/image/obj/c-2219"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("div", TexUI.PLUGIN_ID + "/image/obj/c-00f7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cap", TexUI.PLUGIN_ID + "/image/obj/c-2229"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cup", TexUI.PLUGIN_ID + "/image/obj/c-222a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("setminus", TexUI.PLUGIN_ID + "/image/obj/c-2216"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("uplus", TexUI.PLUGIN_ID + "/image/obj/c-228e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqcap", TexUI.PLUGIN_ID + "/image/obj/c-2293"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqcup", TexUI.PLUGIN_ID + "/image/obj/c-2294"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangleleft", TexUI.PLUGIN_ID + "/image/obj/c-25c1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangleright", TexUI.PLUGIN_ID + "/image/obj/c-25b7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("wr", TexUI.PLUGIN_ID + "/image/obj/c-2240"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("wedge", TexUI.PLUGIN_ID + "/image/obj/c-2228"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vee", TexUI.PLUGIN_ID + "/image/obj/c-2227"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oplus", TexUI.PLUGIN_ID + "/image/obj/c-2295"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ominus", TexUI.PLUGIN_ID + "/image/obj/c-2296"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("otimes", TexUI.PLUGIN_ID + "/image/obj/c-2297"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oslash", TexUI.PLUGIN_ID + "/image/obj/c-2298"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("odot", TexUI.PLUGIN_ID + "/image/obj/c-2299"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("amalg", TexUI.PLUGIN_ID + "/image/obj/c-2a3f"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("equiv", TexUI.PLUGIN_ID + "/image/obj/c-2261"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sim", TexUI.PLUGIN_ID + "/image/obj/c-223c"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("simeq", TexUI.PLUGIN_ID + "/image/obj/c-2ab0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("asymp", TexUI.PLUGIN_ID + "/image/obj/c-224d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("approx", TexUI.PLUGIN_ID + "/image/obj/c-2248"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("approxeq", TexUI.PLUGIN_ID + "/image/obj/c-224a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqsim", TexUI.PLUGIN_ID + "/image/obj/c-2242"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cong", TexUI.PLUGIN_ID + "/image/obj/c-2245"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("doteq", TexUI.PLUGIN_ID + "/image/obj/c-2250"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("leq", TexUI.PLUGIN_ID + "/image/obj/c-2264"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geq", TexUI.PLUGIN_ID + "/image/obj/c-2265"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ll", TexUI.PLUGIN_ID + "/image/obj/c-226a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gg", TexUI.PLUGIN_ID + "/image/obj/c-226b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leqq", TexUI.PLUGIN_ID + "/image/obj/c-2266"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geqq", TexUI.PLUGIN_ID + "/image/obj/c-2267"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leqslant", TexUI.PLUGIN_ID + "/image/obj/c-2a7d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geqslant", TexUI.PLUGIN_ID + "/image/obj/c-2a7e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqslantless", TexUI.PLUGIN_ID + "/image/obj/c-2a95"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqslantgtr", TexUI.PLUGIN_ID + "/image/obj/c-2a96"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lesssim", TexUI.PLUGIN_ID + "/image/obj/c-2272"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrsim", TexUI.PLUGIN_ID + "/image/obj/c-2273"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lessapprox", TexUI.PLUGIN_ID + "/image/obj/c-2a85"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrapprox", TexUI.PLUGIN_ID + "/image/obj/c-2a86"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lessdot", TexUI.PLUGIN_ID + "/image/obj/c-22d6"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrdot", TexUI.PLUGIN_ID + "/image/obj/c-22d7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("llless", TexUI.PLUGIN_ID + "/image/obj/c-22d8"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gggtr", TexUI.PLUGIN_ID + "/image/obj/c-22d9"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("lessgtr", TexUI.PLUGIN_ID + "/image/obj/c-2276"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrless", TexUI.PLUGIN_ID + "/image/obj/c-2277"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lesseqgtr", TexUI.PLUGIN_ID + "/image/obj/c-22da"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtreqless", TexUI.PLUGIN_ID + "/image/obj/c-22db"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("prec", TexUI.PLUGIN_ID + "/image/obj/c-227a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("succ", TexUI.PLUGIN_ID + "/image/obj/c-227b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("preceq", TexUI.PLUGIN_ID + "/image/obj/c-2aaf"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("succeq", TexUI.PLUGIN_ID + "/image/obj/c-2ab0"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("in", TexUI.PLUGIN_ID + "/image/obj/c-2208"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ni", TexUI.PLUGIN_ID + "/image/obj/c-220b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("subset", TexUI.PLUGIN_ID + "/image/obj/c-2282"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("supset", TexUI.PLUGIN_ID + "/image/obj/c-2283"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("subseteq", TexUI.PLUGIN_ID + "/image/obj/c-2286"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("supseteq", TexUI.PLUGIN_ID + "/image/obj/c-2287"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsubset", TexUI.PLUGIN_ID + "/image/obj/c-228f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsupset", TexUI.PLUGIN_ID + "/image/obj/c-2290"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsubseteq", TexUI.PLUGIN_ID + "/image/obj/c-2291"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsupseteq", TexUI.PLUGIN_ID + "/image/obj/c-2292"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bowtie", TexUI.PLUGIN_ID + "/image/obj/c-22c8"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("propto", TexUI.PLUGIN_ID + "/image/obj/c-221d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mid", TexUI.PLUGIN_ID + "/image/obj/c-2223"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vdash", TexUI.PLUGIN_ID + "/image/obj/c-22a2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dashv", TexUI.PLUGIN_ID + "/image/obj/c-22a3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("models", TexUI.PLUGIN_ID + "/image/obj/c-22a7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vDash", TexUI.PLUGIN_ID + "/image/obj/c-22a8"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Vdash", TexUI.PLUGIN_ID + "/image/obj/c-22a9"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Vvdash", TexUI.PLUGIN_ID + "/image/obj/c-22aa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartriangleleft", TexUI.PLUGIN_ID + "/image/obj/c-22b2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartriangleright", TexUI.PLUGIN_ID + "/image/obj/c-22b3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("trianglelefteq", TexUI.PLUGIN_ID + "/image/obj/c-22b4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("trianglerighteq", TexUI.PLUGIN_ID + "/image/obj/c-22b5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("parallel", TexUI.PLUGIN_ID + "/image/obj/c-2225"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("perp", TexUI.PLUGIN_ID + "/image/obj/c-27c2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("frown", TexUI.PLUGIN_ID + "/image/obj/c-2322"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("smile", TexUI.PLUGIN_ID + "/image/obj/c-2323"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("sum", TexUI.PLUGIN_ID + "/image/obj/c-2211"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("prod", TexUI.PLUGIN_ID + "/image/obj/c-220f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("coprod", TexUI.PLUGIN_ID + "/image/obj/c-2210"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("int", TexUI.PLUGIN_ID + "/image/obj/c-222b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oint", TexUI.PLUGIN_ID + "/image/obj/c-222e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigcap", TexUI.PLUGIN_ID + "/image/obj/c-22c2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigcup", TexUI.PLUGIN_ID + "/image/obj/c-22c3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigsqcup", TexUI.PLUGIN_ID + "/image/obj/c-2a06"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigwedge", TexUI.PLUGIN_ID + "/image/obj/c-22c0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigvee", TexUI.PLUGIN_ID + "/image/obj/c-22c1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigodot", TexUI.PLUGIN_ID + "/image/obj/c-2a00"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigoplus", TexUI.PLUGIN_ID + "/image/obj/c-2a01"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigotimes", TexUI.PLUGIN_ID + "/image/obj/c-2a02"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("biguplus", TexUI.PLUGIN_ID + "/image/obj/c-2a04"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("aleph", TexUI.PLUGIN_ID + "/image/obj/c-2135"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("beth", TexUI.PLUGIN_ID + "/image/obj/c-2136"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gimel", TexUI.PLUGIN_ID + "/image/obj/c-2137"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("daleth", TexUI.PLUGIN_ID + "/image/obj/c-2138"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("imath", TexUI.PLUGIN_ID + "/image/obj/c-1d6a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("jmath", TexUI.PLUGIN_ID + "/image/obj/c-1d6a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("complement", TexUI.PLUGIN_ID + "/image/obj/c-2201"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ell", TexUI.PLUGIN_ID + "/image/obj/c-2113"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eth", TexUI.PLUGIN_ID + "/image/obj/c-00f0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("hslash", TexUI.PLUGIN_ID + "/image/obj/c-210f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mho", TexUI.PLUGIN_ID + "/image/obj/c-2127"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("partial", TexUI.PLUGIN_ID + "/image/obj/c-2202"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Finv", TexUI.PLUGIN_ID + "/image/obj/c-2132"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("wp", TexUI.PLUGIN_ID + "/image/obj/script-u-p"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Re", TexUI.PLUGIN_ID + "/image/obj/fraktur-u-r"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Im", TexUI.PLUGIN_ID + "/image/obj/fraktur-u-i"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("prime", TexUI.PLUGIN_ID + "/image/obj/c-2032"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("backprime", TexUI.PLUGIN_ID + "/image/obj/c-2035"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("infty", TexUI.PLUGIN_ID + "/image/obj/c-221e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("emptyset", TexUI.PLUGIN_ID + "/image/obj/misc-o-emptyset"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varnothing", TexUI.PLUGIN_ID + "/image/obj/misc-o-varnothing"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nabla", TexUI.PLUGIN_ID + "/image/obj/c-2207"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("surd", TexUI.PLUGIN_ID + "/image/obj/c-221a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("top", TexUI.PLUGIN_ID + "/image/obj/c-22a4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bot", TexUI.PLUGIN_ID + "/image/obj/c-22a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("angle", TexUI.PLUGIN_ID + "/image/obj/c-2220"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("measuredangle", TexUI.PLUGIN_ID + "/image/obj/c-2221"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sphericalangle", TexUI.PLUGIN_ID + "/image/obj/c-2222"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("blacktriangle", TexUI.PLUGIN_ID + "/image/obj/misc-g-blacktriangle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangle", TexUI.PLUGIN_ID + "/image/obj/misc-g-triangle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("blacktriangledown", TexUI.PLUGIN_ID + "/image/obj/misc-g-blacktriangledown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangledown", TexUI.PLUGIN_ID + "/image/obj/misc-g-triangledown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("blacksquare", TexUI.PLUGIN_ID + "/image/obj/misc-g-blacksquare"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("square", TexUI.PLUGIN_ID + "/image/obj/misc-g-square"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("forall", TexUI.PLUGIN_ID + "/image/obj/c-2200"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("exists", TexUI.PLUGIN_ID + "/image/obj/c-2203"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nexists", TexUI.PLUGIN_ID + "/image/obj/c-2204"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("neg", TexUI.PLUGIN_ID + "/image/obj/c-00ac"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("flat", TexUI.PLUGIN_ID + "/image/obj/c-266d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("natural", TexUI.PLUGIN_ID + "/image/obj/c-266e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sharp", TexUI.PLUGIN_ID + "/image/obj/c-266f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("spadesuit", TexUI.PLUGIN_ID + "/image/obj/c-2660"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("heartsuit", TexUI.PLUGIN_ID + "/image/obj/c-2661"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("diamondsuit", TexUI.PLUGIN_ID + "/image/obj/c-2662"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("clubsuit", TexUI.PLUGIN_ID + "/image/obj/c-2663"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("lbrack", TexUI.PLUGIN_ID + "/image/obj/delim-b-lbrack"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rbrack", TexUI.PLUGIN_ID + "/image/obj/delim-b-rbrack"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lceil", TexUI.PLUGIN_ID + "/image/obj/delim-b-lceil"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rceil", TexUI.PLUGIN_ID + "/image/obj/delim-b-rceil"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lfloor", TexUI.PLUGIN_ID + "/image/obj/delim-b-lfloor"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rfloor", TexUI.PLUGIN_ID + "/image/obj/delim-b-rfloor"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lbrace", TexUI.PLUGIN_ID + "/image/obj/delim-b-lbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rbrace", TexUI.PLUGIN_ID + "/image/obj/delim-b-rbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("langle", TexUI.PLUGIN_ID + "/image/obj/delim-b-langle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rangle", TexUI.PLUGIN_ID + "/image/obj/delim-b-rangle"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("leftarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-right"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("uparrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("downarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-leftright"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nwarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-nw"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nearrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-ne"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("searrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-se"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("swarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-sw"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("updownarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-updown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Leftarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-left-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Rightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-right-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Uparrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-up-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Downarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-down-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Leftrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-leftright-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Updownarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-s-updown-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longleftarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-right"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longleftrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-leftright"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longleftarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-left-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-right-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longleftrightarrow", TexUI.PLUGIN_ID + "/image/obj/arrow-l-leftright-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mapsto", TexUI.PLUGIN_ID + "/image/obj/arrow-s-right-bar"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longmapsto", TexUI.PLUGIN_ID + "/image/obj/arrow-l-right-bar"); //$NON-NLS-1$ //$NON-NLS-2$
//		commandMap.put("hookleftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-left-hook"); //$NON-NLS-1$ //$NON-NLS-2$
//		commandMap.put("hookrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right-hook"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftharpoonup", TexUI.PLUGIN_ID + "/image/obj/arrow-h-left-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftharpoondown", TexUI.PLUGIN_ID + "/image/obj/arrow-h-left-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightharpoonup", TexUI.PLUGIN_ID + "/image/obj/arrow-h-right-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightharpoondown", TexUI.PLUGIN_ID + "/image/obj/arrow-h-right-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightleftharpoons", TexUI.PLUGIN_ID + "/image/obj/arrow-h-rightleft"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("grave", TexUI.PLUGIN_ID + "/image/obj/c-0300"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("acute", TexUI.PLUGIN_ID + "/image/obj/c-0301"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("hat", TexUI.PLUGIN_ID + "/image/obj/c-0302"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tilde", TexUI.PLUGIN_ID + "/image/obj/c-0303"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bar", TexUI.PLUGIN_ID + "/image/obj/c-0304"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("overline", TexUI.PLUGIN_ID + "/image/obj/c-0305"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("breve", TexUI.PLUGIN_ID + "/image/obj/c-0306"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("check", TexUI.PLUGIN_ID + "/image/obj/c-030c"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dot", TexUI.PLUGIN_ID + "/image/obj/c-0307"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddot", TexUI.PLUGIN_ID + "/image/obj/c-0308"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dddot", TexUI.PLUGIN_ID + "/image/obj/c-20db"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vec", TexUI.PLUGIN_ID + "/image/obj/c-20d7"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("widehat", TexUI.PLUGIN_ID + "/image/obj/comb-hat"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("widetilde", TexUI.PLUGIN_ID + "/image/obj/comb-tilde"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqrt", TexUI.PLUGIN_ID + "/image/obj/comb-root"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("frac", TexUI.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		
		final StringBuilder sb= new StringBuilder();
		for (final String key : commandMap.values()) {
			sb.setLength(0);
			sb.append(key, key.lastIndexOf('/')+1, key.length());
			sb.append(".png");
			util.register(key, ImageRegistryUtil.T_OBJ, sb.toString());
		}
		
		// elements
		commandMap.put("part", TexUIResources.OBJ_PART_IMAGE_ID);
		commandMap.put("chapter", TexUIResources.OBJ_CHAPTER_IMAGE_ID);
		commandMap.put("section", TexUIResources.OBJ_SECTION_IMAGE_ID);
		commandMap.put("subsection", TexUIResources.OBJ_SUBSECTION_IMAGE_ID);
		commandMap.put("subsubsection", TexUIResources.OBJ_SUBSUBSECTION_IMAGE_ID);
		
		commandMap.put("label", TexUIResources.OBJ_LABEL_IMAGE_ID);
		commandMap.put("ref", TexUIResources.OBJ_LABEL_IMAGE_ID);
		commandMap.put("pageref", TexUIResources.OBJ_LABEL_IMAGE_ID);
		commandMap.put("nameref", TexUIResources.OBJ_LABEL_IMAGE_ID);
		commandMap.put("eqref", TexUIResources.OBJ_LABEL_IMAGE_ID);
		
		// alias
		commandMap.put("dots", TexUI.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsc", TexUI.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsb", TexUI.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsm", TexUI.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsi", TexUI.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("textbullet", TexUI.PLUGIN_ID + "/image/obj/binop-bullet"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textperiodcentered", TexUI.PLUGIN_ID + "/image/obj/binop-cdot"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("le", TexUI.PLUGIN_ID + "/image/obj/c-2264"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ge", TexUI.PLUGIN_ID + "/image/obj/c-2265"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("dagger", TexUI.PLUGIN_ID + "/image/obj/c-2020"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddagger", TexUI.PLUGIN_ID + "/image/obj/c-2021"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("land", TexUI.PLUGIN_ID + "/image/obj/c-2228"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lor", TexUI.PLUGIN_ID + "/image/obj/c-2227"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("{", TexUI.PLUGIN_ID + "/image/obj/delim-b-lbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("}", TexUI.PLUGIN_ID + "/image/obj/delim-b-rbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("gets", TexUI.PLUGIN_ID + "/image/obj/arrow-s-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("to", TexUI.PLUGIN_ID + "/image/obj/arrow-s-right"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("dfrac", TexUI.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tfrac", TexUI.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.commandImages= commandMap;
	}
	
	
	public synchronized IPreferenceStore getEditorPreferenceStore() {
		if (this.editorPreferenceStore == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.editorPreferenceStore= CombinedPreferenceStore.createStore(
					getPreferenceStore(),
					EditorsUI.getPreferenceStore() );
		}
		return this.editorPreferenceStore;
	}
	
	public synchronized LtxDocumentProvider getTexDocumentProvider() {
		if (this.ltxDocumentProvider == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.ltxDocumentProvider= new LtxDocumentProvider();
			this.disposables.add(this.ltxDocumentProvider);
		}
		return this.ltxDocumentProvider;
	}
	
	public TextStyleManager getLtxTextStyles() {
		if (this.ltxTextStyles == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.ltxTextStyles= new TextStyleManager(SharedUIResources.getColors(),
					getPreferenceStore(),
					ITexTextStyles.LTX_TEXTSTYLE_CONFIG_QUALIFIER );
			PreferencesUtil.getSettingsChangeNotifier().addManageListener(this.ltxTextStyles);
		}
		return this.ltxTextStyles;
	}
	
	public synchronized ContextTypeRegistry getLtxEditorTemplateContextTypeRegistry() {
		if (this.ltxEditorTemplateContextTypeRegistry == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.ltxEditorTemplateContextTypeRegistry= new WaContributionContextTypeRegistry(
					"de.walware.docmlet.tex.templates.LtxEditor" ); //$NON-NLS-1$
		}
		return this.ltxEditorTemplateContextTypeRegistry;
	}
	
	public synchronized TemplateStore getLtxEditorTemplateStore() {
		if (this.ltxEditorTemplateStore == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.ltxEditorTemplateStore= new ContributionTemplateStore(
					getLtxEditorTemplateContextTypeRegistry(), getPreferenceStore(),
					"editor/assist/Ltx/EditorTemplates.store" ); //$NON-NLS-1$
			try {
				this.ltxEditorTemplateStore.load();
			}
			catch (final IOException e) {
				getLog().log(new Status(IStatus.ERROR, TexUI.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"An error occured when loading 'LaTeX Editor' template store.", e)); 
			}
		}
		return this.ltxEditorTemplateStore;
	}
	
	public synchronized ContentAssistComputerRegistry getTexEditorContentAssistRegistry() {
		if (this.ltxEditorContentAssistRegistry == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.ltxEditorContentAssistRegistry= new ContentAssistComputerRegistry(
					TexCore.LTX_CONTENT_ID_NG, 
					TexEditingSettings.ASSIST_LTX_PREF_QUALIFIER ); 
			this.disposables.add(this.ltxEditorContentAssistRegistry);
		}
		return this.ltxEditorContentAssistRegistry;
	}
	
	public Map<String, String> getCommandImages() {
		getImageRegistry();
		return this.commandImages;
	}
	
}
