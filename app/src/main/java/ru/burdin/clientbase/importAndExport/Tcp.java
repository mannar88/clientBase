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

    private  Socket socket;
private  DataInputStream in;
private DataOutputStream out;


    public  Tcp () {

    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "начало работы ";
        try {
            socket = new Socket("78.153.4.192", 2016);
         socket.setSoTimeout(10000) ;
            result = result + " " + socket.isClosed();
            out = new DataOutputStream(socket.getOutputStream());
 in = new DataInputStream(socket.getInputStream());
                    result = result + "Пошло соеденение";
                    out.writeUTF(strings[0]);
out.flush();
result = in.readUTF();
                }catch (IOException e) {
                    result = result + " " +  e.getLocalizedMessage();
            }finally {
//        if (socket != null) {
//            try {
//                socket.close();
//            } catch (IOException e) {
//result = result + " " + e.getLocalizedMessage();
//            }
//        if (out != null) {
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        if (in != null){
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        }
//        }
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

    public  void close () {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        if (socket != null) {

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

}
