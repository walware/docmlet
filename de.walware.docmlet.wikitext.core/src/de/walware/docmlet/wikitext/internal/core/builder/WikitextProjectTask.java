/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.walware.ecommons.ltk.LTK;

import de.walware.docmlet.wikitext.core.WikitextBuildParticipant;
import de.walware.docmlet.wikitext.internal.core.WikitextProject;


public abstract class WikitextProjectTask {
	
	
	private static final WikitextBuildParticipant NO_PARTICIPANT= new WikitextBuildParticipant();
	
	
	private final Map<String, WikitextBuildParticipant> participants= new HashMap<>();
	
	private final WikitextProjectBuilder wikitextProjectBuilder;
	private final WikitextProject wikitextProject;
	
	private int buildType;
	
	
	public WikitextProjectTask(final WikitextProjectBuilder projectBuilder) {
		this.wikitextProjectBuilder= projectBuilder;
		this.wikitextProject= projectBuilder.getWikitextProject();
	}
	
	
	protected final WikitextProjectBuilder getWikitextProjectBuilder() {
		return this.wikitextProjectBuilder;
	}
	
	public final WikitextProject getWikitextProject() {
		return this.wikitextProject;
	}
	
	public void setBuildType(final int buildType) {
		this.buildType= buildType;
	}
	
	protected final Collection<WikitextBuildParticipant> getParticipants() {
		final Collection<WikitextBuildParticipant> values= this.participants.values();
		final List<WikitextBuildParticipant> list= new ArrayList<>(values.size());
		for (final WikitextBuildParticipant participant : values) {
			if (participant != null) {
				list.add(participant);
			}
		}
		return list;
	}
	
	protected final WikitextBuildParticipant getParticipant(final String modelTypeId) {
		if (modelTypeId == null) {
			return null;
		}
		WikitextBuildParticipant participant= this.participants.get(modelTypeId);
		if (participant == null) {
			participant= loadParticipant(modelTypeId);
			this.participants.put(modelTypeId, participant);
		}
		return (participant != NO_PARTICIPANT) ? participant : null;
	}
	
	private WikitextBuildParticipant loadParticipant(final String modelTypeId) {
		final WikitextBuildParticipant participant= (WikitextBuildParticipant) LTK.getModelAdapter(
				modelTypeId, WikitextBuildParticipant.class );
		if (participant == null) {
			return NO_PARTICIPANT;
		}
		participant.wikitextProject= getWikitextProject();
		participant.buildType= this.buildType;
		participant.enabled= false;
		participant.init();
		return participant;
	}
	
}
