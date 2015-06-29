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

package de.walware.docmlet.wikitext.core.source.extdoc;

import java.util.List;

import de.walware.ecommons.collections.ImCollections;

import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;


public abstract class AbstractMarkupConfig<T extends AbstractMarkupConfig<? super T>>
		implements IMarkupConfig {
	
	
	public static final String YAML_METADATA_ENABLED_PROP= "YamlMetadataEnabled"; //$NON-NLS-1$
	private static final String YAML_METADATA_ENABLED_KEY= "yaml_metadata_block"; //$NON-NLS-1$
	
	public static final String TEX_MATH_DOLLARS_ENABLED_PROP= "TexMathDollarsEnabled"; //$NON-NLS-1$
	private static final String TEX_MATH_DOLLARS_ENABLED_KEY= "tex_math_dollars"; //$NON-NLS-1$
	
	public static final String TEX_MATH_SBACKSLASH_ENABLED_PROP= "TexMathSBackslashEnabled"; //$NON-NLS-1$
	private static final String TEX_MATH_SBACKSLASH_ENABLED_KEY= "tex_math_single_backslash"; //$NON-NLS-1$
	
	
	private boolean isSealed;
	
	private boolean isYamlMetadataEnabled;
	
	private boolean isTexMathDollarsEnabled;
	private boolean isTexMathSBackslashEnabled;
	
	private String configString;
	
	
	protected AbstractMarkupConfig() {
	}
	
	
	@Override
	public T clone() {
		try {
			final AbstractMarkupConfig clone= (AbstractMarkupConfig) super.clone();
			clone.isSealed= false;
			return (T) clone;
		}
		catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void seal() {
		this.isSealed= true;
	}
	
	@Override
	public final boolean isSealed() {
		return this.isSealed;
	}
	
	protected final void checkSeal() {
		if (isSealed()) {
			throw new IllegalStateException("object is read only");
		}
	}
	
	protected void afterChange(final String propertyName) {
		this.configString= null;
	}
	
	
	public void setYamlMetadataEnabled(final boolean enabled) {
		checkSeal();
		if (this.isYamlMetadataEnabled != enabled) {
			this.isYamlMetadataEnabled= enabled;
			afterChange(YAML_METADATA_ENABLED_PROP);
		}
	}
	
	public boolean isYamlMetadataEnabled() {
		return this.isYamlMetadataEnabled;
	}
	
	public void setTexMathDollarsEnabled(final boolean enabled) {
		checkSeal();
		if (this.isTexMathDollarsEnabled != enabled) {
			this.isTexMathDollarsEnabled= enabled;
			afterChange(TEX_MATH_DOLLARS_ENABLED_PROP);
		}
	}
	
	public boolean isTexMathDollarsEnabled() {
		return this.isTexMathDollarsEnabled;
	}
	
	public void setTexMathSBackslashEnabled(final boolean enabled) {
		checkSeal();
		if (this.isTexMathSBackslashEnabled != enabled) {
			this.isTexMathSBackslashEnabled= enabled;
			afterChange(TEX_MATH_SBACKSLASH_ENABLED_PROP);
		}
	}
	
	public boolean isTexMathSBackslashEnabled() {
		return this.isTexMathSBackslashEnabled;
	}
	
	
	@Override
	public String getString() {
		String s= this.configString;
		if (s == null) {
			s= createConfigString();
			this.configString= s;
		}
		return s;
	}
	
	protected abstract String getConfigType();
	
	protected boolean supportsConfigType(final String configType) {
		return getConfigType().equals(configType);
	}
	
	protected String createConfigString() {
		final StringBuilder sb= new StringBuilder(getConfigType());
		sb.append(':');
		if (isYamlMetadataEnabled()) {
			sb.append(YAML_METADATA_ENABLED_KEY + ';');
		}
		if (isTexMathDollarsEnabled()) {
			sb.append(TEX_MATH_DOLLARS_ENABLED_KEY + ';');
		}
		if (isTexMathSBackslashEnabled()) {
			sb.append(TEX_MATH_SBACKSLASH_ENABLED_KEY + ';');
		}
		return (sb.length() == 0) ? "" : sb.substring(0, sb.length() - 1); //$NON-NLS-1$
	}
	
	protected void load(final String configType, final List<String> s) {
		setYamlMetadataEnabled(s.contains(YAML_METADATA_ENABLED_KEY));
		setTexMathDollarsEnabled(s.contains(TEX_MATH_DOLLARS_ENABLED_KEY));
		setTexMathSBackslashEnabled(s.contains(TEX_MATH_SBACKSLASH_ENABLED_KEY));
	}
	
	@Override
	public boolean load(final String configString) {
		final int i= configString.indexOf(':');
		final String configType;
		if (i < 0 || !supportsConfigType((configType= configString.substring(0, i)))) {
			return false;
		}
		load(configType,
				ImCollections.newList(configString.substring(i + 1).split(";")) ); //$NON-NLS-1$
		return true;
	}
	
	public void load(final T config) {
		setYamlMetadataEnabled(config.isYamlMetadataEnabled());
		setTexMathDollarsEnabled(config.isTexMathDollarsEnabled());
		setTexMathSBackslashEnabled(config.isTexMathSBackslashEnabled());
	}
	
	
	@Override
	public int hashCode() {
		return getString().hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractMarkupConfig)) {
			return false;
		}
		final AbstractMarkupConfig<?> other= (AbstractMarkupConfig<?>) obj;
		return (this.getConfigType() == other.getConfigType()
				&& this.isYamlMetadataEnabled() == other.isYamlMetadataEnabled()
				&& this.isTexMathDollarsEnabled() == other.isTexMathDollarsEnabled()
				&& this.isTexMathSBackslashEnabled() == other.isTexMathSBackslashEnabled() );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getString(); //$NON-NLS-1$
	}
	
}
