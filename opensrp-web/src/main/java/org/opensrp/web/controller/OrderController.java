package org.opensrp.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Drug;
import org.opensrp.common.AllConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsHouseHold;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.HouseholdService;
import org.opensrp.connector.openmrs.service.OpenmrsOrderService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.Client;
import org.opensrp.form.domain.*;
import org.opensrp.domain.DrugOrder;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.domain.Event;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionConverter;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.repository.AllDrugOrders;
import org.opensrp.repository.AllDrugs;
import org.opensrp.scheduler.SystemEvent;
import org.opensrp.scheduler.TaskSchedulerService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.MultimediaService;
import org.opensrp.service.formSubmission.FormEntityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans. factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ch.lambdaj.function.convert.Converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class OrderController {
    private static Logger logger = LoggerFactory.getLogger(OrderController.class.toString());
    private AllDrugs allDrugs;
    private AllDrugOrders allDrugOrders;
    
    @Autowired
    public OrderController(AllDrugs allDrugs, AllDrugOrders allDrugOrders) {
        this.allDrugs=allDrugs;
        this.allDrugOrders = allDrugOrders;        
    }
    
    @RequestMapping(method = GET, value="/drugs")
    @ResponseBody
    private List<Drug> getAllDrugs() throws JSONException {
        List<Drug> druglist = allDrugs.getAll();
        return druglist;
    }
    
    @RequestMapping(method = GET, value="/drugorders")
    @ResponseBody
    private List<DrugOrder> getAllOrders() throws JSONException {
        List<DrugOrder> drugOrderList = allDrugOrders.getAll();
        return drugOrderList;
    }
    
}