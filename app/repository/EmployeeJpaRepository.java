package repository;


import com.fasterxml.jackson.databind.JsonNode;
import jpa.model.Employee;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import util.JsonMapConverter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EmployeeJpaRepository extends JpaRepositoryBase<Long, Employee> {

    static final transient Logger.ALogger LOG = Logger.of(EmployeeJpaRepository.class);

    public EmployeeJpaRepository() {
        super(Long.class, Employee.class);
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public List<Employee> findByName(String name, int pageIndex, int pageSize) {
        final String queryString = "%" + name + "%";
        LOG.debug("queryString=[{}]", queryString);

        final List<Employee> employees = JPA.em().createNamedQuery("Employee.findByName", Employee.class)
                .setParameter("name", queryString)
                .setFirstResult(pageIndex)
                .setMaxResults(pageSize)
                .getResultList();
        LOG.trace("employees=[{}]", employees);

        return employees;
    }

    @Transactional
    public Employee updateEmployeeProfile(Long id, JsonNode jsonNode) {
        final Employee employee = JPA.em().find(Employee.class, id);
        LOG.debug("employee=[{}]", employee);

        final Map<String, Object> map = JsonMapConverter.toMap(jsonNode);
        LOG.debug("map=[{}]", map);

        employee.setProfile(map);
        final Employee merged = JPA.em().merge(employee);
        LOG.debug("merged=[{}]", merged);

        return merged;
    }

    @Transactional(readOnly = true)
    public List<Employee> findByFirstNameAndLanguage(String firstName, String language) {
        final String sql = "SELECT * from employee where first_name = '" +firstName + "' and profile->>'language' = '" +language+ "'";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Employee> employees = JPA.em().createNativeQuery(sql, Employee.class).getResultList();
        LOG.debug("employees=[{}]", employees);

        return employees;
    }

    @Transactional(readOnly = true)
    public HashSet findAllSpokenLanguages() {
        final String sql = "select profile->>'language' from employee";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Object[]> list = (List<Object[]>) JPA.em().createNativeQuery(sql).getResultList();
        LOG.debug("list=[{}]", list);

        final HashSet set = new HashSet<>(list);
        LOG.debug("set=[{}]", set);

        return set;
    }

    @Transactional(readOnly = true)
    public Map<String, BigInteger> findCountsOfLanguages() {
        final String sql = "SELECT profile->>'language' AS language, count(profile->>'language') from employee GROUP BY profile->>'language'";
        LOG.debug("sql=[{}]", sql);

        //noinspection unchecked
        final List<Object[]> list = (List<Object[]>) JPA.em().createNativeQuery(sql).getResultList();
        LOG.debug("list=[{}]", list);

        final Map<String, BigInteger> resultMap = new HashMap<>();
        for(Object[] o : list) {
            final String language = (String) o[0];
            final BigInteger count = (BigInteger) o[1];
            resultMap.put(language, count);
        }

        return resultMap;
    }
}
