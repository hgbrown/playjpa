package repository;

import jpa.model.Company;
import org.junit.Before;
import org.junit.Test;
import utils.AbstractFakeApplication;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.db.jpa.JPA.withTransaction;


public class CompanyJpaRepositoryIntegrationTest extends AbstractFakeApplication {

    private CompanyJpaRepository cut;

    @Before
    public void setUp() throws Exception {
        cut = new CompanyJpaRepository();
    }

    @Test
    public void sanityTest() throws Exception {
        assertThat(cut, notNullValue());
    }

    @Test
    public void shouldBeAbleToGetAll() throws Throwable {
        withTransaction(() -> {
            final List<Company> all = cut.getAll();
            System.out.println(all);
            assertThat(all, notNullValue());
        });
    }

    @Test
    public void shouldBeAbleToGetById() throws Exception {
        withTransaction(() -> {
            final Company company = cut.get(200L);

            assertThat(company, notNullValue());
        });
    }

}
