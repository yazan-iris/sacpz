package edu.iris.sacpz.io;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.iris.sacpz.SacpzResponse;
import edu.iris.seed.Blockette;
import edu.iris.seed.SeedException;
import edu.iris.seed.SeedInputStream;
import edu.iris.seed.abbreviation.AbbreviationBlockette;
import edu.iris.seed.abbreviation.B033;
import edu.iris.seed.abbreviation.B034;
import edu.iris.seed.io.SeedBlocketteIterator;
import edu.iris.seed.record.AbbreviationRecord;
import edu.iris.seed.station.B050;
import edu.iris.seed.station.B052;
import edu.iris.seed.station.B053;
import edu.iris.seed.station.B058;

public class SeedSacpzIterator implements Iterator<SacpzResponse>, AutoCloseable {

	private SeedBlocketteIterator iterator;

	private SacpzResponse cachedResponse;
	private boolean finished;

	private AbbreviationRecord abbreviationRecord;

	public SeedSacpzIterator(InputStream inputStream) {
		iterator = new SeedBlocketteIterator(new SeedInputStream(inputStream));
		abbreviationRecord = new AbbreviationRecord();
	}

	@Override
	public boolean hasNext() {
		if (cachedResponse != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			try {
				SacpzResponse response = nextSacpzResponse();
				if (response == null) {
					finished = true;
					return false;
				} else {
					cachedResponse = response;
					return true;
				}
			} catch (SeedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					iterator.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public SacpzResponse next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more responses");
		}
		final SacpzResponse currentResponse = cachedResponse;
		cachedResponse = null;
		return currentResponse;
	}

	private SacpzResponse.SeedBuilder lookAheadRespone = null;
	private B050 b050;

	private SacpzResponse nextSacpzResponse() throws SeedException {
		SacpzResponse.SeedBuilder responseBuilder = null;
		SacpzResponse.SeedBuilder response = null;
		if (lookAheadRespone != null) {
			response = lookAheadRespone;
			lookAheadRespone = null;
		}
		while (iterator.hasNext()) {
			Blockette b = iterator.next();
			int bType = b.getType();
			if (bType >= 30 && bType < 50) {
				abbreviationRecord.add((AbbreviationBlockette) b);
			} else {
				if (bType == 50) {
					responseBuilder.b050((B050) b);
					b050 = (B050) b;
				} else if (bType == 52) {
					B052 b052 = (B052) b;
					if (response == null) {
						responseBuilder = SacpzResponse.SeedBuilder.newInstance().b050(b050).b052(b052)
								.b033((B033) abbreviationRecord.get(52, b052.getInstrumentIdentifier()));
						// response = startNewResponse(b052);
					} else {
						lookAheadRespone = SacpzResponse.SeedBuilder.newInstance().b050(b050).b052(b052)
								.b033((B033) abbreviationRecord.get(52, b052.getInstrumentIdentifier()));
						return response.build();
					}
				} else if (b.getType() == 53) {
					B053 b053 = (B053) b;
					responseBuilder.b053(b053, (B034) abbreviationRecord.get(34, b053.getSignalInputUnit()),
							(B034) abbreviationRecord.get(34, b053.getSignalOutputUnit()));
				} else if (b.getType() == 58) {
					B058 b058 = (B058) b;
					responseBuilder.b058(b058, (B034)abbreviationRecord.get(34,b058.getSignalOutputUnit()));
				}
			}
		}
		if (response == null) {

		}
		return response.build();
	}

	public static ZonedDateTime toZonedDateTime(String source) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[z]").withZone(ZoneId.of("UTC"));
		return ZonedDateTime.parse(source, format);
	}

	@Override
	public void close() throws Exception {
		finished = true;
		cachedResponse = null;
		if (iterator != null) {
			iterator.close();
		}

	}
}
