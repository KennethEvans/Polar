package net.kenevans.polar.accessmanager.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.MetadataType;
import net.kenevans.gpxtrackpointextensionv2.TrkType;
import net.kenevans.gpxtrackpointextensionv2.TrksegType;
import net.kenevans.gpxtrackpointextensionv2.WptType;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXClone;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXParser;
import net.kenevans.trainingcenterdatabasev2.ActivityListT;
import net.kenevans.trainingcenterdatabasev2.ActivityT;
import net.kenevans.trainingcenterdatabasev2.PlanT;
import net.kenevans.trainingcenterdatabasev2.SportT;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.TrainingT;
import net.kenevans.trainingcenterdatabasev2.TrainingTypeT;
import net.kenevans.trainingcenterdatabasev2.parser.TCXParser;

/*
 * Created on Feb 2, 2019
 * By Kenneth Evans, Jr.
 */

/**
 * RenameTcxGpx
 * @author Kenneth Evans, Jr.
 */
/**
 * RenameTcxGpx
 * @author Kenneth Evans, Jr.
 */
/**
 * RenameTcxGpx
 * @author Kenneth Evans, Jr.
 */
/**
 * RenameTcxGpx
 * 
 * @author Kenneth Evans, Jr.
 */
public class RenameTcxGpx implements IConstants
{
    public static final String LS = System.getProperty("line.separator");
    PolarAccessManager manager;
    private List<TcxGpxFile> tcxGpxFiles = new ArrayList<>();
    private String initialSrcDir;
    private boolean doGpx;

    RenameTcxGpx(PolarAccessManager manager, boolean doMerge) {
        this.manager = manager;
        this.doGpx = doMerge;
        getPreferences();
    }

    public void processTcxFiles() {
        tcxGpxFiles.clear();
        File[] tcxFiles = getTcxFiles();
        if(tcxFiles == null) {
            return;
        }

        // Sort them by date, latest first
        Arrays.sort(tcxFiles, Collections.reverseOrder());

        // Get the matching file pairs
        String tcxPath, gpxPath;
        File tcxFile = null, gpxFile = null;
        for(File file : tcxFiles) {
            // Find the matching GPX file
            tcxPath = file.getPath();
            gpxFile = null;
            if(tcxPath.contains(".")) {
                gpxPath = tcxPath.substring(0, tcxPath.lastIndexOf('.'))
                    + ".gpx";
                gpxFile = new File(gpxPath);
            }
            if(gpxFile != null && !gpxFile.exists()) {
                gpxFile = null;
            }
            tcxGpxFiles.add(new TcxGpxFile(file, gpxFile));
        }

        // Process them
        TrainingCenterDatabaseT tcx = null;
        GpxType gpx = null;
        for(TcxGpxFile tcxgpx : tcxGpxFiles) {
            tcxFile = tcxgpx.tcxFile;
            try {
                tcx = TCXParser.parse(tcxFile);
            } catch(JAXBException ex) {
                manager.appendLineText("Failed to parse " + tcxFile.getPath()
                    + LS + ex.getMessage());
                continue;
            }
            manager.appendLineText(tcxFile.getPath());
            // Also do GPX if there is one
            gpxFile = null;
            gpx = null;
            if(doGpx && tcxgpx.gpxFile != null) {
                gpxFile = tcxgpx.gpxFile;
                manager.appendLineText(gpxFile.getPath());
                try {
                    gpx = GPXParser.parse(gpxFile);
                } catch(JAXBException ex) {
                    manager.appendLineText("Failed to parse "
                        + gpxFile.getPath() + LS + ex.getMessage());
                    continue;
                }
            }
            FileRenameDialog.Result result = doRename(tcxFile, tcx, gpxFile,
                gpx);
            if(result != FileRenameDialog.Result.OK) {
                // Utils.errMsg("Aborting remainder of TCX/GPX file renaming");
                break;
            }
        }
    }

    /**
     * Merges ele values from the given GPX to the given GpxType.
     * 
     * @param gpx
     * @param file
     * @return
     */
    public GpxType mergeEle(File file, GpxType gpx) {
        if(gpx == null || file == null) {
            return null;
        }
        GpxType gpxMerged = GPXClone.clone(gpx);
        if(gpxMerged == null) {
            manager.appendLineText("Failed to clone the input GPX");
            return null;
        }
        GpxType gpxFile = null;
        try {
            gpxFile = GPXParser.parse(file);
        } catch(JAXBException ex) {
            manager.appendLineText(
                "Failed to parse " + file.getPath() + LS + ex.getMessage());
            return null;
        }
        if(gpxFile == null) {
            manager.appendLineText("GPX is null from " + file.getPath());
            return null;
        }

        // Get the ele, time values
        List<AltitudeVal> altList = new ArrayList<>();
        BigDecimal ele;
        XMLGregorianCalendar time;
        double eleVal, timeVal;
        AltitudeVal alt;
        double lastTimeVal = Double.NEGATIVE_INFINITY;
        for(TrkType trk : gpxFile.getTrk()) {
            for(TrksegType trkseg : trk.getTrkseg()) {
                for(WptType trkpt : trkseg.getTrkpt()) {
                    ele = trkpt.getEle();
                    time = trkpt.getTime();
                    if(ele != null && time != null) {
                        timeVal = time.toGregorianCalendar().getTime()
                            .getTime();
                        // Values need to be increasing
                        if(timeVal > lastTimeVal) {
                            alt = new AltitudeVal(ele.doubleValue(), timeVal);
                            altList.add(alt);
                        }
                        lastTimeVal = timeVal;
                    }
                }
            }
        }
        if(altList.isEmpty()) {
            manager.appendLineText("No ele values in " + file.getPath());
            return null;
        }
        int nVals = altList.size();
        double[] eleVals = new double[nVals];
        double[] timeVals = new double[nVals];
        int i = 0;
        for(AltitudeVal val : altList) {
            eleVals[i] = val.ele;
            timeVals[i] = val.time;
            i++;
        }
        // // DEBUG
        // System.out.println("nVals=" + nVals);
        // int count = 10;
        // if(count > nVals) {
        // count = nVals;
        // }
        // for(int j = 0; j < count; j++) {
        // System.out.println(timeVals[j] + "," + eleVals[j]);
        // }
        // for(int j = 0; j < nVals; j++) {
        // if(j == 0) continue;
        // if(timeVals[j] <= timeVals[j - 1]) {
        // System.out.println("Vals[" + j + "] " + timeVals[j] + ","
        // + eleVals[j] + "Vals[" + (j - 1) + "] " + timeVals[j - 1]
        // + "," + eleVals[j - 1]);
        // }
        // }
        UnivariateInterpolator interp = new LinearInterpolator();
        UnivariateFunction function = null;
        try {
            function = interp.interpolate(timeVals, eleVals);
        } catch(MathIllegalArgumentException ex) {
            manager.appendLineText("Failed to create interpolator for "
                + file.getPath() + LS + ex.getMessage());
            return null;
        }
        if(function == null) {
            manager.appendLineText(
                "  Failed to create interpolator for " + file.getPath());
            return null;
        }
        // Loop over the trackpoints in gpx
        for(TrkType trk : gpxMerged.getTrk()) {
            for(TrksegType trkseg : trk.getTrkseg()) {
                for(WptType trkpt : trkseg.getTrkpt()) {
                    time = trkpt.getTime();
                    if(time != null) {
                        timeVal = time.toGregorianCalendar().getTime()
                            .getTime();
                        try {
                            eleVal = Math.round(function.value(timeVal));
                            trkpt.setEle(new BigDecimal(eleVal));
                        } catch(IllegalArgumentException ex) {
                            // No nothing
                        }
                    }
                }
            }
        }
        return gpxMerged;
    }

    private File[] getTcxFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TCX",
            "tcx");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Select TCX Files");
        if(initialSrcDir != null) {
            File file = new File(initialSrcDir);
            if(file != null && file.exists()) {
                chooser.setCurrentDirectory(file);
            }
        }
        int result = chooser.showOpenDialog(manager);
        if(result == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            // Save the selected path for next time
            initialSrcDir = chooser.getSelectedFile().getParentFile().getPath();
            setPreferences();
            return files;
        }
        return null;
    }

    /**
     * Gets the preferences from the preference store.
     */
    public void getPreferences() {
        // Use the download directory. Mabe make another preference?
        initialSrcDir = manager.getSettings().getTcxGpxDownloadDir();
    }

    /**
     * Sets the preferences to the preference store.
     */
    public void setPreferences() {
        // Don't reset any directories
        // These won't be put in the preference store until PolarAccessManager
        // exits
    }

    /**
     * Get various metadata for the given TrainingCenterDatabaseT.
     * 
     * @param tcx
     * @return
     */
    public static String getMetaData(TrainingCenterDatabaseT tcx) {
        StringBuilder sb = new StringBuilder();
        // Metadata
        String desc = TCXParser.getMetadataDescriptionFromTcx(tcx);
        sb.append("metadataDescription: " + desc);

        ActivityListT activities;
        List<ActivityT> activityList;
        TrainingT training;
        TrainingTypeT trainingType;
        SportT sport;
        PlanT plan;
        String planName, notes, sportName;
        XMLGregorianCalendar id;
        // Loop over activities (Correspond to a track)
        activities = tcx.getActivities();
        // Loop over activities
        activityList = activities.getActivity();
        int nActivities = 0;
        for(ActivityT activity : activityList) {
            nActivities++;
            sportName = "";
            desc = "";
            id = activity.getId();
            sport = activity.getSport();
            if(sport != null) {
                sportName = sport.name();
            }
            sb.append(LS + "Activity " + nActivities + ": " + id + " Sport="
                + sportName);
            planName = "";
            notes = "";
            training = activity.getTraining();
            if(training != null) {
                plan = training.getPlan();
                if(plan != null) {
                    trainingType = plan.getType();
                    desc += "Training Type: " + trainingType;
                    planName = plan.getName();
                    if(planName != null && !planName.isEmpty()) {
                        if(!desc.isEmpty()) {
                            desc += " ";
                        }
                        desc += "PlanName: " + planName;
                    }
                }
            }
            notes = activity.getNotes();
            if(notes != null && !notes.isEmpty()) {
                if(!desc.isEmpty()) {
                    desc += " ";
                }
                desc += "Notes: " + notes;
            }
            sb.append(LS + desc);

        }
        return sb.toString();
    }

    /**
     * Get various metadata for the given GpxType.
     * 
     * @param gpx
     * @return
     */
    public static String getMetaData(GpxType gpx) {
        StringBuilder sb = new StringBuilder();
        String desc = null;
        MetadataType metadata;
        List<TrkType> trkList;
        // Metadata
        metadata = gpx.getMetadata();
        if(metadata != null) {
            desc = metadata.getDesc();
        }
        sb.append("metadataDescription: " + desc);

        trkList = gpx.getTrk();
        int nTrks = 0;
        if(trkList != null) {
            for(TrkType trk : trkList) {
                nTrks++;
                sb.append(LS + "Track " + nTrks);
                desc = trk.getDesc();
                sb.append(LS + "description: " + desc);
            }
        }
        return sb.toString();
    }

    private FileRenameDialog.Result doRename(File tcxFile,
        TrainingCenterDatabaseT tcx, File gpxFile, GpxType gpx) {
        FileRenameDialog dlg = new FileRenameDialog(manager, manager, tcxFile,
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

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public class TcxGpxFile
    {
        private File tcxFile;
        private File gpxFile;
        // private GpxType gpx;

        TcxGpxFile(File tcxFile, File gpxFile) {
            this.tcxFile = tcxFile;
            this.gpxFile = gpxFile;
        }
    }

    public class AltitudeVal
    {
        private double ele;
        private double time;

        AltitudeVal(double ele, double time) {
            this.ele = ele;
            this.time = time;
        }
    }

}
