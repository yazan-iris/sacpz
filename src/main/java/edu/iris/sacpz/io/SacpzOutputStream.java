package edu.iris.sacpz.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.iris.sacpz.SacPole;
import edu.iris.sacpz.SacPolesZeros;
import edu.iris.sacpz.SacZero;
import edu.iris.sacpz.SacpzResponse;

public class SacpzOutputStream implements AutoCloseable {

	private PrintWriter writer;

	public SacpzOutputStream(OutputStream outputStream) {
		this.writer = new PrintWriter(new OutputStreamWriter(outputStream));
	}

	public void write(SacpzResponse response) throws IOException {
		if (this.writer == null) {
			throw new IOException("Failed to open a stream to write to!");
		}

		// DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("UTC"));
		NumberFormat formatter = new DecimalFormat("#.000000");

		this.writer.println("* **********************************");
		this.writer.println("* NETWORK   (KNETWK): " + response.getNetwork());
		this.writer.println("* STATION    (KSTNM): " + response.getStation());
		this.writer.println("* LOCATION   (KHOLE): " + response.getLocation());
		this.writer.println("* CHANNEL   (KCMPNM): " + response.getChannel());
		this.writer.println("* CREATED           : " + format.format(ZonedDateTime.now()));
		this.writer.println("* START             : " + format.format(response.getStart()));

		if (response.getEnd() == null) {
			this.writer.println("* END               : ");
		} else {
			this.writer.println("* END               : " + format.format(response.getEnd()));
		}

		String instrument = response.getSite();
		this.writer.print("* DESCRIPTION       : ");
		if (instrument != null) {
			this.writer.println(response.getSite());
		} else {
			this.writer.println();
		}
		this.writer.println("* LATITUDE          : " + formatter.format(response.getLatitude()));
		this.writer.println("* LONGITUDE         : " + formatter.format(response.getLongitude()));
		this.writer.println("* ELEVATION         : " + response.getElevation());
		this.writer.println("* DEPTH             : " + response.getDepth());

		double dip = response.getDip();
		this.writer.println("* DIP               : " + dip);
		this.writer.println("* AZIMUTH           : " + response.getAzimuth());
		this.writer.println("* SAMPLE RATE       : " + response.getSampleRate());

		this.writer.print("* INPUT UNIT        : ");
		if (response.getInputUnits() != null) {
			this.writer.print(response.getInputUnits());
		}
		this.writer.println();

		this.writer.print("* OUTPUT UNIT       : ");
		if (response.getOutputUnits() != null) {
			this.writer.print(response.getOutputUnits());
		}

		this.writer.println();

		this.writer.print("* INSTTYPE          : ");
		if (response.getEquipment() != null) {
			this.writer.println(response.getEquipment());
		} else {
			this.writer.println();
		}
		this.writer.print("* INSTGAIN          : ");

		this.writer.print(String.format("%e", new Object[] { response.getInstrumentGain() }));
		if (response.getOriginalInputUnits() != null) {
			this.writer.print(" (" + response.getOriginalInputUnits() + ")");
		}
		this.writer.println();

		this.writer.print("* COMMENT           : ");
		if (response.getComment() != null) {
			this.writer.print(response.getComment());
		}
		this.writer.println();

		this.writer.print("* SENSITIVITY       : ");

		this.writer.print(String.format("%e", new Object[] { response.getSensitivity() }));
		if (response.getOriginalInputUnits() != null) {
			this.writer.print(" (" + response.getOriginalInputUnits() + ")");
		}

		this.writer.println();

		SacPolesZeros pz = response.getPolesZeros();
		if (pz != null) {

			List<SacZero> zeros = pz.getZeros();
			List<SacPole> poles = pz.getPoles();

			double a0_local = response.calculatedNormalizationFactor();
			this.writer.println("* A0                : " + String.format("%e", new Object[] { a0_local }));

			this.writer.println("* **********************************");

			if (zeros.size() > 0) {
				this.writer.println("ZEROS\t" + zeros.size());
				for (SacZero z : zeros) {
					this.writer.println("\t" + String.format("%+e", new Object[] { z.getReal() }) + "\t"
							+ String.format("%+e", new Object[] { z.getImaginary() }) + "\t");
				}
			} else {

				this.writer.println("ZEROS\t0");
			}

			if (poles.size() > 0) {
				this.writer.println("POLES\t" + poles.size());
				for (SacPole p : poles) {
					this.writer.println("\t" + String.format("%+e", new Object[] { p.getReal() }) + "\t"
							+ String.format("%+e", new Object[] { p.getImaginary() }) + "\t");
				}
			} else {

				this.writer.println("POLES\t0");
			}

			if ((zeros.size() > 0 || poles.size() > 0)) {
				this.writer.println("CONSTANT\t" + String.format("%e", new Object[] {

						response.calculatedConstant() }));
			}
		}
		if (this.writer.checkError()) {
			close();
			throw new IOException("Unkown IO Error?");
		}
		this.writer.println();
		this.writer.println();
		if(this.writer.checkError()) {
			throw new IOException("Unkown IO error.'");
		}

	}

	public void flush() throws IOException{
		if(writer.checkError()) {
			throw new IOException("Unkown IO error.");
		}
	}
	
	public void println() throws IOException{
		writer.println();
	}
	public void close() {
		if (this.writer != null)
			this.writer.close();
	}
}