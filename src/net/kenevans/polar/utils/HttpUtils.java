package net.kenevans.polar.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Created on Jan 31, 2019
 * By Kenneth Evans, Jr.
 */

public class HttpUtils
{
    public static final String LS = System.getProperty("line.separator");
    public static final String CONN_NOINPUT = "Cannot read input stream";
    public static final String CONN_NOERROR = "Cannot read error stream";
    public static final String CONN_WRITE_ERROR = "Cannot write output stream";
    public static final String CONN_OK = "OK";
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

    /**
     * Gets the status message for a given status code.
     *
     * @param statusCode
     * @return The status message or null if the code is not found.
     */
    public String getStatusMessage(int statusCode) {
        return HTTP_CODES.get(statusCode);
    }

    public static String getInput(HttpURLConnection con) {
        if(con == null) {
            return null;
        }
        InputStream is = null;
        String result = null;
        try {
            is = con.getInputStream();
            if(con == null || is == null) {
                return CONN_NOINPUT;
            }
        } catch(IOException ex) {
            return CONN_NOINPUT;
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
            String msg = "Error reading input stream" + LS + ex.getMessage();
            if(content != null && content.toString() != null) {
                return content.toString() + LS + msg;
            } else {
                return msg;
            }
        }
        return result;
    }

    public static String getError(HttpURLConnection con) {
        if(con == null) {
            return null;
        }
        InputStream is = null;
        String result = null;
        is = con.getErrorStream();
        if(con == null || is == null) {
            return CONN_NOERROR;
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
            String msg = "Error reading error stream" + LS + ex.getMessage();
            if(content != null && content.toString() != null) {
                return content.toString() + LS + msg;
            } else {
                return msg;
            }
        }
        return result;
    }

    public static String writeOutput(HttpURLConnection con, byte[] output) {
        if(con == null) {
            return null;
        }
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(con.getOutputStream());
            out.write(output);
            out.flush();
            out.close();
            return CONN_OK;
        } catch(IOException ex) {
            return CONN_WRITE_ERROR;
        }
    }

    public static String getHeaderFields(HttpURLConnection con) {
        if(con == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        con.getHeaderFields().entrySet().stream()
            .filter(entry -> entry.getKey() != null).forEach(entry -> {
                sb.append(entry.getKey()).append(": ");
                List<String> headerValues = entry.getValue();
                Iterator<String> it = headerValues.iterator();
                if(it.hasNext()) {
                    sb.append(it.next());
                    while(it.hasNext()) {
                        sb.append(", ").append(it.next());
                    }
                }
                sb.append(LS);
            });
        if(sb == null) {
            return null;
        } else {
            return sb.toString();
        }
    }

}
