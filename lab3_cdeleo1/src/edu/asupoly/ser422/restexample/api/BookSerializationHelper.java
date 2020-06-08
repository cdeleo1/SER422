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

import edu.asupoly.ser422.restexample.model.Book;

/*
 * This is an example of a simple custom serializer (converts a Java object to a JSON string).
 * Note all this one does is rename the property keys for Book, but what you do in the serialize
 * method is entirely up to you, so you may generalize to as rich a hypermedia format as you like.
 * 
 * Note that you would typically write a deserializer as well when you customize the JSON. A
 * deserializer goes in the opposite direction, converting JSON to a Java object. So, in this case
 */
public final class BookSerializationHelper {
	// some locally used constant naming our Book field names
	private static final String __BOOKID = "bookIdent";
	private static final String __TITLE = "title";
	private static final String __AUTHORID = "aid";
        private static final String __SUBJECTID = "sid";
	
	private final static BookSerializationHelper __me = new BookSerializationHelper();
	private ObjectMapper mapper = new ObjectMapper();
	private SimpleModule module = new SimpleModule();
	
	// Singleton
	private BookSerializationHelper() {
		module.addSerializer(Book.class, new BookJSON());
		module.addDeserializer(Book.class, new JSONBook());
		mapper.registerModule(module);
	}
	
	public static BookSerializationHelper getHelper() {
		return __me;
	}
	
	public String generateJSON(Book book) throws JsonProcessingException {
		// Since a custom serializer was added to the mapper via registerModule,
		// internally it will invoke the serialize method in the inner class below
		return mapper.writeValueAsString(book);
	}
	
	public Book consumeJSON(String json) throws IOException, JsonProcessingException {
		// A deserializer goes from JSON to the Object using the inverse process
		System.out.println("consumeJSON: " + json);
		return mapper.readValue(json, Book.class);
	}
	
	// Inner class for custom Book deserialization.
	// Loosely based on http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
    final private class JSONBook extends JsonDeserializer<Book>  {
		@Override
		public Book deserialize(JsonParser parser, DeserializationContext context)
				throws IOException, JsonProcessingException {
			Book book = new Book();
			JsonToken token = parser.nextToken();
			while (!parser.isClosed()) {
				System.out.print("Deserializer processing token: " + token.asString());
				if (token != null && JsonToken.FIELD_NAME.equals(token)) {
					// we have a JSON Field, get it and see which one we have
					String fieldName = parser.getCurrentName();
					System.out.println(", field name: " + fieldName);
					// Check for which of our 3 fields comes next and set the next token in there
					token = parser.nextToken();
					if (fieldName.equals(__BOOKID)) 
						book.setBookId(parser.getValueAsInt());
					else if (fieldName.equals(__TITLE))
						book.setTitle(parser.getValueAsString());
					else if (fieldName.equals(__AUTHORID))
						book.setAuthorId(parser.getValueAsInt());
                                        else if (fieldName.equals(__SUBJECTID))
						book.setSubjectId(parser.getValueAsInt());
				}
				token = parser.nextToken();
			}
			System.out.println("Deserializer returning Book: " + book);
			return book;
		}
    }
    
	// Inner class for custom Book serialization.
    final private class BookJSON extends JsonSerializer<Book>  {
       @Override
       public void serialize(Book book, JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonProcessingException {
           jgen.writeStartObject();	
           jgen.writeNumberField(__BOOKID, book.getBookId());
           jgen.writeStringField(__TITLE, book.getTitle());
           jgen.writeNumberField(__AUTHORID, book.getAuthorId());
           jgen.writeNumberField(__SUBJECTID, book.getSubjectId());
           jgen.writeEndObject();
       }
   }
}
