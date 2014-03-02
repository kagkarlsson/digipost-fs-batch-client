package no.bekk.java.dpostbatch.receipt;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class ReceiptParserTest {
	
	String receiptXml = 
			"<masseutsendelse-resultat><status>OK</status></masseutsendelse-resultat>";

	@Test
	public void test() {
		new ReceiptParser().parse(new ByteArrayInputStream(receiptXml.getBytes()));
	}
	
}
