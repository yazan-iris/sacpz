package edu.iris.sacpz.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.iris.sacpz.SacpzResponse;

public class XmlToSacpzListTest {
	@Test
	public void channelOnlyNoRespose() throws Exception {
		try (InputStream inputStream = SeedSacpzIteratorTest.class.getClassLoader()
				.getResourceAsStream("IU.ANMO.BHZ.xml");) {
			List<SacpzResponse> list = SacIOUtils.toList(inputStream);

			assertNotNull(list);
			assertTrue(!list.isEmpty());
			assertEquals(5, list.size());

			SacpzResponse r1 = list.get(0);
			assertEquals(ZonedDateTime.of(2018, 7, 9, 20, 45, 0, 0, ZoneId.of("UTC")), r1.getStart());
			assertNull(r1.getEnd());

			assertEquals(9.772600e+12, r1.getNormalizationFactor());
			assertEquals(1.183000e+03, r1.getInstrumentGain());
			assertEquals(1.984740e+09, r1.getSensitivity());
			assertEquals(9.772600e+12, r1.calculatedNormalizationFactor());
			// 1.9396070124E22

			BigDecimal expected = new BigDecimal(1.939607e+22);
			BigDecimal received = new BigDecimal(r1.calculatedConstant());
			
			System.out.println(received.setScale(-16,RoundingMode.DOWN));
			
			received.setScale(100);
			System.out.println(expected.setScale(-16,RoundingMode.DOWN));
			assertEquals(expected.setScale(-16,RoundingMode.DOWN), received.setScale(-16,RoundingMode.DOWN));

			// r1.getSensitivity()

			SacpzResponse r2 = list.get(1);
			SacpzResponse r3 = list.get(2);
			SacpzResponse r4 = list.get(3);
			SacpzResponse r5 = list.get(4);
		}

	}

}
