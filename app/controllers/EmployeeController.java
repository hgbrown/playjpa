package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Employee;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.EmployeeJpaRepository;
import repository.PersistenceException;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EmployeeController extends Controller {

    static final transient Logger.ALogger LOG = Logger.of(EmployeeController.class);

    static EmployeeJpaRepository repository = new EmployeeJpaRepository();

    @Transactional(readOnly = true)
    public static Result getAll() {
        try {
            final List<Employee> employees = repository.getAll();
            try {
                return ok(Json.toJson(employees));
            } catch (Exception e) {
                LOG.error("Unable to convert response back to JSON", e);
                return internalServerError(e.getMessage());
            }
        } catch(PersistenceException e) {
            LOG.error("Unable to getAll", e);
            return badRequest(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public static Result get(Long id) {
        try {
            final Employee employee = repository.get(id);
            try {
                return ok(Json.toJson(employee));
            } catch(Exception e) {
                LOG.error("Unable to convert to JSON", e);
                return internalServerError(e.getMessage());
            }
        } catch (PersistenceException e) {
            LOG.error("Unable to get by id", e);
            return badRequest(e.getMessage());
        }
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        try {
            final JsonNode jsonNode = getRequestBodyAsJson();
            try {
                final Employee employee = convertJsonToEmployee(jsonNode);
                try {
                    final Long generatedId = repository.create(employee);
                    LOG.debug("generatedId=[{}]", generatedId);
                    return ok("{ \"id\" : \"" + generatedId + "\"}");
                } catch(PersistenceException e) {
                    LOG.error("Unable to update instance", e);
                    return badRequest(e.getMessage());
                }
            } catch(Exception e) {
                LOG.error("Unable to convert JSON to company instance", e);
                return badRequest(e.getMessage());
            }
        } catch(Exception e) {
            LOG.error("Unable to get JSON from request");
            return badRequest(e.getMessage());
        }
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        try {
            final JsonNode jsonNode = getRequestBodyAsJson();
            try {
                final Employee employee = convertJsonToEmployee(jsonNode);
                try {
                    final Employee updated = repository.update(employee, id);
                    try {
                        return ok(Json.toJson(updated));
                    } catch(Exception e) {
                        LOG.error("Unable to convert persisted entity back to JSON", e);
                        return internalServerError(e.getMessage());
                    }
                } catch(PersistenceException e) {
                    LOG.error("Unable to update instance", e);
                    return badRequest(e.getMessage());
                }
            } catch(Exception e) {
                LOG.error("Unable to convert JSON to company instance", e);
                return badRequest(e.getMessage());
            }
        } catch(Exception e) {
            LOG.error("Unable to get JSON from request");
            return badRequest(e.getMessage());
        }
    }

    @Transactional
    public static Result delete(Long id) {
        try {
            final boolean removed = repository.remove(id);
            if(removed) {
                return ok();
            }
        } catch (PersistenceException e) {
            LOG.error("Unable to remove instance", e);
            return badRequest(e.getMessage());
        }
        return notFound();
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public static Result findByName(String name, int pageIndex, int pageSize) {
        final List<Employee> employees = repository.findByName(name, pageIndex, pageSize);
        return ok(Json.toJson(employees));
    }

    @Transactional
    public static Result updateEmployeeProfile(Long id) {
        final JsonNode jsonNode = getRequestBodyAsJson();
        LOG.debug("jsonNode=[{}]", jsonNode);

        final Employee employee = repository.updateEmployeeProfile(id, jsonNode);

        return ok(Json.toJson(employee));
    }

    @Transactional(readOnly = true)
    public static Result findByFirstNameAndLanguage(String firstName, String language) {
        final List<Employee> employees = repository.findByFirstNameAndLanguage(firstName, language);
        return ok(Json.toJson(employees));
    }

    @Transactional(readOnly = true)
    public static Result findAllSpokenLanguages() {
        final HashSet set = repository.findAllSpokenLanguages();
        return ok(Json.toJson(set));
    }

    @Transactional(readOnly = true)
    public static Result findCountsOfLanguages() {
        final Map<String, BigInteger> resultMap = repository.findCountsOfLanguages();
        return ok(Json.toJson(resultMap));
    }

    private static Employee convertJsonToEmployee(JsonNode jsonNode) {
        final Employee employee = Json.fromJson(jsonNode, Employee.class);
        LOG.debug("employee=[{}]", employee);

        return employee;
    }

    private static JsonNode getRequestBodyAsJson() {
        final Http.Request request = request();
        LOG.debug("request=[{}]", request);

        final Http.RequestBody body = request.body();
        LOG.debug("body=[{}]", body);

        final JsonNode jsonNode = body.asJson();
        LOG.debug("jsonNode=[{}]", jsonNode);

        return jsonNode;
    }

}
