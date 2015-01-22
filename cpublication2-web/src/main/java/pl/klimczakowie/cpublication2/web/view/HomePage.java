package pl.klimczakowie.cpublication2.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends FirmManagement {
    private static final long serialVersionUID = 4300390880041685656L;
    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    public HomePage() {
        super();

    }

    @Override
    protected String getPageTitle() {
        return "Wellington Publication S.L.";
    }

}
