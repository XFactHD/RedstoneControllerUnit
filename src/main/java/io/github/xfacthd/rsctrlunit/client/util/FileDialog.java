package io.github.xfacthd.rsctrlunit.client.util;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class FileDialog
{
    public static void openFileDialog(
            Screen screen, LastPathStorage lastPathStorage, String title, Filter filter, boolean save, Consumer<Path> action
    )
    {
        String path = lastPathStorage.getAsTinyFDString();
        openFileDialogAsync(path, title, filter, save)
                .thenAcceptAsync(filePath ->
                {
                    // Make sure the screen that requested the file dialog is still the active one
                    if (filePath != null && Minecraft.getInstance().screen == screen)
                    {
                        String lastPath = lastPathStorage.getAsTinyFDString();
                        if (!lastPath.equals(path))
                        {
                            RedstoneControllerUnit.LOGGER.warn("Last path changed unexpectedly during file chooser invocation");
                        }
                        else
                        {
                            lastPathStorage.update(filePath);
                        }
                        action.accept(Path.of(filePath));
                    }
                }, Minecraft.getInstance())
                .exceptionally(ex ->
                {
                    RedstoneControllerUnit.LOGGER.error("Encountered an error while opening file chooser", ex);
                    return null;
                });
    }

    private static CompletableFuture<String> openFileDialogAsync(String path, String title, Filter filter, boolean save)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                PointerBuffer filterBuffer = stack.mallocPointer(filter.filters.length);
                for (String filterEntry : filter.filters)
                {
                    filterBuffer.put(stack.UTF8(filterEntry));
                }
                filterBuffer.flip();

                if (save)
                {
                    return TinyFileDialogs.tinyfd_saveFileDialog(title, path, filterBuffer, filter.filderDesc);
                }
                else
                {
                    return TinyFileDialogs.tinyfd_openFileDialog(title, path, filterBuffer, filter.filderDesc, false);
                }
            }
        }, Util.backgroundExecutor());
    }



    public record Filter(String[] filters, String filderDesc) { }



    private FileDialog() { }
}
