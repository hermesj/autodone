package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.utils.NetUtils.hostOf;
import static de.uoc.dh.idh.autodone.utils.NetUtils.isInternal;
import static de.uoc.dh.idh.autodone.utils.NetUtils.requireDomain;
import static de.uoc.dh.idh.autodone.utils.NetUtils.requirePublicHost;
import static java.net.InetAddress.getByName;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NetUtilsTest {

	@ParameterizedTest()
	@ValueSource(strings = { "mastodon.social", "fedihum.org", "social.uni-koeln.de", "192.168.0.59:3000",
			"localhost:3000", "xn--mstdn-kva.example" })
	void acceptsHostNamesWithOptionalPort(String domain) {
		assertEquals(domain, requireDomain(domain));
	}

	@ParameterizedTest()
	@ValueSource(strings = { "", " ", "intranet.local/secret#", "intranet.local?x=", "user@intranet.local",
			"https://mastodon.social", "mastodon.social/", "[::1]", "-bad.example", "a..b", "host:port" })
	void rejectsAnythingButHostAndPort(String domain) {
		assertThrows(IllegalArgumentException.class, () -> requireDomain(domain));
	}

	@Test()
	void rejectsMissingDomain() {
		assertThrows(IllegalArgumentException.class, () -> requireDomain(null));
	}

	@Test()
	void trimsAndSplitsPort() {
		assertEquals("mastodon.social", requireDomain(" mastodon.social "));
		assertEquals("192.168.0.59", hostOf("192.168.0.59:3000"));
		assertEquals("mastodon.social", hostOf("mastodon.social"));
	}

	@ParameterizedTest()
	@ValueSource(strings = { "0.0.0.0", "127.0.0.1", "10.1.2.3", "172.16.0.1", "192.168.0.59", "169.254.169.254",
			"100.64.0.1", "192.0.0.8", "198.18.0.1", "224.0.0.1", "255.255.255.255", "::1", "fe80::1", "fd00::1",
			"::ffff:10.0.0.1" })
	void detectsInternalAddresses(String address) throws Exception {
		assertTrue(isInternal(getByName(address)));
		assertThrows(SecurityException.class, () -> requirePublicHost(address));
	}

	@ParameterizedTest()
	@ValueSource(strings = { "8.8.8.8", "134.95.0.1", "100.128.0.1", "2a00:1450:4001::1" })
	void acceptsPublicAddresses(String address) throws Exception {
		assertFalse(isInternal(getByName(address)));
		assertDoesNotThrow(() -> requirePublicHost(address));
	}

}
