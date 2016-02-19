/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.regex.Pattern;

import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.variables.core.DynamicVariable;

import de.walware.docmlet.base.internal.ui.processing.Messages;


public class DocProcessingConfig {
	
	
/*[ Attributes ]===============================================================*/
	
	public static final String BASE_MAIN_ATTR_QUALIFIER= "de.walware.docmlet.base/main"; //$NON-NLS-1$
	
	public static final String WORKING_DIRECTORY_ATTR_NAME= BASE_MAIN_ATTR_QUALIFIER + '/' +
			"WorkingDirectory.path"; //$NON-NLS-1$
	
	
	public static final String BASE_PREVIEW_ATTR_QUALIFIER= "de.walware.docmlet.base/preview"; //$NON-NLS-1$
	
	
	public static final String STEP_ENABLED_ATTR_KEY= "Run.enabled"; //$NON-NLS-1$
	
	public static final String STEP_OUTPUT_FORMAT_ATTR_KEY= "Output.Format.key"; //$NON-NLS-1$
	public static final String STEP_OUTPUT_FILE_PATH_ATTR_KEY= "Output.File.path"; //$NON-NLS-1$
	
	public static final String STEP_OPERATION_ID_ATTR_KEY= "Operation.id"; //$NON-NLS-1$
	public static final String STEP_OPERATION_SETTINGS_ATTR_KEY= "Operation.settings"; //$NON-NLS-1$
	
	public static final String STEP_POST_CHECK_OUTPUT_ENABLED_ATTR_KEY= "Post.CheckOutput.enabled"; //$NON-NLS-1$
	public static final String STEP_POST_OPEN_OUTPUT_ENABLED_ATTR_KEY= "Post.OpenOutput.enabled"; //$NON-NLS-1$
	
	
/*[ Variables ]================================================================*/
	
	
	public static final String WD_LOC_VAR_NAME= "wd_loc"; //$NON-NLS-1$
	public static final String WD_PATH_VAR_NAME= "wd_path"; //$NON-NLS-1$
	
	public static final String SOURCE_FILE_PATH_VAR_NAME= "source_file_path"; //$NON-NLS-1$
	
	public static final String OUT_FILE_PATH_VAR_NAME= "out_file_path"; //$NON-NLS-1$
	
	public static final String OUT_FILE_EXT_VAR_NAME= "out_file_ext"; //$NON-NLS-1$
	
	
	public static final IDynamicVariable SOURCE_FILE_PATH_VAR= new DynamicVariable(
			SOURCE_FILE_PATH_VAR_NAME, Messages.Variable_SourceFilePath_description, false );
	
	@Deprecated
	public static final IDynamicVariable IN_FILE_PATH_VAR= new DynamicVariable(
			"in_file_path", Messages.Variable_InFilePath_description, false ); //$NON-NLS-1$
	
	public static final IDynamicVariable OUT_FILE_PATH_VAR= new DynamicVariable(
			OUT_FILE_PATH_VAR_NAME, Messages.Variable_OutFilePath_description, false );
	
	public static final IDynamicVariable OUT_FILE_EXT_VAR= new DynamicVariable(
			OUT_FILE_EXT_VAR_NAME, Messages.Variable_OutFileExt_description, false );
	
	
/*[ Formats ]==================================================================*/
	
	public static class Format {
		
		
		public static final String SOURCE_TYPE= "source"; //$NON-NLS-1$
		public static final String AUTO_TYPE= "auto"; //$NON-NLS-1$
		public static final String EXT_TYPE= "ext"; //$NON-NLS-1$
		
		
		private final String key;
		
		private final String label;
		private final String infoLabel;
		private final String ext;
		
		
		public Format(final String key, final String label, final String ext) {
			this(key, label, null, ext);
		}
		
		public Format(final String key, final String label, final String infoLabel,
				final String ext) {
			this.key= key;
			this.label= label;
			this.infoLabel= infoLabel;
			this.ext= ext;
		}
		
		Format(final Format format, final String key, final String ext) {
			this.key= key;
			this.label= format.label;
			this.infoLabel= format.infoLabel;
			this.ext= ext;
		}
		
		
		public String getKey() {
			return this.key;
		}
		
		boolean matches(final String key) {
			return key.equals(this.key);
		}
		
		public String getExt() {
			return this.ext;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public String getInfoLabel() {
			return (this.infoLabel != null) ? NLS.bind(this.infoLabel, this.ext) : this.label;
		}
		
		public String getExt(final String inputExt) {
			return this.ext;
		}
		
		
		@Override
		public int hashCode() {
			return this.key.hashCode();
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (null == obj || getClass() != obj.getClass()) {
				return false;
			}
			final Format other= (Format) obj;
			return (this.key.equals(other.key)
					&& this.label.equals(other.label)
					&& ((this.infoLabel != null) ? this.infoLabel.equals(other.infoLabel) : null == other.infoLabel)
					&& ((this.ext != null) ? this.ext.equals(other.ext) : null == other.ext) );
		}
		
		@Override // for LabelProvider
		public String toString() {
			return getLabel();
		}
		
	}
	
	public static class CustomExtFormat extends Format {
		
		private static String checkKey(final String key, final String ext) {
			if (key.isEmpty() || key.charAt(key.length() - 1) != ':') {
				throw new IllegalArgumentException("key= " + key);
			}
			return key + ext;
		}
		
		
		private final String baseKey;
		
		
		public CustomExtFormat(final String key, final String label, final String infoLabel, final String ext) {
			super(checkKey(key, ext), label, infoLabel, ext);
			this.baseKey= key;
		}
		
		public CustomExtFormat(final CustomExtFormat format, final String ext) {
			super(format, format.getBaseKey() + ext, ext);
			this.baseKey= format.getBaseKey();
		}
		
		
		public String getBaseKey() {
			return this.baseKey;
		}
		
		@Override
		boolean matches(final String key) {
			return key.startsWith(this.baseKey);
		}
		
		
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj)
					&& this.baseKey.equals(((CustomExtFormat) obj).baseKey );
		}
		
	}
	
	
	public static final String SOURCE_FORMAT_KEY= Format.SOURCE_TYPE;
	
	public static final String AUTO_YAML_FORMAT_KEY= Format.AUTO_TYPE + ":by-indoc-yaml"; //$NON-NLS-1$
	
	public static final String EXT_LTX_FORMAT_KEY= Format.EXT_TYPE + ":ltx"; //$NON-NLS-1$
	public static final String EXT_PDF_FORMAT_KEY= Format.EXT_TYPE + ":pdf"; //$NON-NLS-1$
	public static final String EXT_HTML_FORMAT_KEY= Format.EXT_TYPE + ":html"; //$NON-NLS-1$
	public static final String EXT_OTHER_FORMAT_KEY= Format.EXT_TYPE + ":"; //$NON-NLS-1$
	
	
	public static Format createSourceFormat(final String formatLabel) {
		return new Format(Format.SOURCE_TYPE,
				NLS.bind(Messages.Format_SourceDoc_label, formatLabel),
				"" ) { //$NON-NLS-1$
			
			@Override
			public String getExt(final String inputExt) {
				return inputExt;
			}
			
		};
	}
	
	public static final Format AUTO_YAML_FORMAT= new Format(AUTO_YAML_FORMAT_KEY,
			Messages.Format_AutoByInDocYaml_label, "*" ) { //$NON-NLS-1$
		
		@Override
		public String getExt(final String inputExt) {
			return "* (YAML)"; //$NON-NLS-1$
		}
		
	};
	
	public static final Format EXT_LTX_FORMAT= new Format(EXT_LTX_FORMAT_KEY, "LaTeX", "ltx"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Format EXT_PDF_FORMAT= new Format(EXT_PDF_FORMAT_KEY, "PDF", "pdf"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Format EXT_HTML_FORMAT= new Format(EXT_HTML_FORMAT_KEY, "HTML", "html"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final Format EXT_OTHER_FORMAT= new CustomExtFormat(EXT_OTHER_FORMAT_KEY,
			Messages.Format_Other_label, Messages.Format_Other_Info_label, "" ); //$NON-NLS-1$
	
	public static Format createOutputFormat(final Format format) {
		return new Format(format.getKey(),
				NLS.bind(Messages.Format_Output_label, format.getInfoLabel()),
				format.getExt() );
	}
	
	
	/**
	 * Returns the first matching format specified by the key from the given list of available
	 * formats.
	 * <p>{@link CustomExtFormat Customizable formats} are not adapted.
	 * </p>
	 * 
	 * @param formats list of available formats
	 * @param defaultFormat default format or <code>null</code> for no default format
	 * @param key the key specifying the format
	 * 
	 * @return the format or <code>null</code>
	 */
	public static Format getFormat(final List<Format> formats, final String key) {
		for (final Format aFormat : formats) {
			if (aFormat.matches(key)) {
				return aFormat;
			}
		}
		return null;
	}
	
	/**
	 * Returns the first matching format specified by the key from the given list of available
	 * formats. If no format is found in the list, it returns the specified default format.
	 * <p>{@link CustomExtFormat Customizable formats} are adapted, if required.
	 * </p>
	 * 
	 * @param formats list of available formats
	 * @param defaultFormat default format or <code>null</code> for no default format
	 * @param key the key specifying the format
	 * 
	 * @return the format or <code>null</code>
	 */
	public static Format getFormat(final List<Format> formats, final Format defaultFormat,
			final String key) {
		if (key == null) {
			return null;
		}
		
		Format format= defaultFormat;
		for (final Format aFormat : formats) {
			if (aFormat.matches(key)) {
				format= aFormat;
				break;
			}
		}
		
		if (format instanceof CustomExtFormat) {
			final int idx= key.indexOf(':');
			if (idx >= 0 && idx + 1 < key.length()) {
				format= new CustomExtFormat((CustomExtFormat) format, key.substring(idx + 1));
			}
		}
		
		return format;
	}
	
	
	public static final Pattern VALID_EXT_PATTERN= Pattern.compile("[\\p{Alnum}\\-,;_~]+"); //$NON-NLS-1$
	
}
