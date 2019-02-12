package net.kenevans.polar.accessmanager.ui;

import java.net.HttpURLConnection;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.Exercise;
import net.kenevans.polar.accessmanager.classes.Exercises;
import net.kenevans.polar.accessmanager.classes.ExercisesHash;
import net.kenevans.polar.accessmanager.classes.TransactionLocation;
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
    public static Integer exercise_transaction_id = -1;
    public static List<String> exerciseList = new ArrayList<>();
    public static int lastResponseCode;

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
            exercise_transaction_id = prefs.getInt(P_EXERCISE_TRANSACTION_ID,
                D_EXERCISE_TRANSACTION_ID);

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
            prefs.putInt(P_EXERCISE_TRANSACTION_ID, exercise_transaction_id);
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
     * Reformats a string where lines that are longer than <tt>width</tt> are
     * split apart at the earliest wordbreak or at maxLength, whichever is
     * sooner. If the width specified is less than 5 or greater than the input
     * Strings length the string will be returned as is.
     * <p/>
     * Please note that this method can be lossy - trailing spaces on wrapped
     * lines may be trimmed.
     *
     * @param input the String to reformat.
     * @param width the maximum length of any one line.
     * @return a new String with reformatted as needed.
     */
    public static String wordWrap(String input, int width) {
        // protect ourselves
        if(input == null) {
            return "";
        } else if(width < 5) {
            return input;
        } else if(width >= input.length()) {
            return input;
        }

        StringBuilder buf = new StringBuilder(input);
        boolean endOfLine = false;
        int lineStart = 0;
        for(int i = 0; i < buf.length(); i++) {
            if(buf.charAt(i) == '\n') {
                lineStart = i + 1;
                endOfLine = true;
            }

            // Handle splitting at width character
            if(i > lineStart + width - 1) {
                if(!endOfLine) {
                    int limit = i - lineStart - 1;
                    BreakIterator breaks = BreakIterator.getLineInstance();
                    breaks.setText(buf.substring(lineStart, i));
                    int end = breaks.last();

                    // If the last character in the search string isn't a space,
                    // we can't split on it (looks bad). Search for a previous
                    // break character
                    if(end == limit + 1) {
                        if(!Character
                            .isWhitespace(buf.charAt(lineStart + end))) {
                            end = breaks.preceding(end - 1);
                        }
                    }

                    // If the last character is a space, replace it with a \n
                    if(end != BreakIterator.DONE && end == limit + 1) {
                        buf.replace(lineStart + end, lineStart + end + 1, "\n");
                        lineStart = lineStart + end;
                    }
                    // Otherwise, just insert a \n
                    else if(end != BreakIterator.DONE && end != 0) {
                        buf.insert(lineStart + end, LS);
                        lineStart = lineStart + end + 1;
                    } else {
                        buf.insert(i, LS);
                        lineStart = i + 1;
                    }
                } else {
                    buf.insert(i, '\n');
                    lineStart = i + 1;
                    endOfLine = false;
                }
            }
        }
        return buf.toString();
    }

    public static String getLastResponseCodeString() {
        return lastResponseCode + " "
            + Request.getStatusMessage(lastResponseCode);
    }

    public static String getRateLimits(boolean popup) {
        if(access_code == null) {
            if(popup) {
                Utils.errMsg("No access_code");
            }
            return null;
        }
        // Use the notifications as it doesn't require the token
        // Using just ACCESS_LINK_URL works but gives a 404
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, access_code);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode > 299) {
            String msg = "list getRateLimits: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            if(popup) {
                if(popup) {
                    Utils.errMsg(msg);
                }
            }
            // No return here, get the headers anyway
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

    /**
     * Queries the server for the token using the code received from the
     * access_code request.
     * 
     * @param code Access code received from the web browser.
     * @return An AssetToken with the data or null on failure.
     */
    public static AccessToken getToken(String code, boolean popup) {
        if(access_code == null) {
            if(popup) {
                Utils.errMsg("No access_code");
            }
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
            if(popup) {
                Utils.errMsg("Failed to write output " + LS + req.lastError);
            }
            return null;
        }

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getToken Failed: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            AccessToken obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.AccessToken.class);
            return obj;
        }
    }

    public static User registerUser(boolean popup) {
        if(token == null) {
            if(popup) {
                if(popup) {
                    Utils.errMsg("No token");
                }
            }
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
            if(popup) {
                Utils.errMsg("Failed to write output " + LS + req.lastError);
            }
            return null;
        }

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "registerUser Failed: " + getLastResponseCodeString();
            // String error = req.getError();
            // if(error != null) {
            // msg += LS + error;
            // }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            User obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.User.class);
            if(obj != null) {
                polar_user_id = obj.polarUserId;
                System.out.println("polar_user_id-=" + polar_user_id);
                setPreferences();
            }
            return obj;
        }
    }

    public static User getUserInformation(boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + polar_user_id);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getUserInformation Failed: "
                + getLastResponseCodeString();
            // if(error != null) {
            // msg += LS + error;
            // }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            User user = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.User.class);
            return user;
        }
    }

    public static boolean deleteUser(boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return false;
        }
        if(polar_user_id == null || polar_user_id.length() == 0) {
            if(popup) {
                Utils.errMsg("No Polar user_id");
            }
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

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            String msg = "deleteUser Failed: " + getLastResponseCodeString();
            // String error = req.getError();
            // if(error != null) {
            // msg += LS + error;
            // }
            if(popup) {
                Utils.errMsg(msg);
            }
            return false;
        }
        return true;
    }

    public static String listNotifications(boolean popup) {
        if(access_code == null) {
            if(popup) {
                Utils.errMsg("No access_code");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, access_code);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "listNotifications Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + error;
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
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

    /**
     * Get available exercise. Does not return a valid JSONObject. Returns a
     * JSON list of something similar to exercise, with some differences. This
     * may be undocumented, hence a mistake.
     * 
     * @return
     */
    public static ExercisesHash getExercisesHash(boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "exercises");
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getExercisesHash Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            // This is not a JSON element as returned, make one
            json = "{\"exercises-hash\" :" + json + "}";
            System.out.println(json);
            Gson gson = new Gson();
            ExercisesHash obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.ExercisesHash.class);
            return obj;
        }
    }

    public static TransactionLocation getExerciseTranslationLocation(
        boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        Request req = new Request(Request.Method.POST, ACCESS_LINK_URL
            + "users/" + polar_user_id + "/exercise-transactions");
        System.out.println("*** " + req.url);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_CREATED) {
            String msg = "getTranslationLocation Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            TransactionLocation obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.TransactionLocation.class);
            if(obj != null) {
                exercise_transaction_id = obj.transactionId;
                Manager.setPreferences();
                System.out.println("transaction_id=" + exercise_transaction_id);
                String resourceUri = obj.resourceUri;
                System.out.println("resourceUri=" + resourceUri);
            }
            return obj;
        }
    }

    public static Exercises getExerciseList(boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        if(exercise_transaction_id < 0) {
            if(popup) {
                Utils.errMsg("No exercise_transaction_id");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + polar_user_id
                + "/exercise-transactions/" + exercise_transaction_id);
        System.out.println("*** " + req.url);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");
        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getExerciseList: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        Manager.exerciseList.clear();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            Exercises obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.Exercises.class);
            if(obj != null) {
                List<String> exerciseList = obj.exercises;
                if(exerciseList == null || exerciseList.isEmpty()) {
                    System.out.println("No exercises");
                } else {
                    for(String exercise : exerciseList) {
                        Manager.exerciseList.add(exercise);
                        System.out.println(exercise);
                    }
                }
            }
            return obj;
        }
    }

    public static Exercise getExerciseSummary(String url, boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        if(url == null) {
            if(popup) {
                Utils.errMsg("No url given");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        System.out.println("*** " + req.url);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getExerciseSummary: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            Exercise obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.Exercise.class);
            if(obj != null) {
            }
            return obj;
        }
    }

    public static String getGpx(String url, boolean popup) {
        if(token == null) {
            if(popup) {
                Utils.errMsg("No token");
            }
            return null;
        }
        if(url == null) {
            if(popup) {
                Utils.errMsg("No url given");
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        System.out.println("*** " + req.url);
        req.setAuthorization(Request.AuthMode.BEARER, token);
        req.setRequestProperty("Accept", "application/gpx+xml");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            String msg = "getGpx: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                msg += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(msg);
            }
            return null;
        }
        return req.getInput();
    }

}
