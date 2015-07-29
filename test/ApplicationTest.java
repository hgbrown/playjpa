import controllers.Application;
import org.junit.Test;
import play.mvc.Result;
import utils.AbstractFakeApplication;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 */
public class ApplicationTest extends AbstractFakeApplication {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a, equalTo(2));
    }

    @Test
    public void testIndex() {
        Result result = Application.index();
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
        assertEquals("utf-8", result.charset());
        assertTrue(contentAsString(result).contains("Welcome"));
    }

    @Test
    public void testCallIndex() {
        Result result = route(
                fakeRequest(controllers.routes.Application.index())
                );
        assertEquals(OK, result.status());
    }

}
