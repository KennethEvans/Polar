package net.kenevans.polar.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*
 * Created on Feb 2, 2019
 * By Kenneth Evans, Jr.
 */

public class JsonUtils
{
    public static String getJsonFromJsonFile(String fileName) {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get(fileName)), "UTF-8");
        } catch(IOException ex) {
            Utils.excMsg("Failed to read " + fileName, ex);
            return null;
        }
        return json;
    }

    public static void writeJsonToFile(String json, String fileName) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(fileName);
            out.println(json);
            out.close();
        } catch(FileNotFoundException ex) {
            Utils.excMsg("Failed to write " + fileName, ex);
        }
    }

    /**
     * Returns a pretty printed version of the given JSON String.
     * 
     * @param jsonString
     * @return
     */
    public static String prettyFormat(String jsonString) {
        String prettyJson;
        JsonElement element = null;
        JsonParser parser = new JsonParser();
        try {
            element = parser.parse(jsonString);
        } catch(Exception ex) {
            return "Got exception parsing:" + Utils.LS + jsonString + Utils.LS
                + ex.getMessage();
        }
        if(element == null) {
            return "Failed to parse:" + Utils.LS + jsonString;
        }
        if(!element.isJsonObject()) {
            return "Not valid JSON:" + Utils.LS + jsonString;
        }

        JsonObject json = element.getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        prettyJson = gson.toJson(json);
        return prettyJson;
    }

}
