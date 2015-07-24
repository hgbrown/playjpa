package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Company;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.CompanyJpaRepository;
import repository.PersistenceException;

import java.util.List;

public class CompanyController extends Controller {

    static final transient Logger.ALogger LOG = Logger.of(CompanyController.class);

    protected static CompanyJpaRepository repository = new CompanyJpaRepository();

//    public CompanyController() {
//        this(new CompanyJpaRepository());
//    }
//
//    protected CompanyController(CompanyJpaRepository repository) {
//        this.repository = repository;
//    }

    @Transactional(readOnly = true)
    public static Result getAll() {
        try {
            final List<Company> companies = repository.getAll();
            try {
                return ok(Json.toJson(companies));
            } catch(Exception e) {
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
            final Company company = repository.get(id);

            try {
                return ok(Json.toJson(company));
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
                final Company company = convertJsonToCompany(jsonNode);
                try {
                    final Long generatedId = repository.create(company);
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
                final Company company = convertJsonToCompany(jsonNode);
                try {
                    final Company updated = repository.update(company, id);
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
        final List<Company> companies = repository.findByName(name, pageIndex, pageSize);
        return ok(Json.toJson(companies));
    }

    @Transactional
    public static Result updateFinancialHistory(Long id) {
        final JsonNode jsonNode = getRequestBodyAsJson();
        LOG.debug("jsonNode=[{}]", jsonNode);

        final Company company = repository.updateFinancialHistory(id, jsonNode);
        return ok(Json.toJson(company));
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
