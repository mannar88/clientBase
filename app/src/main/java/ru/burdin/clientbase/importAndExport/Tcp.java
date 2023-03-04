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

import ru.burdin.clientbase.R;

public class Tcp extends AsyncTask <String, Void,String> {

    public  Tcp () {

    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "начало работы ";
        try ( Socket socket = new Socket("78.153.4.192", 2016);
                         DataOutputStream out = new DataOutputStream(socket.getOutputStream());
 DataInputStream in = new DataInputStream(socket.getInputStream());
        ){
 socket.setSoTimeout(10000) ;
                    out.writeUTF(strings[0]);
out.flush();
result = in.readUTF();
                }catch (IOException e) {
result = "Сервер не доступен";
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
