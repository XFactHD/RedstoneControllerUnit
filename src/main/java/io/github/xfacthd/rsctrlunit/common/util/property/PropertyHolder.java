package io.github.xfacthd.rsctrlunit.common.util.property;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class PropertyHolder
{
    public static final EnumProperty<RedstoneType> RS_CON_0 = EnumProperty.create("rs_con_0", RedstoneType.class);
    public static final EnumProperty<RedstoneType> RS_CON_1 = EnumProperty.create("rs_con_1", RedstoneType.class);
    public static final EnumProperty<RedstoneType> RS_CON_2 = EnumProperty.create("rs_con_2", RedstoneType.class);
    public static final EnumProperty<RedstoneType> RS_CON_3 = EnumProperty.create("rs_con_3", RedstoneType.class);
    @SuppressWarnings("unchecked")
    public static final EnumProperty<RedstoneType>[] RS_CON_PROPS = new EnumProperty[] {
            RS_CON_0, RS_CON_1, RS_CON_2, RS_CON_3
    };

    public static final BooleanProperty SHOW_PORT_MAPPING = BooleanProperty.create("show_port_mapping");

    public static final EnumProperty<CompoundDirection> FACING_DIR = EnumProperty.create("facing_dir", CompoundDirection.class);

    private PropertyHolder() { }
}
