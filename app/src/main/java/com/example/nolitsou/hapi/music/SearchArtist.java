package com.example.nolitsou.hapi.music;

import com.example.nolitsou.hapi.data.SocketData;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchArtist extends SocketData {
    public String artistId;
    public String artistName;
    public ArrayList<String> imageUrls;

    public SearchArtist(String artistId, String artistName, ArrayList<String> imageUrls) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.imageUrls = imageUrls;
    }

    public SearchArtist(JSONObject jsonData) {
        int i = 0;
        JSONArray jsonImages;
        try {
            this.artistId = jsonData.getString("id");
            this.artistName = jsonData.getString("name");
            this.imageUrls = new ArrayList<String>();
            jsonImages = jsonData.getJSONArray("images");
            for (i = 0; i < jsonImages.length(); i++) {
                this.imageUrls.add((jsonImages.getJSONObject(i)).getString("url"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public SearchArtist(String artistId, String artistName, String url) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.imageUrls = new ArrayList<String>();
        imageUrls.add(url);
    }

    public static boolean searchArtist(String queryStr) {
        JSONObject data = new JSONObject();
        JSONObject query = new JSONObject();
        try {
            query.put("artist", queryStr);
            data.put("query", query);
            return true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean searchArtist(String queryStr, Ack ack) {
        JSONObject data = new JSONObject();
        JSONObject query = new JSONObject();
        try {
            query.put("artist", queryStr);
            data.put("query", query);
            return true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<SearchArtist> handleSearchResult(JSONObject json) {
        ArrayList<SearchArtist> resultList = new ArrayList<SearchArtist>();
        if (json.has("result")) {
            JSONObject result;
            try {
                result = json.getJSONObject("result");
                if (result.has("artists")) {
                    JSONObject artists = result.getJSONObject("artists");
                    JSONArray items = artists.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        resultList.add(new SearchArtist(items.getJSONObject(i)));
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public static void setSocket(Socket socket) {

    }
}
