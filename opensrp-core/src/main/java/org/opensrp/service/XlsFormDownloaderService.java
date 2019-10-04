package org.opensrp.service;



import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.ObjectInputStream.GetField;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jackson.JsonProcessingException;
import org.joda.time.DateTime;
import org.opensrp.util.FileCreator;
import org.opensrp.util.JsonParser;
import org.opensrp.util.NetClientGet;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.icu.math.BigDecimal;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author muhammad.ahmed@ihsinformatics.com
 *  Created on 17-September, 2015
 */
@Service
public class XlsFormDownloaderService {
	private NetClientGet netClientGet;
	private FileCreator fileCreator;
	private JsonParser jsonParser;
	
	private byte[] formJson=null; 
	public XlsFormDownloaderService() {
	netClientGet=new NetClientGet();
	fileCreator=new FileCreator();
	
	jsonParser=new JsonParser();
	}

	public static void main(String[] args) {
		try {
			
			System.out.print(new XlsFormDownloaderService().getFormDefinition());

			/*System.out.println(DateTime.now().getWeekOfWeekyear());
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "crvs_verbal_autopsy", "156735");
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "crvs_death_notification", "156734");
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "crvs_birth_notification", "156733");
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "crvs_pregnancy_notification", "156721");
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "new_member_registration", "148264");
			
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "family_registration_form", "148263");
			*/
			/*new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "vaccine_stock_position", "151804");
*/			

			
			/*new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "child_vaccination_enrollment", "135187");
			//-------------------------			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "child_vaccination_followup", "135199");
			//---------------------------
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "woman_tt_enrollement_form", "135200");
			//----------------------------
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "woman_tt_followup_form", "135203");
			
			*/
			
			
			/*new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "offsite_child_vaccination_followup", "115138");
			
			
			new XlsFormDownloaderService().downloadFormFiles("D:\\opensrpVaccinatorWkspc\\forms", 
					"maimoonak", "opensrp", JustForFun.Form, "offsite_woman_followup_form", "115135");*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String formatXML(String input)
    {
        try
        {
            final InputSource src = new InputSource(new StringReader(input));
            final Node document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(src).getDocumentElement();

            final DOMImplementationRegistry registry = DOMImplementationRegistry
                    .newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry
                    .getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print",
                    Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", false);

            return writer.writeToString(document);
        } catch (Exception e)
        {
            e.printStackTrace();
            return input;
        }
    }
	
	public String format(String unformattedXml) {
        try {
            final org.w3c.dom.Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(380);
            //format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private org.w3c.dom.Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
	
	public boolean downloadFormFiles(String directory,String username ,String formPath, String password,String formId, String formPk) throws IOException{
		
		String xmlData=netClientGet.convertToString("", formPath, formId);
		String modelData=netClientGet.getModel(xmlData);
		String formData=fileCreator.prettyFormat(netClientGet.getForm(xmlData));
		
		modelData=format(modelData);
		
		formData = formData.replaceAll("selected\\(", "contains(");
		formData = formData.replaceAll("<span.*lang=\"openmrs_code\".*</span>", "");
		formData = formData.replaceAll("<option value=\"openmrs_code\">openmrs_code</option>", "");
		
		formJson=netClientGet.downloadJson(username,password,  formPk);
		
		//formData=fileCreator.prettyFormat(formData);
		System.out.println(getFormDefinition());
		fileCreator.createFile("form_definition.json", fileCreator.osDirectorySet(directory)+formId, getFormDefinition().getBytes());
		return fileCreator.createFormFiles(fileCreator.osDirectorySet(directory)+formId, formId, formData.getBytes(), modelData.getBytes(), formJson);
	}
	
	public String getFormDefinition() throws JsonProcessingException, IOException{
		if(formJson==null){
			String s = "{\"name\":\"child_health_indicators\",\"type\":\"survey\",\"instance\":{\"encounter_type\":\"child health indicators\"},\"title\":\"Registrar Indicador Infantil\",\"id_string\":\"child_health_indicators_form\",\"sms_keyword\":\"child_health_indicators_form\",\"default_language\":\"Spanish\",\"version\":\"201908220840\",\"children\":[{\"name\":\"provider_uc\",\"type\":\"hidden\"},{\"name\":\"provider_town\",\"type\":\"hidden\"},{\"name\":\"provider_city\",\"type\":\"hidden\"},{\"name\":\"provider_province\",\"type\":\"hidden\"},{\"name\":\"provider_id\",\"type\":\"hidden\"},{\"name\":\"provider_location_id\",\"type\":\"hidden\",\"instance\":{\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"location_id\"}},{\"name\":\"provider_location_name\",\"type\":\"hidden\"},{\"name\":\"start\",\"type\":\"start\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:form start datetime\"}},{\"name\":\"end\",\"type\":\"end\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:form end datetime\"}},{\"name\":\"today\",\"type\":\"today\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:data enty datetime\"}},{\"name\":\"deviceid\",\"type\":\"deviceid\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:device id\"}},{\"name\":\"subscriberid\",\"type\":\"subscriberid\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:subscriber id\"}},{\"name\":\"simserial\",\"type\":\"simserial\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:sim serial number\"}},{\"name\":\"phonenumber\",\"type\":\"phonenumber\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:sim number\"}},{\"name\":\"client_reg_date\",\"label\":{\"Spanish\":\"Indraj ki Tareekh\",\"English\":\"Data Entry Date\"},\"type\":\"calculate\",\"bind\":{\"calculate\":\"${today}\"},\"instance\":{\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"}},{\"name\":\"program_id\",\"label\":{\"English\":\"Program ID\"},\"type\":\"hidden\",\"instance\":{\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"TBREACH Program ID\"}},{\"name\":\"program_id_note\",\"label\":{\"Spanish\":\"DNI: ${program_id}\",\"English\":\"DNI: ${program_id}\"},\"type\":\"note\",\"bind\":{\"required\":\"yes\"}},{\"name\":\"zero_tuberculosis\",\"type\":\"hidden\"},{\"name\":\"zero_antiherpatitis\",\"type\":\"hidden\"},{\"name\":\"two_antipolio\",\"type\":\"hidden\"},{\"name\":\"two_pentavalente\",\"type\":\"hidden\"},{\"name\":\"two_neumococo\",\"type\":\"hidden\"},{\"name\":\"two_rotavirus\",\"type\":\"hidden\"},{\"name\":\"four_antipolio\",\"type\":\"hidden\"},{\"name\":\"four_pentavalente\",\"type\":\"hidden\"},{\"name\":\"four_neumococo\",\"type\":\"hidden\"},{\"name\":\"four_rotavirus\",\"type\":\"hidden\"},{\"name\":\"six_antipolio\",\"type\":\"hidden\"},{\"name\":\"six_pentavalente\",\"type\":\"hidden\"},{\"name\":\"twelve_neumococo\",\"type\":\"hidden\"},{\"name\":\"twelve_sarampion\",\"type\":\"hidden\"},{\"name\":\"fifteen_antimaralica\",\"type\":\"hidden\"},{\"name\":\"age\",\"type\":\"hidden\"},{\"name\":\"first_name\",\"label\":{\"Spanish\":\"Naam\",\"English\":\"First Name\"},\"type\":\"hidden\",\"instance\":{\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\"}},{\"name\":\"last_name\",\"label\":{\"Spanish\":\"Walid ya shohar ka naam\",\"English\":\"Last Name\"},\"type\":\"hidden\",\"instance\":{\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\"}},{\"name\":\"pt_name_note\",\"label\":{\"Spanish\":\"Nombre del Paciente: ${first_name} ${last_name}\",\"English\":\"Patient Name: ${first_name} ${last_name}\"},\"type\":\"note\"},{\"name\":\"next_visit_date\",\"label\":{\"Spanish\":\"Fecha de siguiente visita\",\"English\":\"Next Visit Date\"},\"type\":\"date\",\"bind\":{\"required\":\"yes\",\"constraint\":\".> ${today}\",\"jr:constraintMsg\":{\"Spanish\":\"La fecha de la proxima visita debe ser mayor que la fecha actual\",\"English\":\"Next visit date must be greater than current date\"}},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:next visit date\"}},{\"name\":\"next_growth_monitoring\",\"label\":{\"Spanish\":\"Fecha de siguiente monitoreo de crecimiento\",\"English\":\"Next Growth Monitoring check-up\"},\"type\":\"date\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:next growth monitoring check-up\"}},{\"name\":\"weight\",\"label\":{\"Spanish\":\"Peso\",\"English\":\"Weight\"},\"hint\":{\"Spanish\":\"(kg)\",\"English\":\"(kg)\"},\"type\":\"decimal\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:weight\"}},{\"name\":\"height\",\"label\":{\"Spanish\":\"Estatura\",\"English\":\"Height\"},\"hint\":{\"Spanish\":\"(cm)\",\"English\":\"(cm)\"},\"type\":\"decimal\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:height\"}},{\"name\":\"height_weight_date\",\"label\":{\"Spanish\":\"Fecha de medicion de altura y peso\",\"English\":\"Height and weight measurement date\"},\"type\":\"date\",\"bind\":{\"constraint\":\".< ${today}\",\"jr:constraintMsg\":{\"Spanish\":\"La fecha debe ser menor que la fecha actual\",\"English\":\"Date must be less than current date\"}},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:height/weight measurement date\"}},{\"name\":\"hgb\",\"label\":{\"Spanish\":\"Nivel de hemoglobina\",\"English\":\"Haemoglobin Level\"},\"type\":\"decimal\",\"bind\":{\"constraint\":\".>0\",\"jr:constraintMsg\":{\"Spanish\":\"Debe ser mayor que 0\",\"English\":\"Must be greater than 0\"}},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:haemoglobin\"}},{\"name\":\"vaccines_zero_months\",\"label\":{\"Spanish\":\"Vacunas al mes 0\",\"English\":\"Vaccines at 0 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${zero_tuberculosis}='no' or ${zero_antiherpatitis}='no'\"},\"children\":[{\"name\":\"zero_tuberculosis_form\",\"label\":{\"Spanish\":\"Tuberculosis\",\"English\":\"Tuberculosis\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${zero_tuberculosis}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"zero_antiherpatitis_form\",\"label\":{\"Spanish\":\"Antiherpatitis\",\"English\":\"Antiherpatitis\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${zero_antiherpatitis}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"vaccines_two_months\",\"label\":{\"Spanish\":\"Vacunas a los 2 meses\",\"English\":\"Vaccines at 2 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${age}>=2 and (${two_antipolio}='no' or ${two_pentavalente}='no' or ${two_neumococo}='no' or ${two_rotavirus}='no')\"},\"children\":[{\"name\":\"two_antipolio_form\",\"label\":{\"Spanish\":\"Antipolio\",\"English\":\"Antipolio\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${two_antipolio}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"two_pentavalente_form\",\"label\":{\"Spanish\":\"Pentavalente\",\"English\":\"Pentavalente\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${two_pentavalente}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"two_neumococo_form\",\"label\":{\"Spanish\":\"Neumococo\",\"English\":\"Neumococo\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${two_neumococo}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"two_rotavirus_form\",\"label\":{\"Spanish\":\"Rotavirus\",\"English\":\"Rotavirus\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${two_rotavirus}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"vaccines_four_months\",\"label\":{\"Spanish\":\"Vacunas a las 4 meses\",\"English\":\"Vaccines at 4 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${age}>=4 and (${four_antipolio}='no' or ${four_pentavalente}='no' or ${four_neumococo}='no' or ${four_rotavirus}='no')\"},\"children\":[{\"name\":\"four_antipolio_form\",\"label\":{\"Spanish\":\"Antipolio\",\"English\":\"Antipolio\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${four_antipolio}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"four_pentavalente_form\",\"label\":{\"Spanish\":\"Pentavalente\",\"English\":\"Pentavalente\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${four_pentavalente}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"four_neumococo_form\",\"label\":{\"Spanish\":\"Neumococo\",\"English\":\"Neumococo\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${four_neumococo}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"four_rotavirus_form\",\"label\":{\"Spanish\":\"Rotavirus\",\"English\":\"Rotavirus\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${four_rotavirus}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"vaccines_six_months\",\"label\":{\"Spanish\":\"Vacunas a las 6 meses\",\"English\":\"Vaccines at 6 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${age}>=6 and (${six_antipolio}='no' or ${six_pentavalente}='no')\"},\"children\":[{\"name\":\"six_antipolio_form\",\"label\":{\"Spanish\":\"Antipolio\",\"English\":\"Antipolio\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${six_antipolio}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"six_pentavalente_form\",\"label\":{\"Spanish\":\"Pentavalente\",\"English\":\"Pentavalente\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${six_pentavalente}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"vaccines_twelve_months\",\"label\":{\"Spanish\":\"Vacunas a las 12 meses\",\"English\":\"Vaccines at 12 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${age}>=12 and (${twelve_neumococo}='no' or ${twelve_sarampion}='no')\"},\"children\":[{\"name\":\"twelve_neumococo_form\",\"label\":{\"Spanish\":\"Neumococo\",\"English\":\"Neumococo\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${twelve_neumococo}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"twelve_sarampion_form\",\"label\":{\"Spanish\":\"Sarampion\",\"English\":\"Sarampion\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${twelve_sarampion}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"vaccines_fifteen_months\",\"label\":{\"Spanish\":\"Vacunas a las 15 meses\",\"English\":\"Vaccines at 15 months\"},\"type\":\"group\",\"bind\":{\"relevant\":\"${age}>=15 and ${fifteen_antimaralica}='no'\"},\"children\":[{\"name\":\"fifteen_antimaralica_form\",\"label\":{\"Spanish\":\"Antimaralica\",\"English\":\"Antimaralica\"},\"type\":\"select one\",\"bind\":{\"required\":\"yes\",\"relevant\":\"${fifteen_antimaralica}='no'\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"deworming\",\"label\":{\"Spanish\":\"El ni\u00F1o ha recibido desparacitacion\",\"English\":\"Has the child received deworming\"},\"type\":\"select one\",\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:deworming\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"deworming_date\",\"label\":{\"Spanish\":\"Fecha\",\"English\":\"Date\"},\"type\":\"date\",\"bind\":{\"required\":\"yes\",\"constraint\":\".<=${today}\",\"jr:constraintMsg\":{\"Spanish\":\"La fecha no puede ser en el futuro\",\"English\":\"Date cannot be in future\"},\"relevant\":\"${deworming}='yes'\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:deworming date\"}},{\"name\":\"illnesses\",\"label\":{\"Spanish\":\"Enfermedades\",\"English\":\"Illnesses\"},\"type\":\"group\",\"children\":[{\"name\":\"diarrea\",\"label\":{\"Spanish\":\"Diarrea\",\"English\":\"Diarrea\"},\"default\":\"no\",\"type\":\"select one\",\"bind\":{\"required\":\"yes\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:diarrea\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"malaria\",\"label\":{\"Spanish\":\"Malaria\",\"English\":\"Malaria\"},\"default\":\"no\",\"type\":\"select one\",\"bind\":{\"required\":\"yes\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:malaria\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"cold\",\"label\":{\"Spanish\":\"Gripe\",\"English\":\"Cold/Flu\"},\"default\":\"no\",\"type\":\"select one\",\"bind\":{\"required\":\"yes\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:cold\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"pneumonia\",\"label\":{\"Spanish\":\"Neumon\u00EDa\",\"English\":\"Pneumonia\"},\"default\":\"no\",\"type\":\"select one\",\"bind\":{\"required\":\"yes\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:pneumonia\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]},{\"name\":\"bronchitis\",\"label\":{\"Spanish\":\"Bronchitis\",\"English\":\"Bronchitis\"},\"default\":\"no\",\"type\":\"select one\",\"bind\":{\"required\":\"yes\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:bronchitis\"},\"children\":[{\"name\":\"yes\",\"label\":{\"Spanish\":\"Si\",\"English\":\"Yes\"},\"instance\":{\"openmrs_code\":\"TBR:yes\"}},{\"name\":\"no\",\"label\":{\"Spanish\":\"No\",\"English\":\"No\"},\"instance\":{\"openmrs_code\":\"TBR:no\"}}]}]},{\"name\":\"date\",\"type\":\"hidden\",\"bind\":{\"required\":\"yes\",\"calculate\":\"${today}\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:sample collection date\"}},{\"name\":\"zero_tuberculosis_submit\",\"label\":{\"English\":\"Tuberculosis\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${zero_tuberculosis_form}='yes' or ${zero_tuberculosis_form}='no',${zero_tuberculosis_form},${zero_tuberculosis})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:0 months tuberculosis\"}},{\"name\":\"zero_antiherpatitis_submit\",\"label\":{\"English\":\"Antiherpatitis\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${zero_antiherpatitis_form}='yes' or ${zero_antiherpatitis_form}='no',${zero_antiherpatitis_form},${zero_antiherpatitis})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:0 months antiherpatitis\"}},{\"name\":\"two_antipolio_submit\",\"label\":{\"English\":\"Antipolio\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${two_antipolio_form}='yes' or ${two_antipolio_form}='no', ${two_antipolio_form}, ${two_antipolio} )\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:2 months antipolio\"}},{\"name\":\"two_pentavalente_submit\",\"label\":{\"English\":\"Pentavalente\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${two_pentavalente_form}='yes' or ${two_pentavalente_form}='no', ${two_pentavalente_form}, ${two_pentavalente})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:2 months pentavalente\"}},{\"name\":\"two_neumococo_submit\",\"label\":{\"English\":\"Neumococo\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${two_neumococo_form}='yes' or ${two_neumococo_form}='no', ${two_neumococo_form}, ${two_neumococo})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:2 months neumococo\"}},{\"name\":\"two_rotavirus_submit\",\"label\":{\"English\":\"Rotavirus\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${two_rotavirus_form}='yes' or ${two_rotavirus_form}='no', ${two_rotavirus_form}, ${two_rotavirus})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:2 months rotavirus\"}},{\"name\":\"four_antipolio_submit\",\"label\":{\"English\":\"Tuberculosis\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${four_antipolio_form}='yes' or ${four_antipolio_form}='no', ${four_antipolio_form}, ${four_antipolio} )\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:4 months antipolio\"}},{\"name\":\"four_pentavalente_submit\",\"label\":{\"English\":\"Pentavalente\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${four_pentavalente_form}='yes' or ${four_pentavalente_form}='no', ${four_pentavalente_form}, ${four_pentavalente})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:4 months pentavalente\"}},{\"name\":\"four_neumococo_submit\",\"label\":{\"English\":\"Neumococo\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${four_neumococo_form}='yes' or ${four_neumococo_form}='no', ${four_neumococo_form}, ${four_neumococo})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:4 months neumococo\"}},{\"name\":\"four_rotavirus_submit\",\"label\":{\"English\":\"Rotavirus\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${four_rotavirus_form}='yes' or ${four_rotavirus_form}='no', ${four_rotavirus_form}, ${four_rotavirus})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:4 months rotavirus\"}},{\"name\":\"six_antipolio_submit\",\"label\":{\"English\":\"Antipolio\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${six_antipolio_form}='yes' or ${six_antipolio_form}='no', ${six_antipolio_form}, ${six_antipolio} )\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:6 months antipolio\"}},{\"name\":\"six_pentavalente_submit\",\"label\":{\"English\":\"Pentavalente\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${six_pentavalente_form}='yes' or ${six_pentavalente_form}='no', ${six_pentavalente_form}, ${six_pentavalente})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:6 months pentavalente\"}},{\"name\":\"twelve_neumococo_submit\",\"label\":{\"English\":\"Neumococo\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${twelve_neumococo_form}='yes' or ${twelve_neumococo_form}='no', ${twelve_neumococo_form}, ${twelve_neumococo})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:12 months neumococo\"}},{\"name\":\"twelve_sarampion_submit\",\"label\":{\"English\":\"Sarampion\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${twelve_sarampion_form}='yes' or ${twelve_sarampion_form}='no', ${twelve_sarampion_form}, ${twelve_sarampion})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:12 months sarampion\"}},{\"name\":\"fifteen_antimaralica_submit\",\"label\":{\"English\":\"Antimaralica\"},\"type\":\"calculate\",\"bind\":{\"required\":\"yes\",\"calculate\":\"if(${fifteen_antimaralica_form}='yes' or ${fifteen_antimaralica_form}='no', ${fifteen_antimaralica_form}, ${fifteen_antimaralica})\"},\"instance\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"TBR:15 months antimaralica\"}},{\"name\":\"meta\",\"type\":\"group\",\"control\":{\"bodyless\":true},\"children\":[{\"name\":\"instanceID\",\"type\":\"calculate\",\"bind\":{\"readonly\":\"true()\",\"calculate\":\"concat('uuid:', uuid())\"}}]}]}";
			formJson =s.getBytes();
//			return "Data not found on server . Please retry again !";
			
		}
		return jsonParser.getFormDefinition(formJson);		
	}	
}
