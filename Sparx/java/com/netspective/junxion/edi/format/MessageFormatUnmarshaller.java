/**
 * This class was generated from a set of XML constraints
 *   by the Enhydra Zeus XML Data Binding Framework. All
 *   source code in this file is constructed specifically
 *   to work with other Zeus-generated classes. If you
 *   modify this file by hand, you run the risk of breaking
 *   this interoperation, as well as introducing errors in
 *   source code compilation.
 *
 * * * * * MODIFY THIS FILE AT YOUR OWN RISK * * * * *
 *
 * To find out more about the Enhydra Zeus framework, you
 *   can point your browser at <http://zeus.enhydra.org>
 *   where you can download releases, join and discuss Zeus
 *   on user and developer mailing lists, and access source
 *   code. Please report any bugs through that website.
 */
package com.netspective.junxion.edi.format;

// Global Unmarshaller Import Statements
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public class MessageFormatUnmarshaller {

    /** The EntityResolver for parser resolution. */
    private static EntityResolver entityResolver;

    /** The ErrorHandler for parser resolution. */
    private static ErrorHandler errorHandler;

    /**
     * <p>
     *  This sets a SAX <code>EntityResolver</code> for this unmarshalling process.
     * </p>
     *
     * @param resolver the entity resolver to use.
     */
    public static void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }

    /**
     * <p>
     *  This sets a SAX <code>ErrorHandler</code> for this unmarshalling process.
     * </p>
     *
     * @param handler the error handler to use.
     */
    public static void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    public static MessageFormat unmarshal(File file) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new FileReader(file));
    }

    public static MessageFormat unmarshal(File file, boolean validate) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new FileReader(file), validate);
    }

    public static MessageFormat unmarshal(InputStream inputStream) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new InputStreamReader(inputStream));
    }

    public static MessageFormat unmarshal(InputStream inputStream, boolean validate) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new InputStreamReader(inputStream), validate);
    }

    public static MessageFormat unmarshal(Reader reader) throws IOException {
        // Delegate with default validation state
        return unmarshal(reader, false);
    }

    public static MessageFormat unmarshal(Reader reader, boolean validate) throws IOException {
        // Set the entity resolver, if needed
        if (entityResolver != null) {
            MessageFormatImpl.setEntityResolver(entityResolver);
        }

        // Set the error handler, if needed
        if (errorHandler != null) {
            MessageFormatImpl.setErrorHandler(errorHandler);
        }

        // Unmarshal using the implementation class
        return MessageFormatImpl.unmarshal(reader, validate);
    }

}
