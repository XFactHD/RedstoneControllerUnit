package io.github.xfacthd.rsctrlunit.common.emulator.assembler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface ErrorPrinter
{
    void warning(String msg);

    void warning(String msg, Object... args);

    void error(String msg);

    void error(String msg, Object... args);



    record Collecting(List<Component> lines) implements ErrorPrinter
    {
        @Override
        public void warning(String msg)
        {
            lines.add(Component.literal(msg).withStyle(ChatFormatting.GOLD));
        }

        @Override
        public void warning(String msg, Object... args)
        {
            warning(msg.formatted(args));
        }

        @Override
        public void error(String msg)
        {
            lines.add(Component.literal(msg).withStyle(ChatFormatting.DARK_RED));
        }

        @Override
        public void error(String msg, Object... args)
        {
            error(msg.formatted(args));
        }
    }
}
