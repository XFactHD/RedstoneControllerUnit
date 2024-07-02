package io.github.xfacthd.rsctrlunit.common.emulator.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.util.RCUCodecs;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.nbt.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

public record Labels(Int2ObjectMap<String> labels, long codeCrc32)
{
    private static final Codec<Labels> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RCUCodecs.int2ObjectMap(Codec.STRING).fieldOf("labels").forGetter(Labels::labels),
            Codec.LONG.fieldOf("code_crc32").forGetter(Labels::codeCrc32)
    ).apply(inst, Labels::new));
    public static final Labels EMPTY = new Labels(Int2ObjectMaps.emptyMap(), 0L);

    public void writeToFile(Path codePath) throws IOException
    {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        if (result.isSuccess() && result.getOrThrow() instanceof CompoundTag tag)
        {
            Path labelPath = getLabelPathFromCodePath(codePath);
            NbtIo.writeCompressed(tag, labelPath);
        }
    }



    public static Labels of(Code code)
    {
        return new Labels(code.labels(), computeCRC32(code.rom()));
    }

    /**
     * @param codePath The path of the associated binary code file
     * @param codeBytes The machine code bytes from the binary code file
     */
    public static Labels readFromFile(Path codePath, byte[] codeBytes) throws IOException
    {
        Path labelPath = getLabelPathFromCodePath(codePath);
        if (!Files.isRegularFile(labelPath))
        {
            return EMPTY;
        }

        CompoundTag tag = NbtIo.readCompressed(labelPath, NbtAccounter.unlimitedHeap());
        DataResult<Pair<Labels, Tag>> result = CODEC.decode(NbtOps.INSTANCE, tag);
        if (result.isSuccess())
        {
            Labels labels = result.getOrThrow().getFirst();
            if (labels.codeCrc32 == computeCRC32(codeBytes))
            {
                return labels;
            }
        }
        return EMPTY;
    }

    private static Path getLabelPathFromCodePath(Path codePath)
    {
        Path parentPath = codePath.getParent();
        String codeFileName = Utils.getFileNameNoExt(codePath);
        return parentPath.resolve(codeFileName + ".lbl");
    }

    private static long computeCRC32(byte[] codeBytes)
    {
        CRC32 crc32 = new CRC32();
        crc32.update(codeBytes, 0, codeBytes.length);
        return crc32.getValue();
    }
}
