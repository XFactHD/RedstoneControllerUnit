package io.github.xfacthd.rsctrlunit.client.util;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import net.minecraft.Util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public final class Explorer
{
    private static final Util.OS PLATFORM = Util.getPlatform();

    public static boolean revealInFileExplorer(Path path)
    {
        URI pathUri = path.toUri();
        return switch (PLATFORM)
        {
            case LINUX, SOLARIS -> revealOnLinux(path, pathUri);
            // Windows Explorer is stupid and always returns exit code 1...
            case WINDOWS -> { exec(path, true, "explorer", "/select," + pathUri); yield true; }
            case OSX -> exec(path, true, "open", "-R", pathUri.toString());
            case UNKNOWN -> throw new UnsupportedOperationException("Unknown platform");
        };
    }

    private static boolean revealOnLinux(Path path, URI pathUri)
    {
        String uriString = getLinuxCompatibleUri(pathUri);
        String[] dbusParams = new String[] {
                "dbus-send",
                "--session",
                "--print-reply",
                "--dest=org.freedesktop.FileManager1",
                "--type=method_call",
                "/org/freedesktop/FileManager1",
                "org.freedesktop.FileManager1.ShowItems",
                "array:string:\"" + uriString + "\"",
                "string:\"\""
        };
        if (exec(path, false, dbusParams)) return true;

        uriString = getLinuxCompatibleUri(path.getParent().toUri());
        if (exec(path, false, "xdg-open", uriString)) return true;
        if (exec(path, false, "kde-open", uriString)) return true;
        return exec(path, true, "gnome-open", uriString);
    }

    private static String getLinuxCompatibleUri(URI pathUri)
    {
        String uriString = pathUri.toString();
        if ("file".equals(pathUri.getScheme()))
        {
            uriString = uriString.replace("file:", "file://");
        }
        return uriString;
    }

    private static boolean exec(Path path, boolean log, String... params)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(params);
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
            return process.waitFor() == 0;
        }
        catch (IOException | InterruptedException e)
        {
            if (log)
            {
                RedstoneControllerUnit.LOGGER.error("Couldn't reveal file '{}' in explorer", path, e);
            }
            return false;
        }
    }



    private Explorer() { }
}
