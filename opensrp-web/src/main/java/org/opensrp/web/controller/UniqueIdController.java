package org.opensrp.web.controller;

import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.opensrp.service.OpenmrsIDService;
import org.opensrp.web.utils.PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;

@Controller
@RequestMapping("/uniqueids")
public class UniqueIdController {
	
	private static Logger logger = LoggerFactory.getLogger(UniqueIdController.class.toString());
	
	
	
	@Autowired
	OpenmrsIDService openmrsIdService;
	/**
	 * Download extra ids from openmrs if less than the specified batch size, convert the ids to qr and print to a pdf
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/print")
	@ResponseBody
	public ResponseEntity<String> thisMonthDataSendTODHIS2(HttpServletRequest request,HttpServletResponse response) throws JSONException {
		
		String message = "";
		try {
			Integer numberToGenerate = Integer.valueOf(getStringFilter("batchSize", request));

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentPrincipalName = authentication.getName();

			openmrsIdService.downloadAndSaveIds(numberToGenerate,currentPrincipalName);
			List<String> idsToPrint = openmrsIdService.getNotUsedIdsAsString(numberToGenerate);
			SimpleDateFormat df= new SimpleDateFormat("dd-MM-yyyy");
			
			
			String fileName="QRCodes_".concat(df.format(new Date())).concat("_").concat(currentPrincipalName).concat("_"+numberToGenerate+".pdf");
			ByteArrayOutputStream byteArrayOutputStream = PdfUtil.generatePdf(idsToPrint,140, 140, 1, 5);
			if(byteArrayOutputStream.size()>0){
				//mark ids as used
				openmrsIdService.markIdsAsUsed(idsToPrint);
			}
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + fileName);

			OutputStream os = response.getOutputStream();
			byteArrayOutputStream.writeTo(os);
			os.flush();
			os.close();
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
		return new ResponseEntity<>(new Gson().toJson("" + message), HttpStatus.OK);
		
	}
	
}
