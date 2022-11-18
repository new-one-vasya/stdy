package nw.one.vasya.lesson4;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * Получаем команыд в параметрах приложения передаём их серверу
 * Переводим всё в нижний регистр
 * Когда кончились - отправляем stop
 * Если stop сулчился раньше - логируем что не всё успели отправить и выходим
 */
public class MainClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainClient.class);
    private static boolean NEED_SAND_STOP = true;

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buff;

            for(String cmd : args) {
                buff = cmd.toLowerCase().getBytes();
                DatagramPacket packet = new DatagramPacket(buff, buff.length, address, 9000);
                socket.send(packet);

                LOGGER.debug("Client send: {}", cmd);

                if ("stop".equalsIgnoreCase(cmd)) {
                    NEED_SAND_STOP = false;
                    break;
                }

                packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                LOGGER.debug("Received {}", new String(packet.getData()));
            }

            if (NEED_SAND_STOP) {
                LOGGER.debug("Forced STOP");
                buff = "stop".getBytes();
                DatagramPacket packet = new DatagramPacket(buff, buff.length, address, 9000);
                socket.send(packet);
            }

            LOGGER.debug("exit");
        }
    }
}