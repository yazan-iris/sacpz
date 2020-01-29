package edu.iris.sacpz.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SacFileUtils {

	public static String toString(File file) throws IOException {
		validateFile(file);
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			return SacIOUtils.toString(inputStream);
		} catch (final IOException | RuntimeException ex) {
			if (inputStream != null) {
				inputStream.close();
			}
			throw ex;
		}
	}

	public static XmlSacpzIterator xmlSacpzIterator(File file) throws IOException {
		validateFile(file);
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			return SacIOUtils.xmlSacpzIterator(inputStream);
		} catch (final IOException | RuntimeException ex) {
			if (inputStream != null) {
				inputStream.close();
			}
			throw ex;
		}
	}

	public static SeedSacpzIterator seedSacpzIterator(File file) throws IOException {
		validateFile(file);
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			return SacIOUtils.seedSacpzIterator(inputStream);
		} catch (final IOException | RuntimeException ex) {
			if (inputStream != null) {
				inputStream.close();
			}
			throw ex;
		}
	}

	public static void validateFile(final File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
	}
}
