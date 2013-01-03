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

package de.walware.docmlet.tex.core.commands;


public final class Argument {
	
	
	public static final byte NONE =     0x00;
	
	public static final byte REQUIRED = 0x01;
	public static final byte OPTIONAL = 0x02;
//	public static final byte ROUND =    0x04;
	
	public static final byte TITLE =                        (byte) 0x11;
	
	public static final byte CONTROLWORD =                  (byte) 0x20;
	
	public static final byte LABEL =                        (byte) 0x30;
	public static final byte LABEL_ENV =                    (byte) 0x31;
	public static final byte LABEL_REFLABEL_DEF =           (byte) 0x32;
	public static final byte LABEL_REFLABEL_REF =           (byte) 0x33;
	public static final byte LABEL_COUNTER_DEF =            (byte) 0x34;
	public static final byte LABEL_COUNTER_SET =            (byte) 0x35;
	public static final byte LABEL_COUNTER_REF =            (byte) 0x36;
	public static final byte LABEL_BIB_DEF =                (byte) 0x38;
	public static final byte LABEL_BIB_REF =                (byte) 0x39;
	
	public static final byte RESOURCE =                     (byte) 0x40;
	public static final byte RESOURCE_SINGLE =              (byte) 0x41;
	public static final byte RESOURCE_LIST =                (byte) 0x42;
	
	public static final byte NUM =                          (byte) 0x50;
	
	public static final byte POS =                          (byte) 0x7f;
	public static final byte LOC =                          (byte) 0x7f;
	
	public static final byte EMBEDDED =                     (byte) 0xf1;
	
	
	private final String fLabel;
	private final byte fType;
	private final byte fContent;
	
	
	public Argument(final byte type, final byte content) {
		fLabel = null;
		fType = type;
		fContent = content;
	}
	
	public Argument(final String label, final byte type, final byte content) {
		fLabel = label;
		fType = type;
		fContent = content;
	}
	
	
	public byte getType() {
		return fType;
	}
	
	public boolean isRequired() {
		return ((fType & REQUIRED) != 0);
	}
	
	public boolean isOptional() {
		return ((fType & OPTIONAL) != 0);
	}
	
	public byte getContent() {
		return fContent;
	}
	
}
