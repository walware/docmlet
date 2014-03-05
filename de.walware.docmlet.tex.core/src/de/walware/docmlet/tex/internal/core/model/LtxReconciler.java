/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.SourceContent;
import de.walware.ecommons.text.FixInterningStringCache;
import de.walware.ecommons.text.SourceParseInput;
import de.walware.ecommons.text.StringParseInput;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.core.ast.LtxParser;
import de.walware.docmlet.tex.core.ast.SourceComponent;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.ILtxSuModelContainerEmbeddedExtension;
import de.walware.docmlet.tex.core.model.LtxProblemReporter;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;
import de.walware.docmlet.tex.core.parser.NowebLtxLexer;


public class LtxReconciler {
	
	
	protected static class Data {
		
		public final LtxSuModelContainer<?> adapter;
		public final ILtxSuModelContainerEmbeddedExtension embedded;
		public final SourceContent content;
		
		private final ITexCoreAccess texCoreAccess;
		
		public SourceParseInput parseInput;
		public int parseOffset;
		
		public AstInfo ast;
		
		public Map<String, TexCommand> customCommands;
		public Map<String, TexCommand> customEnvs;
		
		public ILtxModelInfo oldModel;
		public ILtxModelInfo newModel;
		
		public Data(final LtxSuModelContainer<?> adapter, final IProgressMonitor monitor) {
			this.adapter= adapter;
			this.embedded= (adapter instanceof ILtxSuModelContainerEmbeddedExtension) ?
					(ILtxSuModelContainerEmbeddedExtension) adapter : null;
			this.content= adapter.getParseContent(monitor);
			this.texCoreAccess= adapter.getSourceUnit().getTexCoreAccess();
		}
		
	}
	
	
	private final LtxModelManager texManager;
	protected boolean stop= false;
	
	private final Object f1AstLock= new Object();
	private final NowebLtxLexer f1Lexer= new NowebLtxLexer(new FixInterningStringCache(24));
	private final LtxParser f1Parser= new LtxParser(this.f1Lexer, new FixInterningStringCache(24));
	
	private final Object f2ModelLock= new Object();
	private final SourceAnalyzer f2SourceAnalyzer= new SourceAnalyzer();
	
	private final Object f3ReportLock= new Object();
	private final LtxProblemReporter f3ProblemReporter= new LtxProblemReporter();
	
	
	public LtxReconciler(final LtxModelManager manager) {
		this.texManager= manager;
	}
	
	
	public void reconcile(final LtxSuModelContainer<?> adapter, final int flags,
			final IProgressMonitor monitor) {
		final ILtxSourceUnit unit= adapter.getSourceUnit();
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
				this.texManager.getEventJob().addUpdate(unit, data.oldModel, data.newModel);
			}
		}
		
		if ((flags & IModelManager.RECONCILER) != 0 && data.newModel != null) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			
			IProblemRequestor problemRequestor= null;
			synchronized (this.f3ReportLock) {
				if (!this.stop && !monitor.isCanceled()
						&& data.newModel == adapter.getCurrentModel() ) {
					problemRequestor= adapter.createProblemRequestor(data.ast.stamp);
					if (problemRequestor != null) {
						this.f3ProblemReporter.run(unit, data.content, data.newModel,
								problemRequestor, flags, monitor);
						
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
	
	protected final void updateAst(final Data data, final int flags,
			final IProgressMonitor monitor) {
		data.ast= data.adapter.getCurrentAst(data.content.stamp);
		
		if (data.ast == null) {
			final SourceComponent sourceNode;
			final StringParseInput input= new StringParseInput(data.content.text);
			
			if (data.embedded != null) {
				this.f1Lexer.setNowebType(data.embedded.getNowebType());
				this.f1Parser.setCollectEmebeddedNodes(true);
				
				sourceNode= this.f1Parser.parse(input, data.texCoreAccess.getTexCommandSet());
				data.customCommands= this.f1Parser.getCustomCommandMap();
				data.customEnvs= this.f1Parser.getCustomEnvMap();
				
				final List<Embedded> embeddedNodes= this.f1Parser.getEmbeddedNodes();
				
				data.embedded.reconcileEmbeddedAst(data.content, embeddedNodes, flags, monitor);
			}
			else {
				this.f1Lexer.setNowebType(null);
				this.f1Parser.setCollectEmebeddedNodes(false);
				
				sourceNode= this.f1Parser.parse(input, data.texCoreAccess.getTexCommandSet());
			}
			data.ast= new AstInfo(1, data.content.stamp, sourceNode);
			
			synchronized (data.adapter) {
				data.adapter.setAst(data.ast);
			}
		}
	}
	
	protected final boolean updateModel(final Data data, final int flags,
			final IProgressMonitor monitor) {
		data.newModel= data.adapter.getCurrentModel(data.ast.stamp);
		if (data.newModel == null) {
			final LtxSourceModelInfo model= this.f2SourceAnalyzer.createModel(data.adapter.getSourceUnit(),
					data.content.text, data.ast, data.customCommands, data.customEnvs );
			final boolean isOK= (model != null);
			
			if (data.embedded != null) {
				data.embedded.reconcileEmbeddedModel(data.content, model,
						this.f2SourceAnalyzer.getEmbeddedItems(), flags, monitor );
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
