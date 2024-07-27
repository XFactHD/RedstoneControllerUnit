package io.github.xfacthd.rsctrlunit.client.screen.widget;

import io.github.xfacthd.rsctrlunit.client.util.ClientUtils;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.OpcodeHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.IntSupplier;
import java.util.function.ToIntBiFunction;

public sealed class Register
{
    public static final int HEIGHT = 11;

    protected final int x;
    protected final int y;
    protected final String name;
    protected final String valFormat;
    protected final Rect2i tooltipRect;
    protected final ToIntBiFunction<byte[], byte[]> address;
    protected final ToIntBiFunction<byte[], byte[]> reader;

    protected Register(int x, int y, String name, int digits, ToIntBiFunction<byte[], byte[]> address, ToIntBiFunction<byte[], byte[]> reader)
    {
        this.x = x;
        this.y = y;
        this.name = name;
        this.valFormat = "0x%0" + digits + "X";
        this.tooltipRect = new Rect2i(0, y, 0, 0);
        this.address = address;
        this.reader = reader;
    }

    public Register(int x, int y, String name, int digits, ToIntBiFunction<byte[], byte[]> address)
    {
        this(x, y, name, digits, address, (ram, sfr) -> ram[address.applyAsInt(ram, sfr)] & 0xFF);
    }

    public Register(int x, int y, String name, int digits, int address)
    {
        this(x, y, name, digits, (ram, sfr) -> address, (ram, sfr) -> sfr[address - Constants.SFR_START] & 0xFF);
    }

    public Register(int x, int y, int regIdx)
    {
        this(x, y, "R" + regIdx, 2, (ram, sfr) -> OpcodeHelpers.getRegisterAddress(sfr[Constants.ADDRESS_STATUS_WORD - Constants.SFR_START], regIdx));
    }

    public void draw(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr)
    {
        ClientUtils.drawStringInBatch(graphics, font, name, tooltipRect.getX(), y + 2, 0xFF404040);

        int value = reader.applyAsInt(ram, sfr);
        ClientUtils.drawStringInBatch(graphics, font, String.format(Locale.ROOT, valFormat, value), x + 2, y + 2, 0xFF000000);
    }

    public void drawTooltip(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr, int mouseX, int mouseY)
    {
        if (tooltipRect.contains(mouseX, mouseY))
        {
            int addr = address.applyAsInt(ram, sfr);
            Component text = Component.literal(String.format(Locale.ROOT, "%s: 0x%02X", name, addr));
            graphics.renderTooltip(font, text, mouseX, mouseY);
        }
    }

    public void updateTooltipRect(Font font)
    {
        int minX = x - 1 - font.width(name);
        tooltipRect.setX(minX);
        int valWidth = font.width(String.format(Locale.ROOT, valFormat, 0));
        tooltipRect.setWidth(x - minX + 2 + valWidth);
        tooltipRect.setHeight(font.lineHeight + 1);
    }



    public static final class Port extends Register
    {
        private final int port;
        private final byte[] inputs;

        public Port(int x, int y, int port, byte[] outputs, byte[] inputs)
        {
            super(x, y, "P" + port, 2, (ram, sfr) -> Constants.IO_PORTS[port], (ram, sfr) -> outputs[port] & 0xFF);
            this.port = port;
            this.inputs = inputs;
        }

        @Override
        public void draw(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr)
        {
            super.draw(graphics, font, ram, sfr);

            int value = inputs[port] & 0xFF;
            ClientUtils.drawStringInBatch(graphics, font, String.format(Locale.ROOT, valFormat, value), x + 29, y + 2, 0xFF000000);
        }

        @Override
        public void updateTooltipRect(Font font)
        {
            int minX = x - 1 - font.width(name);
            tooltipRect.setX(minX);
            int valWidth = font.width(String.format(Locale.ROOT, valFormat, 0));
            tooltipRect.setWidth(x - minX + 5 + valWidth * 2);
            tooltipRect.setHeight(font.lineHeight + 1);
        }
    }

    public static final class ProgramCounter extends Register
    {
        public ProgramCounter(int x, int y, IntSupplier reader)
        {
            super(x, y, "PC", 4, (ram, sfr) -> -1, (ram, sfr) -> reader.getAsInt());
        }

        @Override // Unaddressable -> no tooltip
        public void drawTooltip(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr, int mouseX, int mouseY) { }
    }

    public static final class StatusWord extends Register
    {
        public StatusWord(int x, int y)
        {
            super(x, y, "PSW", 6, Constants.ADDRESS_STATUS_WORD);
        }

        @Override
        public void draw(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr)
        {
            ClientUtils.drawStringInBatch(graphics, font, name, tooltipRect.getX(), y + 2, 0xFF404040);

            int psw = sfr[Constants.ADDRESS_STATUS_WORD - Constants.SFR_START] & 0xFF;
            for (int i = 0; i < 8; i++)
            {
                String bit = (psw & (1 << (7 - i))) != 0 ? "1" : "0";
                ClientUtils.drawStringInBatch(graphics, font, bit, x + 2 + i * 9, y + 2, 0xFF000000);
            }
        }

        @Override
        public void drawTooltip(GuiGraphics graphics, Font font, byte[] ram, byte[] sfr, int mouseX, int mouseY)
        {
            if (tooltipRect.contains(mouseX, mouseY))
            {
                int relX = mouseX - x;
                int digit = relX / 9;
                if (relX >= 0 && digit >= 0)
                {
                    String label = switch (digit)
                    {
                        case 0 -> "C";
                        case 1 -> "AC";
                        case 2 -> "F0";
                        case 3 -> "RS1";
                        case 4 -> "RS0";
                        case 5 -> "OV";
                        case 6 -> "UD";
                        case 7 -> "P";
                        default -> throw new IllegalStateException();
                    };
                    graphics.renderTooltip(font, Component.literal(label), mouseX, mouseY);
                    return;
                }
                super.drawTooltip(graphics, font, ram, sfr, mouseX, mouseY);
            }
        }

        @Override
        public void updateTooltipRect(Font font)
        {
            super.updateTooltipRect(font);
            int minX = x - 1 - font.width(name);
            tooltipRect.setX(minX);
            tooltipRect.setWidth(x - minX + 71);
            tooltipRect.setHeight(font.lineHeight + 1);
        }
    }
}
