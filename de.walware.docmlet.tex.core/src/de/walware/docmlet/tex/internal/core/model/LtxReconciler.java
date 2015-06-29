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

package de.walware.docmlet.tex.internal.core.model;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.impl.SourceModelStamp;
import de.walware.ecommons.ltk.core.util.AstPrinter;
import de.walware.ecommons.string.InternStringCache;
import de.walware.ecommons.text.core.input.StringParserInput;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.core.ast.LtxParser;
import de.walware.docmlet.tex.core.ast.SourceComponent;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ILtxSuModelContainerEmbeddedExtension;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.LtxProblemReporter;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;
import de.walware.docmlet.tex.core.parser.NowebLtxLexer;


public class LtxReconciler {
	
	
	protected static class Data {
		
		public final LtxSuModelContainer<?> adapter;
		public final ILtxSuModelContainerEmbeddedExtension embedded;
		public final SourceContent content;
		
		private final ITexCoreAccess texCoreAccess;
		
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
	
	
	private static final boolean DEBUG_LOG_AST= "true".equalsIgnoreCase( //$NON-NLS-1$
			Platform.getDebugOption("de.walware.docmlet.tex/debug/Reconciler/logAst")); //$NON-NLS-1$
	
	
	private final LtxModelManager texManager;
	protected boolean stop= false;
	
	private final Object raLock= new Object();
	private final StringParserInput raInput= new StringParserInput(0x1000);
	private final NowebLtxLexer raLexer= new NowebLtxLexer();
	private final LtxParser raParser= new LtxParser(this.raLexer, new InternStringCache(0x20));
	private AstPrinter raDebugAstPrinter;
	
	private final Object rmLock= new Object();
	private final SourceAnalyzer rmSourceAnalyzer= new SourceAnalyzer();
	
	private final Object rpLock= new Object();
	private final LtxProblemReporter rpReporter= new LtxProblemReporter();
	
	
	public LtxReconciler(final LtxModelManager manager) {
		this.texManager= manager;
	}
	
	
	public void reconcile(final LtxSuModelContainer<?> adapter, final int flags,
			final IProgressMonitor monitor) {
		final ITexSourceUnit su= adapter.getSourceUnit();
		final Data data= new Data(adapter, monitor);
		if (data.content == null) {
			return;
		}
		
		synchronized (this.raLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			updateAst(data, flags, monitor);
		}
		
		if (this.stop || monitor.isCanceled()
				|| (flags & 0xf) < IModelManager.MODEL_FILE) {
			return;
		}
		
		synchronized (this.rmLock) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			final boolean updated= updateModel(data, flags, monitor);
			
			if (updated) {
				this.texManager.getEventJob().addUpdate(su, data.oldModel, data.newModel);
			}
		}
		
		if ((flags & IModelManager.RECONCILE) != 0 && data.newModel != null) {
			if (this.stop || monitor.isCanceled()) {
				return;
			}
			
			IProblemRequestor problemRequestor= null;
			synchronized (this.rpLock) {
				if (!this.stop && !monitor.isCanceled()
						&& data.newModel == adapter.getCurrentModel() ) {
					problemRequestor= adapter.createProblemRequestor();
					if (problemRequestor != null) {
						this.rpReporter.run(su, data.content, data.newModel,
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
		final SourceModelStamp stamp= new SourceModelStamp(data.content.getStamp());
		
		data.ast= data.adapter.getCurrentAst();
		if (data.ast != null && !stamp.equals(data.ast.getStamp())) {
			data.ast= null;
		}
		
		if (data.ast == null) {
			final SourceComponent sourceNode;
			this.raInput.reset(data.content.getText());
			
			if (data.embedded != null) {
				this.raLexer.setNowebType(data.embedded.getNowebType());
				this.raParser.setCollectEmebeddedNodes(true);
				
				sourceNode= this.raParser.parse(this.raInput.init(), data.texCoreAccess.getTexCommandSet());
				data.customCommands= this.raParser.getCustomCommandMap();
				data.customEnvs= this.raParser.getCustomEnvMap();
				
				final List<Embedded> embeddedNodes= this.raParser.getEmbeddedNodes();
				
				data.embedded.reconcileEmbeddedAst(data.content, embeddedNodes, flags, monitor);
			}
			else {
				this.raLexer.setNowebType(null);
				this.raParser.setCollectEmebeddedNodes(false);
				
				sourceNode= this.raParser.parse(this.raInput.init(), data.texCoreAccess.getTexCommandSet());
			}
			data.ast= new AstInfo(1, stamp, sourceNode);
			
			if (DEBUG_LOG_AST) {
				if (this.raDebugAstPrinter == null) {
					this.raDebugAstPrinter= new AstPrinter(new StringWriter());
				}
				final StringWriter out= (StringWriter) this.raDebugAstPrinter.getWriter();
				out.getBuffer().setLength(0);
				try {
					out.append("====\nLtx AST:\n"); //$NON-NLS-1$
					this.raDebugAstPrinter.print(data.ast.root, data.content.getText());
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
			final LtxSourceUnitModelInfo model= this.rmSourceAnalyzer.createModel(data.adapter.getSourceUnit(),
					data.content.getText(), data.ast, data.customCommands, data.customEnvs );
			final boolean isOK= (model != null);
			
			if (data.embedded != null) {
				data.embedded.reconcileEmbeddedModel(data.content, model,
						this.rmSourceAnalyzer.getEmbeddedItems(), flags, monitor );
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
