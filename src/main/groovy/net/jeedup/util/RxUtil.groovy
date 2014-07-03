package net.jeedup.util

import net.jeedup.io.IOUtil
import rx.Observable
import rx.Subscriber

/**
 * Created by zack on 7/3/14.
 */
class RxUtil {

    private static final DEFAULT_BUFFER_SIZE = 8 * 1024

    public static Observable<char[]> stream(final Reader reader) {
        return stream(reader, DEFAULT_BUFFER_SIZE)
    }

    public static Observable<char[]> stream(final Reader reader, final int size) {
        return Observable.create(new Observable.OnSubscribe<char[]>() {
            void call(Subscriber<char[]> subscriber) {
                try {
                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    char[] buffer = new char[size]

                    int n = reader.read(buffer)
                    while (n != -1 && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(Arrays.copyOf(buffer, n))
                        if (subscriber.isUnsubscribed()) {
                        } else {
                            n = reader.read(buffer)
                        }
                    }

                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    subscriber.onCompleted()
                    IOUtil.close(reader)
                } catch (Exception e) {
                    subscriber.onError(e)
                }
            }
        })
    }

    public static Observable<byte[]> stream(final InputStream inputStream) {
        return stream(inputStream, DEFAULT_BUFFER_SIZE)
    }

    public static Observable<byte[]> stream(final InputStream inputStream, final int size) {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            void call(Subscriber<byte[]> subscriber) {
                try {
                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    byte[] buffer = new byte[size]

                    int n = inputStream.read(buffer)
                    while (n != -1 && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(Arrays.copyOf(buffer, n))
                        if (!subscriber.isUnsubscribed()) {
                            n = inputStream.read(buffer)
                        }
                    }

                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    subscriber.onCompleted()
                    IOUtil.close(inputStream)
                } catch (Exception e) {
                    subscriber.onError(e)
                }
            }
        })
    }

    public static Observable<String> streamLines(final Reader reader) {
        return streamLines(new BufferedReader(reader))
    }

    public static Observable<String> streamLines(final BufferedReader reader) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            void call(Subscriber<? super String> subscriber) {
                try {
                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    String line
                    while ((line = reader.readLine()) != null) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(line)
                        }
                    }

                    if (subscriber.isUnsubscribed()) {
                        return
                    }

                    subscriber.onCompleted()
                    IOUtil.close(reader)
                } catch (Exception e) {
                    subscriber.onError(e)
                }
            }
        })
    }

    public static Observable<String> streamLines(final InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        return streamLines(reader)
    }
}
