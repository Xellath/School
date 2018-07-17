package server;

import server.exceptions.RequestException;
import spark.Request;
import spark.Response;

public interface HandlerInterface {
	
	public Response add(Request request, Response response, DBManager db) throws RequestException;
	public Response update(Request request, Response response, DBManager db) throws RequestException;
	public Response remove(Request request, Response response, DBManager db) throws RequestException;
	public Response getAll(Request request, Response response, DBManager db) throws RequestException;
	public Response get(Request request, Response response, DBManager db) throws RequestException;
}