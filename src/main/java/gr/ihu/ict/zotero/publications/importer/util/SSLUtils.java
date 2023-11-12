package gr.ihu.ict.zotero.publications.importer.util;


import io.vavr.control.Try;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class SSLUtils {
    public static Try<SSLConnectionSocketFactory> createSSLConnectionFactory() {
        return constructSSLContext()
                .flatMap(sslContext -> Try.of(() -> new SSLConnectionSocketFactory(sslContext)));
    }

    private static Try<SecureRandom> constructSecureRandom() {
        return Try.of(SecureRandom::new);
    }

    private static Try<X509TrustManager> constructX509TrustManager() {
        return Try.of(() -> new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        });
    }

    private static Try<TrustManager[]> constructTrustManager() {
        return constructX509TrustManager()
                .flatMap(x509TrustManager -> Try.of(() -> new TrustManager[]{
                        x509TrustManager
                }));
    }

    private static Try<SSLContext> constructSSLContext() {
        return Try.of(() -> SSLContext.getInstance("SSL"))
                .flatMap(sslContext -> constructTrustManager()
                        .flatMap(trustManagers -> constructSecureRandom()
                                .flatMap(secureRandom -> Try.of(() -> {
                                    sslContext.init(
                                            null,
                                            trustManagers,
                                            secureRandom
                                    );

                                    return sslContext;
                                }))));
    }
}
