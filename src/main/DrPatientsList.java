package main;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "drPatientsList")
public class DrPatientsList {
    private List<Doctor> doctors; 
    private List<Patient> patients;
    private AtomicInteger docId;
    private AtomicInteger pId;

    public DrPatientsList() { 
	doctors = new CopyOnWriteArrayList<Doctor>(); 
	patients = new CopyOnWriteArrayList<Patient>();
	docId = new AtomicInteger();
	pId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "doctors") 
    public List<Doctor> getDoctors() { 
	return this.doctors;
    } 
    public void setDoctors(List<Doctor> doctors) { 
	this.doctors = doctors;
    }

    @Override
    public String toString() {
	String s = "";
	for (Doctor d : doctors) s += d.toString();
	return s;
    }

    public Doctor find(int id) {
	Doctor doc = null;
	// Search the list -- for now, the list is short enough that
	// a linear search is ok but binary search would be better if the
	// list got to be an order-of-magnitude larger in size.
	for (Doctor d : doctors) {
	    if (d.getId() == id) {
		doc = d;
		break;
	    }
	}	
	return doc;
    }
    
    public int addDoctor(String docName, List<Patient> patients) {
	int id = docId.incrementAndGet();
	Doctor d = new Doctor();
	d.setName(docName);
	d.setId(id);
	d.setPatients(patients);
	doctors.add(d);
	return id;
    }
    
    public List<Patient> addPatient(String patientName, String insurNo) {
	int id = pId.incrementAndGet();
	Patient p = new Patient();
	p.setName(patientName);
	p.setInsurNo(insurNo);
	p.setId(id);
	patients.add(p);
	return patients;
    }

}
