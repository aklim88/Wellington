package pl.klimczakowie.cpublication2.web.tooling;

import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.protocol.http.IMultipartWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.upload.FileItem;

public class MyFileUploadField extends FormComponent<byte[]> {
    private static final long serialVersionUID = 1L;

    private transient byte[] fileUpload;
    private transient String fileName;

    public MyFileUploadField(final String id) {
        super(id);
    }

    public byte[] getFileUploads() {
        if (fileUpload != null) {
            return fileUpload;
        }

        final Request request = getRequest();

        if (request instanceof IMultipartWebRequest) {
            final List<FileItem> fileItems = ((IMultipartWebRequest) request).getFile(getInputName());

            if (fileItems != null) {
                for (FileItem item : fileItems) {
                    if (item != null && item.getSize() > 0) {
                        FileUpload fu = new FileUpload(item);
                        fileUpload = fu.getBytes();
                        fileName = fu.getClientFileName();
                        fu.closeStreams();
                    }
                }
            }
        }
        return fileUpload;
    }

    @Override
    public void updateModel() {
        if (getModel() != null) {
            super.updateModel();
        }
    }

    @Override
    public String[] getInputAsArray() {
        return new String[]{fileName};
    }

    @Override
    protected byte[] convertValue(String[] value) throws ConversionException {
        return getFileUploads();
    }

    @Override
    public boolean isMultiPart() {
        return true;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        checkComponentTag(tag, "input");
        checkComponentTagAttribute(tag, "type", "file");
        super.onComponentTag(tag);
    }

    @Override
    protected void onDetach() {
        if ((fileUpload != null) && forceCloseStreamsOnDetach()) {
            fileUpload = null;

            if (getModel() != null) {
                getModel().setObject(null);
            }
        }
        super.onDetach();
    }

    protected boolean forceCloseStreamsOnDetach() {
        return true;
    }
}