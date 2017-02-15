package org.opensrp.register.mcare.action;

import static org.opensrp.common.AllConstants.BnfFollowUpVisitFields.SCHEDULE_BNF_IME;
import static org.opensrp.dto.BeneficiaryType.household;
import static org.opensrp.dto.BeneficiaryType.members;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.common.AllConstants.BnfFollowUpVisitFields;
import org.opensrp.common.AllConstants.ELCOSchedulesConstantsImediate;
import org.opensrp.common.AllConstants.ScheduleNames;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.BeneficiaryType;
import org.opensrp.register.mcare.domain.Members;
import org.opensrp.register.mcare.domain.HouseHold;
import org.opensrp.register.mcare.repository.AllHouseHolds;
import org.opensrp.register.mcare.repository.AllMembers;
import org.opensrp.register.mcare.service.MultimediaRegisterService;
import org.opensrp.register.mcare.service.scheduling.ScheduleLogService;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.HookedEvent;
import org.opensrp.scheduler.MilestoneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
@Component
@Qualifier("AlertCreationAction")
public class AlertCreationAction implements HookedEvent {
	private HealthSchedulerService scheduler;
	private AllHouseHolds allHouseHolds;
	private AllMembers allMembers;
	private ScheduleLogService scheduleLogService;
	private MultimediaRegisterService multimediaRegisterService;
	private static Logger logger = LoggerFactory.getLogger(AlertCreationAction.class.toString());
	@Autowired
	public AlertCreationAction(HealthSchedulerService scheduler,
			AllHouseHolds allHouseHolds, AllMembers allMembers, 
			ScheduleLogService scheduleLogService, MultimediaRegisterService multimediaRegisterService) {
		this.scheduler = scheduler;
		this.allHouseHolds = allHouseHolds;
		this.allMembers = allMembers;
		this.scheduleLogService = scheduleLogService;
		this.multimediaRegisterService = multimediaRegisterService;
	}

	@Override
	public void invoke(MilestoneEvent event, Map<String, String> extraData) {
		BeneficiaryType beneficiaryType = BeneficiaryType.from(extraData
				.get("beneficiaryType"));

		// TODO: Get rid of this horrible if-else after Motech-Platform fixes
		// the bug related to metadata in motech-schedule-tracking.
		String instanceId = null;
		String providerId = null;
		String caseID = event.externalId();
		DateTime startOfEarliestWindow = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		
		if (household.equals(beneficiaryType)) {
			HouseHold houseHold = allHouseHolds.findByCaseId(caseID);
			if (houseHold != null) {
				instanceId= houseHold.INSTANCEID();
				providerId = houseHold.PROVIDERID();
				startOfEarliestWindow = DateTime.parse(houseHold.getToday(),formatter);
			}
		} else if (members.equals(beneficiaryType)) {
			
			Members members = allMembers.findByCaseId(caseID);
			
			if (members != null) {
				instanceId= members.INSTANCEID();
				providerId = members.PROVIDERID();
				startOfEarliestWindow = DateTime.parse(members.getToday(),formatter);
			}
		}
		else {
			throw new IllegalArgumentException("Beneficiary Type : "
					+ beneficiaryType + " is of unknown type");
		}

		scheduler.alertFor(event.windowName(), beneficiaryType, caseID, instanceId, providerId, parseScheduleName(event.scheduleName()), parseScheduleName(event.milestoneName()),
				startOfEarliestWindow, event.startOfDueWindow(), event.startOfLateWindow(), event.startOfMaxWindow());
	}
	
	public String parseScheduleName(String scheduleName){
		if(scheduleName.equalsIgnoreCase(ScheduleNames.IMD_SCHEDULE_Woman_BNF)){
    		return scheduleName.replace(ScheduleNames.IMD_SCHEDULE_Woman_BNF, ScheduleNames.SCHEDULE_Woman_BNF);
    	}
    	else if(scheduleName.equalsIgnoreCase(ScheduleNames.IMD_ELCO_SCHEDULE_PSRF)){
    		return scheduleName.replace(ScheduleNames.IMD_ELCO_SCHEDULE_PSRF, ScheduleNames.ELCO_SCHEDULE_PSRF);
    	}
    	else if(scheduleName.equalsIgnoreCase(ScheduleNames.IMD_child_bcg)){
    		return scheduleName.replace(ScheduleNames.IMD_child_bcg, ScheduleNames.child_bcg);
    	}
    	else if(scheduleName.equalsIgnoreCase(ScheduleNames.IMD_Adolescent_Health)){
    		return scheduleName.replace(ScheduleNames.IMD_Adolescent_Health, ScheduleNames.Adolescent_Health);
    	}
    	else return scheduleName;    
    	
    }

	@Override
	public void scheduleSaveToOpenMRSMilestone(Enrollment el,List<Action> alertActions ) {		
		try {
			scheduleLogService.saveActionDataToOpenMrsMilestoneTrack(el, alertActions);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.info("From scheduleSaveToOpenMRSMilestone :"+e.getMessage());
		}
		
	}

	@Override
	public void saveMultimediaToRegistry(Multimedia multimediaFile) {
		multimediaRegisterService.saveMultimediaFileToRegistry(multimediaFile);
	}

}
