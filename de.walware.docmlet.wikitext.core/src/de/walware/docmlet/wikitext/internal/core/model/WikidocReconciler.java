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

package de.walware.docmlet.wikitext.internal.core.model;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.AbstractDocument;

import de.walware.jcommons.string.InternStringCache;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.model.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.core.util.AstPrinter;

import de.walware.docmlet.tex.core.model.TexModel;

import de.walware.eutils.yaml.core.model.YamlModel;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.MarkupSourceModelStamp;
import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.ast.SourceComponent;
import de.walware.docmlet.wikitext.core.ast.WikidocParser;
import de.walware.docmlet.wikitext.core.ast.WikitextAstInfo;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;
import de.walware.docmlet.wikitext.core.model.EmbeddingReconcileItem;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikidocSuModelContainerEmbeddedExtension;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikidocSuModelContainer;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;


public class WikidocReconciler {
	
	
	protected static class Data {
		
		public final WikidocSuModelContainer<?> adapter;
		public final IWikidocSuModelContainerEmbeddedExtension embedded;
		public final SourceContent content;
		
		private final IWikitextCoreAccess coreAccess;
		
		public int parseOffset;
		
		public WikitextAstInfo ast;
		
		public IWikidocModelInfo oldModel;
		public IWikidocModelInfo newModel;
		
		public Data(final WikidocSuModelContainer<?> adapter, final IProgressMonitor monitor) {
			this.adapter= adapter;
			this.embedded= (adapter instanceof IWikidocSuModelContainerEmbeddedExtension) ?
					(IWikidocSuModelContainerEmbeddedExtension) adapter : null;
			this.content= adapter.getParseContent(monitor);
			this.coreAccess= adapter.getSourceUnit().getWikitextCoreAccess();
		}
		
	}
	
	private static final boolean DEBUG_LOG_AST= "true".equalsIgnoreCase( //$NON-NLS-1$
			Platform.getDebugOption("de.walware.docmlet.wikitext/debug/Reconciler/logAst")); //$NON-NLS-1$
	
	
	private final Map<IMarkupLanguage, IMarkupLanguage> languages= new HashMap<>();
	
	private final WikitextModelManager modelManager;
	protected boolean stop= false;
	
	private final IMarkupLanguageManager1 markupLanguageManager;
	
	private final Object f1AstLock= new Object();
	private final WikidocParser f1Parser= new WikidocParser(new InternStringCache(0x20));
	private AstPrinter f1DebugAstPrinter;
	
	private final Object f2ModelLock= new Object();
	private final SourceAnalyzer f2SourceAnalyzer= new SourceAnalyzer();
	
	private final Object f3ReportLock= new Object();
//	private final WikidocProblemReporter f3ProblemReporter= new WikidocProblemReporter();
	
	private IWikidocSuModelContainerEmbeddedExtension yamlExt;
	private IWikidocSuModelContainerEmbeddedExtension ltxExt;
	
	
	public WikidocReconciler(final WikitextModelManager manager) {
		this.modelManager= manager;
		this.markupLanguageManager= WikitextCore.getMarkupLanguageManager();
		
		this.f1Parser.setCollectHeadingText(true);
	}
	
	
	public void reconcile(final WikidocSuModelContainer<?> adapter, final int flags,
			final IProgressMonitor monitor) {
		final IWikitextSourceUnit su= adapter.getSourceUnit();
		final Data data= new Data(adapter, monitor);
		if (data.content == null) {
			return;
		}
		
		synchronized (this.f1AstLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			updateAst(data, flags, monitor);
		}
		
		if (this.stop || monitor.isCanceled()
				|| (flags & 0xf) < IModelManager.MODEL_FILE) {
			return;
		}
		
		synchronized (this.f2ModelLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			final boolean updated= updateModel(data, flags, monitor);
			
			if (updated) {
				this.modelManager.getEventJob().addUpdate(su, data.oldModel, data.newModel);
			}
		}
		
		if ((flags & IModelManager.RECONCILE) != 0 && data.newModel != null) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			
			IProblemRequestor problemRequestor= null;
			synchronized (this.f3ReportLock) {
				if (!this.stop && !monitor.isCanceled()
						&& data.newModel == adapter.getCurrentModel() ) {
					problemRequestor= adapter.createProblemRequestor();
					if (problemRequestor != null) {
						
						if (data.ast.getMarkupLanguage() instanceof IExtdocMarkupLanguage) {
							((IExtdocMarkupLanguage) data.ast.getMarkupLanguage()).getProblemReporter()
									.run(su, data.content, data.newModel,
											problemRequestor, flags, monitor );
						}
						
						if (data.ast.getEmbeddedTypes().contains(YamlModel.YAML_TYPE_ID)) {
							this.yamlExt.reportEmbeddedProblems(data.content, data.newModel,
									problemRequestor, flags, monitor);
						}
						
						if (data.embedded != null) {
							data.embedded.reportEmbeddedProblems(data.content, data.newModel,
									problemRequestor, flags, monitor);
						}
					}
				}
				if (problemRequestor != null) {
					problemRequestor.finish();
				}
			}
		}
	}
	
	protected final IMarkupLanguage getMarkupLanguage(final Data data, final IProgressMonitor monitor) {
		final IWikitextSourceUnit su= data.adapter.getSourceUnit();
		
		IMarkupLanguage markupLanguage= null;
		if (su.getWorkingContext() == LTK.EDITOR_CONTEXT) {
			final AbstractDocument document= su.getDocument(monitor);
			markupLanguage= MarkupLanguageDocumentSetupParticipant.getMarkupLanguage(document, su.getDocumentContentInfo().getPartitioning());
		}
		if (markupLanguage == null && su instanceof IWorkspaceSourceUnit) {
			markupLanguage= this.markupLanguageManager.getLanguage((IFile) su.getResource(),
					null, true );
		}
		if (markupLanguage == null) {
			return null;
		}
		synchronized (this.languages) {
			IMarkupLanguage internal= this.languages.get(markupLanguage);
			if (internal == null) {
				internal= markupLanguage.clone("Reconciler", markupLanguage.getMode());
				this.languages.put(internal, internal);
			}
			return internal;
		}
	}
	
	protected final void updateAst(final Data data, final int flags,
			final IProgressMonitor monitor) {
		final IMarkupLanguage markupLanguage= getMarkupLanguage(data, monitor);
		if (markupLanguage == null) {
			throw new UnsupportedOperationException("Markup language is missing.");
		}
		final MarkupSourceModelStamp stamp= new MarkupSourceModelStamp(data.content.getStamp(), markupLanguage);
		
		data.ast= (WikitextAstInfo) data.adapter.getCurrentAst();
		if (data.ast != null && !stamp.equals(data.ast.getStamp())) {
			data.ast= null;
		}
		
		if (data.ast == null) {
			final SourceComponent sourceNode;
//			final StringParseInput input= new StringParseInput(data.content.text);
//			
			this.f1Parser.setMarkupLanguage(markupLanguage);
			this.f1Parser.setCollectEmebeddedNodes(true);
			
			sourceNode= this.f1Parser.parse(data.content);
			
			final List<Embedded> embeddedNodes= this.f1Parser.getEmbeddedNodes();
			final Map<String, Boolean> embeddedTypes= new IdentityHashMap<>(4);
			for (final Embedded node : embeddedNodes) {
				embeddedTypes.put(node.getForeignTypeId(), Boolean.TRUE);
			}
			
			if (embeddedTypes.containsKey(YamlModel.YAML_TYPE_ID)) {
				if (this.yamlExt == null) {
					this.yamlExt= new YamlReconcilerExtension();
				}
				this.yamlExt.reconcileEmbeddedAst(data.content, embeddedNodes, markupLanguage,
						flags, monitor );
			}
			if (embeddedTypes.containsKey(TexModel.LTX_TYPE_ID)) {
				if (this.ltxExt == null) {
					this.ltxExt= new LtxReconcilerExtension();
				}
				this.ltxExt.reconcileEmbeddedAst(data.content, embeddedNodes, markupLanguage,
						flags, monitor );
			}
			if (data.embedded != null) {
				data.embedded.reconcileEmbeddedAst(data.content, embeddedNodes, markupLanguage,
						flags, monitor );
			}
			
			data.ast= new WikitextAstInfo(1, stamp, sourceNode, markupLanguage,
					embeddedTypes.keySet() );
			
			if (DEBUG_LOG_AST) {
				if (this.f1DebugAstPrinter == null) {
					this.f1DebugAstPrinter= new AstPrinter(new StringWriter());
				}
				final StringWriter out= (StringWriter) this.f1DebugAstPrinter.getWriter();
				out.getBuffer().setLength(0);
				try {
					out.append("====\nWikidoc AST:\n"); //$NON-NLS-1$
					this.f1DebugAstPrinter.print(data.ast.root, data.content.getText());
					out.append("====\n"); //$NON-NLS-1$
					System.out.println(out.toString());
				}
				catch (final Exception e) {
					System.out.println(out.toString());
					e.printStackTrace();
				}
			}
			
			synchronized (data.adapter) {
				data.adapter.setAst(data.ast);
			}
		}
	}
	
	protected final boolean updateModel(final Data data, final int flags,
			final IProgressMonitor monitor) {
		data.newModel= data.adapter.getCurrentModel();
		if (data.newModel != null && !data.ast.getStamp().equals(data.newModel.getStamp())) {
			data.newModel= null;
		}
		
		if (data.newModel == null) {
			final WikidocSourceUnitModelInfo model= this.f2SourceAnalyzer.createModel(data.adapter.getSourceUnit(),
					data.content.getText(), data.ast );
			final boolean isOK= (model != null);
			
			final List<EmbeddingReconcileItem> embeddedItems= this.f2SourceAnalyzer.getEmbeddedItems();
			
			if (data.ast.getEmbeddedTypes().contains(YamlModel.YAML_TYPE_ID)) {
				this.yamlExt.reconcileEmbeddedModel(data.content, model, embeddedItems,
						flags, monitor );
			}
			if (data.ast.getEmbeddedTypes().contains(TexModel.LTX_TYPE_ID)) {
				this.ltxExt.reconcileEmbeddedModel(data.content, model, embeddedItems,
						flags, monitor );
			}
			if (data.embedded != null) {
				data.embedded.reconcileEmbeddedModel(data.content, model, embeddedItems,
						flags, monitor );
			}
			
			if (isOK) {
				synchronized (data.adapter) {
					data.oldModel= data.adapter.getCurrentModel();
					data.adapter.setModel(model);
				}
				data.newModel= model;
				return true;
			}
		}
		return false;
	}
	
	
	void stop() {
		this.stop= true;
	}
	
}
