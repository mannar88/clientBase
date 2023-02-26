package ru.burdin.clientbase.importAndExport;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.Socket;

import ru.burdin.clientbase.R;

public class Tcp extends AsyncTask <String, Void,String> {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    public  Tcp () {

    }

    @Override
    protected String doInBackground(String... strings){
        String result = "Test";
        try {
            socket = new Socket("78.153.4.192", 2016);
            if (socket.isConnected()) {
                send(strings[0] + "\r\n\r\n");
                result = getString();

                result = result == null ? "Ничего нету" : result;
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            result = e.toString();
        }
        return  result;
    }

    private   String getString () throws IOException {
        String result = null;
        if (!socket.isClosed()) {
            boolean sendCheck = false;
            while (!sendCheck) {
                if (!out.checkError()) {
                    sendCheck = true;
                }
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                for (String str = in.readLine(); str != null && !str.isEmpty(); str = in.readLine()) {
                    result = str;
                }
            }
        }else {
            result = "socket для чтения закрыт";
        }
        return  result;
    }

    private   void   send(String text) throws IOException {
        String result ="";
        if (!socket.isClosed()) {
            out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            out.print(text);
            out.flush();
            result = "Сообщение ушло";
        }else {
            result = "Socket для отправки закрыт";
        }
    }

}
