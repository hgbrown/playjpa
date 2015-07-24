package repository;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Company;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.util.List;

public class CompanyJpaRepository extends JpaRepositoryBase<Long, Company> {

    public CompanyJpaRepository() {
        super(Long.class, Company.class);
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public List<Company> findByName(String name, int pageIndex, int pageSize) {
        final String queryString = "%" + name + "%";
        LOG.debug("queryString=[{}]", queryString);

        final List<Company> companies = JPA.em().createNamedQuery("Company.findByName", Company.class)
                .setParameter("name", queryString)
                .setFirstResult(pageIndex)
                .setMaxResults(pageSize)
                .getResultList();
        LOG.trace("companies=[{}]", companies);

        return companies;
    }

    @Transactional
    public Company updateFinancialHistory(Long id, JsonNode jsonNode) {
        final Company company = JPA.em().find(Company.class, id);
        LOG.debug("company=[{}]", company);

        company.setFinancialHistory(jsonNode.toString());
        final Company merged = JPA.em().merge(company);
        LOG.debug("merged=[{}]", merged);

        return merged;
    }

}
