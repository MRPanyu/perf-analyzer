package perfanalyzer.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import perfanalyzer.core.model.PerfStatisticsGroup;

public abstract class PerfIOSupport {

	public static void writePerfStatisticsGroup(OutputStream out, PerfStatisticsGroup group) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(group);
		oout.close();
		byte[] data = bout.toByteArray();
		byte[] lengthHead = ByteBuffer.allocate(4).putInt(data.length).array();
		out.write(lengthHead);
		out.write(data);
	}

	public static PerfStatisticsGroup readPerfStatisticsGroup(InputStream in)
			throws IOException, ClassNotFoundException {
		byte[] lengthHead = new byte[4];
		int count = readFully(in, lengthHead);
		if (count == 0) {
			return null;
		}
		int length = ByteBuffer.wrap(lengthHead).getInt();
		byte[] data = new byte[length];
		count = readFully(in, data);
		if (count < length) {
			return null;
		}
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream oin = new ObjectInputStream(bin);
		return (PerfStatisticsGroup) oin.readObject();
	}

	public static int readFully(InputStream in, byte[] buf) throws IOException {
		int remaining = buf.length;
		while (remaining > 0) {
			final int location = buf.length - remaining;
			final int count = in.read(buf, location, remaining);
			if (count == -1) { // EOF
				break;
			}
			remaining -= count;
		}
		return buf.length - remaining;
	}

}
