package zeebe.camunda.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


@SpringBootApplication
@EnableZeebeClient
@RestController
public class WorkerApplication {
	static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	static ConsoleHandler handler = new ConsoleHandler();

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
		logger.setLevel(Level.ALL);
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
	}

	@JobWorker(type = "UpdateCMSRecord", autoComplete = true)
	public void updateCSMRecord(final JobClient client, final ActivatedJob job, @Variable String id, @Variable String first_Name) {
		if (first_Name.equalsIgnoreCase("gustavo mendoza"))
		{
		String errorCodeName = "FailedToCreateRecord";
		client
		   .newThrowErrorCommand(job)
		   .errorCode(errorCodeName)
		   .send();
		
	}
		logger.info("Confirming user added with ID: " + id + " to CMS...");
		}

	@JobWorker(type = "CreateSupportAccount")
	public void createSupportAccount(final JobClient client, final ActivatedJob job, @Variable String id) {
		UUID created_account_id = UUID.randomUUID();
		logger.info("Generating confirmation for user with ID: " + id +"...");
		logger.info("Generated certificate ID: " + created_account_id);

		client.newCompleteCommand(job.getKey())
				.variables("{\"confirmationID\": \"" + created_account_id + "\"}")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@JobWorker(type = "GrantPlaformAccess", autoComplete = true)
	public void grantPlaformAccess(final JobClient client, final ActivatedJob job, @Variable String id, @Variable String confirmationID, @Variable String first_Name) {
		
	//	logger.info("Retrieving confirmation ID: " + confirmationID + " from external database...");
	//	logger.info("Granting access to person with name: " + first_Name + " from external database...");
	}
	
	@JobWorker(type = "RemoveCreatedRecords", autoComplete = true)
	public void RemoveCreatedRecords(final JobClient client, final ActivatedJob job, @Variable String id, @Variable String create_account_id) {
		logger.info("Deleting entries for Confirmation ID: " + create_account_id + " from CMS...");
	}
	
	//___________________________________________________________________________Customer Onboarding______________________________________________________________________________//
	
	@JobWorker(type = "email", autoComplete = true)
	public void SendEmail(final JobClient client, final ActivatedJob job, @Variable String product) {
		logger.info("Sending request to customer for documentation for the product requested: " + product + " via email...");
	}
	
	//___________________________________________________________________________Quarantine______________________________________________________________________________//
	
	@JobWorker(type = "notify_person_to_quarantine", autoComplete = true)
	public void notifyPersonToQuarantine(final JobClient client, final ActivatedJob job, @Variable String person_uuid) {
		logger.info("Retrieving contact details for person " + person_uuid + " from external database...");
		logger.info("Sending notification to person " + person_uuid + " to quarantine...");
	}

	@JobWorker(type = "generate_certificate_of_recovery")
	public void generateCertificateOfRecovery(final JobClient client, final ActivatedJob job, @Variable String person_uuid) {
		UUID recovery_certificate_uuid = UUID.randomUUID();
		logger.info("Generating certificate of recovery for person " + person_uuid +"...");
		logger.info("Generated certificate ID: " + recovery_certificate_uuid);
		logger.info("Storing Recovery Certificate in external database...");

		client.newCompleteCommand(job.getKey())
				.variables("{\"recovery_certificate_uuid\": \"" + recovery_certificate_uuid + "\"}")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@JobWorker(type = "send_certificate_of_recovery", autoComplete = true)
	public void sendCertificateOfRecovery(final JobClient client, final ActivatedJob job, @Variable String person_uuid, @Variable String recovery_certificate_uuid) {
		logger.info("Retrieving Recovery Certificate " + recovery_certificate_uuid + " from external database...");
		logger.info("Retrieving contact details for person " + person_uuid + "from external database...");
		logger.info("Sending Recovery Certificate to person " + person_uuid + ". Enjoy that ice-cream!");
	}
	
	
}
