package net.m_schwarz.journe.Comm;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by michael on 05.11.16.
 */
public class GeoImage {
    public String url;

    public GeoImage(String url){
        this.url = url;
    }

    public static GeoImage[] load(String url) throws Exception {
        URL mUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        String message = sb.toString();

        Gson gson = new Gson();
        Images images = gson.fromJson(message, Images.class);

        GeoImage[] result = new GeoImage[images.listOfPictures.size()];

        for(int i=0;i < images.listOfPictures.size();i++){
            result[i] = new GeoImage(Config.baseUrl+ "/getPicture/"
                    + images.listOfPictures.get(i).id + "/");
        }

        return result;
    }

    public static class Images {
        public List<Image> listOfPictures;
    }

    public static class Image{
        public int id,userId;
        public double lat,lng;
    }

}
