package cz.ekishigo.mobilitiassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.ekishigo.mobilitiassignment.utils.Security;


/**
 * Created by ekishigo on 8.6.16.
 */
public class PostClass extends AsyncTask<String, Void, Void> {


    private final static String charset = StandardCharsets.UTF_8.name();
    private URL url;
    private final Context context;

    public PostClass(String url, Context context, View viewById) {
        this.imageView = (ImageView) viewById;
        this.context = context;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        String url = "https://mobility.cleverlance.com/download/bootcamp/image.php";
//        PostClass postClass = new PostClass(url, context);
//        postClass.login("ekishev", "igor");

    }


    public void login(String username, String password) {
        // TODO validation
        try {
            // prepare Strings, query is for request body and encodedPassword for header
            String query = "username=".concat(username);
            String encodedPassword = Security.encodeSha1(password);

            // open connection, set headers for request
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); // Triggers POST
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", encodedPassword);

            // write data
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(query.getBytes(charset));
            outputStream.close();

            // get response
            int responseCode = connection.getResponseCode();
            // TODO log
            System.out.printf("POST request on : %s\n", url.toString());
            System.out.printf("Query : %s\n", query);
            System.out.printf("Response code=%d\n", responseCode);

            // process response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                processResponse(bufferedReader.readLine());
            } else {
                System.err.println("Chyba");
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] processResponse(String s) throws IOException {
        s = clearResponse(s);
        System.out.println(s);
//        byte[] imgBytes = Base64.getDecoder().decode(s);
        byte[] imgBytes = Base64.decode(s, Base64.DEFAULT);
//        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imgBytes));


        Bitmap imgBitMap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        imageView = new ImageView(context);
        imageView.setImageBitmap(imgBitMap);
        return imgBytes;
    }

    ImageView imageView;


    private String clearResponse(String s) {
        return s.substring(10, (s.length() - 2));
    }

    @Override
    protected Void doInBackground(String... params) {
        System.out.println("I'm here");
        login(params[0], params[1]);
        return null;
    }
}