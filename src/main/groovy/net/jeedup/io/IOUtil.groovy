package net.jeedup.io

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/15/13
 */
@CompileStatic
class IOUtil {

    public static long copyStream(final InputStream inputStream,
                                  final OutputStream outputStream,
                                  final int bufferSize = 16384 * 2,
                                  final long maxBytes = -1) throws IOException {

        byte[] buffer = new byte[bufferSize]
        int bytesRead = inputStream.read(buffer)
        long totalBytes = bytesRead == -1 ? 0 : bytesRead
        while (bytesRead != -1) {
            outputStream.write(buffer, 0, bytesRead)
            if (maxBytes > 0 && totalBytes >= maxBytes) {
                break
            }
            bytesRead = inputStream.read(buffer)
            if (bytesRead != -1)
                totalBytes += bytesRead
        }
        return totalBytes
    }

    public static String readString(InputStream ins) throws IOException {
        return new String(readBytes(ins), 'UTF-8')
    }

    public static byte[] readBytes(InputStream ins) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream()
        try {
            copyStream(ins, bout)
            close(bout)
        } finally {
            close(bout)
            close(ins)
        }

        return bout.toByteArray()
    }

    public static void close(OutputStream out) {
        try {
            if (out != null) {
                out.flush()
                out.close()
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public static void close(InputStream ins) {
        try {
            if (ins != null) {
                ins.close();
            }
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close()
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
