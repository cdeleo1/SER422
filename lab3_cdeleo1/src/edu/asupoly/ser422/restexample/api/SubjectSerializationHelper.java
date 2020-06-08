package edu.asupoly.ser422.restexample.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import edu.asupoly.ser422.restexample.model.Subject;

/*
 * This is an example of a simple custom serializer (converts a Java object to a JSON string).
 * Note all this one does is rename the property keys for Subject, but what you do in the serialize
 * method is entirely up to you, so you may generalize to as rich a hypermedia format as you like.
 * 
 * Note that you would typically write a deserializer as well when you customize the JSON. A
 * deserializer goes in the opposite direction, converting JSON to a Java object. So, in this case
 */
public final class SubjectSerializationHelper {
	// some locally used constant naming our Subject field names
	private static final String __SUBJECTID = "subjectIdent";
	private static final String __SUBJECT = "subject";
	private static final String __LOCATION = "location";
	
	private final static SubjectSerializationHelper __me = new SubjectSerializationHelper();
	private ObjectMapper mapper = new ObjectMapper();
	private SimpleModule module = new SimpleModule();
	
	// Singleton
	private SubjectSerializationHelper() {
		module.addSerializer(Subject.class, new SubjectJSON());
		module.addDeserializer(Subject.class, new JSONSubject());
		mapper.registerModule(module);
	}
	
	public static SubjectSerializationHelper getHelper() {
		return __me;
	}
	
	public String generateJSON(Subject subject) throws JsonProcessingException {
		// Since a custom serializer was added to the mapper via registerModule,
		// internally it will invoke the serialize method in the inner class below
		return mapper.writeValueAsString(subject);
	}
	
	public Subject consumeJSON(String json) throws IOException, JsonProcessingException {
		// A deserializer goes from JSON to the Object using the inverse process
		System.out.println("consumeJSON: " + json);
		return mapper.readValue(json, Subject.class);
	}
	
	// Inner class for custom Subject deserialization.
	// Loosely based on http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
    final private class JSONSubject extends JsonDeserializer<Subject>  {
		@Override
		public Subject deserialize(JsonParser parser, DeserializationContext context)
				throws IOException, JsonProcessingException {
			Subject subject = new Subject();
			JsonToken token = parser.nextToken();
			while (!parser.isClosed()) {
				System.out.print("Deserializer processing token: " + token.asString());
				if (token != null && JsonToken.FIELD_NAME.equals(token)) {
					// we have a JSON Field, get it and see which one we have
					String fieldName = parser.getCurrentName();
					System.out.println(", field name: " + fieldName);
					// Check for which of our 3 fields comes next and set the next token in there
					token = parser.nextToken();
                                        if (fieldName.equals(__LOCATION))
						subject.setLocation(parser.getValueAsString());
                                        else if (fieldName.equals(__SUBJECT))
						subject.setSubject(parser.getValueAsString());
                                        else if (fieldName.equals(__SUBJECTID)) 
						subject.setSubjectId(parser.getValueAsInt());
				}
				token = parser.nextToken();
			}
			System.out.println("Deserializer returning Subject: " + subject);
			return subject;
		}
    }
    
	// Inner class for custom Subject serialization.
    final private class SubjectJSON extends JsonSerializer<Subject>  {
       @Override
       public void serialize(Subject subject, JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonProcessingException {
           jgen.writeStartObject();	
           jgen.writeNumberField(__SUBJECTID, subject.getSubjectId());
           jgen.writeStringField(__SUBJECT, subject.getSubject());
           jgen.writeStringField(__LOCATION, subject.getLocation());
           jgen.writeEndObject();
       }
   }
}
