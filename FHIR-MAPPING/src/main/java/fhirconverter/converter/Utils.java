package fhirconverter.converter;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;


public final class Utils{
	private Utils(){
	//No Constructing 
	}
  
	public static JsonNode loadJsonScheme(final String filePath) throws Exception
    	{
	
        	try {
			return JsonLoader.fromPath(filePath);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
   	}
	
	public static boolean validateScheme(final JsonNode newJson, final String filePath) throws Exception{
		
		try{
			final JsonNode jsonSchema = Utils.loadJsonScheme(filePath);
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonSchema schema = factory.getJsonSchema(jsonSchema);
			ProcessingReport report;
			report = schema.validate(newJson);
			System.out.println(report);
			return report.isSuccess();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * This method removes duplicate person records and makes the response readable by XML parser
	 * @param finalresponse
	 */
	public static String removeDuplicateRecords(String response) {
		
		response = response.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "\t");
		response = response.replaceAll("<people>", "\n");
		response = response.replaceAll("</people>", "\n");
		response = response.replaceAll("</person>", "</person>\n");
		String[] people = response.split("\n");

		Set<String> set = new HashSet<String>();
		for(int i = 0; i < people.length; i++){
			 set.add(people[i]);
		}
		String finalresponse = set.toString();
		finalresponse = finalresponse.replaceAll("</person>,", "</person>");
		finalresponse = finalresponse.replaceAll("]", "");
		finalresponse = finalresponse.replace("[", "");
		finalresponse = finalresponse.replace(">,", ">");
		
		return "<people>" + finalresponse + "</people>";
	
	}
	
	public static HashMap<String,String> getProperties(String domainDatabase){
		HashMap<String,String> connectionCreds = new HashMap<String,String>();	
		try {
			Properties properties = new Properties();		
			FileReader reader = new FileReader("config.properties");
			properties.load(reader);
			connectionCreds.put("baseURL", properties.getProperty(domainDatabase+"-baseURL"));
			connectionCreds.put("username", properties.getProperty(domainDatabase+"-username"));
			connectionCreds.put("password", properties.getProperty(domainDatabase+"-password"));
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return connectionCreds;
	}
	
	/**
	 * This method takes the XML response from openEMPI as input parameter. The parameter is list of identifier
	 * domain in XML object that is present in OpenEMPI. This method iterates through the XML and returns list
	 * of identifier domain
	 * 
	 * @param parameters in XML formats
	 * @return List<String> 
	 */
	public static List<String> convertToList(String parameters) {

		List<String> identifierList = new ArrayList<>();

		JSONObject jsonFromXML = XML.toJSONObject(parameters);
		if (jsonFromXML.has("identifierDomains")) {
			
			JSONObject identifierDomainObj = jsonFromXML.optJSONObject("identifierDomains");
			JSONArray identifierDomains = identifierDomainObj.optJSONArray("identifierDomain");
			for (int i = 0; i < identifierDomains.length(); i++) {
				JSONObject identifierDomain = identifierDomains.getJSONObject(i);
				if (identifierDomain.has("identifierDomainName")) {
					String identifierDomainName = identifierDomain.optString("identifierDomainName");
					identifierList.add(identifierDomainName);
				}
			}
		}
		return identifierList;
	}
	
	public static JSONObject readJsonFile(){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("aql_path.json"));
			JSONObject jsonObj = (JSONObject) obj;	
			return jsonObj;
 		}catch (FileNotFoundException e) {
            		e.printStackTrace();
        	} catch (IOException e) {
            		e.printStackTrace();
        	} catch (ParseException e) {
            		e.printStackTrace();
        	}
		return null;
	}
}
