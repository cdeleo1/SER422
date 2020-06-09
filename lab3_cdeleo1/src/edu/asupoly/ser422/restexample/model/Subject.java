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
        public void setLocation(String __location) {
		this.__location = __location;
	}
        public void setSubjectId(int __id) {
		this.__id = __id;
	}
        public void setSubject(String __subject) {
		this.__subject = __subject;
	}
        private int __id;
	private String __subject;
	private String __location;
}
