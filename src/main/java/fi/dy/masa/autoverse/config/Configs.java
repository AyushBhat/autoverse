package fi.dy.masa.autoverse.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.autoverse.reference.Reference;

public class Configs
{
    public static boolean disableSounds;
    public static boolean fifoBufferUseWrappedInventory;

    public static File configurationFile;
    public static Configuration config;
    
    public static final String CATEGORY_GENERIC = "Generic";

    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event)
    {
        if (Reference.MOD_ID.equals(event.getModID()) == true)
        {
            loadConfigs(config);
        }
    }

    public static void loadConfigsFromFile()
    {
        config = new Configuration(configurationFile, null, true);
        config.load();

        loadConfigs(config);
    }

    public static void loadConfigsFromFile(File configFile)
    {
        configurationFile = configFile;
        loadConfigsFromFile();
    }

    private static void loadConfigs(Configuration conf)
    {
        Property prop;

        prop = conf.get(CATEGORY_GENERIC, "disableSounds", false);
        prop.setComment("Disable all sounds");
        disableSounds = prop.getBoolean();

        prop = conf.get(CATEGORY_GENERIC, "fifoBufferUseWrappedInventory", false);
        prop.setComment("Use a wrapper inventory to offset the slots so that the output is always the first slot");
        fifoBufferUseWrappedInventory = prop.getBoolean();

        if (conf.hasChanged() == true)
        {
            conf.save();
        }
    }
}
