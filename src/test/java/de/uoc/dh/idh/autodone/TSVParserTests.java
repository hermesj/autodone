package de.uoc.dh.idh.autodone;

import org.junit.jupiter.api.Test;

import de.uoc.dh.idh.autodone.helpers.TSVParser;

class TSVParserTests {

	@Test
	void testTSVParserReadTSV() {
		TSVParser tp = new TSVParser();
		System.out.println(tp.readTSV("src/test/resources/test.tsv"));
	}

}
