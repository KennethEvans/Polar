package net.kenevans.polar.accessmanager.ui;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import com.google.gson.Gson;

import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.User;
import net.kenevans.polar.utils.HttpUtils;
import net.kenevans.polar.utils.Utils;

/*
 * Created on Feb 1, 2019
 * By Kenneth Evans, Jr.
 */

/**
 * Manager
 * 
 * @author Kenneth Evans, Jr.
 */
public class Manager implements IConstants
{
    public static String access_code = "";
    public static String token = "";
    public static String client_user_id = DEFAULT_USER_ID;
    public static String polar_user_id = "";

    /**
     * Gets the preferences from the preference store.
     */
    public static void getPreferences() {
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        if(prefs != null) {
            access_code = prefs.get(P_ACCESS, D_ACCESS);
            token = prefs.get(P_TOKEN, D_TOKEN);
            client_user_id = prefs.get(P_CLIENT_USER_ID, D_CLIENT_USER_ID);
            polar_user_id = prefs.get(P_POLAR_USER_ID, D_POLAR_USER_ID);

            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // client_user_id = "User1";
        }
    }

    /**
     * Sets the preferences to the preference store.
     */
    public static void setPreferences() {
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        if(prefs != null) {
            prefs.put(P_ACCESS, access_code);
            prefs.put(P_TOKEN, token);
            prefs.put(P_CLIENT_USER_ID, client_user_id);
            prefs.put(P_POLAR_USER_ID, polar_user_id);
        }
    }
    
    public static String getAthorizationURL() {
        if(access_code == null || access_code.isEmpty()) {
            Utils.errMsg("No access code");
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(access_code);
        String decodedString = new String(decodedBytes);
        String[] parts = decodedString.split(":");
        if(parts == null || parts.length != 2) {
            Utils.errMsg("Unable to parse access code");
            return null;
        }
        return AUTHORIZATION_URL_PREFIX + parts[0];
    }

    /**
     * Queries the server for the token using the code received from the access_code
     * request.
     * 
     * @param code Access code received from the web browser.
     * @return An AssetToken with the data or null on failure.
     */
    public static AccessToken getToken(String code) {
        if(access_code == null) {
            Utils.errMsg("No access_code code");
            return null;
        }
        String json = null;
        String basicAuth = "Basic " + access_code;
        String body = "grant_type=authorization_code&code=" + code;
        byte[] postData = body.getBytes(StandardCharsets.UTF_8);
        String url = ACCESS_TOKEN_URL;
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json;charset=UTF-8");
            con.setRequestProperty("Authorization", basicAuth);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);

            // Apparently cannot get Authorization by getRequest property or
            // getHeaderField
            System.out.println("..........................................");
            System.out.println(url);
            System.out.println(basicAuth);
            System.out.println("..........................................");
            System.out.println(
                "Authorization: " + con.getRequestProperty("Authorization"));
            System.out.println(
                "Content-Length: " + con.getRequestProperty("Content-Length"));
            System.out.println(
                "Content-Type: " + con.getRequestProperty("Content-Type"));
            System.out.println("Accept: " + con.getRequestProperty("Accept"));
            System.out.println("urlParameters: " + body);
            System.out.println(
                "postData.length=" + Integer.toString(postData.length));
            System.out.println("..........................................");

            String result = HttpUtils.writeOutput(con, postData);
            if(result.equals(HttpUtils.CONN_WRITE_ERROR)) {
                System.out.println(result);
            }

            int responseCode = con.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                String msg = "getToken Failed : HTTP error code : "
                    + con.getResponseCode();
                String error = HttpUtils.getError(con);
                if(error == null) {
                    msg += LS + HttpUtils.CONN_NOERROR;
                } else {
                    msg += LS + error;
                }
                Utils.errMsg(msg);
            }

            String response = HttpUtils.getInput(con);
            System.out.println(response);
            if(response != null && !response.equals(HttpUtils.CONN_NOINPUT)) {
                json = response;
            }
            con.disconnect();
        } catch(Exception ex) {
            Utils.excMsg("Failed to get token", ex);
        }
        if(json == null) {
            return null;
        } else {
            Gson gson = new Gson();
            AccessToken at = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.AccessToken.class);
            return at;
        }
    }

    public static User registerUser() {
        if(token == null) {
            Utils.errMsg("No token");
            return null;
        }
        String json = null;
        String bearerAuth = "Bearer " + token;
        String body = "{\"member-id\": \"" + client_user_id + "\"}";
        byte[] postData = body.getBytes(StandardCharsets.UTF_8);
        String url = ACCESS_LINK_URL + "/users";
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", bearerAuth);
            con.setDoInput(true);
            con.setDoOutput(true);

            System.out.println("..........................................");
            System.out.println(url);
            System.out.println(bearerAuth);
            System.out.println("..........................................");
            System.out.println("Accept: " + con.getRequestProperty("Accept"));
            System.out.println(
                "Content-Length: " + con.getRequestProperty("Content-Length"));
            System.out.println(
                "Content-Type: " + con.getRequestProperty("Content-Type"));
            System.out.println("urlParameters: " + body);
            System.out.println(
                "postData.length=" + Integer.toString(postData.length));
            System.out.println("..........................................");

            OutputStream os = con.getOutputStream();
            String result = HttpUtils.writeOutput(con, postData);
            if(result.equals(HttpUtils.CONN_WRITE_ERROR)) {
                System.out.println(result);
            }
            os.flush();

            int responseCode = con.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_CREATED) {
                String msg = "registerUser Failed : HTTP error code : "
                    + con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_CONFLICT) {
                    msg += LS + "(Probably already registered)" + LS;
                }
                String error = HttpUtils.getError(con);
                if(error == null) {
                    msg += LS + HttpUtils.CONN_NOERROR;
                } else {
                    msg += LS + error;
                }
                Utils.errMsg(msg);
            }

            String response = HttpUtils.getInput(con);
            System.out.println(response);
            if(response != null && !response.equals(HttpUtils.CONN_NOINPUT)) {
                json = response;
            }
        } catch(Exception ex) {
            Utils.excMsg("Failed to register user", ex);
        }
        if(json == null) {
            return null;
        } else {
            Gson gson = new Gson();
            User user = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.User.class);
            if(user != null) {
                polar_user_id = user.polarUserId;
                setPreferences();
            }
            return user;
        }
    }

    public static boolean deleteUser() {
        if(token == null) {
            Utils.errMsg("No token");
            return false;
        }
        if(polar_user_id == null || polar_user_id.length() == 0) {
            Utils.errMsg("No Polar user_id");
            return false;
        }
        String bearerAuth = "Bearer " + token;
        String url = ACCESS_LINK_URL + "/users/" + polar_user_id;
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", bearerAuth);
            con.setDoInput(true);
            con.setDoOutput(true);

            System.out.println("..........................................");
            System.out.println(url);
            System.out.println(bearerAuth);
            System.out.println("..........................................");
            System.out.println("Accept: " + con.getRequestProperty("Accept"));
            System.out.println(
                "Content-Length: " + con.getRequestProperty("Content-Length"));
            System.out.println(
                "Content-Type: " + con.getRequestProperty("Content-Type"));
            System.out.println("..........................................");

            int responseCode = con.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                String msg = "deleteUser Failed : HTTP error code : "
                    + con.getResponseCode();
                String error = HttpUtils.getError(con);
                if(error == null) {
                    msg += LS + HttpUtils.CONN_NOERROR;
                } else {
                    msg += LS + error;
                }
                Utils.errMsg(msg);
            }
            return responseCode == HttpURLConnection.HTTP_CREATED;
        } catch(Exception ex) {
            Utils.excMsg("Failed to delete user", ex);
            return false;
        }
    }

    public static String listNotifications() {
        if(token == null) {
            Utils.errMsg("No token");
            return null;
        }
        String json = null;
        String basicAuth = "Basic " + access_code;
        String url = ACCESS_LINK_URL + "/notifications";
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", basicAuth);
            con.setDoInput(true);
            con.setDoOutput(true);

            System.out.println("..........................................");
            System.out.println(url);
            System.out.println(basicAuth);
            System.out.println("..........................................");
            System.out.println("Accept: " + con.getRequestProperty("Accept"));
            System.out.println(
                "Content-Length: " + con.getRequestProperty("Content-Length"));
            System.out.println(
                "Content-Type: " + con.getRequestProperty("Content-Type"));
            System.out.println("..........................................");

            int responseCode = con.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                String msg = "list Failed : HTTP error code : "
                    + con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    msg += LS + "(No content)" + LS;
                }
                String error = HttpUtils.getError(con);
                if(error == null) {
                    msg += LS + HttpUtils.CONN_NOERROR;
                } else {
                    msg += LS + error;
                }
                Utils.errMsg(msg);
            }

            String response = HttpUtils.getInput(con);
            System.out.println(response);
            if(response != null && !response.equals(HttpUtils.CONN_NOINPUT)) {
                json = response;
            }

            // Response headers
            System.out.println("..........................................");
            for(Map.Entry<String, List<String>> entries : con.getHeaderFields()
                .entrySet()) {
                String values = "";
                for(String value : entries.getValue()) {
                    values += value + ",";
                }
                System.out.println(entries.getKey() + " - " + values);
            }
            System.out.println("..........................................");
        } catch(Exception ex) {
            Utils.excMsg("Failed to get available data list", ex);
        }
        if(json == null) {
            return null;
        } else {
            // Gson gson = new Gson();
            // User user = gson.fromJson(json,
            // net.kenevans.polar.accessmanager.classes.User.class);
            return json;
        }
    }

}
