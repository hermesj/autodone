package de.uoc.dh.idh.autodone.utils;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class NetUtils {

	// host name or IPv4 address with optional port, nothing else (no path, user info, ...)
	private static final Pattern DOMAIN = compile(
			"^(?=[^:]{1,253}(:|$))[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)*(:[0-9]{1,5})?$",
			CASE_INSENSITIVE);

	//

	public static String requireDomain(String domain) {
		if (domain == null || !DOMAIN.matcher(domain.trim()).matches()) {
			throw new IllegalArgumentException("Invalid server domain: " + domain);
		}

		return domain.trim();
	}

	public static String hostOf(String domain) {
		var colon = domain.indexOf(':');
		return colon < 0 ? domain : domain.substring(0, colon);
	}

	//

	public static void requirePublicHost(String host) throws UnknownHostException {
		for (var address : InetAddress.getAllByName(host)) {
			if (isInternal(address)) {
				throw new SecurityException("Private/internal host not allowed: " + host);
			}
		}
	}

	public static boolean isInternal(InetAddress address) {
		if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
				|| address.isSiteLocalAddress() || address.isMulticastAddress()) {
			return true;
		}

		var bytes = address.getAddress();

		if (address instanceof Inet4Address) {
			var first = bytes[0] & 0xff;
			var second = bytes[1] & 0xff;

			// 0.0.0.0/8, 100.64.0.0/10 (carrier-grade NAT), 192.0.0.0/24, 198.18.0.0/15, 240.0.0.0/4
			return first == 0 || (first == 100 && (second & 0xc0) == 64) || (first == 192 && second == 0 && bytes[2] == 0)
					|| (first == 198 && (second & 0xfe) == 18) || first >= 240;
		}

		if (address instanceof Inet6Address) {
			// fc00::/7 (unique local addresses)
			return (bytes[0] & 0xfe) == 0xfc;
		}

		return false;
	}

	//

	private NetUtils() {
	}

}
