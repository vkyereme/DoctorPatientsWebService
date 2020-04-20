package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
public class DrPatientsRS {
	@Context
	private ServletContext sctx; // dependency injection
	private static DrPatientsList dPlist; // set in populate()

	public DrPatientsRS() {
	}

	@GET
	@Path("/xml")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getXml() {
		checkContext();
		return Response.ok(dPlist, "application/xml").build();
	}

	@GET
	@Path("/xml/{id: \\d+}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getXml(@PathParam("id") int id) {
		checkContext();
		return toRequestedType(id, "application/xml");
	}

	@GET
	@Path("/plain")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlain() {
		checkContext();
		return dPlist.toString();
	}

	@GET
	@Path("/plain/{id: \\d+}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlain(@PathParam("id") int id) {
		checkContext();

		return dPlist.find(id).toString();
	}

	@POST
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/create")
	public Response create(@FormParam("docName") String docName, @FormParam("patient") List<String> patient,
			@FormParam("insurNo") List<String> insurNo, @FormParam("num") int num) {
		checkContext();
		String msg = null;

		if (docName == null || patient == null || insurNo == null || num == 0) {
			msg = "Property 'docName' or 'patientName' or 'insurNo' is missing.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
		Iterator<String> patientI = patient.iterator();
		Iterator<String> insurNoI = insurNo.iterator();
		List<Patient> patients = new ArrayList<>();
		while (patientI.hasNext() && insurNoI.hasNext()) {
			String p = patientI.next();
			String ins = insurNoI.next();
			patients = addPatient(p, ins);
		}

		int id = addDoctor(docName, patients);
		msg = "Doctor " + id + " created: (name = " + docName + " with " + num + " patients, " + patients + ").\n";
		return Response.ok(msg, "text/plain").build();
	}

	@PUT
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/update")
	public Response update(@FormParam("id") int id, @FormParam("docName") String docName) {
		checkContext();

		// Check that sufficient data is present to do an edit.
		String msg = null;
		if (docName == null)
			msg = "No doctor's name is given: nothing to edit.\n";

		Doctor d = dPlist.find(id);
		if (d == null)
			msg = "There is no doctor with ID " + id + "\n";

		if (msg != null)
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		// Update.
		if (docName != null) {
			d.setName(docName);
		}

		msg = "Doctor " + id + " has been updated.\n";
		return Response.ok(msg, "text/plain").build();
	}

	@DELETE
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/delete/{id: \\d+}")
	public Response delete(@PathParam("id") int id) {
		checkContext();
		String msg = null;
		Doctor d = dPlist.find(id);
		if (d == null) {
			msg = "There is no doctor with ID " + id + ". Cannot delete.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
		dPlist.getDoctors().remove(d);
		msg = "Doctor " + id + " deleted.\n";

		return Response.ok(msg, "text/plain").build();
	}

	// populating the doctor patient list
	private void checkContext() {
		if (dPlist == null) {
			populate();

		}
	}

	private void populate() {
		dPlist = new DrPatientsList();

		String docFileName = "/WEB-INF/data/drs.db";
		InputStream in = sctx.getResourceAsStream(docFileName);

		// Read the data into the array of Doctors.
		if (in != null) {
			try {
				BufferedReader docreader = new BufferedReader(new InputStreamReader(in));
				String docrecord = null;

				int numOfPatients = 0;
				List<Patient> patientList = populatePatient();

				while ((docrecord = docreader.readLine()) != null) {

					String[] doctorParts = docrecord.split("!");
					numOfPatients = Integer.valueOf(doctorParts[1]);
					List<Patient> patients = new ArrayList<>();
					for (int j = 0; j < numOfPatients; j++) {

						patients.add(patientList.get(j));

					}
					List<Patient> patien2 = patientList;
					patien2.removeAll(patients);

					addDoctor(doctorParts[0], patients);
				}

			} catch (Exception e) {
				throw new RuntimeException("I/O failed!");
			}
		}
	}

	private List<Patient> populatePatient() {
		dPlist = new DrPatientsList();

		String patientFileName = "/WEB-INF/data/patients.db";
		InputStream in = sctx.getResourceAsStream(patientFileName);
		List<Patient> patients = new ArrayList<>();
		// Read the data into the array of Patients.
		if (in != null) {
			try {
				BufferedReader patientreader = new BufferedReader(new InputStreamReader(in));
				String patientrecord = null;

				while ((patientrecord = patientreader.readLine()) != null) {
					String[] patientParts = patientrecord.split("!");
					patients = addPatient(patientParts[0], patientParts[1]);
				}
			} catch (Exception e) {
				throw new RuntimeException("I/O failed!");
			}
		}
		return patients;
	}

	// Add a new Doctor to the list.
	private int addDoctor(String docName, List<Patient> patients) {
		int id = dPlist.addDoctor(docName, patients);
		return id;
	}

	// Add a new Patient to the list.
	private List<Patient> addPatient(String patientName, String insurNo) {
		List<Patient> patientList = dPlist.addPatient(patientName, insurNo);
		return patientList;
	}

	// Generate an HTTP error response or typed OK response.
	private Response toRequestedType(int id, String type) {
		Doctor doc = dPlist.find(id);
		if (doc == null) {
			String msg = id + " is a bad ID.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		} else
			return Response.ok(doc, type).build(); 
	}

}
