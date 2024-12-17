package com.example.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    ImageView imgView;
    TextView textView;
    String get_title;

    String name = "정현우";

    Boolean get_checked;
    String site_url = "https://pinkanimal.pythonanywhere.com";
    String token ="3f8b7598a118c99b5a33e658164b2678980a3ada";

    JSONObject post_json;
    String imageUrl = null;
    Bitmap bmImg = null;
    OutputStreamWriter outputStreamWriter = null;

    CloadImage taskDownload;
    CloadChecked taskCheck;
    UploadImage taskUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView = (TextView)findViewById(R.id.textView);

    }
    public void onClickDownload(View v) {
        if (taskDownload != null && taskDownload.getStatus() == AsyncTask.Status.RUNNING) {
            taskDownload.cancel(true);
        }
        Log.e("down", "before");
        taskDownload = new CloadImage();
        Log.e("down", "ready");
        taskDownload.execute(site_url + "/api_root/CheckAttendance/");
        //Toast.makeText(getApplicationContext(), site_url+" /api_root/CheckAttendance/ Download", Toast.LENGTH_LONG).show();
    }
    public void onClickCheckedDownload(View v){
        if (taskCheck != null && taskCheck.getStatus() == AsyncTask.Status.RUNNING) {
            taskCheck.cancel(true);
        }
        Log.e("down", "before");
        taskCheck = new CloadChecked();
        Log.e("down", "ready");
        taskCheck.execute(site_url + "/api_root/CheckedAttendance/");
        //Toast.makeText(getApplicationContext(), site_url+"/api_root/CheckedAttendance/ Download", Toast.LENGTH_LONG).show();
    }
    public void onClickUpload(View v, String text, String name) {
        if (taskUpload != null && taskUpload.getStatus() == AsyncTask.Status.RUNNING) {
            taskUpload.cancel(true);
        }

        taskUpload = new UploadImage();
        Log.e("down", "ready");
        taskUpload.execute(site_url + "/api_root/Post/", text, name);
        Toast.makeText(getApplicationContext(), "출석 확인", Toast.LENGTH_LONG).show();
    }

    private class CloadImage extends AsyncTask<String, Integer, List<infoObject>> {
        @Override
        protected List<infoObject> doInBackground(String... urls) {
            List<infoObject> infoList = new ArrayList<>();

            try {

                String apiUrl = urls[0];
                Log.e("http", apiUrl);
                URL urlAPI = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) urlAPI.openConnection();

                conn.setRequestProperty("Authorization", "Token " + token);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    InputStream is = conn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    is.close();

                    String strJson = result.toString();
                    JSONArray aryJson = new JSONArray(strJson);
                    Log.e("Get", "success");
                    for (int i = 0; i < aryJson.length(); i++) {
                        post_json = (JSONObject) aryJson.get(i);
                        imageUrl = post_json.getString("image");

                        get_checked = post_json.getBoolean("checked");
                        if(get_checked){
                            get_title = post_json.getString("text");
                        }
                        else{
                            get_title = post_json.getString("title");
                        }

                        if (!imageUrl.equals("")) {
                            URL myImageUrl = new URL(imageUrl);
                            conn = (HttpURLConnection) myImageUrl.openConnection();
                            InputStream imgStream = conn.getInputStream();
                            Bitmap imageBitmap = BitmapFactory.decodeStream(imgStream);
                            if(!get_checked){
                                infoList.add(new infoObject(imageBitmap, get_title,get_checked));
                            }


                            imgStream.close();
                        }
                    }

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return infoList;

        }
        @Override
        protected void onPostExecute(List<infoObject> objects) {
            if (objects.isEmpty()) {
                textView.setText("불러올 출석 이미지가 없습니다.");
            } else {
                textView.setText("출석 이미지 로드 성공!");
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                CustomAdapter adapter = new CustomAdapter(objects);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);
            }
        }
    }
    private class CloadChecked extends AsyncTask<String, Integer, List<infoObject>> {
        @Override
        protected List<infoObject> doInBackground(String... urls) {
            List<infoObject> infoList = new ArrayList<>();

            try {

                String apiUrl = urls[0];
                Log.e("http", apiUrl);
                URL urlAPI = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) urlAPI.openConnection();

                conn.setRequestProperty("Authorization", "Token " + token);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    InputStream is = conn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    is.close();

                    String strJson = result.toString();
                    JSONArray aryJson = new JSONArray(strJson);
                    Log.e("Get", "success");
                    for (int i = 0; i < aryJson.length(); i++) {
                        post_json = (JSONObject) aryJson.get(i);
                        imageUrl = post_json.getString("image");

                        get_checked = post_json.getBoolean("checked");
                        if(get_checked){
                            get_title = post_json.getString("text");
                        }
                        else{
                            get_title = post_json.getString("title");
                        }
                        if(get_checked){
                            infoList.add(new infoObject(bmImg, get_title,get_checked));
                        }

                    }

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return infoList;

        }
        @Override
        protected void onPostExecute(List<infoObject> objects) {
            if (objects.isEmpty()) {
                textView.setText("확인된 출석이 없습니다.");
            } else {
                textView.setText("출석 목록 로드 성공!");
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                CustomAdapter adapter = new CustomAdapter(objects);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private class UploadImage extends AsyncTask<String, String, String> {
        String currentTime = LocalDateTime.now().toString();
        String title;
        String name;

        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    title = urls[1];
                    name = urls[2];
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "JWT "+token);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("author", 1);
                    jsonObject.put("title", title+" " +name);
                    jsonObject.put("text", title + "에 "+name+"님이 출석하였습니다.");
                    jsonObject.put("created_date", currentTime);
                    jsonObject.put("published_date", currentTime);
                    jsonObject.put("checked", true);



                    connection.getOutputStream();


                    outputStreamWriter =new OutputStreamWriter(connection.getOutputStream());
                    outputStreamWriter.write(jsonObject.toString());

                    outputStreamWriter.flush();

                    connection.connect();

                    if (connection.getResponseCode() == 200) {
                        Log.e("uploadImage", "Success");
                    }
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                Log.e("uploadImage", "Exception in uploadImage: " + e.getMessage());
            }

            Log.e("uploadtask", "task finished");
            return null;
        }
    }
    public class infoObject{
        public infoObject(Bitmap bitmap1, String title1, boolean checked1){
            bitmap = bitmap1;
            title = title1;
            checked = checked1;

        }
        public Bitmap bitmap;
        public String title;
        public boolean checked;
    }



    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        private final List<infoObject> objectList;
        public CustomAdapter(List<infoObject> objectList) {
            this.objectList = objectList;
        }

        @NonNull
        @Override
        public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new CustomAdapter.CustomViewHolder(view);
        }



        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
            Bitmap bitmap = objectList.get(position).bitmap;
            String title = objectList.get(position).title;
            boolean checked = objectList.get(position).checked;

            holder.tv.setText(title);
            holder.text = title;
            if(checked){
                holder.btn.setVisibility(View.GONE);
                holder.iv.setVisibility(View.GONE);
            }
            else{
                holder.btn.setVisibility(View.VISIBLE);
                holder.iv.setVisibility(View.VISIBLE);
                holder.iv.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            return objectList.size();
        }



        public class CustomViewHolder extends RecyclerView.ViewHolder{
            protected ImageView iv;
            protected TextView tv;
            protected Button btn;

            public String text = "";
            public CustomViewHolder(@NonNull View item_image){
                super(item_image);
                this.iv = (ImageView) item_image.findViewById(R.id.imageViewItem);
                this.tv = (TextView) item_image.findViewById(R.id.textViewItem);
                this.btn = (Button) item_image.findViewById(R.id.buttonItem);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickUpload(v, text, name);
                    }
                });
            }

        }
    }
}

