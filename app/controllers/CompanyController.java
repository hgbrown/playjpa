package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Company;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

public class CompanyController extends Controller {

    static final transient Logger.ALogger LOG = Logger.of(CompanyController.class);

    @Transactional(readOnly = true)
    public static Result getAll() {
        final List<Company> companies = JPA.em().createNamedQuery("Company.getAll", Company.class).getResultList();
        LOG.debug("companies=[{}]", companies);

        return ok(Json.toJson(companies));
    }

    @Transactional(readOnly = true)
    public static Result get(Long id) {
        final Company company = JPA.em().find(Company.class, id);
        LOG.debug("company=[{}]", company);

        final JsonNode companyJson = Json.toJson(company);
        LOG.debug("companyJson=[{}]", companyJson);

        return ok(companyJson);
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        final JsonNode jsonNode = getRequestBodyAsJson();
        final Company company = convertJsonToCompany(jsonNode);

        if (company.getId() != null) {
            badRequest("Cannot create a company with an id");
        }

        JPA.em().persist(company);
        return ok();
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        final JsonNode jsonNode = getRequestBodyAsJson();
        final Company company = convertJsonToCompany(jsonNode);

        if (!id.equals(company.getId())) {
            return badRequest("Id in url does not match id of company");
        }

        JPA.em().merge(company);
        return ok();
    }

    @Transactional
    public static Result delete(Long id) {
        final Company company = JPA.em().getReference(Company.class, id);
        LOG.debug("company=[{}]", company);

        if (company != null) {
            JPA.em().remove(company);
            return ok();
        }
        return notFound();
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public static Result findByName(String name, int pageIndex, int pageSize) {
        final String queryString = "%" + name + "%";
        LOG.debug("queryString=[{}]", queryString);

        final List<Company> companies = JPA.em().createNamedQuery("Company.findByName", Company.class)
                .setParameter("name", queryString)
                .setFirstResult(pageIndex)
                .setMaxResults(pageSize)
                .getResultList();

        LOG.trace("companies=[{}]", companies);

        return ok(Json.toJson(companies));
    }

    @Transactional
    public static Result updateFinancialHistory(Long id) {
        final Company company = JPA.em().find(Company.class, id);
        LOG.debug("company=[{}]", company);

        final JsonNode jsonNode = getRequestBodyAsJson();
        LOG.debug("jsonNode=[{}]", jsonNode);

        company.setFinancialHistory(jsonNode.toString());
        JPA.em().merge(company);
        return ok();
    }

    private static Company convertJsonToCompany(JsonNode jsonNode) {
        final Company company = Json.fromJson(jsonNode, Company.class);
        LOG.debug("company=[{}]", company);

        return company;
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
