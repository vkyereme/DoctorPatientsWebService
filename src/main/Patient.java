package main;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "patient")
public class Patient implements Comparable<Patient> {
    private String name;   // patient name
    private String insurNo;  // insurance number
    private int    id;    // identifier used as lookup-key

    public Patient() { }

    @Override
    public String toString() {
    	return String.format("%2d: ", id) + name + " ==> " + insurNo + "\n";
    }
    
    //** properties
    public void setName(String name) {
    	this.name = name;
    }
    @XmlElement
    public String getName() {
    	return this.name;
    }

    public void setInsurNo(String insurNo) {
    	this.insurNo = insurNo;
    }
    @XmlElement
    public String getInsurNo() {
    	return this.insurNo;
    }

    public void setId(int id) {
    	this.id = id;
    }
    @XmlElement
    public int getId() {
    	return this.id;
    }

    // implementation of Comparable interface
    public int compareTo(Patient other) {
    	return this.id - other.id;
    }

}
