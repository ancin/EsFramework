package org.es.framework.common.web.upload.exception;

import org.apache.commons.fileupload.FileUploadException;

public class FileNameLengthLimitExceededException extends FileUploadException {

    /**  */
    private static final long serialVersionUID = 1L;
    private final int         length;
    private final int         maxLength;
    private final String      filename;

    public FileNameLengthLimitExceededException(String filename, int length, int maxLength) {
        super("file name : [" + filename + "], length : [" + length + "], max length : ["
              + maxLength + "]");
        this.length = length;
        this.maxLength = maxLength;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public int getLength() {
        return length;
    }

    public int getMaxLength() {
        return maxLength;
    }

}
