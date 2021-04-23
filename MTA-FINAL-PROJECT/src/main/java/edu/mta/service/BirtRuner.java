package edu.mta.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import edu.mta.utils.GeneralValue;
import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.model.api.DesignConfig;

/**
 * @author BePro
 *
 */
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
	 * @param fileReportName
	 * @param jsonMap
	 * @param fileName
	 * @param folderSymbolicLink
	 * @return return a complete path to file report in server
	 * @throws FileNotFoundException 
	 */
	public static String runBirtReport(String fileReportName, Map<String, Object> jsonMap, String fileName,
			String folderSymbolicLink) throws FileNotFoundException {
		
		if (fileReportName == null || fileReportName.isEmpty()) {
			return null;
		}

		String reportFile = GeneralValue.LINK_FOLDER_REPORT_IN_SERVER + File.separator + fileReportName;
		System.out.println("\n\n report file = " + reportFile);
		EngineConfig conf = null;

		OutputStream outputStream = null;
		IRunAndRenderTask task = null;
		String pathFile = null;
		
		try {
			System.out.println("\n\n birt engine = " + birtReportEngine);
			IReportRunnable design = birtReportEngine.openReportDesign(reportFile);

			// create task to run and render report
			task = birtReportEngine.createRunAndRenderTask(design);
			
			if (jsonMap != null && !jsonMap.isEmpty()) {
				task.setParameterValues(jsonMap);
			}
			
			//if not have link to folder => auto choose Out folder
			if (folderSymbolicLink == null || folderSymbolicLink.isEmpty()) {
				pathFile = GeneralValue.LINK_OUT_REPORT_IN_SERVER;
			} else {
				pathFile = folderSymbolicLink;
			}
			
			// logger.info("Path file outPut: " + pathFile);
			System.out.println("\n\npath file = " + pathFile);
			
			File dir = new File(pathFile);
			dir.mkdir();

			String fileType = jsonMap.get("fileType").toString();
			File fileOutput = new File(dir, File.separator + fileName + "." + fileType);
			fileOutput.createNewFile(); // if file already exists will do nothing
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
			outputStream = new FileOutputStream(fileOutput);
			options.setOutputStream(outputStream);
			task.setRenderOption(options);
			task.run();
			
			
			return pathFile + File.separator + fileName + "." + fileType;
			//return null;
			
		} catch (BirtException | IOException e) {
			// logger.error("Have error", e);
			e.printStackTrace();
			return null;
			// throw e;
		} finally {
			if (null != task) {
				task.close();
			}
			IOUtils.closeQuietly(outputStream);
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
