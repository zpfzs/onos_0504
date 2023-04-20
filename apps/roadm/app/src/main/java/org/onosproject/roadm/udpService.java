package org.onosproject.roadm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class udpService{
    public String rec(int port) {
        String message = null;
        try {
            ServerSocket serverSocket = new ServerSocket(8888); // 监听指定的端口
            while (true) {
                Socket socket = serverSocket.accept(); // 阻塞等待客户端连接
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                for (String line; (line = br.readLine()) != null; message += line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}