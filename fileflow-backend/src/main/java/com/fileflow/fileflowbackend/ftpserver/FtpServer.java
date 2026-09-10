package com.fileflow.fileflowbackend.ftpserver;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FtpServer
{
    private final int port;
    private final File storageDir;
    private final AtomicInteger clientCount = new AtomicInteger(1);

    public FtpServer(int port, File storageDir)
    {
        this.port = port;
        this.storageDir = storageDir;

        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }
    }

    public void start() throws IOException
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("-------------------------------------");
            System.out.println("----- FTP Server Started (port " + port + ") -----");
            System.out.println("----- Storage directory: " + storageDir.getAbsolutePath() + " -----");
            System.out.println("-------------------------------------");

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected successfully");

                Thread t = new Thread(() -> handleClientRequest(clientSocket));
                t.setDaemon(true);
                t.start();
            }
        }
    }

    /** Rejects filenames that try to escape the storage directory. */
    private boolean isSafeFileName(String fileName)
    {
        if (fileName == null || fileName.isBlank())
        {
            return false;
        }

        return !fileName.contains("..")
            && !fileName.contains("/")
            && !fileName.contains("\\");
    }

    private File resolve(String fileName)
    {
        return new File(storageDir, fileName);
    }

    private void handleClientRequest(Socket socket)
    {
        int clientNo = clientCount.getAndIncrement();

        System.out.println("New thread created for client no : " + clientNo);

        try (
            Socket s = socket;
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream())
        )
        {
            dos.writeUTF("Connected to Marvellous Server");

            while (true)
            {
                String command = dis.readUTF();

                System.out.println("Command received from client " + clientNo + " : " + command);

                String parts[] = command.split(" ");

                String operation = parts[0].toUpperCase();

                if (operation.equals("QUIT"))
                {
                    if (parts.length != 1)
                    {
                        dos.writeUTF("Usage : QUIT");
                        continue;
                    }

                    dos.writeUTF("Disconnected from server");
                    break;
                }
                else if (operation.equals("GET"))
                {
                    handleGet(parts, dis, dos);
                }
                else if (operation.equals("PUT"))
                {
                    handlePut(parts, dis, dos);
                }
                else if (operation.equals("INFO"))
                {
                    handleInfo(parts, dos);
                }
                else if (operation.equals("SIZE"))
                {
                    handleSize(parts, dos);
                }
                else if (operation.equals("EXISTS"))
                {
                    handleExists(parts, dos);
                }
                else if (operation.equals("RENAME"))
                {
                    handleRename(parts, dos);
                }
                else if (operation.equals("DELETE"))
                {
                    handleDelete(parts, dos);
                }
                else if (operation.equals("LIST"))
                {
                    handleList(parts, dos);
                }
                else
                {
                    dos.writeUTF("Invalid operation");
                }
            }

            System.out.println("Client " + clientNo + " disconnected");
        }
        catch (EOFException | SocketException e)
        {
            // Client disconnected abruptly - not a server-side error.
            System.out.println("Client " + clientNo + " connection closed");
        }
        catch (Exception e)
        {
            System.out.println("Exception occurred for client " + clientNo + " : " + e);
        }
    }

    private void handleGet(String[] parts, DataInputStream dis, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : GET <FileName>");
            return;
        }

        File file = resolve(parts[1]);

        if (!file.exists() || !file.isFile())
        {
            dos.writeUTF("FILE_NOT_FOUND");
            return;
        }

        dos.writeUTF("File found");

        long filesize = file.length();

        dos.writeLong(filesize);

        try (FileInputStream fis = new FileInputStream(file))
        {
            byte buffer[] = new byte[4096];

            int bytesread;

            while ((bytesread = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, bytesread);
            }

            dos.flush();
        }

        System.out.println("File sent successfully to the client");
    }

    private void handlePut(String[] parts, DataInputStream dis, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : PUT <FileName>");
            return;
        }

        String fileName = parts[1];

        dos.writeUTF("READY");

        long filesize = dis.readLong();

        File file = resolve(fileName);

        long received = 0;

        try (FileOutputStream fos = new FileOutputStream(file))
        {
            byte buffer[] = new byte[4096];

            while (received < filesize)
            {
                long remaining = filesize - received;

                int toread = remaining > buffer.length ? buffer.length : (int) remaining;

                int bytesread = dis.read(buffer, 0, toread);

                if (bytesread == -1)
                {
                    break;
                }

                fos.write(buffer, 0, bytesread);

                received = received + bytesread;
            }
        }

        if (received == filesize)
        {
            dos.writeUTF("File uploaded successfully");
        }
        else
        {
            dos.writeUTF("File upload failed");
        }
    }

    private void handleInfo(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : INFO <FileName>");
            return;
        }

        File file = resolve(parts[1]);

        if (file.exists())
        {
            String info = "";

            info = info + "File name : " + file.getName() + "\n";
            info = info + "File Size : " + file.length() + "\n";
            info = info + "Readable : " + file.canRead() + "\n";
            info = info + "Writable : " + file.canWrite() + "\n";

            dos.writeUTF(info);
        }
        else
        {
            dos.writeUTF("File does not exist");
        }
    }

    private void handleSize(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : SIZE <FileName>");
            return;
        }

        File file = resolve(parts[1]);

        if (file.exists() && file.isFile())
        {
            dos.writeUTF("File size is : " + file.length() + " bytes");
        }
        else
        {
            dos.writeUTF("File does not exist");
        }
    }

    private void handleExists(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : EXISTS <FileName>");
            return;
        }

        File file = resolve(parts[1]);

        dos.writeUTF(file.exists() ? "File exist" : "File does not exist");
    }

    private void handleRename(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 3 || !isSafeFileName(parts[1]) || !isSafeFileName(parts[2]))
        {
            dos.writeUTF("Usage : RENAME <OldFileName> <NewFileName>");
            return;
        }

        File oldFile = resolve(parts[1]);
        File newFile = resolve(parts[2]);

        if (!oldFile.exists())
        {
            dos.writeUTF("Source file does not exist");
            return;
        }

        if (oldFile.renameTo(newFile))
        {
            dos.writeUTF("File renamed successfully");
        }
        else
        {
            dos.writeUTF("Unable to rename file");
        }
    }

    private void handleDelete(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 2 || !isSafeFileName(parts[1]))
        {
            dos.writeUTF("Usage : DELETE <FileName>");
            return;
        }

        File file = resolve(parts[1]);

        if (!file.exists())
        {
            dos.writeUTF("There is no such file");
            return;
        }

        if (file.delete())
        {
            dos.writeUTF("File deleted successfully");
        }
        else
        {
            dos.writeUTF("Unable to delete the file");
        }
    }

    private void handleList(String[] parts, DataOutputStream dos) throws IOException
    {
        if (parts.length != 1)
        {
            dos.writeUTF("Usage : LIST");
            return;
        }

        File files[] = storageDir.listFiles();

        String result = "";

        if (files != null)
        {
            for (File f : files)
            {
                if (f.isFile())
                {
                    result = result + f.getName() + "\n";
                }
            }
        }

        if (result.length() == 0)
        {
            result = "No files available";
        }

        dos.writeUTF(result);
    }
}
