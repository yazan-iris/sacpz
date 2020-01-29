package edu.iris.sacpz.io;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class XmlToSacpzStringTest {
	@Test
	public void channelOnlyNoRespose() throws Exception {
		try (InputStream inputStream = SeedSacpzIteratorTest.class.getClassLoader()
				.getResourceAsStream("IU.ANMO.BHZ.xml");) {
			String string = SacIOUtils.toString(inputStream);

			System.out.println(string);
		}

	}

}
