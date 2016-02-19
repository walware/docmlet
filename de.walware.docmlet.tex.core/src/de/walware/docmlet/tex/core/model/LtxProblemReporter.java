/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.internal.core.model.LtxProblemAstVisitor;
import de.walware.docmlet.tex.internal.core.model.LtxProblemModelCheck;


public class LtxProblemReporter {
	
	
	private final LtxProblemAstVisitor astVisitor = new LtxProblemAstVisitor();
	private final LtxProblemModelCheck modelCheck = new LtxProblemModelCheck();
	
	
	public LtxProblemReporter() {
	}
	
	
	public void run(final ITexSourceUnit su, final SourceContent content, 
			final ILtxModelInfo model,
			final IProblemRequestor requestor, final int level, final IProgressMonitor monitor) {
		final AstInfo ast = model.getAst();
		if (ast.root instanceof TexAstNode) {
			this.astVisitor.run(su, content, (TexAstNode) ast.root, requestor);
		}
		this.modelCheck.run(su, content, model, requestor);
	}
	
}
