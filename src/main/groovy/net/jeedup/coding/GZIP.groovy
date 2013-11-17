package net.jeedup.coding

import groovy.transform.CompileStatic
import net.jeedup.io.IOUtil

import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
class GZIP {

    public static byte[] compress(byte[] bytes) {
        Deflater df = new Deflater()
        //df.setLevel(Deflater.BEST_COMPRESSION)
        df.setInput(bytes)
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length)
        df.finish()
        byte[] buff = new byte[1024]
        while(!df.finished())   {
            int count = df.deflate(buff)
            baos.write(buff, 0, count)
        }

        IOUtil.close(baos)

        byte[] output = baos.toByteArray()
        return output
    }

    public static byte[] decompress(byte[] bytes) {
        Inflater ifl = new Inflater()
        //df.setLevel(Deflater.BEST_COMPRESSION)
        ifl.setInput(bytes)
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length)
        byte[] buff = new byte[1024]
        while(!ifl.finished())  {
            int count = ifl.inflate(buff)
            baos.write(buff, 0, count)
        }

        IOUtil.close(baos)
        byte[] output = baos.toByteArray()
        return output
    }

}
