/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.klimczakowie.cpublication2.web;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.file.Path;
import org.apache.wicket.util.time.Duration;

import pl.klimczakowie.cpublication2.web.auth.Cpublication2AuthenticatedWebSession;
import pl.klimczakowie.cpublication2.web.auth.CpublicationSignInPage;
import pl.klimczakowie.cpublication2.web.view.SearchPage;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see org.apache.wicket.example.Start#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {
    /**
     * Constructor
     */
    public WicketApplication() {
    }

    /**
     * Init
     */
    @Override
    public void init() {
        getResourceSettings().getResourceFinders().add(new Path(new Folder("htmls/cpublication2/")));
        getResourceSettings().setResourcePollFrequency(Duration.milliseconds(1000));
        getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public Class<SearchPage> getHomePage() {
        // return HomePage.class;
        return SearchPage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return Cpublication2AuthenticatedWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return CpublicationSignInPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

}
