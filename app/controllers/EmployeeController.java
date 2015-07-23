package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Employee;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.JsonMapConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EmployeeController extends Controller {

    static final transient Logger.ALogger LOG = Logger.of(EmployeeController.class);

    @Transactional(readOnly = true)
    public static Result getAll() {
        final List<Employee> employees = JPA.em().createNamedQuery("Employee.getAll", Employee.class).getResultList();
        LOG.debug("employees=[{}]", employees);

        return ok(Json.toJson(employees));
    }

    @Transactional(readOnly = true)
    public static Result get(Long id) {
        final Employee employee = JPA.em().find(Employee.class, id);
        LOG.debug("employee=[{}]", employee);

        final JsonNode employeeJson = Json.toJson(employee);
        LOG.debug("employeeJson=[{}]", employeeJson);

        return ok(employeeJson);
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        final JsonNode jsonNode = getEmployeeAsJsonFromRequest();
        final Employee employee = convertJsonToEmployee(jsonNode);

        if (employee.getId() != null) {
            badRequest("Cannot create a employee with an id");
        }

        JPA.em().persist(employee);
        return ok();
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        final JsonNode jsonNode = getEmployeeAsJsonFromRequest();
        final Employee employee = convertJsonToEmployee(jsonNode);

        if (!id.equals(employee.getId())) {
            return badRequest("Id in url does not match id of employee");
        }

        JPA.em().merge(employee);
        return ok();
    }

    @Transactional
    public static Result delete(Long id) {
        final Employee employee = JPA.em().getReference(Employee.class, id);
        LOG.debug("employee=[{}]", employee);

        if (employee != null) {
            JPA.em().remove(employee);
            return ok();
        }
        return notFound();
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public static Result findByName(String name, int pageIndex, int pageSize) {
        final String queryString = "%" + name + "%";
        LOG.debug("queryString=[{}]", queryString);

        final List<Employee> employees = JPA.em().createNamedQuery("Company.findByName", Employee.class)
                .setParameter("name", queryString)
                .setFirstResult(pageIndex)
                .setMaxResults(pageSize)
                .getResultList();

        LOG.trace("employees=[{}]", employees);

        return ok(Json.toJson(employees));
    }

    @Transactional
    public static Result updateEmployeeProfile(Long id) {
        final Employee employee = JPA.em().find(Employee.class, id);
        LOG.debug("employee=[{}]", employee);

        final JsonNode jsonNode = getEmployeeAsJsonFromRequest();
        LOG.debug("jsonNode=[{}]", jsonNode);

        final Map<String, Object> map = JsonMapConverter.toMap(jsonNode);
        LOG.debug("map=[{}]", map);

        employee.setProfile(map);
        JPA.em().merge(employee);

        return ok(Json.toJson(employee));
    }

    @Transactional(readOnly = true)
    public static Result findByFirstNameAndLanguage(String firstName, String language) {
        final String sql = "SELECT * from employee where first_name = '" +firstName + "' and profile->>'language' = '" +language+ "'";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Employee> employees = JPA.em().createNativeQuery(sql, Employee.class).getResultList();
        LOG.debug("employees=[{}]", employees);

        return ok(Json.toJson(employees));
    }

    @Transactional(readOnly = true)
    public static Result findAllSpokenLanguages() {
        final String sql = "select profile->>'language' from employee";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Object[]> list = (List<Object[]>) JPA.em().createNativeQuery(sql).getResultList();
        LOG.debug("list=[{}]", list);

        final HashSet set = new HashSet<>(list);
        LOG.debug("set=[{}]", set);

        return ok(Json.toJson(set));
    }

    @Transactional(readOnly = true)
    public static Result findCountsOfLanguages() {
        final String sql = "SELECT profile->>'language' AS language, count(profile->>'language') from employee GROUP BY profile->>'language'";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Object[]> list = (List<Object[]>) JPA.em().createNativeQuery(sql).getResultList();
        LOG.debug("list=[{}]", list);

        final Map<String, Integer> resultMap = new HashMap<>();
        for(Object[] o : list) {
            final String language = (String) o[0];
            final Integer count = (Integer) o[1];
            resultMap.put(language, count);
        }

        return ok(Json.toJson(resultMap));
    }

    private static Employee convertJsonToEmployee(JsonNode jsonNode) {
        final Employee employee = Json.fromJson(jsonNode, Employee.class);
        LOG.debug("employee=[{}]", employee);

        return employee;
    }

    private static JsonNode getEmployeeAsJsonFromRequest() {
        final Http.Request request = request();
        LOG.debug("request=[{}]", request);

        final Http.RequestBody body = request.body();
        LOG.debug("body=[{}]", body);

        final JsonNode jsonNode = body.asJson();
        LOG.debug("jsonNode=[{}]", jsonNode);

        return jsonNode;
    }

}
