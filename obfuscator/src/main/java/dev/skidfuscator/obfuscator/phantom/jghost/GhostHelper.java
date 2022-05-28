package dev.skidfuscator.obfuscator.phantom.jghost;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import dev.skidfuscator.obfuscator.Skidfuscator;
import dev.skidfuscator.obfuscator.phantom.jghost.tree.GhostClassNode;
import dev.skidfuscator.obfuscator.phantom.jghost.tree.GhostContents;
import dev.skidfuscator.obfuscator.phantom.jghost.tree.GhostLibrary;
import lombok.experimental.UtilityClass;
import org.mapleir.asm.ClassNode;
import org.topdank.byteengineer.commons.data.JarClassData;
import org.topdank.byteengineer.commons.data.JarInfo;
import org.topdank.byteio.in.SingleJarDownloader;

import java.io.File;

@UtilityClass
public class GhostHelper {
    public GhostLibrary createFromLibraryFile(final File file) {
        final JarInfo jarInfo = new JarInfo(file);
        final SingleJarDownloader<ClassNode> downloader = new SingleJarDownloader<>(jarInfo);
        try {
            downloader.download();
        } catch (Throwable e) {
            Skidfuscator.LOGGER.error("Failed to download library", e);
            return null;
        }

        final GhostContents ghostContents = new GhostContents();
        final GhostLibrary ghostLibrary = new GhostLibrary();
        ghostLibrary.setContents(ghostContents);

        try {
            final ByteSource byteSource = Files.asByteSource(file);
            ghostLibrary.setMd5(byteSource.hash(Hashing.md5()).toString());
            ghostLibrary.setSha1(byteSource.hash(Hashing.sha1()).toString());
            ghostLibrary.setSha256(byteSource.hash(Hashing.sha256()).toString());
        } catch (Throwable e) {
            Skidfuscator.LOGGER.error("Failed to hash library", e);
            return null;
        }

        for (JarClassData classContent : downloader.getJarContents().getClassContents()) {
            final GhostClassNode ghostClassNode = GhostClassNode.of(classContent.getClassNode().node);
            ghostContents.getClasses().put(classContent.getName(), ghostClassNode);
        }

        return ghostLibrary;
    }
}
