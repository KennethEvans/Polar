package net.kenevans.polar.accessmanager.ui;

/*
 * Created on Feb 1, 2019
 * By Kenneth Evans, Jr.
 */

public interface IConstants
{
    public static final String LS = System.getProperty("line.separator");

    public static enum SaveMode {
        PROMPT, SKIP, OVERWRITE
    };

    public static enum RenameMode {
        NO, PROMPT, AUTO
    };

    public static final String AUTHORIZATION_URL_PREFIX = "https://flow.polar.com/oauth2/authorization?response_type=code&client_id=";
    public static final String ACCESS_TOKEN_URL = "https://polarremote.com/v2/oauth2/token";
    public static final String ACCESS_LINK_URL = "https://www.polaraccesslink.com/v3/";

    /** Our custom id for the user, used in register user */
    public static final String DEFAULT_USER_ID = "User1";

    public int PRETTY_PRINT_INDENT = 2;

    /***
     * The name of the preference node for accessing preferences for this
     * application. On Windows these are found in the registry under
     * HKCU/Software/JavaSoft/Prefs.
     */
    public static final String P_PREFERENCE_NODE = "net/kenevans/polaraccessmanager/preferences";

    /*** The preference name for the authorization access code. */
    public static final String P_ACCESS = "access";
    /*** The default value for the authorization access code. */
    public static final String D_ACCESS = "";
    /*** The preference name for the token. */
    public static final String P_TOKEN = "token";
    /*** The default value for the token. */
    public static final String D_TOKEN = "";
    /*** The preference name for the client user_id. */
    public static final String P_CLIENT_USER_ID = "client user_id";
    /*** The default value for the client user_id. */
    public static final String D_CLIENT_USER_ID = DEFAULT_USER_ID;
    /*** The preference name for the user_id. */
    public static final String P_POLAR_USER_ID = "polarUserId";
    /*** The default value for the Polar user_id. */
    public static final String D_POLAR_USER_ID = "";
    /*** The preference name for the exercise transaction_id. */
    public static final String P_EXERCISE_TRANSACTION_ID = "exercise_Transaction_id";
    /*** The preference name for the activity transaction_id. */
    public static final String P_ACTIVITY_TRANSACTION_ID = "activity_Transaction_id";
    /*** The preference name for the physical info transaction_id. */
    public static final String P_PHYSICAL_INFO_TRANSACTION_ID = "physical_info_Transaction_id";
    /*** The default value for the exercise transaction_id. */
    public static final int D_EXERCISE_TRANSACTION_ID = -1;
    /*** The default value for the activity transaction_id. */
    public static final int D_ACTIVITY_TRANSACTION_ID = -1;
    /*** The default value for the physical info transaction_id. */
    public static final int D_PHYSICAL_INFO_TRANSACTION_ID = -1;
    /*** The preference name for file name substitution (a JSON string). */
    public static final String P_FILENAME_SUBSTITUTION = "fileNameSubstitution";
    /*** The default value for file name substitution. */
    public static final String D_FILENAME_SUBSTITUTION = "{}";

    /*** The preference name for TCX/GPX Save Directory. */
    public static final String P_TCX_GPX_DOWNLOAD_DIR = "tcxGpxDownloadDirectory";
    /*** The default value for the TCX/GPX Save Directory. */
    public static final String D_TCX_GPX_DOWNLOAD_DIR = "";

    /*** The preference name for TCX/GPX Rename Destination Directory. */
    public static final String P_TCX_GPX_RENAME_DEST_DIR = "tcxGpxRenameDestDirectory";
    /*** The default value for the TCX/GPX Rename Destination Directory. */
    public static final String D_TCX_GPX_RENAME_DEST_DIR = "";

    /*** The preference name for TCX/GPX Save Mode. */
    public static final String P_TCX_GPX_DOWN_LOAD_SAVE_MODE = "tcxGpxDownloadSaveMode";
    /*** The default value for the TCX/GPX Save Mode. */
    public static final String D_TCX_GPX_DOWNLOAD_SAVE_MODE = SaveMode.PROMPT
        .toString();

    /*** The preference name for TCX/GPX Rename Mode. */
    public static final String P_TCX_GPX_DOWN_LOAD_RENAME_MODE = "tcxGpxDownloadRenameMode";
    /*** The default value for the TCX/GPX Rename Mode. */
    public static final String D_TCX_GPX_DOWNLOAD_RENAME_MODE = RenameMode.NO
        .toString();

    /*** The preference name for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String P_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR = "mergeTcxAndGpxtoGpxSrcDirectory";
    /*** The default value for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String D_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR = "";
    /*** The preference name for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String P_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR = "mergeTcxAndGpxtoGpxDestDirectory";
    /*** The default value for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String D_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR = "";

    public static final String MERGE_TCX_AND_GPX_TO_GPX_AUTHOR = "Polar Access Http - MergeTcxAndGpxToGpx";
    public static final String MERGE_TCX_AND_GPX_TO_GPX_EXT = ".conv.gpx";

}
