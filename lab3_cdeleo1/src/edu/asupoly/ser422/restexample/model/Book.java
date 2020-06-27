package edu.asupoly.ser422.restexample.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Book {
	public Book() {	
	}
	
	public Book(int id, String t, int aid, int sid) {
		this.__id = id;
		this.__title = t;
		this.__authorId = aid;
		this.__subjectId = sid;
	}
	
	public int getBookId() {
		return __id;
	}
	public void setBookId(int __id) {
		this.__id = __id;
	}
	public String getTitle() {
		return __title;
	}
	public void setTitle(String __title) {
		this.__title = __title;
	}
	public int getAuthorId() {
		return __authorId;
	}
	public void setAuthorId(int __authorId) {
		this.__authorId = __authorId;
	}
	public int getSubjectId() {
		return __subjectId;
	}
	public void setSubjectId(int __subjectId) {
		this.__subjectId = __subjectId;
	}
        public String toString() {
		return "Book ID " + getBookId() + ", title " + getTitle() + 
                        ", aid " + getAuthorId() + ", sid " + getSubjectId();
	}
        
        private int __id;
	private String __title;
	private int __authorId;
	private int __subjectId;
}
