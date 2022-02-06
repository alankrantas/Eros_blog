package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

/**
 * 上傳檔案到指定的 resources 位置
 */
public class Uploader {
	
	/**
	 * @param fileName
	 * @param library
	 * @param uploadedFile
	 * @return
	 * @throws IOException
	 */
	public static String createFile(String fileName, String library, UploadedFile uploadedFile) throws IOException {

		String filename = fileName + uploadedFile.getFileName().substring(uploadedFile.getFileName().length() - 4);
		ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
		File result = new File(extContext.getRealPath("//resources//" + library + "//" + filename));
		FileOutputStream fileOutputStream = new FileOutputStream(result);

		byte[] buffer = new byte[1024];
		int bulk;
		InputStream inputStream = uploadedFile.getInputstream();
		while (true) {
			bulk = inputStream.read(buffer);
			if (bulk < 0) {
				break;
			}
			fileOutputStream.write(buffer, 0, bulk);
			fileOutputStream.flush();
		}
		fileOutputStream.close();
		inputStream.close();

		return filename;
	}

}
