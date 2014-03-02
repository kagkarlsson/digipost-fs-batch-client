package no.bekk.java.dpostbatch.receipt;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;

public class ReceiptParser {

	public void parse(InputStream is) {
		try {
			// 1: need input factory
			SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
			// 2: and root cursor that reads XML document from File:
			SMHierarchicCursor rootC = inf.rootElementCursor(is);
			rootC.advance();
			String status = rootC.childElementCursor("status").advance().collectDescendantText();
			//SMInputCursor advance = jobbInfoCursor.advance();
			//int employeeId = rootC.getAttrIntValue(0);
//			SMHierarchicCursor nameC = rootC.childElementCursor("name").advance();
//			SMHierarchicCursor leafC = nameC.childElementCursor().advance();
//			String first = leafC.collectDescendantText(false);
//			leafC.advance();
//			String last = leafC.collectDescendantText(false);
			System.out.println(status);
			rootC.getStreamReader().closeCompletely();
		} catch (FactoryConfigurationError | XMLStreamException e) {
			throw new RuntimeException("Failed to parse receipt.", e);
		}
	}

	
}
