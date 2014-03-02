package no.bekk.java.dpostbatch.sftp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SftpReceipt {
	
	private static final Pattern RECEIPT_PATTERN = Pattern.compile("(.*/)?(.*).resultat.\\d{8}-\\d{9}.zip");

	private String originalFilename;
	private String receiptFilename;

	private SftpReceipt(String originalFilename, String receiptFilename) {
		this.originalFilename = originalFilename;
		this.receiptFilename = receiptFilename;
	}

	public String getRemotePath() {
		return receiptFilename;
	}

	public static SftpReceipt fromReceiptName(String filename) {
		Matcher m = RECEIPT_PATTERN.matcher(filename);
		if (m.find()) {
			return new SftpReceipt(m.group(2), filename);
		} else {
			throw new RuntimeException("Not valid receipt-format: " + filename);
		}
	}

	public static boolean isReceipt(String filename) {
		return RECEIPT_PATTERN.matcher(filename).matches();
	}

	public String getOriginalName() {
		return originalFilename;
	}

}
