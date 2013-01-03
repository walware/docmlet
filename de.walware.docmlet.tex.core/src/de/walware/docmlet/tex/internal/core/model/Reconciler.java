/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.SourceContent;
import de.walware.ecommons.ltk.SourceContentLines;
import de.walware.ecommons.text.FixInterningStringCache;
import de.walware.ecommons.text.LineInformationCreator;
import de.walware.ecommons.text.SourceParseInput;
import de.walware.ecommons.text.StringParseInput;

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


public class Reconciler {
	
	
	protected static class Data {
		
		public final LtxSuModelContainer<?> adapter;
		public final ILtxSuModelContainerEmbeddedExtension embedded;
		public final SourceContent content;
		
		public SourceParseInput parseInput;
		public int parseOffset;
		
		private SourceContentLines contentLines;
		
		public AstInfo ast;
		
		public Map<String, TexCommand> customCommands;
		public Map<String, TexCommand> customEnvs;
		
		public ILtxModelInfo oldModel;
		public ILtxModelInfo newModel;
		
		public Data(final LtxSuModelContainer<?> adapter, final IProgressMonitor monitor) {
			this.adapter = adapter;
			this.embedded = (adapter instanceof ILtxSuModelContainerEmbeddedExtension) ?
					(ILtxSuModelContainerEmbeddedExtension) adapter : null;
			this.content = adapter.getParseContent(monitor);
		}
		
	}
	
	
	private final LtxModelManager fManager;
	protected boolean fStop = false;
	
	private final LineInformationCreator fLineInformationCreator = new LineInformationCreator();
	
	private final Object f1AstLock = new Object();
	private final NowebLtxLexer f1Lexer = new NowebLtxLexer(new FixInterningStringCache(24));
	private final LtxParser f1Parser = new LtxParser(f1Lexer, new FixInterningStringCache(24));
	
	private final Object f2ModelLock = new Object();
	private final SourceAnalyzer f2SourceAnalyzer = new SourceAnalyzer();
	
	private final Object f3ReportLock = new Object();
	private final LtxProblemReporter f3ProblemReporter = new LtxProblemReporter();
	
	
	public Reconciler(final LtxModelManager manager) {
		fManager = manager;
	}
	
	
	protected SourceContentLines getContentLines(final Data data) {
		if (data.contentLines == null) {
			synchronized (fLineInformationCreator) {
				data.contentLines = new SourceContentLines(data.content,
						fLineInformationCreator.create(data.content.text) );
			}
		}
		return data.contentLines;
	}
	
	protected void reconcile(final LtxSuModelContainer<?> adapter,
			final int level, final IProgressMonitor monitor) {
		final ILtxSourceUnit su = adapter.getSourceUnit();
		final Data data = new Data(adapter, monitor);
		
		synchronized (f1AstLock) {
			if (fStop || monitor.isCanceled()) {
				return;
			}
			data.ast = data.adapter.getCurrentAst(data.content.stamp);
			
			if (data.ast == null) {
				final SourceComponent sourceNode;
				final StringParseInput input = new StringParseInput(data.content.text);
				
				if (data.embedded != null) {
					f1Lexer.setNowebType(data.embedded.getNowebType());
					f1Parser.setCollectEmebeddedNodes(true);
					
					sourceNode = f1Parser.parse(input, su.getTexCoreAccess().getTexCommandSet());
					data.customCommands = f1Parser.getCustomCommandMap();
					data.customEnvs = f1Parser.getCustomEnvMap();
					
					final List<Embedded> embeddedNodes = f1Parser.getEmbeddedNodes();
					
					data.embedded.reconcileEmbeddedAst(data.content, embeddedNodes, level, monitor);
				}
				else {
					sourceNode = f1Parser.parse(input, su.getTexCoreAccess().getTexCommandSet());
				}
				data.ast = new AstInfo(1, data.content.stamp, sourceNode);
				
				synchronized (data.adapter) {
					data.adapter.setAst(data.ast);
				}
			}
		}
		
		if (fStop || monitor.isCanceled()
				|| (level & 0xf) < IModelManager.MODEL_FILE) {
			return;
		}
		
		boolean updated;
		synchronized (f2ModelLock) {
			if (fStop || monitor.isCanceled()) {
				return;
			}
			updated = updateModel(data, level, monitor);
			
			if (updated) {
				fManager.getEventJob().addUpdate(su, data.oldModel, data.newModel);
			}
		}
		
		if ((level & IModelManager.RECONCILER) != 0 && data.newModel != null) {
			if (fStop || monitor.isCanceled()) {
				return;
			}
			
			IProblemRequestor problemRequestor = null;
			synchronized (f3ReportLock) {
				if (!fStop && !monitor.isCanceled()
						&& data.newModel == adapter.getCurrentModel() ) {
					problemRequestor = adapter.createProblemRequestor(data.ast.stamp);
					if (problemRequestor != null) {
						f3ProblemReporter.run(su, getContentLines(data), data.newModel,
								problemRequestor, level, monitor);
						
						if (data.embedded != null) {
							data.embedded.reportEmbeddedProblems(getContentLines(data), data.newModel, 
									problemRequestor, level, monitor);
						}
					}
				}
				if (problemRequestor != null) {
					problemRequestor.finish();
				}
			}
		}
	}
	
	protected final boolean updateModel(final Data data, final int level, final IProgressMonitor monitor) {
		data.newModel = data.adapter.getCurrentModel(data.ast.stamp);
		if (data.newModel == null) {
			final LtxSourceModelInfo model = f2SourceAnalyzer.createModel(data.adapter.getSourceUnit(),
					data.content.text, data.ast, data.customCommands, data.customEnvs );
			final boolean isOK = (model != null);
			
			if (data.embedded != null) {
				data.embedded.reconcileEmbeddedModel(data.content, model, f2SourceAnalyzer.getEmbeddedItems(),
						level, monitor);
			}
			
			if (isOK) {
				synchronized (data.adapter) {
					data.oldModel = data.adapter.getCurrentModel();
					data.adapter.setModel(model);
				}
				data.newModel = model;
				return true;
			}
		}
		return false;
	}
	
	void stop() {
		fStop = true;
	}
	
}
