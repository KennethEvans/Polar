package net.kenevans.polar.accessmanager.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.Gson;

import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.Exercise;
import net.kenevans.polar.accessmanager.classes.ExerciseHash;
import net.kenevans.polar.accessmanager.classes.Exercises;
import net.kenevans.polar.accessmanager.classes.ExercisesHash;
import net.kenevans.polar.accessmanager.classes.TransactionLocation;
import net.kenevans.polar.accessmanager.classes.User;
import net.kenevans.polar.utils.AboutBoxPanel;
import net.kenevans.polar.utils.ImageUtils;
import net.kenevans.polar.utils.JsonUtils;
import net.kenevans.polar.utils.Utils;

public class PolarAccessManager extends JFrame
    implements IConstants, PropertyChangeListener
{
    public static final String LS = System.getProperty("line.separator");
    private static final String NAME = "Polar Access Http";
    private static final String VERSION = "1.0.0";
    private static final String HELP_TITLE = NAME + " " + VERSION;
    private static final String AUTHOR = "Written by Kenneth Evans, Jr.";
    private static final String COPYRIGHT = "Copyright (c) 2012-2019 Kenneth Evans";
    private static final String COMPANY = "kenevans.net";
    private static SimpleDateFormat fileDateFormat = new SimpleDateFormat(
        "yyyy-MM-dd_HH-mm-ss");
    private static SimpleDateFormat startTimeFormat = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final long serialVersionUID = 1L;

    private static final String TITLE = NAME;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private SaveMode saveMode = SaveMode.SKIP;

    private JTextArea textArea;
    private JMenuBar menuBar;

    private WebPageDialog webPageDialog;

    // TODO Make this a preference and implement it. Also for MergeTcxGpx.
    private String initialSaveDir = "C:/Users/evans/Documents/GPSLink/Polar/Access";

    public PolarAccessManager() {
        uiInit();

        System.out.println("PolarAccessManager started at: " + new Date());

        Http.getPreferences();

        // Debug WebPage
        // Http.setToken(null);

        // Debug Fiddler
        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("https.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "8888");
        // System.setProperty("https.proxyPort", "8888");
        appendLineText(
            "java.version=" + System.getProperty("java.version", ""));
        appendLineText("java.home=" + System.getProperty("java.home", ""));
        appendLineText("proxySet=" + System.getProperty("proxySet", ""));
        appendLineText("http.proxyPort=" + System.getProperty("proxyPort", ""));
        appendLineText(
            "https.proxyHost=" + System.getProperty("https.proxyHost", ""));
        appendLineText(
            "https.proxyPort=" + System.getProperty("https.proxyPort", ""));
        appendLineText("");

        // appendLineText("access_code=" + Http.access_code);
        appendLineText("token=" + Http.token);
        appendLineText("client_user_id=" + Http.client_user_id);
        appendLineText("polar_user_id=" + Http.polar_user_id);
        appendLineText(
            "exercise_transaction_id=" + Http.exercise_transaction_id);

        // TEMPORARY
        // Http.polar_user_id="9839019";
        // Http.token="ef0f9246a9d0216e9d5a4c21c349d391";
        // Http.setPreferences();
        // Http.exercise_transaction_id = 173373912;

        Http.debug = true;
    }

    /**
     * Initializes the user interface.
     */
    void uiInit() {
        this.setLayout(new BorderLayout());

        textArea = new JTextArea(25, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the menus.
     */
    private void initMenus() {
        JMenuItem menuItem;

        // Menu
        menuBar = new JMenuBar();

        // File
        JMenu menu = new JMenu();
        menu.setText("File");
        menuBar.add(menu);

        // File Exit
        menuItem = new JMenuItem();
        menuItem.setText("Exit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                quit();
            }
        });
        menu.add(menuItem);

        // Basic
        menu = new JMenu();
        menu.setText("Basic");
        menuBar.add(menu);

        // Set access code
        menuItem = new JMenuItem();
        menuItem.setText("Set Access Code");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "Set access code");
                String res = JOptionPane
                    .showInputDialog("Enter new access code", Http.access_code);
                if(res != null) {
                    Http.access_code = res;
                    Http.setPreferences();
                    PolarAccessManager.this
                        .appendLineText("access_code=" + Http.access_code);
                } else {
                    appendLineText("Aborted");
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem();
        menuItem.setText("Set Client User ID");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "Set client-user-id");
                String res = JOptionPane.showInputDialog(
                    "Enter new client-user-id", Http.client_user_id);
                if(res != null) {
                    Http.client_user_id = res;
                    Http.setPreferences();
                    appendLineText("polar_user_id=" + Http.client_user_id);
                } else {
                    appendLineText("Aborted");
                }
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Get access
        menuItem = new JMenuItem();
        menuItem.setText("Get Access");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                getAccess();
            }
        });
        menu.add(menuItem);

        // Register user
        menuItem = new JMenuItem();
        menuItem.setText("Register User");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "registerUser");
                User obj = Http.registerUser(true);
                if(obj == null) {
                    appendLineText("registerUser failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("User:");
                Gson gson = new Gson();
                String json = gson.toJson(obj);
                appendLineText(JsonUtils.prettyFormat(json));
            }
        });
        menu.add(menuItem);

        // Get user information
        menuItem = new JMenuItem();
        menuItem.setText("Get User Information");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "getUserInformation");
                User user = Http.getUserInformation(true);
                if(user == null) {
                    appendLineText("getUserInformation failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("User:");
                Gson gson = new Gson();
                String json = gson.toJson(user);
                appendLineText(JsonUtils.prettyFormat(json));
            }
        });
        menu.add(menuItem);

        // Delete user
        menuItem = new JMenuItem();
        menuItem.setText("Delete User");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "deleteUser");
                boolean res = Http.deleteUser(true);
                if(res) {
                    appendLineText("deleteUser succeeded");
                } else {
                    appendLineText("deleteUser failed "
                        + Http.getLastResponseCodeString());
                }
                return;
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Get rate limits
        menuItem = new JMenuItem();
        menuItem.setText("Get Rate Limits");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getRateLimits");
                String data = Http.getRateLimits(true);
                if(data == null) {
                    appendLineText("getRateLimits failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Rate Limits:");
                appendLineText(data);
                return;
            }
        });
        menu.add(menuItem);

        // Data
        menu = new JMenu();
        menu.setText("Data");
        menuBar.add(menu);

        // Get available data
        menuItem = new JMenuItem();
        menuItem.setText("Get Available Data");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "listNotifications");
                String json = Http.listNotifications(true);
                if(json == null) {
                    appendLineText("listNotifications failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Available Data:");
                // Gson gson = new Gson();
                // String json = gson.toJson(user);
                appendLineText(JsonUtils.prettyFormat(json));
                // appendLineText(json);
                return;
            }
        });
        menu.add(menuItem);

        // List exercise
        menuItem = new JMenuItem();
        menuItem.setText("Get Exercises Hash");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getExercisesHash");
                ExercisesHash obj = Http.getExercisesHash(true);
                if(obj == null) {
                    appendLineText("getExercisesHash failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Exercises Hash:");
                List<ExerciseHash> exerciseList = obj.exercisesHash;
                if(exerciseList == null || exerciseList.isEmpty()) {
                    appendLineText("No exercises");
                    return;
                }
                int nExercise = 0;
                for(ExerciseHash exercise : exerciseList) {
                    nExercise++;
                    appendLineText("Exercise " + nExercise);
                    appendLineText("  id=" + exercise.id);
                    appendLineText("  start_time=" + exercise.startTime);
                    appendLineText("  upload_time=" + exercise.uploadTime);
                    appendLineText("  sport=" + exercise.sport);
                    appendLineText(
                        "  detailed_sport_info=" + exercise.detailedSportInfo);
                    appendLineText("  duration=" + exercise.duration);
                    appendLineText("  distance=" + exercise.distance);
                }
                return;
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Get exercise transaction id
        menuItem = new JMenuItem();
        menuItem.setText("Get Exercise Transaction ID");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getExerciseTranslationLocation");
                TransactionLocation obj = Http
                    .getExerciseTranslationLocation(true);
                if(obj == null) {
                    appendLineText("getExerciseTranslationLocation failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText(
                    "New exercise_token-id=" + Http.exercise_transaction_id);
                appendLineText("Transaction Location:");
                Gson gson = new Gson();
                String json = gson.toJson(obj);
                appendLineText(JsonUtils.prettyFormat(json));
                return;
            }
        });
        menu.add(menuItem);

        // Get exercise list
        menuItem = new JMenuItem();
        menuItem.setText("Get Exercise List");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getExerciseList");
                Exercises obj = Http.getExerciseList(true);
                if(obj == null) {
                    appendLineText("getExerciseList failed "
                        + Http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Exercise List:");
                List<String> exerciseList = obj.exercises;
                if(exerciseList == null) {
                    appendLineText("Exercise List is null");
                    return;
                }
                if(exerciseList.isEmpty()) {
                    appendLineText("Exercise List is empty");
                    return;
                }
                for(String exercise : exerciseList) {
                    appendLineText(exercise);
                }
                return;
            }
        });
        menu.add(menuItem);

        // Get exercise summaries
        menuItem = new JMenuItem();
        menuItem.setText("Get Exercise Summaries");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getExerciseSummaries");
                getExerciseSummaries();
            }
        });
        menu.add(menuItem);

        // Get gpx
        menuItem = new JMenuItem();
        menuItem.setText("Get TCX/GPX");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getTcxGpx");
                getTpxGpx();
                return;
            }
        });
        menu.add(menuItem);

        // Convert
        menu = new JMenu();
        menu.setText("Convert");
        menuBar.add(menu);

        // TCX to GPX
        menuItem = new JMenuItem();
        menuItem.setText("TCX to GPXConvert ...");
        menuItem.setToolTipText("Converts TCX filesto GPX.");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "Convert TCX to GPX");
                MergeTcxAndGpxToGpx merge = new MergeTcxAndGpxToGpx(
                    PolarAccessManager.this, false);
                merge.processTcxFiles();
            }
        });
        menu.add(menuItem);

        // Merge TCX and GPX to GPX
        menuItem = new JMenuItem();
        menuItem.setText("Merge TCX and GPX to GPX...");
        menuItem.setToolTipText("Merges TCX/GPX file pairs to GPX with HR, "
            + "Cadence, and Elevation.");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "Merge TCX/GPX file pairs to GPX");
                MergeTcxAndGpxToGpx merge = new MergeTcxAndGpxToGpx(
                    PolarAccessManager.this, true);
                merge.processTcxFiles();
            }
        });
        menu.add(menuItem);

        // Help
        menu = new JMenu();
        menu.setText("Help");
        menuBar.add(menu);

        menuItem = new JMenuItem();
        menuItem.setText("About");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null,
                    new AboutBoxPanel(HELP_TITLE, AUTHOR, COMPANY, COPYRIGHT),
                    "About", JOptionPane.PLAIN_MESSAGE);
            }
        });
        menu.add(menuItem);
    }

    /**
     * Puts the panel in a JFrame and runs the JFrame.
     */
    public void run() {
        try {
            // Create and set up the window.
            // JFrame.setDefaultLookAndFeelDecorated(true);
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // SwingUtilities.updateComponentTreeUI(this);
            this.setTitle(TITLE);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // frame.setLocationRelativeTo(null);
            // Make the window manager close button run our routine
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    quit();
                }
            });

            // Set the icon
            ImageUtils.setIconImageFromResource(this,
                "/resources/PolarAccess.png");

            // Has to be done here. The menus are not part of the JPanel.
            initMenus();
            this.setJMenuBar(menuBar);

            // Display the window
            this.setBounds(20, 20, WIDTH, HEIGHT);
            this.setVisible(true);

            // Do this after the UI is up
            if(webPageDialog != null) {
                webPageDialog.setVisible(true);
            }
        } catch(Throwable t) {
            Utils.excMsg("Error running PolarAccessManager", t);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent ev) {
        String name = ev.getPropertyName();
        String code = (String)ev.getNewValue();
        if(name.equals(WebPageDialog.CODE_CHANGED) && code != null) {
            appendLineText("Got code=" + code);
            // Prompt to get token
            int selection = JOptionPane.showConfirmDialog(null,
                "Got code. OK to get new token?", "Confirmation",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(selection == JOptionPane.OK_OPTION) {
                AccessToken at = Http.getToken(code, true);
                if(at == null) {
                    appendLineText("Failed to get token");
                    return;
                }
                String token = at.accessToken;
                if(token == null || token.length() == 0) {
                    appendLineText("Invalid token: " + token);
                    return;
                }
                Http.token = token;
                Http.setPreferences();
                appendLineText("New token: " + Http.token);
                appendLineText("  access_token=" + at.accessToken);
                appendLineText("  token_type=" + at.tokenType);
                Integer sec = at.expiresIn;
                if(sec != null) {
                    appendLineText("  expires_in=" + String.format(
                        "%d sec, %.1f hours %.1f days %.1f years", sec,
                        sec / 3600f, sec / (3600f * 24f),
                        sec / (3600f * 24F * 365f)));
                }
                appendLineText("  x_user_id=" + at.xUserId);
                if(webPageDialog != null) {
                    webPageDialog.setVisible(false);
                }
            } else {
                appendLineText("Not replacing token");
            }
        }
    }

    /**
     * Sets the text in the TextArea.
     * 
     * @param text
     */
    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }

    /**
     * Appends to the text in the TextArea.
     * 
     * @param text
     */
    public void appendText(String text) {
        String oldText = textArea.getText();
        if(oldText == null) {
            textArea.setText(text);
        } else {
            textArea.setText(oldText + text);

        }
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Appends to the text in the TextArea adding a line separator.
     * 
     * @param text
     */
    public void appendLineText(String text) {
        appendText(text + LS);
    }

    /**
     * Quits the application
     */
    private void quit() {
        Http.setPreferences();
        System.exit(0);
    }

    /**
     * Generate a filename using the current client-user-name and the supplied
     * XMLGregorianCalendar value, activity string, and extension. The activity
     * should be space or underscore-separated words (usually a location). The
     * extension should start with dot.
     * 
     * @param xcal
     * @param activity
     * @param ext
     * @return
     */
    private String getFileName(String startTime, String activity, String ext) {
        String userName = Http.client_user_id;
        if(userName == null || userName.isEmpty()) {
            userName = "Polar";
        }
        userName = userName.trim().replaceAll(" ", "_");
        Date date;
        String time;
        if(startTime == null) {
            time = "0000-00-00_000000";
        } else {
            try {
                date = startTimeFormat.parse(startTime);
                time = fileDateFormat.format(date);
            } catch(ParseException ex) {
                time = "0000-00-00_000000";
            }
        }
        String activity1;
        if(activity == null || activity.isEmpty()) {
            activity1 = "Unknown";
        } else {
            activity1 = activity.replaceAll(" ", "_");
        }
        return userName + "_" + time + "_" + activity1 + ext;
    }

    private void getAccess() {
        appendLineText(LS + "getAccess");
        String accessUrl = Http.getAuthorizationURL();
        if(accessUrl == null) {
            appendLineText("No access code");
            return;
        }
        webPageDialog = new WebPageDialog(this, Http.getAuthorizationURL());
        webPageDialog.setVisible(true);
    }

    private void getExerciseSummaries() {
        // Get a new transaction-id if available
        Http.getExerciseTranslationLocation(false);
        appendLineText("getExerciseTranslationLocation() returned "
            + Http.getLastResponseCodeString());
        // Get the exerciseList
        Exercises exercises = Http.getExerciseList(false);
        if(!Http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getExerciseList() returned " + Http.lastResponseMessage);
        }
        switch(Http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no exercises");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(Http.lastResponseMessage);
            return;
        }
        if(exercises == null) {
            appendLineText("exercises is null");
            return;
        }
        // Loop over exercises
        List<String> exerciseList = exercises.exercises;
        if(exerciseList == null) {
            appendLineText("exerciseList is null");
            return;
        }
        if(exerciseList.isEmpty()) {
            appendLineText("exerciseList is empty");
            return;
        }
        int nExercise = 0;
        Exercise exercise;
        for(String exerciseString : exerciseList) {
            nExercise++;
            exercise = Http.getExerciseSummary(exerciseString, false);
            if(!Http.lastResponseMessage.isEmpty()) {
                appendLineText("getExerciseSummary() returned "
                    + Http.getLastResponseCodeString());
            }
            if(exercise != null) {
                appendLineText("Exercise " + nExercise);
                appendLineText("  start-time=" + exercise.startTime);
                appendLineText("  upload-time=" + exercise.uploadTime);
                appendLineText("  sport=" + exercise.sport);
                appendLineText(
                    "  detailed-sport-info=" + exercise.detailedSportInfo);
                appendLineText("  duration=" + exercise.duration);
                appendLineText("  distance=" + exercise.distance);
                appendLineText("  device=" + exercise.device);
                // appendLineText(" clubName=" + exercise.clubName);
                // appendLineText(" clubId=" + exercise.clubId);
            }
        }
    }

    private void getTpxGpx() {
        // Get a new transaction-id if available
        Http.getExerciseTranslationLocation(false);
        appendLineText("getExerciseTranslationLocation() returned "
            + Http.getLastResponseCodeString());
        // Get the exerciseList
        Exercises exercises = Http.getExerciseList(false);
        if(!Http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getExerciseList() returned " + Http.lastResponseMessage);
        }
        switch(Http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no exercises");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(Http.lastResponseMessage);
            return;
        }
        if(exercises == null) {
            appendLineText("exercises is null");
            return;
        }
        // Loop over exercises
        List<String> exerciseList = exercises.exercises;
        if(exerciseList == null) {
            appendLineText("exerciseList is null");
            return;
        }
        if(exerciseList.isEmpty()) {
            appendLineText("exerciseList is empty");
            return;
        }
        int nExercise = 0;
        int nEmpty = 0, nWritten = 0, nAborted = 0, nSkipped = 0;
        String gpxName, tcxName;
        Exercise exercise;
        String startTime;
        String detailedSportInfo;
        for(String exerciseString : exerciseList) {
            nExercise++;
            appendLineText("Exercise " + nExercise);
            exercise = Http.getExerciseSummary(exerciseString, false);
            if(!Http.lastResponseMessage.isEmpty()) {
                appendLineText("getExerciseSummary() returned "
                    + Http.getLastResponseCodeString());
            }
            if(exercise == null) {
                startTime = null;
                detailedSportInfo = null;
            } else {
                startTime = exercise.startTime;
                detailedSportInfo = exercise.detailedSportInfo;
            }

            // GPX
            gpxName = getFileName(startTime, detailedSportInfo, ".gpx");
            File gpxFile = new File(initialSaveDir, gpxName);
            if(saveMode == SaveMode.SKIP && gpxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + gpxName);
            } else {
                appendLineText(gpxName);
                String url = exerciseString + "/gpx";
                String gpx = Http.getGpx(url, false);
                if(!Http.lastResponseMessage.isEmpty()) {
                    appendLineText(
                        "getGpx() returned " + Http.lastResponseMessage);
                }
                if(gpx == null) {
                    appendLineText("Failed to get GPX for " + nExercise);
                } else {
                    int len = gpx.length();
                    if(len > 80) {
                        len = 80;
                    }
                    // appendLineText(
                    // gpx.substring(0, len) + "... [" + gpx.length() + "]");
                    appendLineText("Length is " + gpx.length());
                    if(len == 0) {
                        nEmpty++;
                    } else {
                        boolean skip = false;
                        if(saveMode == SaveMode.PROMPT && gpxFile.exists()) {
                            int selection = JOptionPane.showConfirmDialog(null,
                                "File exists:" + LS + gpxFile.getPath() + LS
                                    + "OK to overwrite?",
                                "File Exists", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            if(selection != JOptionPane.OK_OPTION) {
                                skip = true;
                                nSkipped++;
                                appendLineText("Aborted " + gpxFile.getPath());
                            }
                        }
                        if(!skip) {
                            try (PrintWriter out = new PrintWriter(gpxFile)) {
                                nWritten++;
                                out.println(gpx);
                                appendLineText("Wrote " + gpxFile.getPath());
                            } catch(FileNotFoundException ex) {
                                appendLineText("Error writing "
                                    + gpxFile.getPath() + LS + ex.getMessage());
                            }
                        }
                    }
                }
            }

            // TCX
            tcxName = getFileName(startTime, detailedSportInfo, ".tcx");
            File tcxFile = new File(initialSaveDir, tcxName);
            if(saveMode == SaveMode.SKIP && tcxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + tcxName);
            } else {
                appendLineText(tcxName);
                String url = exerciseString + "/tcx";
                String tcx = Http.getTcx(url, false);
                if(!Http.lastResponseMessage.isEmpty()) {
                    appendLineText(
                        "getTcx() returned " + Http.lastResponseMessage);
                }
                if(tcx == null) {
                    appendLineText("Failed to get TCX for " + nExercise);
                } else {
                    int len = tcx.length();
                    if(len > 80) {
                        len = 80;
                    }
                    // appendLineText(
                    // tcx.substring(0, len) + "... [" + tcx.length() + "]");
                    appendLineText("Length is " + tcx.length());
                    if(len == 0) {
                        nEmpty++;
                    } else {
                        boolean skip = false;
                        if(saveMode == SaveMode.PROMPT && tcxFile.exists()) {
                            int selection = JOptionPane.showConfirmDialog(null,
                                "File exists:" + LS + tcxFile.getPath() + LS
                                    + "OK to overwrite?",
                                "File Exists", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            if(selection != JOptionPane.OK_OPTION) {
                                skip = true;
                                nAborted++;
                                appendLineText("Aborted " + gpxFile.getPath());
                            }
                        }
                        if(!skip) {
                            try (PrintWriter out = new PrintWriter(tcxFile)) {
                                nWritten++;
                                out.println(tcx);
                                appendLineText("Wrote " + tcxFile.getPath());
                            } catch(FileNotFoundException ex) {
                                appendLineText("Error writing "
                                    + tcxFile.getPath() + LS + ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
        appendLineText("Written: " + nWritten + " Empty: " + nEmpty
            + " Skipped: " + nSkipped + " Aborted: " + nAborted);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        final PolarAccessManager app = new PolarAccessManager();

        try {
            // Set window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);

            // Set the native look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Throwable t) {
            Utils.excMsg("Error setting Look & Feel", t);
        }

        // Make the job run in the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(app != null) {
                    app.run();
                }
            }
        });
    }

}
