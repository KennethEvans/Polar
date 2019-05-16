package net.kenevans.polar.accessmanager.preferences;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.kenevans.polar.accessmanager.ui.IConstants;
import net.kenevans.polar.accessmanager.ui.PolarAccessManager;
import net.kenevans.polar.utils.Utils;

/**
 * PreferencesDialog is a dialog to set the Preferences for PolarAccessManager. It only
 * returns after Cancel. It can save the values to the preference store or set
 * them in the manager. In either case it remains visible.
 * 
 * @author Kenneth Evans, Jr.
 */
/**
 * PreferencesDialog
 * 
 * @author Kenneth Evans, Jr.
 */
public class PreferencesDialog extends JDialog implements IConstants
{
    private static final long serialVersionUID = 1L;
    private PolarAccessManager manager;
    /**
     * The return value. It is always true.
     */
    private boolean ok = true;
    private Map<String, String> fileNameSubstitutionMap;

    JTextField accessCodeText;
    JTextField tokenText;
    JTextField clientUserIdText;
    JTextField polarUserIdText;
    JTextField exerciseTransactionIdText;
    JTextField activityTransactionIdText;
    JTextField physicalInfoTransactionIdText;

    JTextField initialTcxGpxSrcDirText;
    JTextField initialTcxGpxDestDirText;
    JTextField tcxGpxDownloadDirText;
    JTextField tcxGpxRenameDestDirText;
    JComboBox<SaveMode> tcxGpxDownloadSaveModeCombo;
    JComboBox<RenameMode> tcxGpxDownloadRenameModeCombo;

    JList<String> fileNameSubstitutionList;
    DefaultListModel<String> fileNameSubstitutionModel;

    /**
     * Constructor
     */
    public PreferencesDialog(Component parent, PolarAccessManager viewer) {
        super();
        this.manager = viewer;
        if(viewer == null) {
            Utils.errMsg("Viewer is null");
            return;
        }
        URL url = PolarAccessManager.class
            .getResource("/resources/PolarAccessManager.32x32.png");

        if(url != null) {
            setIconImage(new ImageIcon(url).getImage());
        }
        init();
        Settings settings = new Settings();
        settings.loadFromPreferences();
        setValues(settings);
        // Locate it on the screen
        this.setLocationRelativeTo(parent);
    }

    /**
     * This method initializes this dialog
     * 
     * @return void
     */
    private void init() {
        JLabel label;
        JButton button;
        JPanel panel;
        this.setTitle("Preferences");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints gbcDefault = new GridBagConstraints();
        gbcDefault.insets = new Insets(2, 2, 2, 2);
        gbcDefault.anchor = GridBagConstraints.WEST;
        gbcDefault.fill = GridBagConstraints.NONE;
        GridBagConstraints gbc = null;
        int gridy = -1;
        int gridPanel = -1;

        // File Group //////////////////////////////////////////////////////
        JPanel fileGroup = new JPanel();
        fileGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Directories"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        fileGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(fileGroup, gbc);

        gridPanel = -1;

        // tcxGpxDownloadDirText
        gridPanel++;
        label = new JLabel("TCX/GPX Download:");
        label.setToolTipText(
            "The directory where TCX and GPX files downloaded from Polar Access are saved");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        // File JPanel holds the filename and browse button
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(panel, gbc);

        tcxGpxDownloadDirText = new JTextField(30);
        tcxGpxDownloadDirText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(tcxGpxDownloadDirText, gbc);

        button = new JButton();
        button.setText("Browse");
        button.setToolTipText("Choose the directory.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                if(tcxGpxDownloadDirText == null) {
                    return;
                }
                String initialDirName = tcxGpxDownloadDirText.getText();
                String dirName = browse(initialDirName);
                tcxGpxDownloadDirText.setText(dirName);
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        panel.add(button);

        // Download save mode
        gridPanel++;
        label = new JLabel("    Download Save Mode:");
        label.setToolTipText(
            "The mode for saving files downloaded from Polar Access.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        tcxGpxDownloadSaveModeCombo = new JComboBox<SaveMode>(
            SaveMode.values());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.NORTH;
        gbc.weightx = 100;
        fileGroup.add(tcxGpxDownloadSaveModeCombo, gbc);

        // Download rename mode
        gridPanel++;
        label = new JLabel("    Download Rename Mode:");
        label.setToolTipText(
            "The mode for renaming files downloaded from Polar Access.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        tcxGpxDownloadRenameModeCombo = new JComboBox<RenameMode>(
            RenameMode.values());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.NORTH;
        gbc.weightx = 100;
        fileGroup.add(tcxGpxDownloadRenameModeCombo, gbc);

        // tcxGpxRenameDestText
        gridPanel++;
        label = new JLabel("TCX/GPX Rename Dest:");
        label.setToolTipText(
            "The directory where renamed TCX and GPX files are saved");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        // File JPanel holds the filename and browse button
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(panel, gbc);

        tcxGpxRenameDestDirText = new JTextField(30);
        tcxGpxRenameDestDirText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(tcxGpxRenameDestDirText, gbc);

        button = new JButton();
        button.setText("Browse");
        button.setToolTipText("Choose the directory.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                if(tcxGpxRenameDestDirText == null) {
                    return;
                }
                String initialDirName = tcxGpxRenameDestDirText.getText();
                String dirName = browse(initialDirName);
                tcxGpxRenameDestDirText.setText(dirName);
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        panel.add(button);

        // initialTcxGpxSrcDirText
        gridPanel++;
        label = new JLabel("TCX/GPX Convert Src:");
        label.setToolTipText(
            "The directory where TCX files for merging and conversion are found");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        // File JPanel holds the filename and browse button
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(panel, gbc);

        initialTcxGpxSrcDirText = new JTextField(30);
        initialTcxGpxSrcDirText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(initialTcxGpxSrcDirText, gbc);

        button = new JButton();
        button.setText("Browse");
        button.setToolTipText("Choose the directory.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                if(initialTcxGpxSrcDirText == null) {
                    return;
                }
                String initialDirName = initialTcxGpxSrcDirText.getText();
                String dirName = browse(initialDirName);
                initialTcxGpxSrcDirText.setText(dirName);
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        panel.add(button);

        // initialTcxGpxSrcDirText
        gridPanel++;
        label = new JLabel("TCX/GPX Convert Dest:");
        label.setToolTipText(
            "The directory where TCX files for merging and conversion are saved");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        // File JPanel holds the filename and browse button
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(panel, gbc);

        initialTcxGpxDestDirText = new JTextField(40);
        initialTcxGpxDestDirText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(initialTcxGpxDestDirText, gbc);

        button = new JButton();
        button.setText("Browse");
        button.setToolTipText("Choose the directory.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                if(initialTcxGpxDestDirText == null) {
                    return;
                }
                String initialDirName = initialTcxGpxDestDirText.getText();
                String dirName = browse(initialDirName);
                initialTcxGpxDestDirText.setText(dirName);
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        panel.add(button);

        // FileNameSubstitution
        gridPanel++;
        label = new JLabel("Filename Substution:");
        label.setToolTipText("Substitutions used in generating filenames.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        fileGroup.add(label, gbc);

        // Create a JPanel for the rest
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(panel, gbc);

        fileNameSubstitutionList = new JList<String>();
        fileNameSubstitutionList.setToolTipText(label.getText());
        fileNameSubstitutionList.setVisibleRowCount(3);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.weightx = 100;
        JScrollPane scrollPane = new JScrollPane(fileNameSubstitutionList);
        panel.add(scrollPane, gbc);

        // Add
        button = new JButton("Add");
        button.setToolTipText("Add a new original:substitution pair.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                String[] vals = promptForSubstitutionValues(null);
                if(vals == null || vals[0] == null || vals[0].isEmpty()) {
                    return;
                }
                fileNameSubstitutionMap.put(vals[0], vals[1]);
                resetFileNameSubstitutionModel();
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.weightx = 100;
        panel.add(button, gbc);

        // Remove
        button = new JButton("Remove");
        button.setToolTipText("Remove the selected substitution.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                int sel = fileNameSubstitutionList.getSelectedIndex();
                if(sel < 0) {
                    Utils.errMsg("Nothing selected");
                    return;
                }
                String val = fileNameSubstitutionList.getSelectedValue();
                if(val == null || val.isEmpty()) {
                    Utils.errMsg("Cannot determine selected value");
                    return;
                }
                String[] tokens = val.split(":");
                if(tokens.length != 2) {
                    Utils.errMsg(
                        "Cannot determine original and substitution values");
                    return;
                }
                String key = tokens[0];
                fileNameSubstitutionMap.remove(key);
                resetFileNameSubstitutionModel();
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        gbc.weightx = 100;
        panel.add(button, gbc);

        // Edit
        button = new JButton("Edit");
        button.setToolTipText(
            "Edit the selected substitution, substitution part.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                int sel = fileNameSubstitutionList.getSelectedIndex();
                if(sel < 0) {
                    Utils.errMsg("Nothing selected");
                    return;
                }
                String val = fileNameSubstitutionList.getSelectedValue();
                if(val == null || val.isEmpty()) {
                    Utils.errMsg("Cannot determine selected value");
                    return;
                }
                String[] tokens = val.split(":");
                if(tokens.length != 2) {
                    Utils.errMsg(
                        "Cannot determine original and substitution values");
                    return;
                }
                String key = tokens[0];
                String[] vals = promptForSubstitutionValues(key);
                if(vals == null || vals[0] == null || vals[0].isEmpty()) {
                    return;
                }
                fileNameSubstitutionMap.put(vals[0], vals[1]);
                resetFileNameSubstitutionModel();
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 3;
        gbc.weightx = 100;
        panel.add(button, gbc);

        // Parameters /////////////////////////////////////////////////////////
        JPanel parametersGroup = new JPanel();
        parametersGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Parameters"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        parametersGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(parametersGroup, gbc);

        // Access code
        label = new JLabel("access-code:");
        label.setToolTipText(
            "Client access code, base64 encoded from username:password.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        parametersGroup.add(label, gbc);

        accessCodeText = new JTextField(40);
        accessCodeText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        parametersGroup.add(accessCodeText, gbc);

        // Client user id
        label = new JLabel("client-user-id:");
        label.setToolTipText(
            "client-user-id. Arbitrary.  Needed to register user, but apparently not used by Polar Access.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        parametersGroup.add(label, gbc);

        clientUserIdText = new JTextField(30);
        clientUserIdText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        parametersGroup.add(clientUserIdText, gbc);

        // Non-Configurable ///////////////////////////////////////////////////
        JPanel nonConfigurationGroup = new JPanel();
        nonConfigurationGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory
                .createTitledBorder("Non-Configurable (Should not be changed)"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        nonConfigurationGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(nonConfigurationGroup, gbc);

        // Polar user id
        label = new JLabel("polar-user-id:");
        label.setToolTipText(
            "polar-user-id.  Returned from Polar Access on register user.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        nonConfigurationGroup.add(label, gbc);

        polarUserIdText = new JTextField(30);
        polarUserIdText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        nonConfigurationGroup.add(polarUserIdText, gbc);

        // Token
        label = new JLabel("token:");
        label.setToolTipText(
            "Token for user returned from Polar Access.  It has a long lifetime.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        nonConfigurationGroup.add(label, gbc);

        tokenText = new JTextField(30);
        tokenText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        nonConfigurationGroup.add(tokenText, gbc);

        // Exercise transaction-id
        label = new JLabel("exercise-transaction-id:");
        label.setToolTipText(
            "exercise-transaction-id returned from Polar Access.  It has a 10 minute lifetime.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        nonConfigurationGroup.add(label, gbc);

        exerciseTransactionIdText = new JTextField(30);
        exerciseTransactionIdText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        nonConfigurationGroup.add(exerciseTransactionIdText, gbc);

        // Activity transaction-id
        label = new JLabel("activity-transaction-id:");
        label.setToolTipText(
            "activity-transaction-id returned from Polar Access.  It has a 10 minute lifetime.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        nonConfigurationGroup.add(label, gbc);

        activityTransactionIdText = new JTextField(30);
        activityTransactionIdText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        nonConfigurationGroup.add(activityTransactionIdText, gbc);

        // Physical info transaction-id
        label = new JLabel("physical-information-transaction-id:");
        label.setToolTipText(
            "transaction-id returned from Polar Access.  It has a 10 minute lifetime.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        nonConfigurationGroup.add(label, gbc);

        physicalInfoTransactionIdText = new JTextField(30);
        physicalInfoTransactionIdText.setToolTipText(label.getToolTipText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        nonConfigurationGroup.add(physicalInfoTransactionIdText, gbc);

        // // Dummy Group
        // JPanel dummyGroup = new JPanel();
        // dummyGroup.setBorder(BorderFactory.createCompoundBorder(
        // BorderFactory.createTitledBorder("Dummy"),
        // BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        // gridy++;
        // dummyGroup.setLayout(new GridBagLayout());
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 0;
        // gbc.gridy = gridy;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.weightx = 100;
        // contentPane.add(dummyGroup, gbc);
        //
        // // Dummy
        // label = new JLabel("Dummy:");
        // label.setToolTipText("Dummy.");
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 0;
        // dummyGroup.add(label, gbc);
        //
        // JTextField dummyText = new JTextField(30);
        // dummyText.setToolTipText(label.getText());
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.weightx = 100;
        // dummyGroup.add(dummyText, gbc);

        // Button panel /////////////////////////////////////////////////////
        gridy++;
        JPanel buttonPanel = new JPanel();
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(buttonPanel, gbc);

        button = new JButton();
        button.setText("Use Current");
        button.setToolTipText("Set to the current manager values.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Settings settings = manager.getSettings();
                if(settings == null) {
                    Utils.errMsg("Settings in the manager do not exist");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Use Defaults");
        button.setToolTipText("Set to the PolarAccessManager default values.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Settings settings = new Settings();
                if(settings == null) {
                    Utils.errMsg("Default settings do not exist");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Use Stored");
        button.setToolTipText("Reset to the current stored preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Settings settings = new Settings();
                settings.loadFromPreferences();
                if(settings == null) {
                    Utils.errMsg("Cannot load preferences");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Save");
        button.setToolTipText("Save the changes as preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                save();
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Set Current");
        button.setToolTipText("Set the current values in the manager.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                setToManager();
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Done");
        button.setToolTipText("Close the dialog and do nothing.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                PreferencesDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(button);

        pack();
    }

    /**
     * Prompts to get a new key/value pair for filename substitution.
     * 
     * @return
     */
    private String[] promptForSubstitutionValues(String oldKey) {
        JTextField key = new JTextField();
        if(oldKey != null && !oldKey.isEmpty()) {
            key.setText(oldKey);
            key.setEditable(false);
        }
        JTextField value = new JTextField();
        Object[] message = {"Original:", key, "Substitution:", value};

        int option = JOptionPane.showConfirmDialog(null, message,
            "New Sunstitution", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            return new String[] {key.getText(), value.getText()};
        } else {
            return null;
        }
    }

    /**
     * Convenience method to reset the fileNameSubstitutionModel.
     */
    private void resetFileNameSubstitutionModel() {
        fileNameSubstitutionModel = new DefaultListModel<String>();
        if(fileNameSubstitutionMap != null
            && !fileNameSubstitutionMap.isEmpty()) {
            for(Map.Entry<String, String> item : fileNameSubstitutionMap
                .entrySet()) {
                fileNameSubstitutionModel
                    .addElement(item.getKey() + ":" + item.getValue());
            }
        }
        fileNameSubstitutionList.setModel(fileNameSubstitutionModel);
    }

    /**
     * Brings up a JFileChooser to choose a directory.
     */
    private String browse(String initialDirName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(initialDirName != null) {
            File dir = new File(initialDirName);
            chooser.setCurrentDirectory(dir);
            chooser.setSelectedFile(dir);
        }
        int result = chooser.showOpenDialog(this);
        if(result == javax.swing.JFileChooser.APPROVE_OPTION) {
            // Process the directory
            String dirName = chooser.getSelectedFile().getPath();
            File dir = new File(dirName);
            if(!dir.exists()) {
                Utils.errMsg("Does not exist: " + dirName);
                return null;
            }
            if(!dir.isDirectory()) {
                Utils.errMsg("Not a diretory: " + dirName);
                return null;
            }
            return dirName;
        } else {
            return null;
        }
    }

    /**
     * Set the Controls from the given Settings. Can also be used to initialize
     * the dialog.
     * 
     * @param settings
     */
    public void setValues(Settings settings) {
        if(manager == null) {
            return;
        }
        if(accessCodeText != null) {
            accessCodeText.setText(settings.getAccessCode());
        }
        if(tokenText != null) {
            tokenText.setText(settings.getToken());
        }
        if(clientUserIdText != null) {
            clientUserIdText.setText(settings.getClientUserId());
        }
        if(polarUserIdText != null) {
            polarUserIdText.setText(settings.getPolarUserId());
        }
        if(exerciseTransactionIdText != null) {
            exerciseTransactionIdText
                .setText(Integer.toString(settings.getExerciseTransactionId()));
        }
        if(activityTransactionIdText != null) {
            activityTransactionIdText
                .setText(Integer.toString(settings.getActivityTransactionId()));
        }
        if(physicalInfoTransactionIdText != null) {
            physicalInfoTransactionIdText.setText(
                Integer.toString(settings.getPhysicalInfoTransactionId()));
        }
        if(initialTcxGpxSrcDirText != null) {
            initialTcxGpxSrcDirText.setText(settings.getInitialTcxGpxSrcDir());
        }
        if(initialTcxGpxDestDirText != null) {
            initialTcxGpxDestDirText
                .setText(settings.getInitialTcxGpxDestDir());
        }
        if(tcxGpxDownloadDirText != null) {
            tcxGpxDownloadDirText.setText(settings.getTcxGpxDownloadDir());
        }
        if(tcxGpxDownloadSaveModeCombo != null) {
            tcxGpxDownloadSaveModeCombo
                .setSelectedItem(settings.getTcxGpxDownloadSaveMode());
        }
        if(tcxGpxDownloadRenameModeCombo != null) {
            tcxGpxDownloadRenameModeCombo
                .setSelectedItem(settings.getTcxGpxDownloadRenameMode());
        }
        if(tcxGpxRenameDestDirText != null) {
            tcxGpxRenameDestDirText.setText(settings.getTcxGpxRenameDestDir());
        }
        if(fileNameSubstitutionList != null) {
            Gson gson = new Gson();
            fileNameSubstitutionMap = gson.fromJson(
                settings.getFileNameSubstitution(),
                new TypeToken<Map<String, String>>() {
                }.getType());
            resetFileNameSubstitutionModel();
        }
    }

    /**
     * Sets the current values in the given Settings then checks if they are
     * valid.
     * 
     * @param settings
     * @return True if they are valid, else false.
     */
    public boolean setSettingsFromValues(Settings settings) {
        if(settings == null) {
            Utils.errMsg("Input settings is null");
            return false;
        }
        try {
            settings.setAccessCode(accessCodeText.getText());
            settings.setToken(tokenText.getText());
            settings.setClientUserId(clientUserIdText.getText());
            settings.setPolarUserId(polarUserIdText.getText());
            settings.setExerciseTransactionId(
                Integer.parseInt(exerciseTransactionIdText.getText()));
            settings.setActivityTransactionId(
                Integer.parseInt(activityTransactionIdText.getText()));
            settings.setPhysicalInfoTransactionId(
                Integer.parseInt(physicalInfoTransactionIdText.getText()));
            settings.setInitialTcxGpxSrcDir(initialTcxGpxSrcDirText.getText());
            settings
                .setInitialTcxGpxDestDir(initialTcxGpxDestDirText.getText());
            settings.setTcxGpxDownloadDir(tcxGpxDownloadDirText.getText());
            settings.setTcxGpxDownloadSaveMode(
                (SaveMode)tcxGpxDownloadSaveModeCombo.getSelectedItem());
            settings.setTcxGpxDownloadRenameMode(
                (RenameMode)tcxGpxDownloadRenameModeCombo.getSelectedItem());
            settings.setTcxGpxRenameDestDir(tcxGpxRenameDestDirText.getText());
            if(fileNameSubstitutionMap != null) {
                Gson gson = new Gson();
                String json = gson.toJson(fileNameSubstitutionMap);
                if(json != null) {
                    settings.setFileNameSubstitution(json);
                }
            }
        } catch(Exception ex) {
            Utils.excMsg("Error reading values", ex);
            return false;
        }

        // Check if the values are valid
        boolean res = settings.checkValues(true);
        if(!res) {
            Utils.errMsg("Some values are invalid");
        }
        return res;
    }

    /**
     * Saves the current values to the preference store if they are valid.
     */
    public void save() {
        Settings settings = new Settings();
        boolean res = setSettingsFromValues(settings);
        if(!res) {
            Utils.errMsg("Aborting");
            return;
        }
        // Save to the preference store
        try {
            res = settings.saveToPreferences(true);
        } catch(Exception ex) {
            Utils.excMsg("Error saving preferences", ex);
            return;
        }
        if(res) {
            // Utils.errMsg("Preferences saved successfully");
        } else {
            Utils.errMsg("Error saving preferences");
        }
    }

    /**
     * Sets the current values to the manager if they are valid.
     */
    public void setToManager() {
        Settings settings = new Settings();
        boolean res = setSettingsFromValues(settings);
        if(!res) {
            Utils.errMsg("Aborting");
            return;
        }
        // Copy to the manager settings
        try {
            manager.onPreferenceReset(settings);
        } catch(Exception ex) {
            Utils.excMsg("Error setting manager settings", ex);
            return;
        }
        if(res) {
            // Utils.errMsg("Viewer settings set successfully");
        } else {
            Utils.errMsg("Error setting manager settings");
        }
    }

    /**
     * Shows the dialog and returns whether it was successful or not. However
     * currently it is always successful and returns only on Cancel/Done.
     * 
     * @return
     */
    public boolean showDialog() {
        setVisible(true);
        dispose();
        return ok;
    }

}
