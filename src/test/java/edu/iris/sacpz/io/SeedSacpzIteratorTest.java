package edu.iris.sacpz.io;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import edu.iris.sacpz.SacpzResponse;

public class SeedSacpzIteratorTest {

	@Test
	public void getAll() throws Exception {
		try (InputStream stream = SeedSacpzIteratorTest.class.getClassLoader()
				.getResourceAsStream("AU.MILA.dataless.fromHughGlanville.20181018");
				SeedSacpzIterator it = new SeedSacpzIterator(stream);) {

			try (SacpzOutputStream out = new SacpzOutputStream(System.out)) {

				while (it.hasNext()) {
					SacpzResponse b = it.next();
					out.write(b);
				}
			}
		}

	}
}
