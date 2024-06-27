package io.github.xfacthd.rsctrlunit.client.util;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import net.minecraft.Util;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.codec.digest.Sha2Crypt;
import oshi.SystemInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class LastPathStorage
{
    private static final String SYSTEM_UUID = Util.make(() ->
    {
        String uuid = new SystemInfo().getHardware().getComputerSystem().getHardwareUUID();
        // Hash the system UUID just in case someone (accidentally) publishes a last-path file in a modpack
        return Sha2Crypt.sha256Crypt(uuid.getBytes(StandardCharsets.UTF_8), "$5$HashedHardwareIdAsSafeguard");
    });
    private static final String ROOT_FOLDER = "rsctrlunit";
    private static final Path ROOT_PATH = FMLPaths.GAMEDIR.get().resolve(ROOT_FOLDER);
    private static final StandardOpenOption[] WRITE_OPTIONS = new StandardOpenOption[] {
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
    };

    private final Path saveFilePath;
    private final Path defaultDirectoryPath;
    private Path lastDirectoryPath = null;

    public LastPathStorage(String fileName, String defaultFolder)
    {
        this.saveFilePath = ROOT_PATH.resolve(fileName).toAbsolutePath().normalize();
        this.defaultDirectoryPath = ROOT_PATH.resolve(defaultFolder);

        try
        {
            Files.createDirectories(ROOT_PATH);
            Files.createDirectories(defaultDirectoryPath);
        }
        catch (IOException e)
        {
            RedstoneControllerUnit.LOGGER.error("Encountered an error while creating ");
        }
    }

    public Path get()
    {
        if (lastDirectoryPath == null)
        {
            if (!Files.isRegularFile(saveFilePath))
            {
                return defaultDirectoryPath;
            }

            try
            {
                List<String> data = Files.readAllLines(saveFilePath);
                if (data.size() != 2)
                {
                    RedstoneControllerUnit.LOGGER.warn("Encountered invalid last-used-path info in file '{}', deleting", saveFilePath);
                    Files.delete(saveFilePath);
                    return defaultDirectoryPath;
                }
                if (!data.getFirst().equals(SYSTEM_UUID))
                {
                    RedstoneControllerUnit.LOGGER.error("Encountered last-used-path info from unknown system ID in file '{}', deleting", saveFilePath);
                    Files.delete(saveFilePath);
                    return defaultDirectoryPath;
                }
                lastDirectoryPath = Path.of(data.getLast());
            }
            catch (Throwable t)
            {
                RedstoneControllerUnit.LOGGER.error("Encountered an error while loading last-used-path info from file '{}'", saveFilePath, t);
                return defaultDirectoryPath;
            }
        }
        return lastDirectoryPath;
    }

    public String getAsTinyFDString()
    {
        return get().toString() + File.separator;
    }

    public void update(String fileOrDirPath)
    {
        try
        {
            Path path = Path.of(fileOrDirPath);
            if (Files.isRegularFile(path))
            {
                path = path.subpath(0, path.getNameCount() - 1);
            }
            update(path);
        }
        catch (Throwable t)
        {
            RedstoneControllerUnit.LOGGER.error("Encountered an error while saving last used path information in file '{}'", saveFilePath, t);
        }
    }

    public void update(Path directoryPath)
    {
        if (!directoryPath.equals(lastDirectoryPath))
        {
            try
            {
                String pathString = directoryPath.toAbsolutePath().normalize().toString();
                Files.write(saveFilePath, List.of(SYSTEM_UUID, pathString), WRITE_OPTIONS);
                lastDirectoryPath = directoryPath;
            }
            catch (IOException e)
            {
                RedstoneControllerUnit.LOGGER.error("Encountered an error while saving last used path information in file '{}'", saveFilePath, e);
            }
        }
    }
}
