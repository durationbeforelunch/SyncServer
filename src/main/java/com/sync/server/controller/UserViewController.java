package com.sync.server.controller;

import com.sync.server.network.udp.MulticastServer;
import com.sync.server.network.udp.ServerStatus;
import com.sync.server.storage.FileStorage;
import com.sync.server.view.UserView;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.net.InetAddress;

@Log4j2
@Controller
public class UserViewController {

    public static final String PATH_NOT_CHOSEN = "Путь не выбран"; // PATH NOT CHOSEN
    public static final String PATH_CHOSEN = "Выбранный путь"; // PATH CHOSEN

    @Autowired
    private ServletWebServerApplicationContext context;

    private final UserView userView;

    private final MulticastServer server;

    private final FileStorage fileStorage;

    public UserViewController(UserView userView, MulticastServer server, FileStorage fileStorage) {
        this.userView = userView;
        this.server = server;
        this.fileStorage = fileStorage;
    }

    // RU: Инициализция UI
    // EN: Initializing UI
    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void showView() {
        userView.init();

        // Logging current server ip and port
        userView.appendLog(String.format("Сервер запущен по адресу-%s:%s", InetAddress.getLocalHost().getHostAddress(), context.getWebServer().getPort()));
        addButtonActionListeners();
    }

    // RU: Здесь вешаются разные EventListener's
    // EN: Here is some EventListener's
    private void addButtonActionListeners() {

        JFileChooser fileChooser = userView.getFileChooser();

        // RU: Сюда ставим на кнопку вызов Java проводника
        // EN: Here is button listener calling for Java Explorer
        userView.getFolderChooserButton().addActionListener(l -> {

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // RU: Получаем путь
                // EN: Getting path
                String path = (
                        fileChooser
                        .getSelectedFile()
                        .toString()
                );

                // RU: Задаем путь классу FileStorage, который затем проинициализует все файлы и папки внутри этого пути,
                // которые затем будет использовать REST контроллер
                // EN: Setting path for FileStorage class, which one will init all files and directories inside given path,
                // which REST Controller will use
                fileStorage.setOrigin(path);

                // RU: Лог в UI
                // EN: Log into UI
                userView.setChosenPath(String.format("%s: %s", PATH_CHOSEN, path));
                userView.appendLog(String.format("%s: %s", PATH_CHOSEN, path));

                log.info("{}: {}", PATH_CHOSEN, path);
            } else {
                userView.setChosenPath(PATH_NOT_CHOSEN);
                userView.appendLog(PATH_NOT_CHOSEN);
                log.info(PATH_NOT_CHOSEN);
            }

        });


        // RU: Здесь ставим на кнопку запуск Multicast сервера
        // EN: Here is button listener calling for start/stop Multicast server
        userView.getStartServerButton().addActionListener(l -> {

            // RU: Проверяем статус сервера
            // EN: Checking server status
            if (server.getServerStatus() != ServerStatus.UP) {
                // RU: Запуск сервера
                // EN: Start server
                server.startClientFinder();

                // RU: Лог в UI
                // EN: Log into UI
                userView.setDiscoveryStatus(true);
                userView.appendLog("Обнаружение включено"); // Discovery ON
            } else {
                // RU: Остановка сервера
                // EN: Stopping server
                server.stopClientFinder();

                // RU: Лог в UI
                // EN: Log into UI
                userView.setDiscoveryStatus(false);
                userView.appendLog("Обнаружение выключено"); // Discovery OFF
            }

        });

    }

}
