package net.m_schwarz.journe.Comm;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by michael on 05.11.16.
 */

public class User {
    public int points;
    public int id;
    public String email;
    public String errorReason;

    public static User get(int id) throws Exception {
        URL url = new URL(Config.baseUrl + "/user/"+ id + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
        return gson.fromJson(message, User.class);
    }

    public static User register(String email, String password) throws Exception {
        URL url = new URL(Config.baseUrl + "/createUser/" + email + "/" + password + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
        return gson.fromJson(message, User.class);
    }

    public static User login(String email, String password) throws Exception {
        URL url = new URL(Config.baseUrl + "/login/" + email + "/" + password + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
        return gson.fromJson(message, User.class);
    }
}
