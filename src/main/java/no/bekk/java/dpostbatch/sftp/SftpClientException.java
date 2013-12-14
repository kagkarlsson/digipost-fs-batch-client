package no.bekk.java.dpostbatch.sftp;

import com.jcraft.jsch.JSchException;

@SuppressWarnings("serial")
public class SftpClientException extends RuntimeException {

	public SftpClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
