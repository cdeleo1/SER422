package edu.asupoly.ser422.restexample.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Subject {
	public Subject() {	
	}
	public Subject (int id, String s, String l) {
		this.__id = id; 
		this.__subject = s;
		this.__location = l;
	}
	public int getSubjectId() {
		return __id;
	}
	public String getSubject() {
		return __subject;
	}
	public String getLocation() {
		return __location;
	}
        public void setLocation(String location) {
		this.__location = location;
	}
        public void setSubjectId(int id) {
		this.__id = id;
	}
        public void setSubject(String subject) {
		this.__subject = subject;
	}
        private int __id;
	private String __subject;
	private String __location;
}
