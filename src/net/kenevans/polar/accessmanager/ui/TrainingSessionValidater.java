package net.kenevans.polar.accessmanager.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import net.kenevans.polar.trainingsession.TrainingSession;
import net.kenevans.polar.utils.JsonUtils;
import net.kenevans.polar.utils.Utils;

/*
 * Created on Jan 30, 2019
 * By Kenneth Evans, Jr.
 */

public class TrainingSessionValidater
{
    private static String TEST_FILE = "C:/Users/evans/Documents/GPSLink/Polar/Training Session/training-session-2018-08-10-2719022112-6d312b79-3bda-4569-ac73-6c3889617a48.json";
    private static String OUTPUT_DIR = "C:/Users/evans/Documents/GPSLink/Polar/Training Session/Test/";

    public static boolean validate(String fileName) {
        BufferedReader in = null;
        TrainingSession ts = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            Gson gson = new Gson();
            ts = gson.fromJson(in,
                net.kenevans.polar.trainingsession.TrainingSession.class);

        } catch(FileNotFoundException ex) {
            Utils.excMsg("Could not parse " + fileName, ex);
            return false;
        } finally {
            try {
                if(in != null) in.close();
            } catch(Exception ex) {
                // Do nothing
            }
        }
        String json1 = JsonUtils.getJsonFromJsonFile(fileName);
        json1 = JsonUtils.prettyFormat(json1);
        Gson gson = new Gson();
        String json2 = gson.toJson(ts);
        json2 = JsonUtils.prettyFormat(json2);
        System.out.println("Input file: length=" + new File(fileName).length());
        System.out.println("Json1: length=" + json1.length());
        System.out.println("Json2: length=" + json2.length());
        String fileName1 = OUTPUT_DIR + "TestFile1.json";
        String fileName2 = OUTPUT_DIR + "TestFile2.json";
        System.out.println("Writing " + fileName1);
        JsonUtils.writeJsonToFile(json1, fileName1);
        System.out.println("Writing " + fileName2);
        JsonUtils.writeJsonToFile(json2, fileName2);
        System.out
            .println("Json1 file: length=" + new File(fileName1).length());
        System.out
            .println("Json2 file: length=" + new File(fileName2).length());
        return json1.equals(json2);
    }

    public static void main(String[] args) {
        String fileName = TEST_FILE;
        System.out.println("Validating " + fileName);
        boolean res = validate(fileName);
        System.out.println("Result: " + res);
        System.out.println();
        System.out.println("All done");
    }

}
