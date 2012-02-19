/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.PreferencesUtil;

import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.commands.TexCommandSet;


public class TexCorePreferenceInitializer extends AbstractPreferenceInitializer {
	
	
	public TexCorePreferenceInitializer() {
	}
	
	
	@Override
	public void initializeDefaultPreferences() {
		final DefaultScope defaultScope = new DefaultScope();
		final Map<Preference, Object> defaults = new HashMap<Preference, Object>();
		
		{	final IEclipsePreferences node = defaultScope.getNode(TexCommandSet.QUALIFIER);
			node.put(TexCommandSet.MASTER_COMMANDS_INCLUDE_PREF.getKey(),
					"{,},#,%," +
					"AAA:1,acute,addcontentsline,addtocounter,adots,aleph,Alph,alph,alpha,amalg,angle,approx,approxeq,arabic,arccos,arcsin,arctan,arg,ast,asymp,author," +
					"backprime,backslash,bar,begin,beta,beth,bf,bfseries,bibitem,bibliography,bibliographystyle,bigcap,bigcup,bigodot,bigoplus,bigotimes,bigsqcup,biguplus,bigvee,bigwedge,blacklozenge,blacksquare,blacktriangle,blacktriangledown,bot,bowtie,breve,bullet," +
					"cap,caption,cdot,cdots,chapter,check,chi,circ,cite,clubsuit,complement,cong,coprod,cos,cosh,cot,coth,csc,cup," +
					"dag,dagger,daleth,dashv,date,ddag,ddagger,dddot,ddot,ddots,deg,Delta,delta,det,diamond,diamondsuit,dim,div,documentclass,dot,dots,Downarrow,downarrow," +
					"ell,em,emph,emptyset,end,ensuremath,epsilon,eqref,eqsim,eqslantgtr,eqslantless,equiv,eta,eth,exists,exp," +
					"flat,forall,frac,frown," +
					"Gamma,gamma,gcd,geq,geqq,geqslant,gg,gggtr,gimel,grave,gtrapprox,gtrdot,gtreqless,gtrless,gtrsim," +
					"hat,hbar,heartsuit,hline,hom,hookleftarrow,hookrightarrow,hslash,hspace,Huge,hyphenation," +
					"Im,imath,in,includegraphics,index,inf,infty,input,insert,insertonly,int,iota,it,item,itshape," +
					"jmath," +
					"kappa,ker," +
					"label,Lambda,lambda,land,langle,Large,LaTeX,lbrace,lbrack,lceil,ldots,Leftarrow,leftarrow,leftharpoondown,leftharpoonup,Leftrightarrow,leftrightarrow,leq,leqq,leqslant,lessapprox,lessdot,lesseqgtr,lessgtr,lesssim,lfloor,lg,lim,liminf,limsup,listoffigures,listoftables,ll,llless,ln,log,Longleftarrow,longleftarrow,Longleftrightarrow,longleftrightarrow,longmapsto,Longrightarrow,longrightarrow,lor,lozenge," +
					"maketitle,mapsto,mathbf,mathcal,mathit,mathnormal,mathrm,mathsf,mathtt,max,mdseries,measuredangle,mho,mid,min,models,mp,mu," +
					"nabla,natural,nearrow,neg,newcommand,newcounter,newenvironment,nexists,ni,nocite,nonumber,normalsize,nu,nwarrow," +
					"odot,oint,Omega,omega,ominus,oplus,oslash,otimes,overline," +
					"pageref,paragraph,parallel,part,partial,perp,Phi,phi,Pi,pi,pm,Pr,prec,preceq,prime,printindex,prod,propto,providecommand,Psi,psi," +
					"rangle,rbrace,rbrack,rceil,Re,ref,renewcommand,renewenvironment,rfloor,rho,Rightarrow,rightarrow,rightharpoondown,rightharpoonup,rightleftharpoons,rm,rmfamily,Roman,roman," +
					"S,scriptsize,scshape,searrow,sec,section,setcounter,setminus,sffamily,sharp,Sigma,sigma,sim,simeq,sin,sinh,sl,slshape,smile,spadesuit,sphericalangle,sqcap,sqcup,sqrt,sqsubset,sqsubseteq,sqsupset,sqsupseteq,square,SS,ss,star,stepcounter,subparagraph,subsection,subset,subseteq,subsubsection,succ,succeq,sum,sup,supset,supseteq,surd,swarrow," +
					"tableofcontents,tan,tanh,tau,textasciicircum,textasciitilde,textbackslash,textbf,textbullet,textit,textmd,textperiodcentered,textrm,textsc,textsf,textsl,texttt,textup,Theta,theta,tilde,times,tiny,title,top,triangle,triangledown,triangleleft,trianglelefteq,triangleright,trianglerighteq,tt,ttfamily," +
					"underline,Uparrow,uparrow,Updownarrow,updownarrow,uplus,upshape,upsilon,usepackage," +
					"value,varepsilon,varkappa,varnothing,varphi,varpi,varrho,varsigma,vartheta,vartriangleleft,vartriangleright,Vdash,vDash,vdash,vdots,vec,vee,verb,vspace,Vvdash," +
					"wedge,widehat,widetilde,wp,wr," +
					"Xi,xi," +
					"zeta" );
			node.put(TexCommandSet.PREAMBLE_INCLUDE_PREF.getKey(),
					"author,date," +
					"documentclass," +
					"ensuremath," +
					"insertonly," +
					"hyphenation," +
					"newcommand,newenvironment," +
					"providecommand," +
					"renewcommand,renewenvironment," +
					"usepackage" );
			node.put(TexCommandSet.TEXT_COMMANDS_INCLUDE_PREF.getKey(),
					"{,},#,%," +
					"addcontentsline,addtocounter,Alph,alph,arabic," +
					"begin,bf,bfseries,bibitem,bibliography,bibliographystyle," +
					"caption,chapter,cite," +
					"dag,ddag,dots," +
					"em,emph,end,eqref," +
					"hline,hspace,Huge,huge," +
					"includegraphics,index,input,insert,it,item,itshape," +
					"label,LARGE,Large,large,LaTeX,ldots,listoffigures,listoftables," +
					"maketitle,mdseries," +
					"newcounter,nocite,normalsize," +
					"pageref,paragraph,part,printindex," +
					"ref,rm,rmfamily,Roman,roman," +
					"S,scriptsize,scshape,section,setcounter,sffamily,sl,slshape,small,SS,ss,stepcounter,subparagraph,subsection,subsubsection," +
					"tableofcontents,TeX,textasciicircum,textasciitilde,textbackslash,textbf,textbullet,textit,textmd,textperiodcentered,textrm,textsc,textsf,textsl,texttt,textup,tiny,tt,ttfamily," +
					"underline,upshape," +
					"value,verb,vspace" );
			node.put(TexCommandSet.MATH_COMMANDS_INCLUDE_PREF.getKey(),
					"{,},#,%," +
					"abs,acute,adots,aleph,Alpha,alpha,amalg,angle,approx,approxeq,arccos,arcsin,arctan,arg,ast,asymp," +
					"backprime,backslash,bar,begin,Beta,beta,beth,bigcap,bigcup,bigodot,bigoplus,bigotimes,bigsqcup,biguplus,bigvee,bigwedge,blacklozenge,blacksquare,blacktriangle,blacktriangledown,bot,bowtie,breve,bullet," +
					"cap,cdot,cdots,check,Chi,chi,circ,clubsuit,complement,cong,coprod,cos,cosh,cot,coth,csc,cup," +
					"dag,dagger,daleth,dashv,ddag,ddagger,dddot,ddot,ddots,deg,Delta,delta,det,dfrac,diamond,diamondsuit,dim,div,dot,dots,dotsb,dotsc,dotsi,dotsm,dotso,Downarrow,downarrow," +
					"ell,emptyset,end,Epsilon,epsilon,eqref,eqsim,eqslantgtr,eqslantless,equiv,Eta,eta,eth,exists,exp," +
					"Finv,flat,forall,frac,frown," +
					"Gamma,gamma,gcd,geq,geqq,geqslant,gg,gggtr,gimel,grave,gtrapprox,gtrdot,gtreqless,gtrless,gtrsim," +
					"hat,hbar,heartsuit,hom,hookleftarrow,hookrightarrow,hslash,hspace," +
					"Im,imath,in,inf,infty,int,Iota,iota," +
					"jmath," +
					"Kappa,kappa,ker," +
					"label,Lambda,lambda,land,langle,lbrace,lbrack,lceil,ldots,Leftarrow,leftarrow,leftharpoondown,leftharpoonup,Leftrightarrow,leftrightarrow,leq,leqq,leqslant,lessapprox,lessdot,lesseqgtr,lessgtr,lesssim,lfloor,lg,lim,liminf,limsup,ll,llless,ln,log,Longleftarrow,longleftarrow,Longleftrightarrow,longleftrightarrow,longmapsto,Longrightarrow,longrightarrow,lor,lozenge," +
					"mapsto,mathbf,mathcal,mathit,mathnormal,mathrm,mathsf,mathtt,max,measuredangle,mho,mid,min,models,mp,Mu,mu," +
					"nabla,natural,nearrow,neg,nexists,ni,nonumber,norm,Nu,nu,nwarrow," +
					"odot,oint,Omega,omega,Omicron,omicron,ominus,oplus,oslash,otimes,overline," +
					"pageref,parallel,partial,perp,Phi,phi,Pi,pi,pm,Pr,prec,preceq,prime,prod,propto,Psi,psi," +
					"rangle,rbrace,rbrack,rceil,Re,ref,rfloor,Rho,rho,Rightarrow,rightarrow,rightharpoondown,rightharpoonup,rightleftharpoons," +
					"searrow,sec,setminus,sharp,Sigma,sigma,sim,simeq,sin,sinh,smile,spadesuit,sphericalangle,sqcap,sqcup,sqrt,sqsubset,sqsubseteq,sqsupset,sqsupseteq,square,star,subset,subseteq,succ,succeq,sum,sup,supset,supseteq,surd,swarrow," +
					"tan,tanh,Tau,tau,tfrac,Theta,theta,tilde,times,top,triangle,triangledown,triangleleft,trianglelefteq,triangleright,trianglerighteq," +
					"underline,Uparrow,uparrow,Updownarrow,updownarrow,uplus,Upsilon,upsilon," +
					"varepsilon,varkappa,varnothing,varphi,varpi,varrho,varsigma,vartheta,vartriangleleft,vartriangleright,Vdash,vDash,vdash,vdots,vec,vee,verb,vspace,Vvdash," +
					"wedge,widehat,widetilde,wp,wr," +
					"Xi,xi," +
					"Zeta,zeta" );
			
			node.put(TexCommandSet.TEXT_ENVS_INCLUDE_PREF.getKey(),
					"align,align*,alignat,alignat*," +
					"center,comment," +
					"description,displaymath,document," +
					"enumerate,eqnarray,eqnarray*,equation,equation*," +
					"gather,gather*," +
					"figure,flushleft,flushright," +
					"itemize,lstlisting," +
					"math,multiline,multiline*," +
					"picture," +
					"quotation,quote," +
					"tabbing,table,tabular,thebibliography," +
					"verbatim,verbatim*,verse," );
			node.put(TexCommandSet.MATH_ENVS_INCLUDE_PREF.getKey(),
					"array," +
					"Bmatrix,bmatrix," +
					"matrix," +
					"pmatrix," +
					"smallmatrix," +
					"Vmatrix,vmatrix" );
		}
		
		new TexCodeStyleSettings(0).deliverToPreferencesMap(defaults);
		
		for (final Entry<Preference, Object> entry : defaults.entrySet()) {
			PreferencesUtil.setPrefValue(defaultScope, entry.getKey(), entry.getValue());
		}
	}
	
}
