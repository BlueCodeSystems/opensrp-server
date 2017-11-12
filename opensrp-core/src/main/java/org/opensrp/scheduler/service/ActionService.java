package org.opensrp.scheduler.service;

import static org.opensrp.common.AllConstants.ScheduleNames.ANC;
import static org.opensrp.common.AllConstants.ScheduleNames.CHILD;
import static org.opensrp.common.AllConstants.ScheduleNames.PNC;
import static org.opensrp.dto.BeneficiaryType.child;
import static org.opensrp.dto.BeneficiaryType.ec;
import static org.opensrp.dto.BeneficiaryType.elco;
import static org.opensrp.dto.BeneficiaryType.household;
import static org.opensrp.dto.BeneficiaryType.mother;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.opensrp.common.AllConstants.ScheduleNames;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.dto.BeneficiaryType;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.repository.AllActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionService {
    private AllActions allActions;
    private ReportActionService reportActionService;
    private final ScheduleService scheduleService;
    
    private static Logger logger = LoggerFactory.getLogger(ActionService.class
			.toString());
    @Autowired
    public ActionService(AllActions allActions, ReportActionService reportActionService,ScheduleService scheduleService) {
        this.allActions = allActions;
        this.reportActionService = reportActionService;
        this.scheduleService = scheduleService;
    }

    public List<Action> getNewAlertsForANM(String anmIdentifier, long timeStamp) {
        return allActions.findByANMIDAndTimeStamp(anmIdentifier, timeStamp);
    }

    public List<Action> findByCaseIdScheduleAndTimeStamp(String caseId, String schedule, DateTime start, DateTime end) {
		return allActions.findByCaseIdScheduleAndTimeStamp(caseId, schedule, start, end);
	}
    public void alertForBeneficiary(BeneficiaryType beneficiaryType, String caseID, String instanceId,  String anmIdentifier, String scheduleName, String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate) {
    	if (!(mother.equals(beneficiaryType)||child.equals(beneficiaryType)||ec.equals(beneficiaryType)||household.equals(beneficiaryType) || elco.equals(beneficiaryType))) {
            throw new IllegalArgumentException("Beneficiary Type : " + beneficiaryType + " is of unknown type");
        }
    	if(scheduleName.equals(ScheduleNames.BNF) || scheduleName.equals(ScheduleNames.CENCUS) || scheduleName.equals(ScheduleNames.ELCO) || scheduleName.equals(ScheduleNames.MIS_ELCO)){
    		this.ActionUpdateOrCreateForOther(beneficiaryType, caseID, instanceId, anmIdentifier, scheduleName, visitCode, alertStatus, startDate, expiryDate);
    		
    	}else if(scheduleName.equals(ScheduleNames.ANC) || scheduleName.equals(ScheduleNames.PNC) || scheduleName.equals(ScheduleNames.CHILD)){
    		this.ActionUpdateOrCreateForMotherType(beneficiaryType, caseID, instanceId, anmIdentifier, scheduleName, visitCode, alertStatus, startDate, expiryDate);
    		
    	}else{
    		
    	}
    	
    }

    public void markAllAlertsAsInactive(String entityId) {
        allActions.markAllAsInActiveFor(entityId);
    }

    public void markAlertAsInactive(String anmId, String entityId, String scheduleName) {
        allActions.markAlertAsInactiveFor(anmId, entityId, scheduleName);
    }

    public void markAlertAsClosed(String caseId, String anmIdentifier, String visitCode, String completionDate) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.markAlertAsClosed(visitCode, completionDate)));
    }
    
    public void closeBeneficiary(BeneficiaryType beneficiary, String caseId, String anmIdentifier, String reasonForClose) {
        allActions.add(new Action(caseId, anmIdentifier, ActionData.closeBeneficiary(beneficiary.name(), reasonForClose)));
    }

    public void reportForIndicator(String anmIdentifier, ActionData actionData) {
        allActions.add(new Action("", anmIdentifier, actionData));
    }

    public void deleteReportActions() {
        allActions.deleteAllByTarget("report");
    }
    public void ActionUpdateOrCreateForOther(BeneficiaryType beneficiaryType, String caseID, String instanceId,  String anmIdentifier, String scheduleName, String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate){
    	try{
	    	List<Action> existingAlerts = allActions.findAlertByANMIdEntityIdScheduleName(anmIdentifier, caseID, scheduleName);
	    	if(existingAlerts.size() > 0){ 	    	      
	        	
	    		updateDataAction(visitCode,alertStatus,startDate,expiryDate,existingAlerts);
	    	}
    	}catch(Exception e){
    		logger.info(e.getMessage());
    		
    	}
    }
    
    public void ActionUpdateOrCreateForMotherType(BeneficiaryType beneficiaryType, String caseID, String instanceId,  String anmIdentifier, String scheduleName, String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate){
    	
    	try{
	    	List<Action> existingAlerts = allActions.findAlertByANMIdEntityIdScheduleName(anmIdentifier, caseID, scheduleName);
	    	
	    	updateDataAction(visitCode,alertStatus,startDate,expiryDate,existingAlerts);
	    	if(existingAlerts.size() > 0){ 	    		
	        	long numOfDays =  this.getDaysDifference(expiryDate);
	        	
	        	if(ANC.equalsIgnoreCase(scheduleName) ){
	        		if(( numOfDays<=2)   && alertStatus.name().equalsIgnoreCase("urgent")){	        		
		    			scheduleService.fulfillMilestone(caseID, scheduleName, new LocalDate());
		    			
		    		  }else{
		    			logger.info("Date diffrenece required less or equal 2")	;
		    			
		    		 }
	        		
	        	}else if(PNC.equalsIgnoreCase(scheduleName) || CHILD.equalsIgnoreCase(scheduleName)){
		        	if(( numOfDays<=0 || numOfDays<=1)   && alertStatus.name().equalsIgnoreCase("urgent")){	        		
		    			scheduleService.fulfillMilestone(caseID, scheduleName, new LocalDate());
		    			
		    		  }else{
		    			logger.info("Date diffrenece required less or equal 1 or 0")	;
		    			
		    		 }
	        	}else{
	        		System.err.println("NOT PNC OR ENCC OR ANC");
	        	}
	        	
	        	
	        }else{
	        	System.err.println("No Doc found");
	        }
	        
	        
    	}catch(Exception e){
    		logger.info(e.getMessage());
    	}
    }
    
    public void updateDataAction( String visitCode, AlertStatus alertStatus, DateTime startDate, DateTime expiryDate,List<Action> existingAlerts){
    	existingAlerts.get(0).setRevision(existingAlerts.get(0).getRevision());
	   	existingAlerts.get(0).data().put("alertStatus", alertStatus.toString());
	   	existingAlerts.get(0).data().put("expiryDate", expiryDate.toLocalDate().toString());
	   	existingAlerts.get(0).data().put("startDate", startDate.toLocalDate().toString());
	   	existingAlerts.get(0).data().put("visitCode", visitCode);
	   	existingAlerts.get(0).markAsActive();
	   	existingAlerts.get(0).timestamp(Calendar.getInstance().getTimeInMillis()); 
	   	allActions.update(existingAlerts.get(0));
    }
    
   
    
   public long getDaysDifference(DateTime expiryDate){
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
   	   Date today = Calendar.getInstance().getTime();
   	   
   	   long days = 0;
	   try {
	   	 Date expiredDate = format.parse(expiryDate.toString());
	   	 String todayDate = format.format(today);
	   	 Date today_date = format.parse(todayDate);
	   	 long diff = expiredDate.getTime() - today_date.getTime();
	   	 days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	    } catch (ParseException e) {
	   		
	   		e.printStackTrace();
	   }
	   return days;
   }
   
   public long getActionTimestamp(String anmIdentifier, String caseID, String scheduleName){
	   List<Action> existingAlerts = allActions.findAlertByANMIdEntityIdScheduleName(anmIdentifier, caseID, scheduleName);
	   return existingAlerts.get(0).timestamp();
   }
}