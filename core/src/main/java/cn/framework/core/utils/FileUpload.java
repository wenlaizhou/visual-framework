package cn.framework.core.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16/4/12 下午4:55
 *
 * @author wenlai
 */
public final class FileUpload {

    public static List<FileItem> load(HttpServletRequest request) {
        try {
            org.apache.commons.fileupload.FileUpload upload = new org.apache.commons.fileupload.FileUpload(new DiskFileItemFactory());
            return upload.parseRequest(new ServletRequestContext(request));
        }
        catch (Exception x) {
            return null;
        }
    }

}
