package controllers;

import jpa.model.Company;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import repository.CompanyJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static utils.TestHelper.resultToClass;


public class CompanyControllerUnitTest {

    CompanyJpaRepositoryMock mockRepository;

    @Before
    public void setUp() throws Exception {
        mockRepository = new CompanyJpaRepositoryMock();
        CompanyController.repository = mockRepository;
    }

    @Test
    public void sanityTest() throws Exception {
        assertThat(mockRepository, notNullValue());
    }

    @Test
    public void shouldBeAbleToGetAll() throws Exception {
        final Result result = CompanyController.getAll();
        final List companies = resultToClass(result, List.class);

        assertThat(companies.size(), is(2));
    }

    @Test
    public void shouldBeAbleToGetById() throws Exception {
        final Result result = CompanyController.get(1L);
        final Company company = resultToClass(result, Company.class);

        assertThat(company, notNullValue());
        assertThat(company.getName(), is("Google"));
    }

    private static final class CompanyJpaRepositoryMock extends CompanyJpaRepository {

        private final Map<Long, Company> DB = new ConcurrentHashMap<>();

        public CompanyJpaRepositoryMock() {
            DB.put(1L, toCompany(1L, "Google", "0001"));
            DB.put(2L, toCompany(2L, "Apple", "0002"));
        }

        @Override
        public List<Company> getAll() {
            return new ArrayList<>(DB.values());
        }

        @Override
        public Company get(Long id) {
            return DB.get(id);
        }

        private Company toCompany(Long id, String name, String registrationNumber) {
            final Company c = new Company();
            c.setId(id);
            c.setName(name);
            c.setRegistrationNumber(registrationNumber);
            return c;
        }
    }
}