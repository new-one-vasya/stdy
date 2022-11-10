package nw.one.vasya.lesson1;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class Main {

    /**
     * Закрытый потому что утилитарный.
     */
    private Main() {
    }

    /**
     * Формат отображения.
     */
    private static final String FORMAT = "name = %8s, isUp = %5b, isVirtual = %5b, isLoopback = %5b\n";

    /**
     * main метод.
     * @param args не используются
     * @throws SocketException привнесён {@link java.net.NetworkInterface#getNetworkInterfaces()}
     */
    public static void main(final String[] args) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            var networkInterface = networkInterfaces.nextElement();
            printInfo(networkInterface);
        }
    }

    private static void printInfo(final NetworkInterface nwi) throws SocketException {
        System.out.format(FORMAT, nwi.getName(), nwi.isUp(),  nwi.isVirtual(), nwi.isLoopback());
    }

}
