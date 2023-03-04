package ru.burdin.clientbase.importAndExport;

import android.content.Context;
import android.os.AsyncTask;
import android.util.PrintWriterPrinter;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.Preferences;

public class TcpCloudSync extends AsyncTask <Void, Void, String>{

private Context context;
public static  final    String EXPORT = "export";
public  static  final  String IMPORT = "import";
private  String select;
    public  TcpCloudSync(Context context, String select) {
    this.select = select;
        this.context = context;
}

@Override
    protected String doInBackground(Void... voids) {
    String res = "чей-то не то";
    try (Socket socket = new Socket("78.153.4.192", 2016);
    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    DataInputStream in = new DataInputStream(socket.getInputStream());
         ) {
        socket.setSoTimeout(6000);
        if (EXPORT.equals(select)) {
            res = exportDb(socket, out, in);
        }
    if (IMPORT.equals(select)){
        res = eimportDb(socket, out, in);
    }
    }catch (SocketTimeoutException e){
res = "Истекло время ожидания";
    } catch (ConnectException e){
res = "ошибка соединения";
    }        catch (IOException e) {
res = "Сервер не доступен";
    }
    return res;
}

/*
Импорт БД
 */
    private String eimportDb(Socket socket, DataOutputStream out, DataInputStream in) throws IOException {
        String res = null;
        String login = "importBd="+Preferences.getString(context.getApplicationContext(), Preferences.LOGIN_PASSWORD, "false");
        out.writeUTF(login);
        res = in.readUTF();
        if (res.equals("ok")) {
        long size = in.readLong();
        byte [] bytes = new byte[(int) size];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = in.readByte();
            }
long newBd = readBd(bytes);
        if (size == newBd) {
            res = "true";
        }else {
            res = "Что-то пошло не так";
            }
        }
        return  res;
        }

        /*
        Запись базы
         */

    private long readBd(byte[] bytes) {
try (FileOutputStream ot = new FileOutputStream(context.getDatabasePath(Bd.DATABASE_NAME))){
    ot.write(bytes);
    ot.flush();
} catch (FileNotFoundException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}
    return  context.getDatabasePath(Bd.DATABASE_NAME).length();
    }

    /*
    Экспорт БД
     */
    private String exportDb(Socket socket, DataOutputStream out, DataInputStream in) throws IOException {
String res = null;
        String login = "exportBd="+Preferences.getString(context.getApplicationContext(), Preferences.LOGIN_PASSWORD, "false");
        out.writeUTF(login);
        res = in.readUTF();
        if (res.equals("ok")) {
            File file = context.getDatabasePath(Bd.DATABASE_NAME);
            out.writeLong(file.length());
            out.flush();
            out.write(readDb(file));
            out.flush();
            res = in.readUTF();
        }
        return  res;
    }
    private byte[] readDb (File file){
    byte[] buffer = null;
    try (FileInputStream fin = new FileInputStream(file)) {
        buffer = new byte[fin.available()];
        fin.read(buffer, 0, fin.available());
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return  buffer;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}
