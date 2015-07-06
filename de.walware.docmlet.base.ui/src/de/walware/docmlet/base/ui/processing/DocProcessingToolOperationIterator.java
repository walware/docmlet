/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import java.util.List;

import de.walware.ecommons.collections.ImList;

import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;


public class DocProcessingToolOperationIterator implements Comparable<DocProcessingToolOperationIterator> {
	
	
	public static final byte PRE= 1;
	public static final byte MAIN= 2;
	public static final byte POST= 3;
	
	
	private final ImList<StepConfig> steps;
	
	private int stepIdx;
	private StepConfig stepConfig;
	private byte stepPart;
	private List<DocProcessingOperation> stepPartList;
	private int stepPartIdx;
	
	private DocProcessingOperation operation;
	
	
	public DocProcessingToolOperationIterator(final ImList<StepConfig> steps) {
		this.steps= steps;
		reset();
	}
	
	
	@Override
	public int compareTo(final DocProcessingToolOperationIterator other) {
		int diff= this.stepIdx - other.stepIdx;
		if (diff == 0) {
			diff= this.stepPart - other.stepPart;
			if (diff == 0) {
				diff= this.stepPartIdx - other.stepPartIdx;
			}
		}
		return diff;
	}
	
	public void reset() {
		this.stepIdx= -1;
		this.stepConfig= null;
		this.stepPart= -1;
		this.stepPartList= null;
		this.stepPartIdx= -1;
		this.operation= null;
	}
	
	public void reset(final DocProcessingToolOperationIterator other) {
		this.stepIdx= other.stepIdx;
		this.stepConfig= other.stepConfig;
		this.stepPart= other.stepPart;
		this.stepPartList= other.stepPartList;
		this.stepPartIdx= other.stepPartIdx;
		this.operation= other.operation;
	}
	
	public boolean hasNext() {
		if (this.stepIdx < 0) {
			return nextStep();
		}
		return (this.operation != null);
	}
	
	public boolean next() {
		if (this.stepIdx < 0) {
			return nextStep();
		}
		if (this.stepIdx == Integer.MAX_VALUE) {
			return false;
		}
		
		this.operation= null;
		return (nextInPart() || nextPart() || nextStep());
	}
	
	
	private boolean nextStep() {
		while (++this.stepIdx < this.steps.size()) {
			this.stepConfig= this.steps.get(this.stepIdx);
			if (this.stepConfig.isRun() && nextPart()) {
				return true;
			}
		}
		this.stepIdx= Integer.MAX_VALUE;
		this.stepConfig= null;
		return false;
	}
	
	private boolean nextPart() {
		while (true) {
			switch (++this.stepPart) {
			case 0:
				break;
			case PRE:
				this.stepPartList= this.stepConfig.getPre();
				if (nextInPart()) {
					return true;
				}
				break;
			case MAIN:
				this.operation= this.stepConfig.getOperation();
				if (this.operation != null) {
					return true;
				}
				break;
			case POST:
				this.stepPartList= this.stepConfig.getPost();
				if (nextInPart()) {
					return true;
				}
				break;
			default:
				this.stepPart= -1;
				return false;
			}
		}
	}
	
	private boolean nextInPart() {
		if (this.stepPartList != null) {
			if (++this.stepPartIdx < this.stepPartList.size()) {
				this.operation= this.stepPartList.get(this.stepPartIdx);
				return true;
			}
			this.stepPartIdx= -1;
			this.stepPartList= null;
		}
		return false;
	}
	
	public int getStepIdx() {
		return this.stepIdx;
	}
	
	public StepConfig getStepConfig() {
		return this.stepConfig;
	}
	
	public byte getStepPart() {
		return this.stepPart;
	}
	
	public DocProcessingOperation getOperation() {
		return this.operation;
	}
	
	
}