package net.kenevans.polar.accessmanager.ui;

import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.User;
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

    public static String getAuthorizationURL() {
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
     * Queries the server for the token using the code received from the
     * access_code request.
     * 
     * @param code Access code received from the web browser.
     * @return An AssetToken with the data or null on failure.
     */
    public static AccessToken getToken(String code) {
        if(access_code == null) {
            Utils.errMsg("No access_code");
            return null;
        }
        Request req = new Request(Request.Method.POST, ACCESS_TOKEN_URL);
        req.setAuthorization(Request.AuthMode.BASIC, access_code);
        req.setRequestProperty("Accept", "application/json;charset=UTF-8");
        req.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");

        String body = "grant_type=authorization_code&code=" + code;
        boolean res = req.writeOutput(body);
        if(!res) {
            Utils.errMsg("Failed to write output " + LS + req.lastError);
            return null;
        }

        int responseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getToken Failed: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            Utils.errMsg(msg);
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
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
        Request req = new Request(Request.Method.POST,
            ACCESS_LINK_URL + "users");
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");
        req.setRequestProperty("Content-Type", "application/json");

        String body = "{\"member-id\": \"" + client_user_id + "\"}";
        boolean res = req.writeOutput(body);
        if(!res) {
            Utils.errMsg("Failed to write output " + LS + req.lastError);
            return null;
        }

        int responseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "registerUser Failed: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            // String error = req.getError();
            // if(error != null) {
            // msg += LS + error;
            // }
            Utils.errMsg(msg);
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
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

    public static User getUserInformation() {
        if(token == null) {
            Utils.errMsg("No token");
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + polar_user_id);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getUserInformation Failed: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            // String error = req.getError();
            // if(error != null) {
            // msg += LS + error;
            // }
            Utils.errMsg(msg);
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
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
        int result = JOptionPane.showConfirmDialog(null,
            "Are you sure?" + LS + "This revokes the access token and will make"
                + LS + "any existing data inaccessible forever." + LS
                + "OK to continue?",
            " Conformation", JOptionPane.OK_CANCEL_OPTION);
        if(result != JOptionPane.OK_OPTION) {
            return false;
        }
        Request req = new Request(Request.Method.DELETE,
            ACCESS_LINK_URL + "users/" + polar_user_id);
        req.setAuthorization(Request.AuthMode.BEARER, token);

        int responseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            String msg = "deleteUser Failed: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            // String error = req.getError();
            // if(error != null) {
            // msg += LS + error;
            // }
            Utils.errMsg(msg);
        }
        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }

    public static String listNotifications() {
        if(access_code == null) {
            Utils.errMsg("No access_code");
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, access_code);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "listNotifications Failed: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            Utils.errMsg(msg);
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            // Gson gson = new Gson();
            // User user = gson.fromJson(json,
            // net.kenevans.polar.accessmanager.classes.User.class);
            return json;
        }
    }

    public static String getRateLimits() {
        if(access_code == null) {
            Utils.errMsg("No access_code");
            return null;
        }
        // Use the notifications as it doesn't require the token
        // Using just ACCESS_LINK_URL works but gives a 404
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, access_code);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = req.getResponseCode();
        if(responseCode > 299) {
            String msg = "list getRateLimits: " + responseCode + " "
                + Request.getStatusMessage(responseCode);
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            Utils.errMsg(msg);
        }

        Map<String, List<String>> headers = req.getHeaderFields();
        if(headers == null) {
            return null;
        }
        String info = "";
        String key;
        String values;
        for(Map.Entry<String, List<String>> entries : headers.entrySet()) {
            key = entries.getKey();
            if(key != null && key.startsWith("Rate")) {
                values = "";
                if(entries.getKey().startsWith("Rate")) {
                    for(String value : entries.getValue()) {
                        values += value + ",";
                    }
                }
                info += entries.getKey() + " - " + values + LS;
            }
        }
        return info;
    }

}
