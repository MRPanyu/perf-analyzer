package perfanalyzer.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import perfanalyzer.core.model.PerfStatisticsGroup;

/**
 * 统计信息序列化写入/读取的工具类，目前使用的时Java序列化加上一定的自定义头信息格式（为了实现追加写入），适用于任意流式IO。
 * 
 * @author panyu
 *
 */
public abstract class PerfIOSupport {

	/**
	 * 写入对象
	 * 
	 * @param out 输出流
	 * @param obj 需写入的对象
	 * @throws IOException
	 */
	public static void writeObject(OutputStream out, Serializable obj) throws IOException {
		// 将对象序列化成byte数组
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(obj);
		oout.close();
		byte[] data = bout.toByteArray();

		// 写入4字节int，表示后面整段内容的长度
		byte[] lengthHead = ByteBuffer.allocate(4).putInt(data.length).array();
		out.write(lengthHead);

		// 写入序列化数组内容
		out.write(data);
	}

	/**
	 * 读取输入流中下一个对象
	 * 
	 * @param in 输入流
	 * @return 读取到的对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Serializable readObject(InputStream in) throws IOException, ClassNotFoundException {
		// 读取4字节int，表示后面整段内容长度
		byte[] lengthHead = new byte[4];
		int count = readFully(in, lengthHead);
		if (count == 0) { // 一般是EOF，返回null
			return null;
		}
		int length = ByteBuffer.wrap(lengthHead).getInt();
		// 按获取的长度，读取后面的byte数组
		byte[] data = new byte[length];
		count = readFully(in, data);
		if (count < length) { // 后面的内容损坏了，可能是文件还在写入中的时候被取出来，放弃这段内容
			return null;
		}
		// 将byte数组反序列化为性能统计信息对象
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream oin = new ObjectInputStream(bin);
		return (PerfStatisticsGroup) oin.readObject();
	}

	/** 完整读取的方法，参考org.apache.commons.io.IOUtils中的类似方法 */
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
