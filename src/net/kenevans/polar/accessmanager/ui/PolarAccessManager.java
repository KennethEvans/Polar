package net.kenevans.polar.accessmanager.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import net.kenevans.polar.accessmanager.classes.User;
import net.kenevans.polar.utils.AboutBoxPanel;
import net.kenevans.polar.utils.ImageUtils;
import net.kenevans.polar.utils.JsonUtils;
import net.kenevans.polar.utils.Utils;

public class PolarAccessManager extends JFrame
    implements IConstants, PropertyChangeListener
{
    public static final String LS = System.getProperty("line.separator");
    private static final String NAME = "Polar Access Manager";
    private static final String VERSION = "1.0.0";
    private static final String HELP_TITLE = NAME + " " + VERSION;
    private static final String AUTHOR = "Written by Kenneth Evans, Jr.";
    private static final String COPYRIGHT = "Copyright (c) 2012-2019 Kenneth Evans";
    private static final String COMPANY = "kenevans.net";

    public static final boolean USE_START_FILE_NAME = false;
    private static final long serialVersionUID = 1L;
    private static final String TITLE = NAME;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private JTextArea textArea;
    private JMenuBar menuBar;

    private WebPageDialog webPageDialog;

    public PolarAccessManager() {
        uiInit();

        Manager.getPreferences();

        // Debug WebPage
        // Manager.setToken(null);

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

        appendLineText("access_code=" + Manager.access_code);
        appendLineText("token=" + Manager.token);
        appendLineText("client_user_id=" + Manager.client_user_id);
        appendLineText("polar_user_id=" + Manager.polar_user_id);

        // TEMPORARY
        // Manager.polar_user_id="9839019";
        // Manager.token="ef0f9246a9d0216e9d5a4c21c349d391";
        // Manager.setPreferences();
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
                PolarAccessManager.this.appendLineText(LS + "Set access code");
                String res = JOptionPane.showInputDialog(
                    "Enter new access code", Manager.access_code);
                if(res != null) {
                    Manager.access_code = res;
                    Manager.setPreferences();
                    PolarAccessManager.this
                        .appendLineText("access_code=" + Manager.access_code);
                } else {
                    PolarAccessManager.this.appendLineText("Aborted");
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
                    "Enter new client-user-id", Manager.client_user_id);
                if(res != null) {
                    Manager.client_user_id = res;
                    Manager.setPreferences();
                    PolarAccessManager.this.appendLineText(
                        "polar_user_id=" + Manager.client_user_id);
                } else {
                    PolarAccessManager.this.appendLineText("Aborted");
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
                PolarAccessManager.this.appendLineText(LS + "registerUser");
                User user = Manager.registerUser();
                if(user == null) {
                    appendLineText("registerUser failed");
                    return;
                }
                appendLineText("User:");
                Gson gson = new Gson();
                String json = gson.toJson(user);
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
                User user = Manager.getUserInformation();
                if(user == null) {
                    appendLineText("getUserInformation failed");
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
                PolarAccessManager.this.appendLineText(LS + "deleteUser");
                boolean res = Manager.deleteUser();
                if(res) {
                    appendLineText("deleteUser succeeded");
                } else {
                    appendLineText("deleteUser failed");
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
                PolarAccessManager.this.appendLineText(LS + "getRateLimits");
                String data = Manager.getRateLimits();
                if(data == null) {
                    appendLineText("getRateLimits failed");
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
                String data = Manager.listNotifications();
                if(data == null) {
                    appendLineText("listNotifications failed");
                    return;
                }
                appendLineText("Available Data:");
                // Gson gson = new Gson();
                // String json = gson.toJson(user);
                // appendLineText(JsonUtils.prettyFormat(json));
                appendLineText(data);
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
                AccessToken at = Manager.getToken(code);
                if(at == null) {
                    appendLineText("Failed to get token");
                    return;
                }
                String token = at.accessToken;
                if(token == null || token.length() == 0) {
                    appendLineText("Invalid token: " + token);
                    return;
                }
                Manager.token = token;
                Manager.setPreferences();
                appendLineText("New token: " + Manager.token);
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
        Manager.setPreferences();
        System.exit(0);
    }

    private void getAccess() {
        appendLineText("getAccess");
        String accessUrl = Manager.getAuthorizationURL();
        if(accessUrl == null) {
            appendLineText("No access code");
            return;
        }
        webPageDialog = new WebPageDialog(this, Manager.getAuthorizationURL());
        webPageDialog.setVisible(true);
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
