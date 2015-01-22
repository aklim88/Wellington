package pl.klimczakowie.cpublication2.web.view;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.io.IClusterable;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Logo;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.tooling.TableToModalProvider;

public class FirmListRowPanel<T extends Serializable> extends Panel implements IClusterable {
    private static final long serialVersionUID = -3950757818454313205L;

    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;
    private ModalWindow modal;
    final TableToModalProvider<? extends Serializable> pageProvider;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FirmListRowPanel(String id, Cpublication2Service persistenceReference2, IModel<Firm> rowModel, FirmListPage page, String title) {
        super(id, rowModel);

        modal = new ModalWindow("modal");

        pageProvider = new TableToModalProvider<Firm>(rowModel, page.getRenderComponentProvider()) {
            private static final long serialVersionUID = 2726806843257623618L;

            @Override
            public Page constructPage() {
                return new FirmViewPanel(this, persistenceReference);
            }

            @Override
            public String getShownValue() {
                return getModel().getObject().getName();
            }

            @Override
            public Firm constructNewObject() {
                return new Firm();
            }
        };
        pageProvider.setModalWindow(modal);

        modal.setTitle(title);
        modal.setCookieName(pageProvider.getModel().getObject().getClass().getCanonicalName());

        modal.setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = -7563445644644734793L;

            public Page createPage() {
                return pageProvider.constructPage();
            }
        });
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            private static final long serialVersionUID = 1291348829469961993L;

            public void onClose(AjaxRequestTarget target) {
                pageProvider.reRenderDependentComponents(target);
            }
        });

        modal.setMinimalHeight(450);
        modal.setMinimalWidth(650);

        final Logo logo = rowModel.getObject().getLogo();
        IResource imageResource = new DynamicImageResource() {
            private static final long serialVersionUID = -1473185395438905405L;

            @Override
            protected byte[] getImageData(IResource.Attributes attributes) {
                return logo != null ? logo.getLogo() : null;
            }
        };

        AjaxLink link = new AjaxLink("select", pageProvider.getModel()) {
            private static final long serialVersionUID = -6629808232411772408L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                pageProvider.stopSelfUpdateBehavior();
                modal.show(target);
            }
        };

        link.add(modal);
        link.add(feedback.setOutputMarkupId(true));
        link.add(new Label("company_name", rowModel.getObject().getName()));
        link.add(new NonCachingImage("logo.logo", imageResource).setVisibilityAllowed(logo != null).setVisible(logo != null));
        link.add(new Label("company_street", rowModel.getObject().getStreet()));
        link.add(new Label("post_code", rowModel.getObject().getPostCode()));
        link.add(new Label("city", rowModel.getObject().getCity()));
        link.add(new Label("country", rowModel.getObject().getCountry()));
        add(link);
    }

}