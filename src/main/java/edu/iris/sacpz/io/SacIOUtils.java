package edu.iris.sacpz.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.iris.sacpz.SacpzResponse;
import edu.iris.seed.io.output.StringBuilderOutputStream;
import edu.iris.station.io.StationXmlIterator;
import edu.iris.station.model.Channel;
import edu.iris.station.model.Station;

public class SacIOUtils {

	public static String toString(InputStream inputStream) throws IOException {
		try (StationXmlIterator iterator = new StationXmlIterator(inputStream);
				StringBuilderOutputStream stringBuilder = new StringBuilderOutputStream();
				SacpzOutputStream out = new SacpzOutputStream(stringBuilder);) {
			while (iterator.hasNext()) {
				Station station = iterator.next();
				for (Channel channel : station.getChannels()) {
					SacpzResponse response = SacpzResponse.Builder.newInstance().forChannel(channel).build();
					out.write(response);
				}
			}
			return stringBuilder.toString();
		}
	}
	
	public static List<SacpzResponse> toList(InputStream inputStream) throws IOException {
		try (StationXmlIterator iterator = new StationXmlIterator(inputStream);
				StringBuilderOutputStream stringBuilder = new StringBuilderOutputStream();
				SacpzOutputStream out = new SacpzOutputStream(stringBuilder);) {
			List<SacpzResponse>list=new ArrayList<>();
			while (iterator.hasNext()) {
				Station station = iterator.next();
				for (Channel channel : station.getChannels()) {
					list.add(SacpzResponse.Builder.newInstance().forChannel(channel).build());
				}
			}
			return list;
		}
	}

	public static XmlSacpzIterator xmlSacpzIterator(InputStream inputStream) throws IOException {
		return new XmlSacpzIterator(inputStream);
	}

	public static SeedSacpzIterator seedSacpzIterator(InputStream inputStream) throws IOException {
		return new SeedSacpzIterator(inputStream);
	}

}
