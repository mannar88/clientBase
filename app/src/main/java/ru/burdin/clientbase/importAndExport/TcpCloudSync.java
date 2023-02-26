package ru.burdin.clientbase.importAndExport;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.Preferences;

public class TcpCloudSync extends AsyncTask <Void, Void, String>{

    private BufferedOutputStream bufferedOutputStream;
private Context context;
private  BufferedReader bufferedReader = null;


    public  TcpCloudSync(Context context) {
    this.context = context;
}

@Override
    protected String doInBackground(Void... voids) {
    String res = "чей-то не то";
    try(Socket socket = new Socket("78.153.4.192", 2016); OutputStream         out = socket.getOutputStream();
InputStream in = socket.getInputStream()
    ) {

        String login ="exportBd=" +Preferences.getString(context, Preferences.LOGIN_PASSWORD, "fals") +  "\r\n\r\n";
        request(out, login);
if ("ok".equals(response(in))) {
    File file = context.getDatabasePath(Bd.DATABASE_NAME);
    request(out, String.valueOf(file.length()) +  "\r\n\r\n");
    exportFile(out);
response(in);
    res = "ok";
    }
    } catch (IOException e) {
            res = e.getLocalizedMessage();
        }
    return res;
}


    private  void  request(OutputStream out, String text) throws IOException {
 bufferedOutputStream =  new BufferedOutputStream(out);
            bufferedOutputStream.write(text.getBytes());
            bufferedOutputStream.flush();
}

private  String response (InputStream in) {
    String result = "";
    try {
bufferedReader = new BufferedReader(
                            new InputStreamReader(in)
                    );
        for (String str = bufferedReader.readLine(); str != null && !str.isEmpty(); str = bufferedReader.readLine()) {
        result = str;
    }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return  result;

}

private  void exportFile(OutputStream out) throws IOException {
    File file = context.getDatabasePath(Bd.DATABASE_NAME);
long lengs = file.length();
    byte[] bytes = new byte[4000 * 1024];
    InputStream in = new FileInputStream(file);
    ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
    buf.putLong(lengs);
    out.write(buf.array());
    int count;
    while ((count = in.read(bytes)) != -1) {
        out.write(bytes, 0, count);
    }
out.flush();
    in.close();
    }

}
