package hx.io;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Benchun on 3/12/17
 */
public class IOUtils {

    public static void download(String url) throws IOException {
        download(url, url.substring(url.lastIndexOf("/") + 1));
    }

    public static void download(String url, String path) throws IOException {
        Files.copy(new URL(url).openStream(), Paths.get(path));
    }

    public static void main(String[] args) throws IOException {
        download("http://courses.cs.tamu.edu/rgutier/cpsc636_s10/elman1990findStructureTime.pdf",
                "Deep Learning for the Web.pdf");
//        download("https://web.stanford.edu/~hastie/StatLearnSparsity_files/SLS.pdf",
//                "Statistical Learning with Sparsity The Lasso and Generalizations.pdf");
    }

}
