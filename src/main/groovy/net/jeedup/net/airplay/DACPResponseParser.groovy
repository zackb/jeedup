package net.jeedup.net.airplay

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * Created by zack on 2/12/15.
 */
@CompileStatic
class DACPResponseParser {

    public final static Pattern BRANCHES = Pattern
            .compile("(casp|cmst|mlog|agal|mccr|mlcl|mdcl|mshl|mlit|abro|abar|agar|apso|caci|avdb|cmgt|aply|adbs)")

    public final static Pattern STRINGS = Pattern
            .compile("(minm|cann|cana|cang|canl|asaa|asal|asar|ascn|asgn|assa|assu|mcnm|mcna)")

    public final static Pattern RAWS = Pattern.compile("(canp)")

    public static int performSearch(byte[] raw,  Pattern listenFor, boolean haltmlit)
            throws IOException {
        final DataInputStream stream = new DataInputStream(new ByteArrayInputStream(raw))
        final int hits = search(stream,  listenFor, stream.available(), haltmlit)
        return hits
    }

    public static DACPResponse performParse(byte[] raw,  Pattern listenFor) throws IOException {
        final DataInputStream stream = new DataInputStream(new ByteArrayInputStream(raw))
        final DACPResponse resp = parse(stream,  listenFor, stream.available())
        return resp
    }

    public static DACPResponse performParse(byte[] raw) throws IOException {
        final DataInputStream stream = new DataInputStream(new ByteArrayInputStream(raw))
        return parse(stream,  null, stream.available())
    }

    private static int search(DataInputStream raw,  Pattern listenFor, int handle, boolean haltmlit)
            throws IOException {
        int hits = 0

        // loop until done with the section we have been assigned
        while (handle > 0) {
            final String key = readString(raw, 4)
            // Log.d(TAG, key)
            int length = -1
            try {
                length = raw.readInt()
            } catch (EOFException eofe) {
                return hits
            }
            handle -= 8 + length

            // check if we need to handle mlit special-case where it doesnt branch
            if (haltmlit && key.equals("mlit")) {
                final DACPResponse resp = new DACPResponse()
                resp.put(key, readString(raw, length))
                hits++

            } else if (BRANCHES.matcher(key).matches()) {
                if (listenFor.matcher(key).matches()) {
                    // parse and report if interesting branches
                    hits++
                } else {
                    // recurse searching for other branches
                    hits += search(raw,  listenFor, length, haltmlit)
                }

            } else {
                // otherwise discard data
                readString(raw, length)
            }
        }

        return hits
    }

    private static DACPResponse parse(DataInputStream raw, Pattern listenFor, int handle)
            throws IOException {
        final DACPResponse resp = new DACPResponse()
        int progress = 0

        // loop until done with the section weve been assigned
        while (handle > 0) {
            final String key = readString(raw, 4)
            final int length = raw.readInt()
            handle -= 8 + length
            progress += 8 + length

            // handle key collisions by using index notation
            final String nicekey = resp.containsKey(key) ? String.format("%s[%06d]", key, progress) : key

            if (BRANCHES.matcher(key).matches()) {
                // recurse off to handle branches
                final DACPResponse branch = parse(raw,  listenFor, length)
                resp.put(nicekey, branch)


            } else if (STRINGS.matcher(key).matches()) {
                // force handling as string
                resp.put(nicekey, readString(raw, length))
            } else if (RAWS.matcher(key).matches()) {
                // force handling as raw
                resp.put(nicekey, readRaw(raw, length))
            } else if (length == 1 || length == 2 || length == 4 || length == 8) {
                // handle parsing unsigned bytes, ints, longs
                resp.put(nicekey, new BigInteger(1, readRaw(raw, length)))
            } else {
                // fallback to just parsing as string
                resp.put(nicekey, readString(raw, length))
            }

        }

        return resp
    }

    private static byte[] readRaw(DataInputStream raw, int length) throws IOException {
        byte[] buf = new byte[length]
        raw.read(buf, 0, length)
        return buf
    }

    private static String readString(DataInputStream raw, int length) throws IOException {
        byte[] key = new byte[length]
        raw.read(key, 0, length)
        return new String(key, "UTF-8")
    }
}
