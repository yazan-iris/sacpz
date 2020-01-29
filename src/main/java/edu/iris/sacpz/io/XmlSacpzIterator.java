package edu.iris.sacpz.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.iris.sacpz.SacpzResponse;
import edu.iris.station.io.StationXmlIterator;
import edu.iris.station.model.Channel;
import edu.iris.station.model.Station;

public class XmlSacpzIterator implements Iterator<SacpzResponse>, AutoCloseable {

	private StationXmlIterator stationIterator;

	private SacpzResponse cachedResponse;
	private boolean finished;

	public XmlSacpzIterator(InputStream inputStream) throws IOException {
		stationIterator = new StationXmlIterator(inputStream);
	}

	@Override
	public boolean hasNext() {
		if (cachedResponse != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			SacpzResponse response = nextSacpzResponse();
			if (response == null) {
				finished = true;
				return false;
			} else {
				cachedResponse = response;
				return true;
			}
		}
	}

	@Override
	public SacpzResponse next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more responses");
		}
		final SacpzResponse currentResponse = cachedResponse;
		cachedResponse=null;
		return currentResponse;
	}

	private Iterator<Channel> channelIteartor;

	private Iterator<Channel> nextchannelIterator() {
		if (stationIterator == null) {
			return null;
		}
		if (stationIterator.hasNext()) {
			Station station = stationIterator.next();
			if (station != null && station.getChannels() != null) {
				if (!station.getChannels().isEmpty()) {
					return station.getChannels().iterator();
				}
			}
		}
		return null;
	}

	private SacpzResponse nextSacpzResponse() {
		if (channelIteartor == null || !channelIteartor.hasNext()) {
			channelIteartor = nextchannelIterator();
			if (channelIteartor == null) {
				return null;
			}
		}
		Channel channel = channelIteartor.next();

		return SacpzResponse.Builder.newInstance().forChannel(channel).build();
		/*SacpzResponse response = new SacpzResponse();

		Network network = null;
		Station station = channel.getStation();
		if (station != null) {
			response.setStation(station.getCode());
			network = station.getNetwork();
			if (network != null) {
				response.setNetwork(network.getCode());
			}
		}

		response.setChannel(channel.getCode());
		response.setLocation(channel.getLocationCode());

		if (channel.getStartDate() != null) {
			response.setStart(channel.getStartDate());
		}
		if (channel.getEndDate() != null) {
			response.setEnd(channel.getEndDate());
		}
		if (channel.getAzimuth() != null) {
			response.setAzimuth(channel.getAzimuth().getValue());
		}
		if (channel.getDepth() != null) {
			response.setDepth(channel.getDepth().getValue());
		}
		if (channel.getDip() != null) {
			response.setDip(channel.getDip().getValue());
		}
		if (channel.getElevation() != null) {
			response.setElevation(channel.getElevation().getValue());
		}
		if (channel.getLatitude() != null) {
			response.setLatitude(channel.getLatitude().getValue());
		}
		if (channel.getLongitude() != null) {
			response.setLongitude(channel.getLongitude().getValue());
		}
		if (channel.getSampleRate() != null) {
			response.setSampleRate(channel.getSampleRate().getValue());
		}
		Equipment e = channel.getSensor();
		if (e != null) {
			response.setEquipment(e.getDescription());
		}
		
		Response r = channel.getResponse();
		if (r != null) {
			Sensitivity sensitivity = r.getInstrumentSensitivity();

			if (sensitivity != null) {
				response.setSensitivity(sensitivity.getValue());

				if (sensitivity.getInputUnits() != null) {
					response.setInputUnits(sensitivity.getInputUnits().getName(),
							sensitivity.getInputUnits().getDescription());
				}

				if (sensitivity.getOutputUnits() != null) {
					response.setOutputUnits(sensitivity.getOutputUnits().getName());
				}
			}
			if (r.getStage() != null && !r.getStage().isEmpty()) {
				ResponseStage stage = r.getStage().get(0);
				StageGain gain = stage.getStageGain();
				if (gain != null) {
					response.setInstrumentGain(gain.getValue());
				}
				PolesZeros pz = stage.getPolesZeros();
				if (pz != null) {
					char fType = ' ';
					if ("LAPLACE (RADIANS/SECOND)".equals(pz.getPzTransferFunctionType())) {
						fType = 'A';
					} else if ("LAPLACE (HERTZ)".equals(pz.getPzTransferFunctionType())) {
						fType = 'B';
					} else if ("DIGITAL (Z-TRANSFORM)".equals(pz.getPzTransferFunctionType())) {
						fType = 'D';
					}
					response.setTransferType(fType);
					response.setnFactor(pz.getNormalizationFactor());
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
						response.setOriginalInputUnits(inputUnits);
						response.setOriginalInputUnitsDescription(description);
						response.setInputUnits(inputUnits, description);
					}

					if (pz.getOutputUnits() != null) {
						response.setOutputUnits(pz.getOutputUnits().getName());
						response.setOutputUnitsDescription(pz.getOutputUnits().getDescription());
					}
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
			}
		}
		return response;*/
	}

	private int determineGamma(String inUnitsName, String inUnitsDesc) {
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
	public void close() throws Exception {
		finished = true;
		cachedResponse = null;
		if (stationIterator != null) {
			stationIterator.close();
		}

	}
}
