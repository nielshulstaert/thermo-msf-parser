package com.compomics.thermo_msf_parser.msf;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 4/24/12
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideLowMem {

    private int counter = 0;

    // Class specific log4j logger for Thermo_msf_parserGUI instances.
    private static Logger logger = Logger.getLogger(Peptide.class);
    /**
     * The peptide id
     */
    private int iPeptideId;
    /**
     * The spectrum id linked to this peptide
     */
    private int iSpectrumId;
    /**
     * The confidence level
     */
    private int iConfidenceLevel;
    /**
     * The peptide sequence
     */
    private String iSequence;
    /**
     * The scores
     */
    private Vector<Double> iScores = new Vector<Double>();
    /**
     * The scoretypeids
     */
    private Vector<Integer> iScoreTypeIds = new Vector<Integer>();
    /**
     * The scoretype
     */
    private Vector<ScoreTypeLowMem> iScoreTypes = new Vector<ScoreTypeLowMem>();
    /**
     * The total ions count
     */
    private int iTotalIonsCount;
    /**
     * The matched ions count
     */
    private int iMatchedIonsCount;
    /**
     * The peptide annotation
     */
    private String iAnnotation;

    /**
     * The proteins linked to this peptide
     */
    
    private Vector<ProteinLowMem> iPeptideProteins = new Vector<ProteinLowMem>();
    
    /**
     * The modifications linked to this peptide
     */
    
    private Vector<Modification> iPeptideModifications = new Vector<Modification>();
    
    /**
     * The modifications positions of the modifications in the iPeptideModifications Vector
     */
    
    private Vector<ModificationPosition> iPeptideModificationPositions = new Vector<ModificationPosition>();
    
    /**
     * The site probabilities of the Phospho modifications
     */
    
    private Vector<Float> iPhosphoRSSiteProbabilities = new Vector<Float>();
    
    /**
     * the phosphoRS p value for the current 'phosphorylation isoform'
     */
    
    private Float phosphoRSScore = null;
    
    /**
     * the phosphoRS probability of the sequence
     */
    
    private Float phoshpoRSSequenceProbability = null;
    
    /**
     * Boolean that indicates if this peptide is N-terminally modified
     */
    
    private boolean iHasNTermModification = false;
    
    /**
     * The modified sequence
     */
    
    private String iModifiedPeptide = null;
    
    /**
     * The channel
     */
    
    private int iChannelId = 0;
    
    /**
     * HashMap with the custom data field values. The key is the id of the custom data field
     */
    
    private HashMap<Integer, String> iCustomDataFieldValues = new HashMap<Integer,String>();
    
    /**
     * All the amino acids
     */
    
    private Vector<AminoAcid> iAminoAcids;
    
    /**
     * The fragment ions
     */
    
    private Vector<PeptideFragmentIon> iTheoreticalFragmentIons;
    
    /**
     * This vector holds the peptide sequence in amino acid objects
     */
    
    private Vector<AminoAcid> iAminoAcidSequence = new Vector<AminoAcid>();
    
    /**
     * The spectrum linked to this peptide
     */
    
    private SpectrumLowMem iParentSpectrum;
    
    /**
     * The processing node number
     */
    
    private int iProcessingNodeNumber;
    
    /**
     * Int that indicates if this peptide has a missed cleavage
     * WORKS ONLY FROM PROTEOME DISCOVERER VERSION 1.3
     */
    
    private int iMissedCleavage;
    
    /**
     * The unique peptide sequence id
     * WORKS ONLY FROM PROTEOME DISCOVERER VERSION 1.3
     */
    
    /**
     * int of the unique peptide id
     */
    private int iUniquePeptideSequenceId;

    /**
     * connection to the msf file
     */
    
    private Connection iConn;

     /**
     * Constructor for a low memory instance peptide
     * @param iPeptideId SQLite id for peptide
     * @param iSpectrumId SQLite id for spectrum
     * @param iConfidenceLevel the confidence level of the peptide
     * @param iSequence the peptide sequence
     * @param iTotalIonsCount the total ions counted for the peptide
     * @param iMatchedIonsCount the matched ions for the peptide
     * @param iAnnotation the peptide annotation
     * @param iProcessingNodeNumber the procession number
     * @param iAminoAcids Vector containing the amino acids used in the SQLite database. returned from the AminoAcidLowMem class
     * @param aConn connection to the msf file
     */
    
    public PeptideLowMem(int iPeptideId, int iSpectrumId, int iConfidenceLevel, String iSequence, int iTotalIonsCount, int iMatchedIonsCount, String iAnnotation, int iProcessingNodeNumber, Vector<AminoAcid> iAminoAcids,Connection aConn) {
        this.iPeptideId = iPeptideId;
        this.iSpectrumId = iSpectrumId;
        this.iConfidenceLevel = iConfidenceLevel;
        this.iSequence = iSequence;
        this.iTotalIonsCount = iTotalIonsCount;
        this.iMatchedIonsCount = iMatchedIonsCount;
        this.iAnnotation = iAnnotation;
        this.iAminoAcids = iAminoAcids;
        this.iProcessingNodeNumber = iProcessingNodeNumber;
        this.iConn = aConn;
        iAminoAcidSequence = new Vector<AminoAcid>();
        for (int i = 0; i < iSequence.length(); i++) {
            String lAaOneLetterCode = String.valueOf(iSequence.charAt(i));
            for(int a = 0; a< iAminoAcids.size(); a ++){
                if(iAminoAcids.get(a).getOneLetterCode() != null){
                    if(iAminoAcids.get(a).getOneLetterCode().equalsIgnoreCase(lAaOneLetterCode)){
                        iAminoAcidSequence.add(iAminoAcids.get(a));
                    }
                }
            }
        }
    }

    /**
     * Getter for the peptide id
     * @return int with the peptide id
     */
    public int getPeptideId() {
        return iPeptideId;
    }

    /**
     * Getter for the spectrum id
     * @return int with the spectrum id
     */
    public int getSpectrumId() {
        return iSpectrumId;
    }

    /**
     * Getter for the confidence level
     * @return int with the confidence level
     */
    public int getConfidenceLevel() {
        return iConfidenceLevel;
    }

    /**
     * Getter for the sequence
     * @return String with the sequence
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * Getter for the scores
     * @return Vector with the scores (double)
     */
    public Vector<Double> getScores() {
        return iScores;
    }

    /**
     * Getter for the score type ids. These score type ids represent the score types
     * @return Vector with the score type ids
     */
    public Vector<Integer> getScoreTypeIds() {
        return iScoreTypeIds;
    }

    /**
     * Getter for the score type. These score type are linked to the scores
     * @return Vector with the score type
     */
    public Vector<ScoreTypeLowMem> getScoreTypes() {
        return iScoreTypes;
    }

    /**
     * Getter for the total ion count
     * @return int with the number of ions counted
     */
    public int getTotalIonsCount() {
        return iTotalIonsCount;
    }

    /**
     * Getter for the number of matched ions
     * @return int with the number of matched ions
     */
    public int getMatchedIonsCount() {
        return iMatchedIonsCount;
    }

    /**
     * Getter for the annotation
     * @return String with the annotation
     */
    public String getAnnotation() {
        return iAnnotation;
    }

    /**
     * Getter of the proteins linked to this peptide
     * @return vector with the linked proteins
     */
    public Vector<ProteinLowMem> getPeptideProteins() {
        return iPeptideProteins;
    }

    /**
     * Getter for the modifications linked to this peptide
     * @return vector with the linked modifications
     */
    public Vector<Modification> getPeptideModifications() {
        return iPeptideModifications;
    }

    /**
     * Getter for the modification positions linked to this peptide
     * @return vector with the linked modification positions
     */
    public Vector<ModificationPosition> getPeptideModificationPositions() {
        return iPeptideModificationPositions;
    }

    /**
     * Getter for a boolean that indicates if this peptide has an N terminal modifcation
     * @return boolean that indicates if this peptide has an N terminal modifcation
     */
    public boolean isHasNTermModification() {
        return iHasNTermModification;
    }

    /**
     * Getter for the custom data fields linked to this peptide. The key is
     * the id of the custom data field and the value is the data field value
     * @return hashmap
     */
    public HashMap<Integer, String> getCustomDataFieldValues() {
        return iCustomDataFieldValues;
    }

    /**
     * Getter for the amino acids found in the msf file
     * @return vector with all the amino acids found in the msf file
     */
    public Vector<AminoAcid> getAminoAcids() {
        return iAminoAcids;
    }

    /**
     * Getter for a vector with all theoretical fragment ions
     * @return vector with all theoretical fragment ions
     */
    public Vector<PeptideFragmentIon> getTheoreticalFragmentIons() {
        return iTheoreticalFragmentIons;
    }

    /**
     * Getter for the vector with the amino acid sequence
     * @return vector with the amino acid sequence
     */
    public Vector<AminoAcid> getAminoAcidSequence() {
        return iAminoAcidSequence;
    }

    /**
     * Getter for the parent spectrum
     * @return The parent spectrum
     */
    public SpectrumLowMem getParentSpectrum() {
        return iParentSpectrum;
    }

    /**
     * Setter for the parent spectrum
     * @param lSpectrum The parent spectrum to set
     */
    public void setParentSpectrum(SpectrumLowMem lSpectrum){
        this.iParentSpectrum = lSpectrum;
    }

    /**
     * Getter for a score by score type
     * @param lScoreType The score type of the requested score
     * @return double with the score
     */
    public Double getScoreByScoreType(ScoreTypeLowMem lScoreType){
        Double lScore = null;

        for(int i = 0; i<iScoreTypes.size(); i ++){
            if(lScoreType.getDescription().equalsIgnoreCase(iScoreTypes.get(i).getDescription())){
                lScore = iScores.get(i);
            }
        }
        return lScore;
    }

    /**
     * Getter for the main score
     * @return double with the score
     */
    public Double getMainScore(){
        Double lScore = null;
        for(int i = 0; i<iScoreTypes.size(); i ++){
            if(iScoreTypes.get(i).getIsMainScore() == 1){
                lScore = iScores.get(i);
            }
        }
        return lScore;
    }

    /**
     * This method will add a score (with a specific score type id) to this peptide
     * @param iScore double with the score
     * @param iScoreTypeid the score type id the added score
     * @param lScoreTypes The different score types found in the msf file
     */
    public void setScore(double iScore, int iScoreTypeid, Vector<ScoreTypeLowMem> lScoreTypes) {
        this.iScores.add(iScore);
        this.iScoreTypeIds.add(iScoreTypeid);
        boolean added = false;
        for(int i = 0; i<lScoreTypes.size(); i ++){
            if(lScoreTypes.get(i).getScoreTypeId() == iScoreTypeid){
                iScoreTypes.add(lScoreTypes.get(i));
                added = true;
            }
        }
        if(!added){
            iScoreTypes.add(null);
        }
    }

    /**
     * This method will add a protein to this peptide
     * @param lProtein The protein to add
     */
    public void addProtein(ProteinLowMem lProtein) {
        iPeptideProteins.add(lProtein);
        lProtein.addPeptide(this);
    }

    /**
     * This method will add an amino acid modification to this peptide
     * @param lMod The modification
     * @param lModPos The modification position
     */
    public void addModification(Modification lMod, ModificationPosition lModPos, Float pRSSiteMod) {
        iPeptideModifications.add(lMod);
        iPeptideModificationPositions.add(lModPos);
        iPhosphoRSSiteProbabilities.add(pRSSiteMod);
        if (lModPos.isNterm()) {
            iHasNTermModification = true;
        }
    }

    /**
     * Getter for the proteins linked to this peptide
     * @return Vector with the proteins linked to this peptide
     */
    public Vector<ProteinLowMem> getProteins() {
        return iPeptideProteins;
    }

    /**
     * Getter for the modified peptide
     * @return String with the modified peptide
     */
    public String getModifiedPeptide() {
        if (iModifiedPeptide == null) {
            //do the N terminus
            if (iHasNTermModification) {
                for (int m = 0; m < iPeptideModifications.size(); m++) {
                    if (iPeptideModifications.get(m).getPositionType() == 1) {
                        iModifiedPeptide = iPeptideModifications.get(m).getAbbreviation() + "-";
                    }
                }
            } else {
                iModifiedPeptide = "NH2-";
            }
            //do the middle
            for (int c = 0; c < iSequence.length(); c++) {
                iModifiedPeptide += iSequence.charAt(c);
                for (int m = 0; m < iPeptideModifications.size(); m++) {
                    if (iPeptideModificationPositions.get(m).getPosition() == c && !iPeptideModificationPositions.get(m).isNterm()) {

                        iModifiedPeptide = iModifiedPeptide + "<" + iPeptideModifications.get(m).getAbbreviation();
                        if (iPhosphoRSSiteProbabilities.get(m) != null) {
                            iModifiedPeptide += ":"+(iPhosphoRSSiteProbabilities.get(m)*100) + "%";
                        }
                        iModifiedPeptide += ">";
                    }
                }
            }
            //do the C terminus
            iModifiedPeptide += "-COOH";

        }
        return iModifiedPeptide;
    }

    /**
     * Setter for the channel id
     * @param lChannelId int with the channel id to set
     */
    public void setChannelId(int lChannelId) {
        this.iChannelId = lChannelId;
    }

    /**
     * Getter for the channel id
     * @return int with the channel id
     */
    public int getChannelId() {
        return iChannelId;
    }


    /**
     * Getter for the processing node number
     * @return int with the processing node number
     */
    public int getProcessingNodeNumber() {
        return iProcessingNodeNumber;
    }

    /**
     * This method will add a value in the custom data field map by the id off the custom data field
     * @param lId The custom data field id
     * @param lValue The value to add
     */
    public void addCustomDataField(int lId, String lValue){
        iCustomDataFieldValues.put(lId, lValue);
    }

    /**
     * This code is adapted from the X!tandem parser code
     * @param iSpectrumCharge The maximum charge of the fragement ions
     */
    public void calculateFragmentions(int iSpectrumCharge){

        iTheoreticalFragmentIons = new Vector<PeptideFragmentIon>();
        double lHydrogenMass = 1.007825;
        double lCarbonMass = 12.000000;
        double lNitrogenMass = 14.003070;
        double lOxygenMass = 15.994910;

        for (int charge = 1; charge <= iSpectrumCharge; charge++) {

            for (int i = 0; i < iAminoAcidSequence.size() - 1; i++) {
                double bMass = 0.0;
                double yMass = 0.0;

                // Each peptide mass is added to the b ion mass
                for (int j = 0; j <= i; j++) {
                    bMass += iAminoAcidSequence.get(j).getMonoisotopicMass();
                    //check if it is not modified
                    for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                        if(iPeptideModificationPositions.get(m).getPosition() == j){
                            bMass += iPeptideModifications.get(m).getDeltaMass();
                        }
                    }

                }
                // Each peptide mass is added to the y ion mass, taking the reverse direction (from the C terminal end)
                for (int j = 0; j <= i; j++) {
                    yMass += iAminoAcidSequence.get((iAminoAcidSequence.size() - 1) - j).getMonoisotopicMass();
                    for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                        if(iPeptideModificationPositions.get(m).getPosition() == (iAminoAcidSequence.size() - 1) - j){
                            yMass += iPeptideModifications.get(m).getDeltaMass();
                        }
                    }
                }
                //add 1 Hydrogen for the Y ions (for the NH2 terminus) and 1 H and 1 O for the OH group
                yMass = yMass + lHydrogenMass + lHydrogenMass + lOxygenMass;

                // Create an instance for each fragment ion

                //B Ion
                PeptideFragmentIon lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.B_ION, i + 1, (bMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //BNH3 Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.BNH3_ION, i + 1, (bMass - lOxygenMass - 2 * - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //BH2O Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.BH2O_ION, i + 1, (bMass - lOxygenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //A Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.A_ION, i + 1, (bMass - lOxygenMass - lCarbonMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //ANH3 Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.ANH3_ION, i + 1, (bMass - lOxygenMass - lCarbonMass - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //AH2O Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.AH2O_ION, i + 1, (bMass - 2 * lOxygenMass - lCarbonMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //C Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.C_ION, i + 1, (bMass + lNitrogenMass + 3 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);

                // Create an instance of the fragment y ion
                //Y Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.Y_ION, i + 1, (yMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //YNH3 Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.YNH3_ION, i + 1, (yMass - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //YH2O Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.YH2O_ION, i + 1, (yMass - 2 * lHydrogenMass - lOxygenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //X Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.X_ION, i + 1, (yMass + lCarbonMass + lOxygenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);
                //Z Ion
                lIon = new PeptideFragmentIon(PeptideFragmentIon.PeptideFragmentIonType.Z_ION, i + 1, (yMass - lNitrogenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge);
                lIon.addUrParam(new com.compomics.thermo_msf_parser.gui.Charge(charge));
                iTheoreticalFragmentIons.add(lIon);

            }
        }
    }

    /**
     * This method will give the fragment ions for a specific type and charge
     * @param lCharge The charge of the fragment ions wanted
     * @param lTypes The typs of fragment ions wanted
     * @return Vector with the requested fragment ions
     */
    public Vector<PeptideFragmentIon> getFragmentIonsByTypeAndCharge(int lCharge, Vector<PeptideFragmentIon.PeptideFragmentIonType> lTypes){
        if(iTheoreticalFragmentIons == null){
            this.calculateFragmentions(getParentSpectrum().getCharge());
        }
        Vector<PeptideFragmentIon> lResult = new Vector<PeptideFragmentIon>();

        for(int i = 0; i<iTheoreticalFragmentIons.size(); i ++){
            if(((com.compomics.thermo_msf_parser.gui.Charge) iTheoreticalFragmentIons.get(i).getUrParam(new com.compomics.thermo_msf_parser.gui.Charge())).getCharge() == lCharge){
                boolean lPass = false;
                for(int t = 0; t<lTypes.size(); t ++){
                    if(iTheoreticalFragmentIons.get(i).getType() == lTypes.get(t)){
                        lPass = true;
                    }
                }
                if(lPass){
                    lResult.add(iTheoreticalFragmentIons.get(i));
                }
            }
        }

        return lResult;
    }

    /**
     * To string method
     * @return String with the peptide sequence
     */
    public String toString(){
        return iSequence;
    }
    /**
     * 
     * @param aMissedCleavage 
     */
    public void setMissedCleavage(int aMissedCleavage) {
        this.iMissedCleavage = aMissedCleavage;
    }
    /**
     * 
     * @param aUniquePeptideSequenceId 
     */
    public void setUniquePeptideSequenceId(int aUniquePeptideSequenceId) {
        this.iUniquePeptideSequenceId = aUniquePeptideSequenceId;
    }
/**
 * 
 * @return 
 */
    public int getMissedCleavage() {
        return iMissedCleavage;
    }
/**
 * 
 * @return 
 */
    public int getUniquePeptideSequenceId() {
        return iUniquePeptideSequenceId;
    }
/**
 * 
 * @param aProtein 
 */
    public void addDecoyProtein(ProteinLowMem aProtein) {
        iPeptideProteins.add(aProtein);
        aProtein.addDecoyPeptide(this);
    }
/**
 * 
 * @param aAnnotation 
 */
    public void setAnnotation(String aAnnotation) {
        this.iAnnotation = aAnnotation;
    }
    public Connection getConnection(){
        return iConn;
    }
/**
 * 
 * @param pRSScore 
 */
    public void setPhosphoRSScore(Float pRSScore) {
        this.phosphoRSScore = pRSScore;
    }
/**
 * 
 * @return 
 */
    public Float getPhosphoRSScore() {
        return phosphoRSScore;
    }
/**
 * 
 * @return 
 */
    public Float getPhoshpoRSSequenceProbability() {
        return phoshpoRSSequenceProbability;
    }
/**
 * 
 * @param phoshpoRSSequenceProbability 
 */
    public void setPhoshpoRSSequenceProbability(Float phoshpoRSSequenceProbability) {
        this.phoshpoRSSequenceProbability = phoshpoRSSequenceProbability;
    }
/**
 * 
 * @return 
 */
    public Vector<Float> getPhosphoRSSiteProbabilities() {
        return iPhosphoRSSiteProbabilities;
    }
/**
 * 
 * @param lCharge
 * @return 
 */
    public double getPeptideMassForCharge(int lCharge){
        double lCalculatedMass = 0.0;
        //calculate the peptide mass
        for (int j = 0; j < iAminoAcidSequence.size(); j++) {
            lCalculatedMass += iAminoAcidSequence.get(j).getMonoisotopicMass();
            //check if it is not modified
            for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                if(iPeptideModificationPositions.get(m).getPosition() == j){
                    lCalculatedMass += iPeptideModifications.get(m).getDeltaMass();
                }
            }
        }
        lCalculatedMass = lCalculatedMass + 1.007825 + 15.994910 + 1.007825;
        lCalculatedMass = (lCalculatedMass  + ((double)lCharge * 1.007825)) / (double)lCharge;
        return lCalculatedMass;
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
