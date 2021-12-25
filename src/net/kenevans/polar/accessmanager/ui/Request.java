package net.kenevans.polar.accessmanager.ui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

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
            put(HttpsURLConnection.HTTP_ACCEPTED, "Accepted");
            put(HttpsURLConnection.HTTP_BAD_GATEWAY, "Bad Gateway");
            put(HttpsURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            put(HttpsURLConnection.HTTP_BAD_REQUEST, "Bad Request");
            put(HttpsURLConnection.HTTP_CLIENT_TIMEOUT, "Request Time-Out");
            put(HttpsURLConnection.HTTP_CONFLICT, "Conflict");
            put(HttpsURLConnection.HTTP_CREATED, "Created");
            put(HttpsURLConnection.HTTP_ENTITY_TOO_LARGE,
                "Request Entity Too Large");
            put(HttpsURLConnection.HTTP_FORBIDDEN, "Forbidden");
            put(HttpsURLConnection.HTTP_GATEWAY_TIMEOUT, "Gateway Timeout");
            put(HttpsURLConnection.HTTP_GONE, "Gone");
            put(HttpsURLConnection.HTTP_INTERNAL_ERROR,
                "Internal Server Error");
            put(HttpsURLConnection.HTTP_LENGTH_REQUIRED, "Length Required");
            put(HttpsURLConnection.HTTP_MOVED_PERM, "Moved Permanently");
            put(HttpsURLConnection.HTTP_MOVED_TEMP, "Temporary Redirect");
            put(HttpsURLConnection.HTTP_MULT_CHOICE, "Multiple Choices");
            put(HttpsURLConnection.HTTP_NO_CONTENT, "No Content");
            put(HttpsURLConnection.HTTP_NOT_ACCEPTABLE, "Not Acceptable");
            put(HttpsURLConnection.HTTP_NOT_AUTHORITATIVE,
                "Non-Authoritative Information");
            put(HttpsURLConnection.HTTP_NOT_FOUND, "Not Found");
            put(HttpsURLConnection.HTTP_NOT_IMPLEMENTED, "Not Implemented");
            put(HttpsURLConnection.HTTP_NOT_MODIFIED, "Not Modified");
            put(HttpsURLConnection.HTTP_OK, "OK");
            put(HttpsURLConnection.HTTP_PARTIAL, "Partial Content");
            put(HttpsURLConnection.HTTP_PAYMENT_REQUIRED, "Payment Required");
            put(HttpsURLConnection.HTTP_PRECON_FAILED, "Precondition Failed");
            put(HttpsURLConnection.HTTP_PROXY_AUTH,
                "Proxy Authentication Required");
            put(HttpsURLConnection.HTTP_REQ_TOO_LONG, "Request-URI Too Large");
            put(HttpsURLConnection.HTTP_RESET, "Reset Content");
            put(HttpsURLConnection.HTTP_SEE_OTHER, "See Other");
            put(HttpsURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
            put(HttpsURLConnection.HTTP_UNAVAILABLE, "Service Unavailable");
            put(HttpsURLConnection.HTTP_UNSUPPORTED_TYPE,
                "Unsupported Media Type");
            put(HttpsURLConnection.HTTP_USE_PROXY, "Use Proxy");
            put(HttpsURLConnection.HTTP_VERSION,
                "put(HttpsURLConnection.HTTP Version Not Supported");
        }
    };

    enum AuthMode {
        NONE, BASIC, BEARER
    };

    enum Method {
        GET, PUT, POST, DELETE,
    };

    public URL url;
    public HttpsURLConnection conn;
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
            conn = (HttpsURLConnection)url.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, null, new SecureRandom());
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setRequestMethod(method.toString());
        } catch(Exception ex) {
            error = true;
            lastError = "Failed to create HttpsURLConnection" + LS
                + ex.getMessage();
            return;
        }
        if(method == Method.POST || method == Method.PUT) {
            // DEBUG
            try {
                conn.setDoOutput(true);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
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
