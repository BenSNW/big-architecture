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
        download("http://dlx.bookzz.org/genesis/915000/d38ddc30906a74bcfb1162d831d533dd/_as/[Hrafn_Loftsson;_Eiri%CC%81kur_Ro%CC%88gnvaldsson;_Sigru%CC%81(BookZZ.org).pdf",
                "Advances in Natural Language Processing 7th International Conference on NLP 2011.pdf");
//        download("https://web.stanford.edu/~hastie/StatLearnSparsity_files/SLS.pdf",
//                "Statistical Learning with Sparsity The Lasso and Generalizations.pdf");
    }

}
