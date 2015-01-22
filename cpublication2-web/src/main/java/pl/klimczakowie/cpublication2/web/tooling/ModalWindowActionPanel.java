package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.io.IClusterable;

/**
 * Link based on {@link Panel}.
 * 
 * @author Agnieszka Klimczak
 */
public class ModalWindowActionPanel<T extends Serializable> extends Panel implements IClusterable {
    private static final long serialVersionUID = -3964450560423356403L;
    private ModalWindow modal;

    @SuppressWarnings({"serial", "rawtypes", "unchecked"})
    public ModalWindowActionPanel(String id, String title, final TableToModalProvider<? extends Serializable> pageProvider) {
        super(id, pageProvider.getModel());

        modal = new ModalWindow("modal");
        pageProvider.setModalWindow(modal);

        modal.setTitle(title);
        modal.setCookieName(pageProvider.getModel().getObject().getClass().getCanonicalName());

        modal.setPageCreator(new ModalWindow.PageCreator() {
            public Page createPage() {
                return pageProvider.constructPage();
            }
        });
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
                pageProvider.reRenderDependentComponents(target);
            }
        });

        modal.setMinimalHeight(450);
        modal.setMinimalWidth(650);

        AjaxLink link = new AjaxLink("select", pageProvider.getModel()) {
            private static final long serialVersionUID = -6629808232411772408L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                pageProvider.stopSelfUpdateBehavior();
                modal.show(target);
            }

        };

        link.add(modal);
        link.add(new Label("value", pageProvider.getShownValue()));

        add(link);
    }

    public ModalWindow getModal() {
        return modal;
    }
}
