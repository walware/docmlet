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

package de.walware.docmlet.tex.core.commands;


public final class Argument {
	
	
	public static final byte NONE=                          0b00000000;
	
	public static final byte REQUIRED=                      0b00000001;
	public static final byte OPTIONAL=                      0b00000010;
//	public static final byte ROUND=                         0b00000100;
	
	public static final byte TITLE=                         (byte) 0x11;
	
	public static final byte CONTROLWORD=                   (byte) 0x20;
	
	public static final byte LABEL=                         (byte) 0x30;
	public static final byte LABEL_ENV=                     (byte) 0x31;
	public static final byte LABEL_REFLABEL_DEF=            (byte) 0x32;
	public static final byte LABEL_REFLABEL_REF=            (byte) 0x33;
	public static final byte LABEL_COUNTER_DEF=             (byte) 0x34;
	public static final byte LABEL_COUNTER_SET=             (byte) 0x35;
	public static final byte LABEL_COUNTER_REF=             (byte) 0x36;
	public static final byte LABEL_BIB_DEF=                 (byte) 0x38;
	public static final byte LABEL_BIB_REF=                 (byte) 0x39;
	
	public static final byte RESOURCE=                      (byte) 0x40;
	public static final byte RESOURCE_SINGLE=               (byte) 0x41;
	public static final byte RESOURCE_LIST=                 (byte) 0x42;
	
	public static final byte NUM=                           (byte) 0x50;
	
	public static final byte POS=                           (byte) 0x7f;
	public static final byte LOC=                           (byte) 0x7f;
	
	public static final byte EMBEDDED=                      (byte) 0xf1;
	
	
	private final String label;
	private final byte type;
	private final byte content;
	
	
	public Argument(final byte type, final byte content) {
		this.label= null;
		this.type= type;
		this.content= content;
	}
	
	public Argument(final String label, final byte type, final byte content) {
		this.label= label;
		this.type= type;
		this.content= content;
	}
	
	
	public byte getType() {
		return this.type;
	}
	
	public boolean isRequired() {
		return ((this.type & REQUIRED) != 0);
	}
	
	public boolean isOptional() {
		return ((this.type & OPTIONAL) != 0);
	}
	
	public byte getContent() {
		return this.content;
	}
	
	
	@Override
	public String toString() {
		return String.format("%s (type= 0x%02x, content= 0x%02x)", //$NON-NLS-1$
				this.label, this.type, this.content);
	}
	
}
