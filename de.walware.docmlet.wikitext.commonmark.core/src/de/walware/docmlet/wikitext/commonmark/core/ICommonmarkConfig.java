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

package de.walware.docmlet.wikitext.commonmark.core;

import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;


public interface ICommonmarkConfig extends IMarkupConfig {
	
	
	/**
	 * Returns if ATX header blocks do not interrupt paragraphs.
	 * That means a ATX header block after a paragraph requires a blank line.
	 * 
	 * <p>Defaults:
	 * <table>
	 *   <tr><td>CommonMark</td>				<td><code>false</code></td>	<td></td></tr>
	 *   <tr><td>Pandoc's Markdown</td>			<td><code>true</code></td>	<td>blank_before_header</td></tr>
	 * </table></p>
	 * 
	 * @return if extension is enabled
	 */
	boolean isHeaderInterruptParagraphDisabled();
	
	/**
	 * Returns if quote blocks do not interrupt paragraphs.
	 * That means quote blocks after a paragraph requires a blank line.
	 * 
	 * <p>Defaults:
	 * <table>
	 *   <tr><td>CommonMark</td>				<td><code>false</code></td>	<td></td></tr>
	 *   <tr><td>Pandoc's Markdown</td>			<td><code>true</code></td>	<td>blank_before_blockquote</td></tr>
	 * </table></p>
	 * 
	 * @return if extension is enabled
	 */
	boolean isBlockquoteInterruptParagraphDisabled();
	
	
	/**
	 * Returns if strikeout typesetting spans by delimiter '~~' is enabled.
	 * 
	 * <p>Defaults:
	 * <table>
	 *   <tr><td>CommonMark</td>				<td><code>false</code></td>	<td></td></tr>
	 *   <tr><td>Pandoc's Markdown</td>			<td><code>true</code></td>	<td>strikeout</td></tr>
	 * </table></p>
	 * 
	 * @return if extension is enabled
	 */
	boolean isStrikeoutByDTildeEnabled();
	
	/**
	 * Returns if superscript typesetting span by delimiter '^' is enabled.
	 * 
	 * <p>Defaults:
	 * <table>
	 *   <tr><td>CommonMark</td>				<td><code>false</code></td>	<td></td></tr>
	 *   <tr><td>Pandoc's Markdown</td>			<td><code>true</code></td>	<td>superscript</td></tr>
	 * </table></p>
	 * 
	 * @return if extension is enabled
	 */
	boolean isSuperscriptBySCircumflexEnabled();
	
	/**
	 * Returns if subscript typesetting span by delimiter '~' is enabled.
	 * 
	 * <p>Defaults:
	 * <table>
	 *   <tr><td>CommonMark</td>				<td><code>false</code></td>	<td></td></tr>
	 *   <tr><td>Pandoc's Markdown</td>			<td><code>true</code></td>	<td>subscript</td></tr>
	 * </table></p>
	 * 
	 * @return if extension is enabled
	 */
	boolean isSubscriptBySTildeEnabled();
	
}
