package net.kenevans.polar.accessmanager.preferences;

import java.util.prefs.Preferences;

import net.kenevans.polar.accessmanager.ui.IConstants;
import net.kenevans.polar.utils.Utils;

/**
 * Settings stores the settings for PolarAccessManager.
 * 
 * @author Kenneth Evans, Jr.
 */
public class Settings implements IConstants
{
    private String accessCode = D_ACCESS;
    private String token = D_TOKEN;
    private String clientUserId = D_CLIENT_USER_ID;
    private String polarUserId = D_CLIENT_USER_ID;
    private Integer exerciseTransactionId = D_EXERCISE_TRANSACTION_ID;
    private Integer activityTransactionId = D_ACTIVITY_TRANSACTION_ID;
    private Integer physicalInfoTransactionId = D_PHYSICAL_INFO_TRANSACTION_ID;
    private String initialTcxGpxSrcDir = D_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR;
    private String initialTcxGpxDestDir = D_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR;
    private String tcxGpxDownloadDir = D_TCX_GPX_DOWNLOAD_DIR;
    private String fileNameSubstitution = D_FILENAME_SUBSTITUTION;
    private String tcxGpxRenameDestDir = D_TCX_GPX_RENAME_DEST_DIR;
    private SaveMode tcxGpxDownloadSaveMode = SaveMode
        .valueOf(D_TCX_GPX_DOWNLOAD_SAVE_MODE);
    private RenameMode tcxGpxDownloadRenameMode = RenameMode
        .valueOf(D_TCX_GPX_DOWNLOAD_RENAME_MODE);

    /**
     * Loads the settings from the preferences
     */
    public void loadFromPreferences() {
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        accessCode = prefs.get(P_ACCESS, D_ACCESS);
        token = prefs.get(P_TOKEN, D_TOKEN);
        clientUserId = prefs.get(P_CLIENT_USER_ID, D_CLIENT_USER_ID);
        polarUserId = prefs.get(P_POLAR_USER_ID, D_POLAR_USER_ID);
        exerciseTransactionId = prefs.getInt(P_EXERCISE_TRANSACTION_ID,
            D_EXERCISE_TRANSACTION_ID);
        activityTransactionId = prefs.getInt(P_ACTIVITY_TRANSACTION_ID,
            D_ACTIVITY_TRANSACTION_ID);
        physicalInfoTransactionId = prefs.getInt(P_PHYSICAL_INFO_TRANSACTION_ID,
            D_PHYSICAL_INFO_TRANSACTION_ID);
        initialTcxGpxSrcDir = prefs.get(P_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR,
            D_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR);
        initialTcxGpxDestDir = prefs.get(P_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR,
            D_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR);
        tcxGpxDownloadDir = prefs.get(P_TCX_GPX_DOWNLOAD_DIR,
            D_TCX_GPX_DOWNLOAD_DIR);
        tcxGpxDownloadSaveMode = SaveMode.valueOf(prefs
            .get(P_TCX_GPX_DOWN_LOAD_SAVE_MODE, D_TCX_GPX_DOWNLOAD_SAVE_MODE));
        tcxGpxDownloadRenameMode = RenameMode.valueOf(prefs.get(
            P_TCX_GPX_DOWN_LOAD_RENAME_MODE, D_TCX_GPX_DOWNLOAD_RENAME_MODE));
        tcxGpxRenameDestDir = prefs.get(P_TCX_GPX_RENAME_DEST_DIR,
            D_TCX_GPX_RENAME_DEST_DIR);
        fileNameSubstitution = prefs.get(P_FILENAME_SUBSTITUTION,
            D_FILENAME_SUBSTITUTION);
    }

    /**
     * Save the current values to the preferences.
     * 
     * @param showErrors Use Utils.errMsg() to show the errors.
     * @return
     */
    public boolean saveToPreferences(boolean showErrors) {
        boolean retVal = checkValues(showErrors);
        if(!retVal) {
            return retVal;
        }
        try {
            Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
            prefs.put(P_ACCESS, accessCode);
            prefs.put(P_TOKEN, token);
            prefs.put(P_CLIENT_USER_ID, clientUserId);
            prefs.put(P_POLAR_USER_ID, polarUserId);
            prefs.putInt(P_EXERCISE_TRANSACTION_ID, exerciseTransactionId);
            prefs.putInt(P_ACTIVITY_TRANSACTION_ID, activityTransactionId);
            prefs.putInt(P_PHYSICAL_INFO_TRANSACTION_ID, physicalInfoTransactionId);
            prefs.put(P_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR, initialTcxGpxSrcDir);
            prefs.put(P_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR,
                initialTcxGpxDestDir);
            prefs.put(P_TCX_GPX_DOWNLOAD_DIR, tcxGpxDownloadDir);
            prefs.put(P_TCX_GPX_DOWN_LOAD_SAVE_MODE,
                tcxGpxDownloadSaveMode.toString());
            prefs.put(P_TCX_GPX_DOWN_LOAD_RENAME_MODE,
                tcxGpxDownloadRenameMode.toString());
            prefs.put(P_TCX_GPX_RENAME_DEST_DIR, tcxGpxRenameDestDir);
            prefs.put(P_FILENAME_SUBSTITUTION, fileNameSubstitution);
        } catch(Exception ex) {
            retVal = false;
            if(showErrors) {
                Utils.excMsg("Error saving preferences", ex);
            }
        }
        return retVal;
    }

    /**
     * Returns if the parameters are valid
     * 
     * @param showErrors Use Utils.errMsg() to show the errors.
     * @return
     */
    public boolean checkValues(boolean showErrors) {
        boolean retVal = true;

        // // Default directory
        // if(defaultDirectory == null) {
        // if(showErrors) {
        // Utils.errMsg("Value for the default directory is null");
        // }
        // retVal = false;
        // } else {
        // File file = new File(defaultDirectory);
        // if(file == null) {
        // if(showErrors) {
        // Utils.errMsg("The default directory is invalid");
        // }
        // retVal = false;
        // } else {
        // if(!file.exists()) {
        // if(showErrors) {
        // Utils.errMsg("The default directory does not exist");
        // }
        // retVal = false;
        // } else {
        // if(!file.isDirectory()) {
        // if(showErrors) {
        // Utils
        // .errMsg("The default directory is not a directory");
        // }
        // retVal = false;
        // }
        // }
        // }
        // }
        //
        // // Database
        // if(database == null) {
        // if(showErrors) {
        // Utils.errMsg("Value for the database is null");
        // }
        // retVal = false;
        // } else {
        // File file = new File(database);
        // if(file == null) {
        // if(showErrors) {
        // Utils.errMsg("The database is invalid");
        // }
        // retVal = false;
        // } else {
        // if(!file.exists()) {
        // if(showErrors) {
        // Utils.errMsg("The database does not exist");
        // }
        // retVal = false;
        // }
        // }
        // }
        //
        return retVal;
    }

    /**
     * Copies the values in the given settings to this settings.
     * 
     * @param settings
     */
    public void copyFrom(Settings settings) {
        this.accessCode = settings.accessCode;
        this.token = settings.token;
        this.clientUserId = settings.clientUserId;
        this.polarUserId = settings.polarUserId;
        this.exerciseTransactionId = settings.exerciseTransactionId;
        this.activityTransactionId = settings.activityTransactionId;
        this.physicalInfoTransactionId = settings.physicalInfoTransactionId;
        this.initialTcxGpxSrcDir = settings.initialTcxGpxSrcDir;
        this.initialTcxGpxDestDir = settings.initialTcxGpxDestDir;
        this.tcxGpxDownloadDir = settings.tcxGpxDownloadDir;
        this.tcxGpxDownloadSaveMode = settings.tcxGpxDownloadSaveMode;
        this.tcxGpxDownloadRenameMode = settings.tcxGpxDownloadRenameMode;
        this.tcxGpxRenameDestDir = settings.tcxGpxRenameDestDir;
        this.fileNameSubstitution = settings.fileNameSubstitution;
    }

    /**
     * @return The value of accessCode.
     */
    public String getAccessCode() {
        return accessCode;
    }

    /**
     * @param accessCode The new value for accessCode.
     */
    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    /**
     * @return The value of token.
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The new value for token.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @param token The new value for token.
     */
    public void saveToken(String token) {
        this.token = token;
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        prefs.put(P_TOKEN, token);
    }

    /**
     * @return The value of clientUserId.
     */
    public String getClientUserId() {
        return clientUserId;
    }

    /**
     * @param clientUserId The new value for clientUserId.
     */
    public void setClientUserId(String clientUserId) {
        this.clientUserId = clientUserId;
    }

    /**
     * @return The value of polarUserId.
     */
    public String getPolarUserId() {
        return polarUserId;
    }

    /**
     * @param polarUserId The new value for polarUserId.
     */
    public void savePolarUserId(String polarUserId) {
        this.polarUserId = polarUserId;
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        prefs.put(P_POLAR_USER_ID, polarUserId);
    }

    /**
     * @param polarUserId The new value for polarUserId.
     */
    public void setPolarUserId(String polarUserId) {
        this.polarUserId = polarUserId;
    }

    /**
     * @return The value of exerciseTransactionId.
     */
    public Integer getExerciseTransactionId() {
        return exerciseTransactionId;
    }

    /**
     * @return The value of activityTransactionId.
     */
    public Integer getActivityTransactionId() {
        return activityTransactionId;
    }

    /**
     * @return The value of exerciseTransactionId.
     */
    public Integer getPhysicalInfoTransactionId() {
        return physicalInfoTransactionId;
    }

    /**
     * @param exerciseTransactionId The new value for exerciseTransactionId.
     */
    public void setExerciseTransactionId(Integer exerciseTransactionId) {
        this.exerciseTransactionId = exerciseTransactionId;
    }

    /**
     * @param activityTransactionId The new value for activityTransactionId.
     */
    public void setActivityTransactionId(Integer activityTransactionId) {
        this.activityTransactionId = activityTransactionId;
    }

    /**
     * @param physicalInfoTransactionId The new value for physicalInfoTransactionId.
     */
    public void setPhysicalInfoTransactionId(Integer physicalInfoTransactionId) {
        this.physicalInfoTransactionId = physicalInfoTransactionId;
    }

    /**
     * @param exerciseTransactionId The new value for exerciseTransactionId.
     */
    public void saveExerciseTransactionId(Integer exerciseTransactionId) {
        this.exerciseTransactionId = exerciseTransactionId;
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        prefs.putInt(P_EXERCISE_TRANSACTION_ID, exerciseTransactionId);
    }

    /**
     * @param activityTransactionId The new value for activityTransactionId.
     */
    public void saveActivityTransactionId(Integer activityTransactionId) {
        this.activityTransactionId = activityTransactionId;
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        prefs.putInt(P_ACTIVITY_TRANSACTION_ID, exerciseTransactionId);
    }

    /**
     * @param physicalInfoTransactionId The new value for physicalInfoTransactionId.
     */
    public void savePhysicalInfoTransactionId(Integer physicalInfoTransactionId) {
        this.physicalInfoTransactionId = physicalInfoTransactionId;
        Preferences prefs = Preferences.userRoot().node(P_PREFERENCE_NODE);
        prefs.putInt(P_PHYSICAL_INFO_TRANSACTION_ID, physicalInfoTransactionId);
    }

    /**
     * @return The value of initialTcxGpxSrcDir.
     */
    public String getInitialTcxGpxSrcDir() {
        return initialTcxGpxSrcDir;
    }

    /**
     * @param initialTcxGpxSrcDir The new value for initialTcxGpxSrcDir.
     */
    public void setInitialTcxGpxSrcDir(String initialTcxGpxSrcDir) {
        this.initialTcxGpxSrcDir = initialTcxGpxSrcDir;
    }

    /**
     * @return The value of initialTcxGpxDestDirText.
     */
    public String getInitialTcxGpxDestDir() {
        return initialTcxGpxDestDir;
    }

    /**
     * @param initialTcxGpxDestDirText The new value for
     *            initialTcxGpxDestDirText.
     */
    public void setInitialTcxGpxDestDir(String initialTcxGpxDestDir) {
        this.initialTcxGpxDestDir = initialTcxGpxDestDir;
    }

    /**
     * @return The value of tcxGpxDownloadDir.
     */
    public String getTcxGpxDownloadDir() {
        return tcxGpxDownloadDir;
    }

    /**
     * @param tcxGpxDownloadDir The new value for tcxGpxDownloadDir.
     */
    public void setTcxGpxDownloadDir(String tcxGpxDownloadDir) {
        this.tcxGpxDownloadDir = tcxGpxDownloadDir;
    }

    /**
     * @return The value of tcxGpxDownloadSaveMode.
     */
    public SaveMode getTcxGpxDownloadSaveMode() {
        return tcxGpxDownloadSaveMode;
    }

    /**
     * @param tcxGpxDownloadSaveMode The new value for tcxGpxDownloadSaveMode.
     */
    public void setTcxGpxDownloadSaveMode(SaveMode tcxGpxDownloadSaveMode) {
        this.tcxGpxDownloadSaveMode = tcxGpxDownloadSaveMode;
    }

    /**
     * @return The value of tcxGpxDownloadRenameMode.
     */
    public RenameMode getTcxGpxDownloadRenameMode() {
        return tcxGpxDownloadRenameMode;
    }

    /**
     * @param tcxGpxDownloadRenameMode The new value for
     *            tcxGpxDownloadRenameMode.
     */
    public void setTcxGpxDownloadRenameMode(
        RenameMode tcxGpxDownloadRenameMode) {
        this.tcxGpxDownloadRenameMode = tcxGpxDownloadRenameMode;
    }

    /**
     * @return The value of tcxGpxRenameDestDir.
     */
    public String getTcxGpxRenameDestDir() {
        return tcxGpxRenameDestDir;
    }

    /**
     * @param tcxGpxRenameDestDir The new value for tcxGpxRenameDestDir.
     */
    public void setTcxGpxRenameDestDir(String tcxGpxRenameDestDir) {
        this.tcxGpxRenameDestDir = tcxGpxRenameDestDir;
    }

    /**
     * @return The value of fileNameSubstitution.
     */
    public String getFileNameSubstitution() {
        return fileNameSubstitution;
    }

    /**
     * @param fileNameSubstitution The new value for fileNameSubstitution.
     */
    public void setFileNameSubstitution(String fileNameSubstitution) {
        this.fileNameSubstitution = fileNameSubstitution;
    }

}
