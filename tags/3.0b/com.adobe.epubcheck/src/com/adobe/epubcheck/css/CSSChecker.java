/*
 * Copyright (c) 2011 Adobe Systems Incorporated
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.adobe.epubcheck.css;

import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.helpers.ParserFactory;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.opf.ContentChecker;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.Messages;

public class CSSChecker implements ContentChecker {

	private OCFPackage ocf;
	private Report report;
	private String path;
	private XRefChecker xrefChecker;

	public CSSChecker(OCFPackage ocf, Report report, String path,
			XRefChecker xrefChecker) {
		this.ocf = ocf;
		this.report = report;
		this.path = path;
		this.xrefChecker = xrefChecker;
	}

	public void runChecks() {
		try {
			if (!ocf.hasEntry(path)) {
				report.error(null, 0, 0, String.format(Messages.MISSING_FILE, path));
				return;
			}

			System.setProperty("org.w3c.css.sac.parser",
					"org.w3c.flute.parser.Parser");

			ParserFactory pf = new ParserFactory();
			Parser parser = pf.makeParser();

			parser.setDocumentHandler(new CSSHandler(path, xrefChecker, report));

			InputSource input = new InputSource();

			input.setByteStream(ocf.getInputStream(path));

			parser.parseStyleSheet(input);

		} catch (Exception e) {
			report.error(path, -1, 0, e.getMessage());
		}

	}
}
