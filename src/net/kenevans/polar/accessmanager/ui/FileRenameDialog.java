package net.kenevans.polar.accessmanager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.MetadataType;
import net.kenevans.gpxtrackpointextensionv2.TrkType;
import net.kenevans.polar.utils.Utils;
import net.kenevans.trainingcenterdatabasev2.AbstractSourceT;
import net.kenevans.trainingcenterdatabasev2.ActivityListT;
import net.kenevans.trainingcenterdatabasev2.ActivityT;
import net.kenevans.trainingcenterdatabasev2.PlanT;
import net.kenevans.trainingcenterdatabasev2.SportT;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.TrainingT;
import net.kenevans.trainingcenterdatabasev2.TrainingTypeT;

/**
 * FileRenameDialog is a dialog to set the Preferences for PolarAccessManager. It only
 * returns after Cancel. It can save the values to the preference store or set
 * them in the manager. In either case it remains visible.
 * 
 * @author Kenneth Evans, Jr.
 */
/**
 * FileRenameDialog
 * 
 * @author Kenneth Evans, Jr.
 */
public class FileRenameDialog extends JDialog implements IConstants
{
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_PLAN = "pppp";
    private static final String DEFAULT_NOTES = "nnnn";

    public static enum Result {
        OK, FAIL, ABORT, ABORT_REMAINING
    };

    private static final int DEFAULT_TEXT_LENGTH = 60;
    private PolarAccessManager manager;
    private File tcxFile;
    private File gpxFile;
    private String renameDestDirPath;
    private TrainingCenterDatabaseT tcx;
    private GpxType gpx;
    private boolean tcxSaved = false;
    private boolean gpxSaved = false;

    /** The return value. */
    private Result result = Result.OK;

    JPanel tcxGroup;
    JPanel gpxGroup;

    JTextField tcxOldNameText;
    JTextArea tcxMetadataText;
    JTextField gpxOldNameText;
    JTextArea gpxMetadataText;

    JTextField tcxNewNameText;
    JTextField gpxNewNameText;
    JTextField tcxStatusText;
    JTextField gpxStatusText;

    JTextField accessCodeText;
    JTextField tokenText;
    JTextField clientUserIdText;
    JTextField polarUserIdText;
    JTextField exerciseTransactionIdText;

    JTextField initialTcxGpxSrcDirText;
    JTextField initialTcxGpxDestDirText;
    JTextField tcxGpxDownloadDirText;
    JComboBox<SaveMode> tcxGpxDownloadSaveModeCombo;

    JList<String> fileNameSubstitutionList;
    DefaultListModel<String> fileNameSubstitutionModel;

    /**
     * Constructor
     */
    public FileRenameDialog(Component parent, PolarAccessManager viewer,
        File tcxFile, TrainingCenterDatabaseT tcx, File gpxFile, GpxType gpx) {
        super();
        this.manager = viewer;
        if(viewer == null) {
            Utils.errMsg("Viewer is null");
            result = Result.FAIL;
            return;
        }
        if(tcxFile == null) {
            Utils.errMsg("TCX file must be specified");
            result = Result.FAIL;
            return;
        }
        if(tcx == null) {
            Utils.errMsg("TCX must be specified");
            result = Result.FAIL;
            return;
        }
        // Reset the parent to the renameDestDir from settings
        renameDestDirPath = manager.getSettings().getTcxGpxRenameDestDir();
        if(renameDestDirPath == null || renameDestDirPath.isEmpty()) {
            Utils.errMsg(
                "The rename destination directory has not been specified." + LS
                    + "Specify it in Preferences.");
            result = Result.FAIL;
            return;
        }
        this.tcxFile = tcxFile;
        this.gpxFile = gpxFile;
        this.tcx = tcx;
        this.gpx = gpx;

        URL url = PolarAccessManager.class
            .getResource("/resources/PolarAccessManager.32x32.png");

        if(url != null) {
            setIconImage(new ImageIcon(url).getImage());
        }
        init();

        // Disable the GPX group if there is no GPX
        if(gpxFile == null || gpx == null) {
            setPanelEnabled(gpxGroup, false);
        }

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
        JScrollPane scroll;
        MetaData metaData;
        String metaDataText;
        String tcxNewName;
        String tcxNameFromMetadata = null;
        File tcxNewFile = null;
        String gpxNewName;
        File gpxNewFile = null;

        this.setTitle("Rename");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints gbcDefault = new GridBagConstraints();
        gbcDefault.insets = new Insets(2, 2, 2, 2);
        gbcDefault.anchor = GridBagConstraints.WEST;
        gbcDefault.fill = GridBagConstraints.NONE;
        GridBagConstraints gbc = null;
        int gridy = -1;
        int gridPanel = -1;

        // TCX Group //////////////////////////////////////////////////////
        tcxGroup = new JPanel();
        tcxGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("TCX"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        tcxGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(tcxGroup, gbc);

        gridPanel = -1;

        // Old name
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        tcxGroup.add(panel, gbc);

        label = new JLabel("Old File:");
        label.setToolTipText("Original filename.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        panel.add(label, gbc);

        tcxOldNameText = new JTextField(DEFAULT_TEXT_LENGTH);
        tcxOldNameText.setToolTipText(label.getToolTipText());
        tcxOldNameText.setText(tcxFile.getPath());
        tcxOldNameText.setEditable(false);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(tcxOldNameText, gbc);

        // Metadata
        gridPanel++;
        label = new JLabel("Metadata");
        label.setToolTipText("Metadata found in the file");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        tcxGroup.add(label, gbc);

        gridPanel++;
        tcxMetadataText = new JTextArea(10, DEFAULT_TEXT_LENGTH);
        tcxMetadataText.setToolTipText(label.getToolTipText());
        metaData = getMetaData(tcx);
        metaDataText = null;
        if(metaData != null) {
            metaDataText = getMetaDataText(metaData);
            if(metaDataText != null) {
                tcxMetadataText.setText(metaDataText);
            }
        }
        scroll = new JScrollPane(tcxMetadataText);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        tcxGroup.add(scroll, gbc);

        // New name
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        tcxGroup.add(panel, gbc);

        label = new JLabel("New File:");
        label.setToolTipText("New filename.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        panel.add(label, gbc);

        tcxNewNameText = new JTextField(DEFAULT_TEXT_LENGTH);
        tcxNewNameText.setToolTipText(label.getToolTipText());
        tcxNewNameText.getDocument()
            .addDocumentListener(new SimpleDocumentListener() {
                @Override
                public void update(DocumentEvent e) {
                    if(tcxStatusText == null) {
                        return;
                    }
                    File file = new File(tcxNewNameText.getText());
                    if(file == null) {
                        tcxStatusText.setText("Null file");
                        return;
                    }
                    try {
                        // Doesn't catch everything
                        file.getCanonicalFile();
                    } catch(Exception ex) {
                        tcxStatusText.setText("Invalid file");
                        return;
                    }
                    if(file.getPath().toLowerCase()
                        .equals(tcxFile.getPath().toLowerCase())) {
                        tcxStatusText.setText("Same as original");
                        return;
                    }
                    if(file.exists()) {
                        tcxStatusText.setText("Already exists");
                        return;
                    }
                    if(tcxNewNameText.getText().contains(DEFAULT_PLAN)
                        || tcxNewNameText.getText().contains(DEFAULT_NOTES)) {
                        tcxStatusText.setText("Missing Plan and/or Notes");
                        return;
                    }
                    tcxStatusText.setText("");
                }
            });
        tcxNewName = null;
        tcxNewFile = null;
        if(metaData != null) {
            tcxNewName = getTcxNameFromMetaData(metaData);
            // Save to use for gpx
            tcxNameFromMetadata = tcxNewName;
            if(tcxNewName != null) {
                tcxNewFile = new File(renameDestDirPath, tcxNewName);
            }
        }
        if(tcxNewFile != null) {
            tcxNewName = tcxNewFile.getPath();
            tcxNewNameText.setText(tcxNewName);
        } else {
            tcxNewNameText.setText(tcxFile.getPath());
        }
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(tcxNewNameText, gbc);

        // Controls
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        tcxGroup.add(panel, gbc);

        label = new JLabel("Status:");
        label.setToolTipText("Issues with the new filename");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        panel.add(label, gbc);

        tcxStatusText = new JTextField(DEFAULT_TEXT_LENGTH);
        tcxStatusText.setToolTipText(label.getToolTipText());
        tcxStatusText.setForeground(Color.RED);
        if(tcxNewNameText.getText().contains(DEFAULT_PLAN)
            || tcxNewNameText.getText().contains(DEFAULT_NOTES)) {
            tcxStatusText.setText("Missing Plan and/or Notes");
        }
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(tcxStatusText, gbc);

        button = new JButton();
        button.setText("Rename");
        button.setToolTipText("Perform the renaming.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Result result = rename(tcxOldNameText, tcxNewNameText,
                    tcxStatusText, tcxFile);
                tcxSaved = result == Result.OK;
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        panel.add(button, gbc);

        // GPX ////////////////////////////////////////////////////////////////
        gpxGroup = new JPanel();
        gpxGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("GPX"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        gpxGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(gpxGroup, gbc);

        gridPanel = -1;

        // Old Name
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        gpxGroup.add(panel, gbc);

        label = new JLabel("Old File:");
        label.setToolTipText("Original filename.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        panel.add(label, gbc);

        gpxOldNameText = new JTextField(DEFAULT_TEXT_LENGTH);
        gpxOldNameText.setToolTipText(label.getToolTipText());
        if(gpxFile != null) {
            gpxOldNameText.setText(gpxFile.getPath());
        }
        gpxOldNameText.setEditable(false);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(gpxOldNameText, gbc);

        // Metadata
        gridPanel++;
        label = new JLabel("Metadata");
        label.setToolTipText("Metadata found in the file");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gpxGroup.add(label, gbc);

        gridPanel++;
        gpxMetadataText = new JTextArea(10, DEFAULT_TEXT_LENGTH);
        gpxMetadataText.setToolTipText(label.getToolTipText());
        if(gpxFile != null && gpx != null) {
            metaDataText = null;
            metaData = getMetaData(gpx);
            if(metaData != null) {
                metaDataText = getMetaDataText(metaData);
                if(metaDataText != null) {
                    gpxMetadataText.setText(metaDataText);
                }
            }
        }
        scroll = new JScrollPane(gpxMetadataText);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gpxGroup.add(scroll, gbc);

        // New name
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        gpxGroup.add(panel, gbc);

        label = new JLabel("New File:");
        label.setToolTipText("New filename.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        panel.add(label, gbc);

        gpxNewNameText = new JTextField(DEFAULT_TEXT_LENGTH);
        gpxNewNameText.setToolTipText(label.getToolTipText());
        gpxNewNameText.getDocument()
            .addDocumentListener(new SimpleDocumentListener() {
                @Override
                public void update(DocumentEvent e) {
                    if(gpxStatusText == null) {
                        return;
                    }
                    File file = new File(gpxNewNameText.getText());
                    if(file == null) {
                        gpxStatusText.setText("Null file");
                        return;
                    }
                    try {
                        // Doesn't catch everything
                        file.getCanonicalFile();
                    } catch(Exception ex) {
                        gpxStatusText.setText("Invalid file");
                        return;
                    }
                    if(file.getPath().toLowerCase()
                        .equals(gpxFile.getPath().toLowerCase())) {
                        gpxStatusText.setText("Same as original");
                        return;
                    }
                    if(file.exists()) {
                        gpxStatusText.setText("Already exists");
                        return;
                    }
                    if(gpxNewNameText.getText().contains(DEFAULT_PLAN)
                        || gpxNewNameText.getText().contains(DEFAULT_NOTES)) {
                        gpxStatusText.setText("Missing Plan and/or Notes");
                        return;
                    }
                    gpxStatusText.setText("");
                }
            });
        gpxNewName = null;
        gpxNewFile = null;
        if(gpxFile != null && gpx != null) {

            // Make it the same as for TCX except the extension.
            if(tcxNameFromMetadata != null) {
                gpxNewName = tcxNameFromMetadata.replaceAll("tcx$", "gpx");
                if(gpxNewName != null) {
                    gpxNewFile = new File(renameDestDirPath, gpxNewName);
                }
            }
            if(gpxNewFile != null) {
                gpxNewNameText.setText(gpxNewFile.getPath());
            } else {
                gpxNewNameText.setText(gpxFile.getPath());
            }
        }
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(gpxNewNameText, gbc);

        // Controls
        gridPanel++;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridPanel;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        gpxGroup.add(panel, gbc);

        label = new JLabel("Status:");
        label.setToolTipText("Issues with the new filename");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        panel.add(label, gbc);

        gpxStatusText = new JTextField(DEFAULT_TEXT_LENGTH);
        gpxStatusText.setToolTipText(label.getToolTipText());
        gpxStatusText.setForeground(Color.RED);
        if(gpxNewNameText.getText().contains(DEFAULT_PLAN)
            || gpxNewNameText.getText().contains(DEFAULT_NOTES)) {
            gpxStatusText.setText("Missing Plan and/or Notes");
        }
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        panel.add(gpxStatusText, gbc);

        button = new JButton();
        button.setText("Rename");
        button.setToolTipText("Perform the renaming.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                Result result = rename(gpxOldNameText, gpxNewNameText,
                    gpxStatusText, gpxFile);
                gpxSaved = result == Result.OK;
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        panel.add(button, gbc);

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
        // dummyText.setToolTipText(label.getToolTipText());
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
        button.setText("Abort");
        button.setToolTipText(
            "Close the dialog, do nothing more, and return ABORT_REMAINING.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                result = Result.ABORT_REMAINING;
                FileRenameDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Done");
        button.setToolTipText(
            "Close the dialog, do nothing more, and return OK.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                String msg = null;
                if(gpxFile == null || gpx == null) {
                    if(!tcxSaved) {
                        msg = "TCX not saved." + LS + "OK to continue?";
                    }
                } else {
                    if(!tcxSaved || !gpxSaved) {
                        msg = "TCX " + (tcxSaved ? "" : "not ") + "saved, GPX "
                            + (gpxSaved ? "" : "not ") + "saved." + LS
                            + "OK to continue?";
                    }
                }
                if(msg != null) {
                    int selection = JOptionPane.showConfirmDialog(null, msg,
                        "Confirmation", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    if(selection != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                result = Result.OK;
                FileRenameDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(button);

        pack();
    }

    private void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);
        Component[] components = panel.getComponents();
        for(Component component : components) {
            if(component instanceof JPanel) {
                setPanelEnabled((JPanel)component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    /**
     * Get various metadata for the given TrainingCenterDatabaseT.
     * 
     * @param tcx
     * @return
     */
    public MetaData getMetaData(TrainingCenterDatabaseT tcx) {
        MetaData metaData = new MetaData();
        AbstractSourceT author = tcx.getAuthor();
        if(author != null && author.getName() != null) {
            metaData.author = author.getName();
        }

        ActivityListT activities;
        List<ActivityT> activityList;
        TrainingT training;
        TrainingTypeT trainingType;
        SportT sport;
        PlanT plan;
        String planName, notes, sportName, trainingTypeName;
        XMLGregorianCalendar id;
        // Loop over activities (Correspond to a track)
        activities = tcx.getActivities();
        // Loop over activities
        activityList = activities.getActivity();
        for(ActivityT activity : activityList) {
            sportName = "";
            planName = "";
            trainingTypeName = "";
            notes = "";
            id = activity.getId();
            sport = activity.getSport();
            if(sport != null && sport.name() != null) {
                sportName = sport.name();
            }
            planName = "";
            trainingTypeName = "";
            notes = "";
            training = activity.getTraining();
            if(training != null) {
                plan = training.getPlan();
                if(plan != null) {
                    trainingType = plan.getType();
                    if(trainingType != null) {
                        trainingTypeName = trainingType.name();
                    }
                    planName = plan.getName();
                }
            }
            notes = activity.getNotes();

            metaData.activityId.add(id);
            metaData.activitySport.add(sportName);
            metaData.planName.add(planName);
            metaData.trainingType.add(trainingTypeName);
            metaData.notes.add(notes);
        }
        return metaData;
    }

    /**
     * Get various metadata for the given GpxType.
     * 
     * @param gpx
     * @return
     */
    public MetaData getMetaData(GpxType gpx) {
        MetaData metaData = new MetaData();
        String desc = null;
        MetadataType metadata;
        List<TrkType> trkList;
        // Metadata
        metadata = gpx.getMetadata();
        if(metadata != null) {
            metaData.metadataDesc = metadata.getDesc();
            if(metadata.getAuthor() != null
                && metadata.getAuthor().getName() != null) {
                metaData.author = metadata.getAuthor().getName();
            }
        }
        trkList = gpx.getTrk();
        if(trkList != null) {
            for(TrkType trk : trkList) {
                desc = trk.getDesc();
                if(desc != null) {
                    metaData.trackDesc.add(desc);
                }
            }
        }
        return metaData;
    }

    private String getMetaDataText(MetaData metaData) {
        if(metaData == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if(metaData.author != null && !metaData.author.isEmpty()) {
            sb.append("Author: " + metaData.author + LS);
        } else {
            sb.append("Author: None" + LS);
        }
        if(metaData.metadataDesc != null && !metaData.metadataDesc.isEmpty()) {
            sb.append("metadataDesc: " + metaData.metadataDesc + LS);
        } else {
            sb.append("metadataDesc: None" + LS);
        }
        int nActivities = metaData.activityId.size();
        for(int i = 0; i < nActivities; i++) {
            sb.append("Activity " + i + ": " + metaData.activityId.get(i) + " "
                + metaData.activitySport.get(i) + LS);
            sb.append("TrainingType: " + metaData.trainingType.get(i) + LS);
            sb.append("Sport: " + metaData.activitySport.get(i) + LS);
            sb.append("Plan: " + metaData.planName.get(i) + LS);
            sb.append("Notes: " + metaData.notes.get(i) + LS);
        }
        int nTracks = metaData.trackDesc.size();
        for(int i = 0; i < nTracks; i++) {
            sb.append("Track " + i + LS);
            sb.append("TrackDesc " + i + metaData.trackDesc.get(i) + LS);
        }
        return sb.toString();
    }

    /**
     * Generates a name using the user-name and metadata.
     * 
     * @param metaData
     * @return
     */
    private String getTcxNameFromMetaData(MetaData metaData) {
        String userName = manager.getSettings().getClientUserId();
        if(userName == null || userName.isEmpty()) {
            userName = "Polar";
        }
        userName = userName.trim().replaceAll(" ", "_");
        Date date;
        String time = "0000-00-00_000000";
        String plan = DEFAULT_PLAN;
        String notes = DEFAULT_NOTES;
        if(metaData != null) {
            XMLGregorianCalendar id = null;
            if(metaData.activityId != null && !metaData.activityId.isEmpty()) {
                id = metaData.activityId.get(0);
                if(id != null) {
                    date = id.toGregorianCalendar().getTime();
                    time = PolarAccessManager.fileDateFormat.format(date);
                }
            }
            if(metaData.planName != null && !metaData.planName.isEmpty()) {
                plan = metaData.planName.get(0);
                if(plan != null) {
                    plan = plan.trim().replaceAll(" ", "_");
                    plan = PolarAccessManager.capitalizeFirstLetters(plan, "_");
                } else {
                    plan = DEFAULT_PLAN;
                }
            }
            if(metaData.notes != null && !metaData.notes.isEmpty()) {
                notes = metaData.notes.get(0);
                if(notes != null) {
                    // Only use up to the first period
                    int index = notes.indexOf(".");
                    if(index != -1) {
                        notes = notes.substring(0, index);
                    }
                    // Limit it just in case
                    if(notes.length() > 40) {
                        notes = notes.substring(0, 40);
                    }
                    notes = notes.trim().replaceAll(" ", "_");
                } else {
                    notes = DEFAULT_NOTES;
                }
            }
        }
        String name = userName + "_" + time + "_" + plan + "_" + notes + ".tcx";

        // Do substitutions
        Map<String, String> fileNameSubstitutionMap;
        Gson gson = new Gson();
        fileNameSubstitutionMap = gson.fromJson(
            manager.getSettings().getFileNameSubstitution(),
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
     * Renaming method. Renaming method in one place to keep it the same for TCX
     * and GPX.
     * 
     * @param oldNameText JTextField for the old name.
     * @param newNameText JTextField for the new name.
     * @param statusText JTextField for the status.
     * @param srcFile File corresponding to the oldNameText.
     * @return
     */
    private Result rename(JTextField oldNameText, JTextField newNameText,
        JTextField statusText, File srcFile) {
        File oldFile = new File(oldNameText.getText());
        if(oldFile == null) {
            statusText.setText("Error accessing old file");
            Utils.errMsg("Error accessing " + oldNameText.getText());
            return Result.FAIL;
        }
        File newFile = new File(newNameText.getText());
        if(newFile == null) {
            statusText.setText("Error accessing new file");
            Utils.errMsg("Error accessing " + newNameText.getText());
            return Result.FAIL;
        }
        if(newFile.getPath().toLowerCase()
            .equals(oldFile.getPath().toLowerCase())) {
            statusText.setText("Name has not changed");
            Utils.errMsg("Name has not changed");
            return Result.FAIL;
        }
        if(newFile.exists()) {
            int selection = JOptionPane.showConfirmDialog(null,
                "File exists:" + LS + newFile.getPath() + LS
                    + "OK to overwrite?",
                "File Exists", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if(selection != JOptionPane.OK_OPTION) {
                return Result.ABORT;
            }
        }
        try {
            if(newFile.getPath().toLowerCase()
                .equals(srcFile.getPath().toLowerCase())) {
                // Rename
                Files.move(Paths.get(srcFile.getPath()),
                    Paths.get(newFile.getPath()),
                    StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Copy
                Files.copy(Paths.get(srcFile.getPath()),
                    Paths.get(newFile.getPath()),
                    StandardCopyOption.REPLACE_EXISTING);
            }
            statusText.setText("Successful");
            srcFile = newFile;
            oldNameText.setText(srcFile.getPath());
            return Result.OK;
        } catch(IOException ex) {
            statusText.setText("Error renaming file");
            Utils.excMsg("Error renaming file", ex);
            return Result.FAIL;
        }
    }

    /**
     * Shows the dialog and returns whether it was successful or not.
     * 
     * @return
     */
    public Result showDialog() {
        // Get a blank dialog if CTOR failed and you don't do this.
        if(result == Result.OK) {
            setVisible(true);
        }
        dispose();
        return result;
    }

    public Result renameSilently() {
        // Get a blank dialog if CTOR failed and you don't do this.
        if(result == Result.FAIL) {
            dispose();
            return result;
        }
        String statusText = tcxStatusText.getText();
        if(statusText != null && !statusText.isEmpty()) {
            int selection = JOptionPane.showConfirmDialog(null,
                tcxNewNameText.getText() + ":" + LS + statusText + LS
                    + "Ok to continue?",
                "Possible Problem", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if(selection != JOptionPane.OK_OPTION) {
                result = Result.ABORT;
                dispose();
                return result;
            }
        }
        result = rename(tcxOldNameText, tcxNewNameText, tcxStatusText, tcxFile);
        if(result == Result.FAIL) {
            manager.appendLineText("Rename Failed " + tcxNewNameText.getText());
            dispose();
            return result;
        }
        if(result == Result.ABORT) {
            manager
                .appendLineText("Rename Aborted " + tcxNewNameText.getText());
            dispose();
            return result;
        }
        manager.appendLineText("Rename Wrote " + tcxNewNameText.getText());
        if(gpxFile != null && gpx != null) {
            statusText = gpxStatusText.getText();
            if(statusText != null && !statusText.isEmpty()) {
                int selection = JOptionPane.showConfirmDialog(null,
                    gpxNewNameText.getText() + ":" + LS + statusText + LS
                        + "Ok to continue?",
                    "Possible Problem", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if(selection != JOptionPane.OK_OPTION) {
                    result = Result.ABORT;
                    dispose();
                    return result;
                }
            }
            result = rename(gpxOldNameText, gpxNewNameText, gpxStatusText,
                gpxFile);
            if(result == Result.FAIL) {
                manager.appendLineText(
                    "Rename Failed " + gpxNewNameText.getText());
                dispose();
                return result;
            }
            if(result == Result.ABORT) {
                manager.appendLineText(
                    "Rename Aborted " + gpxNewNameText.getText());
                dispose();
                return result;
            }
            manager.appendLineText("Rename Wrote " + gpxNewNameText.getText());
        }
        dispose();
        return result;
    }

    public class MetaData
    {
        public String author = "";
        public String metadataDesc = "";
        public List<String> trackDesc = new ArrayList<>();
        public List<XMLGregorianCalendar> activityId = new ArrayList<>();
        public List<String> activitySport = new ArrayList<>();
        public List<String> trainingType = new ArrayList<>();
        public List<String> planName = new ArrayList<>();
        public List<String> notes = new ArrayList<>();
    }

    /**
     * SimpleDocumentListener simplifies a document listener.
     * 
     * @author Kenneth Evans, Jr.
     */
    @FunctionalInterface
    public interface SimpleDocumentListener extends DocumentListener
    {
        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        default void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }

}
