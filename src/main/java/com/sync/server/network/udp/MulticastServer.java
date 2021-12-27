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
     * Метод, позволяющий серверу использовать многоадресную рассылку(Multicast)
     * Нужен для того, чтобы клиент смог обнаружить сервер, к которому необходимо подключиться
     * */
    private void sender() {

        // Создаем новый поток для постоянного отправления пакетов
        new Thread(() -> {
            // Присваиваем поток переменной, для дальнейших манипуляций
            senderThread = Thread.currentThread();

            // Создаем узел, через который будем отправлять UDP пакеты данных
            try (DatagramSocket socket = new DatagramSocket()) {
                log.info("Multicast server started with GROUP IP:{} and PORT:{}", groupIP, port);

                // Указываем на какой адрес будет идти вещание
                InetAddress group = InetAddress.getByName(groupIP);

                // Переводим ключ в байтовый тип данных
                byte[] byteKey = serverKey.getBytes(StandardCharsets.UTF_8);

                // Создаем пакет данных, который будет отправлен через узел
                // Передаем через конструктор байтовый массив, длину массива, адрес вещания и порт на который будет отправлен пакет данных
                DatagramPacket packet = new DatagramPacket(byteKey, byteKey.length, group, port);

                synchronized (this) {

                    // Здесь создаем таймер, который будет отправлять пакеты каждые n миллисекунд
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