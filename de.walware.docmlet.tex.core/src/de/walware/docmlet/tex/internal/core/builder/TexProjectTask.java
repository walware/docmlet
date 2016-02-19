/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.walware.ecommons.ltk.LTK;

import de.walware.docmlet.tex.core.TexBuildParticipant;
import de.walware.docmlet.tex.internal.core.TexProject;


public abstract class TexProjectTask {
	
	
	private static final TexBuildParticipant NO_PARTICIPANT= new TexBuildParticipant();
	
	
	private final Map<String, TexBuildParticipant> participants= new HashMap<>();
	
	private final TexProjectBuilder texProjectBuilder;
	private final TexProject texProject;
	
	private int buildType;
	
	
	public TexProjectTask(final TexProjectBuilder projectBuilder) {
		this.texProjectBuilder= projectBuilder;
		this.texProject= projectBuilder.getTexProject();
	}
	
	
	protected final TexProjectBuilder getTexProjectBuilder() {
		return this.texProjectBuilder;
	}
	
	public final TexProject getTexProject() {
		return this.texProject;
	}
	
	public void setBuildType(final int buildType) {
		this.buildType= buildType;
	}
	
	protected final Collection<TexBuildParticipant> getParticipants() {
		final Collection<TexBuildParticipant> values= this.participants.values();
		final List<TexBuildParticipant> list= new ArrayList<>(values.size());
		for (final TexBuildParticipant participant : values) {
			if (participant != null) {
				list.add(participant);
			}
		}
		return list;
	}
	
	protected final TexBuildParticipant getParticipant(final String modelTypeId) {
		if (modelTypeId == null) {
			return null;
		}
		TexBuildParticipant participant= this.participants.get(modelTypeId);
		if (participant == null) {
			participant= loadParticipant(modelTypeId);
			this.participants.put(modelTypeId, participant);
		}
		return (participant != NO_PARTICIPANT) ? participant : null;
	}
	
	private TexBuildParticipant loadParticipant(final String modelTypeId) {
		final TexBuildParticipant participant= (TexBuildParticipant) LTK.getModelAdapter(
				modelTypeId, TexBuildParticipant.class );
		if (participant == null) {
			return NO_PARTICIPANT;
		}
		participant.texProject= getTexProject();
		participant.buildType= buildType;
		participant.enabled= false;
		participant.init();
		return participant;
	}
	
}
