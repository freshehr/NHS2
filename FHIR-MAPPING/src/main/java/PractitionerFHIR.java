import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Practitioner;

import java.io.IOException;

public class PractitionerFHIR extends OpenEMPIbase {

	public String convertFHIR(){ // call for converting, change this to practitioner	
		return "Practitioner";
	}	

}

