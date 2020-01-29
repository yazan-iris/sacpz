package edu.iris.sacpz;

import java.time.ZonedDateTime;

import edu.iris.seed.abbreviation.B033;
import edu.iris.seed.abbreviation.B034;
import edu.iris.seed.station.B050;
import edu.iris.seed.station.B052;
import edu.iris.seed.station.B053;
import edu.iris.seed.station.B058;
import edu.iris.seed.station.Pole;
import edu.iris.seed.station.Zero;
import edu.iris.station.model.Channel;
import edu.iris.station.model.Dip;
import edu.iris.station.model.Equipment;
import edu.iris.station.model.Network;
import edu.iris.station.model.PoleZero;
import edu.iris.station.model.PolesZeros;
import edu.iris.station.model.Response;
import edu.iris.station.model.ResponseStage;
import edu.iris.station.model.Sensitivity;
import edu.iris.station.model.StageGain;
import edu.iris.station.model.Station;

public class SacpzResponse {
	private String network;
	private String station;
	private String channel;
	private String location;
	private String site;

	private ZonedDateTime created;
	private ZonedDateTime start;
	private ZonedDateTime end;

	private double azimuth;
	private double depth;
	private double dip;
	private double elevation;
	private double latitude;
	private double longitude;
	private double sampleRate;

	private String comment;
	private String equipment;

	private int stage;
	private char transferType;
	private String inputUnits;
	private String originalInputUnits;
	private String originalInputUnitsDescription;
	private String outputUnits;
	private String inputUnitsDescription;
	// private String originalInputUnitsDescription;
	private String outputUnitsDescription;
	private double normalizationFactor;
	private double sensitivity;
	private double instrumentGain;
	private SacPolesZeros polesZeros;
	// private int addedZeros;

	private SacpzResponse() {
		this.polesZeros = new SacPolesZeros();
	}

	public SacpzResponse(int stage, char transferType) {
		this.polesZeros = new SacPolesZeros();
		// this.addedZeros = 0;

		this.stage = stage;
		this.transferType = transferType;

	}

	public String getNetwork() {
		return network;
	}

	public String getStation() {
		return station;
	}

	public String getChannel() {
		return channel;
	}

	public String getLocation() {
		return location;
	}

	public String getComment() {
		return comment;
	}

	public String getSite() {
		return site;
	}

	public ZonedDateTime getCreated() {
		return created;
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public ZonedDateTime getEnd() {
		return end;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public double getDepth() {
		return depth;
	}

	public double getDip() {
		return dip;
	}

	public double getElevation() {
		return elevation;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getSampleRate() {
		return sampleRate;
	}

	public String getEquipment() {
		return equipment;
	}

	public int getStage() {
		return this.stage;
	}

	public String getInputUnits() {
		return this.inputUnits;
	}

	public void setInputUnits(String inputUnits, String inputUnitsDescription) {
		this.inputUnits = inputUnits;
		this.inputUnitsDescription = inputUnitsDescription;
	}

	public String getOriginalInputUnits() {
		return this.originalInputUnits;
	}

	public String getOriginalInputUnitsDescription() {
		return this.originalInputUnitsDescription;
	}

	public String getOutputUnits() {
		return this.outputUnits;
	}

	public String getInputUnitsDescription() {
		return this.inputUnitsDescription;
	}

	public String getOutputUnitsDescription() {
		return this.outputUnitsDescription;
	}

	public double getNormalizationFactor() {
		return this.normalizationFactor;
	}

	public double calculatedNormalizationFactor() {
		double a0 = this.normalizationFactor;
		if ((this.polesZeros.getPoles().size() > 0 || getPolesZeros().getZeros().size() > 0)
				&& 'B' == this.transferType) {

			double exponent = (this.polesZeros.getPoles().size()
					- getPolesZeros().getZeros().size()/* + this.addedZeros */);
			a0 = a0 * (float) Math.pow(6.283185307179586D, exponent);
		}
		return a0;
	}

	public double calculatedConstant() {
		double localA0 = this.normalizationFactor;
		if (localA0 == 0) {
			localA0 = 1;
		}
		return localA0 * this.sensitivity;
	}

	public double getSensitivity() {
		return this.sensitivity;
	}

	public double getInstrumentGain() {
		return this.instrumentGain;
	}

	public void setInstrumentGain(Double instrumentGain) {
		this.instrumentGain = instrumentGain;
	}

	public SacPolesZeros getPolesZeros() {
		return this.polesZeros;
	}

	public void setPolesZeros(SacPolesZeros polesZeros) {
		this.polesZeros = polesZeros;
	}

	public char getTransferType() {
		return this.transferType;
	}

	public static class SeedBuilder {
		private SacpzResponse response;

		private SeedBuilder() {
			this.response = new SacpzResponse();
		}

		public static SeedBuilder newInstance() {
			return new SeedBuilder();
		}

		public SeedBuilder b033(B033 b033) {
			if (b033 != null && b033.getDescription() != null) {
				response.equipment = b033.getDescription();
			}
			return this;
		}

		public SeedBuilder b050(B050 b050) {
			response.network = b050.getNetworkCode();
			response.station = b050.getStationCode();
			return this;
		}

		public SeedBuilder b052(B052 b052) {
			response.channel = b052.getChannelCode();
			response.location = b052.getLocationCode();
			response.comment = b052.getOptionalComment();
			if (b052.getStartTime() != null) {
				response.start = b052.getStartTime().toZonedDateTime();
			}
			if (b052.getEndTime() != null) {
				response.end = b052.getEndTime().toZonedDateTime();
			}
			response.azimuth = b052.getAzimuth();
			response.depth = b052.getLocalDepth();
			response.dip = b052.getDip();
			response.elevation = b052.getElevation();
			response.latitude = b052.getLatitude();
			response.longitude = b052.getLongitude();
			response.sampleRate = b052.getSampleRate();

			return this;
		}

		public SeedBuilder b053(B053 b053, B034 b034In, B034 b034Out) {
			response.transferType = b053.getTransferFunctionType();
			response.normalizationFactor = b053.getNormalizationFactor();

			if (b034In != null) {
				String inputUnits = b034In.getName();
				String description = b034In.getDescription();
				int gamma = determineGamma(b034In.getName(), b034In.getDescription());

				if (gamma < 0) {
					inputUnits = "";
				} else if (gamma > 0) {
					for (int i = 0; i < gamma; i++) {
						SacZero z = new SacZero(Double.valueOf(0.0D), Double.valueOf(0.0D));
						response.getPolesZeros().addZero(z);
					}
					inputUnits = "M";
					description = "Displacement - M";
				}
				response.originalInputUnits = inputUnits;
				response.originalInputUnitsDescription = description;
				response.setInputUnits(inputUnits, description);
			}
			if (b034Out != null) {
				response.outputUnits = b034Out.getName();
				response.outputUnitsDescription = b034Out.getDescription();
			}

			for (Pole p : b053.getPoles()) {
				double real = p.getReal() == null ? 0 : p.getReal().getValue();
				double imaginary = p.getImaginary() == null ? 0 : p.getImaginary().getValue();

				if ('B' == b053.getTransferFunctionType()) {
					real = real * 2.0D * Math.PI;
					imaginary = imaginary * 2.0D * Math.PI;
				}
				response.getPolesZeros().addPole(new SacPole(real, imaginary));
			}
			for (Zero z : b053.getZeros()) {
				double real = z.getReal() == null ? 0 : z.getReal().getValue();
				double imaginary = z.getImaginary() == null ? 0 : z.getImaginary().getValue();
				if ('B' == b053.getTransferFunctionType()) {
					real = real * 2.0D * Math.PI;
					imaginary = imaginary * 2.0D * Math.PI;
				}
				response.getPolesZeros().addZero(new SacZero(real, imaginary));
			}
			return this;
		}

		public SeedBuilder b058(B058 b058, B034 b034Out) {
			if (b058.getStageNumber() == 0) {
				response.sensitivity = b058.getSensitivity();
				if (b034Out != null) {
					response.outputUnits = b034Out.getName();
				}
			} else if (b058.getStageNumber() == 1) {
				response.instrumentGain = b058.getSensitivity();
			}
			return this;
		}

		public SacpzResponse build() {
			return response;
		}
	}

	public static class Builder {
		private SacpzResponse response;

		private Builder() {
			this.response = new SacpzResponse();
		}

		public Builder network(String network) {
			response.network = network;
			return this;
		}

		public Builder station(String station) {
			response.station = station;
			return this;
		}

		public Builder site(String site) {
			response.site = site;
			return this;
		}

		public Builder channel(String channel) {
			response.channel = channel;
			return this;
		}

		public Builder location(String location) {
			response.location = location;
			return this;
		}

		public Builder created(ZonedDateTime time) {
			response.created = time;
			return this;
		}

		public Builder end(ZonedDateTime time) {
			response.end = time;
			return this;
		}

		public Builder start(ZonedDateTime time) {
			response.start = time;
			return this;
		}

		public Builder latitude(double latitude) {
			response.latitude = latitude;
			return this;
		}

		public Builder longitude(double longitude) {
			response.longitude = longitude;
			return this;
		}

		public Builder dip(Dip dip) {
			return this;
		}

		public Builder dip(double dip) {
			response.dip = dip;
			return this;
		}

		public Builder depth(double depth) {
			response.depth = depth;
			return this;
		}

		public Builder elevation(double elevation) {
			response.elevation = elevation;
			return this;
		}

		public Builder azimuth(double azimuth) {
			response.azimuth = azimuth;
			return this;
		}

		public Builder sampleRate(double sampleRate) {
			response.sampleRate = sampleRate;
			return this;
		}

		public Builder equipment(String equipment) {
			response.equipment = equipment;
			return this;
		}
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder forChannel(Channel channel) {
			Network network = null;
			Station station = channel.getStation();
			if (station != null) {
				response.station = station.getCode();
				network = station.getNetwork();
				if (network != null) {
					response.network = network.getCode();
				}
			}

			response.channel = channel.getCode();
			response.location = channel.getLocationCode();
			response.start = channel.getStartDate();
			response.end = channel.getEndDate();

			if (channel.getAzimuth() != null) {
				response.azimuth = channel.getAzimuth().getValue();
			}
			if (channel.getDepth() != null) {
				response.depth = channel.getDepth().getValue();
			}
			if (channel.getDip() != null) {
				response.dip = channel.getDip().getValue();
			}
			if (channel.getElevation() != null) {
				response.elevation = channel.getElevation().getValue();
			}
			if (channel.getLatitude() != null) {
				response.latitude = channel.getLatitude().getValue();
			}
			if (channel.getLongitude() != null) {
				response.longitude = channel.getLongitude().getValue();
			}
			if (channel.getSampleRate() != null) {
				response.sampleRate = channel.getSampleRate().getValue();
			}
			Equipment e = channel.getSensor();
			if (e != null) {
				response.equipment = e.getDescription();
			}
			Response r = channel.getResponse();
			if (r != null) {
				sensitivity(r.getInstrumentSensitivity());

				if (r.getStage() != null && !r.getStage().isEmpty()) {
					ResponseStage stage = r.getStage().get(0);
					stageGain(stage.getStageGain());
					polesZeros(stage.getPolesZeros());

				}
			}
			return this;
		}
		public Builder stageGain(StageGain stageGain) {
			if (stageGain != null) {
				response.setInstrumentGain(stageGain.getValue());
			}
			return this;
		}
		public Builder sensitivity(Sensitivity sensitivity) {
			if (sensitivity != null) {
				response.sensitivity = sensitivity.getValue();

				if (sensitivity.getInputUnits() != null) {
					response.setInputUnits(sensitivity.getInputUnits().getName(),
							sensitivity.getInputUnits().getDescription());
				}

				if (sensitivity.getOutputUnits() != null) {
					response.outputUnits = sensitivity.getOutputUnits().getName();
				}
			}
			return this;
		}

		public Builder polesZeros(PolesZeros pz) {
			if (pz != null) {
				char fType = ' ';
				if ("LAPLACE (RADIANS/SECOND)".equals(pz.getPzTransferFunctionType())) {
					fType = 'A';
				} else if ("LAPLACE (HERTZ)".equals(pz.getPzTransferFunctionType())) {
					fType = 'B';
				} else if ("DIGITAL (Z-TRANSFORM)".equals(pz.getPzTransferFunctionType())) {
					fType = 'D';
				}
				response.transferType = fType;
				response.normalizationFactor = pz.getNormalizationFactor();
				if (pz.getInputUnits() != null) {
					String inputUnits = pz.getInputUnits().getName();
					String description = pz.getInputUnits().getDescription();
					int gamma = determineGamma(inputUnits, description);

					if (gamma < 0) {
						inputUnits = "";
					} else if (gamma > 0) {
						for (int i = 0; i < gamma; i++) {
							SacZero z = new SacZero(Double.valueOf(0.0D), Double.valueOf(0.0D));
							response.getPolesZeros().addZero(z);
						}
						inputUnits = "M";
						description = "Displacement - M";
					}
					response.originalInputUnits = inputUnits;
					response.originalInputUnitsDescription = description;
					response.setInputUnits(inputUnits, description);
				}

				/*if (pz.getOutputUnits() != null) {
					response.outputUnits = pz.getOutputUnits().getName();
					response.outputUnitsDescription = pz.getOutputUnits().getDescription();
				}*/
				if (pz.getPole() != null) {
					for (PoleZero p : pz.getPole()) {
						double real = p.getReal() == null ? 0 : p.getReal().getValue();
						double imaginary = p.getImaginary() == null ? 0 : p.getImaginary().getValue();

						if ('B' == fType) {
							real = real * 2.0D * Math.PI;
							imaginary = imaginary * 2.0D * Math.PI;
						}
						response.getPolesZeros().addPole(new SacPole(real, imaginary));
					}
				}
				for (PoleZero z : pz.getZero()) {
					double real = z.getReal() == null ? 0 : z.getReal().getValue();
					double imaginary = z.getImaginary() == null ? 0 : z.getImaginary().getValue();
					if ('B' == fType) {
						real = real * 2.0D * Math.PI;
						imaginary = imaginary * 2.0D * Math.PI;
					}
					response.getPolesZeros().addZero(new SacZero(real, imaginary));
				}
			}
			return this;
		}

		public SacpzResponse build() {
			return response;
		}
	}

	private static int determineGamma(String inUnitsName, String inUnitsDesc) {
		if (inUnitsDesc == null || inUnitsDesc == null) {
			return -1;
		}
		String inDescription = inUnitsDesc.toUpperCase();
		String inName = inUnitsName.toUpperCase();

		if (inDescription != null && !inDescription.equals("")) {
			if (inDescription.indexOf("VEL") != -1)
				return 1;
			if (inDescription.indexOf("ACCEL") != -1)
				return 2;
			if (inDescription.indexOf("DISP") != -1) {
				return 0;
			}
			return -1;
		}
		if (inName != null && !inName.equals("")) {
			if (inName.equals("M"))
				return 0;
			if (inName.equals("M/S"))
				return 1;
			if (inName.equals("M/S**2")) {
				return 2;
			}
			return -1;
		}

		return -1;
	}

	@Override
	public String toString() {
		return "SacpzResponse [network=" + network + ", station=" + station + ", channel=" + channel + ", location="
				+ location + ", start=" + start + ", end=" + end + "]";
	}

}
