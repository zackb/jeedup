package net.jeedup.net.airplay

import groovy.transform.CompileStatic
import net.jeedup.io.IOUtil
import net.jeedup.net.http.HTTP
import net.jeedup.net.http.Request

/**
 * https://code.google.com/p/android-cookbook/source/browse/trunk/TunesControl/src/org/tunescontrol/daap/RequestHelper.java
 * http://clican-pluto.googlecode.com/svn/trunk/appletv/appletv/src/test/java/com/clican/appletv/service/RemoteTestCase.java
 * https://github.com/skevy/atlas-backend/blob/master/atlas/services/appletv.coffee
 *
 */
@CompileStatic
class AirPlayControl {

    private final String host

    private final int port

    private final Map<String, String> headers = [
            'Accept-Language': 'en-us',
            'Viewer-Only-Client': '1',
            'Pragma': 'no-cache',
            'Client-DAAP-Version': '3.12',
            'User-Agent': 'Remote/849',
            'Client-iTunes-Sharing-Version': '3.10',
            'Accept': '*/*',
            'Client-ATV-Sharing-Version': '1.2',
            'Connection': 'keep-alive',
            'Accept-Encoding': 'gzip',
            'Accept': '*/*'
    ]

    private static byte[] MENU = [0x63, 0x6d, 0x63, 0x63, 0x00, 0x00, 0x00, 0x01, 0x30, 0x63, 0x6d, 0x62, 0x65, 0x00, 0x00, 0x00, 0x04, 0x6d, 0x65, 0x6e, 0x75]


    private String hsgid = '00000000-00d5-77e2-04bd-501f29834bc3'

    public AirPlayControl(String host, int port = 3689) {
        this.host = host
        this.port = port
    }

    private String createSession() {
        String loginurl = "http://$host:$port/login?hasFP=1&hsgid=$hsgid"

        //InputStream ins = get(loginurl, headers)
        //byte[] data = IOUtil.readBytes(ins)
        //byte[] data = get(loginurl, headers)
        byte[] data = request(loginurl)

        DACPResponse login = DACPResponseParser.performParse(data)
        String sessionId = login.getNested('mlog').getNumberString('mlid')

        println 'SessionId: ' + sessionId

        return sessionId
    }

    public void menu() {

        String sessionId = createSession()

        String controlpromptentry = "http://$host:$port/ctrl-int/1/controlpromptentry?prompt-id=405&session-id=$sessionId"

        Map<String,String> hs = new HashMap<String,String>(headers)

        hs.put('Content-Type', 'application/x-www-form-urlencoded')
        HTTP.post(controlpromptentry, MENU, hs)
    }

    public void play() {
        String url = "http://$host:$port/ctrl-int/1/playpause?session-id=" + createSession()
        byte[] bytes = request(url)
        println new String(bytes, 'UTF-8')
    }

    private byte[] get(String url, Object data) {
        Request request = new Request()
                .url(url)
                .headers(headers)
                .data(data)
                .method('GET')

        ByteArrayOutputStream os = new ByteArrayOutputStream()
        InputStream is = HTTP.performRequest(request).inputStream

        byte[] buffer = new byte[1024]
        int bytesRead
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead)
        }

        return os.toByteArray()
    }


    private static byte[] request(String remote) throws Exception {
        URL url = new URL(remote)
        HttpURLConnection connection = (HttpURLConnection)url.openConnection()
        connection.setRequestProperty('Viewer-Only-Client', '1')

        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        connection.connect()

        if (connection.responseCode >= 400) {
            throw new Exception('HTTP Error Response Code: ' + connection.responseCode)
        }

        byte[] result = null
        try {
            result = IOUtil.readBytes(connection.inputStream)
        } finally {
            IOUtil.close(connection.inputStream)
        }
        return result
    }

    public static void main(String[] args) {
        AirPlayControl c = new AirPlayControl('10.0.1.3')
        c.play()
    }
}
