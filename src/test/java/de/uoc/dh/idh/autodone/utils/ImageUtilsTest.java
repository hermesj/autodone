package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.utils.ImageUtils.MAX_STORED_SIZE;
import static de.uoc.dh.idh.autodone.utils.ImageUtils.readImage;
import static de.uoc.dh.idh.autodone.utils.ImageUtils.shrink;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

class ImageUtilsTest {

	@Test()
	void decodesRegularImages() throws Exception {
		var image = readImage(png(new BufferedImage(64, 32, TYPE_INT_RGB)));

		assertEquals(64, image.getWidth());
		assertEquals(32, image.getHeight());
	}

	@Test()
	void rejectsPixelBombsBeforeDecoding() throws Exception {
		// 26000 x 26000 RGB declared in a tiny PNG: ImageIO.read allocates ~2 GB for it
		var exception = assertThrows(IllegalArgumentException.class, () -> readImage(header(26_000, 26_000)));

		assertTrue(exception.getMessage().contains("26000x26000"));
	}

	@Test()
	void rejectsNonImages() {
		assertThrows(IllegalArgumentException.class, () -> readImage("<html></html>".getBytes(US_ASCII)));
		assertThrows(IllegalArgumentException.class, () -> shrink("text/html", new byte[10], 512, 512, "png"));
		assertThrows(IllegalArgumentException.class, () -> shrink(null, new byte[10], 512, 512, "png"));
	}

	@Test()
	void keepsSmallFilesUnchanged() throws Exception {
		var file = png(new BufferedImage(64, 32, TYPE_INT_RGB));
		var image = shrink("image/png", file, 512, 512, "png");

		assertEquals("image/png", image.contentType());
		assertArrayEquals(file, image.file());
	}

	@Test()
	void scalesLargeFilesDown() throws Exception {
		// random pixels do not compress, so this PNG is well above MAX_STORED_SIZE
		var source = new BufferedImage(1600, 800, TYPE_INT_RGB);
		var random = new Random(1);

		for (var x = 0; x < source.getWidth(); x++) {
			for (var y = 0; y < source.getHeight(); y++) {
				source.setRGB(x, y, random.nextInt());
			}
		}

		var file = png(source);
		assertTrue(file.length > MAX_STORED_SIZE);

		var image = shrink("image/jpeg", file, 512, 512, "png");
		var stored = ImageIO.read(new ByteArrayInputStream(image.file()));

		assertEquals("image/png", image.contentType());
		assertEquals(512, stored.getWidth());
		assertEquals(256, stored.getHeight());
		assertTrue(image.file().length < MAX_STORED_SIZE);
	}

	//

	private static byte[] png(BufferedImage image) throws Exception {
		var buffer = new ByteArrayOutputStream();
		ImageIO.write(image, "png", buffer);
		return buffer.toByteArray();
	}

	private static byte[] header(int width, int height) throws Exception {
		var buffer = new ByteArrayOutputStream();
		var output = new DataOutputStream(buffer);

		output.write(new byte[] { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n' });

		var header = new ByteArrayOutputStream();
		var data = new DataOutputStream(header);
		data.writeInt(width);
		data.writeInt(height);
		data.write(new byte[] { 8, 2, 0, 0, 0 }); // 8 bit RGB, deflate, no filter, no interlace

		chunk(output, "IHDR", header.toByteArray());
		chunk(output, "IEND", new byte[0]);
		return buffer.toByteArray();
	}

	private static void chunk(DataOutputStream output, String type, byte[] data) throws Exception {
		var crc = new CRC32();
		crc.update(type.getBytes(US_ASCII));
		crc.update(data);

		output.writeInt(data.length);
		output.write(type.getBytes(US_ASCII));
		output.write(data);
		output.writeInt((int) crc.getValue());
	}

}
