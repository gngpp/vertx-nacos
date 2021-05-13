package com.zf1976.naming.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
public class InetUtils implements Closeable {
    private final ExecutorService executorService;
    private final InetUtilsProperties properties;
    private final Log log = LogFactory.getLog(InetUtils.class);

    public InetUtils(final InetUtilsProperties properties) {
        this.properties = properties;
        this.executorService = Executors.newSingleThreadExecutor((r) -> {
            Thread thread = new Thread(r);
            thread.setName("spring.cloud.inetutils");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void close() {
        this.executorService.shutdown();
    }

    public InetUtils.HostInfo findFirstNonLoopbackHostInfo() {
        InetAddress address = this.findFirstNonLoopbackAddress();
        if (address != null) {
            return this.convertAddress(address);
        } else {
            InetUtils.HostInfo hostInfo = new InetUtils.HostInfo();
            hostInfo.setHostname(this.properties.getDefaultHostname());
            hostInfo.setIpAddress(this.properties.getDefaultIpAddress());
            return hostInfo;
        }
    }

    public InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;

        try {
            int lowest = 2147483647;
            Enumeration nics = NetworkInterface.getNetworkInterfaces();

            label61:
            while(true) {
                NetworkInterface ifc;
                do {
                    while(true) {
                        do {
                            if (!nics.hasMoreElements()) {
                                break label61;
                            }

                            ifc = (NetworkInterface)nics.nextElement();
                        } while(!ifc.isUp());

                        this.log.trace("Testing interface: " + ifc.getDisplayName());
                        if (ifc.getIndex() >= lowest && result != null) {
                            if (result != null) {
                                continue;
                            }
                            break;
                        }

                        lowest = ifc.getIndex();
                        break;
                    }
                } while(this.ignoreInterface(ifc.getDisplayName()));

                Enumeration addrs = ifc.getInetAddresses();

                while(addrs.hasMoreElements()) {
                    InetAddress address = (InetAddress)addrs.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && this.isPreferredAddress(address)) {
                        this.log.trace("Found non-loopback interface: " + ifc.getDisplayName());
                        result = address;
                    }
                }
            }
        } catch (IOException var8) {
            this.log.error("Cannot get first non-loopback address", var8);
        }

        if (result != null) {
            return result;
        } else {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException var7) {
                this.log.warn("Unable to retrieve localhost");
                return null;
            }
        }
    }

    boolean isPreferredAddress(InetAddress address) {
        if (this.properties.isUseOnlySiteLocalInterfaces()) {
            boolean siteLocalAddress = address.isSiteLocalAddress();
            if (!siteLocalAddress) {
                this.log.trace("Ignoring address: " + address.getHostAddress());
            }

            return siteLocalAddress;
        } else {
            List<String> preferredNetworks = this.properties.getPreferredNetworks();
            if (preferredNetworks.isEmpty()) {
                return true;
            } else {
                Iterator var3 = preferredNetworks.iterator();

                String regex;
                String hostAddress;
                do {
                    if (!var3.hasNext()) {
                        this.log.trace("Ignoring address: " + address.getHostAddress());
                        return false;
                    }

                    regex = (String)var3.next();
                    hostAddress = address.getHostAddress();
                } while(!hostAddress.matches(regex) && !hostAddress.startsWith(regex));

                return true;
            }
        }
    }

    boolean ignoreInterface(String interfaceName) {
        Iterator var2 = this.properties.getIgnoredInterfaces().iterator();

        String regex;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            regex = (String)var2.next();
        } while(!interfaceName.matches(regex));

        this.log.trace("Ignoring interface: " + interfaceName);
        return true;
    }

    public InetUtils.HostInfo convertAddress(final InetAddress address) {
        InetUtils.HostInfo hostInfo = new InetUtils.HostInfo();
        ExecutorService var10000 = this.executorService;
        address.getClass();
        Future result = var10000.submit(address::getHostName);

        String hostname;
        try {
            hostname = (String)result.get((long)this.properties.getTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (Exception var6) {
            this.log.info("Cannot determine local hostname");
            hostname = "localhost";
        }

        hostInfo.setHostname(hostname);
        hostInfo.setIpAddress(address.getHostAddress());
        return hostInfo;
    }

    public static class HostInfo {
        public boolean override;
        private String ipAddress;
        private String hostname;

        public HostInfo(String hostname) {
            this.hostname = hostname;
        }

        public HostInfo() {
        }

        public int getIpAddressAsInt() {
            InetAddress inetAddress = null;
            String host = this.ipAddress;
            if (host == null) {
                host = this.hostname;
            }

            try {
                inetAddress = InetAddress.getByName(host);
            } catch (UnknownHostException var4) {
                throw new IllegalArgumentException(var4);
            }

            return ByteBuffer.wrap(inetAddress.getAddress()).getInt();
        }

        public boolean isOverride() {
            return this.override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }

        public String getIpAddress() {
            return this.ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getHostname() {
            return this.hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }
}
