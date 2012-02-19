/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

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
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.ui.util.ImageRegistryUtil;

import de.walware.docmlet.tex.internal.ui.editors.LtxDocumentProvider;
import de.walware.docmlet.tex.ui.TexImages;


public class TexUIPlugin extends AbstractUIPlugin {
	
	
	public static final String PLUGIN_ID = "de.walware.docmlet.tex.ui"; //$NON-NLS-1$
	
	public static final String TEX_EDITOR_TEMPLATES_ID = "de.walware.docmlet.tex.templates.LtxEditor"; //$NON-NLS-1$
	
	public static final String TEX_EDITOR_QUALIFIER = PLUGIN_ID + "/tex.editor/options"; //$NON-NLS-1$
	
	public static final String TEX_EDITOR_ASSIST_REGISTRY_GROUP_ID = "tex/tex.editor/assist.registry"; //$NON-NLS-1$
	public static final String TEX_EDITOR_ASSIST_UI_GROUP_ID = "tex/tex.editor/assist.ui"; //$NON-NLS-1$
	
	
	/** The shared instance */
	private static TexUIPlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static TexUIPlugin getDefault() {
		return gPlugin;
	}
	
	
	private boolean fStarted;
	
	private final List<IDisposable> fDisposables = new ArrayList<IDisposable>();
	
	private LtxDocumentProvider fTexDocumentProvider;
	
	private IPreferenceStore fEditorPreferenceStore;
	
	private ContextTypeRegistry fTexEditorTemplateContextTypeRegistry;
	private TemplateStore fTexEditorTemplateStore;
	private ContentAssistComputerRegistry fTexEditorContentAssistRegistry;
	
	private Map<String, String> fCommandImages;
	
	
	public TexUIPlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		gPlugin = this;
		
		fStarted = true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			if (fTexEditorTemplateStore != null) {
				fTexEditorTemplateStore.stopListeningForPreferenceChanges();
			}
			
			synchronized (this) {
				fStarted = false;
				
				fEditorPreferenceStore = null;
				
				fTexDocumentProvider = null;
				fTexEditorTemplateStore = null;
				fTexEditorTemplateContextTypeRegistry = null;
				fTexEditorContentAssistRegistry = null;
			}
			
			for (final IDisposable listener : fDisposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN, "Error occured when dispose module", e)); 
				}
			}
			fDisposables.clear();
		}
		finally {
			gPlugin = null;
			super.stop(context);
		}
	}
	
	
	public void addStoppingListener(final IDisposable listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fDisposables.add(listener);
		}
	}
	
	
	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		if (!fStarted) {
			throw new IllegalStateException("Plug-in is not started.");
		}
		final ImageRegistryUtil util = new ImageRegistryUtil(this);
		
		util.register(TexImages.OBJ_PREAMBLE, ImageRegistryUtil.T_OBJ, "preamble.gif");
		
		util.register(TexImages.OBJ_PART, ImageRegistryUtil.T_OBJ, "sectioning-part.png");
		util.register(TexImages.OBJ_CHAPTER, ImageRegistryUtil.T_OBJ, "sectioning-chapter.png");
		util.register(TexImages.OBJ_SECTION, ImageRegistryUtil.T_OBJ, "sectioning-section.png");
		util.register(TexImages.OBJ_SUBSECTION, ImageRegistryUtil.T_OBJ, "sectioning-subsection.png");
		util.register(TexImages.OBJ_SUBSUBSECTION, ImageRegistryUtil.T_OBJ, "sectioning-subsubsection.png");
		
		util.register(TexImages.OBJ_LABEL, ImageRegistryUtil.T_OBJ, "label.png");
		
		final Map<String, String> commandMap = new HashMap<String, String>();
		
		commandMap.put("S", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00a7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dag", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2020"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddag", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2021"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textasciicircum", TexUIPlugin.PLUGIN_ID + "/image/obj/c-005e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textasciitilde", TexUIPlugin.PLUGIN_ID + "/image/obj/c-02dc"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("ldots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cdots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vdots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22ee"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("adots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22f0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22f1"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("underline", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0332"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("Alpha", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-alpha"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("alpha", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-alpha"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Beta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-beta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("beta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-beta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Gamma", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-gamma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gamma", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-gamma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Delta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-delta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("delta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-delta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Epsilon", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-epsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("epsilon", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-epsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varepsilon", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-epsilon-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Zeta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-zeta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("zeta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-zeta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Eta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-eta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-eta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Theta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-theta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("theta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-theta"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartheta", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-theta-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Iota", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-iota"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("iota", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-iota"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Kappa", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-kappa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("kappa", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-kappa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varkappa", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-kappa-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Lambda", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-lambda"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lambda", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-lambda"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Mu", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-mu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mu", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-mu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Nu", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-nu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nu", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-nu"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Xi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-xi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("xi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-xi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Omicron", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-omicron"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("omicron", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-omicron"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Pi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-pi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("pi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-pi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varpi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-pi-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Rho", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-rho"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rho", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-rho"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varrho", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-rho-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Sigma", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-sigma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sigma", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-sigma"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varsigma", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-sigma-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Tau", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-tau"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tau", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-tau"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Upsilon", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-upsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("upsilon", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-upsilon"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Phi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-phi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("phi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-phi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varphi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-phi-var"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Chi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-chi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("chi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-chi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Psi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-psi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("psi", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-psi"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Omega", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-u-omega"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("omega", TexUIPlugin.PLUGIN_ID + "/image/obj/greek-l-omega"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("pm", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00b1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mp", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2213"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cdot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("times", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00d7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ast", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2217"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("star", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c6"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("diamond", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("circ", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2218"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bullet", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2219"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("div", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00f7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cap", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2229"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cup", TexUIPlugin.PLUGIN_ID + "/image/obj/c-222a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("setminus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2216"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("uplus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-228e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqcap", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2293"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqcup", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2294"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangleleft", TexUIPlugin.PLUGIN_ID + "/image/obj/c-25c1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangleright", TexUIPlugin.PLUGIN_ID + "/image/obj/c-25b7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("wr", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2240"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("wedge", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2228"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vee", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2227"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oplus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2295"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ominus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2296"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("otimes", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2297"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oslash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2298"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("odot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2299"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("amalg", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a3f"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("equiv", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2261"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sim", TexUIPlugin.PLUGIN_ID + "/image/obj/c-223c"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("simeq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2ab0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("asymp", TexUIPlugin.PLUGIN_ID + "/image/obj/c-224d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("approx", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2248"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("approxeq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-224a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqsim", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2242"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("cong", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2245"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("doteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2250"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("leq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2264"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2265"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ll", TexUIPlugin.PLUGIN_ID + "/image/obj/c-226a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gg", TexUIPlugin.PLUGIN_ID + "/image/obj/c-226b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leqq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2266"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geqq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2267"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leqslant", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a7d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("geqslant", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a7e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqslantless", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a95"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eqslantgtr", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a96"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lesssim", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2272"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrsim", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2273"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lessapprox", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a85"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrapprox", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a86"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lessdot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22d6"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrdot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22d7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("llless", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22d8"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gggtr", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22d9"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("lessgtr", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2276"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtrless", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2277"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lesseqgtr", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22da"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gtreqless", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22db"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("prec", TexUIPlugin.PLUGIN_ID + "/image/obj/c-227a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("succ", TexUIPlugin.PLUGIN_ID + "/image/obj/c-227b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("preceq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2aaf"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("succeq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2ab0"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("in", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2208"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ni", TexUIPlugin.PLUGIN_ID + "/image/obj/c-220b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("subset", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2282"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("supset", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2283"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("subseteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2286"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("supseteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2287"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsubset", TexUIPlugin.PLUGIN_ID + "/image/obj/c-228f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsupset", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2290"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsubseteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2291"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqsupseteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2292"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bowtie", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c8"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("propto", TexUIPlugin.PLUGIN_ID + "/image/obj/c-221d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mid", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2223"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vdash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dashv", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("models", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a7"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vDash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a8"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Vdash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a9"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Vvdash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22aa"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartriangleleft", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22b2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vartriangleright", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22b3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("trianglelefteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22b4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("trianglerighteq", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22b5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("parallel", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2225"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("perp", TexUIPlugin.PLUGIN_ID + "/image/obj/c-27c2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("frown", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2322"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("smile", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2323"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("sum", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2211"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("prod", TexUIPlugin.PLUGIN_ID + "/image/obj/c-220f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("coprod", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2210"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("int", TexUIPlugin.PLUGIN_ID + "/image/obj/c-222b"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("oint", TexUIPlugin.PLUGIN_ID + "/image/obj/c-222e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigcap", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c2"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigcup", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c3"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigsqcup", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a06"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigwedge", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigvee", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22c1"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigodot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a00"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigoplus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a01"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bigotimes", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a02"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("biguplus", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2a04"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("aleph", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2135"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("beth", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2136"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("gimel", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2137"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("daleth", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2138"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("imath", TexUIPlugin.PLUGIN_ID + "/image/obj/c-1d6a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("jmath", TexUIPlugin.PLUGIN_ID + "/image/obj/c-1d6a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("complement", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2201"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ell", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2113"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("eth", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00f0"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("hslash", TexUIPlugin.PLUGIN_ID + "/image/obj/c-210f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mho", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2127"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("partial", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2202"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Finv", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2132"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("wp", TexUIPlugin.PLUGIN_ID + "/image/obj/script-u-p"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Re", TexUIPlugin.PLUGIN_ID + "/image/obj/fraktur-u-r"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Im", TexUIPlugin.PLUGIN_ID + "/image/obj/fraktur-u-i"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("prime", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2032"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("backprime", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2035"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("infty", TexUIPlugin.PLUGIN_ID + "/image/obj/c-221e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("emptyset", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-o-emptyset"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("varnothing", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-o-varnothing"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nabla", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2207"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("surd", TexUIPlugin.PLUGIN_ID + "/image/obj/c-221a"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("top", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a4"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22a5"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("angle", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2220"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("measuredangle", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2221"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sphericalangle", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2222"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("blacktriangle", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-blacktriangle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangle", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-triangle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("blacktriangledown", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-blacktriangledown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("triangledown", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-triangledown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("blacksquare", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-blacksquare"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("square", TexUIPlugin.PLUGIN_ID + "/image/obj/misc-g-square"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("forall", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2200"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("exists", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2203"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nexists", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2204"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("neg", TexUIPlugin.PLUGIN_ID + "/image/obj/c-00ac"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("flat", TexUIPlugin.PLUGIN_ID + "/image/obj/c-266d"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("natural", TexUIPlugin.PLUGIN_ID + "/image/obj/c-266e"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sharp", TexUIPlugin.PLUGIN_ID + "/image/obj/c-266f"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("spadesuit", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2660"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("heartsuit", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2661"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("diamondsuit", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2662"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("clubsuit", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2663"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("lbrack", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-lbrack"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rbrack", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rbrack"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lceil", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-lceil"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rceil", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rceil"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lfloor", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-lfloor"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rfloor", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rfloor"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lbrace", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-lbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rbrace", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("langle", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-langle"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rangle", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rangle"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("leftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("uparrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("downarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-leftright"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nwarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-nw"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("nearrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-ne"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("searrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-se"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("swarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-sw"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("updownarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-updown"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Leftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-left-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Rightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Uparrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-up-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Downarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-down-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Leftrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-leftright-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Updownarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-updown-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longleftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-right"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longleftrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-leftright"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longleftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-left-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-right-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("Longleftrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-leftright-double"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("mapsto", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right-bar"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("longmapsto", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-l-right-bar"); //$NON-NLS-1$ //$NON-NLS-2$
//		commandMap.put("hookleftarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-left-hook"); //$NON-NLS-1$ //$NON-NLS-2$
//		commandMap.put("hookrightarrow", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right-hook"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftharpoonup", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-h-left-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("leftharpoondown", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-h-left-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightharpoonup", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-h-right-up"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightharpoondown", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-h-right-down"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("rightleftharpoons", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-h-rightleft"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("grave", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0300"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("acute", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0301"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("hat", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0302"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tilde", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0303"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("bar", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0304"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("overline", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0305"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("breve", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0306"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("check", TexUIPlugin.PLUGIN_ID + "/image/obj/c-030c"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0307"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-0308"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dddot", TexUIPlugin.PLUGIN_ID + "/image/obj/c-20db"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("vec", TexUIPlugin.PLUGIN_ID + "/image/obj/c-20d7"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("widehat", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-hat"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("widetilde", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-tilde"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("sqrt", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-root"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("frac", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		
		final StringBuilder sb = new StringBuilder();
		for (final String key : commandMap.values()) {
			sb.setLength(0);
			sb.append(key, key.lastIndexOf('/')+1, key.length());
			sb.append(".png");
			util.register(key, ImageRegistryUtil.T_OBJ, sb.toString());
		}
		
		// elements
		commandMap.put("part", TexImages.OBJ_PART);
		commandMap.put("chapter", TexImages.OBJ_CHAPTER);
		commandMap.put("section", TexImages.OBJ_SECTION);
		commandMap.put("subsection", TexImages.OBJ_SUBSECTION);
		commandMap.put("subsubsection", TexImages.OBJ_SUBSUBSECTION);
		
		commandMap.put("label", TexImages.OBJ_LABEL);
		commandMap.put("ref", TexImages.OBJ_LABEL);
		commandMap.put("pageref", TexImages.OBJ_LABEL);
		commandMap.put("nameref", TexImages.OBJ_LABEL);
		commandMap.put("eqref", TexImages.OBJ_LABEL);
		
		// alias
		commandMap.put("dots", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsc", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2026"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsb", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsm", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("dotsi", TexUIPlugin.PLUGIN_ID + "/image/obj/c-22ef"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("textbullet", TexUIPlugin.PLUGIN_ID + "/image/obj/binop-bullet"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("textperiodcentered", TexUIPlugin.PLUGIN_ID + "/image/obj/binop-cdot"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("le", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2264"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ge", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2265"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("dagger", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2020"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("ddagger", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2021"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("land", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2228"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("lor", TexUIPlugin.PLUGIN_ID + "/image/obj/c-2227"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("{", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-lbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("}", TexUIPlugin.PLUGIN_ID + "/image/obj/delim-b-rbrace"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("gets", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-left"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("to", TexUIPlugin.PLUGIN_ID + "/image/obj/arrow-s-right"); //$NON-NLS-1$ //$NON-NLS-2$
		
		commandMap.put("dfrac", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		commandMap.put("tfrac", TexUIPlugin.PLUGIN_ID + "/image/obj/comb-frac"); //$NON-NLS-1$ //$NON-NLS-2$
		
		fCommandImages = commandMap;
	}
	
	
	public synchronized IPreferenceStore getEditorPreferenceStore() {
		if (fEditorPreferenceStore == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fEditorPreferenceStore = CombinedPreferenceStore.createStore(
					getPreferenceStore(),
					EditorsUI.getPreferenceStore() );
		}
		return fEditorPreferenceStore;
	}
	
	public synchronized LtxDocumentProvider getTexDocumentProvider() {
		if (fTexDocumentProvider == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fTexDocumentProvider = new LtxDocumentProvider();
			fDisposables.add(fTexDocumentProvider);
		}
		return fTexDocumentProvider;
	}
	
	public synchronized ContextTypeRegistry getTexEditorTemplateContextTypeRegistry() {
		if (fTexEditorTemplateContextTypeRegistry == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fTexEditorTemplateContextTypeRegistry = new ContributionContextTypeRegistry(TEX_EDITOR_TEMPLATES_ID);
		}
		return fTexEditorTemplateContextTypeRegistry;
	}
	
	public synchronized TemplateStore getTexEditorTemplateStore() {
		if (fTexEditorTemplateStore == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fTexEditorTemplateStore = new ContributionTemplateStore(
					getTexEditorTemplateContextTypeRegistry(), getPreferenceStore(), TEX_EDITOR_TEMPLATES_ID);
			try {
				fTexEditorTemplateStore.load();
			}
			catch (final IOException e) {
				getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"Error occured when loading 'LaTeX Editor' template store.", e)); 
			}
		}
		return fTexEditorTemplateStore;
	}
	
	public synchronized ContentAssistComputerRegistry getTexEditorContentAssistRegistry() {
		if (fTexEditorContentAssistRegistry == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fTexEditorContentAssistRegistry = new ContentAssistComputerRegistry("de.walware.docmlet.tex.contentTypes.Ltx", 
					TEX_EDITOR_QUALIFIER, TEX_EDITOR_ASSIST_REGISTRY_GROUP_ID); 
			fDisposables.add(fTexEditorContentAssistRegistry);
		}
		return fTexEditorContentAssistRegistry;
	}
	
	public Map<String, String> getCommandImages() {
		getImageRegistry();
		return fCommandImages;
	}
	
	
}
