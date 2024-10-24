package io.github.xfacthd.rsctrlunit.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.*;

public record AreaMaskSource(ResourceLocation src, Optional<ResourceLocation> fallback, ResourceLocation sprite, int x, int y, int w, int h) implements SpriteSource
{
    private static final MapCodec<AreaMaskSource> CODEC = RecordCodecBuilder.<AreaMaskSource>mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("src").forGetter(AreaMaskSource::src),
            ResourceLocation.CODEC.optionalFieldOf("fallback").forGetter(AreaMaskSource::fallback),
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(AreaMaskSource::sprite),
            Codec.intRange(0, 15).fieldOf("x").forGetter(AreaMaskSource::x),
            Codec.intRange(0, 15).fieldOf("y").forGetter(AreaMaskSource::y),
            Codec.intRange(1, 16).fieldOf("width").forGetter(AreaMaskSource::w),
            Codec.intRange(1, 16).fieldOf("height").forGetter(AreaMaskSource::h)
    ).apply(inst, AreaMaskSource::new)).validate(res ->
    {
        if (res.x + res.w > 16) return DataResult.error(() -> "x + width must be <= 16!");
        if (res.y + res.h > 16) return DataResult.error(() -> "y + height must be <= 16!");
        return DataResult.success(res);
    });
    public static final ResourceLocation ID = Utils.rl("mask");
    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);

    @Override
    public void run(ResourceManager manager, Output out)
    {
        ResourceLocation srcPath = TEXTURE_ID_CONVERTER.idToFile(src);
        Optional<Resource> optSource = manager.getResource(srcPath);
        if (optSource.isEmpty() && fallback.isPresent())
        {
            srcPath = TEXTURE_ID_CONVERTER.idToFile(fallback.get());
            optSource = manager.getResource(srcPath);
        }
        if (optSource.isEmpty())
        {
            RedstoneControllerUnit.LOGGER.warn("Missing source texture: {}", srcPath);
            return;
        }

        Resource srcRes = optSource.get();
        Rect2i rect = new Rect2i(x, y, w - 1, h - 1);
        out.add(sprite, new AreaMaskInstance(srcPath, srcRes, new LazyLoadedImage(srcPath, srcRes, 1), rect, sprite));
    }

    @Override
    public SpriteSourceType type()
    {
        return Objects.requireNonNull(TYPE);
    }

    public record AreaMaskInstance(
            ResourceLocation srcPath,
            Resource srcRes,
            LazyLoadedImage srcImg,
            Rect2i rect,
            ResourceLocation sprite
    ) implements SpriteSupplier
    {
        @Override
        public SpriteContents apply(SpriteResourceLoader loader)
        {
            try
            {
                NativeImage source = srcImg.get();

                AnimationMetadataSection sourceAnim = srcRes.metadata()
                        .getSection(AnimationMetadataSection.SERIALIZER)
                        .orElse(AnimationMetadataSection.EMPTY);
                FrameSize frameSize = sourceAnim.calculateFrameSize(source.getWidth(), source.getHeight());
                int factorX = frameSize.width() / 16;
                int factorY = frameSize.height() / 16;
                rect.setPosition(factorX * rect.getX(), factorY * rect.getY());
                rect.setWidth(rect.getWidth() * factorX);
                rect.setHeight(rect.getHeight() * factorY);

                NativeImage imageOut = new NativeImage(NativeImage.Format.RGBA, source.getWidth(), source.getHeight(), false);
                List<FrameInfo> frames = collectFrames(source, frameSize, sourceAnim);
                buildOutputImage(frames, source, rect, imageOut, frameSize);
                return new SpriteContents(sprite, frameSize, imageOut, srcRes.metadata());
            }
            catch (Exception e)
            {
                RedstoneControllerUnit.LOGGER.error("Failed to create masked texture '{}' from source texture'{}'", sprite, srcPath);
            }
            finally
            {
                srcImg.release();
            }
            return null;
        }

        private static List<FrameInfo> collectFrames(NativeImage image, FrameSize size, AnimationMetadataSection animation)
        {
            List<FrameInfo> frames = new ArrayList<>();
            int rowCount = image.getWidth() / size.width();
            // Collect explicitly specified frames
            animation.forEachFrame((idx, time) ->
            {
                int frameX = (idx % rowCount) * size.width();
                int frameY = (idx / rowCount) * size.height();
                frames.add(new FrameInfo(idx, frameX, frameY));
            });
            // Collect implicit frames if no explicit ones are specified in the animation or no animation is present
            if (frames.isEmpty())
            {
                int frameCount = rowCount * (image.getHeight() / size.height());
                for (int idx = 0; idx < frameCount; idx++)
                {
                    int frameX = (idx % rowCount) * size.width();
                    int frameY = (idx / rowCount) * size.height();
                    frames.add(new FrameInfo(idx, frameX, frameY));
                }
            }
            return frames;
        }

        private static void buildOutputImage(List<FrameInfo> frames, NativeImage source, Rect2i rect, NativeImage imageOut, FrameSize frameSize)
        {
            frames.forEach(frame ->
            {
                int fx = frame.x();
                int fy = frame.y();

                for (int y = 0; y < frameSize.height(); y++)
                {
                    for (int x = 0; x < frameSize.width(); x++)
                    {
                        int absX = fx + x;
                        int absY = fy + y;
                        int color = 0;
                        if (rect.contains(x, y))
                        {
                            color = source.getPixel(absX, absY);
                        }
                        imageOut.setPixel(absX, absY, color);
                    }
                }
            });
        }

        @Override
        public void discard()
        {
            srcImg.release();
        }
    }

    private record FrameInfo(int idx, int x, int y) { }
}
