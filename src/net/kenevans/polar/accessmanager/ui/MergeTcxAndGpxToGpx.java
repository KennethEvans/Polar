package net.kenevans.polar.accessmanager.ui;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

import net.kenevans.gpxtrackpointextensionv2.GpxType;
import net.kenevans.gpxtrackpointextensionv2.TrkType;
import net.kenevans.gpxtrackpointextensionv2.TrksegType;
import net.kenevans.gpxtrackpointextensionv2.WptType;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXClone;
import net.kenevans.gpxtrackpointextensionv2.parser.GPXParser;
import net.kenevans.trainingcenterdatabasev2.TrainingCenterDatabaseT;
import net.kenevans.trainingcenterdatabasev2.parser.TCXParser;

/*
 * Created on Feb 2, 2019
 * By Kenneth Evans, Jr.
 */

public class MergeTcxAndGpxToGpx implements IConstants
{
    public static final String LS = System.getProperty("line.separator");
    PolarAccessManager manager;
    private List<TcxGpxFile> tcxGpxFiles = new ArrayList<>();
    private String initialSrcDir;
    private String initialDestDir;
    private boolean doMerge;

    MergeTcxAndGpxToGpx(PolarAccessManager manager, boolean doMerge) {
        this.manager = manager;
        this.doMerge = doMerge;
        getPreferences();
    }

    public void processTcxFiles() {
        tcxGpxFiles.clear();
        File[] tcxFiles = getTcxFiles();
        if(tcxFiles == null) {
            return;
        }

        // Get the matching file pairs
        String tcxPath, gpxPath;
        File tcxFile, gpxFile;
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
        TrainingCenterDatabaseT tcx;
        for(TcxGpxFile tcxgpx : tcxGpxFiles) {
            tcxFile = tcxgpx.tcxFile;
            try {
                tcx = TCXParser.parse(tcxFile);
            } catch(JAXBException ex) {
                manager.appendLineText("Failed to parse " + tcxFile.getPath()
                    + LS + ex.getMessage());
                continue;
            }
            GpxType gpxNew = TCXParser.convertTCXtoGpx(tcx);
            tcxgpx.gpx = gpxNew;
            // Merge with existing GPX file (that should have ele)
            if(doMerge && tcxgpx.gpxFile != null) {
                GpxType gpxMerged = mergeEle(tcxgpx.gpxFile, gpxNew);
                if(gpxMerged == null) {
                    manager.appendLineText(
                        "  Failed to merge " + tcxgpx.gpxFile.getPath());
                    continue;
                } else {
                    manager
                        .appendLineText("Merged " + tcxgpx.gpxFile.getPath());
                    tcxgpx.gpx = gpxMerged;
                }
            }
        }

        // Prompt for output directory
        File outDir = getGpxOutputDir();
        if(outDir == null) {
            manager.appendLineText("Cancelled");
            return;
        }

        // Determine how many to save
        int nSave = 0;
        for(TcxGpxFile tcxgpx : tcxGpxFiles) {
            if(tcxgpx.gpx != null) {
                nSave++;
            }
        }

        // Get ok to save
        SaveMode saveMode = SaveMode.PROMPT;
        if(nSave > 0) {
            // Prompt that files will be overwritten
            String[] buttons = {"Prompt", "Skip", "Overwrite", "Cancel"};
            int selection = JOptionPane.showOptionDialog(null,
                "Writing " + nSave + " files."
                    + "Select how to handle existing files",
                "Confirmation", JOptionPane.DEFAULT_OPTION, 0, null, buttons,
                buttons[0]);
            switch(selection) {
            case 0:
                saveMode = SaveMode.PROMPT;
                break;
            case 1:
                saveMode = SaveMode.SKIP;
                break;
            case 2:
                saveMode = SaveMode.OVERWRITE;
                break;
            default:
                manager.appendLineText("Cancelled");
                return;
            }
        } else {
            manager.appendLineText("No converted files to save");
            return;
        }

        // Write the files
        for(TcxGpxFile tcxgpx : tcxGpxFiles) {
            if(tcxgpx.gpx != null) {
                String tcxName = tcxgpx.tcxFile.getName();
                tcxPath = tcxgpx.tcxFile.getPath();
                String gpxName = null;
                gpxFile = null;
                if(tcxName.contains(".")) {
                    gpxName = tcxName.substring(0, tcxName.lastIndexOf('.'))
                        + MERGE_TCX_AND_GPX_TO_GPX_EXT;
                    gpxFile = new File(initialDestDir, gpxName);
                }
                if(gpxFile == null) {
                    manager.appendLineText("Error renaming " + tcxPath);
                    continue;
                }
                switch(saveMode) {
                case PROMPT:
                    int selection = JOptionPane.showConfirmDialog(null,
                        "File exists:" + LS + gpxFile.getPath() + LS
                            + "OK to overwrite?",
                        "File Exists", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    if(selection != JOptionPane.OK_OPTION) {
                        continue;
                    }
                    break;
                case SKIP:
                    continue;
                case OVERWRITE:
                    break;
                }
                try {
                    GPXParser.save(MERGE_TCX_AND_GPX_TO_GPX_AUTHOR, tcxgpx.gpx,
                        gpxFile);
                    manager.appendLineText("Saved " + gpxFile.getPath());
                } catch(JAXBException ex) {
                    manager.appendLineText("Error saving " + gpxFile.getPath()
                        + LS + ex.getMessage());
                }
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

    private File getGpxOutputDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select GPX Output Directory");
        if(initialDestDir != null) {
            File file = new File(initialDestDir);
            if(file != null && file.exists()) {
                chooser.setCurrentDirectory(file);
            }
        }
        int result = chooser.showOpenDialog(manager);
        if(result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Save the selected path for next time
            initialDestDir = chooser.getSelectedFile().getPath();
            setPreferences();
            return file;
        }
        return null;
    }

    /**
     * Gets the preferences from the preference store.
     */
    public void getPreferences() {
        initialSrcDir = manager.getSettings().getInitialTcxGpxSrcDir();
        initialDestDir = manager.getSettings().getInitialTcxGpxDestDir();
    }

    /**
     * Sets the preferences to the preference store.
     */
    public void setPreferences() {
        manager.getSettings().setInitialTcxGpxSrcDir(initialSrcDir);
        manager.getSettings().setInitialTcxGpxDestDir(initialDestDir);
        // These won't be put in the preference store until PolarAccessManager
        // exits
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public class TcxGpxFile
    {
        private File tcxFile;
        private File gpxFile;
        private GpxType gpx;

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
