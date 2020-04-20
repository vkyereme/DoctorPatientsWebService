package main;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "doctor")
public class Doctor implements Comparable<Doctor> {
    private String name;   // doctor name
    private List<Patient> patients;  // list of patients
    private int    id;    // identifier used as lookup-key

    public Doctor() { }

    @Override
    public String toString() {
    	return name + " --  " + patients + "\n";
    }
    
    //** properties
    public void setName(String name) {
    	this.name = name;
    }
    @XmlElement
    public String getName() {
    	return this.name;
    }

    public void setPatients(List<Patient> patients) {
    	this.patients = patients;
    }
    @XmlElement
    public List<Patient> getPatients() {
    	return this.patients;
    }

    public void setId(int id) {
    	this.id = id;
    }
    @XmlElement
    public int getId() {
    	return this.id;
    }  

    // implementation of Comparable interface
    public int compareTo(Doctor other) {
    	return this.id - other.id;
    }
	

}
