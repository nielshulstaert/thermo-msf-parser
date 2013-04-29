package com.compomics.thermo_msf_parser_API.lowmeminstance.controllers;

import com.compomics.thermo_msf_parser_API.lowmeminstance.model.MsfFile;
import com.compomics.thermo_msf_parser_API.enums.MsfVersion;
import com.compomics.thermo_msf_parser_API.highmeminstance.ProcessingNode;
import com.compomics.thermo_msf_parser_API.highmeminstance.ProcessingNodeParameter;
import com.compomics.thermo_msf_parser_API.interfaces.ProcessingNodeInterface;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 4/26/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessingNodeLowMemController implements ProcessingNodeInterface {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ProcessingNodeLowMemController.class);

    @Override
    public List<ProcessingNode> getAllProcessingNodes(MsfFile msfFile){
        HashMap<Integer, ProcessingNode> allNodesMap = new HashMap<Integer, ProcessingNode>();
        try {
            Statement stat = msfFile.getConnection().createStatement();
            ResultSet rs = stat.executeQuery("select * from ProcessingNodes");
            while (rs.next()) {
                allNodesMap.put(rs.getInt("ProcessingNodeNumber"),new ProcessingNode(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeID"), rs.getString("ProcessingNodeParentNumber"), rs.getString("NodeName"), rs.getString("FriendlyName"), rs.getInt("MajorVersion"), rs.getInt("MinorVersion"), rs.getString("NodeComment")));
            }
            rs.close();
            if (msfFile.getVersion()== MsfVersion.VERSION1_3) {
                rs = stat.executeQuery("select * from CustomDataProcessingNodes");
                while (rs.next()){
                    if(allNodesMap.get(rs.getInt("ProcessingNodeNumber")) != null){
                        allNodesMap.get(rs.getInt("ProcessingNodeNumber")).addCustomDataField(rs.getInt("FieldID"),rs.getString("FieldValue"));
                    }
                } 
            }
            rs = stat.executeQuery("select * from ProcessingNodeParameters");
            while (rs.next()) {
                ProcessingNodeParameter lNodeParameter = new ProcessingNodeParameter(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeId"), rs.getString("ParameterName"), rs.getString("FriendlyName"), rs.getInt("IntendedPurpose"), rs.getString("PurposeDetails"), rs.getInt("Advanced"), rs.getString("Category"), rs.getInt("Position"), rs.getString("ParameterValue"), rs.getString("ValueDisplayString"));

                if(allNodesMap.get(lNodeParameter.getProcessingNodeNumber()) != null){
                    allNodesMap.get(lNodeParameter.getProcessingNodeNumber()).addProcessingNodeParameter(lNodeParameter);
                }
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return new ArrayList<ProcessingNode>(allNodesMap.values());
    }
    
    @Override
    public String getQuantitationMethod(MsfFile msfFile){
        String iQuantitationMethod = "";
        try {
            PreparedStatement stat = msfFile.getConnection().prepareStatement("select ParameterValue from ProcessingNodeParameters where ParameterName = 'QuantificationMethod'");
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                iQuantitationMethod = rs.getString(1);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return iQuantitationMethod;
    }
    
    
    @Override
    public ProcessingNode getProcessingNodeByNumber(int processingNodeId,MsfFile msfFile) {
        
            ProcessingNode processingNodeToReturn = null;
            Statement stat;
        try {
            stat = msfFile.getConnection().createStatement();
            ResultSet rs = stat.executeQuery("select * from ProcessingNodes where ProcessingNodeNumber = "+processingNodeId);
            while(rs.next()){
               processingNodeToReturn = new ProcessingNode(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeID"), rs.getString("ProcessingNodeParentNumber"), rs.getString("NodeName"), rs.getString("FriendlyName"), rs.getInt("MajorVersion"), rs.getInt("MinorVersion"), rs.getString("NodeComment"));
            }
            rs.close();
            if (msfFile.getVersion()== MsfVersion.VERSION1_3) {
                rs = stat.executeQuery("select * from CustomDataProcessingNodes where ProcessingNodeNumber = "+processingNodeId);
                while (rs.next()){
                        processingNodeToReturn.addCustomDataField(rs.getInt("FieldID"),rs.getString("FieldValue"));
                    }
                }
            rs = stat.executeQuery("select * from ProcessingNodeParameters where processingNodeNumber = "+processingNodeId);
            while(rs.next()) {
                ProcessingNodeParameter lNodeParameter = new ProcessingNodeParameter(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeId"), rs.getString("ParameterName"), rs.getString("FriendlyName"), rs.getInt("IntendedPurpose"), rs.getString("PurposeDetails"), rs.getInt("Advanced"), rs.getString("Category"), rs.getInt("Position"), rs.getString("ParameterValue"), rs.getString("ValueDisplayString"));
                processingNodeToReturn.addProcessingNodeParameter(lNodeParameter);
            }    
            rs.close();
            stat.close();
        } catch (SQLException ex) {
            logger.error(ex);
        }    
        return processingNodeToReturn;
    }
    
    @Override
    public ProcessingNode getProcessingNodeByName(String processingNodeName, MsfFile msfFile){
            
            ProcessingNode processingNodeToReturn = null;
            Statement stat;
        try {
            stat = msfFile.getConnection().createStatement();
            ResultSet rs = stat.executeQuery("select * from ProcessingNodes where NodeName = '"+processingNodeName+"'");
            while(rs.next()){
               processingNodeToReturn = new ProcessingNode(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeID"), rs.getString("ProcessingNodeParentNumber"), rs.getString("NodeName"), rs.getString("FriendlyName"), rs.getInt("MajorVersion"), rs.getInt("MinorVersion"), rs.getString("NodeComment"));
            }
            rs.close();
            if (msfFile.getVersion()== MsfVersion.VERSION1_3) {
                rs = stat.executeQuery("select * from CustomDataProcessingNodes where ProcessingNodeNumber = "+processingNodeToReturn.getProcessingNodeId());
                while (rs.next()){
                        processingNodeToReturn.addCustomDataField(rs.getInt("FieldID"),rs.getString("FieldValue"));
                    }
                }
            rs = stat.executeQuery("select * from ProcessingNodeParameters where processingNodeNumber = "+processingNodeToReturn.getProcessingNodeId());
            while(rs.next()) {
                ProcessingNodeParameter lNodeParameter = new ProcessingNodeParameter(rs.getInt("ProcessingNodeNumber"), rs.getInt("ProcessingNodeId"), rs.getString("ParameterName"), rs.getString("FriendlyName"), rs.getInt("IntendedPurpose"), rs.getString("PurposeDetails"), rs.getInt("Advanced"), rs.getString("Category"), rs.getInt("Position"), rs.getString("ParameterValue"), rs.getString("ValueDisplayString"));
                processingNodeToReturn.addProcessingNodeParameter(lNodeParameter);
            }    
            rs.close();
            stat.close();
        } catch (SQLException ex) {
            logger.error(ex);
        }    
        return processingNodeToReturn;
    }
}
