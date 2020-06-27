define({ "api": [
  {
    "type": "get",
    "url": "/authors",
    "title": "Get list of Authors",
    "name": "getAuthors",
    "group": "Authors",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"authorId\":1111,\"firstName\":\"Ariel\",\"lastName\":\"Denham\"},\n{\"authorId\":1212,\"firstName\":\"John\",\"lastName\":\"Worsley\"} ]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/AuthorResource.java",
    "groupTitle": "Authors",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "post",
    "url": "/books/title/{title}/authorid/{aid}/subjectid/{sid}",
    "title": "Create a book",
    "name": "createBook",
    "group": "Books",
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/BookResource.java",
    "groupTitle": "Books",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "delete",
    "url": "?id={id}",
    "title": "Delete a book",
    "name": "deleteBook",
    "group": "Books",
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/BookResource.java",
    "groupTitle": "Books",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/{bookId}",
    "title": "Get author of a book",
    "name": "getAuthor",
    "group": "Books",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK ",
          "content": "[\n   {\"authorId\":1111,\"firstName\":\"Ariel\",\"lastName\":\"Denham\"}\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/BookResource.java",
    "groupTitle": "Books",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/{bookId}",
    "title": "Get a book",
    "name": "getBook",
    "group": "Books",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"bookId\":9898,\"title\":\"Title 1\",\"authorId\":\"1111\",\"subjectId\":\"Fiction\"}\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/BookResource.java",
    "groupTitle": "Books",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/books",
    "title": "Get list of Books",
    "name": "getBooks",
    "group": "Books",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"bookId\":9898,\"title\":\"Title\n1\",\"authorId\":\"1111\",\"subjectId\":\"Fiction\"},\n{\"bookId\":9999,\"title\":\"Title\n2\",\"authorId\":\"1212\",\"subjectId\":\"Nonfiction\"} ]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/BookResource.java",
    "groupTitle": "Books",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/subjects/findAuthors",
    "title": "Get list of Authors by Subject location",
    "name": "findAuthorsBySubject",
    "group": "Subjects",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"authorId\":1111,\"firstName\":\"Ariel\",\"lastName\":\"Denham\"},\n{\"authorId\":1212,\"firstName\":\"John\",\"lastName\":\"Worsley\"} ]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/SubjectResource.java",
    "groupTitle": "Subjects",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/subjects/{subjectId}",
    "title": "Get a Subject by ID",
    "name": "getSubject",
    "group": "Subjects",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"subjectId\":1111,\"subject\":\"Fiction\",\"location\":\"Location1\"}\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/SubjectResource.java",
    "groupTitle": "Subjects",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "get",
    "url": "/subjects",
    "title": "Get list of Subjects",
    "name": "getSubjects",
    "group": "Subjects",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"subjectId\":1111,\"subject\":\"Fiction\",\"location\":\"Location1\"},\n{\"subjectId\":1212,\"subject\":\"Nonfiction\",\"location\":\"Location2\"} ]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/SubjectResource.java",
    "groupTitle": "Subjects",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  },
  {
    "type": "put",
    "url": "/subjects",
    "title": "Update the location of a Subject",
    "name": "updateSubject",
    "group": "Subjects",
    "success": {
      "examples": [
        {
          "title": "Success-Response: HTTP/1.1 200 OK [",
          "content": "{\"subjectId\":1111,\"subject\":\"Fiction\",\"location\":\"Location1\"}\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/edu/asupoly/ser422/restexample/api/SubjectResource.java",
    "groupTitle": "Subjects",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "400",
            "optional": false,
            "field": "BadRequest",
            "description": "<p>Bad Request Encountered</p>"
          }
        ],
        "Error 5xx": [
          {
            "group": "Error 5xx",
            "type": "500",
            "optional": false,
            "field": "InternalServerError",
            "description": "<p>Something went wrong at server, Please contact the administrator!</p>"
          }
        ]
      }
    }
  }
] });
