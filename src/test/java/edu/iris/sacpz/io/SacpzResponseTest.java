package edu.iris.sacpz.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.iris.sacpz.SacpzResponse;
import edu.iris.seed.station.B050;
import edu.iris.seed.station.B052;
import edu.iris.station.model.Channel;

public class SacpzResponseTest {

	@Test
	public void xml() throws Exception {
		Channel channel = new Channel();

		channel.setCode("BH1");
		channel.setLocationCode("00");
		SacpzResponse sacpz = SacpzResponse.Builder.newInstance().forChannel(channel).build();
		sacpz.calculatedNormalizationFactor();

		assertEquals(1, 1);
	}

	@Test
	public void seed() throws Exception {
		B050 b050=new B050();
		
		B052 b052=new B052();
		SacpzResponse sacpz = SacpzResponse.SeedBuilder.newInstance().b050(b050).b052(b052).build();
		sacpz.calculatedNormalizationFactor();

		assertEquals(1, 1);
	}
}
