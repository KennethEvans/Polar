package net.kenevans.polar.accessmanager.ui;

import java.net.HttpURLConnection;
import java.text.BreakIterator;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.Activity;
import net.kenevans.polar.accessmanager.classes.ActivityLogs;
import net.kenevans.polar.accessmanager.classes.Exercise;
import net.kenevans.polar.accessmanager.classes.Exercises;
import net.kenevans.polar.accessmanager.classes.ExercisesHash;
import net.kenevans.polar.accessmanager.classes.PhysicalInformation;
import net.kenevans.polar.accessmanager.classes.PhysicalInformations;
import net.kenevans.polar.accessmanager.classes.TransactionLocation;
import net.kenevans.polar.accessmanager.classes.User;
import net.kenevans.polar.utils.Utils;

/*
 * Created on Feb 1, 2019
 * By Kenneth Evans, Jr.
 */

/**
 * Http
 * 
 * @author Kenneth Evans, Jr.
 */
public class Http implements IConstants
{
    // private String getAccessCode() = "";
    // private String getToken() = "";
    // private String getClientUserId() = DEFAULT_USER_ID;
    // private String getPolarUserId() = "";
    // private Integer getExerciseTransactionId() = -1;
    // public static List<String> physicalInfoList = new ArrayList<>();
    public int lastResponseCode;
    public String lastResponseMessage = "";
    public boolean debug;

    PolarAccessManager manager;

    public Http(PolarAccessManager manager) {
        this.manager = manager;
    }

    /**
     * @return The value of getAccessCode().
     */
    public String getAccessCode() {
        return manager.getSettings().getAccessCode();
    }

    /**
     * @param getAccessCode() The new value for getAccessCode().
     */
    public void setAccessCode(String accessCode) {
        manager.getSettings().setAccessCode(accessCode);
    }

    /**
     * @return The value of getToken().
     */
    public String getToken() {
        return manager.getSettings().getToken();
    }

    /**
     * @param getToken() The new value for getToken().
     */
    public void setToken(String token) {
        manager.getSettings().setToken(token);
    }

    /**
     * @return The value of getClientUserId().
     */
    public String getClientUserId() {
        return manager.getSettings().getClientUserId();
    }

    /**
     * @param getClientUserId() The new value for getClientUserId().
     */
    public void setClientUserId(String clientUserId) {
        manager.getSettings().setClientUserId(clientUserId);
    }

    /**
     * @return The value of getPolarUserId().
     */
    public String getPolarUserId() {
        return manager.getSettings().getPolarUserId();
    }

    /**
     * @param getPolarUserId() The new value for getPolarUserId().
     */
    public void setPolarUserId(String polarUserId) {
        manager.getSettings().setPolarUserId(polarUserId);
    }

    /**
     * @return The value of physicalInfoTransactionId().
     */
    public Integer getExerciseTransactionId() {
        return manager.getSettings().getExerciseTransactionId();
    }

    /**
     * @return The value of activityTransactionId().
     */
    public Integer getActivityTransactionId() {
        return manager.getSettings().getActivityTransactionId();
    }

    /**
     * @return The value of physicalInfoTransactionId().
     */
    public Integer getPhysicalInfoTransactionId() {
        return manager.getSettings().getPhysicalInfoTransactionId();
    }

    /**
     * @param physicalInfoTransactionId The new value for
     *            physicalInfoTransactionId.
     */
    public void setExerciseTransactionId(Integer physicalInfoTransactionId) {
        manager.getSettings()
            .setExerciseTransactionId(physicalInfoTransactionId);
    }

    /**
     * @param activityTransactionId The new value for activityTransactionId.
     */
    public void setActivityTransactionId(Integer activityTransactionId) {
        manager.getSettings().setActivityTransactionId(activityTransactionId);
    }

    /**
     * @param physicalInfoTransactionId The new value for
     *            physicalInfoTransactionId.
     */
    public void setPhysicalInfoTransactionId(
        Integer physicalInfoTransactionId) {
        manager.getSettings()
            .setPhysicalInfoTransactionId(physicalInfoTransactionId);
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
                    BreakIterator breakItr = BreakIterator.getLineInstance();
                    breakItr.setText(buf.substring(lineStart, i));
                    int end = breakItr.last();

                    // If the last character in the search string isn't a space,
                    // we can't split on it (looks bad). Search for a previous
                    // break character
                    if(end == limit + 1) {
                        if(!Character
                            .isWhitespace(buf.charAt(lineStart + end))) {
                            end = breakItr.preceding(end - 1);
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

    public String getLastResponseCodeString() {
        return lastResponseCode + " "
            + Request.getStatusMessage(lastResponseCode);
    }

    public String getRateLimits(boolean popup) {
        lastResponseMessage = "";
        if(getAccessCode() == null) {
            lastResponseMessage = "No getAccessCode()";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        // Use the notifications as it doesn't require the token
        // Using just ACCESS_LINK_URL works but gives a 404
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, getAccessCode());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode > 299) {
            lastResponseMessage = "list getRateLimits: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + error;
            }
            if(popup) {
                if(popup) {
                    Utils.errMsg(lastResponseMessage);
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

    public String getAuthorizationURL() {
        lastResponseMessage = "";
        if(getAccessCode() == null || getAccessCode().isEmpty()) {
            lastResponseMessage = "No access code";
            Utils.errMsg(lastResponseMessage);
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(getAccessCode());
        String decodedString = new String(decodedBytes);
        String[] parts = decodedString.split(":");
        if(parts == null || parts.length != 2) {
            lastResponseMessage = "Unable to parse access code";
            Utils.errMsg(lastResponseMessage);
            return null;
        }
        return AUTHORIZATION_URL_PREFIX + parts[0];
    }

    /**
     * Queries the server for the token using the code received from the
     * getAccessCode() request.
     * 
     * @param code Access code received from the web browser.
     * @return An AssetToken with the data or null on failure.
     */
    public AccessToken getToken(String code, boolean popup) {
        lastResponseMessage = "";
        if(getAccessCode() == null) {
            lastResponseMessage = "No getAccessCode()";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.POST, ACCESS_TOKEN_URL);
        req.setAuthorization(Request.AuthMode.BASIC, getAccessCode());
        req.setRequestProperty("Accept", "application/json;charset=UTF-8");
        req.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");

        String body = "grant_type=authorization_code&code=" + code;
        boolean res = req.writeOutput(body);
        if(!res) {
            lastResponseMessage = "Failed to write output " + LS
                + req.lastError;
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getToken Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + error;
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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

    public User registerUser(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No getToken()";
            if(popup) {
                if(popup) {
                    Utils.errMsg(lastResponseMessage);
                }
            }
            return null;
        }
        Request req = new Request(Request.Method.POST,
            ACCESS_LINK_URL + "users");
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");
        req.setRequestProperty("Content-Type", "application/json");

        String body = "{\"member-id\": \"" + getClientUserId() + "\"}";
        boolean res = req.writeOutput(body);
        if(!res) {
            lastResponseMessage = "Failed to write output " + LS
                + req.lastError;
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "registerUser Failed: "
                + getLastResponseCodeString();
            // String error = req.getError();
            // if(error != null) {
            // lastResponseMessage += LS + error;
            // }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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
                String polarUserId = obj.polarUserId;
                // Save it now
                manager.getSettings().savePolarUserId(polarUserId);
                System.out.println("polar_user_id-=" + polarUserId);
            }
            return obj;
        }
    }

    public User getUserInformation(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No getToken()";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + getPolarUserId());
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getUserInformation Failed: "
                + getLastResponseCodeString();
            // if(error != null) {
            // lastResponseMessage += LS + error;
            // }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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

    public boolean deleteUser(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No getToken()";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        if(getPolarUserId() == null || getPolarUserId().length() == 0) {
            lastResponseMessage = "No Polar user_id";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        int result = JOptionPane.showConfirmDialog(null,
            "Are you sure?" + LS
                + "This revokes the access getToken() and will make" + LS
                + "any existing data inaccessible forever." + LS
                + "OK to continue?",
            " Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if(result != JOptionPane.OK_OPTION) {
            return false;
        }
        Request req = new Request(Request.Method.DELETE,
            ACCESS_LINK_URL + "users/" + getPolarUserId());
        req.setAuthorization(Request.AuthMode.BEARER, getToken());

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            lastResponseMessage = "deleteUser Failed: "
                + getLastResponseCodeString();
            // String error = req.getError();
            // if(error != null) {
            // lastResponseMessage += LS + error;
            // }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        return true;
    }

    public String listNotifications(boolean popup) {
        lastResponseMessage = "";
        if(getAccessCode() == null) {
            lastResponseMessage = "No getAccessCode()";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "notifications");
        req.setAuthorization(Request.AuthMode.BASIC, getAccessCode());
        req.setRequestProperty("Accept", "application/json");
        if(debug) {
            System.out.println("*** " + req.url);
        }

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "listNotifications Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + error;
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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
     * Get available physicalInfo. Does not return a valid JSONObject. Returns a
     * JSON list of something similar to physicalInfo, with some differences.
     * This may be undocumented, hence a mistake.
     * 
     * @return
     */
    public ExercisesHash getExercisesHash(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "physicalInfos");
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getExercisesHash Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            // This is not a JSON element as returned, make one
            json = "{\"physicalInfos-hash\" :" + json + "}";
            System.out.println(json);
            Gson gson = new Gson();
            ExercisesHash obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.ExercisesHash.class);
            return obj;
        }
    }

    public TransactionLocation getExerciseTransactionLocation(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.POST, ACCESS_LINK_URL
            + "users/" + getPolarUserId() + "/physicalInfo-transactions");
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_CREATED) {
            lastResponseMessage = "getTranslationLocation Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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
                int physicalInfoTransactionId = obj.transactionId;
                manager.getSettings()
                    .saveExerciseTransactionId(physicalInfoTransactionId);
                System.out
                    .println("transaction_id=" + physicalInfoTransactionId);
                String resourceUri = obj.resourceUri;
                System.out.println("resourceUri=" + resourceUri);
            }
            return obj;
        }
    }

    public TransactionLocation getActivityTransactionLocation(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.POST, ACCESS_LINK_URL
            + "users/" + getPolarUserId() + "/activity-transactions");
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_CREATED) {
            lastResponseMessage = "getTranslationLocation Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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
                int activityTransactionId = obj.transactionId;
                manager.getSettings()
                    .saveActivityTransactionId(activityTransactionId);
                System.out.println("transaction_id=" + activityTransactionId);
                String resourceUri = obj.resourceUri;
                System.out.println("resourceUri=" + resourceUri);
            }
            return obj;
        }
    }

    public TransactionLocation getPhysicalInfoTransactionLocation(
        boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.POST,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/physical-information-transactions");
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_CREATED) {
            lastResponseMessage = "getTranslationLocation Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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
                int physicalInfoTransactionId = obj.transactionId;
                manager.getSettings()
                    .savePhysicalInfoTransactionId(physicalInfoTransactionId);
                System.out
                    .println("transaction_id=" + physicalInfoTransactionId);
                String resourceUri = obj.resourceUri;
                System.out.println("resourceUri=" + resourceUri);
            }
            return obj;
        }
    }

    public boolean commitExerciseTransaction(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        if(getExerciseTransactionId() < 0) {
            lastResponseMessage = "No physicalInfoTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        int result = JOptionPane.showConfirmDialog(null,
            "Are you sure?" + LS
                + "This will cause any existing data on Polar Access" + LS
                + "to be removed forever." + LS + "OK to continue?",
            " Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if(result != JOptionPane.OK_OPTION) {
            return false;
        }
        Request req = new Request(Request.Method.PUT,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/physicalInfo-transactions/" + getExerciseTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "commitTransaction Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        setExerciseTransactionId(D_EXERCISE_TRANSACTION_ID);
        return true;
    }

    public boolean commitActivityTransaction(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        if(getActivityTransactionId() < 0) {
            lastResponseMessage = "No activityTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        int result = JOptionPane.showConfirmDialog(null,
            "Are you sure?" + LS
                + "This will cause any existing data on Polar Access" + LS
                + "to be removed forever." + LS + "OK to continue?",
            " Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if(result != JOptionPane.OK_OPTION) {
            return false;
        }
        Request req = new Request(Request.Method.PUT,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/activity-transactions/" + getActivityTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "commitTransaction Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        setActivityTransactionId(D_ACTIVITY_TRANSACTION_ID);
        return true;
    }

    public boolean commitPhysicalInfoTransaction(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        if(getPhysicalInfoTransactionId() < 0) {
            lastResponseMessage = "No physicalInfoTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        int result = JOptionPane.showConfirmDialog(null,
            "Are you sure?" + LS
                + "This will cause any existing data on Polar Access" + LS
                + "to be removed forever." + LS + "OK to continue?",
            " Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if(result != JOptionPane.OK_OPTION) {
            return false;
        }
        Request req = new Request(Request.Method.PUT,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/physical-information-transactions/"
                + getPhysicalInfoTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "commitTransaction Failed: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return false;
        }
        setPhysicalInfoTransactionId(D_PHYSICAL_INFO_TRANSACTION_ID);
        return true;
    }

    public Exercises getExerciseList(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(getExerciseTransactionId() < 0) {
            lastResponseMessage = "No exerciseTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/physicalInfo-transactions/" + getExerciseTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");
        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getExerciseList: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            Exercises obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.Exercises.class);
            if(obj != null) {
                List<String> exercisesList = obj.exercises;
                if(exercisesList == null || exercisesList.isEmpty()) {
                    System.out.println("No exercises");
                } else {
                    for(String exercise : exercisesList) {
                        // Http.physicalInfoList.add(physicalInfo);
                        System.out.println(exercise);
                    }
                }
            }
            return obj;
        }
    }

    public ActivityLogs getActivityList(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(getActivityTransactionId() < 0) {
            lastResponseMessage = "No activityTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/activity-transactions/" + getActivityTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");
        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getActivityList: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            ActivityLogs obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.ActivityLogs.class);
            if(obj != null) {
                List<String> activityList = obj.activityLogs;
                if(activityList == null || activityList.isEmpty()) {
                    System.out.println("No activity logs");
                } else {
                    for(String activity : activityList) {
                        // Http.physicalInfoList.add(physicalInfo);
                        System.out.println(activity);
                    }
                }
            }
            return obj;
        }
    }

    public PhysicalInformations getPhysicalInfoList(boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(getPhysicalInfoTransactionId() < 0) {
            lastResponseMessage = "No physicalInfoTransactionId";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET,
            ACCESS_LINK_URL + "users/" + getPolarUserId()
                + "/physical-information-transactions/"
                + getPhysicalInfoTransactionId());
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");
        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getPhysicalInfoList: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        // Http.physicalInfoList.clear();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            PhysicalInformations obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.PhysicalInformations.class);
            if(obj != null) {
                List<String> physicalInfoList = obj.physicalInformations;
                if(physicalInfoList == null || physicalInfoList.isEmpty()) {
                    System.out.println("No physical informations");
                } else {
                    for(String physicalInfo : physicalInfoList) {
                        // Http.physicalInfoList.add(physicalInfo);
                        System.out.println(physicalInfo);
                    }
                }
            }
            return obj;
        }
    }

    public Exercise getExerciseSummary(String url, boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(url == null) {
            lastResponseMessage = "No url given";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getExerciseSummary: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
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

    public Activity getActivitySummary(String url, boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(url == null) {
            lastResponseMessage = "No url given";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getctivitySummary: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            Activity obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.Activity.class);
            if(obj != null) {
            }
            return obj;
        }
    }

    public PhysicalInformation getPhysicalInfoSummary(String url,
        boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(url == null) {
            lastResponseMessage = "No url given";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/json");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getExerciseSummary: "
                + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }

        String json = req.getInput();
        if(json == null) {
            return null;
        } else {
            System.out.println(json);
            Gson gson = new Gson();
            PhysicalInformation obj = gson.fromJson(json,
                net.kenevans.polar.accessmanager.classes.PhysicalInformation.class);
            if(obj != null) {
            }
            return obj;
        }
    }

    public String getGpx(String url, boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(url == null) {
            lastResponseMessage = "No url given";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/gpx+xml");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getGpx: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        return req.getInput();
    }

    public String getTcx(String url, boolean popup) {
        lastResponseMessage = "";
        if(getToken() == null) {
            lastResponseMessage = "No token";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        if(url == null) {
            lastResponseMessage = "No url given";
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        Request req = new Request(Request.Method.GET, url);
        if(debug) {
            System.out.println("*** " + req.url);
        }
        req.setAuthorization(Request.AuthMode.BEARER, getToken());
        req.setRequestProperty("Accept", "application/vnd.garmin.tcx+xml");

        int responseCode = lastResponseCode = req.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            lastResponseMessage = "getTcx: " + getLastResponseCodeString();
            String error = req.getError();
            if(error != null) {
                lastResponseMessage += LS + wordWrap(error, 80);
            }
            if(popup) {
                Utils.errMsg(lastResponseMessage);
            }
            return null;
        }
        String tcx = req.getUnzippedInput();
        return tcx;
    }

}
