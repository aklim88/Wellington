package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.time.Duration;

/**
 * Main ajax table rerender policy and data provider.
 * 
 * @author Agnieszka Klimczak
 * @param <T>
 */
public abstract class TableToModalProvider<T extends Serializable> implements IClusterable {
    private static final long serialVersionUID = 9115114717086596638L;

    private IModel<T> model;
    private ModalWindow modal;
    private ReRenderComponentProvider toBeRerenderedOnModalClose;

    public TableToModalProvider(IModel<T> model, ReRenderComponentProvider rrp) {
        this.model = model;
        this.toBeRerenderedOnModalClose = rrp;
    }

    public TableToModalProvider(ReRenderComponentProvider rrp) {
        this.toBeRerenderedOnModalClose = rrp;
        reconstructNewObject();
    }

    public void reconstructNewObject() {
        this.model = new Model<T>(constructNewObject());
    }

    public ModalWindow getModal() {
        return modal;
    }

    public void setModalWindow(ModalWindow modal) {
        this.modal = modal;
    }

    public IModel<T> getModel() {
        return model;
    }

    public synchronized void reRenderDependentComponents(AjaxRequestTarget target) {
        for (SelfUpdatingComponent rrc : toBeRerenderedOnModalClose.getComponentsToBeReRendered()) {
            target.add(rrc.getComponent());

            if (rrc.getUpdater() != null) {
                rrc.getUpdater().enable();
            }
        }
    }

    public synchronized void stopSelfUpdateBehavior() {
        for (SelfUpdatingComponent rrc : toBeRerenderedOnModalClose.getComponentsToBeReRendered()) {
            if (rrc.getUpdater() != null) {
                rrc.getUpdater().disable();
            }
        }
    }

    public abstract Page constructPage();

    public abstract String getShownValue();

    protected abstract T constructNewObject();

    /**
     * Rerender data provider.
     * 
     * @author Agnieszka Klimczak
     */
    public static class ReRenderComponentProvider implements IClusterable {
        private static final long serialVersionUID = 4885152009543076746L;

        private List<SelfUpdatingComponent> reRenderComponents = new ArrayList<SelfUpdatingComponent>();

        public void addReRenderComponent(Component componentToBeReRendered) {
            addReRenderComponent(componentToBeReRendered, null);
        }

        public void addReRenderComponent(Component componentToBeReRendered, Duration updateDuration) {
            MyAjaxSelfUpdater updater = null;

            if (updateDuration != null) {
                updater = new MyAjaxSelfUpdater(updateDuration);
                componentToBeReRendered.add(updater);
            }

            reRenderComponents.add(new SelfUpdatingComponent(componentToBeReRendered, updater));
        }

        public List<SelfUpdatingComponent> getComponentsToBeReRendered() {
            return reRenderComponents;
        }

        public synchronized void reRenderComponents(AjaxRequestTarget target) {
            for (SelfUpdatingComponent rrc : getComponentsToBeReRendered()) {
                target.add(rrc.getComponent());

                if (rrc.getUpdater() != null) {
                    rrc.getUpdater().enable();
                }
            }
        }
    }

    /**
     * Component provider
     * 
     * @author Agnieszka Klimczak
     */
    private static class SelfUpdatingComponent implements IClusterable {
        private static final long serialVersionUID = -1551663861027825161L;
        private Component component;
        private MyAjaxSelfUpdater updater;

        public SelfUpdatingComponent(Component component, MyAjaxSelfUpdater updater) {
            this.component = component;
            this.updater = updater;
        }

        public Component getComponent() {
            return component;
        }

        public MyAjaxSelfUpdater getUpdater() {
            return updater;
        }
    }

    /**
     * Extension of AbstractAjaxTimerBehavior to let selfUpdating stoping and
     * starting properly
     * 
     * @author Agnieszka Klimczak
     */
    private static class MyAjaxSelfUpdater extends AbstractAjaxTimerBehavior {
        private static final long serialVersionUID = 3579799534767319347L;
        private boolean enabled = true;

        public MyAjaxSelfUpdater(Duration updateInterval) {
            super(updateInterval);
        }

        public synchronized void disable() {
            enabled = false;
        }

        public synchronized void enable() {
            enabled = true;
        }

        @Override
        protected synchronized void onTimer(AjaxRequestTarget target) {
            if (enabled) {
                target.add(getComponent());
                onPostProcessTarget(target);
            }
        }

        protected void onPostProcessTarget(final AjaxRequestTarget target) {
        }
    }
}
