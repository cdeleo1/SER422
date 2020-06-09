package edu.asupoly.ser422.restexample.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asupoly.ser422.restexample.model.Author;
import edu.asupoly.ser422.restexample.model.Book;
import edu.asupoly.ser422.restexample.services.BooktownService;
import edu.asupoly.ser422.restexample.services.BooktownServiceFactory;

@Path("/books")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, 
    MediaType.TEXT_XML})
public class BookResource {

    private static BooktownService __bService = 
            BooktownServiceFactory.getInstance();

    // Technique for location header taken from
    // http://usna86-techbits.blogspot.com/2013/02/how-to-return-location-header-from.html
    @Context
    private UriInfo _uriInfo;

    /**
     * @apiDefine BadRequestError
     * @apiError (Error 4xx) {400} BadRequest Bad Request Encountered
     *
     */
    
    /**
     * @apiDefine ActivityNotFoundError
     * @apiError (Error 4xx) {404} NotFound Activity cannot be found
     *
     */
    
    /**
     * @apiDefine InternalServerError
     * @apiError (Error 5xx) {500} InternalServerError Something went wrong at
     * server, Please contact the administrator!
     *
     */
    
    /**
     * @apiDefine NotImplementedError
     * @apiError (Error 5xx) {501} NotImplemented The resource has not been
     * implemented. Please keep patience, our developers are working hard on
     * it!!
     *
     */
    
    /**
     * @api {get} /books Get list of Books
     * @apiName getBooks
     * @apiGroup Books
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
     * {"bookId":9898,"title":"Title
     * 1","authorId":"1111","subjectId":"Fiction"},
     * {"bookId":9999,"title":"Title
     * 2","authorId":"1212","subjectId":"Nonfiction"} ]
     *
     */
    @GET
    public List<Book> getBooks() {
        return __bService.getBooks();
    }

    /**
     * @api {get} /{bookId} Get a book
     * @apiName getBook
     * @apiGroup Books
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
     * {"bookId":9898,"title":"Title 1","authorId":"1111","subjectId":"Fiction"}
     * ]
     *
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{bookId}")
    public Response getBook(@PathParam("bookId") int bid) {
        Book book = __bService.getBook(bid);

        // BookSerializationHelper will build a slightly different JSON string 
        // and we still use the ResponseBuilder to use that. The key property 
        // names are changed in the result.
        try {
            String bString = BookSerializationHelper.getHelper().generateJSON(book);
            return Response.status(Response.Status.OK).entity(bString).build();
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
    
    /**
     * @api {get} /{bookId} Get author of a book
     * @apiName getAuthor
     * @apiGroup Books
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK 
     * [
     *    {"authorId":1111,"firstName":"Ariel","lastName":"Denham"}
     * ]
     *
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getAuthor/{bookId}")
    public Response findAuthorOfBook(@PathParam("bookId") int bid) {
        Author author = __bService.findAuthorOfBook(bid);

        // AuthorSerializationHelper will build a slightly different JSON string 
        // and we still use the ResponseBuilder to use that. The key property 
        // names are changed in the result.
        try {
            String aString = 
                    AuthorSerializationHelper.getHelper().generateJSON(author);
            return Response.status(Response.Status.OK).entity(aString).build();
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
    
    /**
     * @api {post} /books/title/{title}/authorid/{aid}/subjectid/{sid} Create a book
     * @apiName createBook
     * @apiGroup Books
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK 
     *
     */
    @POST
    @Consumes("application/json")
    @Path("/title/{title}/authorid/{aid}/subjectid/{sid}")
    public Response createBook(@PathParam("title") String title, 
            @PathParam("aid") int aid, @PathParam("sid") int sid) {
        int bid = __bService.createBook(title, aid, sid);
        if (bid == -1) {
            return Response.status(500).entity(
                    "{ \" EXCEPTION INSERTING INTO DATABASE! \"}").build();
        } else if (bid == 0) {
            return Response.status(500).entity(
                    "{ \" ERROR INSERTING INTO DATABASE! \"}").build();
        }
        return Response.status(201)
                .header("Location", String.format(
                        "%s/%d", _uriInfo.getAbsolutePath().toString(), bid))
                .entity("{ \"Book\" : \"" + bid + "\"}").build();
    }

    /**
     * @api {delete} ?id={id} Delete a book
     * @apiName deleteBook
     * @apiGroup Books
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 204 OK
     *
     */
    @DELETE
    public Response deleteBook(@QueryParam("id") int bid) {
        if (__bService.deleteBook(bid)) {
            return Response.status(204).build();
        } else {
            return Response.status(404, "{ \"message \" : \"No such Book " 
                    + bid + "\"}").build();
        }
    }
}
