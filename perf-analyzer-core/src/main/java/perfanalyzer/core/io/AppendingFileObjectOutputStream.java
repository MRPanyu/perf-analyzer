package perfanalyzer.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class AppendingFileObjectOutputStream extends ObjectOutputStream {

	private static ThreadLocal<Boolean> appendFlag = new ThreadLocal<Boolean>();

	private static OutputStream createOutputStream(File file) throws IOException {
		if (file.exists() && file.length() > 0) {
			appendFlag.set(true);
			return new FileOutputStream(file, true);
		} else {
			appendFlag.set(false);
			return new FileOutputStream(file);
		}
	}

	public AppendingFileObjectOutputStream(File file) throws IOException {
		super(createOutputStream(file));
	}

	@Override
	protected void writeStreamHeader() throws IOException {
		if (!appendFlag.get()) {
			super.writeStreamHeader();
		}
		appendFlag.remove();
	}

}
