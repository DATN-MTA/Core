package edu.mta.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import edu.mta.utils.GeneralValue;

@Service
public class BaseService {
	
	
	/**
	 * Generate path to file report in server
	 * @param fileName - name or file report
	 * @return path to file report in server; null if system failed to create path
	 */
	public String getFolderSymLink(String fileName) {
		try {
            String pathCreateSymlink = GeneralValue.LINK_OUT_REPORT_IN_SERVER;
            if (!pathCreateSymlink.endsWith("/")) {
                pathCreateSymlink += File.separator;
            }
            pathCreateSymlink += fileName;
            System.out.println("\n\n pathCreateSymlink = " + pathCreateSymlink);
            
            File directory = new File(pathCreateSymlink);
            if (!directory.exists()) {
                boolean mkdirResult = directory.mkdirs();
                if (!mkdirResult) {
                    //logger.error("cannot mkdir: "  );
                	System.out.println("\n\ncannot mkdir: " + pathCreateSymlink);
                    return null;
                }
            }
            return pathCreateSymlink;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * Generate a new path directory
	 * @param strSymLink
	 * @return a new path: folder/date/time/strSymLink
	 */
	private static String createPathDirectorySimLink(String strSymLink) {
        try {
            String pathFolderSymLink = GeneralValue.LINK_FOLDER_REPORT_IN_SERVER;
            String strDirectory = pathFolderSymLink + File.separator + LocalDate.now() + File.separator + LocalTime.now();
            File f = new File(strDirectory);
            boolean success;

            if (!f.exists()) {
                success = f.mkdirs();
                if (success == true) {
                    //logger.info("Directory is created: " + strDirectory);
                    return strDirectory + File.separator + strSymLink;
                }
            }

            return strDirectory + File.separator + strSymLink;
        } catch (Exception ex) {
            //logger.error("Directory is not created: ", ex);
        }
        return null;
    }

	public static String genSymLink(String pathFile) {
        try {
            //logger.info("Beginning genSymLink................");
        	System.out.println("\n\nBegin genSymlink");
            Path file = Paths.get(pathFile);
            
            int index = pathFile.lastIndexOf(File.separator);
            System.out.println("\n\n pathFile = " + pathFile);
            System.out.println("\n\n index = " + index);
            if (index < 0) {
                index = pathFile.lastIndexOf("\\");
                System.out.println("\n\n index of \\ = " + index);
            }
            
            String strSymLink = pathFile.substring(index + 1);
            System.out.println("\n\nstrSymLink = " + strSymLink);
            
            //Tao moi symlink cho tomcat
            String path = createPathDirectorySimLink(strSymLink);
            Path sLink = Paths.get(path);
            try {
                if (!Files.isSymbolicLink(sLink)) {
                    Files.createSymbolicLink(sLink, file);
                }
            } catch (UnsupportedOperationException ex) {
                //logger.error("This OS doesn't support creating Sym links: ", ex);
                return null;
            } catch (IOException ex) {
                //logger.error("Error createSymbolicLink: ", ex);
                return null;
            }
            //logger.info("slink: " + path);
            String pathFolderSymLink = "C:/Users/BePro/Desktop/invoiceFolder";
            //logger.info("pathFolderSymLink: " + pathFolderSymLink);
            String result = "";
            if (path != null) {
                result = path.replace(pathFolderSymLink, "symlink");
            }
            result = "http://localhost:8775/" + File.separator + result;
            System.out.println("\n\n result = " + result);
            //logger.info("return result: " + result);
            return result;
        } catch (Exception ex) {
            //logger.error("genSymLink have error: ", ex);
            return null;
        }
    }
}
