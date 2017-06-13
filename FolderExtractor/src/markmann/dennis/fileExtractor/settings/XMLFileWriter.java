package markmann.dennis.fileExtractor.settings;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import markmann.dennis.fileExtractor.logging.LogHandler;

/**
 * Class used to write the current settings into XML files.
 *
 * @author Dennis.Markmann
 */

class XMLFileWriter {

    private static final Logger LOGGER = LogHandler.getLogger("./Logs/FileExtractor.log");

    /**
     * Stores the given settings in the XML file of the given name / path.
     *
     * @param name of the setting file to write to.
     * @param settings to store in the file.
     */
    void createXmlFile(String name, Settings settings) {

        final FileWriteHelper helper = new FileWriteHelper();
        final Document doc = helper.createDocument();
        final Element element = helper.createMainElement(doc, "Settings");

        for (Field field : settings.getClass().getDeclaredFields()) {

            try {
                String fieldName = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);

                if ((settings instanceof TypeSettings) && fieldName.equals("Exceptions")) {
                    for (final ExceptionPath exceptionPath : ((TypeSettings) settings).getExceptions()) {
                        final Element exceptionElement = helper.createElement(doc, element, "Exception", null);
                        helper.createElement(doc, exceptionElement, "ExceptionName", exceptionPath.getName());
                        helper.createElement(doc, exceptionElement, "ExceptionPath", exceptionPath.getPath());
                    }
                }
                else {
                    helper.createElement(doc, element, fieldName, field.get(settings) + "");
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                LOGGER.error("Writing of '" + name + "' file failed.", e);
                e.printStackTrace();
            }
        }
        helper.writeFile("./Settings/", name, doc);
    }
}
