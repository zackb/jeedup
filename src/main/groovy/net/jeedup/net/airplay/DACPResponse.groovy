package net.jeedup.net.aiplay

import groovy.transform.CompileStatic

/**
 * Created by zack on 2/12/15.
 */
@CompileStatic
class DACPResponse extends HashMap<String, Object> {

    public DACPResponse getNested(String key) throws Exception {
        return (DACPResponse) this.get(key);
    }

    public String getString(String key) throws Exception {
        Object obj = this.get(key);
        if (obj instanceof String)
            return (String) obj;
        else
            return "";
    }

    public BigInteger getNumber(String key) throws Exception {
        Object obj = this.get(key);
        if (obj instanceof BigInteger)
            return (BigInteger) obj;
        else
            return new BigInteger("-1");
    }

    public long getNumberLong(String key) throws Exception {
        return getNumber(key).longValue();
    }

    public String getNumberString(String key) throws Exception {
        return getNumber(key).toString();
    }

    public String getNumberHex(String key) throws Exception {
        return Long.toHexString(getNumberLong(key));
    }

    public byte[] getRaw(String key) throws Exception {
        Object obj = this.get(key);
        return (byte[]) obj;
    }

    public List<DACPResponse> findArray(String prefix) throws Exception {
        List<DACPResponse> found = new LinkedList<DACPResponse>();
        String[] keys = this.keySet().toArray([] as String[]);
        Arrays.sort(keys);

        for (String key : keys) {
            if (key.startsWith(prefix))
                found.add((DACPResponse) this.get(key));
        }

        return found;
    }

    /**
     * Convert milliseconds to m:ss string format for track lengths.
     * <p>
     * @param milliseconds the milliseconds value to convert
     * @return a String formatted in m:ss format
     */
    public static String convertTime(long milliseconds) {
        final int seconds = (int) ((milliseconds / 1000) % 60);
        final int minutes = (int) ((milliseconds / 1000) / 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
