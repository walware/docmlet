/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.ast;


public interface ITexAstStatusConstants {
	
	
	int STATUS_INCOMPLETE_ENV=                             0x00000011;
	int STATUS2_ENV_MISSING_NAME=                          STATUS_INCOMPLETE_ENV | 0x00001100;
	int STATUS2_ENV_NOT_CLOSED=                            STATUS_INCOMPLETE_ENV | 0x00002100;
	int STATUS2_ENV_NOT_OPENED=                            STATUS_INCOMPLETE_ENV | 0x00002200;
	int STATUS2_ENV_UNKNOWN_ENV=                           STATUS_INCOMPLETE_ENV | 0x00003100;
	
	int STATUS_INCOMPLETE_VERBATIM=                        0x00000012;
	int STATUS2_VERBATIM_INLINE_C_MISSING=                 STATUS_INCOMPLETE_VERBATIM | 0x00001100;
	int STATUS2_VERBATIM_INLINE_NOT_CLOSED=                STATUS_INCOMPLETE_VERBATIM | 0x00002100;
	
	int STATUS_INCOMPLETE_MATH=                            0x00000013;
	int STATUS2_MATH_NOT_CLOSED=                           STATUS_INCOMPLETE_MATH | 0x00002100;
	
	int STATUS_INCOMPLETE_GROUP=                           0x00000014;
	int STATUS2_GROUP_NOT_CLOSED=                          STATUS_INCOMPLETE_GROUP | 0x00002100;
	int STATUS2_GROUP_NOT_OPENED=                          STATUS_INCOMPLETE_GROUP | 0x00002200;
	
	int STATUS_MISSING_SILENT= 0xf0000000; //?
	
	
}
