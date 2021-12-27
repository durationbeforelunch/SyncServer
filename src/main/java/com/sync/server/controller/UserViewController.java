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

    public static final String PATH_NOT_CHOSEN = "Путь не выбран";
    public static final String PATH_CHOSEN = "Выбранный путь";

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

    // Инициализция UI
    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void showView() {
        userView.init();

        userView.appendLog(String.format("Сервер запущен по адресу-%s:%s", InetAddress.getLocalHost().getHostAddress(), context.getWebServer().getPort()));
        addButtonActionListeners();
    }

    // Здесь вешаются разные EventListener's
    private void addButtonActionListeners() {

        JFileChooser fileChooser = userView.getFileChooser();

        // Сюда ставим на кнопку вызов Java проводника
        userView.getFolderChooserButton().addActionListener(l -> {

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // Получаем путь
                String path = (
                        fileChooser
                        .getSelectedFile()
                        .toString()
                );

                // Задаем путь классу FileStorage, который затем проинициализует все файлы и папки внутри этого пути,
                // которые затем будет использовать REST контроллер
                fileStorage.setOrigin(path);

                // Лог в UI
                userView.setChosenPath(String.format("%s: %s", PATH_CHOSEN, path));
                userView.appendLog(String.format("%s: %s", PATH_CHOSEN, path));

                log.info("{}: {}", PATH_CHOSEN, path);
            } else {
                userView.setChosenPath(PATH_NOT_CHOSEN);
                userView.appendLog(PATH_NOT_CHOSEN);
                log.info(PATH_NOT_CHOSEN);
            }

        });


        // Здесь ставим на кнопку запуск Multicast сервера
        userView.getStartServerButton().addActionListener(l -> {

            // Проверяем статус сервера
            if (server.getServerStatus() != ServerStatus.UP) {
                // Запуск сервера
                server.startClientFinder();

                // Лог в UI
                userView.setDiscoveryStatus(true);
                userView.appendLog("Обнаружение включено");
            } else {
                // Остановка сервера
                server.stopClientFinder();

                // Лог в UI
                userView.setDiscoveryStatus(false);
                userView.appendLog("Обнаружение выключено");
            }

        });

    }

}
