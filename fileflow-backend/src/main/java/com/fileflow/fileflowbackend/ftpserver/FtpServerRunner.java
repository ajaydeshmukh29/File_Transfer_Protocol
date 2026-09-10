package com.fileflow.fileflowbackend.ftpserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Boots the socket-based FtpServer in a background daemon thread as soon as
 */
@Component
public class FtpServerRunner implements ApplicationRunner
{
    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.storage-dir}")
    private String storageDirPath;

    @Override
    public void run(ApplicationArguments args)
    {
        FtpServer ftpServer = new FtpServer(port, new File(storageDirPath));

        Thread serverThread = new Thread(() ->
        {
            try
            {
                ftpServer.start();
            }
            catch (Exception e)
            {
                System.out.println("Failed to start embedded FTP server : " + e);
            }
        });

        serverThread.setDaemon(true);
        serverThread.setName("ftp-server");
        serverThread.start();
    }
}
