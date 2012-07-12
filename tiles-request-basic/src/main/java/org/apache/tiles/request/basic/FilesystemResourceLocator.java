package org.apache.tiles.request.basic;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.ApplicationResourceLocator;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.codehaus.plexus.util.DirectoryScanner;

public class FilesystemResourceLocator implements ApplicationResourceLocator {

    private File root;

    public FilesystemResourceLocator(File root) {
        this.root = root;
    }

    private URLApplicationResource createResource(String localePath, File targetFile) {
        if (targetFile.exists()) {
            try {
                return new URLApplicationResource(localePath, targetFile.toURI().toURL(), this);
            } catch (MalformedURLException e) {
                // should not happen
                throw new IllegalArgumentException("Illegal resource path " + localePath + " in directory "
                        + root.getAbsolutePath(), e);
            }
        } else {
            return null;
        }
    }

    @Override
    public URLApplicationResource getResource(String path) {
        return createResource(path, new File(root, path));
    }

    @Override
    public URLApplicationResource getResource(ApplicationResource base, Locale locale) {
        if (base instanceof URLApplicationResource && ((URLApplicationResource) base).getSource() == this) {
            String path = base.getLocalePath();
            return createResource(path, new File(root, path));
        }
        return null;
    }

    @Override
    public Collection<ApplicationResource> getResources(String path) {
        ArrayList<ApplicationResource> result = new ArrayList<ApplicationResource>();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(root);
        scanner.setIncludes(new String[] { path });
        scanner.scan();
        for (String file : scanner.getIncludedFiles()) {
            result.add(createResource(file, new File(root, file)));
        }
        return result;
    }

}
