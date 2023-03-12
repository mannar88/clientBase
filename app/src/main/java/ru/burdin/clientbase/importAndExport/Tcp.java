package ru.burdin.clientbase.importAndExport;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ru.burdin.clientbase.R;

public class Tcp extends AsyncTask <String, Void,String> {
      public static final String IOEXCEPTION = "Сервер не досупен";
    public static final String CONNECT_EXCETPION = "Нет связи с сервером";

    public  Tcp () {

    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "начало работы ";
        try (Socket socket = new Socket("78.153.4.192", 2016);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
        ) {
            socket.setSoTimeout(10000);
            out.writeUTF(strings[0]);
            out.flush();
            result = in.readUTF();
        }catch (SocketTimeoutException e){
            result = "Истекло время ожидания";
        } catch (ConnectException e){
result = CONNECT_EXCETPION;
    }        catch (IOException e) {
            result = IOEXCEPTION;
        }
                    return  result;
            }


    private   String getString (BufferedReader in) throws IOException {
            String rst = "пусто";
            while ((rst =in.readLine()) != null) {
                if (rst.length() > 0) {
                                        break;
                }
            }
return  rst;
    }

     private   void   send(String text, PrintWriter out) throws IOException {
        String result ="";
            out.print(text+ "\r\n\r\n");
            out.flush();
            result = "Сообщение ушло";
    }


}
