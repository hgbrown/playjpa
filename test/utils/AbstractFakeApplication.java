package utils;

import play.test.FakeApplication;
import play.test.WithApplication;

import static utils.TestHelper.getFakeApplication;

public abstract class AbstractFakeApplication extends WithApplication {

    @Override
    protected FakeApplication provideFakeApplication() {
        return getFakeApplication();
    }

}
