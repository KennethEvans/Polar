package net.kenevans.polar.accessmanager.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXParser;
import net.kenevans.polar.accessmanager.classes.AccessToken;
import net.kenevans.polar.accessmanager.classes.Activity;
import net.kenevans.polar.accessmanager.classes.ActivityLogs;
import net.kenevans.polar.accessmanager.classes.Exercise;
import net.kenevans.polar.accessmanager.classes.ExerciseHash;
import net.kenevans.polar.accessmanager.classes.Exercises;
import net.kenevans.polar.accessmanager.classes.ExercisesHash;
import net.kenevans.polar.accessmanager.classes.PhysicalInformation;
import net.kenevans.polar.accessmanager.classes.PhysicalInformations;
import net.kenevans.polar.accessmanager.classes.TransactionLocation;
import net.kenevans.polar.accessmanager.classes.User;
import net.kenevans.polar.accessmanager.preferences.PreferencesDialog;
import net.kenevans.polar.accessmanager.preferences.Settings;
import net.kenevans.polar.accessmanager.ui.FileRenameDialog.Result;
import net.kenevans.polar.utils.AboutBoxPanel;
import net.kenevans.polar.utils.ImageUtils;
import net.kenevans.polar.utils.JsonUtils;
import net.kenevans.polar.utils.ScrolledHTMLDialog;
import net.kenevans.polar.utils.Utils;
import net.kenevans.polar.utils.XmlUtils;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.parser.TCXParser;

public class PolarAccessManager extends JFrame
    implements IConstants, PropertyChangeListener, IWorkerMethod
{
    enum BackgroundMethodType {
        GetTcxConvertGpx, GetTcxGpx, GetExerciseSummaries, GetActivitySummaries, GetPhysicalInfoSummaries,
    }

    public static final String LS = System.getProperty("line.separator");
    private static final String NAME = "Polar Access Manager";
    private static final String VERSION = "2.4.0";
    private static final String HELP_TITLE = NAME + " " + VERSION;
    private static final String AUTHOR = "Written by Kenneth Evans, Jr.";
    private static final String COPYRIGHT = "Copyright (c) 2019 Kenneth Evans";
    private static final String COMPANY = "kenevans.net";
    public static SimpleDateFormat fileDateFormat = new SimpleDateFormat(
        "yyyy-MM-dd_HH-mm-ss");
    public static SimpleDateFormat startTimeFormat = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static SimpleDateFormat startTimeFormatShort = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss");

    private static final long serialVersionUID = 1L;

    private static final String TITLE = NAME;
    private static final int WIDTH = 700;
    private static final int HEIGHT = 600;

    private Http http;
    private Settings settings;
    private PreferencesDialog preferencesDialog;
    private ScrolledHTMLDialog overviewDialog;

    private String initialPrettyPrintDir;

    private JTextArea textArea;
    private JMenuBar menuBar;

    private WebPageDialog webPageDialog;

    // TODO Make this a preference and implement it. Also for MergeTcxGpx.
    private String initialSaveDir = "C:/Users/evans/Documents/GPSLink/Polar/Access";

    public PolarAccessManager() {
        http = new Http(this);
        uiInit();

        System.out.println("PolarAccessManager started at: " + new Date());

        loadUserPreferences();

        // Not a prference for now
        initialPrettyPrintDir = settings.getInitialTcxGpxSrcDir();

        // Debug WebPage
        // http.setToken(null);

        // Debug Fiddler
        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("https.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "8888");
        // System.setProperty("https.proxyPort", "8888");
        appendLineText(new Date().toString());
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

        // appendLineText("accessCode=" + http.accessCode);
        // appendLineText("token=" + settings.getToken());
        appendLineText("clientUserId=" + settings.getClientUserId());
        appendLineText("polarUserId=" + settings.getPolarUserId());
        appendLineText(
            "exerciseTransactionId=" + settings.getExerciseTransactionId());
        appendLineText(
            "activityTransactionId=" + settings.getActivityTransactionId());
        appendLineText("physicalInfoTransactionId="
            + settings.getPhysicalInfoTransactionId());

        // // DEBUG
        // appendLineText("");
        // appendLineText(
        // "fileNameSubstitution: " + settings.getFileNameSubstitution());
        // testFilename();

        // // TEMPORARY
        // http.polarUserId="9839019";
        // http.token="ef0f9246a9d0216e9d5a4c21c349d391";
        // http.setPreferences();
        // http.exerciseTransactionId = 173373912;

        http.debug = true;
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

        // Preferences
        menuItem = new JMenuItem();
        menuItem.setText("Preferences...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                setPreferences();
            }
        });
        menu.add(menuItem);

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
                User obj = http.registerUser(true);
                if(obj == null) {
                    appendLineText("registerUser failed "
                        + http.getLastResponseCodeString());
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
                User user = http.getUserInformation(false);
                if(user == null) {
                    appendLineText("getUserInformation failed "
                        + http.getLastResponseCodeString());
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
                boolean res = http.deleteUser(false);
                if(res) {
                    appendLineText("deleteUser succeeded");
                } else {
                    appendLineText("deleteUser failed "
                        + http.getLastResponseCodeString());
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
                String data = http.getRateLimits(false);
                if(data == null) {
                    appendLineText("getRateLimits failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Rate Limits:");
                appendLineText(data);
                return;
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Get available data
        menuItem = new JMenuItem();
        menuItem.setText("Get Available Data");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "listNotifications");
                String json = http.listNotifications(false);
                if(json == null) {
                    appendLineText("listNotifications failed "
                        + http.getLastResponseCodeString());
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

        // Exercises
        menu = new JMenu();
        menu.setText("Exercises");
        menuBar.add(menu);

        // Get exercises hash
        menuItem = new JMenuItem();
        menuItem.setText("Get Exercises Hash");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getExercisesHash");
                ExercisesHash obj = http.getExercisesHash(false);
                if(obj == null) {
                    appendLineText("getExercisesHash failed "
                        + http.getLastResponseCodeString());
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
                TransactionLocation obj = http
                    .getExerciseTransactionLocation(false);
                if(obj == null) {
                    appendLineText("getExerciseTranslationLocation failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("New exercise_transaction-id="
                    + settings.getExerciseTransactionId());
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
                Exercises obj = http.getExerciseList(false);
                if(obj == null) {
                    appendLineText("getExerciseList failed "
                        + http.getLastResponseCodeString());
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
                BackgroundWorker worker = new BackgroundWorker(
                    PolarAccessManager.this,
                    PolarAccessManager.BackgroundMethodType.GetExerciseSummaries,
                    PolarAccessManager.this);
                worker.execute();
            }
        });
        menu.add(menuItem);

        // Get TCX, Convert to GPX
        menuItem = new JMenuItem();
        menuItem.setText("Get TCX and Convert to GPX");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getTcxAndConvertGpx");
                BackgroundWorker worker = new BackgroundWorker(
                    PolarAccessManager.this,
                    PolarAccessManager.BackgroundMethodType.GetTcxConvertGpx,
                    PolarAccessManager.this);
                worker.execute();
                return;
            }
        });
        menu.add(menuItem);

        // Get TCX/GPX
        menuItem = new JMenuItem();
        menuItem.setText("!!! Get TCX and GPX");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getTcxGpx");
                BackgroundWorker worker = new BackgroundWorker(
                    PolarAccessManager.this,
                    PolarAccessManager.BackgroundMethodType.GetTcxGpx,
                    PolarAccessManager.this);
                worker.execute();
                return;
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Commit transaction
        menuItem = new JMenuItem();
        menuItem.setText("Commit exercise transaction");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "commitExerciseTransaction");
                boolean res = http.commitExerciseTransaction(false);
                if(res) {
                    appendLineText("commitExerciseTransaction succeeded");
                } else {
                    appendLineText("commitExerciseTransaction failed "
                        + http.getLastResponseCodeString());
                }
                return;
            }
        });
        menu.add(menuItem);

        // Activities
        menu = new JMenu();
        menu.setText("Activities");
        menuBar.add(menu);

        // Get activity transaction id
        menuItem = new JMenuItem();
        menuItem.setText("Get Activity Transaction ID");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getActivityTranslationLocation");
                TransactionLocation obj = http
                    .getActivityTransactionLocation(false);
                if(obj == null) {
                    appendLineText("getActivityTranslationLocation failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("New activity_transaction-id="
                    + settings.getActivityTransactionId());
                appendLineText("Activity Transaction Location:");
                Gson gson = new Gson();
                String json = gson.toJson(obj);
                appendLineText(JsonUtils.prettyFormat(json));
                return;
            }
        });
        menu.add(menuItem);

        // Get activity list
        menuItem = new JMenuItem();
        menuItem.setText("Get Activity List");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getActivityList");
                ActivityLogs obj = http.getActivityList(false);
                if(obj == null) {
                    appendLineText("getActivityList failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Activity List:");
                List<String> activityList = obj.activityLogs;
                if(activityList == null) {
                    appendLineText("Activity List is null");
                    return;
                }
                if(activityList.isEmpty()) {
                    appendLineText("Activity List is empty");
                    return;
                }
                for(String activity : activityList) {
                    appendLineText(activity);
                }
                return;
            }
        });
        menu.add(menuItem);

        // Get activity summaries
        menuItem = new JMenuItem();
        menuItem.setText("Get Activity Summaries");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getActivitySummaries");
                BackgroundWorker worker = new BackgroundWorker(
                    PolarAccessManager.this,
                    PolarAccessManager.BackgroundMethodType.GetActivitySummaries,
                    PolarAccessManager.this);
                worker.execute();
                return;
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Commit transaction
        menuItem = new JMenuItem();
        menuItem.setText("Commit activity transaction");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "commitActivityTransaction");
                boolean res = http.commitActivityTransaction(false);
                if(res) {
                    appendLineText("commitActivityTransaction succeeded");
                } else {
                    appendLineText("commitActivityTransaction failed "
                        + http.getLastResponseCodeString());
                }
                return;
            }
        });
        menu.add(menuItem);

        // Physical Information
        menu = new JMenu();
        menu.setText("Physical Info");
        menuBar.add(menu);

        // Get physical info transaction id
        menuItem = new JMenuItem();
        menuItem.setText("Get Physical Info Transaction ID");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getPhysicalInfoTranslationLocation");
                TransactionLocation obj = http
                    .getPhysicalInfoTransactionLocation(false);
                if(obj == null) {
                    appendLineText("getPhysicalInfoTranslationLocation failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("New physical_info_transaction-id="
                    + settings.getActivityTransactionId());
                appendLineText("Physical Info Transaction Location:");
                Gson gson = new Gson();
                String json = gson.toJson(obj);
                appendLineText(JsonUtils.prettyFormat(json));
                return;
            }
        });
        menu.add(menuItem);

        // Get physical info list
        menuItem = new JMenuItem();
        menuItem.setText("Get Physical Info List");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getPhysicalInfoList");
                PhysicalInformations obj = http.getPhysicalInfoList(false);
                if(obj == null) {
                    appendLineText("getPhysicalInfoList failed "
                        + http.getLastResponseCodeString());
                    return;
                }
                appendLineText("Physical Information List:");
                List<String> activityList = obj.physicalInformations;
                if(activityList == null) {
                    appendLineText("Physical Info List is null");
                    return;
                }
                if(activityList.isEmpty()) {
                    appendLineText("Physical Info List is empty");
                    return;
                }
                for(String physicalInfo : activityList) {
                    appendLineText(physicalInfo);
                }
                return;
            }
        });
        menu.add(menuItem);

        // Get physical information summaries
        menuItem = new JMenuItem();
        menuItem.setText("Get Physical Info Summaries");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "getPhysicalInfoSummaries");
                BackgroundWorker worker = new BackgroundWorker(
                    PolarAccessManager.this,
                    PolarAccessManager.BackgroundMethodType.GetPhysicalInfoSummaries,
                    PolarAccessManager.this);
                worker.execute();
            }
        });
        menu.add(menuItem);

        menu.add(new JSeparator());

        // Commit transaction
        menuItem = new JMenuItem();
        menuItem.setText("Commit physical info transaction");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                appendLineText(LS + "commitPhysicalInfoTransaction");
                boolean res = http.commitPhysicalInfoTransaction(false);
                if(res) {
                    appendLineText("commitPhysicalInfoTransaction succeeded");
                } else {
                    appendLineText("commitPhysicalInfoTransaction failed "
                        + http.getLastResponseCodeString());
                }
                return;
            }
        });
        menu.add(menuItem);

        // TCX/GPX
        menu = new JMenu();
        menu.setText("TCX/GPX");
        menuBar.add(menu);

        // TCX to GPX
        menuItem = new JMenuItem();
        menuItem.setText("Convert TCX to GPX...");
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

        // Rename TCX and GPX
        menuItem = new JMenuItem();
        menuItem.setText("Rename TCX and GPX...");
        menuItem.setToolTipText("Renames TCX/GPX file pairs.");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PolarAccessManager.this
                    .appendLineText(LS + "Rename TCX/GPX file pairs");
                RenameTcxGpx rename = new RenameTcxGpx(PolarAccessManager.this,
                    true);
                rename.processTcxFiles();
            }
        });
        menu.add(menuItem);

        // Pretty Print
        menuItem = new JMenuItem();
        menuItem.setText("Pretty Print...");
        menuItem
            .setToolTipText("Pretty Print an XML file such as TCX and GPX.");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                prettyPrint();
            }
        });
        menu.add(menuItem);

        // Help
        menu = new JMenu();
        menu.setText("Help");
        menuBar.add(menu);

        menuItem = new JMenuItem();
        menuItem.setText("Overview...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                overview();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem();
        menuItem.setText("Show Overview in Browser...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showHelpInBrowser();
            }
        });
        menu.add(menuItem);

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
     * Set viewer fields from the user preferences.
     */
    public void loadUserPreferences() {
        settings = new Settings();
        settings.loadFromPreferences();
    }

    /**
     * Brings up a dialog to set preferences.
     */
    private void setPreferences() {
        if(preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog(this, this);
        }
        // For modal, use this and preferencesDialog.showDialog() instead of
        // preferencesDialog.setVisible(true)
        // preferencesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        preferencesDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        preferencesDialog.setVisible(true);
        // This only returns on Cancel and always returns true. All actions are
        // done from the dialog.
        // dialog.showDialog();
    }

    /**
     * Copies the given settings to settings and resets the viewer.
     * 
     * @param settings
     */
    public void onPreferenceReset(Settings settings) {
        // Copy from the given settings.
        this.settings.copyFrom(settings);
    }

    /**
     * @return The value of settings.
     */
    public Settings getSettings() {
        return settings;
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
                "/resources/PolarAccessManager.32x32.png");

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
                AccessToken at = http.getToken(code, true);
                if(at == null) {
                    appendLineText("Failed to get token");
                    return;
                }
                String token = at.accessToken;
                if(token == null || token.length() == 0) {
                    appendLineText("Invalid token: " + token);
                    return;
                }
                settings.saveToken(token);
                appendLineText("New token: " + settings.getToken());
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

    private void overview() {
        String resource = "/resources/PolarAccessManager.htm";
        URL contentsUrl = ScrolledHTMLDialog.class.getResource(resource);
        if(contentsUrl == null) {
            System.err.println("Couldn't find file: " + resource);
            return;
        }
        if(overviewDialog == null) {
            overviewDialog = new ScrolledHTMLDialog(this, contentsUrl);
            overviewDialog.setTitle("Overview");
            overviewDialog.setSize(new Dimension(500, 500));
            // For modal, use this and dialog.showDialog() instead of
            // dialog.setVisible(true)
            // dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            overviewDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            URL url = PolarAccessManager.class
                .getResource("/resources/PolarAccessManager.32x32.png");
            if(url != null) {
                overviewDialog.setIconImage(new ImageIcon(url).getImage());
            }
        }
        overviewDialog.setVisible(true);
    }

    /**
     * Quits the application
     */
    private void quit() {
        settings.saveToPreferences(true);
        System.exit(0);
    }

    /**
     * Shows the help file in a browser. Reads the necessary files from the
     * resources, assuming they may be in a JAR, to a temporary directory, then
     * brings up the default browser.
     */
    private void showHelpInBrowser() {
        // Make a temp directory
        Path tempPath = null;
        try {
            tempPath = Files.createTempDirectory("PolarAccessManager");
        } catch(IOException ex) {
            // TODO Auto-generated catch block
            Utils.excMsg(
                "Error creating temp directory for displaying PolarAccessManager.htm",
                ex);
            return;
        }
        if(tempPath == null) {
            Utils.errMsg(
                "Error creating temp directory for displaying PolarAccessManager.htm");

        }
        tempPath.toFile().deleteOnExit();

        // Copy the HTML files to the tempDir
        File overviewFile = new File(tempPath.toFile(),
            "PolarAccessManager.htm");
        overviewFile.deleteOnExit();
        try {
            Utils.exportResource(this.getClass(),
                "/resources/PolarAccessManager.htm", overviewFile);
        } catch(Exception ex) {
            Utils.excMsg("Error creating temp file for PolarAccessManager.htm",
                ex);
            return;
        }
        File imageFile = new File(tempPath.toFile(),
            "PolarAccessManager.32x32.png");
        imageFile.deleteOnExit();
        try {
            Utils.exportResource(this.getClass(),
                "/resources/PolarAccessManager.32x32.png", imageFile);
        } catch(Exception ex) {
            Utils.excMsg(
                "Error creating temp file for PolarAccessManager.32x32.png",
                ex);
            return;
        }

        try {
            Desktop.getDesktop().browse(overviewFile.toURI());
        } catch(IOException ex) {
            Utils.excMsg("Error starting browser for PolarAccessManager.htm",
                ex);
            return;
        }
    }

    public void prettyPrint() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter tcxFilter = new FileNameExtensionFilter("TCX",
            "tcx");
        FileNameExtensionFilter gpxFilter = new FileNameExtensionFilter("GPX",
            "gpx");
        chooser.addChoosableFileFilter(tcxFilter);
        chooser.addChoosableFileFilter(gpxFilter);
        chooser.setFileFilter(tcxFilter);
        chooser.setDialogTitle("Select XML File");
        if(initialPrettyPrintDir != null) {
            File file = new File(initialPrettyPrintDir);
            if(file != null && file.exists()) {
                chooser.setCurrentDirectory(file);
            }
        }
        int result = chooser.showOpenDialog(this);
        if(result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        // Save the selected path for next time
        initialPrettyPrintDir = chooser.getSelectedFile().getParentFile()
            .getPath();
        String xml;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(file));
            xml = XmlUtils.convertDocumentToString(doc);
            Utils.scrolledTextMsg(this,
                XmlUtils.toPrettyString(xml, PRETTY_PRINT_INDENT),
                file.getName(), 600, 400, Font.MONOSPACED, Font.BOLD, 12);
        } catch(IOException ex) {
            Utils.excMsg("I/O error during PrettyPrint", ex);
        } catch(ParserConfigurationException ex) {
            Utils.excMsg("Parser error during PrettyPrint", ex);
        } catch(SAXException ex) {
            Utils.excMsg("SAX error during PrettyPrint", ex);
        }
    }

    /**
     * Capitalizes the first letter only in the given string.
     * 
     * @param original
     * @return
     */
    public static String capitalizeFirstLetter(String original) {
        if(original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase()
            + original.substring(1).toLowerCase();
    }

    /**
     * Capitalizes the first letter only in parts of the given string separated
     * by the given separator.
     * 
     * @param original
     * @param separator
     * @return
     */
    public static String capitalizeFirstLetters(String original,
        String separator) {
        String converted = "";
        String[] tokens = original.split(separator);
        boolean first = true;
        for(String token : tokens) {
            token = capitalizeFirstLetter(token);
            if(first) {
                first = false;
                converted += token;
            } else {
                converted += "_" + token;
            }
        }
        return converted;
    }

    /**
     * Generate a filename using the current client-user-id and the supplied
     * startTime, activity string, and extension. The activity should be space
     * or underscore-separated words (usually a location). The extension should
     * start with dot.
     * 
     * @param startIime
     * @param activity
     * @param ext
     * @return
     */
    private String getFileName(String startTime, String activity, String ext) {
        String userName = settings.getClientUserId();
        if(userName == null || userName.isEmpty()) {
            userName = "Polar";
        }
        userName = userName.trim().replaceAll(" ", "_");
        Date date;
        String time;
        if(startTime == null) {
            time = "0000-00-00_00-00-00";
        } else {
            try {
                date = startTimeFormat.parse(startTime);
                time = fileDateFormat.format(date);
            } catch(ParseException ex) {
                try {
                    date = startTimeFormatShort.parse(startTime);
                    time = fileDateFormat.format(date);
                } catch(ParseException ex1) {
                    time = "0000-00-00_00-00-00";
                }
            }
        }
        String activityStr;
        if(activity == null || activity.isEmpty()) {
            activityStr = "Unknown";
        } else {
            activityStr = activity.replaceAll(" ", "_");
        }
        activityStr = capitalizeFirstLetters(activityStr, "_");
        String name = userName + "_" + time + "_" + activityStr + ext;

        // Do substitutions
        Map<String, String> fileNameSubstitutionMap;
        Gson gson = new Gson();
        fileNameSubstitutionMap = gson.fromJson(
            settings.getFileNameSubstitution(),
            new TypeToken<Map<String, String>>() {
            }.getType());
        if(fileNameSubstitutionMap != null
            && !fileNameSubstitutionMap.isEmpty()) {
            for(Map.Entry<String, String> item : fileNameSubstitutionMap
                .entrySet()) {
                name = name.replaceAll(item.getKey(), item.getValue());
            }
        }
        return name;
    }

    /**
     * Method used for debugging naming algorithms.
     */
    public void testFilename() {
        String startTime;
        String activity;
        startTime = "2019-02-06T13:10:23.000";
        activity = "OTHER_INDOOR";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-09T14:38:16.000";
        activity = "WALKING";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-11T17:28:02.000";
        activity = "OTHER_INDOOR";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-13T15:24:26.000";
        activity = "OTHER_INDOOR";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-15T14:01:46.000";
        activity = "OTHER_INDOOR";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-16T18:24:15.000";
        activity = "CYCLING";
        appendLineText(getFileName(startTime, activity, ".test"));
        startTime = "2019-02-16T18:27:45.000";
        activity = "OTHER_OUTDOOR";
        appendLineText(getFileName(startTime, activity, ".test"));
    }

    private Result renameTcxGpx(File tcxFile, File gpxFile) {
        RenameMode renameMode = settings.getTcxGpxDownloadRenameMode();
        if(renameMode == RenameMode.NO) {
            return Result.FAIL;
        }
        if(tcxFile == null) {
            appendLineText("renameTcxGpx: tcxFile is null");
            return Result.FAIL;
        }
        if(!tcxFile.exists()) {
            appendLineText("renameTcxGpx: tcxFile does not exist");
            return Result.FAIL;
        }
        // Get tcx
        TrainingCenterDatabaseT tcx = null;
        try {
            tcx = TCXParser.parse(tcxFile);
        } catch(JAXBException ex) {
            appendLineText(
                "Failed to parse " + tcxFile.getPath() + LS + ex.getMessage());
            return Result.FAIL;
        }
        // Get gpx if gpxFile is not null
        GpxType gpx = null;
        if(gpxFile != null) {
            if(gpxFile == null) {
                appendLineText("renameTcxGpx: gpxFile is null");
                return Result.FAIL;
            }
            if(!gpxFile.exists()) {
                appendLineText("renameTcxGpx: gpxFile does not exist");
                return Result.FAIL;
            }
            try {
                gpx = GPXParser.parse(gpxFile);
            } catch(JAXBException ex) {
                appendLineText("Failed to parse " + gpxFile.getPath() + LS
                    + ex.getMessage());
                return Result.FAIL;
            }
        }
        if(renameMode == RenameMode.PROMPT) {
            FileRenameDialog dlg = new FileRenameDialog(this, this, tcxFile,
                tcx, gpxFile, gpx);
            dlg.setModal(true);
            dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Center on screen
            final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
            int x = (screenSize.width / 2) - (dlg.getSize().width / 2);
            int y = (screenSize.height / 2) - (dlg.getSize().height / 2);
            dlg.setLocation(x, y);

            FileRenameDialog.Result result = dlg.showDialog();
            return result;
        }
        if(renameMode == RenameMode.AUTO) {
            FileRenameDialog dlg = new FileRenameDialog(this, this, tcxFile,
                tcx, gpxFile, gpx);
            FileRenameDialog.Result result = dlg.renameSilently();
            return result;
        }
        return Result.FAIL;
    }

    /**
     * Use to start a browser to get the user authorization. The response will
     * come through a PropertyChange event.
     */
    private void getAccess() {
        appendLineText(LS + "getAccess");
        String accessUrl = http.getAuthorizationURL();
        if(accessUrl == null) {
            appendLineText("No access code");
            return;
        }
        try {
            webPageDialog = new WebPageDialog(this, http.getAuthorizationURL());
            webPageDialog.setVisible(true);
        } catch(Throwable t) {
            Utils.excMsg("Error running getAccess", t);
        }
    }

    private void getExerciseSummaries() {
        // Get a new transaction-id if available
        http.getExerciseTransactionLocation(false);
        appendLineText("getExerciseTranslationLocation() returned "
            + http.getLastResponseCodeString());
        // Get the exerciseList
        Exercises exercises = http.getExerciseList(false);
        if(!http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getExerciseList() returned " + http.lastResponseMessage);
        }
        switch(http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no exercises");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(http.lastResponseMessage);
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
            exercise = http.getExerciseSummary(exerciseString, false);
            if(!http.lastResponseMessage.isEmpty()) {
                appendLineText("getExerciseSummary() returned "
                    + http.getLastResponseCodeString());
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
                appendLineText(" clubName=" + exercise.clubName);
                appendLineText(" clubId=" + exercise.clubId);
            }
        }
    }

    private void getActivitySummaries() {
        // Get a new transaction-id if available
        http.getActivityTransactionLocation(false);
        appendLineText("getActivityTransactionLocation() returned "
            + http.getLastResponseCodeString());
        // Get the physicalInfoList
        ActivityLogs activities = http.getActivityList(false);
        if(!http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getActivityList() returned " + http.lastResponseMessage);
        }
        switch(http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no activities");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(http.lastResponseMessage);
            return;
        }
        if(activities == null) {
            appendLineText("activities is null");
            return;
        }
        // Loop over activities
        List<String> activitiesList = activities.activityLogs;
        if(activitiesList == null) {
            appendLineText("activitiesList is null");
            return;
        }
        if(activitiesList.isEmpty()) {
            appendLineText("activitiesList is empty");
            return;
        }
        int nActivities = 0;
        Activity activity;
        for(String activityString : activitiesList) {
            nActivities++;
            activity = http.getActivitySummary(activityString, false);
            if(!http.lastResponseMessage.isEmpty()) {
                appendLineText("getActivitySummary() returned "
                    + http.getLastResponseCodeString());
            }
            if(activity != null) {
                appendLineText("Activity " + nActivities);
                appendLineText("  id=" + activity.id);
                appendLineText("  polar-user=" + activity.polarUser);
                appendLineText("  created=" + activity.created);
                appendLineText("  date=" + activity.date);
                appendLineText("  duration=" + activity.duration);
                appendLineText("  calories=" + activity.calories);
                appendLineText("  active-calories=" + activity.activeCalories);
                appendLineText("  active-steps=" + activity.activeSteps);
            }
        }
    }

    private void getPhysicalInfoSummaries() {
        // Get a new transaction-id if available
        http.getPhysicalInfoTransactionLocation(false);
        appendLineText("getPhysicalInfoTransactionLocation() returned "
            + http.getLastResponseCodeString());
        // Get the physicalInfoList
        PhysicalInformations physicalInformations = http
            .getPhysicalInfoList(false);
        if(!http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getPhysicalInfoList() returned " + http.lastResponseMessage);
        }
        switch(http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no physical informations");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(http.lastResponseMessage);
            return;
        }
        if(physicalInformations == null) {
            appendLineText("physicalInformations is null");
            return;
        }
        // Loop over physical informations
        List<String> physicalInformationsList = physicalInformations.physicalInformations;
        if(physicalInformationsList == null) {
            appendLineText("physicalInformationsList is null");
            return;
        }
        if(physicalInformationsList.isEmpty()) {
            appendLineText("physicalInformationsList is empty");
            return;
        }
        int nActivity = 0;
        PhysicalInformation physicalInformation;
        for(String physicalInformationString : physicalInformationsList) {
            nActivity++;
            physicalInformation = http
                .getPhysicalInfoSummary(physicalInformationString, false);
            if(!http.lastResponseMessage.isEmpty()) {
                appendLineText("getPhysicalInfoSummary() returned "
                    + http.getLastResponseCodeString());
            }
            if(physicalInformation != null) {
                appendLineText("Physical Information " + nActivity);
                appendLineText(" id=" + physicalInformation.id);
                appendLineText("  polar-user=" + physicalInformation.polarUser);
                appendLineText("  created=" + physicalInformation.created);
                appendLineText("  weight=" + physicalInformation.weight);
                appendLineText("  height=" + physicalInformation.height);
                appendLineText("  maximum-heart-rate="
                    + physicalInformation.maximumHeartRate);
                appendLineText("  resting-heart-rate="
                    + physicalInformation.restingHeartRate);
                appendLineText("  aerobic-threshold="
                    + physicalInformation.aerobicThreshold);
                appendLineText("  anaerobic-threshold="
                    + physicalInformation.anaerobicThreshold);
                appendLineText("  vo2-max=" + physicalInformation.vo2Max);
                appendLineText(
                    "  weight-source=" + physicalInformation.weightSource);
            }
        }
    }

    private void getTpxConvertGpx() {
        // Get a new transaction-id if available
        http.getExerciseTransactionLocation(false);
        appendLineText("getExerciseTranslationLocation() returned "
            + http.getLastResponseCodeString());
        // Get the exerciseList
        Exercises exercises = http.getExerciseList(false);
        if(!http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getExerciseList() returned " + http.lastResponseMessage);
        }
        switch(http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no exercises");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(http.lastResponseMessage);
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
        int nEmpty = 0, nFailed = 0, nWritten = 0, nAborted = 0, nSkipped = 0;
        int nRenameSuccessful = 0, nRenameAborted = 0, nRenameFailed = 0;
        Result res;
        String gpxName, tcxName;
        Exercise exercise;
        String startTime;
        String detailedSportInfo;
        File tcxRenameFile, gpxRenameFile;
        for(String exerciseString : exerciseList) {
            nExercise++;
            appendLineText("Exercise " + nExercise);
            exercise = http.getExerciseSummary(exerciseString, false);
            if(!http.lastResponseMessage.isEmpty()) {
                appendLineText("getExerciseSummary() returned "
                    + http.getLastResponseCodeString());
            }
            if(exercise == null) {
                startTime = null;
                detailedSportInfo = null;
            } else {
                startTime = exercise.startTime;
                detailedSportInfo = exercise.detailedSportInfo;
            }

            // TCX
            tcxRenameFile = gpxRenameFile = null;
            tcxName = getFileName(startTime, detailedSportInfo, ".tcx");
            File tcxFile = new File(initialSaveDir, tcxName);
            if(settings.getTcxGpxDownloadSaveMode() == SaveMode.SKIP
                && tcxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + tcxName);
            } else {
                appendLineText(tcxName);
                String url = exerciseString + "/tcx";
                String tcxResponse = http.getTcx(url, false);
                if(!http.lastResponseMessage.isEmpty()) {
                    appendLineText(
                        "getTcx() returned " + http.lastResponseMessage);
                }
                if(tcxResponse == null) {
                    nFailed++;
                    appendLineText("Failed to get TCX for " + nExercise);
                } else {
                    int len = tcxResponse.length();
                    if(len > 80) {
                        len = 80;
                    }
                    // appendLineText(
                    // tcx.substring(0, len) + "... [" + tcx.length() + "]");
                    appendLineText("Length is " + tcxResponse.length());
                    if(len == 0) {
                        nEmpty++;
                    } else {
                        boolean skip = false;
                        if(settings
                            .getTcxGpxDownloadSaveMode() == SaveMode.PROMPT
                            && tcxFile.exists()) {
                            int selection = JOptionPane.showConfirmDialog(null,
                                "File exists:" + LS + tcxFile.getPath() + LS
                                    + "OK to overwrite?",
                                "File Exists", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            if(selection != JOptionPane.OK_OPTION) {
                                skip = true;
                                nAborted++;
                                appendLineText("Aborted " + tcxFile.getPath());
                            }
                        }
                        if(!skip) {
                            try (PrintWriter out = new PrintWriter(tcxFile)) {
                                nWritten++;
                                out.println(tcxResponse);
                                appendLineText("Wrote " + tcxFile.getPath());
                                // Save for possible rename
                                tcxRenameFile = tcxFile;
                            } catch(FileNotFoundException ex) {
                                appendLineText("Error writing "
                                    + tcxFile.getPath() + LS + ex.getMessage());
                            }
                        }
                    }
                }
            }

            // GPX
            gpxName = getFileName(startTime, detailedSportInfo, ".gpx");
            File gpxFile = new File(initialSaveDir, gpxName);
            if(settings.getTcxGpxDownloadSaveMode() == SaveMode.SKIP
                && gpxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + gpxName);
            } else {
                appendLineText(gpxName);
                boolean skip = false;
                if(settings.getTcxGpxDownloadSaveMode() == SaveMode.PROMPT
                    && gpxFile.exists()) {
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
                    MergeTcxAndGpxToGpx merge = new MergeTcxAndGpxToGpx(
                        PolarAccessManager.this, true);
                    // This will do appendLineText
                    boolean status = merge.convertTcxToGpx(tcxFile, gpxFile);
                    if(status) {
                        nWritten++;
                    }
                    // Save for possible rename
                    gpxRenameFile = gpxFile;
                }
            }

            // Do rename
            if(settings.getTcxGpxDownloadRenameMode() != RenameMode.NO
                && tcxRenameFile != null) {
                res = renameTcxGpx(tcxRenameFile, gpxRenameFile);
                if(res == Result.OK) {
                    nRenameSuccessful++;
                } else if(res == Result.ABORT
                    || res == Result.ABORT_REMAINING) {
                    nRenameAborted++;
                } else {
                    nRenameFailed++;
                }
            }
        }
        appendLineText("Summary: " + nWritten + " Written, " + nFailed
            + " Failed, " + nEmpty + " Empty, " + nSkipped + " Skipped, "
            + nAborted + " Aborted");
        appendLineText("Rename Summary: " + nRenameSuccessful + " Successful, "
            + nRenameAborted + " Aborted, " + nRenameFailed
            + " Failed (Refers to TCX/GPX pairs)");
    }

    private void getTpxGpx() {
        // Get a new transaction-id if available
        http.getExerciseTransactionLocation(false);
        appendLineText("getExerciseTranslationLocation() returned "
            + http.getLastResponseCodeString());
        // Get the exerciseList
        Exercises exercises = http.getExerciseList(false);
        if(!http.lastResponseMessage.isEmpty()) {
            appendLineText(
                "getExerciseList() returned " + http.lastResponseMessage);
        }
        switch(http.lastResponseCode) {
        case HttpURLConnection.HTTP_OK:
            break;
        case HttpURLConnection.HTTP_NO_CONTENT:
            appendLineText("There are no exercises");
            return;
        case HttpURLConnection.HTTP_NOT_FOUND:
            appendLineText("May need a new transaction-id, wait a few minutes");
            return;
        default:
            appendLineText(http.lastResponseMessage);
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
        int nEmpty = 0, nFailed = 0, nWritten = 0, nAborted = 0, nSkipped = 0;
        int nRenameSuccessful = 0, nRenameAborted = 0, nRenameFailed = 0;
        Result res;
        String gpxName, tcxName;
        Exercise exercise;
        String startTime;
        String detailedSportInfo;
        File tcxRenameFile, gpxRenameFile;
        for(String exerciseString : exerciseList) {
            nExercise++;
            appendLineText("Exercise " + nExercise);
            exercise = http.getExerciseSummary(exerciseString, false);
            if(!http.lastResponseMessage.isEmpty()) {
                appendLineText("getExerciseSummary() returned "
                    + http.getLastResponseCodeString());
            }
            if(exercise == null) {
                startTime = null;
                detailedSportInfo = null;
            } else {
                startTime = exercise.startTime;
                detailedSportInfo = exercise.detailedSportInfo;
            }

            // TCX
            tcxRenameFile = gpxRenameFile = null;
            tcxName = getFileName(startTime, detailedSportInfo, ".tcx");
            File tcxFile = new File(initialSaveDir, tcxName);
            if(settings.getTcxGpxDownloadSaveMode() == SaveMode.SKIP
                && tcxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + tcxName);
            } else {
                appendLineText(tcxName);
                String url = exerciseString + "/tcx";
                String tcxResponse = http.getTcx(url, false);
                if(!http.lastResponseMessage.isEmpty()) {
                    appendLineText(
                        "getTcx() returned " + http.lastResponseMessage);
                }
                if(tcxResponse == null) {
                    nFailed++;
                    appendLineText("Failed to get TCX for " + nExercise);
                } else {
                    int len = tcxResponse.length();
                    if(len > 80) {
                        len = 80;
                    }
                    // appendLineText(
                    // tcx.substring(0, len) + "... [" + tcx.length() + "]");
                    appendLineText("Length is " + tcxResponse.length());
                    if(len == 0) {
                        nEmpty++;
                    } else {
                        boolean skip = false;
                        if(settings
                            .getTcxGpxDownloadSaveMode() == SaveMode.PROMPT
                            && tcxFile.exists()) {
                            int selection = JOptionPane.showConfirmDialog(null,
                                "File exists:" + LS + tcxFile.getPath() + LS
                                    + "OK to overwrite?",
                                "File Exists", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            if(selection != JOptionPane.OK_OPTION) {
                                skip = true;
                                nAborted++;
                                appendLineText("Aborted " + tcxFile.getPath());
                            }
                        }
                        if(!skip) {
                            try (PrintWriter out = new PrintWriter(tcxFile)) {
                                nWritten++;
                                out.println(tcxResponse);
                                appendLineText("Wrote " + tcxFile.getPath());
                                // Save for possible rename
                                tcxRenameFile = tcxFile;
                            } catch(FileNotFoundException ex) {
                                appendLineText("Error writing "
                                    + tcxFile.getPath() + LS + ex.getMessage());
                            }
                        }
                    }
                }
            }

            // GPX
            gpxName = getFileName(startTime, detailedSportInfo, ".gpx");
            File gpxFile = new File(initialSaveDir, gpxName);
            if(settings.getTcxGpxDownloadSaveMode() == SaveMode.SKIP
                && gpxFile.exists()) {
                nSkipped++;
                appendLineText("Skipping " + gpxName);
            } else {
                appendLineText(gpxName);
                String url = exerciseString + "/gpx";
                String gpxResponse = http.getGpx(url, false);
                if(!http.lastResponseMessage.isEmpty()) {
                    appendLineText(
                        "getGpx() returned " + http.lastResponseMessage);
                }
                if(gpxResponse == null) {
                    nFailed++;
                    appendLineText("Failed to get GPX for " + nExercise);
                } else {
                    int len = gpxResponse.length();
                    if(len > 80) {
                        len = 80;
                    }
                    // appendLineText(
                    // gpx.substring(0, len) + "... [" + gpx.length() + "]");
                    appendLineText("Length is " + gpxResponse.length());
                    if(len == 0) {
                        nEmpty++;
                    } else {
                        boolean skip = false;
                        if(settings
                            .getTcxGpxDownloadSaveMode() == SaveMode.PROMPT
                            && gpxFile.exists()) {
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
                                out.println(gpxResponse);
                                appendLineText("Wrote " + gpxFile.getPath());
                                // Save for possible rename
                                gpxRenameFile = gpxFile;
                            } catch(FileNotFoundException ex) {
                                appendLineText("Error writing "
                                    + gpxFile.getPath() + LS + ex.getMessage());
                            }
                        }
                    }
                }
            }
            // Do rename
            if(settings.getTcxGpxDownloadRenameMode() != RenameMode.NO
                && tcxRenameFile != null) {
                res = renameTcxGpx(tcxRenameFile, gpxRenameFile);
                if(res == Result.OK) {
                    nRenameSuccessful++;
                } else if(res == Result.ABORT
                    || res == Result.ABORT_REMAINING) {
                    nRenameAborted++;
                } else {
                    nRenameFailed++;
                }
            }
        }
        appendLineText("Summary: " + nWritten + " Written, " + nFailed
            + " Failed, " + nEmpty + " Empty, " + nSkipped + " Skipped, "
            + nAborted + " Aborted");
        appendLineText("Rename Summary: " + nRenameSuccessful + " Successful, "
            + nRenameAborted + " Aborted, " + nRenameFailed
            + " Failed (Refers to TCX/GPX pairs)");
    }

    @Override
    public void work(BackgroundMethodType type) {
        switch(type) {
        case GetTcxConvertGpx:
            getTpxConvertGpx();
            break;
        case GetTcxGpx:
            getTpxGpx();
            break;
        case GetExerciseSummaries:
            getExerciseSummaries();
            break;
        case GetActivitySummaries:
            getActivitySummaries();
            break;
        case GetPhysicalInfoSummaries:
            getPhysicalInfoSummaries();
            break;
        default:
            Utils.errMsg("Invalid type (" + type.name() + ") for work method");
        }
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

    /**
     * BackgroundWorker is a class to run a type in the background and display a
     * wait cursor.
     * 
     * @author Kenneth Evans, Jr.
     */
    private class BackgroundWorker extends SwingWorker<Void, Void>
    {
        private final JFrame frame;
        private BackgroundMethodType type;
        private IWorkerMethod workMethod;

        /**
         * BackgroundWorker constructor. Designed to call an arbitrary type with
         * no parameters in PolarAccessmanager and run it in the background. The
         * particular type to run is implemented via a switch statement in the
         * work(BackgroundMethodType type). This allows using the same mechanism
         * for various long running type calls.
         * 
         * @param frame The JFrame, typically {PolarAccessManager.this.
         * @param type The BackgroundMethodType to use in the workMethod.
         * @param workMethod The IWorkerMethod interface implementing the work
         *            type, typically {PolarAccessManager.this.
         */
        public BackgroundWorker(JFrame frame, BackgroundMethodType method,
            IWorkerMethod workMethod) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.frame = frame;
            this.type = method;
            this.workMethod = workMethod;
        }

        @Override
        public Void doInBackground() throws IOException {
            workMethod.work(type);
            return null;
        }

        public void done() {
            try {
                get();
            } catch(ExecutionException ex) {
                Utils.excMsg("Execution error for " + type.name(), ex);
            } catch(InterruptedException ex) {
                Utils.excMsg("Interrupted " + type.name(), ex);
            } finally {
                frame.setCursor(Cursor.getDefaultCursor());
            }
        }

    }

}
