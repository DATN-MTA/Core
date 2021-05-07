package edu.mta.service;

import edu.mta.utils.GeneralValue;
import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author BePro
 */

@Service
public class BirtRuner {
    private static IReportEngine birtReportEngine = null;
    //private static final List<String> listFile = new ArrayList<>();

    static {
        try {
//			File folder = new File(GeneralValue.LINK_FONT_REPORT_IN_SERVER);
//			System.out.println("\n\n folder = " + folder);
//			
//			listFilesForFolder(folder, listFile);
//			// logger.info("lstFontConfig : " + listFile.size());
//			System.out.println("\n\n list file size = " + listFile.size());
            initBirtRunner();
        } catch (BirtException | IOException e) {
            // logger.error("Have Error", e);
            System.out.println("\n\nHave error!!");
            e.printStackTrace();
        }
    }

//	private static URL createUrlFor(File file) throws MalformedURLException {
//		return new URL("file", "", file.getAbsolutePath());
//	}

    private synchronized static void initBirtRunner() throws BirtException, IOException {
        // logger.info("Begin initBirtRunner");
        System.out.println("\n\nBegin initBirt");
        EngineConfig conf;

        try {
            System.out.println("\n\n begin try block");
            DesignConfig config = new DesignConfig();
            Platform.startup(config);
            conf = new EngineConfig();


//			if (listFile != null && !listFile.isEmpty()) {
//				for (String fileFont : listFile) {
//					conf.setFontConfig(createUrlFor(new File(fileFont)));
//					FontFactory.register(fileFont);
//				}
//			}


            IReportEngineFactory factory = (IReportEngineFactory) Platform
                    .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            birtReportEngine = factory.createReportEngine(conf);
            System.out.println("\n\n birt Report engine = " + birtReportEngine);
        } catch (BirtException e) {
            // logger.error("Have error", e);
            throw e;
        }
    }


    /**
     * Generate report file
     *
     * @param fileReportName
     * @param jsonMap
     * @return return a complete path to file report in server
     * @throws FileNotFoundException
     */
    public ByteArrayOutputStream runBirtReport(String fileReportName, Map<String, Object> jsonMap) throws IOException, EngineException {
        try {
            if (fileReportName == null || fileReportName.isEmpty()) {
                return null;
            }
            URL res = getClass().getClassLoader().getResource(fileReportName);
            File file = Paths.get(res.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();

            OutputStream outputStream = null;
            IRunAndRenderTask task = null;

            IReportRunnable design = birtReportEngine.openReportDesign(absolutePath);

            // create task to run and render report
            task = birtReportEngine.createRunAndRenderTask(design);

            if (jsonMap != null && !jsonMap.isEmpty()) {
                task.setParameterValues(jsonMap);
            }

            String fileType = jsonMap.get("fileType").toString();
            RenderOption options;

            if (fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_HTML)) {
                options = new HTMLRenderOption();
            } else if (fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
                options = new PDFRenderOption();
            } else if (fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
                    || fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLSX)) {
                options = new EXCELRenderOption();
            } else {
                options = new EXCELRenderOption();
            }

            options.setOutputFormat(fileType);
            outputStream = new ByteArrayOutputStream();
            options.setOutputStream(outputStream);
            task.setRenderOption(options);
            task.run();
            if (null != task) {
                task.close();
            }
            IOUtils.closeQuietly(outputStream);
            return (ByteArrayOutputStream) outputStream;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void listFilesForFolder(File folder, List<String> lstFile) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, lstFile);
            } else {
                // logger.info(fileEntry.getPath());
                lstFile.add(fileEntry.getPath());
            }
        }
    }

}
