package org.example.ipchecker;

import java.net.NetworkInterface;
import java.net.SocketException;

public class IpChecker {
    public static void main(String[] args) throws SocketException {
        var interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            var iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            for (var addr: iface.getInterfaceAddresses()) {
                var addrStr = addr.getAddress().toString();
                if (addrStr.contains(":")) {
                    // ipv6 address
                    System.out.printf("%s %s, prefix length %s\n", iface.getDisplayName(), addrStr.split("%")[0], addr.getNetworkPrefixLength());
                } else {
                    System.out.printf("%s %s, subnet mask %s\n", iface.getDisplayName(), addr.getAddress(), formatMask(addr.getNetworkPrefixLength()));
                }
            }
        }
    }

    public static String formatMask(short maskLength) {
        var sb = new StringBuilder();
        int b = 0;
        for (int i = 1; i <= 32; ++i) {
            b <<= 1;
            if (maskLength > 0) {
                b |= 1;
                maskLength--;
            }
            if (i % 8 == 0) {
                sb.append(b);
                if (i != 32) {
                    sb.append(".");
                }
                b = 0;
            }
        }
        return sb.toString();
    }
}