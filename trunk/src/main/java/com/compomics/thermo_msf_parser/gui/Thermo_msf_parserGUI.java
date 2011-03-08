package com.compomics.thermo_msf_parser.gui;

import com.compomics.thermo_msf_parser.Parser;
import com.compomics.thermo_msf_parser.msf.*;
import com.compomics.thermo_msf_parser.msf.Event;
import com.compomics.thermo_msf_parser.msf.proteinsorter.ProteinSorterByAccession;
import com.compomics.thermo_msf_parser.msf.proteinsorter.ProteinSorterByNumberOfPeptides;
import com.compomics.util.Util;
import com.compomics.util.gui.spectrum.ChromatogramPanel;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyKrupp;
import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 23-Feb-2011
 * Time: 08:01:12
 */
public class Thermo_msf_parserGUI extends JFrame {
    //gui elements
    private JPanel jpanContent;
    private JTabbedPane jtabpanSpectrum;
    private JTabbedPane jtabpanLower;
    private JPanel jpanMSMS;
    private JPanel jpanMSHolder;
    private JPanel jpanPeptides;
    private JTable jtablePeptides;
    private JScrollPane jscollPeptides;
    private JCheckBox aIonsJCheckBox;
    private JCheckBox bIonsJCheckBox;
    private JCheckBox cIonsJCheckBox;
    private JCheckBox xIonsJCheckBox;
    private JCheckBox yIonsJCheckBox;
    private JCheckBox zIonsJCheckBox;
    private JCheckBox chargeOneJCheckBox;
    private JCheckBox chargeTwoJCheckBox;
    private JCheckBox chargeOverTwoJCheckBox;
    private JPanel jpanMSMSLeft;
    private JCheckBox nh3IonsJCheckBox;
    private JCheckBox h2oIonsJCheckBox;
    private JTextField txtMSMSerror;
    private JTabbedPane jtabChromatogram;
    private JPanel jpanMS;
    private JPanel jpanProtein;
    private JPanel jpanQuantitationSpectrum;
    private JPanel jpanQuantificationSpectrumHolder;
    private JButton showAllPeptidesButton;
    private JList proteinList;
    private JButton jbuttonNumberSort;
    private JButton jbuttonAlphabeticalSort;
    private JPanel jpanProteinLeft;
    private JScrollPane proteinCoverageJScrollPane;
    private JLabel sequenceCoverageJLabel;
    private JCheckBox chbHighConfident;
    private JCheckBox chbMediumConfident;
    private JCheckBox chbLowConfidence;
    private JProgressBar progressBar;
    private JCheckBox onlyHighestScoringCheckBox;
    private JCheckBox onlyLowestScoringCheckBox;
    private JCheckBox allCheckBox;
    private JEditorPane proteinSequenceCoverageJEditorPane;
    private SpectrumPanel iMSMSspectrumPanel;
    private SpectrumPanel iMSspectrumPanel;
    private SpectrumPanel iQuantificationSpectrumPanel;

    /**
     * A vector with the absolute pahts to the msf file
     */
    private Vector<String> iMsfFileLocations = new Vector<String>();
    /**
     * A Vector with the parsed msf files
     */
    private Vector<Parser> iParsedMsfs = new Vector<Parser>();
    /**
     * A Vector with the different scoretypes found in the different files
     */
    private Vector<ScoreType> iMergedPeptidesScores;
    /**
     * The currently selected peptide
     */
    private Peptide iSelectedPeptide;
    /**
     * The currently selected protein
     */
    private Protein iSelectedProtein;
    /**
     * The msms fragmentation error
     */
    private double iMSMSerror = 0.5;
    /**
     * Vector with all the proteins
     */
    private Vector<Protein> iProteins = new Vector<Protein>();
    /**
     * Vector with all the proteins displayed in the protein list
     */
    private Vector<Protein> iDisplayedProteins = new Vector<Protein>();
    /**
     * A hashmap with the protein accession as key and the protein as value
     */
    private HashMap<String, Protein> iProteinsMap = new HashMap<String, Protein>();
    /**
     * The different custom data fields used for peptides in all the files
     */
    private Vector<CustomDataField> iMergedCustomPeptideData;
    /**
     * The different custom data fields used for spectra in all the files
     */
    private Vector<CustomDataField> iMergedCustomSpectrumData;
    /**
     * The different ratio types found in the msf files
     */
    private Vector<RatioType> iMergedRatioTypes;
    /**
     * The major score type
     */
    private ScoreType iMajorScoreType;


    /**
     * The constructor
     */
    public Thermo_msf_parserGUI() {


        //create the gui
        jtablePeptides = new JTable();
        jscollPeptides = new JScrollPane();
        chbHighConfident = new JCheckBox("High");
        chbHighConfident.setSelected(true);
        chbMediumConfident = new JCheckBox("Medium");
        chbMediumConfident.setSelected(true);
        chbLowConfidence = new JCheckBox("Low");
        onlyHighestScoringCheckBox = new JCheckBox();
        onlyLowestScoringCheckBox = new JCheckBox();
        allCheckBox = new JCheckBox();
        proteinList = new JList(iDisplayedProteins);
        $$$setupUI$$$();

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create a menu
        final JMenu lExportMenu = new JMenu("Export");
        menuBar.add(lExportMenu);

        // Create a menu item
        final JMenuItem item = new JMenuItem("Export peptides as csv");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Export peptides csv");
                //check if we can export anything

                //open file chooser
                final String lPath;
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(getFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (fc.getSelectedFile().getAbsolutePath().endsWith(".csv")) {
                        lPath = fc.getSelectedFile().getAbsolutePath();
                    } else {
                        lPath = fc.getSelectedFile().getAbsolutePath() + ".csv";
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Save command cancelled by user.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                com.compomics.util.sun.SwingWorker lCsvSaver = new com.compomics.util.sun.SwingWorker() {
                    public Boolean construct() {
                        //create the writer
                        try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(lPath));

                            //write column headers
                            String lLine = "";
                            for (int j = 0; j < jtablePeptides.getModel().getColumnCount(); j++) {

                                lLine = lLine + jtablePeptides.getModel().getColumnName(j) + ",";
                            }
                            out.write(lLine + "\n");

                            //write data
                            for (int i = 0; i < jtablePeptides.getModel().getRowCount(); i++) {
                                lLine = "";
                                for (int j = 0; j < jtablePeptides.getModel().getColumnCount(); j++) {

                                    lLine = lLine + jtablePeptides.getModel().getValueAt(i, j) + ",";
                                }
                                out.write(lLine + "\n");
                            }

                            out.flush();
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(new JFrame(), "There was a problem saving your data!", "Problem saving", JOptionPane.ERROR_MESSAGE);
                        }


                        return true;
                    }

                    public void finished() {
                        JOptionPane.showMessageDialog(new JFrame(), "Saving done", "Info", JOptionPane.INFORMATION_MESSAGE);

                    }

                };
                lCsvSaver.start();


            }
        }


        );


        // Create a menu item
        final JMenuItem lItemMgf = new JMenuItem("Export spectra as mgf");
        lItemMgf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Export spectra mgf");

                //open file chooser
                final String lPath;
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(getFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (fc.getSelectedFile().getAbsolutePath().endsWith(".mgf")) {
                        lPath = fc.getSelectedFile().getAbsolutePath();
                    } else {
                        lPath = fc.getSelectedFile().getAbsolutePath() + ".mgf";
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Save command cancelled by user.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }


                com.compomics.util.sun.SwingWorker lMgfSaver = new com.compomics.util.sun.SwingWorker() {
                    public Boolean construct() {
                        //create the writer
                        try {
                            int lTotalSpecrtra = 0;
                            for (int i = 0; i < iParsedMsfs.size(); i++) {
                                lTotalSpecrtra = lTotalSpecrtra + iParsedMsfs.get(i).getSpectra().size();
                            }

                            progressBar.setMaximum(lTotalSpecrtra);
                            progressBar.setValue(0);
                            progressBar.setString("Writting all spectra to " + lPath);
                            progressBar.setStringPainted(true);
                            progressBar.setVisible(true);
                            //progressBar.updateUI();
                            BufferedWriter out = new BufferedWriter(new FileWriter(lPath));


                            for (int i = 0; i < iParsedMsfs.size(); i++) {
                                for (int j = 0; j < iParsedMsfs.get(i).getSpectra().size(); j++) {
                                    Spectrum lSpectrum = iParsedMsfs.get(i).getSpectra().get(j);
                                    String lSpectrumLine = "BEGIN IONS\nTITLE=" + lSpectrum.getSpectrumTitle() + "\n";
                                    Peak lMono = lSpectrum.getMonoisotopicPeakCentroidsPeak();
                                    lSpectrumLine = lSpectrumLine + "PEPMASS=" + lMono.getX() + "\t" + lMono.getY() + "\n";
                                    lSpectrumLine = lSpectrumLine + "CHARGE=" + lSpectrum.getCharge() + "+\n";
                                    lSpectrumLine = lSpectrumLine + "RTINSECONDS=" + (lSpectrum.getRetentionTime() / 60.0) + "\n";
                                    if (lSpectrum.getFirstScan() != lSpectrum.getFirstScan()) {
                                        lSpectrumLine = lSpectrumLine + "SCANS=" + lSpectrum.getFirstScan() + "." + lSpectrum.getLastScan() + "\n";
                                    } else {
                                        lSpectrumLine = lSpectrumLine + "SCANS=" + lSpectrum.getFirstScan() + "\n";
                                    }
                                    Vector<Peak> lMSMS = lSpectrum.getMSMSPeaks();
                                    for (int k = 0; k < lMSMS.size(); k++) {
                                        lSpectrumLine = lSpectrumLine + lMSMS.get(k).getX() + "\t" + lMSMS.get(k).getY() + "\n";
                                    }
                                    lSpectrumLine = lSpectrumLine + "END IONS\n\n";

                                    out.write(lSpectrumLine);
                                    progressBar.setValue(progressBar.getValue() + 1);
                                    //progressBar.updateUI();
                                }


                            }

                            out.flush();
                            out.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            progressBar.setVisible(false);
                            JOptionPane.showMessageDialog(new JFrame(), "There was a problem saving your data!", "Problem saving", JOptionPane.ERROR_MESSAGE);
                        }


                        return true;
                    }

                    public void finished() {
                        JOptionPane.showMessageDialog(new JFrame(), "Saving done", "Info", JOptionPane.INFORMATION_MESSAGE);
                        progressBar.setVisible(false);
                        progressBar.setStringPainted(false);
                        //progressBar.updateUI();
                    }

                };
                lMgfSaver.start();
                progressBar.setString("");


            }
        }

        );


        // Install the menu bar in the frame
        this.setJMenuBar(menuBar);
        //create JFrame parameters
        this.setTitle("Thermo msf parser GUI");
        this.setContentPane(jpanContent);
        this.setSize(1200, 800);
        this.setLocation(150, 150);
        this.setVisible(true);


        //add a closing window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing
                    (WindowEvent
                            evt) {
                for (int i = 0; i < iParsedMsfs.size(); i++) {
                    try {
                        iParsedMsfs.get(i).getConnection().close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                System.exit(0);

            }
        }

        );


        progressBar.setVisible(false);


        //open a new thread to parse the files found by the file chooser
        com.compomics.util.sun.SwingWorker lParser = new com.compomics.util.sun.SwingWorker() {
            public Boolean construct() {
                try {

                    //open file chooser
                    JFileChooser fc = new JFileChooser();
                    fc.setMultiSelectionEnabled(true);
                    //create the file filter to choose
                    FileFilter lFilter = new MsfFileFilter();
                    fc.setFileFilter(lFilter);
                    fc.showOpenDialog(getFrame());
                    File[] lFiles = fc.getSelectedFiles();
                    for (int i = 0; i < lFiles.length; i++) {
                        iMsfFileLocations.add(lFiles[i].getAbsolutePath());
                    }
                    if (lFiles.length > 1) {
                        JOptionPane.showMessageDialog(getFrame(), "The workflow of the differnt msf files that are loaded must be the same.\nUnexpected crashes can occur if files with different workflows are loaded!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }

                    progressBar.setVisible(true);
                    progressBar.setStringPainted(true);
                    progressBar.setMaximum(iMsfFileLocations.size());
                    //parse the msf files
                    for (int i = 0; i < iMsfFileLocations.size(); i++) {
                        try {
                            progressBar.setValue(i + 1);
                            progressBar.setString("Parsing: " + iMsfFileLocations.get(i));
                            //progressBar.updateUI();
                            iParsedMsfs.add(new Parser(iMsfFileLocations.get(i)));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            iParsedMsfs.add(null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            iParsedMsfs.add(null);
                        }
                    }


                } catch (Exception e1) {
                    e1.printStackTrace();
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(new JFrame(), "There was a problem saving your data!", "Problem saving", JOptionPane.ERROR_MESSAGE);
                }

                //create the peptide table
                createPeptideTable(null);

                //add the menuitems to the menu
                lExportMenu.add(item);
                lExportMenu.add(lItemMgf);

                //add action listeners
                showAllPeptidesButton.addActionListener(new

                        ActionListener() {
                            public void actionPerformed
                                    (ActionEvent
                                            e) {
                                iSelectedProtein = null;
                                proteinSequenceCoverageJEditorPane.setText("");
                                sequenceCoverageJLabel.setText("");
                                createPeptideTable(null);
                                iDisplayedProteins.removeAllElements();
                                for (int i = 0; i < iProteins.size(); i++) {
                                    iDisplayedProteins.add(iProteins.get(i));
                                }
                                filterDisplayedProteins();
                                proteinList.updateUI();
                            }
                        }

                );
                jtabpanLower.remove(jpanQuantificationSpectrumHolder);

                jbuttonAlphabeticalSort.addActionListener(new

                        ActionListener() {
                            public void actionPerformed
                                    (ActionEvent
                                            e) {
                                Collections.sort(iDisplayedProteins, new ProteinSorterByAccession());
                                filterDisplayedProteins();
                                proteinList.updateUI();
                            }
                        }

                );
                jbuttonNumberSort.addActionListener(new

                        ActionListener() {
                            public void actionPerformed
                                    (ActionEvent
                                            e) {
                                Collections.sort(iDisplayedProteins, new ProteinSorterByNumberOfPeptides());
                                filterDisplayedProteins();
                                proteinList.updateUI();
                            }
                        }

                );
                ActionListener chbChangeActionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        createPeptideTable(iSelectedProtein);
                        if (iSelectedProtein != null) {
                            formatProteinSequence(iSelectedProtein);
                        }
                        filterDisplayedProteins();
                        proteinList.updateUI();

                    }
                };
                chbHighConfident.addActionListener(chbChangeActionListener);
                chbMediumConfident.addActionListener(chbChangeActionListener);
                chbLowConfidence.addActionListener(chbChangeActionListener);
                allCheckBox.addActionListener(chbChangeActionListener);
                onlyHighestScoringCheckBox.addActionListener(chbChangeActionListener);
                onlyLowestScoringCheckBox.addActionListener(chbChangeActionListener);

                progressBar.setVisible(false);
                progressBar.setString("");
                progressBar.setStringPainted(false);

                return true;
            }

            public void finished() {
                //give a message to the user that everything is loaded
                JOptionPane.showMessageDialog(new JFrame(), "All data was loaded", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        };
        lParser.start();


    }

    /**
     * Getter for the JFrame
     *
     * @return JFrame
     */
    public JFrame getFrame() {
        return this;
    }

    /**
     * This method will filter the proteins in the protein list. It will
     * look if proteins still need to be displayed after that the peptide
     * confidence level is changed
     */
    public void filterDisplayedProteins() {
        Vector<Protein> lProteinsToRemove = new Vector<Protein>();
        for (Protein iDisplayedProtein : iDisplayedProteins) {
            Protein lProtein = iDisplayedProtein;
            boolean lDisplay = false;
            //check if there is one peptide that will be displayed
            for (int i = 0; i < lProtein.getPeptides().size(); i++) {
                Peptide lPeptide = lProtein.getPeptides().get(i);
                int lConfidenceLevel = lPeptide.getConfidenceLevel();
                if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                    lDisplay = true;
                }
                if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                    lDisplay = true;
                }
                if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                    lDisplay = true;
                }
                if (onlyHighestScoringCheckBox.isSelected()) {
                    if (!lPeptide.getParentSpectrum().isHighestScoring(lPeptide, iMajorScoreType)) {
                        lDisplay = false;
                    }
                }
                if (onlyLowestScoringCheckBox.isSelected()) {
                    if (!lPeptide.getParentSpectrum().isLowestScoring(lPeptide, iMajorScoreType)) {
                        lDisplay = false;
                    }
                }

            }
            if (!lDisplay) {
                lProteinsToRemove.add(lProtein);
            }
        }
        for (int i = 0; i < lProteinsToRemove.size(); i++) {
            iDisplayedProteins.remove(lProteinsToRemove.get(i));
        }
    }

    /**
     * This method will create the peptide table based on the given protein.
     * If no protein is given (null) all peptides found in the different msf
     * files will be displayed
     *
     * @param lProtein The protein to display the peptides off
     */
    private void createPeptideTable(Protein lProtein) {

        //some gui stuff
        if (chbHighConfident == null) {
            chbHighConfident = new JCheckBox("High");
            chbHighConfident.setSelected(true);
            chbMediumConfident = new JCheckBox("Medium");
            chbMediumConfident.setSelected(true);
            chbLowConfidence = new JCheckBox("Low");
        }

        //create holders for the different columns
        Vector<String> lPeptideTableColumnsTitleVector = new Vector<String>();
        Vector<Boolean> lPeptideTableColumnsEditableVector = new Vector<Boolean>();
        Vector<Class> lPeptideTableColumnsClassVector = new Vector<Class>();
        Vector<Object[]> lPeptidesVector = new Vector<Object[]>();

        //add different columns to the holders
        lPeptideTableColumnsTitleVector.add("Confidence level");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Integer.class);

        lPeptideTableColumnsTitleVector.add("Spectrum title");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Integer.class);

        lPeptideTableColumnsTitleVector.add("Sequence");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Peptide.class);

        lPeptideTableColumnsTitleVector.add("Modified sequence");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(String.class);

        //get the different score types and add it as columns
        if (iMergedPeptidesScores == null) {
            this.collectPeptideScoreTypes();
            //set the major score type
            for (int i = 0; i < iMergedPeptidesScores.size(); i++) {
                if (iMergedPeptidesScores.get(i).getIsMainScore() == 1) {
                    iMajorScoreType = iMergedPeptidesScores.get(i);
                }
            }
        }
        for (int i = 0; i < iMergedPeptidesScores.size(); i++) {
            lPeptideTableColumnsTitleVector.add(iMergedPeptidesScores.get(i).getFriendlyName());
            lPeptideTableColumnsEditableVector.add(false);
            lPeptideTableColumnsClassVector.add(Double.class);
        }


        lPeptideTableColumnsTitleVector.add("Matched ions / Total ions");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(String.class);

        lPeptideTableColumnsTitleVector.add("m/z [Da]");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Double.class);

        lPeptideTableColumnsTitleVector.add("MH+ [Da]");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Double.class);

        lPeptideTableColumnsTitleVector.add("Charge");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Integer.class);

        lPeptideTableColumnsTitleVector.add("Retention time");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Double.class);

        lPeptideTableColumnsTitleVector.add("First scan");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Integer.class);


        lPeptideTableColumnsTitleVector.add("Last scan");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(Integer.class);


        lPeptideTableColumnsTitleVector.add("Annotation");
        lPeptideTableColumnsEditableVector.add(false);
        lPeptideTableColumnsClassVector.add(String.class);

        //get the ratiotypes and add it as columns
        if (iMergedRatioTypes == null) {
            this.collectRatioTypes();
        }
        for (int i = 0; i < iMergedRatioTypes.size(); i++) {
            lPeptideTableColumnsTitleVector.add(iMergedRatioTypes.get(i).getRatioType());
            lPeptideTableColumnsEditableVector.add(false);
            lPeptideTableColumnsClassVector.add(double.class);
        }

        //get the custom peptide data and add it as columns
        if (iMergedCustomPeptideData == null) {
            this.collectCustomPeptideData();
        }
        for (int i = 0; i < iMergedCustomPeptideData.size(); i++) {
            lPeptideTableColumnsTitleVector.add(iMergedCustomPeptideData.get(i).getName());
            lPeptideTableColumnsEditableVector.add(false);
            lPeptideTableColumnsClassVector.add(String.class);
        }

        //get the custom spectra data and add it as columns
        if (iMergedCustomSpectrumData == null) {
            this.collectCustomSpectrumData();
        }
        for (int i = 0; i < iMergedCustomSpectrumData.size(); i++) {
            lPeptideTableColumnsTitleVector.add(iMergedCustomSpectrumData.get(i).getName());
            lPeptideTableColumnsEditableVector.add(false);
            lPeptideTableColumnsClassVector.add(String.class);
        }

        //find the peptides to display
        if (lProtein == null) {
            lPeptidesVector = this.collectPeptides(lPeptidesVector);
        } else {
            lPeptidesVector = this.collectPeptidesFromProtein(lPeptidesVector, lProtein);
        }

        //create the arrays for the table model
        String[] lPeptideTableColumnsTitle = new String[lPeptideTableColumnsTitleVector.size()];
        lPeptideTableColumnsTitleVector.toArray(lPeptideTableColumnsTitle);
        final Boolean[] lPeptideTableColumnsEditable = new Boolean[lPeptideTableColumnsEditableVector.size()];
        lPeptideTableColumnsEditableVector.toArray(lPeptideTableColumnsEditable);
        final Class[] lPeptideTableColumnsClass = new Class[lPeptideTableColumnsClassVector.size()];
        lPeptideTableColumnsClassVector.toArray(lPeptideTableColumnsClass);
        final Object[][] lPeptides = new Object[lPeptidesVector.size()][];
        lPeptidesVector.toArray(lPeptides);

        //create the table model
        DefaultTableModel jtablePeptideModel = new DefaultTableModel(
                lPeptides,
                lPeptideTableColumnsTitle) {

            Class[] types = lPeptideTableColumnsClass;
            Boolean[] canEdit = lPeptideTableColumnsEditable;

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        jtablePeptides = new JTable();
        jtablePeptides.setModel(jtablePeptideModel);

        //set some cell renderers
        jtablePeptides.getColumn("Confidence level").setCellRenderer(new ConfidenceLevelTableCellRenderer());

        double lLowRT = Double.MAX_VALUE;
        double lHighRT = Double.MIN_VALUE;

        for (int p = 0; p < lPeptides.length; p++) {
            Peptide lPeptide = (Peptide) lPeptides[p][2];
            if (lLowRT > lPeptide.getParentSpectrum().getRetentionTime()) {
                lLowRT = lPeptide.getParentSpectrum().getRetentionTime();
            }
            if (lHighRT < lPeptide.getParentSpectrum().getRetentionTime()) {
                lHighRT = lPeptide.getParentSpectrum().getRetentionTime();
            }
        }

        JSparklinesBarChartTableCellRenderer lRTCellRenderer = new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, lLowRT, lHighRT, Color.YELLOW, Color.BLUE);
        jtablePeptides.getColumn("Retention time").setCellRenderer(lRTCellRenderer);
        lRTCellRenderer.showNumberAndChart(true, 50);

        for (int i = 0; i < iMergedPeptidesScores.size(); i++) {
            double lLowScore = Double.MAX_VALUE;
            double lHighScore = Double.MIN_VALUE;

            for (int p = 0; p < lPeptides.length; p++) {
                double lScore = (Double) lPeptides[p][4 + i];
                if (lLowScore > lScore) {
                    lLowScore = lScore;
                }
                if (lHighScore < lScore) {
                    lHighScore = lScore;
                }
            }


            JSparklinesBarChartTableCellRenderer lScoreCellRenderer;
            lScoreCellRenderer = new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, lLowScore, lHighScore, Color.RED, Color.GREEN);
            jtablePeptides.getColumn(iMergedPeptidesScores.get(i).getFriendlyName()).setCellRenderer(lScoreCellRenderer);
            lScoreCellRenderer.showNumberAndChart(true, 50);

            //lPeptideTableColumnsTitleVector.add(iMergedPeptidesScores.get(i).getFriendlyName());
            //lPeptideTableColumnsEditableVector.add(false);
            //lPeptideTableColumnsClassVector.add(Double.class);
        }


        jtablePeptides.setOpaque(false);
        jtablePeptides.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                peptidesTableKeyReleased(evt);
            }
        });
        jtablePeptides.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                peptidesTableMouseClicked(evt);
            }
        });
        if (jscollPeptides == null) {
            jscollPeptides = new JScrollPane();
        }

        jscollPeptides.setViewportView(jtablePeptides);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jtablePeptideModel);
        jtablePeptides.setRowSorter(sorter);
        jtablePeptides.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jtablePeptides.updateUI();
    }

    /**
     * This method will change the ms ms fragmentation error
     */
    private void changeMSMSerror() {
        String lMsMsError = txtMSMSerror.getText();
        try {
            iMSMSerror = Double.valueOf(lMsMsError);
        } catch (NumberFormatException e) {
            txtMSMSerror.setText(String.valueOf(iMSMSerror));
            JOptionPane.showMessageDialog(this, lMsMsError + " is not a valid value!", "Number error", JOptionPane.WARNING_MESSAGE);
        }

        //get the selected peptide
        if (iSelectedPeptide != null) {
            setSpectrumMSMSAnnotations(iSelectedPeptide);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        jpanContent = new JPanel();
        jpanContent.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(panel1, gbc);
        showAllPeptidesButton = new JButton();
        showAllPeptidesButton.setMinimumSize(new Dimension(150, 25));
        showAllPeptidesButton.setText("Show all");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(showAllPeptidesButton, gbc);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(500);
        splitPane1.setOneTouchExpandable(true);
        splitPane1.setOrientation(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(splitPane1, gbc);
        jtabpanLower = new JTabbedPane();
        splitPane1.setRightComponent(jtabpanLower);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        jtabpanLower.addTab("Chromatogram", panel2);
        jtabChromatogram = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(jtabChromatogram, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        jtabChromatogram.addTab("No chromatogram loaded", panel3);
        jpanMSHolder = new JPanel();
        jpanMSHolder.setLayout(new GridBagLayout());
        jtabpanLower.addTab("MS spectrum", jpanMSHolder);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanMSHolder.add(jpanMS, gbc);
        jpanQuantificationSpectrumHolder = new JPanel();
        jpanQuantificationSpectrumHolder.setLayout(new GridBagLayout());
        jtabpanLower.addTab("Quantification spectrum", jpanQuantificationSpectrumHolder);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanQuantificationSpectrumHolder.add(jpanQuantitationSpectrum, gbc);
        jpanMSMS = new JPanel();
        jpanMSMS.setLayout(new GridBagLayout());
        jtabpanLower.addTab("MS/MS spectrum", jpanMSMS);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.95;
        gbc.weighty = 0.95;
        gbc.fill = GridBagConstraints.BOTH;
        jpanMSMS.add(jpanMSMSLeft, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanMSMS.add(panel4, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(aIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(bIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(cIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(xIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(yIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(zIonsJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(chargeOneJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(chargeTwoJCheckBox, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(chargeOverTwoJCheckBox, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(separator1, gbc);
        final JSeparator separator2 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(separator2, gbc);
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(separator3, gbc);
        nh3IonsJCheckBox.setMinimumSize(new Dimension(70, 22));
        nh3IonsJCheckBox.setText("NH3");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(nh3IonsJCheckBox, gbc);
        h2oIonsJCheckBox.setText("H20");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(h2oIonsJCheckBox, gbc);
        txtMSMSerror.setText("0.5");
        txtMSMSerror.setToolTipText("The MS/MS fragmentation error (in Da)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel4.add(txtMSMSerror, gbc);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setOneTouchExpandable(true);
        splitPane1.setLeftComponent(splitPane2);
        jtabpanSpectrum = new JTabbedPane();
        splitPane2.setRightComponent(jtabpanSpectrum);
        jpanProtein = new JPanel();
        jpanProtein.setLayout(new GridBagLayout());
        jtabpanSpectrum.addTab("Protein", jpanProtein);
        jpanProteinLeft = new JPanel();
        jpanProteinLeft.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanProtein.add(jpanProteinLeft, gbc);
        jpanProteinLeft.setBorder(BorderFactory.createTitledBorder("Protein coverage"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanProteinLeft.add(proteinCoverageJScrollPane, gbc);
        sequenceCoverageJLabel = new JLabel();
        sequenceCoverageJLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanProteinLeft.add(sequenceCoverageJLabel, gbc);
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        splitPane2.setLeftComponent(tabbedPane1);
        jpanPeptides = new JPanel();
        jpanPeptides.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Peptides", jpanPeptides);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanPeptides.add(jscollPeptides, gbc);
        jscollPeptides.setViewportView(jtablePeptides);
        chbHighConfident.setSelected(true);
        chbHighConfident.setText("High");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(chbHighConfident, gbc);
        onlyHighestScoringCheckBox.setSelected(false);
        onlyHighestScoringCheckBox.setText("Only highest scoring");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(onlyHighestScoringCheckBox, gbc);
        chbMediumConfident.setSelected(true);
        chbMediumConfident.setText("Medium");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(chbMediumConfident, gbc);
        chbLowConfidence.setText("Low");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(chbLowConfidence, gbc);
        allCheckBox.setSelected(true);
        allCheckBox.setText("All");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(allCheckBox, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Peptide confidence level: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Peptide spectrum match: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(label2, gbc);
        onlyLowestScoringCheckBox.setSelected(false);
        onlyLowestScoringCheckBox.setText("Only lowest scoring");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanPeptides.add(onlyLowestScoringCheckBox, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel5, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(scrollPane1, gbc);
        scrollPane1.setViewportView(proteinList);
        jbuttonAlphabeticalSort = new JButton();
        jbuttonAlphabeticalSort.setText("A -> Z");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(jbuttonAlphabeticalSort, gbc);
        jbuttonNumberSort = new JButton();
        jbuttonNumberSort.setText("1 -> 20");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel5.add(jbuttonNumberSort, gbc);
        progressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(progressBar, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(onlyHighestScoringCheckBox);
        buttonGroup.add(allCheckBox);
        buttonGroup.add(onlyLowestScoringCheckBox);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanContent;
    }

    /**
     * This is a cell renderer that will set the background color of a cell by the confidence level (an int)
     */
    public class ConfidenceLevelTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            Integer lValue = (Integer) obj;
            if (lValue == 1) {
                cell.setBackground(Color.RED);
            }
            if (lValue == 2) {
                cell.setBackground(Color.ORANGE);
            }
            if (lValue == 3) {
                cell.setBackground(Color.GREEN);
            }
            return cell;
        }
    }

    /**
     * This method will initiate a spectrum annotation if a peptide is selected
     */
    private void ionsJCheckBoxActionPerformed() {
        if (iSelectedPeptide != null) {
            setSpectrumMSMSAnnotations(iSelectedPeptide);
        }
    }

    /**
     * This method will generete the object for the peptide table, based on a given protein
     *
     * @param lPeptides Vector to add the peptide line objects to it
     * @param lProtein  The selected protein
     * @return Vector with the peptide line objects
     */
    private Vector<Object[]> collectPeptidesFromProtein(Vector<Object[]> lPeptides, Protein lProtein) {

        for (int p = 0; p < lProtein.getPeptides().size(); p++) {
            Peptide lPeptide = lProtein.getPeptides().get(p);
            int lConfidenceLevel = lPeptide.getConfidenceLevel();
            boolean lUse = false;
            if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                lUse = true;
            }
            if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                lUse = true;
            }
            if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                lUse = true;
            }
            if (onlyHighestScoringCheckBox.isSelected()) {
                if (!lPeptide.getParentSpectrum().isHighestScoring(lPeptide, iMajorScoreType)) {
                    lUse = false;
                }
            }
            if (onlyLowestScoringCheckBox.isSelected()) {
                if (!lPeptide.getParentSpectrum().isLowestScoring(lPeptide, iMajorScoreType)) {
                    lUse = false;
                }
            }

            if (lUse) {
                //only add the peptide line if we need to use it
                lPeptides.add(createPeptideLine(lPeptide).toArray());
            }

        }

        return lPeptides;
    }

    /**
     * This method will generete the object for the peptide table for all the peptides
     *
     * @param lPeptides Vector to add the peptide line objects to it
     * @return Vector with the peptide line objects
     */
    private Vector<Object[]> collectPeptides(Vector<Object[]> lPeptides) {
        boolean lCreateProteins = false;
        if (iProteins.size() == 0) {
            //The proteins are not created yet, so we need to create them
            lCreateProteins = true;
            iProteins = new Vector<Protein>();
        }

        for (int i = 0; i < iParsedMsfs.size(); i++) {
            for (int p = 0; p < iParsedMsfs.get(i).getPeptides().size(); p++) {
                Peptide lPeptide = iParsedMsfs.get(i).getPeptides().get(p);
                int lConfidenceLevel = lPeptide.getConfidenceLevel();
                boolean lUse = false;
                if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                    lUse = true;
                }
                if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                    lUse = true;
                }
                if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                    lUse = true;
                }
                if (onlyHighestScoringCheckBox.isSelected()) {
                    if (!lPeptide.getParentSpectrum().isHighestScoring(lPeptide, iMajorScoreType)) {
                        lUse = false;
                    }
                }
                if (onlyLowestScoringCheckBox.isSelected()) {
                    if (!lPeptide.getParentSpectrum().isLowestScoring(lPeptide, iMajorScoreType)) {
                        lUse = false;
                    }
                }

                if (lUse) {
                    //only add the peptide line if we need to use it
                    lPeptides.add(createPeptideLine(lPeptide).toArray());
                }

                if (lCreateProteins) {
                    //check and possibly create the protein
                    Vector<Protein> lCoupledProteins = lPeptide.getProteins();
                    for (int r = 0; r < lCoupledProteins.size(); r++) {
                        Protein lCoupledProtein = lCoupledProteins.get(r);
                        if (iProteinsMap.get(lCoupledProtein.getDescription()) == null) {
                            Protein lNewProtein = new Protein(-1, lCoupledProtein.getSequence());
                            lNewProtein.setDescription(lCoupledProtein.getDescription());
                            iProteinsMap.put(lNewProtein.getDescription(), lNewProtein);
                            iProteins.add(lNewProtein);
                            iDisplayedProteins.add(lNewProtein);

                            lNewProtein.addPeptide(lPeptide);
                        } else {
                            iProteinsMap.get(lCoupledProtein.getDescription()).addPeptide(lPeptide);
                        }
                    }
                }
            }
        }
        if (lCreateProteins) {
            //create the protein list and add the listeners
            proteinList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent me) {
                    if (me.getButton() == 1) {
                        loadProtein();
                    }
                }
            });

            proteinList.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        loadProtein();
                    }
                }

            });
            this.filterDisplayedProteins();
            proteinList.updateUI();
        }
        return lPeptides;
    }

    /**
     * This method will create a peptide line vector for the peptide table
     *
     * @param lPeptide The peptide to create the peptide line for
     * @return Vector with the objects of the peptide line
     */
    public Vector<Object> createPeptideLine(Peptide lPeptide) {
        Vector<Object> lPeptideObject = new Vector<Object>();
        lPeptideObject.add(lPeptide.getConfidenceLevel());
        lPeptideObject.add(lPeptide.getParentSpectrum().getSpectrumTitle());
        lPeptideObject.add(lPeptide);
        lPeptideObject.add(lPeptide.getModifiedPeptide());
        for (int j = 0; j < iMergedPeptidesScores.size(); j++) {
            Double lScore = lPeptide.getScoreByScoreType(iMergedPeptidesScores.get(j));
            lPeptideObject.add(lScore);
        }

        lPeptideObject.add(lPeptide.getMatchedIonsCount() + "/" + lPeptide.getTotalIonsCount());
        lPeptideObject.add(lPeptide.getParentSpectrum().getMz());
        lPeptideObject.add(lPeptide.getParentSpectrum().getSinglyChargedMass());
        lPeptideObject.add(lPeptide.getParentSpectrum().getCharge());
        lPeptideObject.add(lPeptide.getParentSpectrum().getRetentionTime());
        lPeptideObject.add(lPeptide.getParentSpectrum().getFirstScan());
        lPeptideObject.add(lPeptide.getParentSpectrum().getLastScan());
        lPeptideObject.add(lPeptide.getAnnotation());
        for (int i = 0; i < iMergedRatioTypes.size(); i++) {
            if (lPeptide.getParentSpectrum().getQuanResult() != null) {
                lPeptideObject.add(lPeptide.getParentSpectrum().getQuanResult().getRatioByRatioType(iMergedRatioTypes.get(i)));
            } else {
                lPeptideObject.add(null);
            }
        }
        for (int j = 0; j < iMergedCustomPeptideData.size(); j++) {
            lPeptideObject.add(lPeptide.getCustomDataFieldValues().get(iMergedCustomPeptideData.get(j).getFieldId()));
        }
        for (int j = 0; j < iMergedCustomSpectrumData.size(); j++) {
            lPeptideObject.add(lPeptide.getParentSpectrum().getCustomDataFieldValues().get(iMergedCustomSpectrumData.get(j).getFieldId()));
        }

        return lPeptideObject;

    }

    /**
     * This method will collect the score types used in the different msf files
     */
    private void collectPeptideScoreTypes() {
        iMergedPeptidesScores = new Vector<ScoreType>();
        for (int i = 0; i < iParsedMsfs.size(); i++) {
            for (int j = 0; j < iParsedMsfs.get(i).getScoreTypes().size(); j++) {
                boolean lFound = false;
                for (int k = 0; k < iMergedPeptidesScores.size(); k++) {
                    if (iParsedMsfs.get(i).getScoreTypes().get(j).getDescription().equalsIgnoreCase(iMergedPeptidesScores.get(k).getDescription())) {
                        lFound = true;
                    }
                }
                if (!lFound) {
                    iMergedPeptidesScores.add(iParsedMsfs.get(i).getScoreTypes().get(j));
                }
            }
        }
    }

    /**
     * This method will collect the ratiotypes used in the different msf files
     */
    private void collectRatioTypes() {
        iMergedRatioTypes = new Vector<RatioType>();
        for (int i = 0; i < iParsedMsfs.size(); i++) {
            for (int j = 0; j < iParsedMsfs.get(i).getRatioTypes().size(); j++) {
                boolean lFound = false;
                for (int k = 0; k < iMergedRatioTypes.size(); k++) {
                    if (iParsedMsfs.get(i).getRatioTypes().get(j).getRatioType().equalsIgnoreCase(iMergedRatioTypes.get(k).getRatioType())) {
                        lFound = true;
                    }
                }
                if (!lFound) {
                    iMergedRatioTypes.add(iParsedMsfs.get(i).getRatioTypes().get(j));
                }
            }
        }

    }

    /**
     * This method will collect the custom peptide data used in the different msf files
     */
    private void collectCustomPeptideData() {
        iMergedCustomPeptideData = new Vector<CustomDataField>();
        for (int i = 0; i < iParsedMsfs.size(); i++) {
            for (int j = 0; j < iParsedMsfs.get(i).getPeptideUsedCustomDataFields().size(); j++) {
                boolean lFound = false;
                for (int k = 0; k < iMergedCustomPeptideData.size(); k++) {
                    if (iParsedMsfs.get(i).getPeptideUsedCustomDataFields().get(j).getName().equalsIgnoreCase(iMergedCustomPeptideData.get(k).getName())) {
                        lFound = true;
                    }
                }
                if (!lFound) {
                    iMergedCustomPeptideData.add(iParsedMsfs.get(i).getPeptideUsedCustomDataFields().get(j));
                }
            }
        }

    }


    /**
     * This method will collect the custom spectrum data used in the different msf files
     */
    private void collectCustomSpectrumData() {
        iMergedCustomSpectrumData = new Vector<CustomDataField>();
        for (int i = 0; i < iParsedMsfs.size(); i++) {
            for (int j = 0; j < iParsedMsfs.get(i).getSpectrumUsedCustomDataFields().size(); j++) {
                boolean lFound = false;
                for (int k = 0; k < iMergedCustomSpectrumData.size(); k++) {
                    if (iParsedMsfs.get(i).getSpectrumUsedCustomDataFields().get(j).getName().equalsIgnoreCase(iMergedCustomSpectrumData.get(k).getName())) {
                        lFound = true;
                    }
                }
                if (!lFound) {
                    iMergedCustomSpectrumData.add(iParsedMsfs.get(i).getSpectrumUsedCustomDataFields().get(j));
                }
            }
        }

    }

    /**
     * This method will set the msms spectrum annotation for a peptide
     *
     * @param lPeptide The peptide to set the annotation for
     */
    public void setSpectrumMSMSAnnotations(Peptide lPeptide) {
        Vector<DefaultSpectrumAnnotation> lAnnotations = new Vector<DefaultSpectrumAnnotation>();
        if (iMSMSspectrumPanel != null && lPeptide != null) {
            int lMaximumCharge = iSelectedPeptide.getParentSpectrum().getCharge();
            if (chargeOneJCheckBox.isSelected()) {
                addIonAnnotationByCharge(lAnnotations, 1, lPeptide);
            }
            if (chargeTwoJCheckBox.isSelected()) {
                addIonAnnotationByCharge(lAnnotations, 2, lPeptide);
            }
            if (chargeOverTwoJCheckBox.isSelected()) {
                for (int i = 3; i <= lMaximumCharge; i++) {
                    addIonAnnotationByCharge(lAnnotations, 3, lPeptide);
                }
            }
        }
        iMSMSspectrumPanel.setAnnotations(lAnnotations);
        iMSMSspectrumPanel.validate();
        iMSMSspectrumPanel.repaint();
    }

    /**
     * This method will generate the spectrum annotations based on the charge and the ion checkboxes
     *
     * @param lAnnotations Vector to add the annotations to
     * @param lCharge      The charge
     * @param lPeptide     The peptide
     * @return Vector with the annotations
     */
    public Vector<DefaultSpectrumAnnotation> addIonAnnotationByCharge(Vector<DefaultSpectrumAnnotation> lAnnotations, int lCharge, Peptide lPeptide) {
        Vector<FragmentIonType> lIonTypes = new Vector<FragmentIonType>();
        if (aIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.a);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_a);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H20_a);
            }
        }
        if (bIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.b);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_b);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H2O_b);
            }
        }
        if (cIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.c);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_c);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H2O_c);
            }
        }
        if (xIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.x);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_x);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H2O_x);
            }
        }
        if (yIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.y);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_y);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H2O_y);
            }
        }
        if (zIonsJCheckBox.isSelected()) {
            lIonTypes.add(FragmentIonType.z);
            if (nh3IonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.NH3_z);
            }
            if (h2oIonsJCheckBox.isSelected()) {
                lIonTypes.add(FragmentIonType.H2O_z);
            }
        }

        Vector<FragmentIon> lFragmentIons = lPeptide.getFragmentIonsByTypeAndCharge(lCharge, lIonTypes);

        for (int i = 0; i < lFragmentIons.size(); i++) {
            DefaultSpectrumAnnotation lAnno = null;
            FragmentIon lIon = lFragmentIons.get(i);
            if (lIon.getFragmentIonType() == FragmentIonType.a) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLUE, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H20_a) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_a) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.b) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLUE, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H2O_b) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_b) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.c) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLUE, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H2O_c) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_c) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.x) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLACK, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H2O_x) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_x) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.y) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLACK, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H2O_y) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_y) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.z) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.BLACK, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.H2O_z) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }
            if (lIon.getFragmentIonType() == FragmentIonType.NH3_z) {
                lAnno = new DefaultSpectrumAnnotation(lIon.getMass(), iMSMSerror, Color.GREEN, lIon.toString());
            }

            if (lAnno != null) {
                lAnnotations.add(lAnno);
            }
        }
        return lAnnotations;
    }

    /**
     * @see #peptidesTableMouseClicked(java.awt.event.MouseEvent)
     */
    private void peptidesTableKeyReleased(KeyEvent evt) {
        peptidesTableMouseClicked(null);
    }


    /**
     * Formats the protein sequence such that both the covered parts of the sequence
     * and the peptide selected in the peptide table is highlighted.
     * This code is based on the compomics utilities sample code
     */
    public void formatProteinSequence(Protein lProtein) {


        // and clear the peptide sequence coverage details
        proteinSequenceCoverageJEditorPane.setText("");

        String lCleanProteinSequence = lProtein.getSequence();

        int selectedPeptideStart = -1;
        int selectedPeptideEnd = -1;

        // find the start end end indices for the currently selected peptide, if any
        if (iSelectedPeptide != null) {
            int lConfidenceLevel = iSelectedPeptide.getConfidenceLevel();
            boolean lUse = false;
            if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                lUse = true;
            }
            if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                lUse = true;
            }
            if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                lUse = true;
            }
            if (onlyHighestScoringCheckBox.isSelected()) {
                if (!iSelectedPeptide.getParentSpectrum().isHighestScoring(iSelectedPeptide, iMajorScoreType)) {
                    lUse = false;
                }
            }
            if (onlyLowestScoringCheckBox.isSelected()) {
                if (!iSelectedPeptide.getParentSpectrum().isLowestScoring(iSelectedPeptide, iMajorScoreType)) {
                    lUse = false;
                }
            }

            if (lUse) {
                selectedPeptideStart = lCleanProteinSequence.indexOf(iSelectedPeptide.getSequence());
                selectedPeptideEnd = selectedPeptideStart + iSelectedPeptide.getSequence().length();
                selectedPeptideStart = selectedPeptideStart + 1;
            }
        }

        // an array containing the coverage index for each residue
        int[] coverage = new int[lCleanProteinSequence.length() + 1];

        Vector<Peptide> lPeptides = lProtein.getPeptides();

        // iterate the peptide table and store the coverage for each peptide
        for (int i = 0; i < lPeptides.size(); i++) {
            int lConfidenceLevel = lPeptides.get(i).getConfidenceLevel();
            boolean lUse = false;
            if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                lUse = true;
            }
            if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                lUse = true;
            }
            if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                lUse = true;
            }
            if (onlyHighestScoringCheckBox.isSelected()) {
                if (!lPeptides.get(i).getParentSpectrum().isHighestScoring(lPeptides.get(i), iMajorScoreType)) {
                    lUse = false;
                }
            }
            if (onlyLowestScoringCheckBox.isSelected()) {
                if (!lPeptides.get(i).getParentSpectrum().isLowestScoring(lPeptides.get(i), iMajorScoreType)) {
                    lUse = false;
                }
            }
            if (lUse) {
                int tempPeptideStart = lCleanProteinSequence.indexOf(lPeptides.get(i).getSequence());
                int tempPeptideEnd = tempPeptideStart + lPeptides.get(i).getSequence().length();
                tempPeptideStart = tempPeptideStart + 1;

                for (int j = tempPeptideStart; j <= tempPeptideEnd; j++) {
                    coverage[j]++;
                }
            }
        }

        String sequenceTable = "", currentCellSequence = "";
        boolean selectedPeptide = false, coveredPeptide = false;
        double sequenceCoverage = 0;

        // iterate the coverage table and create the formatted sequence string
        for (int i = 1; i < coverage.length; i++) {

            // add indices per 50 residues
            if (i % 50 == 1 || i == 1) {
                sequenceTable += "</tr><tr><td height='20'><font size=2><a name=\"" + i + ".\"></a>" + i + ".</td>";

                int currentCharIndex = i;

                while (currentCharIndex + 10 < lCleanProteinSequence.length() && currentCharIndex + 10 < (i + 50)) {
                    sequenceTable += "<td height='20'><font size=2><a name=\""
                            + (currentCharIndex + 10) + ".\"></a>" + (currentCharIndex + 10) + ".</td>";
                    currentCharIndex += 10;
                }

                sequenceTable += "</tr><tr>";
            }

            // check if the current residues is covered
            if (coverage[i] > 0) {
                sequenceCoverage++;
                coveredPeptide = true;
            } else {
                coveredPeptide = false;
            }

            // check if the current residue is contained in the selected peptide
            if (i == selectedPeptideStart) {
                selectedPeptide = true;
            } else if (i == selectedPeptideEnd + 1) {
                selectedPeptide = false;
            }

            // highlight the covered and selected peptides
            if (selectedPeptide) {
                currentCellSequence += "<font color=red>" + lCleanProteinSequence.charAt(i - 1) + "</font>";
            } else if (coveredPeptide) {
                currentCellSequence += "<font color=blue>" + lCleanProteinSequence.charAt(i - 1) + "</font>";
            } else {
                currentCellSequence += "<font color=black>" + lCleanProteinSequence.charAt(i - 1) + "</font>";
            }

            // add the sequence to the formatted sequence
            if (i % 10 == 0) {
                sequenceTable += "<td><tt>" + currentCellSequence + "</tt></td>";
                currentCellSequence = "";
            }
        }

        // add remaining tags and complete the formatted sequence
        sequenceTable += "<td><tt>" + currentCellSequence + "</tt></td></table><font color=black>";
        String formattedSequence = "<html><body><table cellspacing='2'>" + sequenceTable + "</html></body>";

        // calculte and display the percent sequence coverage
        sequenceCoverageJLabel.setText("Protein coverage: " + Util.roundDouble(sequenceCoverage / lCleanProteinSequence.length(), 2) + "%");

        // display the formatted sequence
        proteinSequenceCoverageJEditorPane.setText(formattedSequence);
        proteinSequenceCoverageJEditorPane.updateUI();

        // make sure that the currently selected peptide is visible
        if (selectedPeptideStart != -1) {
            proteinSequenceCoverageJEditorPane.scrollToReference((selectedPeptideStart - selectedPeptideStart % 10 + 1) + ".");
        } else {
            proteinSequenceCoverageJEditorPane.setCaretPosition(0);
        }
    }


    /**
     * Update the tables based on the spectrum selected.
     *
     * @param evt
     */
    private void peptidesTableMouseClicked(MouseEvent evt) {

        // Set the cursor into the wait status.
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));


        int row = jtablePeptides.getSelectedRow();

        // Condition if one row is selected.
        if (row != -1) {
            iSelectedPeptide = (Peptide) jtablePeptides.getValueAt(row, 2);

            //update the protein list
            iDisplayedProteins.removeAllElements();
            for (int i = 0; i < iSelectedPeptide.getProteins().size(); i++) {
                for (int j = 0; j < iProteins.size(); j++) {
                    if (iProteins.get(j).getDescription().equalsIgnoreCase(iSelectedPeptide.getProteins().get(i).getDescription())) {
                        iDisplayedProteins.add(iProteins.get(j));
                    }
                }
            }
            this.filterDisplayedProteins();
            proteinList.updateUI();


            try {
                //do the MSMS
                Vector<Peak> lPeaks = iSelectedPeptide.getParentSpectrum().getMSMSPeaks();
                double[] lMzValues = new double[lPeaks.size()];
                double[] lIntensityValues = new double[lPeaks.size()];
                for (int i = 0; i < lPeaks.size(); i++) {
                    lMzValues[i] = lPeaks.get(i).getX();
                    lIntensityValues[i] = lPeaks.get(i).getY();
                }

                // Empty the spectrum panel.
                while (this.jpanMSMSLeft.getComponents().length > 0) {
                    this.jpanMSMSLeft.remove(0);
                }

                // Updating the spectrum panel
                iMSMSspectrumPanel = new SpectrumPanel(
                        lMzValues,
                        lIntensityValues,
                        iSelectedPeptide.getParentSpectrum().getMz(),
                        iSelectedPeptide.getParentSpectrum().getSpectrumTitle(),
                        String.valueOf(iSelectedPeptide.getParentSpectrum().getCharge()),
                        50, true, false);

                this.jpanMSMSLeft.add(iMSMSspectrumPanel);
                this.jpanMSMSLeft.validate();
                this.jpanMSMSLeft.repaint();
                this.setSpectrumMSMSAnnotations(iSelectedPeptide);


                //do the MS
                Vector<Peak> lMSPeaks = iSelectedPeptide.getParentSpectrum().getMSPeaks();
                double[] lMSMzValues = new double[lMSPeaks.size()];
                double[] lMSIntensityValues = new double[lMSPeaks.size()];
                Vector<DefaultSpectrumAnnotation> lMSAnnotations = new Vector<DefaultSpectrumAnnotation>();
                double lMinMZvalue = Double.MAX_VALUE;
                double lMaxMZvalue = Double.MIN_VALUE;
                for (int i = 0; i < lMSPeaks.size(); i++) {
                    lMSMzValues[i] = lMSPeaks.get(i).getX();
                    if (lMSMzValues[i] > lMaxMZvalue) {
                        lMaxMZvalue = lMSMzValues[i];
                    }
                    if (lMSMzValues[i] < lMinMZvalue) {
                        lMinMZvalue = lMSMzValues[i];
                    }
                    lMSIntensityValues[i] = lMSPeaks.get(i).getY();
                    if (lMSPeaks.get(i).getZ() != 0) {
                        lMSAnnotations.add(new DefaultSpectrumAnnotation(lMSPeaks.get(i).getX(), 0.001, Color.BLACK, lMSPeaks.get(i).getX() + "(Z = " + lMSPeaks.get(i).getZ() + ")"));
                    }
                }

                // Empty the spectrum panel.
                while (this.jpanMS.getComponents().length > 0) {
                    this.jpanMS.remove(0);
                }

                // Updating the spectrum panel
                iMSspectrumPanel = new SpectrumPanel(
                        lMSMzValues,
                        lMSIntensityValues,
                        iSelectedPeptide.getParentSpectrum().getMz(),
                        "",
                        String.valueOf(iSelectedPeptide.getParentSpectrum().getCharge()),
                        50, true, false);
                iMSspectrumPanel.rescale(lMinMZvalue, lMaxMZvalue);
                iMSspectrumPanel.setProfileMode(false);
                iMSspectrumPanel.setAnnotations(lMSAnnotations);
                iMSspectrumPanel.setXAxisStartAtZero(false);

                this.jpanMS.add(iMSspectrumPanel);
                this.jpanMS.validate();
                this.jpanMS.repaint();


                //add the chromatograms
                try {
                    jtabChromatogram.removeAll();

                    Vector<Chromatogram> lChros = iSelectedPeptide.getParentSpectrum().getParser().getChromatograms();
                    for (int c = 0; c < lChros.size(); c++) {
                        Chromatogram lChro = lChros.get(c);
                        if (lChro.getFileId() == iSelectedPeptide.getParentSpectrum().getFileId()) {
                            Vector<Chromatogram.Point> lPoints = lChro.getPoints();

                            double[] lXvalues = new double[lPoints.size()];
                            double[] lYvalues = new double[lPoints.size()];

                            double lMaxY = 0.0;
                            for (int p = 0; p < lPoints.size(); p++) {
                                if (lPoints.get(p).getY() != 0.0) {
                                    lXvalues[p] = lPoints.get(p).getT();
                                    lYvalues[p] = lPoints.get(p).getY();
                                    if (lPoints.get(p).getY() > lMaxY) {
                                        lMaxY = lPoints.get(p).getY();
                                    }
                                }
                            }

                            // create the chromatogram
                            ChromatogramPanel chromatogramPanel = new ChromatogramPanel(
                                    lXvalues, lYvalues, "Time (minutes)", "Intensity");
                            chromatogramPanel.setMaxPadding(65);
                            double[] lXAnno = new double[]{iSelectedPeptide.getParentSpectrum().getRetentionTime()};
                            double[] lYAnno = new double[]{lMaxY};
                            chromatogramPanel.addAdditionalDataset(lXAnno, lYAnno, Color.GREEN, Color.GREEN);


                            String lTitle = iSelectedPeptide.getParentSpectrum().getParser().getRawfileNameByFileId(lChro.getFileId());
                            lTitle = lTitle + " - " + lChro.getTraceType();
                            jtabChromatogram.add(lTitle, chromatogramPanel);
                            // remove the default chromatogram panel border, given that our
                            // chromatogram panel already has a border
                            chromatogramPanel.setBorder(null);

                            // add the chromatogram panel to the frame
                            chromatogramPanel.validate();
                            chromatogramPanel.repaint();
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    if (iSelectedPeptide.getParentSpectrum().getQuanResult() != null) {

                        jtabpanLower.add("Quantification spectrum", jpanQuantificationSpectrumHolder);

                        QuanResult lQuan = iSelectedPeptide.getParentSpectrum().getQuanResult();
                        //get the quan events
                        Vector<Event> lQuanEvents = new Vector<Event>();
                        Vector<Integer> lQuanEventsIds = new Vector<Integer>();
                        Vector<Vector<Event>> lQuanEventsByPattern = new Vector<Vector<Event>>();
                        for (int i = 0; i < lQuan.getIsotopePatterns().size(); i++) {
                            Vector<Event> lIsotopePatternEvents = lQuan.getIsotopePatterns().get(i).getEvents(iSelectedPeptide.getParentSpectrum().getConnection());
                            lQuanEventsByPattern.add(lIsotopePatternEvents);
                            for (int j = 0; j < lIsotopePatternEvents.size(); j++) {
                                lQuanEvents.add(lIsotopePatternEvents.get(j));
                                lQuanEventsIds.add(lIsotopePatternEvents.get(j).getEventId());
                            }
                        }

                        //get the min and max retention and mass
                        double lMinMass = Double.MAX_VALUE;
                        double lMinRT = Double.MAX_VALUE;
                        double lMaxMass = Double.MIN_VALUE;
                        double lMaxRT = Double.MIN_VALUE;

                        for (int i = 0; i < lQuanEvents.size(); i++) {
                            if (lMinMass > lQuanEvents.get(i).getMass()) {
                                lMinMass = lQuanEvents.get(i).getMass();
                            }
                            if (lMaxMass < lQuanEvents.get(i).getMass()) {
                                lMaxMass = lQuanEvents.get(i).getMass();
                            }
                            if (lMinRT > lQuanEvents.get(i).getRetentionTime()) {
                                lMinRT = lQuanEvents.get(i).getRetentionTime();
                            }
                            if (lMaxRT < lQuanEvents.get(i).getRetentionTime()) {
                                lMaxRT = lQuanEvents.get(i).getRetentionTime();
                            }
                        }
                        //calculate the borders
                        double lMassDiff = Math.abs(lMaxMass - lMinMass);
                        lMinMass = lMinMass - (lMassDiff / 3.0);
                        lMaxMass = lMaxMass + (lMassDiff / 3.0);
                        lMinRT = lMinRT - 0.5;
                        lMaxRT = lMaxRT + 0.5;

                        Vector<Event> lBackgroundEvents = Event.getEventByRetentionTimeLimitMassLimitAndFileIdExcludingIds(lMinRT, lMaxRT, lMinMass, lMaxMass, lQuanEventsIds, iSelectedPeptide.getParentSpectrum().getFileId(), iSelectedPeptide.getParentSpectrum().getConnection());


                        double[] lQuanMzValues = new double[lBackgroundEvents.size()];
                        double[] lQuanIntensityValues = new double[lBackgroundEvents.size()];

                        for (int i = 0; i < lBackgroundEvents.size(); i++) {
                            lQuanMzValues[i] = lBackgroundEvents.get(i).getMass();
                            lQuanIntensityValues[i] = lBackgroundEvents.get(i).getIntensity();
                        }


                        // Empty the spectrum panel.
                        while (this.jpanQuantitationSpectrum.getComponents().length > 0) {
                            this.jpanQuantitationSpectrum.remove(0);
                        }

                        // Updating the spectrum panel
                        iQuantificationSpectrumPanel = new SpectrumPanel(
                                lQuanMzValues,
                                lQuanIntensityValues,
                                iSelectedPeptide.getParentSpectrum().getMz() / (double) iSelectedPeptide.getParentSpectrum().getCharge(),
                                "RT: " + lMinRT + " - " + lMaxRT,
                                String.valueOf(iSelectedPeptide.getParentSpectrum().getCharge()),
                                50, true, true);
                        iQuantificationSpectrumPanel.rescale(lMinMass, lMaxMass);
                        iQuantificationSpectrumPanel.setProfileMode(false);
                        iQuantificationSpectrumPanel.setXAxisStartAtZero(false);
                        Vector<DefaultSpectrumAnnotation> lQuanAnnotations = new Vector<DefaultSpectrumAnnotation>();
                        for (int i = 0; i < lQuan.getIsotopePatterns().size(); i++) {
                            double[] lQuanPatternMzValues = new double[lQuanEventsByPattern.get(i).size()];
                            double[] lQuanPatternIntensityValues = new double[lQuanEventsByPattern.get(i).size()];
                            for (int j = 0; j < lQuanEventsByPattern.get(i).size(); j++) {
                                lQuanPatternMzValues[j] = lQuanEventsByPattern.get(i).get(j).getMass();
                                lQuanPatternIntensityValues[j] = lQuanEventsByPattern.get(i).get(j).getIntensity();
                                for (int k = 0; k < lQuan.getIsotopePatterns().get(i).getEventAnnotations().size(); k++) {
                                    if (lQuanEventsByPattern.get(i).get(j).getEventId() == lQuan.getIsotopePatterns().get(i).getEventAnnotations().get(k).getEventId()) {
                                        if (lQuan.getIsotopePatterns().get(i).getEventAnnotations().get(k).getQuanChannelId() != -1) {
                                            lQuanAnnotations.add(new DefaultSpectrumAnnotation(lQuanEventsByPattern.get(i).get(j).getMass(), 0.000000000000000000000001, Color.BLACK, "" + iSelectedPeptide.getParentSpectrum().getParser().getQuanChannelNameById(lQuan.getIsotopePatterns().get(i).getEventAnnotations().get(k).getQuanChannelId())));
                                        }
                                    }
                                }
                            }
                            iQuantificationSpectrumPanel.addAdditionalDataset(lQuanPatternMzValues, lQuanPatternIntensityValues, Color.GREEN, Color.GREEN);
                        }

                        iQuantificationSpectrumPanel.setAnnotations(lQuanAnnotations);

                        this.jpanQuantitationSpectrum.add(iQuantificationSpectrumPanel);
                        this.jpanQuantitationSpectrum.validate();
                        this.jpanQuantitationSpectrum.repaint();


                    } else {
                        jtabpanLower.remove(jpanQuantificationSpectrumHolder);
                        // Empty the spectrum panel.
                        while (this.jpanQuantitationSpectrum.getComponents().length > 0) {
                            this.jpanQuantitationSpectrum.remove(0);
                        }
                        this.jpanQuantitationSpectrum.validate();
                        this.jpanQuantitationSpectrum.repaint();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                //check if the protein coverage has to be changed
                if (iSelectedProtein != null) {
                    formatProteinSequence(iSelectedProtein);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // At the end set the cursor back to default.
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Main method
     *
     * @param args no arguments are expected
     */
    public static void main(String[] args) {
        try {
            PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            // ignore exception
        }
        new Thermo_msf_parserGUI();
    }

    /**
     * This method will create custom gui elements
     */
    private void createUIComponents() {
        jpanMSMSLeft = new JPanel();
        jpanMS = new JPanel();
        jpanQuantitationSpectrum = new JPanel();
        aIonsJCheckBox = new JCheckBox("a");
        bIonsJCheckBox = new JCheckBox("b");
        cIonsJCheckBox = new JCheckBox("c");
        yIonsJCheckBox = new JCheckBox("y");
        xIonsJCheckBox = new JCheckBox("x");
        zIonsJCheckBox = new JCheckBox("z");
        chargeOneJCheckBox = new JCheckBox("+");
        chargeTwoJCheckBox = new JCheckBox("++");
        chargeOverTwoJCheckBox = new JCheckBox(">2");
        nh3IonsJCheckBox = new JCheckBox("");
        h2oIonsJCheckBox = new JCheckBox("");
        txtMSMSerror = new JTextField();
        txtMSMSerror.setText("0.5");
        txtMSMSerror.setToolTipText("The MS/MS fragmentation error (in Da)");
        txtMSMSerror.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                changeMSMSerror();
            }
        });

        this.jpanMSMSLeft.setBorder(BorderFactory.createEtchedBorder());
        this.jpanMSMSLeft.setLayout(new BoxLayout(this.jpanMSMSLeft, BoxLayout.LINE_AXIS));
        this.jpanMS.setBorder(BorderFactory.createEtchedBorder());
        this.jpanMS.setLayout(new BoxLayout(this.jpanMS, BoxLayout.LINE_AXIS));
        this.jpanQuantitationSpectrum.setBorder(BorderFactory.createEtchedBorder());
        this.jpanQuantitationSpectrum.setLayout(new BoxLayout(this.jpanQuantitationSpectrum, BoxLayout.LINE_AXIS));


        aIonsJCheckBox.setSelected(true);
        aIonsJCheckBox.setText("a");
        aIonsJCheckBox.setToolTipText("Show a-ions");
        aIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        aIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        aIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        aIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        bIonsJCheckBox.setSelected(true);
        bIonsJCheckBox.setText("b");
        bIonsJCheckBox.setToolTipText("Show b-ions");
        bIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        bIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        bIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        bIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        cIonsJCheckBox.setSelected(true);
        cIonsJCheckBox.setText("c");
        cIonsJCheckBox.setToolTipText("Show c-ions");
        cIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        cIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        cIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        cIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        yIonsJCheckBox.setSelected(true);
        yIonsJCheckBox.setText("y");
        yIonsJCheckBox.setToolTipText("Show y-ions");
        yIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        yIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        yIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        yIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        xIonsJCheckBox.setSelected(true);
        xIonsJCheckBox.setText("x");
        xIonsJCheckBox.setToolTipText("Show x-ions");
        xIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        xIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        xIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        xIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        zIonsJCheckBox.setSelected(true);
        zIonsJCheckBox.setText("z");
        zIonsJCheckBox.setToolTipText("Show z-ions");
        zIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        zIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        zIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        zIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        chargeOneJCheckBox.setSelected(true);
        chargeOneJCheckBox.setText("+");
        chargeOneJCheckBox.setToolTipText("Show ions with charge 1");
        chargeOneJCheckBox.setMaximumSize(new Dimension(39, 23));
        chargeOneJCheckBox.setMinimumSize(new Dimension(39, 23));
        chargeOneJCheckBox.setPreferredSize(new Dimension(39, 23));
        chargeOneJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        chargeTwoJCheckBox.setSelected(true);
        chargeTwoJCheckBox.setText("++");
        chargeTwoJCheckBox.setToolTipText("Show ions with charge 2");
        chargeTwoJCheckBox.setMaximumSize(new Dimension(39, 23));
        chargeTwoJCheckBox.setMinimumSize(new Dimension(39, 23));
        chargeTwoJCheckBox.setPreferredSize(new Dimension(39, 23));
        chargeTwoJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        chargeOverTwoJCheckBox.setSelected(true);
        chargeOverTwoJCheckBox.setText(">2");
        chargeOverTwoJCheckBox.setToolTipText("Show ions with charge >2");
        chargeOverTwoJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        nh3IonsJCheckBox.setSelected(true);
        nh3IonsJCheckBox.setText("NH3");
        nh3IonsJCheckBox.setToolTipText("Show NH3-loss");
        nh3IonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        nh3IonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        nh3IonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        nh3IonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });

        h2oIonsJCheckBox.setSelected(true);
        h2oIonsJCheckBox.setText("H20");
        h2oIonsJCheckBox.setToolTipText("Show H20-loss");
        h2oIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
        h2oIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
        h2oIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
        h2oIonsJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                ionsJCheckBoxActionPerformed();
            }
        });


        aIonsJCheckBox.setSelected(false);
        cIonsJCheckBox.setSelected(false);
        xIonsJCheckBox.setSelected(false);
        zIonsJCheckBox.setSelected(false);
        chargeTwoJCheckBox.setSelected(false);
        chargeOverTwoJCheckBox.setSelected(false);
        nh3IonsJCheckBox.setSelected(false);
        h2oIonsJCheckBox.setSelected(false);


        proteinCoverageJScrollPane = new JScrollPane();
        proteinSequenceCoverageJEditorPane = new JEditorPane();
        proteinCoverageJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        proteinSequenceCoverageJEditorPane.setContentType("text/html");
        proteinSequenceCoverageJEditorPane.setEditable(false);
        proteinSequenceCoverageJEditorPane.setText("<html>\r\n  <head>\r\n\r\n  </head>\r\n  <body>\r\n    <p style=\"margin-top: 0\">\r\n\n    </p>\r\n  </body>\r\n</html>\r\n");
        proteinSequenceCoverageJEditorPane.setMargin(new Insets(10, 10, 10, 10));
        proteinSequenceCoverageJEditorPane.setMinimumSize(new Dimension(22, 22));
        proteinSequenceCoverageJEditorPane.setPreferredSize(new Dimension(22, 22));
        proteinCoverageJScrollPane.setViewportView(proteinSequenceCoverageJEditorPane);


    }


    /**
     * This method will load a protein. It will create the peptide table an format the protein sequence
     */
    public void loadProtein() {
        iSelectedProtein = (Protein) proteinList.getSelectedValue();
        createPeptideTable(iSelectedProtein);
        formatProteinSequence(iSelectedProtein);
    }

    /**
     * A .msf file filter
     */
    class MsfFileFilter extends FileFilter {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".msf");
        }

        public String getDescription() {
            return ".msf files";
        }
    }

}
