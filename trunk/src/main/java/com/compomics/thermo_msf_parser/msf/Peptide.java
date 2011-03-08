package com.compomics.thermo_msf_parser.msf;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 18-Feb-2011
 * Time: 09:17:23
 * To change this template use File | Settings | File Templates.
 */
public class Peptide {

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
    private Vector<ScoreType> iScoreTypes = new Vector<ScoreType>();
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
    private Vector<Protein> iPeptideProteins = new Vector<Protein>();
    /**
     * The modifications linked to this peptide
     */
    private Vector<Modification> iPeptideModifications = new Vector<Modification>();
    /**
     * The modifications positions of the modifications in the iPeptideModifications Vector
     */
    private Vector<ModificationPosition> iPeptideModificationPositions = new Vector<ModificationPosition>();
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
    private Vector<FragmentIon> iTheoreticalFragmentIons;
    /**
     * This vector holds the peptide sequence in amino acid objects
     */
    private Vector<AminoAcid> iAminoAcidSequence = new Vector<AminoAcid>();
    /**
     * The spectrum linked to this peptide
     */
    private Spectrum iParentSpectrum;

    /**
     * Constructor for the peptide
     * @param iPeptideId The peptide id
     * @param iSpectrumId The spectrum id
     * @param iConfidenceLevel The confidence level
     * @param iSequence The sequence
     * @param iTotalIonsCount The total ion count
     * @param iMatchedIonsCount The matched ion count
     * @param iAnnotation The annotation
     * @param iAminoAcids The amino acids found in the msf file
     */
    public Peptide(int iPeptideId, int iSpectrumId, int iConfidenceLevel, String iSequence, int iTotalIonsCount, int iMatchedIonsCount, String iAnnotation, Vector<AminoAcid> iAminoAcids) {
        this.iPeptideId = iPeptideId;
        this.iSpectrumId = iSpectrumId;
        this.iConfidenceLevel = iConfidenceLevel;
        this.iSequence = iSequence;
        this.iTotalIonsCount = iTotalIonsCount;
        this.iMatchedIonsCount = iMatchedIonsCount;
        this.iAnnotation = iAnnotation;
        this.iAminoAcids = iAminoAcids;
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
        if(iAminoAcidSequence.size() != iSequence.length()){
            //error
            System.out.println("ERROR " + iSequence);
        }

    }

    /**
     * This method will calculate the mass based on the charge
     * @param lCharge The charge
     * @return double with the mass
     */
    public double getPeptideMassForCharge(int lCharge){
        double lCalculatedMass = 0.0;
        //calculate the peptide mass
        for (int j = 0; j < iAminoAcidSequence.size(); j++) {
            lCalculatedMass = lCalculatedMass + iAminoAcidSequence.get(j).getMonoisotopicMass();
            //check if it is not modified
            for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                if(iPeptideModificationPositions.get(m).getPosition() == j){
                    lCalculatedMass = lCalculatedMass + iPeptideModifications.get(m).getDeltaMass();
                }
            }
        }
        lCalculatedMass = lCalculatedMass + 1.007825 + 15.994910 + 1.007825;
        lCalculatedMass = (lCalculatedMass  + ((double)lCharge * 1.007825)) / (double)lCharge;
        return lCalculatedMass;
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
    public Vector<ScoreType> getScoreTypes() {
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
    public Vector<Protein> getPeptideProteins() {
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
    public Vector<FragmentIon> getTheoreticalFragmentIons() {
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
    public Spectrum getParentSpectrum() {
        return iParentSpectrum;
    }

    /**
     * Setter for the parent spectrum
     * @param lSpectrum The parent spectrum to set
     */
    public void setParentSpectrum(Spectrum lSpectrum){
        this.iParentSpectrum = lSpectrum;
    }

    /**
     * Getter for a score by score type
     * @param lScoreType The score type of the requested score
     * @return double with the score
     */
    public Double getScoreByScoreType(ScoreType lScoreType){
        Double lScore = null;

        for(int i = 0; i<iScoreTypes.size(); i ++){
            if(lScoreType.getDescription().equalsIgnoreCase(iScoreTypes.get(i).getDescription())){
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
    public void setScore(double iScore, int iScoreTypeid, Vector<ScoreType> lScoreTypes) {
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
    public void addProtein(Protein lProtein) {
        iPeptideProteins.add(lProtein);
        lProtein.addPeptide(this);
    }

    /**
     * This method will add an amino acid modification to this peptide
     * @param lMod The modification
     * @param lModPos The modification position
     */
    public void addModification(Modification lMod, ModificationPosition lModPos) {
        iPeptideModifications.add(lMod);
        iPeptideModificationPositions.add(lModPos);
        if (lModPos.isNterm()) {
            iHasNTermModification = true;
        }
    }

    /**
     * Getter for the proteins linked to this peptide
     * @return Vector with the proteins linked to this peptide
     */
    public Vector<Protein> getProteins() {
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
                iModifiedPeptide = iModifiedPeptide + iSequence.charAt(c);
                for (int m = 0; m < iPeptideModifications.size(); m++) {
                    if (iPeptideModificationPositions.get(m).getPosition() == c && !iPeptideModificationPositions.get(m).isNterm()) {
                        iModifiedPeptide = iModifiedPeptide + "<" + iPeptideModifications.get(m).getAbbreviation() + ">";
                    }
                }
            }
            //do the C terminus
            iModifiedPeptide = iModifiedPeptide + "-COOH";

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

        iTheoreticalFragmentIons = new Vector<FragmentIon>();
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
                    bMass = bMass + iAminoAcidSequence.get(j).getMonoisotopicMass();
                    //check if it is not modified
                    for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                        if(iPeptideModificationPositions.get(m).getPosition() == j){
                            bMass = bMass + iPeptideModifications.get(m).getDeltaMass();
                        }
                    }

                }
                // Each peptide mass is added to the y ion mass, taking the reverse direction (from the C terminal end)
                for (int j = 0; j <= i; j++) {
                    yMass = yMass + iAminoAcidSequence.get((iAminoAcidSequence.size() - 1) - j).getMonoisotopicMass();
                    for(int m = 0; m<iPeptideModificationPositions.size(); m ++){
                        if(iPeptideModificationPositions.get(m).getPosition() == (iAminoAcidSequence.size() - 1) - j){
                            yMass = yMass + iPeptideModifications.get(m).getDeltaMass();
                        }
                    }
                }
                //add 1 Hydrogen for the Y ions (for the NH2 terminus) and 1 H and 1 O for the OH group
                yMass = yMass + lHydrogenMass + lHydrogenMass + lOxygenMass;

                // Create an instance for each fragment ion

                //B Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass + charge * lHydrogenMass) / charge, FragmentIonType.b, i + 1, charge, bMass, yMass));
                //BNH3 Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.NH3_b, i + 1, charge, bMass, yMass));
                //BH2O Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass - lOxygenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.H2O_b, i + 1, charge, bMass, yMass));
                //A Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass - lOxygenMass - lCarbonMass + charge * lHydrogenMass) / charge, FragmentIonType.a, i + 1, charge, bMass, yMass));
                //ANH3 Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass - lOxygenMass - lCarbonMass - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.NH3_a, i + 1, charge, bMass, yMass));
                //AH2O Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass - 2 * lOxygenMass - lCarbonMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.H20_a, i + 1, charge, bMass, yMass));
                //C Ion
                iTheoreticalFragmentIons.add(new FragmentIon((bMass + lNitrogenMass + 3 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.c, i + 1, charge, bMass, yMass));


                // Create an instance of the fragment y ion
                //Y Ion
                iTheoreticalFragmentIons.add(new FragmentIon((yMass + charge * lHydrogenMass) / charge, FragmentIonType.y, i + 1, charge, bMass, yMass));
                //YNH3 Ion
                iTheoreticalFragmentIons.add(new FragmentIon((yMass - lNitrogenMass - 3 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.NH3_y, i + 1, charge, bMass, yMass));
                //YH2O Ion
                iTheoreticalFragmentIons.add(new FragmentIon((yMass - 2 * lHydrogenMass - lOxygenMass + charge * lHydrogenMass) / charge, FragmentIonType.H2O_y, i + 1, charge, bMass, yMass));
                //X Ion
                iTheoreticalFragmentIons.add(new FragmentIon((yMass + lCarbonMass + lOxygenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.x, i + 1, charge, bMass, yMass));
                //Z Ion
                iTheoreticalFragmentIons.add(new FragmentIon((yMass - lNitrogenMass - 2 * lHydrogenMass + charge * lHydrogenMass) / charge, FragmentIonType.z, i + 1, charge, bMass, yMass));

            }
        }
    }

    /**
     * This method will give the fragment ions for a specific type and charge
     * @param lCharge The charge of the fragment ions wanted
     * @param lTypes The typs of fragment ions wanted
     * @return Vector with the requested fragment ions
     */
    public Vector<FragmentIon> getFragmentIonsByTypeAndCharge(int lCharge, Vector<FragmentIonType> lTypes){
        Vector<FragmentIon> lResult = new Vector<FragmentIon>();
        for(int i = 0; i<iTheoreticalFragmentIons.size(); i ++){
            if(iTheoreticalFragmentIons.get(i).getCharge() == lCharge){
                boolean lPass = false;
                for(int t = 0; t<lTypes.size(); t ++){
                    if(iTheoreticalFragmentIons.get(i).getFragmentIonType() == lTypes.get(t)){
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
}
