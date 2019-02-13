package net.kenevans.polar.accessmanager.ui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/*
 * Created on Feb 8, 2019
 * By Kenneth Evans, Jr.
 */

public class Request
{
    public static final String LS = System.getProperty("line.separator");
    private static final Map<Integer, String> HTTP_CODES = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;
        {
            put(HttpURLConnection.HTTP_ACCEPTED, "Accepted");
            put(HttpURLConnection.HTTP_BAD_GATEWAY, "Bad Gateway");
            put(HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            put(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
            put(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Request Time-Out");
            put(HttpURLConnection.HTTP_CONFLICT, "Conflict");
            put(HttpURLConnection.HTTP_CREATED, "Created");
            put(HttpURLConnection.HTTP_ENTITY_TOO_LARGE,
                "Request Entity Too Large");
            put(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
            put(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "Gateway Timeout");
            put(HttpURLConnection.HTTP_GONE, "Gone");
            put(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error");
            put(HttpURLConnection.HTTP_LENGTH_REQUIRED, "Length Required");
            put(HttpURLConnection.HTTP_MOVED_PERM, "Moved Permanently");
            put(HttpURLConnection.HTTP_MOVED_TEMP, "Temporary Redirect");
            put(HttpURLConnection.HTTP_MULT_CHOICE, "Multiple Choices");
            put(HttpURLConnection.HTTP_NO_CONTENT, "No Content");
            put(HttpURLConnection.HTTP_NOT_ACCEPTABLE, "Not Acceptable");
            put(HttpURLConnection.HTTP_NOT_AUTHORITATIVE,
                "Non-Authoritative Information");
            put(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
            put(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "Not Implemented");
            put(HttpURLConnection.HTTP_NOT_MODIFIED, "Not Modified");
            put(HttpURLConnection.HTTP_OK, "OK");
            put(HttpURLConnection.HTTP_PARTIAL, "Partial Content");
            put(HttpURLConnection.HTTP_PAYMENT_REQUIRED, "Payment Required");
            put(HttpURLConnection.HTTP_PRECON_FAILED, "Precondition Failed");
            put(HttpURLConnection.HTTP_PROXY_AUTH,
                "Proxy Authentication Required");
            put(HttpURLConnection.HTTP_REQ_TOO_LONG, "Request-URI Too Large");
            put(HttpURLConnection.HTTP_RESET, "Reset Content");
            put(HttpURLConnection.HTTP_SEE_OTHER, "See Other");
            put(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
            put(HttpURLConnection.HTTP_UNAVAILABLE, "Service Unavailable");
            put(HttpURLConnection.HTTP_UNSUPPORTED_TYPE,
                "Unsupported Media Type");
            put(HttpURLConnection.HTTP_USE_PROXY, "Use Proxy");
            put(HttpURLConnection.HTTP_VERSION,
                "put(HttpURLConnection.HTTP Version Not Supported");
        }
    };

    enum AuthMode {
        NONE, BASIC, BEARER
    };

    enum Method {
        GET, PUT, POST, DELETE,
    };

    public URL url;
    public HttpURLConnection conn;
    boolean error = false;
    public String lastError;

    public Request(Method method, String urlString) {
        if(urlString == null || urlString.isEmpty()) {
            return;
        }
        // Get the URL
        try {
            url = new URL(urlString);
        } catch(MalformedURLException ex) {
            error = true;
            lastError = "Failed to create URL" + LS + ex.getMessage();
            return;
        }
        // Get the connection
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(method.toString());
        } catch(IOException ex) {
            error = true;
            lastError = "Failed to creat HttpURLConnection" + LS
                + ex.getMessage();
            return;
        }
        if(method == Method.POST || method == Method.PUT) {
            conn.setDoOutput(true);
        }
    }

    public void setAuthorization(AuthMode mode, String code) {
        if(conn == null) {
            lastError = "No connection";
            return;
        }
        switch(mode) {
        case BASIC:
            conn.setRequestProperty("Authorization", "Basic " + code);
            break;
        case BEARER:
            conn.setRequestProperty("Authorization", "Bearer " + code);
            break;
        default:
            break;
        }
    }

    public void setRequestProperty(String key, String value) {
        if(conn == null) {
            lastError = "No connection";
            return;
        }
        conn.setRequestProperty(key, value);
    }

    public boolean writeOutput(String output) {
        if(conn == null) {
            lastError = "No connection";
            return false;
        }
        byte[] postData = output.getBytes(StandardCharsets.UTF_8);
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(conn.getOutputStream());
            out.write(postData);
            out.flush();
            out.close();
            lastError = "";
            return true;
        } catch(IOException ex) {
            lastError = "Error writing output" + LS + ex.getMessage();
            return false;
        }
    }

    public Integer getResponseCode() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        try {
            return conn.getResponseCode();
        } catch(IOException ex) {
            lastError = "No connection";
            return null;
        }
    }

    public String getInput() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        InputStream is = null;
        String result = null;
        try {
            is = conn.getInputStream();
            if(is == null) {
                lastError = "Cannot get input stream";
                return null;
            }
        } catch(IOException ex) {
            lastError = "Error getting input stream" + LS + ex.getMessage();
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            result = content.toString();
            in.close();
        } catch(IOException ex) {
            lastError = "Error reading input stream" + LS + ex.getMessage();
            if(content != null && content.toString() != null) {
                return content.toString() + LS + lastError;
            } else {
                return null;
            }
        }
        return result;
    }

    public String getUnzippedInput() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        InputStream is = null;
        InputStream gis = null;
        String result = null;
        try {
            is = conn.getInputStream();
            if(is == null) {
                lastError = "Cannot get input stream";
                return null;
            }
            gis = new GZIPInputStream(is);
            if(is == null) {
                lastError = "Cannot get gzip input stream";
                return null;
            }
        } catch(IOException ex) {
            lastError = "Error getting input stream" + LS + ex.getMessage();
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(gis));
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            result = content.toString();
            in.close();
        } catch(IOException ex) {
            lastError = "Error reading input stream" + LS + ex.getMessage();
            if(content != null && content.toString() != null) {
                return content.toString() + LS + lastError;
            } else {
                return null;
            }
        }
        return result;
    }

    public String getUnzippedInput1() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        String result = null;
        try (InputStream is = conn.getInputStream();
            InputStream gis = new GZIPInputStream(is);
            Reader reader = new InputStreamReader(gis);
            Writer writer = new StringWriter();) {
            char[] buffer = new char[10240];
            for(int length = 0; (length = reader.read(buffer)) > 0;) {
                writer.write(buffer, 0, length);
            }
            result = writer.toString();
        } catch(IOException ex) {
            lastError = "Error reading input stream" + LS + ex.getMessage();
        }
        return result;
    }

    public String getError() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        InputStream is = null;
        String result = null;
        is = conn.getErrorStream();
        if(is == null) {
            lastError = "Failed to get error stream";
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            result = content.toString();
            in.close();
        } catch(IOException ex) {
            lastError = "Error reading error stream" + LS + ex.getMessage();
            if(content != null && content.toString() != null) {
                return content.toString() + LS + lastError;
            } else {
                return null;
            }
        }
        return result;
    }

    public Map<String, List<String>> getHeaderFields() {
        lastError = "";
        if(conn == null) {
            lastError = "No connection";
            return null;
        }
        return conn.getHeaderFields();
    }

    /**
     * Gets the status message for a given status code.
     *
     * @param statusCode
     * @return The status message or null if the code is not found.
     */
    public static String getStatusMessage(int statusCode) {
        return HTTP_CODES.get(statusCode);
    }

}
