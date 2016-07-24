/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de)
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.spec;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.walware.jcommons.collections.ImCollections;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import de.walware.docmlet.wikitext.commonmark.core.CommonmarkLanguage;


@RunWith(Parameterized.class)
public class CommonMarkSpecTest {
	
	
	private static final String SPEC_VERSION = "0.25";
	
	private static final URI COMMONMARK_SPEC_URI = URI.create(
			String.format("https://raw.githubusercontent.com/jgm/CommonMark/%s/spec.txt", SPEC_VERSION) );
	
	private static final String CASE_START= "```````````````````````````````` example";
	private static final String CASE_SEP= ".";
	private static final String CASE_END= "````````````````````````````````";
	
	
	private static final List<String> HEADING_EXCLUSIONS = ImCollections.newList();
	
	private static final List<Integer> LINE_EXCLUSIONS = ImCollections.newList();
	
	
	public static class Expectation {
		
		final String input;
		final String expected;
		
		public Expectation(String input, String expected) {
			this.input = input;
			this.expected = expected;
		}
		
		@Override
		public String toString() {
			return Objects.toStringHelper(Expectation.class).add("input", input).add("expected", expected).toString();
		}
		
	}
	
	
	private final Expectation expectation;
	
	private final String heading;
	
	private final int lineNumber;
	
	
	public CommonMarkSpecTest(String heading, int num, int lineNumber, Expectation expectation) {
		this.heading = heading;
		this.lineNumber = lineNumber;
		this.expectation = expectation;
	}
	
	
	@Before
	public void preconditions() {
		assumeTrue(!HEADING_EXCLUSIONS.contains(heading));
		assumeTrue(!LINE_EXCLUSIONS.contains(Integer.valueOf(lineNumber)));
	}
	
	@Test
	public void test() {
		try {
			CommonmarkLanguage language = createCommonmarkLanguage();
			assertContent(language, expectation.expected, expectation.input);
		} catch (Error | RuntimeException e) {
			String info= this.heading + ": lineNumber= " + this.lineNumber;
			e.addSuppressed(new Exception(info + "\n===\n" + expectation.input + "\n===\n"));
			throw e;
		}
	}
	
	private CommonmarkLanguage createCommonmarkLanguage() {
		CommonmarkLanguage language = new CommonmarkLanguage();
		return language;
	}
	
	
	@Parameters (name= "{index}: {0} - {1}, lineNumber= {2}")
	public static List<Object[]> parameters() {
		List<Object[]> parameters = new ArrayList<>();
		
		loadSpec(parameters);
		
		return ImCollections.toList(parameters);
	}
	
	private static void loadSpec(List<Object[]> parameters) {
		Pattern headingPattern = Pattern.compile("#+\\s*(.+)");
		try {
			String spec = loadCommonMarkSpec();
			LocationTrackingReader reader = new LocationTrackingReader(new StringReader(spec));
			String heading = "unspecified";
			String line;
			int num= 1;
			while ((line = reader.readLine()) != null) {
				line = line.replace('→', '\t');
				if (line.trim().equals(CASE_START)) {
					int testLineNumber = reader.getLineNumber();
					Expectation expectation = readExpectation(reader);
					parameters.add(
							new Object[] { heading, num, testLineNumber + 1, expectation });
					num++;
				}
				Matcher headingMatcher = headingPattern.matcher(line);
				if (headingMatcher.matches()) {
					heading = headingMatcher.group(1);
				}
			}
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static Expectation readExpectation(LocationTrackingReader reader) throws IOException {
		String input = readUntilDelimiter(reader, CASE_SEP);
		String expected = readUntilDelimiter(reader, CASE_END);
		return new Expectation(input, expected);
	}
	
	private static String readUntilDelimiter(LocationTrackingReader reader, String end) throws IOException {
		List<String> lines = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.replace('→', '\t');
			if (line.trim().equals(end)) {
				break;
			}
			lines.add(line);
		}
		return Joiner.on("\n").join(lines);
	}
	
	private static String loadCommonMarkSpec() throws IOException {
		File tmpFolder = new File("./tmp");
		if (!tmpFolder.exists()) {
			tmpFolder.mkdir();
		}
		assertTrue(tmpFolder.getAbsolutePath(), tmpFolder.exists());
		File spec = new File(tmpFolder, String.format("spec%s.txt", SPEC_VERSION));
//		spec.delete();
		if (!spec.exists()) {
			try (FileOutputStream out = new FileOutputStream(spec)) {
				Resources.copy(COMMONMARK_SPEC_URI.toURL(), out);
			}
		}
		try (InputStream in = new FileInputStream(spec)) {
			return CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8));
		}
	}
	
}
