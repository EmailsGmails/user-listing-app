package lv.emils.dev.simpleapp.util;

/**
 * Created by Emils on 17.03.2017.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import lv.emils.dev.simpleapp.content.UserContent;

public class JsonReaderUtil {

    public static UserContent.UserListUser getUserListUser(JSONObject jsonUserListUser) throws JSONException {
        String id = jsonUserListUser.getString("id");
        UserContent.UserListUser userListUser = new UserContent.UserListUser(id);
        userListUser.setUserName(jsonUserListUser.getString("username"));
        userListUser.setEmail(jsonUserListUser.getString("email"));
        return userListUser;
    }

    public static UserContent.User getUser(JSONObject jsonUser) throws JSONException {
        String id = jsonUser.getString("id");
        UserContent.User user = new UserContent.User(id);
        user.setUsername(jsonUser.getString("username"));
        user.setName(jsonUser.getString("name"));
        user.setZipcode(((JSONObject) jsonUser.get("address")).getString("zipcode"));
        user.setCompanyName(((JSONObject) jsonUser.get("company")).getString("name"));
        return user;
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONObject readJsonObjectFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}