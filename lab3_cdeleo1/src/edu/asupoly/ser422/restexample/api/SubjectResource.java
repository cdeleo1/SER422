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

import edu.asupoly.ser422.restexample.model.Subject;
import edu.asupoly.ser422.restexample.services.BooktownService;
import edu.asupoly.ser422.restexample.services.BooktownServiceFactory;

@Path("/subjects")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class SubjectResource {

    private static BooktownService __bService = BooktownServiceFactory.getInstance();

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
     * @api {get} /subjects Get list of Subjects
     * @apiName getSubjects
     * @apiGroup Subjects
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
     * {"subjectId":1111,"subject":"Fiction","location":"Location1"},
     * {"subjectId":1212,"subject":"Nonfiction","location":"Location2"} ]
     *
     *
     */
    @GET
    public List<Subject> getSubjects() {
        return __bService.getSubjects();
    }

    /**
     * @api {get} /subjects/{subjectId} Get a Subject by ID
     * @apiName getSubject
     * @apiGroup Subjects
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
     * {"subjectId":1111,"subject":"Fiction","location":"Location1"}
     * ]
     *
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{subjectId}")
    public Response getSubject(@PathParam("subjectId") int sid) {
        Subject subject = __bService.getSubject(sid);

        // SubjectSerializationHelper will build a slightly different JSON 
        // string and we still use the ResponseBuilder to use that. The key 
        // property names are changed in the result.
        try {
            String sString = SubjectSerializationHelper.getHelper().generateJSON(subject);
            return Response.status(Response.Status.OK).entity(sString).build();
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
    
    /**
     * @api {put} /subjects Update the location of a Subject
     * @apiName updateSubject
     * @apiGroup Subjects
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
     * {"subjectId":1111,"subject":"Fiction","location":"Location1"}
     * ]
     *
     */
    @PUT
    @Consumes("application/json")
    public Response updateSubject(String json) {
        try {
            Subject s = SubjectSerializationHelper.getHelper().consumeJSON(json);
            if (__bService.updateSubject(s)) {
                // In the response payload it would still use Jackson's default 
                // serializer, so we directly invoke our serializer so the PUT 
                // payload reflects what it should.
                String sString = SubjectSerializationHelper.getHelper().generateJSON(s);
                return Response.status(201).entity(sString).build();
            } else {
                return Response.status(404, "{ \"message \" : \"No such Subject " 
                        + s.getSubjectId() + "\"}").build();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return Response.status(500, 
                    "{ \"message \" : \"Internal server error deserializing "
                            + "Subject JSON\"}").build();
        }
    }

}
