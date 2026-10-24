package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_FORMAT;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_X;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_Y;
import static java.awt.Image.SCALE_FAST;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javax.imageio.ImageIO.createImageInputStream;
import static javax.imageio.ImageIO.getImageReaders;
import static javax.imageio.ImageIO.write;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

	// largest file accepted from a download or an upload
	public static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

	// smaller files are stored unchanged, larger ones are scaled down first
	public static final int MAX_STORED_SIZE = 1024000;

	// ~160 MB when decoded as ARGB
	public static final long MAX_IMAGE_PIXELS = 40_000_000;

	//

	public record Image(String contentType, byte[] file) {
	}

	//

	public static Image shrink(String contentType, byte[] file) throws IOException {
		return shrink(contentType, file, AUTODONE_IMG_SIZE_X, AUTODONE_IMG_SIZE_Y, AUTODONE_IMG_FORMAT);
	}

	public static Image shrink(String contentType, byte[] file, int sizeX, int sizeY, String format)
			throws IOException {
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Not an image file: " + contentType);
		}

		if (file.length < MAX_STORED_SIZE) {
			return new Image(contentType, file);
		}

		var source = readImage(file);
		var scaleX = source.getWidth();
		var scaleY = source.getHeight();

		if (scaleX > sizeX) {
			scaleX = sizeX;
			scaleY = (scaleX * source.getHeight()) / source.getWidth();
		}

		if (scaleY > sizeY) {
			scaleY = sizeY;
			scaleX = (scaleY * source.getWidth()) / source.getHeight();
		}

		var buffer = new ByteArrayOutputStream();
		var target = new BufferedImage(scaleX, scaleY, TYPE_INT_ARGB);
		var scaled = source.getScaledInstance(scaleX, scaleY, SCALE_FAST);
		var graphics = target.createGraphics();

		try {
			graphics.drawImage(scaled, 0, 0, null);
		} finally {
			graphics.dispose();
		}

		write(target, format, buffer);
		return new Image("image/" + format, buffer.toByteArray());
	}

	/**
	 * Decodes the first image, but checks its dimensions from the header first: a
	 * few hundred bytes of PNG can declare billions of pixels and exhaust the heap.
	 */
	public static BufferedImage readImage(byte[] bytes) throws IOException {
		try (var input = createImageInputStream(new ByteArrayInputStream(bytes))) {
			var readers = input == null ? null : getImageReaders(input);

			if (readers == null || !readers.hasNext()) {
				throw new IllegalArgumentException("Could not read image");
			}

			var reader = readers.next();

			try {
				reader.setInput(input, true, true);

				var width = (long) reader.getWidth(0);
				var height = (long) reader.getHeight(0);

				if (width * height > MAX_IMAGE_PIXELS) {
					throw new IllegalArgumentException("Image too large: " + width + "x" + height + " pixels");
				}

				return reader.read(0);
			} finally {
				reader.dispose();
			}
		}
	}

	//

	private ImageUtils() {
	}

}
