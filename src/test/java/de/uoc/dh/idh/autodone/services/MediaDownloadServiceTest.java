package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.services.MediaDownloadService.readImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

class MediaDownloadServiceTest {

	@Test()
	void decodesRegularImages() throws Exception {
		var buffer = new ByteArrayOutputStream();
		ImageIO.write(new BufferedImage(64, 32, TYPE_INT_RGB), "png", buffer);

		var image = readImage(buffer.toByteArray());

		assertEquals(64, image.getWidth());
		assertEquals(32, image.getHeight());
	}

	@Test()
	void rejectsPixelBombsBeforeDecoding() throws Exception {
		// 26000 x 26000 RGB declared in a tiny PNG: ImageIO.read allocates ~2 GB for it
		var exception = assertThrows(IllegalArgumentException.class, () -> readImage(png(26_000, 26_000)));

		assertTrue(exception.getMessage().contains("26000x26000"));
	}

	@Test()
	void rejectsNonImages() {
		assertThrows(IllegalArgumentException.class, () -> readImage("<html></html>".getBytes(US_ASCII)));
	}

	//

	private static byte[] png(int width, int height) throws Exception {
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
