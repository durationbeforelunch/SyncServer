package com.sync.server.network.udp;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class MulticastServer {

    @Value("${udp.server.key}")
    private String serverKey;

    @Value("${udp.server.group}")
    private String groupIP;

    @Value("${udp.server.port}")
    private int port;

    private Thread senderThread;

    @Getter
    private ServerStatus serverStatus;

    public void startClientFinder() {

        sender();
        serverStatus = ServerStatus.UP;

    }

    public void stopClientFinder() {

        if (!senderThread.isInterrupted()) {
            senderThread.interrupt();
        } else {
            log.info("Sender Thread already interrupted");
        }

    }

    /*
     * RU: Метод, позволяющий серверу использовать многоадресную рассылку(Multicast)
     * Нужен для того, чтобы клиент смог обнаружить сервер, к которому необходимо подключиться
     *
     * EN: Method, allowing server to use Multicast, for purpose how Client will find server
     * */
    private void sender() {

        // RU: Создаем новый поток для постоянного отправления пакетов
        // EN: Creating new thread for continuous sending UDP packets
        new Thread(() -> {
            // RU: Присваиваем поток переменной, для дальнейших манипуляций
            // EN: Setting thread to a variable for further manipulations
            senderThread = Thread.currentThread();

            // RU: Создаем узел, через который будем отправлять UDP пакеты данных
            // EN: Creating node, which through we will send UDP data packets
            try (DatagramSocket socket = new DatagramSocket()) {
                log.info("Multicast server started with GROUP IP:{} and PORT:{}", groupIP, port);

                // RU: Указываем на какой адрес будет идти вещание
                // EN: Indicating address which broadcast will go
                InetAddress group = InetAddress.getByName(groupIP);

                // RU: Переводим ключ в байтовый тип данных
                // EN: Transferring key into byte data type
                byte[] byteKey = serverKey.getBytes(StandardCharsets.UTF_8);

         /*        RU: Создаем пакет данных, который будет отправлен через узел
                   Передаем через конструктор байтовый массив, длину массива, адрес вещания и порт на который будет отправлен пакет данных

                   EN: Creating data packet, which one will be send through node
                   Passing byte array, array length, broadcast address and port to which data will be sent into constructor

                 */
                DatagramPacket packet = new DatagramPacket(byteKey, byteKey.length, group, port);

                synchronized (this) {

                    // RU: Здесь создаем таймер, который будет отправлять пакеты каждые n миллисекунд
                    // EN: Creating timer, which one will send packets every n milliseconds
                    while (true) {
                        socket.send(packet);
                        wait(1000);
                    }

                }

            } catch (IOException e) {
                log.info("Server Crashed");
                e.printStackTrace();
                serverStatus = ServerStatus.DOWN;
            } catch (InterruptedException e) {
                log.info("Thread interrupted!");
                senderThread.interrupt();
                serverStatus = ServerStatus.DOWN;
            }

        }).start();

    }
}