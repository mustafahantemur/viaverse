package app.viaverse.identity.shared.security;

import app.viaverse.identity.config.HttpProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ClientIpResolver {
    private final List<TrustedProxyMatcher> trustedProxies;

    public ClientIpResolver(HttpProperties properties) {
        this.trustedProxies = properties.getTrustedProxies().stream()
                .filter(value -> value != null && !value.isBlank())
                .map(TrustedProxyMatcher::parse)
                .toList();
    }

    public String resolve(HttpServletRequest request) {
        String directPeer = normalizeIp(request.getRemoteAddr());
        if (!isTrustedProxy(directPeer)) {
            return directPeer;
        }

        List<String> chain = forwardedChain(request);
        if (chain.isEmpty()) {
            return directPeer;
        }

        List<String> nearestFirst = new ArrayList<>(chain);
        Collections.reverse(nearestFirst);
        for (String hop : nearestFirst) {
            String normalizedHop = normalizeIp(hop);
            if (!isTrustedProxy(normalizedHop)) {
                return normalizedHop;
            }
        }
        return normalizeIp(chain.get(0));
    }

    private List<String> forwardedChain(HttpServletRequest request) {
        List<String> forwarded = parseForwarded(request.getHeader("Forwarded"));
        if (!forwarded.isEmpty()) {
            return forwarded;
        }
        return parseXForwardedFor(request.getHeader("X-Forwarded-For"));
    }

    private List<String> parseForwarded(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String element : value.split(",")) {
            for (String parameter : element.split(";")) {
                String trimmed = parameter.trim();
                if (!trimmed.regionMatches(true, 0, "for=", 0, 4)) {
                    continue;
                }
                String candidate = stripQuotes(trimmed.substring(4).trim());
                if (!candidate.isBlank() && !"unknown".equalsIgnoreCase(candidate)) {
                    result.add(stripPort(candidate));
                }
            }
        }
        return result;
    }

    private List<String> parseXForwardedFor(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String candidate : value.split(",")) {
            String normalized = stripPort(candidate.trim());
            if (!normalized.isBlank() && !"unknown".equalsIgnoreCase(normalized)) {
                result.add(normalized);
            }
        }
        return result;
    }

    private String stripQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private String stripPort(String value) {
        if (value.startsWith("[") && value.contains("]")) {
            return value.substring(1, value.indexOf(']'));
        }
        int firstColon = value.indexOf(':');
        int lastColon = value.lastIndexOf(':');
        if (firstColon > 0 && firstColon == lastColon) {
            return value.substring(0, firstColon);
        }
        return value;
    }

    private String normalizeIp(String value) {
        return value == null || value.isBlank() ? "unknown" : stripPort(value.trim());
    }

    private boolean isTrustedProxy(String ip) {
        return trustedProxies.stream().anyMatch(proxy -> proxy.matches(ip));
    }

    private sealed interface TrustedProxyMatcher permits ExactMatcher, CidrMatcher {
        boolean matches(String ip);

        static TrustedProxyMatcher parse(String value) {
            String trimmed = value.trim();
            return trimmed.contains("/") ? CidrMatcher.parse(trimmed) : new ExactMatcher(trimmed);
        }
    }

    private record ExactMatcher(String expectedIp) implements TrustedProxyMatcher {
        @Override
        public boolean matches(String ip) {
            return expectedIp.equals(ip);
        }
    }

    private record CidrMatcher(byte[] network, int prefixLength) implements TrustedProxyMatcher {
        static CidrMatcher parse(String cidr) {
            String[] parts = cidr.split("/", 2);
            try {
                InetAddress address = InetAddress.getByName(parts[0]);
                int prefix = Integer.parseInt(parts[1]);
                int maxBits = address.getAddress().length * Byte.SIZE;
                if (prefix < 0 || prefix > maxBits) {
                    throw new IllegalArgumentException("Invalid CIDR prefix: " + cidr);
                }
                return new CidrMatcher(address.getAddress(), prefix);
            } catch (UnknownHostException | NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid trusted proxy CIDR: " + cidr, exception);
            }
        }

        @Override
        public boolean matches(String ip) {
            try {
                byte[] candidate = InetAddress.getByName(ip).getAddress();
                if (candidate.length != network.length) {
                    return false;
                }
                int fullBytes = prefixLength / Byte.SIZE;
                int remainingBits = prefixLength % Byte.SIZE;
                for (int i = 0; i < fullBytes; i++) {
                    if (candidate[i] != network[i]) {
                        return false;
                    }
                }
                if (remainingBits == 0) {
                    return true;
                }
                int mask = 0xFF << (Byte.SIZE - remainingBits);
                return (candidate[fullBytes] & mask) == (network[fullBytes] & mask);
            } catch (UnknownHostException exception) {
                return false;
            }
        }
    }
}
