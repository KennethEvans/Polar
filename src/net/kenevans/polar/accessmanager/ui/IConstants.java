package net.kenevans.polar.accessmanager.ui;

/*
 * Created on Feb 1, 2019
 * By Kenneth Evans, Jr.
 */

public interface IConstants
{
    public static final String LS = System.getProperty("line.separator");

    public static final String AUTHORIZATION_URL_PREFIX = "https://flow.polar.com/oauth2/authorization?response_type=code&client_id=";
    public static final String ACCESS_TOKEN_URL = "https://polarremote.com/v2/oauth2/token";
    public static final String ACCESS_LINK_URL = "https://www.polaraccesslink.com/v3";

    /**
     * The file where the encrypted access code is kept (so it won't be
     * committed). Needs to be changed for another client.
     */
    public static String ACCESS_CODE_FILE = "C:/Users/evans/Documents/Personal/Keys/polaraccess.txt";

    /** Our custom id for the user, used in register user */
    public static final String DEFAULT_USER_ID = "User1";

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
    public static final String P_POLAR_USER_ID = "polar_user_id";
    /*** The default value for the Polar user_id. */
    public static final String D_POLAR_USER_ID = "";
    /*** The preference name for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String P_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR = "mergeTcxAndGpxtoGpxSrcDirectory";
    /*** The default value for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String D_MERGE_TCX_AND_GPX_TO_GPX_SRC_DIR = "";
    /*** The preference name for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String P_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR = "mergeTcxAndGpxtoGpxDestDirectory";
    /*** The default value for the MergeTcxAndGpxtoGpx source Directory. */
    public static final String D_MERGE_TCX_AND_GPX_TO_GPX_DEST_DIR = "";

    public static final String MERGE_TCX_AND_GPX_TO_GPX_AUTHOR = "Polar Access Manager - MergeTcxAndGpxToGpx";
    public static final String MERGE_TCX_AND_GPX_TO_GPX_EXT = ".conv.gpx";
}
