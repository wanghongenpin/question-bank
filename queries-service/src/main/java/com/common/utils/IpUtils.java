package com.common.utils;

import java.net.*;
import java.util.Enumeration;

/**
 * @author wanghongen
 * 2018/7/2
 */
public class IpUtils {
    public static final String IP_UNKNOWN = "unknown ip";

    public static String getLocalIp() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return getLocalIpFromWindows();
        } else {
            return getLocalIpFromLinux();
        }
    }

    public static String getMacAddr() {
        String MacAddress = "";
        StringBuilder str = new StringBuilder();
        try {
            NetworkInterface NIC = NetworkInterface.getByName("eth0");
            byte[] buf = NIC.getHardwareAddress();
            for (byte aBuf : buf) {
                str.append(byteHEX(aBuf));
            }
            MacAddress = str.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return MacAddress;
    }

    public static String getLocalIpFromLinux() {
        String ip = IP_UNKNOWN;
        try {
            Enumeration<?> e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (ni.getName().equals("eth0")) {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (IP_UNKNOWN.equals(ip)) {
            ip = getLocalIpFromWindows();
        }
        return ip;
    }

    public static String byteHEX(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        return new String(ob);
    }

    public static String getLocalIpFromWindows() {
        String localIp;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localIp = IP_UNKNOWN;
        }
        return localIp;
    }

    public static String getIpWithBracketWrap() {
        return QuestionStringUtils.wrapStringWithBracket(getLocalIp() + "," + getHostName());
    }

    public static String getHostName() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostName = "hostname";
        }
        return hostName;
    }
}
