package edu.iris.sacpz.io;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import edu.iris.sacpz.SacpzResponse;
import edu.iris.seed.io.output.StringBuilderOutputStream;

public class XmlSacpzIteratorTest {
	@Test
	public void channelOnlyNoRespose() throws Exception {
		try (InputStream stream = SeedSacpzIteratorTest.class.getClassLoader().getResourceAsStream("IU.ANMO.BH.cha.xml");
				XmlSacpzIterator it = new XmlSacpzIterator(stream);) {

			StringBuilderOutputStream o=new StringBuilderOutputStream();
			try (SacpzOutputStream out = new SacpzOutputStream(o)) {
				while (it.hasNext()) {
					SacpzResponse b = it.next();
					out.write(b);
				}
				System.out.println(o.toString());
			}
		}

	}
	
	@Test
	public void channelOnlyOneStationNoRespose() throws Exception {
		//SacFileUtils.xmlSacpzIterator(file);
		
		try (InputStream stream = SeedSacpzIteratorTest.class.getClassLoader().getResourceAsStream("IU.ANMO.BH.NORESPONSE.xml");
				XmlSacpzIterator it = new XmlSacpzIterator(stream);) {

			StringBuilderOutputStream o=new StringBuilderOutputStream();
			try (SacpzOutputStream out = new SacpzOutputStream(o)) {
				while (it.hasNext()) {
					SacpzResponse b = it.next();
					out.write(b);
				}
				System.out.println(o.toString());
			}
		}

	}
	
	
}
