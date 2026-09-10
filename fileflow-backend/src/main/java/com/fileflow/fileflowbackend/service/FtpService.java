package com.fileflow.fileflowbackend.service;

import java.io.*;
import java.net.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FtpService
{
    @Value("${ftp.server.host}")
    private String host;

    @Value("${ftp.server.port}")
    private int port;

    /** Result of a GET: either the file bytes, or a not-found/error message. */
    public static class DownloadResult
    {
        private final boolean success;
        private final byte[] data;
        private final String message;

        private DownloadResult(boolean success, byte[] data, String message)
        {
            this.success = success;
            this.data = data;
            this.message = message;
        }

        public static DownloadResult ok(byte[] data)
        {
            return new DownloadResult(true, data, null);
        }

        public static DownloadResult failure(String message)
        {
            return new DownloadResult(false, null, message);
        }

        public boolean isSuccess()
        {
            return success;
        }

        public byte[] getData()
        {
            return data;
        }

        public String getMessage()
        {
            return message;
        }
    }

    private Socket connect() throws IOException
    {
        return new Socket(host, port);
    }

    public String getFileList()
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("LIST");

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    public String deleteFile(String fileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("DELETE " + fileName);

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    public String renameFile(String oldFileName, String newFileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("RENAME " + oldFileName + " " + newFileName);

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    public String checkFileExists(String fileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("EXISTS " + fileName);

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    public String getFileInfo(String fileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("INFO " + fileName);

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    public String getFileSize(String fileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("SIZE " + fileName);

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }

    /**
     * GET. Previously this saved the downloaded bytes onto the BACKEND's own disk
     * (as "Downloaded_<name>") and returned only a text message - the browser never
     * actually received the file. It now streams the bytes back in-memory so
     * FileController can return them straight to the client as a real download.
     */
    public DownloadResult downloadFile(String fileName)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            dos.writeUTF("GET " + fileName);

            String response = dis.readUTF();

            if (response.equals("FILE_NOT_FOUND"))
            {
                return DownloadResult.failure("File not found on server");
            }

            long filesize = dis.readLong();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte chunk[] = new byte[4096];

            long received = 0;

            while (received < filesize)
            {
                long remaining = filesize - received;

                int toread = remaining > chunk.length ? chunk.length : (int) remaining;

                int bytesread = dis.read(chunk, 0, toread);

                if (bytesread == -1)
                {
                    break;
                }

                buffer.write(chunk, 0, bytesread);

                received = received + bytesread;
            }

            if (received != filesize)
            {
                return DownloadResult.failure("Download failed - connection closed early");
            }

            return DownloadResult.ok(buffer.toByteArray());
        }
        catch (Exception e)
        {
            return DownloadResult.failure("Exception occured : " + e);
        }
    }

    public String uploadFile(MultipartFile uploadedFile)
    {
        try (
            Socket socket = connect();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        )
        {
            dis.readUTF();

            String fileName = uploadedFile.getOriginalFilename();

            dos.writeUTF("PUT " + fileName);

            String response = dis.readUTF();

            if (!response.equals("READY"))
            {
                return response;
            }

            long filesize = uploadedFile.getSize();

            dos.writeLong(filesize);

            try (InputStream fis = uploadedFile.getInputStream())
            {
                byte buffer[] = new byte[4096];

                int bytesread;

                while ((bytesread = fis.read(buffer)) != -1)
                {
                    dos.write(buffer, 0, bytesread);
                }

                dos.flush();
            }

            return dis.readUTF();
        }
        catch (Exception e)
        {
            return "Exception occured : " + e;
        }
    }
}
