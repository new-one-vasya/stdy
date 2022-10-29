package nw.one.vasya.lesson1;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Main {

    private static final String format = "name = %8s, isUp = %5b, isVirtual = %5b, isLoopback = %5b\n";

    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            var networkInterface = networkInterfaces.nextElement();
            printInfo(networkInterface);
        }
    }

    private static void printInfo(NetworkInterface nwi) throws SocketException {
        System.out.format(format, nwi.getName(), nwi.isUp(),  nwi.isVirtual(), nwi.isLoopback());
    }

}