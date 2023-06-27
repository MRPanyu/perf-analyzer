package perfanalyzer.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

/**
 * 从IO流读写{@link PerfStatisticsTimedGroup}数据的工具类。
 * 
 * <p>
 * 首先将{@link PerfStatisticsTimedGroup}对象用java序列化变成byte[] data，然后文件中每个数据块的格式：
 * <p>
 * 4bytes(data.length) + 8bytes(statisticsStartTime) + 8bytes(statisticsEndTime)
 * + data
 * 
 * 
 * @author panyu
 *
 */
public abstract class PerfIOUtils {

	/**
	 * 写入对象
	 * 
	 * @param out
	 * @param group
	 * @throws IOException
	 */
	public static void write(OutputStream out, PerfStatisticsTimedGroup group) throws IOException {
		// Serialize into byte[]
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(group);
		oout.close();
		byte[] data = bout.toByteArray();

		// Build header
		byte[] head = ByteBuffer.allocate(20).putInt(data.length).putLong(group.getStatisticsStartTime())
				.putLong(group.getStatisticsEndTime()).array();

		// Write into output stream
		out.write(head);
		out.write(data);
	}

	/**
	 * 读取头信息（包含 statisticsStartTime /
	 * statisticsEndTime），然后跳过数据块不做反序列化。用于不太占用内存地扫描整个文件获取整体信息
	 * 
	 * @param in
	 * @return 只包含 statisticsStartTime / statisticsEndTime
	 * @throws IOException
	 */
	public static PerfStatisticsTimedGroup readHead(InputStream in) throws IOException {
		byte[] head = new byte[20];
		int count = readFully(in, head);
		if (count == 0) { // EOF
			return null;
		}
		ByteBuffer buf = ByteBuffer.wrap(head);
		int length = buf.getInt();
		long startTime = buf.getLong();
		long endTime = buf.getLong();
		long skipped = in.skip(length);
		if (skipped < length) { // maybe unfinished, ignore
			return null;
		} else {
			PerfStatisticsTimedGroup group = new PerfStatisticsTimedGroup(startTime, endTime);
			return group;
		}
	}

	/**
	 * 读取一个完整对象，包含节点信息
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static PerfStatisticsTimedGroup read(InputStream in) throws IOException, ClassNotFoundException {
		byte[] head = new byte[20];
		int count = readFully(in, head);
		if (count == 0) { // EOF
			return null;
		}
		ByteBuffer buf = ByteBuffer.wrap(head);
		int length = buf.getInt();

		// Read data bytes by length
		byte[] data = new byte[length];
		count = readFully(in, data);
		if (count < length) { // maybe unfinished, ignore
			return null;
		}

		// Deserialize
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream oin = new ObjectInputStream(bin);
		return (PerfStatisticsTimedGroup) oin.readObject();
	}

	private static int readFully(InputStream in, byte[] buf) throws IOException {
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
